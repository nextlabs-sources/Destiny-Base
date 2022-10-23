package com.nextlabs.destiny.agent.pdpapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluejungle.destiny.agent.pdpapi.IPDPApplication;
import com.bluejungle.destiny.agent.pdpapi.IPDPHost;
import com.bluejungle.destiny.agent.pdpapi.IPDPNamedAttributes;
import com.bluejungle.destiny.agent.pdpapi.IPDPResource;
import com.bluejungle.destiny.agent.pdpapi.IPDPUser;
import com.bluejungle.destiny.agent.pdpapi.PDPException;

/*
 * Created on Jul 27, 2022
 *
 * All sources, binaries and HTML pages (C) copyright 2022 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

public class PDPMultiRequest implements IPDPMultiRequest {
    private List<IPDPReferenceRequest> referenceRequests;
    private Map<String, IPDPNamedAttributes> references;

    private int referencesCount = 0;

    public PDPMultiRequest() {
        referenceRequests = new ArrayList<IPDPReferenceRequest>();
        references = new HashMap<String, IPDPNamedAttributes>();
    }

    @Override
    public List<IPDPReferenceRequest> getReferenceRequests() {
        return referenceRequests;
    }
    
    @Override
    public void addRequest(String action,
                           String userReference,
                           String applicationReference,
                           String hostReference,
                           String fromResourceReference,
                           String[] additionalDataReferences) throws PDPException {
        addRequest(action, userReference, applicationReference, hostReference, fromResourceReference, null, additionalDataReferences);
    }

    @Override
    public void addRequest(String action,
                           String userReference,
                           String applicationReference,
                           String hostReference,
                           String fromResourceReference,
                           String toResourceReference,
                           String[] additionalDataReferences) throws PDPException {
        IPDPReferenceRequest req = new PDPReferenceRequest(action, userReference, applicationReference, hostReference, fromResourceReference, toResourceReference, additionalDataReferences);

        validateReferences(req);

        referenceRequests.add(req);
    }

    @Override
    public synchronized String addReference(IPDPNamedAttributes ref) throws PDPException {
        String refName = "ref" + (referencesCount++);

        references.put(refName, ref);

        return refName;
    }

    @Override
    public IPDPUser getUserFromRequest(IPDPReferenceRequest referenceRequest) throws PDPException {
        String userRef = referenceRequest.getUserReference();

        IPDPNamedAttributes attrs = references.get(userRef);

        if (attrs instanceof IPDPUser) {
            return (IPDPUser)attrs;
        }

        throw new PDPException("User reference " + userRef + " is not associated with a User object");
    }

    @Override
    public IPDPApplication getApplicationFromRequest(IPDPReferenceRequest referenceRequest) throws PDPException {
        String applicationRef = referenceRequest.getApplicationReference();

        IPDPNamedAttributes attrs = references.get(applicationRef);

        if (attrs instanceof IPDPApplication) {
            return (IPDPApplication)attrs;
        }

        throw new PDPException("Application reference " + applicationRef + " is not associated with an Application object");
    }
    
    @Override
    public IPDPHost getHostFromRequest(IPDPReferenceRequest referenceRequest) throws PDPException {
        String hostRef = referenceRequest.getHostReference();

        IPDPNamedAttributes attrs = references.get(hostRef);

        if (attrs instanceof IPDPHost) {
            return (IPDPHost)attrs;
        }

        throw new PDPException("Host reference " + hostRef + " is not associated with a Host object");
    }
    
    @Override
    public IPDPNamedAttributes[] getAdditionalAttributesFromRequest(IPDPReferenceRequest referenceRequest) throws PDPException {
        String[] additionalAttributeRefs = referenceRequest.getAdditionalDataReferences();

        if (additionalAttributeRefs == null) {
            return null;
        }
        IPDPNamedAttributes[] additionalAttributes = new IPDPNamedAttributes[additionalAttributeRefs.length];

        int i = 0;
        for (String attributeRef : additionalAttributeRefs) {
            additionalAttributes[i++] = references.get(attributeRef);
        }

        return additionalAttributes;
    }
    
    @Override
    public IPDPResource[] getResourcesFromRequest(IPDPReferenceRequest referenceRequest) throws PDPException {
        
        String fromResourceRef = referenceRequest.getFromResourceReference();
        String toResourceRef = referenceRequest.getToResourceReference();

        IPDPResource resources[] = new IPDPResource[toResourceRef == null ? 1 : 2];
        
        IPDPNamedAttributes from = references.get(fromResourceRef);

        if (from instanceof IPDPResource) {
            resources[0] = (IPDPResource)from;
        } else {
            throw new PDPException("Resource reference " + fromResourceRef + " is not associated with a Resource object");
        }

        if (toResourceRef != null) {
            IPDPNamedAttributes to = references.get(toResourceRef);
            
            if (to instanceof IPDPResource) {
                resources[1] = (IPDPResource)to;
            } else {
                throw new PDPException("Resource reference " + toResourceRef + " is not associated with a Resource object");
            }
        }

        return resources;
    }
    
    private void validateReferences(IPDPReferenceRequest req) throws PDPException {
        if (!referenceExists(req.getUserReference())) {
            throw new PDPException("User reference " + req.getUserReference() + " does not exist");
        }

        if (!referenceExists(req.getApplicationReference())) {
            throw new PDPException("Application reference " + req.getApplicationReference() + " does not exist");
        }
        
        if (!referenceExists(req.getHostReference())) {
            throw new PDPException("Host reference " + req.getHostReference() + " does not exist");
        }
        
        if (!referenceExists(req.getFromResourceReference())) {
            throw new PDPException("From resource reference " + req.getFromResourceReference() + " does not exist");
        }

        if (!referenceExists(req.getToResourceReference())) {
            throw new PDPException("To resource reference " + req.getToResourceReference() + " does not exist");
        }

        if (req.getAdditionalDataReferences() != null) {
            for (String ref : req.getAdditionalDataReferences()) {
                if (!referenceExists(ref)) {
                    throw new PDPException("Additional data reference " + ref + " does not exist");
                }
            }
        }
    }

    private boolean referenceExists(String referenceId) {
        return referenceId == null || references.containsKey(referenceId);
    }
}
