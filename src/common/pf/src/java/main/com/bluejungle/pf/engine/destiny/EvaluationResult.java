/*
 * Created on Dec 3, 2004
 * All sources, binaries and HTML pages
 * (C) copyright 2004 by Blue Jungle Inc., Redwood City CA,
 * Ownership remains with Blue Jungle Inc,
 * All rights reserved worldwide.
 */
package com.bluejungle.pf.engine.destiny;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluejungle.pf.domain.destiny.misc.EffectType;
import com.bluejungle.pf.domain.destiny.misc.IDEffectType;
import com.bluejungle.pf.domain.destiny.obligation.IDObligation;
import com.nextlabs.domain.log.PolicyActivityInfoV5;

/**
 * EvaluationResult contains complete information about policy evaluation. It
 * consists of a result, which will be one of ALLOW, DENY, DONT_CARE (no policies were
 * applicable), ERROR (error during evaluation)
 *
 * Each evaluation request is also assigned a globally-unique request id, which is
 * returned in the result.
 *
 * The result may include a list of obligations to be performed by
 * either the PDP or PEP
 *
 * The result may include information about which policies
 * allowed/denied/did not apply
 *
 * @author sasha
 */

public class EvaluationResult {
    public final static String ALLOW = EffectType.ALLOW_NAME;
    public final static String DENY  = EffectType.DENY_NAME;
    public final static String DONT_CARE = EffectType.DONT_CARE_NAME;
    public final static String ERROR = EffectType.ERROR_NAME;

    private IDEffectType effectType;
    private long requestId;
    private final List<IDObligation> obligations = new ArrayList<IDObligation>();
    private final Map<String, Collection<String>> applicablePolicies = new HashMap<String, Collection<String>>();
    private EvaluationRequest req;
    private PolicyActivityInfoV5 paInfo;
    private long logUid = 0;

    /**
     * Constructor
     * @param rquest
     * @param effectName
     */
    public EvaluationResult(EvaluationRequest req, IDEffectType effectType) {
        this.req = req;
        this.effectType = effectType;
    }

    /**
     * Constructor
     * @param rquest
     */
    public EvaluationResult(EvaluationRequest req) {
        this.req = req;
    }
    
    /**
     * set the effectName of this evaluation resulit
     */
    public void setEffectType(IDEffectType effectType) {
        this.effectType = effectType;
    }
    
    /**
     * @return effecttype of this evaluation result
     */
    public IDEffectType getEffectType() {
        return effectType;
    }

    /**
     * @return the name of the effect type of this evaluation result
     */
    public String getEffectName() {
        return effectType.getName();
    }
    
    /**
     * Returns the requestId.
     * @return the requestId.
     */
    public long getRequestId() {
        return this.req.getRequestId().longValue();
    }

    public void addObligation(IDObligation obl) {
        obligations.add(obl);
    }

    public void setPAInfo(PolicyActivityInfoV5 paInfo) {
        this.paInfo = paInfo;
    }

    public PolicyActivityInfoV5 getPAInfo() {
        return paInfo;
    }

    public List<IDObligation> getObligations() {
        return obligations;
    }

    public EvaluationRequest getEvaluationRequest() {
        return req;
    }

    public synchronized void addToApplicablePolicies(String result, String policyName) {
        Collection<String> policies = applicablePolicies.get(result);

        if (policies == null) {
            policies = new ArrayList<String>();
            applicablePolicies.put(result, policies);
        }

        policies.add(policyName);
    }

    public Collection<String> getPoliciesForResult(String result) {
        return applicablePolicies.get(result);
    }

    public Map<String, Collection<String>> getApplicablePolicies() {
        return applicablePolicies;
    }
    
    public void setLogUid(long logUid) {
        // Only set a uid if it hasn't already been set
        if (this.logUid == 0) {
            this.logUid = logUid;
        }
    }

    public long getLogUid() {
        return logUid;
    }
}
