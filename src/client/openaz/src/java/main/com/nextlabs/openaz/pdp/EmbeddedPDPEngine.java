/*
 * Created on Aug 15, 2016
 *
 * All sources, binaries and HTML pages (C) copyright 2016 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan & sduan
 */
package com.nextlabs.openaz.pdp;

import static com.nextlabs.openaz.utils.Constants.DPC_ROOT_PROPERTY;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Obligation;
import org.apache.openaz.xacml.api.Permissions;
import org.apache.openaz.xacml.api.PermissionsResponse;
import org.apache.openaz.xacml.api.PermissionsResult;
import org.apache.openaz.xacml.api.PoliciesAndObligations;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.Response;
import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.api.pdp.PDPEngine;
import org.apache.openaz.xacml.api.pdp.PDPException;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdAttributeAssignment;
import org.apache.openaz.xacml.std.StdAttributeValue;
import org.apache.openaz.xacml.std.StdObligation;
import org.apache.openaz.xacml.std.StdPermissions;
import org.apache.openaz.xacml.std.StdPermissionsResponse;
import org.apache.openaz.xacml.std.StdPermissionsResult;
import org.apache.openaz.xacml.std.StdPoliciesAndObligations;
import org.apache.openaz.xacml.std.StdResponse;
import org.apache.openaz.xacml.std.StdResult;

import com.bluejungle.destiny.agent.pdpapi.IPDPApplication;
import com.bluejungle.destiny.agent.pdpapi.IPDPEnforcement;
import com.bluejungle.destiny.agent.pdpapi.IPDPHost;
import com.bluejungle.destiny.agent.pdpapi.IPDPNamedAttributes;
import com.bluejungle.destiny.agent.pdpapi.IPDPResource;
import com.bluejungle.destiny.agent.pdpapi.IPDPSDKCallback;
import com.bluejungle.destiny.agent.pdpapi.IPDPUser;
import com.bluejungle.destiny.agent.pdpapi.PDPApplication;
import com.bluejungle.destiny.agent.pdpapi.PDPHost;
import com.bluejungle.destiny.agent.pdpapi.PDPSDK;
import com.bluejungle.destiny.agent.pdpapi.PDPTimeout;
import com.bluejungle.pf.engine.destiny.EvaluationResult;
import com.bluejungle.pf.domain.destiny.misc.EffectType;
import com.nextlabs.openaz.pdp.beans.PDPRequest;
import com.nextlabs.openaz.pdp.utils.PDPRequestUtil;
import com.nextlabs.openaz.pepapi.Host;
import com.nextlabs.destiny.agent.controlmanager.IObligationResultData;
import com.nextlabs.destiny.agent.pdpapi.IPDPPermissionsCallback;
import com.nextlabs.destiny.agent.pdpapi.IPDPPermissionsResponse;

public class EmbeddedPDPEngine implements PDPEngine {
	private static final String ATTR_OBLIGATION_COUNT = "CE_ATTR_OBLIGATION_COUNT";
    private static final String ATTR_OBLIGATION_NAME = "CE_ATTR_OBLIGATION_NAME";
    private static final String ATTR_OBLIGATION_NUMVALUES = "CE_ATTR_OBLIGATION_NUMVALUES";
    private static final String ATTR_OBLIGATION_VALUE = "CE_ATTR_OBLIGATION_VALUE";
    
    private static final String ISSUER = "nextlabs.com";
    
    // Not sure about this
    private static final Identifier ATTRIBUTE_CATEGORY = new IdentifierImpl("obligation-attribute");
    
	private static final List<URI> EMPTY_PROFILES = new ArrayList<URI>();
	private static final int DEFAULT_TIMEOUT_MS = 30 * 1000;
	private volatile static EmbeddedPDPEngine engine = null;
	
	private Properties properties;
	
	private EmbeddedPDPEngine(Properties properties) throws PDPException {
		if (properties == null) {
            throw new PDPException("properties is null");
        }

        String dpcRoot = properties.getProperty(DPC_ROOT_PROPERTY);

        if (dpcRoot == null) {
            throw new PDPException(DPC_ROOT_PROPERTY + " is null");
        }

        try {
            PDPSDK.initializePDP(dpcRoot);
            
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        PDPSDK.shutdown();
                    } catch (com.bluejungle.destiny.agent.pdpapi.PDPException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (com.bluejungle.destiny.agent.pdpapi.PDPException e) {
            throw new PDPException("Error initializing embedded PDP", e);
        }
	}
	
	public static EmbeddedPDPEngine getInstance(Properties properties) throws PDPException {
        if (engine == null) {
            synchronized(EmbeddedPDPEngine.class) {
                if (engine == null) {
                    engine = new EmbeddedPDPEngine(properties);
                }
            }
        }

        return engine;
    }
	
	@Override
	public Response decide(Request pepRequest) throws PDPException {
		List<PDPRequest> pdpRequests = PDPRequestUtil.convert(pepRequest);
		List<IPDPEnforcement> ipdpEnforcements = decide(pdpRequests);
		List<Result> results = new ArrayList<Result>();
		for(IPDPEnforcement enforcement: ipdpEnforcements) {
			results.add(buildResult(enforcement));
		}
		return new StdResponse(results);
	}

    @Override
    public PermissionsResponse decidePermissions(Request pepRequest) throws PDPException {
        List<PDPRequest> pdpPermissionsRequests = PDPRequestUtil.convert(pepRequest);
        List<IPDPPermissionsResponse> pdpPermissionsResponses = decidePermissions(pdpPermissionsRequests);
        List<PermissionsResult> results = new ArrayList<PermissionsResult>();
        for (IPDPPermissionsResponse permissionsResponse: pdpPermissionsResponses) {
            results.add(buildPermissionsResult(permissionsResponse));
        }

        return new StdPermissionsResponse(results);
    }
    
	/**
     * This class allows us to wait on the result of each individual callback.
     * It would seem like Future<T> would do this, but the current implementations
     * create a thread to run the action. We put a work unit on a queue to be handled
     * by a thread pool, with the result returned via callback.
     */

    private class CheckablePDPSDKCallback extends CheckableCallback<IPDPEnforcement> implements IPDPSDKCallback {
        public void callback(IPDPEnforcement result) {
            q.add(result);
        }
    }
    
    private class CheckablePDPPermissionsCallback extends CheckableCallback<IPDPPermissionsResponse> implements IPDPPermissionsCallback {
        public void callback(IPDPPermissionsResponse result) {
            q.add(result);
        }
    }
    
    private abstract class CheckableCallback<T> {
        protected ArrayBlockingQueue<T> q = new ArrayBlockingQueue<T>(1);

        public boolean hasResult() {
            return q.peek() != null;
        }

        public T getResult() throws InterruptedException {
            return q.take();
        }

        public T getResult(long timeout, TimeUnit unit) throws InterruptedException {
            return q.poll(timeout, unit);
        }
    }
    
	private List<IPDPEnforcement> decide(List<PDPRequest> pdpRequests) throws PDPException {
		LinkedList<CheckablePDPSDKCallback> callbacks = new LinkedList<>();
		
		for(PDPRequest pdpRequest: pdpRequests) {
			CheckablePDPSDKCallback cb = new CheckablePDPSDKCallback();
			callbacks.add(cb);
			try {
				String action = pdpRequest.getAction();
				IPDPResource[] resources = new IPDPResource[] {pdpRequest.getResource()};
				IPDPUser user = pdpRequest.getUser();
				IPDPApplication application = pdpRequest.getApplication();
				if(application == null) { application = new PDPApplication("Unknown"); }
				IPDPHost host = pdpRequest.getHost();
				if(host == null) { host = new PDPHost(Host.LOCAL_HOST); }
				IPDPNamedAttributes[] additionalData = pdpRequest.getAllNamedAttributes().toArray(
						  new IPDPNamedAttributes[pdpRequest.getAllNamedAttributes().size()]);
				PDPSDK.PDPQueryDecisionEngine(action,
											  resources,
											  user,
											  application,
											  host,
											  true,
											  additionalData,
											  PDPSDK.NOISE_LEVEL_USER_ACTION,
											  DEFAULT_TIMEOUT_MS, // get from ENV?
	                                          cb);
			}catch (PDPTimeout e) {
	            throw new PDPException(e);
	        } catch (com.bluejungle.destiny.agent.pdpapi.PDPException e) {
	            throw new PDPException(e);
	        }
		}
		
		int waitTime = DEFAULT_TIMEOUT_MS;
		List<IPDPEnforcement> results = new ArrayList<IPDPEnforcement>();
		
		for(CheckablePDPSDKCallback cb: callbacks) {
			/* This time is not particularly precise, despite the name. On Windows it is usually some
             * multiple of 16ms (with 0 as an option). It doesn't matter. If you have a 5s timeout then
             * we don't guarantee that it will be *exactly* 5 seconds.
             *
             * More accurate calls are available, but they are slower.
             */
            long start = System.currentTimeMillis();
            try {
                 IPDPEnforcement enf = cb.getResult(waitTime, TimeUnit.MILLISECONDS);

                if (enf == null) {
                    throw new PDPException("timeout gettting enforcement");
                }

                results.add(enf);
            } catch (InterruptedException e) {
                throw new PDPException(e);
            }
            waitTime -= System.currentTimeMillis() - start;
		}
		return results;
	}
    
	private List<IPDPPermissionsResponse> decidePermissions(List<PDPRequest> pdpRequests) throws PDPException {
        LinkedList<CheckablePDPPermissionsCallback> callbacks = new LinkedList<>();

        for (PDPRequest pdpRequest: pdpRequests) {
            CheckablePDPPermissionsCallback cb = new CheckablePDPPermissionsCallback();
            callbacks.add(cb);
            try {
                IPDPResource[] resources = new IPDPResource[]{pdpRequest.getResource()};
                IPDPUser user = pdpRequest.getUser();
                IPDPApplication application = pdpRequest.getApplication() == null ? new PDPApplication("Unknown") : pdpRequest.getApplication();
                IPDPHost host = pdpRequest.getHost() == null ? new PDPHost(Host.LOCAL_HOST) : pdpRequest.getHost();
                IPDPNamedAttributes[] additionalData = pdpRequest.getAllNamedAttributes().toArray(new IPDPNamedAttributes[pdpRequest.getAllNamedAttributes().size()]);
                PDPSDK.PDPGetPermissions(resources,
                                         user,
                                         application,
                                         host,
                                         additionalData,
                                         DEFAULT_TIMEOUT_MS,
                                         cb);
            } catch (PDPTimeout | com.bluejungle.destiny.agent.pdpapi.PDPException e) {
                throw new PDPException(e);
            }
        }

        int waitTime = DEFAULT_TIMEOUT_MS;
        List<IPDPPermissionsResponse> results = new ArrayList<>();

        for (CheckablePDPPermissionsCallback cb: callbacks) {
            long start = System.currentTimeMillis();
            try {
                IPDPPermissionsResponse response = cb.getResult(waitTime, TimeUnit.MILLISECONDS);

                if (response == null) {
                    throw new PDPException("timeout getting enforcement");
                }

                results.add(response);
            } catch (InterruptedException e) {
                throw new PDPException(e);
            }
            waitTime -= System.currentTimeMillis() - start;
        }

        return results;
    }
	
	private Result buildResult(IPDPEnforcement enforcementResult) {
		return new StdResult(buildDecision(enforcementResult.getResult()),
                buildObligations(enforcementResult.getObligations()),
                null, null, null, null);
	}

    private PermissionsResult buildPermissionsResult(IPDPPermissionsResponse permissionsResult) {
        Map<Decision, Permissions> decisionMap = new HashMap<>();
        
        for (EffectType effectType : EffectType.elements()) {
            String effect = effectType.getName();
            Map<String, PoliciesAndObligations> policiesAndObligationsMap = new HashMap<>();
            
            for (String action : permissionsResult.getPermittedActionsForEffect(effect)) {
                PoliciesAndObligations policiesAndObligations = new StdPoliciesAndObligations(permissionsResult.getPoliciesForAction(effect, action),
                                                                                              buildObligations(permissionsResult.getObligationsForAction(effect, action)));

                policiesAndObligationsMap.put(action, policiesAndObligations);
            }

            StdPermissions permissions = new StdPermissions(policiesAndObligationsMap);

            decisionMap.put(buildDecision(effect), permissions);
        }

        return new StdPermissionsResult(decisionMap);
    }
    
	private Decision buildDecision(String result) {
        if (result.equals(EvaluationResult.ALLOW)) {
            return Decision.PERMIT;
        } else if (result.equals(EvaluationResult.DENY)) {
            return Decision.DENY;
        } else {
            return Decision.NOTAPPLICABLE;
        }
    }
	
	private Collection<Obligation> buildObligations(String[] ob) {
        List<Obligation> obligations = new ArrayList<Obligation>();
        
        Map<String, String> obligationsMap = new HashMap<String, String>();

        for (int i = 0; i < ob.length; i+=2) {
            obligationsMap.put(ob[i], ob[i+1]);
        }

        String numObligations = obligationsMap.get(ATTR_OBLIGATION_COUNT);

        if (numObligations == null) {
            return obligations;
        }
        
        // Obligation numbers start at 1. Stupid choice on my part
        for (int i = 1; i <= Integer.valueOf(numObligations); i++) {
            Identifier id = new IdentifierImpl(obligationsMap.get(ATTR_OBLIGATION_NAME + ":" + i));

            List<AttributeAssignment> attributeAssignments = new ArrayList<AttributeAssignment>();
            
            int argCount = Integer.valueOf(obligationsMap.get(ATTR_OBLIGATION_NUMVALUES + ":" + i));

            for (int j = 1; j <= argCount; j+=2) {
                String attrKey = obligationsMap.get(ATTR_OBLIGATION_VALUE + ":" + i + ":" + j);
                String attrValue = obligationsMap.get(ATTR_OBLIGATION_VALUE + ":" + i + ":" + (j+1));

                if (attrValue == null || attrValue.equals("")) {
                    attrValue = attrKey;
                }

                attributeAssignments.add(buildAttributeAssignment(attrKey, attrValue));
            }

            obligations.add(new StdObligation(id, attributeAssignments));
        }
        
        return obligations;
    }

    private Collection<Obligation> buildObligations(Collection<IObligationResultData> obligations) {
        List<Obligation> constructedObligations = new ArrayList<>();

        for (IObligationResultData obligation : obligations) {
            Identifier id = new IdentifierImpl(obligation.getObligationName());

            List<AttributeAssignment> attributeAssignments = new ArrayList<>();
            
            for (int i = 0; i < obligation.getArguments().size(); i+= 2) {
                String key = obligation.getArguments().get(i);
                String value = obligation.getArguments().get(i+1);

                attributeAssignments.add(buildAttributeAssignment(key, value));
            }

            constructedObligations.add(new StdObligation(id, attributeAssignments));
        }

        return constructedObligations;
    }
    
	private AttributeAssignment buildAttributeAssignment(String key, String value) {
        return new StdAttributeAssignment(ATTRIBUTE_CATEGORY,
                                          new IdentifierImpl(key),
                                          ISSUER,
                                          new StdAttributeValue<String>(XACML3.ID_DATATYPE_STRING, value));
    }

	@Override
	public Collection<URI> getProfiles() {
		return EMPTY_PROFILES;
	}

	@Override
	public boolean hasProfile(URI uriProfile) {
		return false;
	}

}
