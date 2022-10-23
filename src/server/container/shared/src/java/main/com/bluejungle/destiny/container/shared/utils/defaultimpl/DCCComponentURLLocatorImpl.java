/*
 * Created on May 2, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.utils.defaultimpl;

import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;

import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.container.shared.utils.IDCCComponentURLLocator;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.bluejungle.destiny.services.management.CommitFault;
import com.bluejungle.destiny.services.management.ComponentServiceStub;
import com.bluejungle.destiny.services.management.ServiceNotReadyFault;
import com.bluejungle.destiny.services.management.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.types.Component;
import com.bluejungle.destiny.services.management.types.ComponentList;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfiguration;
import com.nextlabs.axis.JSSESocketFactoryWrapper;

/**
 * @author safdar
 */

public class DCCComponentURLLocatorImpl implements IDCCComponentURLLocator {

    /*
     * Constants:
     */
    private static final String COMPONENT_SERVICE_LOCATION_SERVLET_PATH = "/services/ComponentService";

    /*
     * Private variables:
     */
    private Log logger;
    private IComponentManager manager;
    private ComponentServiceStub componentServiceStub;

    /**
     * @throws RemoteException 
     * @see com.bluejungle.destiny.container.shared.utils.IDCCComponentURLLocator#getComponentURLs(ServerComponentType)
     */
    public String[] getComponentURLs(ServerComponentType componentType) throws RemoteException {
        String[] urls = null;
        ComponentServiceStub componentService = null;
        try {
            componentService = getComponentServiceStub();
        } catch (IOException ioe) {
            throw new RemoteException("Error acquiring ComponentServiceStub", ioe);
        }
        
        ComponentList dccComponentList = null;
		try {
			dccComponentList = componentService.getComponentsByType(componentType.getName());
		} catch (CommitFault | UnauthorizedCallerFault | ServiceNotReadyFault e) {
			getLog().error("Exception happened",e);
		}
        if (dccComponentList != null) {
            Component[] dccComponentsArr = dccComponentList.getComp();
            if (dccComponentsArr != null) {
                urls = new String[dccComponentsArr.length];
                for (int i = 0; i < urls.length; i++) {
                    urls[i] = dccComponentsArr[i].getLoadBalancerURL();
                }
            }
        }
        return urls;
    }

    /**
     * Retrieve the Component Service stub.
     * 
     * @return the Component Service stub
     * @throws AxisFault 
     */
    private ComponentServiceStub getComponentServiceStub() throws IOException {
        if (componentServiceStub == null) {
            IComponentManager compMgr = this.getManager();
            IConfiguration mainConfig = (IConfiguration) compMgr.getComponent(IDCCContainer.MAIN_COMPONENT_CONFIG_COMP_NAME);
            String location = (String) mainConfig.get(IDCCContainer.DMS_LOCATION_CONFIG_PARAM);
            if (location == null) {
                // We may be in DMS - This is a bit of a hack, but I'm not sure
                // what else to do
                location = (String) mainConfig.get(IDCCContainer.COMPONENT_LOCATION_CONFIG_PARAM);
            }
            location += COMPONENT_SERVICE_LOCATION_SERVLET_PATH;

            componentServiceStub = new ComponentServiceStub(null, location);
            JSSESocketFactoryWrapper.attachToStub(componentServiceStub);
        }

        return componentServiceStub;
    }

    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#getLog()
     */
    public Log getLog() {
        return this.logger;
    }

    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#setLog(org.apache.commons.logging.Log)
     */
    public void setLog(Log log) {
        this.logger = log;
    }

    /**
     * @see com.bluejungle.framework.comp.IManagerEnabled#getManager()
     */
    public IComponentManager getManager() {
        return this.manager;
    }

    /**
     * @see com.bluejungle.framework.comp.IManagerEnabled#setManager(com.bluejungle.framework.comp.IComponentManager)
     */
    public void setManager(IComponentManager manager) {
        this.manager = manager;
    }
}
