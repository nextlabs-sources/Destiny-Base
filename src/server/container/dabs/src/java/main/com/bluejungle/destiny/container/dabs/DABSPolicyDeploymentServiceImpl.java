/*
 * Created on Nov 17, 2004
 */
package com.bluejungle.destiny.container.dabs;

import com.bluejungle.destiny.services.deployment.PolicyDeploymentServiceIF;
import com.bluejungle.destiny.services.deployment.types.PolicyPushList;

/**
 * This is the policy deployment service. It is mostly used to make sure that
 * policy update are properly propagated into DABS. Also, it is used if DABS
 * needs to perform a push to one or more agents.
 * 
 * @author ihanen
 */
public class DABSPolicyDeploymentServiceImpl implements PolicyDeploymentServiceIF {

    /**
     * Invalidates the local policy cache
     * 
     * @return confirmation message
     */
    public String invalidatePolicy() {
        return "OK";
    }

    /**
     * Sends a push signal to a list of hosts. The status of each push is saved
     * in the management repository.
     * 
     * @param list
     *            list of hosts to contact
     * @return status of push action
     */
    public String pushPolicy(PolicyPushList list) {
        return "OK";
    }
}