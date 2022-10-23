/**
 * 
 */
package com.nextlabs.evaluationconnector.adaptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.agent.pdpapi.IPDPEnforcement;
import com.bluejungle.destiny.agent.pdpapi.IPDPHost;
import com.bluejungle.destiny.agent.pdpapi.IPDPNamedAttributes;
import com.bluejungle.destiny.agent.pdpapi.IPDPSDKCallback;
import com.bluejungle.destiny.agent.pdpapi.PDPException;
import com.bluejungle.destiny.agent.pdpapi.PDPHost;
import com.bluejungle.destiny.agent.pdpapi.PDPNamedAttributes;
import com.bluejungle.destiny.agent.pdpapi.PDPSDK;
import com.bluejungle.destiny.agent.pdpapi.PDPTimeout;
import com.bluejungle.pf.engine.destiny.IAgentPolicyContainer;
import com.nextlabs.destiny.agent.controlmanager.IObligationResultData;
import com.nextlabs.destiny.agent.pdpapi.IPDPPermissionsCallback;
import com.nextlabs.destiny.agent.pdpapi.IPDPPermissionsResponse;
import com.nextlabs.destiny.sdk.CEAttributes;
import com.nextlabs.destiny.sdk.CEAttributes.CEAttribute;
import com.nextlabs.destiny.sdk.CEEnforcement;
import com.nextlabs.destiny.sdk.CEEnforcement.CEResponse;
import com.nextlabs.destiny.sdk.CESdkException;
import com.nextlabs.destiny.sdk.CESdkTimeoutException;
import com.nextlabs.evaluationconnector.beans.PDPRequest;
import com.nextlabs.evaluationconnector.beans.PermissionsActionObligations;
import com.nextlabs.evaluationconnector.beans.PermissionsActionObligationsList;
import com.nextlabs.evaluationconnector.beans.PermissionsResponse;
import com.nextlabs.evaluationconnector.beans.PermissionsResponseType;
import com.nextlabs.evaluationconnector.beans.PermissionsResponsesType;
import com.nextlabs.evaluationconnector.beans.ResponseStatusCode;
import com.nextlabs.evaluationconnector.beans.XACMLResponse;
import com.nextlabs.evaluationconnector.exceptions.EvaluationConnectorException;
import com.nextlabs.evaluationconnector.parsers.XACMLParser;
import com.nextlabs.evaluationconnector.utils.Constants;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;
import com.nextlabs.pf.engine.destiny.WrappedXACMLResponse;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResultType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.StatusCodeType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.StatusType;

import org.wso2.balana.ctx.Status;
import org.wso2.balana.ctx.xacml3.Result;

/**
 * <p>
 * PDP Direct Adapter which connects directly with PDP, Only for local seem less
 * integration
 * </p>
 * 
 * @author Amila Silva
 *
 */
public class PDPDirectAdaptor implements PDPAdapter {
	private static final Log log = LogFactory.getLog(PDPDirectAdaptor.class);

	private static final String KEY_RES_ID = "ce::id";
	private static final String KEY_RES_TYPE = "ce::destinytype";
	private static final String KEY_USER_ID = "id";
	private static final String KEY_USER_NAME = "name";
	private static final String KEY_APP_NAME = "name";
	private static final String KEY_APP_PID = "pid";
	private static final String KEY_HOST_INET = "inet_address";

	private static final String ATTR_OBLIGATION_COUNT = "CE_ATTR_OBLIGATION_COUNT";
	private static final String ATTR_OBLIGATION_NAME = "CE_ATTR_OBLIGATION_NAME";
	private static final String ATTR_OBLIGATION_NUMVALUES = "CE_ATTR_OBLIGATION_NUMVALUES";
	private static final String ATTR_OBLIGATION_VALUE = "CE_ATTR_OBLIGATION_VALUE";
	private static final double MILLISECONDS = 1000000.00;

	private static final IPDPHost LOCALHOST = new PDPHost(0x7F000001); // 127.0.0.1

    // The caller can request "wait forever", but that ties up resources on our sides. Five
    // minutes should be more than long enough for any rational query
    private static final int MAX_TIMEOUT_IN_MS = 5 * 60 * 1000;
    
	@Override
	public void init() throws EvaluationConnectorException {
		log.info("Nothing to initialize");
	}

    @Override
    public IWrappedXACMLResponse evaluate(String data, String dataType) throws EvaluationConnectorException {
        try {
            return PDPSDK.PDPQueryXACMLDecisionEngine(data, dataType);
        } catch (Exception e) {
            return WrappedXACMLResponse.buildIndeterminate(e.getMessage());
        }
    }
    
	@Override
	public XACMLResponse evaluate(List<PDPRequest> pdpRequests) throws EvaluationConnectorException {
		XACMLResponse response = new XACMLResponse();
		ResponseType responseType = null;
		try {
			long startCounter = System.nanoTime();
			List<CEEnforcement> enforcementResults = callSDK(pdpRequests, response);

			if (log.isDebugEnabled())
				log.debug(String.format(
						"%s, Thread Id:[%s],  PDPDirectAdaptor -> Total time taken by Direct API Evaluation : %.4f ms",
						Constants.PERF_LOG_PREFIX, "" + Thread.currentThread().getId(),
						(System.nanoTime() - startCounter)/MILLISECONDS));

			long responseStartCounter = System.nanoTime();
			responseType = getResponse(enforcementResults);
			response.setStatus(ResponseStatusCode.OK.getValue());
			response.setResponseType(responseType);

			if (log.isDebugEnabled())
				log.debug(String.format(
						"%s, Thread Id:[%s],  PDPDirectAdaptor -> Total time for by Direct API response creation : %.4f ms",
						Constants.PERF_LOG_PREFIX, "" + Thread.currentThread().getId(),
						(System.nanoTime() - responseStartCounter)/MILLISECONDS));

		} catch (Exception e) {
			log.error("Error occurred while evaluating the request, ", e);
			responseType = getErrorResponse(e.getLocalizedMessage(), ResponseStatusCode.PROCESSING_ERROR);
			response.setStatus(ResponseStatusCode.PROCESSING_ERROR.getValue());
			response.setResponseType(responseType);
		}

		if (log.isDebugEnabled())
			log.debug("XACML Response Generated after request evaluation, [ XACML Response Status : "
					+ response.getStatus() + "] ");
		return response;
	}

	private List<CEEnforcement> callSDK(List<PDPRequest> pdpRequests, XACMLResponse response)
			throws EvaluationConnectorException {
		try {
			long startTime = System.nanoTime();
            
            List<CEEnforcement> enforcementResults = checkResources(pdpRequests);

			response.setPdpEvalTime(System.nanoTime() - startTime);

			if (log.isDebugEnabled())
				log.debug("PDP Enforcement Results size:" + enforcementResults.size());

			return enforcementResults;
		} catch (Exception e) {
			throw new EvaluationConnectorException("Error occurred while invokeSDK, ", e);
		}
	}
	

	private List<CEEnforcement> checkResources(List<PDPRequest> pdpRequests) throws CESdkTimeoutException, CESdkException {
		LinkedList<CheckableCallback> callbacks = new LinkedList<CheckableCallback>();

        int longestTimeoutOfAllRequests = 0;
        
		for (PDPRequest pdpRequest : pdpRequests) {	
            CheckableCallback cb = new CheckableCallback();
            callbacks.add(cb);

            // A couple of minor tweaks to get everything in the right format
            if (pdpRequest.getHost() == null || pdpRequest.getHost().getValue(KEY_HOST_INET) == null) {
                pdpRequest.setHost(LOCALHOST);
            }

            if (pdpRequest.getUser() != null && pdpRequest.getUser().getValue(KEY_USER_NAME) == null) {
                pdpRequest.getUser().setAttribute(KEY_USER_NAME, pdpRequest.getUser().getValue(KEY_USER_ID));
            }

            int timeout = pdpRequest.getTimeoutInMs();

            if (timeout == -1 || timeout > MAX_TIMEOUT_IN_MS) {
                timeout = MAX_TIMEOUT_IN_MS;
            }

            longestTimeoutOfAllRequests = Math.max(longestTimeoutOfAllRequests, timeout);
            
            try {
                IPDPEnforcement ret = PDPSDK.PDPQueryDecisionEngine(pdpRequest.getAction(),
                                                                    pdpRequest.getResourceArr(),
                                                                    pdpRequest.getUser(),
                                                                    pdpRequest.getApplication(),
                                                                    pdpRequest.getHost(),
                                                                    true, // perform obligations
                                                                    pdpRequest.getAdditionalData(),
                                                                    pdpRequest.getNoiseLevel(),
                                                                    timeout,
                                                                    cb);

                // We specified a callback, so the enforcement result should always be null
                assert ret == null;
            } catch (PDPTimeout e) {
                throw new CESdkTimeoutException("PDP query timed out", e);
            } catch (PDPException e) {
                throw new CESdkException("PDP query exception", e);
            }
		}

		/*
		 * There's no need to collect the results in the order they finish. We
		 * need all of them before the timeout happens, so why not just start at
		 * the beginning?
		 * 
		 * If we don't get all the response we give up. We do not return partial
		 * results.
		 */
		ArrayList<CEEnforcement> results = new ArrayList<CEEnforcement>(pdpRequests.size());
		long waitTime = longestTimeoutOfAllRequests;

		for (CheckableCallback cb : callbacks) {
			log.debug("Wait time is " + waitTime + " milliseconds");

			if (waitTime <= 0) {
				throw new CESdkTimeoutException();
			}

			/*
			 * This time is not particularly precise, despite the name. On
			 * Windows it is usually some multiple of 16ms (with 0 as an
			 * option). It doesn't matter. If you have a 5s timeout then we
			 * don't guarantee that it will be *exactly* 5 seconds.
			 * 
			 * More accurate calls are available, but they are slower.
			 */
			long start = System.currentTimeMillis();
			try {
				CEEnforcement enf = cb.getResult(waitTime, TimeUnit.MILLISECONDS);

				if (enf == null) {
					throw new CESdkTimeoutException();
				}

				results.add(enf);
			} catch (InterruptedException e) {
				throw new CESdkTimeoutException(e);
			}
			waitTime -= System.currentTimeMillis() - start;
		}

		return results;
	}

    public PermissionsResponse evaluatePermissions(List<PDPRequest> pdpPermissionsRequests) throws EvaluationConnectorException {
        PermissionsResponse response = new PermissionsResponse();
        PermissionsResponsesType responsesType = null;
        
        try {
            long startCounter = System.nanoTime();
            List<IPDPPermissionsResponse> permissionsCheckResults = checkPermissions(pdpPermissionsRequests);
            if (log.isDebugEnabled()) {
                log.debug(String.format("%s, Thread Id:[%s], PDPDirectAdaptor -> Total time taken by Direct Permissions Evaluation: %.4f ms",
                                        Constants.PERF_LOG_PREFIX, "" + Thread.currentThread().getId(),
                                        (System.nanoTime() - startCounter)/MILLISECONDS));
            }

            long responseStartCounter = System.nanoTime();
            responsesType = getPermissionsResponses(permissionsCheckResults);
            response.setStatus(ResponseStatusCode.OK.getValue());
            response.setResponsesType(responsesType);
        } catch (Exception e) {
            log.error("Error ocurred while evaluating permissions", e);
			response.setStatus(ResponseStatusCode.PROCESSING_ERROR.getValue());
            response.setResponsesType(responsesType);
        }

        return response;
    }

    private List<IPDPPermissionsResponse> checkPermissions(List<PDPRequest> pdpRequests) throws CESdkTimeoutException, CESdkException {
		LinkedList<CheckablePermissionsCallback> callbacks = new LinkedList<CheckablePermissionsCallback>();
        
        int longestTimeoutOfAllRequests = 0;
        
        for (PDPRequest pdpRequest : pdpRequests) {
            CheckablePermissionsCallback cb = new CheckablePermissionsCallback();
            callbacks.add(cb);
            
            if (pdpRequest.getHost() == null || pdpRequest.getHost().getValue(KEY_HOST_INET) == null) {
                pdpRequest.setHost(LOCALHOST);
            }
            
            if (pdpRequest.getUser() != null && pdpRequest.getUser().getValue(KEY_USER_NAME) == null) {
                pdpRequest.getUser().setAttribute(KEY_USER_NAME, pdpRequest.getUser().getValue(KEY_USER_ID));
            }

            addReturnPolicyIdInformation(pdpRequest);
            
            int timeout = pdpRequest.getTimeoutInMs();

            if (timeout == -1 || timeout > MAX_TIMEOUT_IN_MS) {
                timeout = MAX_TIMEOUT_IN_MS;
            }

            longestTimeoutOfAllRequests = Math.max(longestTimeoutOfAllRequests, timeout);
            
            try {
                IPDPPermissionsResponse response = PDPSDK.PDPGetPermissions(pdpRequest.getResourceArr(),
                                                                            pdpRequest.getUser(),
                                                                            pdpRequest.getApplication(),
                                                                            pdpRequest.getHost(),
                                                                            pdpRequest.getAdditionalData(),
                                                                            timeout,
                                                                            cb);
                
            } catch (PDPTimeout e) {
                throw new CESdkTimeoutException("Permissions query timed out", e);
            } catch (PDPException e) {
                throw new CESdkException("Permissions query exception", e);
            }
        }
        
        List<IPDPPermissionsResponse> results = new ArrayList<>(pdpRequests.size());
        long waitTime = longestTimeoutOfAllRequests;
        for (CheckablePermissionsCallback cb : callbacks) {
            log.debug("Wait time is " + waitTime + " milliseconds");

            if (waitTime <= 0) {
                throw new CESdkTimeoutException();
            }

            long start = System.currentTimeMillis();
            try {
                IPDPPermissionsResponse perms = cb.getResult(waitTime, TimeUnit.MILLISECONDS);

                if (perms == null) {
                    throw new CESdkTimeoutException();
                }

                results.add(perms);
            } catch (InterruptedException e) {
                throw new CESdkTimeoutException(e);
            }

            waitTime -= System.currentTimeMillis() - start;
        }

        return results;
    }
    
	/**
	 * This class allows us to wait on the result of each individual callback.
	 * It would seem like Future<T> would do this, but the current
	 * implementations create a thread to run the action. We put a work unit on
	 * a queue to be handled by a thread pool, with the result returned via
	 * callback.
	 */
	private static class CheckableCallback implements IPDPSDKCallback {
		ArrayBlockingQueue<CEEnforcement> q = new ArrayBlockingQueue<CEEnforcement>(1);

		public void callback(IPDPEnforcement result) {
			q.add(new CEEnforcement(result.getResult(), new CEAttributes(result
					.getObligations())));
		}

		public boolean hasResult() {
			return q.peek() != null;
		}

		public CEEnforcement getResult() throws InterruptedException {
			return q.take();
		}

		public CEEnforcement getResult(long timeout, TimeUnit unit)
				throws InterruptedException {
			return q.poll(timeout, unit);
		}
	}

    /**
     * Take the returnPolicyId and put it in the environment context, which is
     * where the PDP will look for it
     */
    private void addReturnPolicyIdInformation(PDPRequest pdpRequest) {
        IPDPNamedAttributes environment = null;
		IPDPNamedAttributes[] additionalData = {};
		if (pdpRequest.getAdditionalData() != null) {
			additionalData = pdpRequest.getAdditionalData();
		}

        for (IPDPNamedAttributes namedAttributes: additionalData) {
            if (namedAttributes.getName().equals("environment")) {
                environment = namedAttributes;
                break;
            }
        }

        if (environment == null) {
            IPDPNamedAttributes[] modified = new IPDPNamedAttributes[additionalData.length + 1];
            for (int i = 0; i < additionalData.length; i++) {
                modified[i] = additionalData[i];
            }
            environment = new PDPNamedAttributes("environment");
            modified[additionalData.length] = environment;
            pdpRequest.setAdditionalData(modified);
        }

        // If the caller specified it here we should leave it alone
        if (environment.getValue(IAgentPolicyContainer.RECORD_MATCHING_POLICIES_IN_RESULT_KEY) == null) {
            environment.setAttribute(IAgentPolicyContainer.RECORD_MATCHING_POLICIES_IN_RESULT_KEY,
                                      pdpRequest.isReturnPolicyIdList() ? "true" : "false");
        }
    }
	/**
	 * This class allows us to wait on the result of each permissions callback.
	 * It would seem like Future<T> would do this, but the current
	 * implementations create a thread to run the action. We put a work unit on
	 * a queue to be handled by a thread pool, with the result returned via
	 * callback.
	 */
	private static class CheckablePermissionsCallback implements IPDPPermissionsCallback {
		ArrayBlockingQueue<IPDPPermissionsResponse> q = new ArrayBlockingQueue<>(1);

		public void callback(IPDPPermissionsResponse result) {
			q.add(result);
		}

		public boolean hasResult() {
			return q.peek() != null;
		}

		public IPDPPermissionsResponse getResult() throws InterruptedException {
			return q.take();
		}

		public IPDPPermissionsResponse getResult(long timeout, TimeUnit unit)
				throws InterruptedException {
			return q.poll(timeout, unit);
		}
	}

	private ResponseType getErrorResponse(String errorMessage,
			ResponseStatusCode statusCode) {
		ResponseType response = new ResponseType();

		ResultType result = new ResultType();
		response.getResult().add(result);
		result.setDecision(DecisionType.INDETERMINATE);
		StatusType status = getResponseStatus(errorMessage, statusCode);
		result.setStatus(status);

		return response;
	}

	private StatusType getResponseStatus(String message,
			ResponseStatusCode statusCode) {
		StatusType status = new StatusType();
		status.setStatusMessage(message);

		StatusCodeType codeType = new StatusCodeType();
		codeType.setValue(statusCode.getValue());
		status.setStatusCode(codeType);

		return status;
	}
	
	private StatusType getResponseStatus(String message, CEResponse ceResponse) {
		
		StatusType status = new StatusType();
		StatusCodeType codeType = new StatusCodeType();
		
		if(CEResponse.ERROR == ceResponse) {
			status.setStatusMessage("Error");
			codeType.setValue(ResponseStatusCode.PROCESSING_ERROR.getValue());
		} else if (CEResponse.DONTCARE == ceResponse) {
			status.setStatusMessage("Not Applicable");
			codeType.setValue(ResponseStatusCode.OK.getValue());	
		} else {
			status.setStatusMessage(message);
			codeType.setValue(ResponseStatusCode.OK.getValue());	
		}
		
		status.setStatusCode(codeType);
		return status;
	}

    private PermissionsResponsesType getPermissionsResponses(List<IPDPPermissionsResponse> permissionsResponses) {
        PermissionsResponsesType responses = new PermissionsResponsesType();
        List<PermissionsResponseType> permissionsResponseList = new ArrayList<>();
        
        for (IPDPPermissionsResponse permissionsResponse : permissionsResponses) {
            PermissionsResponseType response = new PermissionsResponseType();

            for (String effect : PermissionsResponseType.EFFECTS_TO_REPORT) {
                ArrayList<PermissionsActionObligations> actionObligationsList = new ArrayList<>();
                
                for (String action : permissionsResponse.getPermittedActionsForEffect(effect)) {
                    PermissionsActionObligations actionObligations = new PermissionsActionObligations();
                    
                    ObligationsType allObligations = new ObligationsType();
                    
                    actionObligations.setAction(action);
                    actionObligations.setObligations(getObligations(permissionsResponse.getObligationsForAction(effect, action)));
                    actionObligations.setMatchingPolicies(new ArrayList<String>(permissionsResponse.getPoliciesForAction(effect, action)));
                    actionObligationsList.add(actionObligations);
                }

                PermissionsActionObligationsList permissionsActionObligationsList = new PermissionsActionObligationsList();
                permissionsActionObligationsList.setActionObligations(actionObligationsList);
                response.setActionsObligationsList(effect, permissionsActionObligationsList);
            }
            permissionsResponseList.add(response);
        }
        
        responses.setPermissionsResponses(permissionsResponseList);

        return responses;
    }
    
	private ResponseType getResponse(List<CEEnforcement> enforcementResults) {
		ResponseType response = new ResponseType();

		for (CEEnforcement enforcement : enforcementResults) {
			ResultType result = getResult(enforcement.getResponse());
			StatusType status = getResponseStatus("success", enforcement.getResponse());

			ObligationsType obligations = getObligations(enforcement
					.getObligations());

			result.setStatus(status);
			result.setObligations(obligations);

			response.getResult().add(result);
		}

		if (log.isDebugEnabled())
			log.debug("Response created from CE Enforcement Result.");
		return response;
	}

    private ObligationsType getObligations(Collection<IObligationResultData> obligations) {
        ObligationsType obligationsType = new ObligationsType();

        if (obligations == null) {
            return obligationsType;
        }

        for (IObligationResultData obligation : obligations) {
            ObligationType obligationType = new ObligationType();
            
            obligationType.setObligationId(obligation.getObligationName());

            for (int i = 0; i < obligation.getArguments().size(); i+=2) {
                String id = obligation.getArguments().get(i);
                String value = obligation.getArguments().get(i+1);

                AttributeAssignmentType assignmentType = new AttributeAssignmentType();
                assignmentType.setAttributeId(id);
                assignmentType.getContent().add(value);
				assignmentType.setCategory(XACMLParser.CATEGORY_OBLIGATION_ATTR_ASSIGNMENT);
				assignmentType.setDataType(XACMLParser.XACML_STRING_DATA_TYPE);

				obligationType.getAttributeAssignment().add(assignmentType);
            }
			obligationsType.getObligation().add(obligationType);
        }
        
        return obligationsType;
    }
    
	private ObligationsType getObligations(CEAttributes obligations) {
		ObligationsType obligationsType = new ObligationsType();

		List<CEAttribute> attribList = obligations.getAttributes();
		Map<String, String> obligationsMap = new HashMap<String, String>();

		for (CEAttribute ceAttribute : attribList) {
			obligationsMap.put(ceAttribute.getKey(), ceAttribute.getValue());
		}

		int obligationsCount = getIntValue(obligationsMap
				.get(ATTR_OBLIGATION_COUNT));
		if (obligationsCount < 1) {
			return obligationsType;
		}

		for (int i = 1; i <= obligationsCount; i++) {

			ObligationType obligationType = new ObligationType();
			String obligationId = obligationsMap.get(ATTR_OBLIGATION_NAME + ":"
					+ i);
			obligationType.setObligationId(obligationId);

			int numObligationAttribs = getIntValue(obligationsMap
					.get(ATTR_OBLIGATION_NUMVALUES + ":" + i));
			for (int j = 1; j <= numObligationAttribs; j += 2) {
				String attributeId = obligationsMap.get(ATTR_OBLIGATION_VALUE
						+ ":" + i + ":" + j);
				String attributeValue = obligationsMap
						.get(ATTR_OBLIGATION_VALUE + ":" + i + ":" + (j + 1));

				if (attributeValue == null || attributeValue.isEmpty()) {
					attributeValue = "";
				}

				AttributeAssignmentType assignmentType = new AttributeAssignmentType();
				assignmentType.setAttributeId(attributeId);
				assignmentType.getContent().add(attributeValue);
				assignmentType.setCategory(XACMLParser.CATEGORY_OBLIGATION_ATTR_ASSIGNMENT);
				assignmentType.setDataType(XACMLParser.XACML_STRING_DATA_TYPE);

				obligationType.getAttributeAssignment().add(assignmentType);
			}
			obligationsType.getObligation().add(obligationType);
		}

		return obligationsType;
	}

	private int getIntValue(String value) {
		if (value != null && isNumeric(value)) {
			return Integer.valueOf(value);
		} else {
			return 0;
		}
	}

	public static boolean isNumeric(String s) {
		return s.matches("[-+]?\\d*\\.?\\d+");
	}

	private ResultType getResult(CEResponse ceResponse) {
		ResultType result = new ResultType();
		if (ceResponse == CEResponse.ALLOW) {
			result.setDecision(DecisionType.PERMIT);
			
		} else if (ceResponse == CEResponse.DENY) {
			result.setDecision(DecisionType.DENY);

		} else if (ceResponse == CEResponse.DONTCARE) {
			result.setDecision(DecisionType.NOT_APPLICABLE);

		} else if (ceResponse == CEResponse.ERROR) {
			result.setDecision(DecisionType.INDETERMINATE);

		} else {
			result.setDecision(DecisionType.INDETERMINATE);
		}
		return result;
	}

	@Override
	public void closeConnection() {
		log.info("Nothing to close");
	}

}
