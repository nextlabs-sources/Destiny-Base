/*
 * Created on Jul 20, 2012
 *
 * All sources, binaries and HTML pages (C) copyright 2012 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.dpc;

import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;

import com.bluejungle.destiny.container.dcc.DCCContextListener;
import com.bluejungle.destiny.container.dcc.IDCCContainer;
import com.bluejungle.destiny.server.shared.registration.ServerComponentType;

public class DPCContextListener extends DCCContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        String rootDirectory = event.getServletContext().getInitParameter("RootDirectory");
        if (rootDirectory != null) {
            File log4jConfigurationFile = Paths.get(rootDirectory, "config", "log4j2.xml").toFile();
            if (log4jConfigurationFile.exists()) {
                try {
                    System.setProperty("nextlabs.dpc.hostname", InetAddress.getLocalHost().getHostName());
                } catch (UnknownHostException uhe) {
                    System.setProperty("nextlabs.dpc.hostname", "localhost");
                }
                try (FileInputStream log4jConfigurationFileInputStream = new FileInputStream(log4jConfigurationFile)) {
                    LoggerContext context = (LoggerContext) LogManager.getContext(false);
                    Configuration configuration = ConfigurationFactory.getInstance()
                            .getConfiguration(context, new ConfigurationSource(log4jConfigurationFileInputStream));
                    context.start(configuration);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.contextInitialized(event);
    }

    @Override
    public ServerComponentType getComponentType() {
        return DPCComponentImpl.COMPONENT_TYPE;
    }

    @Override
    protected Class<? extends IDCCContainer> getContainerClassName() {
        return DPCContainerImpl.class;
    }

    @Override
    public String getTypeDisplayName() {
        return "Policy Controller";
    }
}
