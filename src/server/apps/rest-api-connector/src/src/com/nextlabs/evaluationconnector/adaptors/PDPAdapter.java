/*
 * Created on Jan 19, 2015
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.evaluationconnector.adaptors;

import java.util.List;

import com.nextlabs.evaluationconnector.beans.PDPRequest;
import com.nextlabs.evaluationconnector.beans.PermissionsResponse;
import com.nextlabs.evaluationconnector.beans.XACMLResponse;
import com.nextlabs.evaluationconnector.exceptions.EvaluationConnectorException;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;

/**
 * <p>
 * This interface will creates the bridge between PDP and this Connector.
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public interface PDPAdapter {

	String RMI_MODE = "RMI";
	String CAPI_MODE = "CAPI";
	String DIRECT_MODE = "DIRECT";
	

	/**
	 * <p>
	 * Respective API initialization code here
	 * </p>
	 *
	 */
	void init() throws EvaluationConnectorException;

	/**
	 * <p>
	 * Evaluate the request by passing to respective underlying API.
	 * </p>
	 *
	 * @param pdpRequest
	 *            {@link PDPRequest}
	 * @return {@link XACMLResponse}
	 * @throws EvaluationConnectorException
	 *             throws in any error
	 */
	XACMLResponse evaluate(List<PDPRequest> pdpRequests)
			throws EvaluationConnectorException;

    /**
     * <p>
     * Evaluate the XACML request by passing to a XACML PDP
     *
     * @param data
     * @param dataType
     * @return response IWrappedXACMLResponse
     * @throws EvaluationConnectorException for any error
     */
    IWrappedXACMLResponse evaluate(String data, String dataType) throws EvaluationConnectorException;

    /**
     * <p>
     * Evaluate the permissions request by passing to respective underlying API
     *
     * @param pdpRequest {@link PDPRequest}
     * @return {@link PermissionsResponse}
     * @throws EvaluationConnectorException for any error
     */
    PermissionsResponse evaluatePermissions(List<PDPRequest> pdpPermissionsRequests) throws EvaluationConnectorException;
    
	/**
	 * <p>
	 * Close the adaptor connection
	 * </p>
	 *
	 */
	void closeConnection();
}
