/*
 * Created on Jan 19, 2015
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.evaluationconnector.listeners;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.evaluationconnector.beans.PermissionsResponse;
import com.nextlabs.evaluationconnector.beans.XACMLResponse;
import com.nextlabs.evaluationconnector.exceptions.EvaluationConnectorException;
import com.nextlabs.evaluationconnector.handlers.EvalRequestHandler;
import com.nextlabs.evaluationconnector.handlers.EvalRequestHandlerFactory;
import com.nextlabs.evaluationconnector.parsers.EvalResponse;
import com.nextlabs.evaluationconnector.parsers.EvalResponseFactory;
import com.nextlabs.evaluationconnector.utils.Constants;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;

/**
 * <p>
 * EvaluationRequestListener <br/>
 * 
 * All the valid evaluation requests are processed by this listener.
 * 
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class EvaluationRequestListener {

	private static final Log log = LogFactory.getLog(EvaluationRequestListener.class);

	/**
	 * <p>
	 * Handle the evaluation request on received.
	 * </p>
	 *
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @throws EvaluationConnectorException
	 * @throws IOException
	 */
	public void onReceived(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String dataType, String data)
			throws EvaluationConnectorException, IOException {

		try {
			long startCounter = System.nanoTime();
			EvalRequestHandler evalRequestHandler = EvalRequestHandlerFactory.createHandler(dataType);

            long policyEvalTime = 0;
            
            if (isXACMLPDPRequest(httpRequest)) {
                IWrappedXACMLResponse enforcementResult = evalRequestHandler.handleXACML(data, dataType);

                EvalResponse evalResponse = EvalResponseFactory.getInstance(dataType);
                evalResponse.handleResponse(httpResponse, enforcementResult);
            } else if (isNextLabsPDPRequest(httpRequest)) {
                XACMLResponse xacmlResponse = evalRequestHandler.handle(data);
            
                EvalResponse evalResponse = EvalResponseFactory.getInstance(dataType);

                xacmlResponse.setRequestStartTime(startCounter);
                evalResponse.handleResponse(httpResponse, xacmlResponse);
                
                policyEvalTime = TimeUnit.NANOSECONDS.toMillis(xacmlResponse.getPdpEvalTime());
            } else if (isPermissionsRequest(httpRequest)) {
                PermissionsResponse permissionsResponse = evalRequestHandler.handlePermissions(data);

                EvalResponse evalResponse = EvalResponseFactory.getInstance(dataType);
                evalResponse.handleResponse(httpResponse, permissionsResponse);
                
                policyEvalTime = TimeUnit.NANOSECONDS.toMillis(permissionsResponse.getPermissionsEvalTime());
            }
            
			long restApiResponseTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startCounter);

			if (log.isDebugEnabled()) {
				String msg = String.format(
						"%s, Thread Id:[%s], EvaluationRequestListener -> Time taken to process the request: %d ms",
						Constants.PERF_LOG_PREFIX, "" + Thread.currentThread().getId(), restApiResponseTime);
				log.debug(msg);
			}

			if (log.isInfoEnabled())
				log.info("Evaluation Request handled successfully. [ REST-API Response Time: " + restApiResponseTime + " ms, Policy eval time: " + policyEvalTime + " ms ]");

		} catch (Exception e) {
			log.error("Error occurred while processing the request, [ Server Error response send back to client. ] ",
					e);
			httpResponse.sendError(SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

    private boolean isXACMLPDPRequest (HttpServletRequest httpRequest) {
        return httpRequest.getRequestURI().endsWith("/pdpxacml");
    }
    
    private boolean isNextLabsPDPRequest (HttpServletRequest httpRequest) {
        return httpRequest.getRequestURI().endsWith("/pdp");
    }
    
    private boolean isPermissionsRequest(HttpServletRequest httpRequest) {
        return httpRequest.getRequestURI().endsWith("/pdppermissions");
    }
}
