package com.nextlabs.pf.engine.destiny;

import java.util.HashSet;
import java.util.Set;

import com.bluejungle.pf.domain.destiny.misc.IDEffectType;
import com.bluejungle.pf.domain.destiny.obligation.IDObligation;
import com.nextlabs.domain.log.PolicyActivityInfoV5;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;

/*
 * Created on Jan 17, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

public class XACMLEvaluationResult {
    private IWrappedXACMLResponse wrappedXACMLResponse;
    private String effectName = IDEffectType.ALLOW_NAME;
    private Set<IDObligation> obligations = new HashSet<>();
    private PolicyActivityInfoV5 policyInfo;

    public XACMLEvaluationResult(IWrappedXACMLResponse xacmlResponse) {
        this.wrappedXACMLResponse = xacmlResponse;
    }
    
    public IWrappedXACMLResponse getXACMLResponse() {
        return wrappedXACMLResponse;
    }
    
    public void setWrappedXACMLResponse(IWrappedXACMLResponse wrappedXACMLResponse) {
        this.wrappedXACMLResponse = wrappedXACMLResponse;
    }

    public String getEffectName() {
        return effectName;
    }
    
    public void setEffectName(String effectName) {
        this.effectName = effectName;
    }

    public void addObligation(IDObligation obligation) {
        obligations.add(obligation);
    }

    public Set<IDObligation> getObligations() {
        return obligations;
    }
    
    public void setPolicyActivityInfo(PolicyActivityInfoV5 policyInfo) {
        this.policyInfo = policyInfo;
    }

    public PolicyActivityInfoV5 getPolicyActivityInfo() {
        return policyInfo;
    }
}
