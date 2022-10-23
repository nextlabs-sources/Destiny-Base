package com.nextlabs.pf.engine.destiny;

/*
 * Created on Dec 05, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.comp.PropertyKey;

public interface IAgentXACMLPolicyContainer {
    PropertyKey<String> BASE_DIR_PROPERTY_NAME = new PropertyKey<String>("BaseDirProperty");

    IAgentXACMLPolicyContainer DUMMY_POLICY_CONTAINER = new IAgentXACMLPolicyContainer() {
        public XACMLEvaluationResult evaluate(String request, String dataType) {
            return new XACMLEvaluationResult(IWrappedXACMLResponse.DUMMY_WRAPPED_RESPONSE);
        }
        
        public void setPolicyFinder(IXACMLPolicyFinderModule policyFinder) {
            return;
        }
    };
    
    ComponentInfo<IAgentXACMLPolicyContainer> COMP_INFO = new ComponentInfo<IAgentXACMLPolicyContainer>(IAgentXACMLPolicyContainer.class.getName(),
                                                                                                        AgentXACMLPolicyContainer.class,
                                                                                                        IAgentXACMLPolicyContainer.class,
                                                                                                        LifestyleType.SINGLETON_TYPE);

    /**
     * Set the policy finder object. This will be used by the policy
     * container to get information about the recieved XACML policies
     */
    void setPolicyFinder(IXACMLPolicyFinderModule policyFinder);
    
    /**
     * Evaluate a XACML request against the XACML (non-ACPL) policies that may exist in the system
     *
     * @param request the request
     * @param dataType either JSON or XML
     */
    XACMLEvaluationResult evaluate(String request, String dataType);
}
