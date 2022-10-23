/*
 * Created on Jan 18, 2005 All sources, binaries and HTML pages (C) copyright
 * 2004 by Blue Jungle Inc., Redwood City CA, Ownership remains with Blue Jungle
 * Inc, All rights reserved worldwide.
 */
package com.bluejungle.destiny.agent.communication;

import org.apache.axis2.AxisFault;

import com.bluejungle.destiny.services.agent.AgentServiceIFPortStub;
import com.nextlabs.destiny.interfaces.log.v5.LogServiceIFPortV5Stub;

/**
 * @author fuad
 */

public interface ICommunicationManager {

    public static final String NAME = "Agent Communication Manager";

    /**
     * Reinitialize the Communication Manager. Reset service interfaces if DABS
     * location has changed. Reset push listener if configuration has changed.
     */
    public abstract void reinit();

    /**
     * @return port number that push listener is waiting on.
     */
    public abstract int getPort();

    /**
     * Returns the agentServiceStub.
     * 
     * @return the agentServiceStub.
     */
    public abstract AgentServiceIFPortStub getAgentServiceStub() throws AxisFault;

    /**
     * Returns the logServiceStub.
     * 
     * @return the logServiceStub.
     */
    public abstract LogServiceIFPortV5Stub getLogServiceStub() throws AxisFault;
}
