/*
 * Created on May 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.utils;

import java.rmi.RemoteException;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.IManagerEnabled;

/**
 * Interface to locate urls of DCC components
 * 
 * @author safdar
 */

public interface IDCCComponentURLLocator extends IManagerEnabled, ILogEnabled {

    /*
     * Component name:
     */
    public static final String COMP_NAME = "DCCComponentURLLocator";

    /**
     * Returns an array of URLs for all DCC components of a given type
     * 
     * @param componentType
     * @return array of urls
     */
    public String[] getComponentURLs(ServerComponentType componentType) throws RemoteException;
}
