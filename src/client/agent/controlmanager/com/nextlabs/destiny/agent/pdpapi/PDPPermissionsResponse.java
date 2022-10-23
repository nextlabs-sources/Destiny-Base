package com.nextlabs.destiny.agent.pdpapi;

/*
 * Created on Jul 31, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.nextlabs.destiny.agent.controlmanager.IObligationResultData;

/**
 * Holds the permissions response. The permissions response is broken
 * down by effect type (allow, deny, does not apply). For each effect
 * type we have actions that will produce that effect type, the
 * obligations that action would trigger, and the policies that were
 * applicable (obviously for "does not apply" the obligations and
 * policies will be empty).
 */
public class PDPPermissionsResponse implements IPDPPermissionsResponse {
    private class ActionResponses {
        private Map<String, ActionResponse> actionResponses;

        public ActionResponses() {
            actionResponses = new HashMap<>();
        }

        public void setActionResponse(String action, ActionResponse actionResponse) {
            actionResponses.put(action, actionResponse);
        }

        public ActionResponse getActionResponse(String action) {
            return actionResponses.get(action);
        }

        public Map<String, ActionResponse> getActionResponses() {
            return actionResponses;
        }
    }
    
    private class ActionResponse {
        private Set<IObligationResultData> obligations;
        private Collection<String> policies;

        public ActionResponse() {
            obligations = new HashSet<>();
            policies = new HashSet<>();
        }
        
        public Set<IObligationResultData> getObligations() {
            return obligations;
        }
        
        public Collection<String> getPolicies() {
            return policies;
        }
    }

    private Map<String, ActionResponses> actionResponsesByEffect;
    
    public PDPPermissionsResponse() {
        actionResponsesByEffect = new HashMap<>();
    }

    public void addAction(String effect, String action) {
        addAction(effect, action, null, null);
    }
    
    public void addAction(String effect, String action, Collection<IObligationResultData> obligations, Collection<String> policies) {

        ActionResponses actionResponses = actionResponsesByEffect.get(effect);

        if (actionResponses == null) {
            actionResponses = new ActionResponses();
            actionResponsesByEffect.put(effect, actionResponses);
        }

        ActionResponse actionResponse = actionResponses.getActionResponse(action);

        if (actionResponse == null) {
            actionResponse = new ActionResponse();
            actionResponses.setActionResponse(action, actionResponse);
        }

        if (obligations != null) {
            actionResponse.getObligations().addAll(obligations);
        }

        if (policies != null) {
            actionResponse.getPolicies().addAll(policies);
        }
    }

    private static final Collection<String> EMPTY_STRING_COLLECTION = new ArrayList<>();
    private static final Collection<IObligationResultData> EMPTY_OBLIGATION_COLLECTION = new ArrayList<>();
    
    public Collection<String> getPermittedActionsForEffect(String effect) {
        ActionResponses actionResponses = actionResponsesByEffect.get(effect);

        if (actionResponses == null) {
            return EMPTY_STRING_COLLECTION;
        }
        
        return actionResponses.getActionResponses().keySet();
    }

    public Collection<IObligationResultData> getObligationsForAction(String effect, String action) {
        ActionResponses actionResponses = actionResponsesByEffect.get(effect);

        if (actionResponses == null) {
            return EMPTY_OBLIGATION_COLLECTION;
        }
        
        return actionResponses.getActionResponse(action).getObligations();
    }

    public Collection<String> getPoliciesForAction(String effect, String action) {
        ActionResponses actionResponses = actionResponsesByEffect.get(effect);

        if (actionResponses == null) {
            return EMPTY_STRING_COLLECTION;
        }
        
        return actionResponses.getActionResponse(action).getPolicies();
    }
}
