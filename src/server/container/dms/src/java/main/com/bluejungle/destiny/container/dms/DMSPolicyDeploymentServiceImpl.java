/*
 * Created on Jan 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.dms;

import com.bluejungle.destiny.services.management.PolicyDeploymentServiceIF;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.types.DeploymentInfo;

/**
 * This is the policy deployment service implementation. This service allows the
 * policy server to notify DMS when policy updates have occured. Based on the
 * situation, new policy may simply be updated on the server side, or "pushed"
 * to individual agents on the client side.
 * 
 * @author ihanen
 */

public class DMSPolicyDeploymentServiceImpl implements PolicyDeploymentServiceIF {

    /**
     * Deploy new policies
     * 
     * @param deployInfo
     *            deployment information. This information mentions how the
     *            deployment should be made.
     * @throws ServiceNotReadyFault
     *             if the call to the service is made when the service is not
     *             ready.
     * @throws UnauthorizedCallerFault
     *             if the caller cannot call this service (this would already be
     *             handled before the caller reaches this API).
     */
    public void deploy(DeploymentInfo deployInfo) throws ServiceNotReadyFault, UnauthorizedCallerFault {
    }

}
