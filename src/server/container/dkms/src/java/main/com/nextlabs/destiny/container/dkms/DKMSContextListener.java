/*
 * Created on Jul 02, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.dkms;

import javax.servlet.ServletContextEvent;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.dcc.DCCContextListener;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;
import com.nextlabs.destiny.configclient.ConfigClient;
import com.nextlabs.framework.utils.PasswordHashUtils;

public class DKMSContextListener extends DCCContextListener {
    private final Log LOG = LogFactory.getLog(DKMSContextListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            ConfigClient.init("dkms");
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
   
    @Override
    public ServerComponentType getComponentType() {
        return DKMSConstants.COMPONENT_TYPE;
    }

    @Override
    public String getTypeDisplayName() {
        return "Key Management Server";
    }

}
