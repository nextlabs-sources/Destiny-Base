package com.nextlabs.destiny.agent.pdpapi;

/*
 * Created on Jul 27, 2022
 *
 * All sources, binaries and HTML pages (C) copyright 2022 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

public class PDPReferenceRequest implements IPDPReferenceRequest {
    private String action;
    private String userReference;
    private String applicationReference;
    private String hostReference;
    private String fromResourceReference;
    private String toResourceReference;
    private String[] additionalDataReferences;

    public PDPReferenceRequest(String action,
                               String userReference,
                               String applicationReference,
                               String hostReference,
                               String fromResourceReference,
                               String[] additionalDataReferences) {
        this(action, userReference, applicationReference, hostReference, fromResourceReference, null, additionalDataReferences);
    }
        
    public PDPReferenceRequest(String action,
                               String userReference,
                               String applicationReference,
                               String hostReference,
                               String fromResourceReference,
                               String toResourceReferenc,
                               String[] additionalDataReferences) {
        
        this.action = action;
        this.userReference = userReference;
        this.applicationReference = applicationReference;
        this.hostReference = hostReference;
        this.fromResourceReference = fromResourceReference;
        this.toResourceReference = toResourceReference;
        this.additionalDataReferences = additionalDataReferences;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public String getUserReference() {
        return userReference;
    }

    @Override
    public String getApplicationReference() {
        return applicationReference;
    }

    @Override
    public String getHostReference() {
        return hostReference;
    }

    @Override
    public String getResourceReference() {
        return fromResourceReference;
    }

    @Override    
    public String getFromResourceReference() {
        return fromResourceReference;
    }

    @Override
    public String getToResourceReference() {
        return toResourceReference;
    }

    @Override
    public String[] getAdditionalDataReferences() {
        return additionalDataReferences;
    }
}
