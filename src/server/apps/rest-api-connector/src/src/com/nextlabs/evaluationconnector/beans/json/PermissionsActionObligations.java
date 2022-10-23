/*
 * Created on Aug 05, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.evaluationconnector.beans.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PermissionsActionObligations implements Serializable {
    private static final long serialVersionUID = 5189546463128517610L;
 
    private String Action;
    private List<String> MatchingPolicies;
    private List<ObligationOrAdvice> Obligations;

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public List<String> getMatchingPolicies() {
        return MatchingPolicies;
    }

    public void setMatchingPolicies(List<String> matchingPolicies) {
        this.MatchingPolicies = matchingPolicies;
    }
    
    public List<ObligationOrAdvice> getObligations() {
        return Obligations;
    }

    public void setObligations(List<ObligationOrAdvice> obligations) {
        Obligations = obligations;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
