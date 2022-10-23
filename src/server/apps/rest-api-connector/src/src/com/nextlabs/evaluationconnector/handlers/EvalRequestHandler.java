/*
 * Created on Jan 19, 2015
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.evaluationconnector.handlers;

import com.nextlabs.evaluationconnector.beans.PermissionsResponse;
import com.nextlabs.evaluationconnector.beans.XACMLResponse;
import com.nextlabs.evaluationconnector.exceptions.EvaluationConnectorException;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;

/**
 * <p>
 * EvalRequestHandler
 * 
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public interface EvalRequestHandler {

	/**
	 * <p>
	 * Handle the evaluation request.
	 * </p>
	 *
	 * @param data
	 *            xacml 3.0 compliance data
	 * @return {@link XACMLResponse}
	 * @throws EvaluationConnectorException
	 */
	public XACMLResponse handle(String data) throws EvaluationConnectorException;

    /**
     * <p>
     * Handle a request for the XACML pdp
     * </p>
     *
     * @param data xacml 1.0, 2.0, 3.0 compliance data
     * @param dataType XML/JSON
     * @return response object which can be converted to XML (JSON tbd)
     * @throws EvaluationConnectorException
     */
	public IWrappedXACMLResponse handleXACML(String data, String dataType) throws EvaluationConnectorException;

    /**
     * Handle a request for permissions
     *
     * @param data
     * @throws EvaluationConnectorException
     */
    public PermissionsResponse handlePermissions(String data) throws EvaluationConnectorException;
}
