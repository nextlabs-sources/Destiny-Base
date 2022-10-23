package com.bluejungle.destiny.container.dps;

/*
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */

import javax.servlet.ServletContextEvent;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.dcc.DCCContextListener;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.nextlabs.destiny.configclient.ConfigClient;
import com.nextlabs.framework.utils.PasswordHashUtils;

/**
 * This is the DPS context listener class.
 * 
 * @author sergey
 */

public class DPSContextListener extends DCCContextListener {
    private final Log LOG = LogFactory.getLog(DPSContextListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            ConfigClient.init("dps");
        } catch (IOException ioe) {
            LOG.error("Error when loading system configuration", ioe);
            throw new RuntimeException(ioe);
        }
            
        PasswordHashUtils.getInstance(ConfigClient.get(PasswordHashUtils.NUM_ITERATIONS).toInt(),
                                      ConfigClient.get(PasswordHashUtils.SECRET_KEY).toString(),
                                      ConfigClient.get(PasswordHashUtils.SALT_WIDTH).toInt(),
                                      ConfigClient.get(PasswordHashUtils.HASH_WIDTH).toInt());
        
        super.contextInitialized(servletContextEvent);
    }

    /**
     * @see com.bluejungle.destiny.container.dcc.DCCContextListener#getComponentType()
     */
    public ServerComponentType getComponentType() {
        return ServerComponentType.DPS;
    }

    @Override
    public String getTypeDisplayName() {
        return "Policy Management Server";
    }
}
