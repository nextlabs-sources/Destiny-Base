/*
 * Created on Jan 20, 2015
 * 
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 *
 */
package com.nextlabs.evaluationconnector.parsers;

import static com.nextlabs.evaluationconnector.utils.Constants.CONTENT_TYPE_JSON;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

import com.nextlabs.evaluationconnector.beans.PermissionsResponse;
import com.nextlabs.evaluationconnector.beans.PermissionsResponsesType;
import com.nextlabs.evaluationconnector.beans.PermissionsResponseType;
import com.nextlabs.evaluationconnector.beans.XACMLResponse;
import com.nextlabs.evaluationconnector.beans.ResponseStatusCode;
import com.nextlabs.evaluationconnector.beans.json.AttributeAssignment;
import com.nextlabs.evaluationconnector.beans.json.ObligationOrAdvice;
import com.nextlabs.evaluationconnector.beans.json.PermissionsActionObligations;
import com.nextlabs.evaluationconnector.beans.json.PermissionsResponseWrapper;
import com.nextlabs.evaluationconnector.beans.json.PermissionsResult;
import com.nextlabs.evaluationconnector.beans.json.ResponseWrapper;
import com.nextlabs.evaluationconnector.beans.json.Result;
import com.nextlabs.evaluationconnector.beans.json.Status;
import com.nextlabs.evaluationconnector.beans.json.StatusCode;
import com.nextlabs.evaluationconnector.exceptions.EvaluationConnectorException;
import com.nextlabs.evaluationconnector.utils.Constants;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResultType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.StatusType;

/**
 * <p>
 * JSONEvalResponse
 * </p>
 *
 *
 * @author Amila Silva
 *
 */
public class JSONEvalResponse implements EvalResponse {

    private static final Log log = LogFactory.getLog(JSONEvalResponse.class);

    private Gson jsonSerializer = null;
    
    private static final double MILISECONDS = 1000000.00;

    @Override
    public void init() throws EvaluationConnectorException {
        jsonSerializer = new Gson();
    }

    @Override
    public void handleResponse(HttpServletResponse httpResponse, XACMLResponse response)
        throws EvaluationConnectorException {

        if (jsonSerializer == null) {
            init();
        }

        try {
            long startCounter = System.nanoTime();
            ResponseWrapper responseWrapper = populateJsonResponse(response);
            String responseString = jsonSerializer.toJson(responseWrapper);

            long policyEvalTime = response.getPdpEvalTime();
            long restApiResponseTime = (System.nanoTime() - response.getRequestStartTime());

            httpResponse.setContentType(CONTENT_TYPE_JSON);
            httpResponse.setContentLength(responseString.length());
            if (log.isDebugEnabled()) {
                httpResponse.setHeader(Constants.POLICY_EVAL_TIME, String.valueOf(policyEvalTime));
                httpResponse.setHeader(Constants.REST_API_RESPONSE_TIME, String.valueOf(restApiResponseTime));
            }

            PrintWriter pWriter = httpResponse.getWriter();
            pWriter.write(responseString);

            if (log.isDebugEnabled()) {
                log.debug(
                    String.format("%s, Thread Id:[%s],  JSONEvalResponse -> Total Response creation Time : %.4f ms",
                                  Constants.PERF_LOG_PREFIX, "" + Thread.currentThread().getId(),
                                  (System.nanoTime() - startCounter)/MILISECONDS));

                log.debug("JSONEvalResponse is written to response successfully, [Response : " + responseString + "]");
            }
            
        } catch (Exception e) {
            throw new EvaluationConnectorException("Unknown exception", e);
        }
    }
    
    @Override
    public void handleResponse(HttpServletResponse httpResponse, IWrappedXACMLResponse response) {
        throw new UnsupportedOperationException("handleResponse(HttpServletResponse, IWrappedXACMLResponse");
    }

    @Override
    public void handleResponse(HttpServletResponse httpResponse, PermissionsResponse response) throws EvaluationConnectorException {
        if (jsonSerializer == null) {
            init();
        }

        try {
            long startCounter = System.nanoTime();
            PermissionsResponseWrapper responseWrapper = populateJsonResponse(response);
            String responseString = jsonSerializer.toJson(responseWrapper);

            long permissionsEvalTime = response.getPermissionsEvalTime();
            long restApiResponseTime = (System.nanoTime() - response.getRequestStartTime());

            httpResponse.setContentType(CONTENT_TYPE_JSON);
            httpResponse.setContentLength(responseString.length());
            if (log.isDebugEnabled()) {
                httpResponse.setHeader(Constants.PERMISSIONS_EVAL_TIME, String.valueOf(permissionsEvalTime));
                httpResponse.setHeader(Constants.REST_API_RESPONSE_TIME, String.valueOf(restApiResponseTime));
            }
            
            PrintWriter pWriter = httpResponse.getWriter();
            pWriter.write(responseString);

            if (log.isDebugEnabled()) {
                log.debug(
                    String.format("%s, Thread Id:[%s],  JSONEvalResponse -> Total Response creation Time : %.4f ms",
                                  Constants.PERF_LOG_PREFIX, "" + Thread.currentThread().getId(),
                                  (System.nanoTime() - startCounter)/MILISECONDS));

                log.debug("JSONEvalResponse is written to response successfully, [Response : " + responseString + "]");
            }
        } catch (Exception e) {
            throw new EvaluationConnectorException("Unknown exception", e);
        }
    }

    /**
     * <p>
     * Populate the ResponseWrapper from the {@link ResponseType}
     * </p>
     *
     * @return {@link ResponseWrapper}
     */
    public ResponseWrapper populateJsonResponse(XACMLResponse response) {
        ResponseWrapper responseWrapper = new ResponseWrapper();

        for (ResultType resultType : response.getResponseType().getResult()) {
            Result result = new Result();
            result.setDecision(resultType.getDecision().value());

            StatusType statusType = resultType.getStatus();

            Status status = new Status();
            StatusCode statusCode = new StatusCode();
            if (ResponseStatusCode.OK.getValue().equals(statusType.getStatusCode().getValue())) {
                statusCode.setValue(ResponseStatusCode.OK.getValue());
            } else if (ResponseStatusCode.MISSING_ATTRIB.getValue().equals(statusType.getStatusCode().getValue())) {
                statusCode.setValue(ResponseStatusCode.MISSING_ATTRIB.getValue());
            } else if (ResponseStatusCode.PROCESSING_ERROR.getValue().equals(statusType.getStatusCode().getValue())) {
                statusCode.setValue(ResponseStatusCode.PROCESSING_ERROR.getValue());
            }

            response.setStatus(statusCode.getValue());
            
            status.setStatusCode(statusCode);
            status.setStatusMessage(statusType.getStatusMessage());
            result.setStatus(status);

            ArrayList<ObligationOrAdvice> obligations = new ArrayList<ObligationOrAdvice>();
            if (resultType.getObligations() != null) {
                for (ObligationType obligationType : resultType.getObligations().getObligation()) {
                    ObligationOrAdvice obligation = new ObligationOrAdvice();
                    obligation.setId(obligationType.getObligationId());

                    for (AttributeAssignmentType attributeType : obligationType.getAttributeAssignment()) {
                        AttributeAssignment attribute = new AttributeAssignment();
                        attribute.setAttributeId(attributeType.getAttributeId());
                        attribute.setValue(attributeType.getContent());
                        attribute.setCategory(attributeType.getCategory());
                        attribute.setDataType(attributeType.getDataType());
                        attribute.setIssuer(attributeType.getIssuer());
                        obligation.getAttributeAssignment().add(attribute);
                    }
                    obligations.add(obligation);
                }
                result.setObligations(obligations);
            }
	
            if (resultType.getAssociatedAdvice() != null) {
                for (AdviceType adviceType : resultType.getAssociatedAdvice().getAdvice()) {
                    ObligationOrAdvice obligation = new ObligationOrAdvice();
                    obligation.setId(adviceType.getAdviceId());

                    for (AttributeAssignmentType attributeType : adviceType.getAttributeAssignment()) {
                        AttributeAssignment attribute = new AttributeAssignment();
                        attribute.setAttributeId(attributeType.getAttributeId());
                        attribute.setValue(attributeType.getContent());
                        attribute.setCategory(attributeType.getCategory());
                        attribute.setDataType(attributeType.getDataType());
                        attribute.setIssuer(attributeType.getIssuer());
                        obligation.getAttributeAssignment().add(attribute);
                    }
                    obligations.add(obligation);
                }
                result.setObligations(obligations);
            }
			
            responseWrapper.getResponse().add(result);
        }
        return responseWrapper;
    }

    public PermissionsResponseWrapper populateJsonResponse(PermissionsResponse response) {
        PermissionsResponseWrapper responseWrapper = new PermissionsResponseWrapper();

        // We are always setting the status to OK
        Status status = new Status();
        StatusCode statusCode = new StatusCode();
        statusCode.setValue(ResponseStatusCode.OK.getValue());

        status.setStatusCode(statusCode);
        responseWrapper.setStatus(status);

        if (response.getResponsesType() != null) {
            for (PermissionsResponseType permissionsResponse : response.getResponsesType().getPermissionsResponses()) {
                PermissionsResult result = new PermissionsResult();

                for (String effect : PermissionsResponseType.EFFECTS_TO_REPORT) {
                    List<PermissionsActionObligations> allActionObligations = new ArrayList<>();

                    for (com.nextlabs.evaluationconnector.beans.PermissionsActionObligations actionAndObligations : permissionsResponse.getActionObligationsForEffect(effect).getActionObligations()) {
                        PermissionsActionObligations singleActionObligations = new PermissionsActionObligations();
                        List<ObligationOrAdvice> obligations = new ArrayList<>();

                        singleActionObligations.setAction(actionAndObligations.getAction());

                    ObligationsType obligationsType = actionAndObligations.getObligations();
                    if (obligationsType != null) {
                        for (ObligationType obligationType : obligationsType.getObligation()) {
                            ObligationOrAdvice obligation = new ObligationOrAdvice();
                            obligation.setId(obligationType.getObligationId());
                        
                            for (AttributeAssignmentType attributeType : obligationType.getAttributeAssignment()) {
                                AttributeAssignment attribute = new AttributeAssignment();
                                attribute.setAttributeId(attributeType.getAttributeId());
                                attribute.setValue(attributeType.getContent());
                                attribute.setCategory(attributeType.getCategory());
                                attribute.setDataType(attributeType.getDataType());
                                attribute.setIssuer(attributeType.getIssuer());
                                obligation.getAttributeAssignment().add(attribute);
                            }
                            obligations.add(obligation);
                        }
                    }
                
                    singleActionObligations.setObligations(obligations);

                    singleActionObligations.setMatchingPolicies(actionAndObligations.getMatchingPolicies());
                    allActionObligations.add(singleActionObligations);
                }
                result.setActionsAndObligationsForEffect(effect, allActionObligations);
            }
            
            
            responseWrapper.getResponse().add(result);
        }
        }

        return responseWrapper;
    }
}
