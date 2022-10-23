/*
 * Created on May 11, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.agent.controlmanager;

import java.util.List;
import java.util.Map;

import com.bluejungle.destiny.agent.activityjournal.IActivityJournal;
import com.bluejungle.framework.comp.PropertyKey;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.pf.domain.destiny.resource.AgentResourceManager;
import com.bluejungle.pf.domain.destiny.subject.IDSubjectManager;
import com.bluejungle.pf.engine.destiny.IAgentPolicyContainer;
import com.nextlabs.destiny.agent.controlmanager.IPolicyEvaluationResult;
import com.nextlabs.destiny.agent.pdpapi.IPDPPermissionsResponse;
import com.nextlabs.pf.engine.destiny.IAgentXACMLPolicyContainer;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;

/**
 * @author ihanen
 */

public interface IPolicyEvaluator {

    /**
     * Configuration parameters required for init
     */
    PropertyKey<IAgentPolicyContainer> AGENT_POLICY_CONTAINER_CONFIG_PARAM = new PropertyKey<IAgentPolicyContainer>("AgentPolicyContainer");
    PropertyKey<IAgentXACMLPolicyContainer> AGENT_XACML_POLICY_CONTAINER_CONFIG_PARAM = new PropertyKey<IAgentXACMLPolicyContainer>("AgentXACMLPolicyContainer");
    PropertyKey<IControlManager> CONTROL_MANAGER_CONFIG_PARAM = new PropertyKey<IControlManager>("ControlManager");
    PropertyKey<AgentResourceManager> RESOURCE_MANAGER_CONFIG_PARAM = new PropertyKey<AgentResourceManager>("ResourceManager");
    PropertyKey<IDSubjectManager> SUBJECT_MANAGER_CONFIG_PARAM = new PropertyKey<IDSubjectManager>("SubjectManager");
    PropertyKey<IActivityJournal> ACTIVITY_JOURNAL_CONFIG_PARAM = new PropertyKey<IActivityJournal>("ActivityJournal");
    
    /**
     * Index of arguments for policy evaluation
     */
    int OLD_ACTION_INDEX = 1;
    int OLD_TIMESTAMP_INDEX = 2;
    int OLD_IGNORE_OBLIGATIONS_INDEX = 3;
    int OLD_LOG_LEVEL_INDEX = 4;
    int OLD_APP_NAME_INDEX = 5;
    int OLD_APP_PATH_INDEX = 6;
    int OLD_APP_FINGERPRINT_INDEX = 7;
    int OLD_USER_SID_INDEX = 8;
    int OLD_USER_NAME_INDEX = 9;
    int OLD_IP_ADDRESS_INDEX = 10;
    int OLD_PROCESS_TOKEN_INDEX = 11;
    int OLD_SOURCE_INDEX = 12;
    int OLD_SOURCE_ATTRS_INDEX = 13;
    int OLD_TARGET_INDEX = 14;
    int OLD_TARGET_ATTRS_INDEX = 15;

    int REQUEST_ID_INDEX = 0;
    int REQUEST_SOCKET_ID_INDEX = 1;
    int AGENT_TYPE_INDEX = 2;
    int TIMESTAMP_INDEX = 3;
    int IGNORE_OBLIGATIONS_INDEX = 4;
    int LOG_LEVEL_INDEX = 5;
    int PROCESS_TOKEN_INDEX = 6;
    int DIMENSION_DEFS_INDEX = 7;

    // Various special query attribute values for the PolicyEvaluatorImpl class
    String DONT_CARE_ACCEPTABLE_KEY = "dont-care-acceptable";
    String ERROR_RESULT_ACCEPTABLE_KEY = "error-result-acceptable";
    String LOGGING_LEVEL_KEY = "nextlabs-logging-level";
    String PROCESS_TOKEN_KEY = "nextlabs-process-token";
    String PERFORM_OBLIGATIONS_KEY = "nextlabs-perform-obligations";
    
    /**
     * Queries the decision engine to check if the operation is allowed.
     * Packages the results into an array.
     * 
     * @param paramArray
     *            array of string input parameters. Should contain strings in
     *            the following order: resource, action, user, application, host
     * @param resultParamArray
     *            output parameters
     * @return true if call is successful, false otherwise.
     *  
     */
    boolean queryDecisionEngine(Object[] paramArray, List resultParamArray);

    /**
     * Queries the decision engine to check if the operation is allowed.  The results are
     * packaged into an array
     *
     * @param context a collection of DynamicAttributes describing the evaluation context (from, to, user, etc)
     * @param processToken the process token, to be used for user impersonation
     * @param resultParamArray
     *            output parameters
     * @return true if call is successful, false otherwise
     */
    boolean queryDecisionEngine(Map<String, DynamicAttributes> context, Long processToken, int loggingLevel, boolean ignoreObligations, List resultParamArray);

    /**
     * Queries the decision engine to check if the operation is allowed. The result is returned in PolicyEvaluationResult
     *
     * @param context a collection of DynamicAttributes describing the evaluation context (from, to, user, etc)
     * @param processToken the process token, to be used for user impersonation
     */
    IPolicyEvaluationResult queryDecisionEngine(Map<String, DynamicAttributes> context);
    
    /**
     * Queries a true XACML decision engine
     */
    IWrappedXACMLResponse queryXacmlDecisionEngine(String request, String dataType);

    /**
     * Queries the permissions API
     */
    IPDPPermissionsResponse queryPermissionsEngine(Map<String, DynamicAttributes> context);

    /**
     * Alternative call for permissions query used by C-API
     */
    boolean queryPermissionsEngine(Object[] paramArray, List resultParamArray);
}

