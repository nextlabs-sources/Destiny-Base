/*
 * Created on Jul 09, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.evaluationconnector.listeners;

import static com.nextlabs.evaluationconnector.utils.Constants.CONTENT_TYPE_JSON;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

import com.nextlabs.evaluationconnector.exceptions.EvaluationConnectorException;
import com.nextlabs.evaluationconnector.handlers.OAuthRequestHandler;
import com.nextlabs.evaluationconnector.beans.json.OAuth2Token;

public class OAuthRequestListener {
	private static final Log log = LogFactory.getLog(OAuthRequestListener.class);
    private Gson jsonSerializer = new Gson();

    public void onReceived(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                           OAuthRequestDO oauthRequest)
        throws EvaluationConnectorException {
        long startCounter = System.nanoTime();
        
        OAuthRequestHandler oAuthRequestHandler = new OAuthRequestHandler();
        
        OAuth2Token token = oAuthRequestHandler.getOAuth2Token(oauthRequest);

        httpResponse.setContentType(CONTENT_TYPE_JSON);

        String tokenResponseString = jsonSerializer.toJson(token);
        
        httpResponse.setContentLength(tokenResponseString.length());

        try {
            PrintWriter pWriter = httpResponse.getWriter();

            pWriter.write(tokenResponseString);
        } catch (IOException e) {
            // Send dummy response
        }
    }
}
