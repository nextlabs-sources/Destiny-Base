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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.evaluationconnector.beans.PDPRequest;
import com.nextlabs.evaluationconnector.beans.PermissionsResponse;
import com.nextlabs.evaluationconnector.beans.XACMLResponse;
import com.nextlabs.evaluationconnector.exceptions.EvaluationConnectorException;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;

/**
 * <p>
 * C API Adaptor implementation for PDP
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class PDPAPIAdaptor implements PDPAdapter {

	private static final Log log = LogFactory.getLog(PDPAPIAdaptor.class);

	@Override
	public void init() throws EvaluationConnectorException {
        log.warn("init() not implemented for C-API");
        throw new EvaluationConnectorException("init() not implemented for C-API");
	}

	@Override
	public XACMLResponse evaluate(List<PDPRequest> pdpRequests)
			throws EvaluationConnectorException {
        log.warn("evaluate(List<PDPRequests>) not implemented for C-API");
        throw new EvaluationConnectorException("evaluate(String, String) not implemented for C-API");
	}

    @Override
    public IWrappedXACMLResponse evaluate(String data, String dataType) throws EvaluationConnectorException {
        log.warn("evaluate(String, String) not implemented for C-API");
        throw new EvaluationConnectorException("evaluate(String, String) not implemented for C-API");
    }

    @Override
    public PermissionsResponse evaluatePermissions(List<PDPRequest> pdpPermissionsRequests) throws EvaluationConnectorException {
        log.warn("evaluationPermissions() not implemented for C-API");
        throw new EvaluationConnectorException("evaluatePermissions(List<PDPRequest>) not implemented for C-API");
    }
    
	@Override
	public void closeConnection() {
        log.warn("closeConnection() not implemented for C-API");
	}

}
