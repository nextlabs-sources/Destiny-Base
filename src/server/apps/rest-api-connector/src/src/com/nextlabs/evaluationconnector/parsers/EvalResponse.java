/*
 * Created on Jan 20, 2015
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.evaluationconnector.parsers;

import javax.servlet.http.HttpServletResponse;

import com.nextlabs.evaluationconnector.beans.PermissionsResponse;
import com.nextlabs.evaluationconnector.beans.XACMLResponse;
import com.nextlabs.evaluationconnector.exceptions.EvaluationConnectorException;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;

/**
 * <p>
 * EvalResponse
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public interface EvalResponse {

	/**
	 * <p>
	 * This method will initialize content
	 * </p>
	 *
	 * @throws EvaluationConnectorException
	 */
	void init() throws EvaluationConnectorException;

	/**
	 * <p>
	 * Transform the XACML Response to respective response data type;
	 * </p>
	 *
	 * @throws EvaluationConnectorException
	 */
	void handleResponse(HttpServletResponse httpResponse, XACMLResponse response)
			throws EvaluationConnectorException;
    
	/**
	 * <p>
	 * Handler for XACML response as a IWrappedXACMLResponse
	 * </p>
	 *
	 * @throws EvaluationConnectorException
	 */
	void handleResponse(HttpServletResponse httpResponse, IWrappedXACMLResponse response)
			throws EvaluationConnectorException;

    /**
     * Handler for permissions query
     */
    void handleResponse(HttpServletResponse httpResponse, PermissionsResponse response)
            throws EvaluationConnectorException;
}
