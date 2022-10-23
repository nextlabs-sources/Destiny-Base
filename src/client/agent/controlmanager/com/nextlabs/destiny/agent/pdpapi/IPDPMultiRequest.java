package com.nextlabs.destiny.agent.pdpapi;

import java.util.List;

import com.bluejungle.destiny.agent.pdpapi.IPDPApplication;
import com.bluejungle.destiny.agent.pdpapi.IPDPHost;
import com.bluejungle.destiny.agent.pdpapi.IPDPNamedAttributes;
import com.bluejungle.destiny.agent.pdpapi.IPDPResource;
import com.bluejungle.destiny.agent.pdpapi.IPDPUser;
import com.bluejungle.destiny.agent.pdpapi.PDPException;

/*
 * Created on Jul 28, 2022
 *
 * All sources, binaries and HTML pages (C) copyright 2022 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

public interface IPDPMultiRequest {
    void addRequest(String action,
                    String userReference,
                    String applicationReference,
                    String hostReference,
                    String fromResourceReference,
                    String[] additionalDataReferences) throws PDPException;
    
    void addRequest(String action,
                    String userReference,
                    String applicationReference,
                    String hostReference,
                    String fromResourceReference,
                    String toResourceReference,
                    String[] additionalDataReferences) throws PDPException;

    List<IPDPReferenceRequest> getReferenceRequests();
    
    String addReference(IPDPNamedAttributes ref) throws PDPException;
    
    IPDPApplication getApplicationFromRequest(IPDPReferenceRequest referenceRequest) throws PDPException;
    IPDPHost getHostFromRequest(IPDPReferenceRequest referenceRequest) throws PDPException;
    
    IPDPResource[] getResourcesFromRequest(IPDPReferenceRequest referenceRequest) throws PDPException;
    
    IPDPUser getUserFromRequest(IPDPReferenceRequest referenceRequest) throws PDPException;
    
    IPDPNamedAttributes[] getAdditionalAttributesFromRequest(IPDPReferenceRequest referenceRequest) throws PDPException;
}
