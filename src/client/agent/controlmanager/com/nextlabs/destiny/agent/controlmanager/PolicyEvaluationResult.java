/*
 * Created on Sep 23, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.agent.controlmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluejungle.pf.domain.destiny.misc.IDEffectType;
import com.nextlabs.destiny.agent.controlmanager.IObligationResultData;

/**
 * This is the result of calling IPolicyEvaluator.queryDecisionEngine().
 */
public class PolicyEvaluationResult implements IPolicyEvaluationResult {
    private String effectName = IDEffectType.ALLOW_NAME;
    private Map<String, Collection<String>> matchingPolicies = new HashMap<String, Collection<String>>();
    private List<IObligationResultData> obligations = new ArrayList<>();
    private long cacheHint = -1;
    
    public PolicyEvaluationResult() {
    }
        
    public PolicyEvaluationResult(String effectName) {
        this.effectName = effectName;
    }

    @Override
    public String getEffectName() {
        return effectName;
    }

    public void setEffectName(String effectName) {
        this.effectName = effectName;
    }

    @Override
    public Map<String, Collection<String>> getMatchingPolicies() {
        return matchingPolicies;
    }

    @Override
    public Collection<String> getMatchingPolicies(String effect) {
        return matchingPolicies.get(effect);
    }

    public void setMatchingPolicies(Map<String, Collection<String>> matchingPolicies) {
        this.matchingPolicies = matchingPolicies;
    }

    @Override
    public Collection<IObligationResultData> getObligations() {
        return obligations;
    }
    
    public void addObligation(IObligationResultData obligation) {
        obligations.add(obligation);
    }

    public void setCacheHint(long cacheHint) {
        this.cacheHint = cacheHint;
    }

    @Override
    public long getCacheHint() {
        return cacheHint;
    }
}
