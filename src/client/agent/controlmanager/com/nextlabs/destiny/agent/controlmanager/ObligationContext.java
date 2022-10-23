/*
 * Created on Oct 05, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.agent.controlmanager;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.pf.engine.destiny.EvaluationResult;
import com.bluejungle.pf.domain.destiny.obligation.DObligation;
import com.bluejungle.pf.domain.destiny.obligation.IDObligation;
import com.bluejungle.destiny.agent.controlmanager.PolicyEvaluatorImpl;

public class ObligationContext {
    private Map<String, DynamicAttributes> obligationDescriptionByPolicy;
    private Map<String, Long> logUidByPolicy;
    private PolicyEvaluationResult policyEvaluationResult;
    private EvaluationResult result;
    private PerformObligationsEnumType performObligations;
    
    public ObligationContext(PolicyEvaluationResult policyEvaluationResult, EvaluationResult result, PerformObligationsEnumType performObligations) {
        obligationDescriptionByPolicy = new HashMap<String, DynamicAttributes>();
        logUidByPolicy = new HashMap<String, Long>();
        this.policyEvaluationResult = policyEvaluationResult;
        this.result = result;
        this.performObligations = performObligations;
    }

    private String getPolicyName(DObligation obl) {
        return obl.getPolicy().getName();
    }

    public void addDescription(DObligation obl, String description) {
        String policyName = getPolicyName(obl);
        DynamicAttributes policyObligations = obligationDescriptionByPolicy.get(policyName);
        if (policyObligations == null) {
            policyObligations = new DynamicAttributes();
            obligationDescriptionByPolicy.put(policyName, policyObligations);
        }

        policyObligations.add("Obligation", description);
    }

    public DynamicAttributes getDescription(DObligation obl) {
        return obligationDescriptionByPolicy.get(getPolicyName(obl));
    }

    public Long getLogUid(String policyName) {
        return  logUidByPolicy.get(policyName);
    }
    
    public void setLogUid(String policyName, Long uid) {
        logUidByPolicy.put(policyName, uid);
    }

    public List<IDObligation> getObligations() {
        return result.getObligations();
    }

    public EvaluationResult getEvaluationResult() {
        return result;
    }

    public PolicyEvaluationResult getPolicyEvaluationResult() {
        return policyEvaluationResult;
    }

    public PerformObligationsEnumType getPerformObligations() {
        return performObligations;
    }
}
