/*
 * Created on Jul 07, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.evaluationconnector.listeners;

import static com.nextlabs.evaluationconnector.utils.Constants.CONTENT_TYPE_XML;
import static com.nextlabs.evaluationconnector.utils.Constants.OAUTH_TOKEN_CLIENT_ID;
import static com.nextlabs.evaluationconnector.utils.Constants.OAUTH_TOKEN_CLIENT_SECRET;
import static com.nextlabs.evaluationconnector.utils.Constants.OAUTH_TOKEN_EXPIRES_IN;
import static com.nextlabs.evaluationconnector.utils.Constants.OAUTH_TOKEN_GRANT_TYPE;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.evaluationconnector.exceptions.EvaluationConnectorException;
import com.nextlabs.evaluationconnector.listeners.OAuthRequestDO;

public class OAuthRequestServlet extends HttpServlet {
	private static final Log log = LogFactory.getLog(OAuthRequestServlet.class);
    
    @Override
    public void init() throws ServletException {
        log.info("OAuthRequestServlet - Initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        if (req.getRequestURI().contains("/oauth")) {
			RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/help.html");
			dispatcher.forward(req, resp);
        } else {
            processEntryPointRequest(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        if (req.getRequestURI().contains("/oauth")) {
            doProcess(req, resp);
        } else {
            processEntryPointRequest(req, resp);
        }
    }

    /**
     * Processes GET and POST requests to this servlet
     */
    protected void doProcess(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        log.info("Start processing OAuth request");

        try {
            request.setCharacterEncoding("UTF-8");

            handleParamBasedPost(request, response);
        } catch (EvaluationConnectorException e) {
			log.error("Error occurred in Evaluation Connector Request processing, ", e);
            response.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            log.error("Error evaluating OAuth request");
            response.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void handleParamBasedPost(HttpServletRequest request, HttpServletResponse response)
        throws EvaluationConnectorException {
        String grantType = request.getParameter(OAUTH_TOKEN_GRANT_TYPE);
        String clientId = request.getParameter(OAUTH_TOKEN_CLIENT_ID);
        String clientSecret = request.getParameter(OAUTH_TOKEN_CLIENT_SECRET);
        String expiresIn = request.getParameter(OAUTH_TOKEN_EXPIRES_IN);

        if (grantType == null || grantType.equals("") ||
            clientId == null || clientId.equals("") ||
            clientSecret == null || clientSecret.equals("")) {
            handleInvalidRequest(request, response);
        } else {
            OAuthRequestDO oAuthRequest = new OAuthRequestDO(grantType, clientId, clientSecret, expiresIn);
            getOAuthRequestListener().onReceived(request, response, oAuthRequest);
        }
    }
    
	/**
	 * <p>
	 * Processes GET request, server responds by sending a standard XACML
	 * response (in XACML format) specifying the end points
	 * </p>
	 * 
	 * @param request
	 * 			  {@link HttpServletRequest}
	 * @param response
	 * 			 {@link HttpServletResponse}
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processEntryPointRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter pWriter = response.getWriter();

        // FIXME. Different string
		// String responseStr = PropertiesUtil.getString("pdp.connector.get.request.std.response");
        String responseStr = "derp";
		response.setContentType(CONTENT_TYPE_XML);

		pWriter.println(responseStr);
		pWriter.flush();
		pWriter.close();
	}

    /**
     * Create a new OAuthRequestListener for each request
     */
    private OAuthRequestListener getOAuthRequestListener() {
        return new OAuthRequestListener();
    }

    private void handleInvalidRequest(HttpServletRequest request, HttpServletResponse response) {
        handleInvalidRequest(request, response, null);
    }
    
    private void handleInvalidRequest(HttpServletRequest request, HttpServletResponse response, String errorMessage) {
        // TODO
    }
}
