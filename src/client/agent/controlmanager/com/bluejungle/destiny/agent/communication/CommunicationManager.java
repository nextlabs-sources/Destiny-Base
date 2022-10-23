/*
 * Created on Dec 13, 2004 All sources, binaries and HTML pages (C) copyright
 * 2004 by Blue Jungle Inc., Redwood City CA, Ownership remains with Blue Jungle
 * Inc, All rights reserved worldwide.
 */
package com.bluejungle.destiny.agent.communication;

import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.transport.http.HTTPConstants;

import org.apache.commons.logging.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import com.bluejungle.destiny.agent.security.AxisSocketFactory;
import com.bluejungle.destiny.services.agent.AgentServiceIFPortStub;
import com.bluejungle.destiny.services.management.types.CommProfileDTO;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.comp.PropertyKey;

import com.nextlabs.destiny.interfaces.log.v5.LogServiceIFPortV5Stub;

/**
 * @author fuad
 */

public class CommunicationManager implements ILogEnabled, IInitializable, IConfigurable, IHasComponentInfo<CommunicationManager>, ICommunicationManager {
	public static final PropertyKey<CommProfileDTO> COMM_PROFILE_CONFIG_KEY = 
	        new PropertyKey<CommProfileDTO>("COMM_PROFILE_CONFIG_KEY");

    private Log log = null;
    private IConfiguration config = null;
    private URI dabsLocation = null;
    private boolean isPushEnabled = false;
    private int defaultPort = 0;
    private IPushListener pushListener = null;
    private CommProfileDTO currentCommProfile = null;
    
    String agentServicePortAddress;
    String logServicePortAddress;

    private static final ComponentInfo<CommunicationManager> COMP_INFO =
			new ComponentInfo<CommunicationManager>(
					NAME, 
					CommunicationManager.class,
					LifestyleType.SINGLETON_TYPE);
	public static final String AGENT_SERVICE_SUFFIX = "/services/AgentServiceIFPort";
    public static final String LOG_SERVICE_SUFFIX = "/services/LogServiceIFPort.v5";
    private static final String SOCKET_FACTORY_PROPERTY = "axis.socketSecureFactory";

    protected static final int AGENT_SERVICE_CONNECT_TIMEOUT = Integer.parseInt(System.getProperty("nextlabs.agentservice.connecttimeout", "120000"));
    protected static final int AGENT_SERVICE_READ_TIMEOUT = Integer.parseInt(System.getProperty("nextlabs.agentservice.readtimeout", "300000"));
    protected static final int LOG_SERVICE_CONNECT_TIMEOUT = Integer.parseInt(System.getProperty("nextlabs.logservice.connecttimeout", "120000"));
    protected static final int LOG_SERVICE_READ_TIMEOUT = Integer.parseInt(System.getProperty("nextlabs.logservice.readtimeout", "300000"));

    /**
     * The Communication Manager is a singleton. It will be instantiated when
     * the agent starts up. Components that need it can get it from the
     * component manager.
     * 
     * @see com.bluejungle.framework.comp.IHasComponentInfo#getComponentInfo()
     */
    public ComponentInfo<CommunicationManager> getComponentInfo() {
        return COMP_INFO;
    }

    /**
     * Initializes the service locators and gets the service interfaces from
     * them.
     * 
     * @see com.bluejungle.framework.comp.IInitializable#init()
     */
    public void init() {
        this.log.info("Communication Manager Initialized.");

        CommProfileDTO commProfile = getCommunicationProfile();
        this.dabsLocation = commProfile.getDABSLocation();

        this.setupWebServiceInterfaces();
        this.setupPushListener(commProfile);
    }

    /**
     * Reinitialize the Communication Manager. Reset service interfaces if DABS
     * location has changed. Reset push listener if configuration has changed.
     */
    public void reinit() {
        this.log.info("Communication Manager Reinitialized.");

        CommProfileDTO commProfile = getCommunicationProfile();
        if (!this.dabsLocation.equals(commProfile.getDABSLocation())) {
            this.dabsLocation = commProfile.getDABSLocation();
            setupWebServiceInterfaces();
        }

        boolean pushEnabled = commProfile.getPushEnabled();
        if (!pushEnabled) {
            if (this.isPushEnabled) {
                this.pushListener.stop();
                this.isPushEnabled = false;
            }
        } else if (this.isPushEnabled && this.defaultPort != commProfile.getDefaultPushPort().intValue()) {
            this.pushListener.stop();
            this.setupPushListener(commProfile);
        } else { //if pushInfo != null and !isPushEnabled
            this.setupPushListener(commProfile);
        }
    }

    /**
     * Gets the Communication Profile from the control module and returns it.
     * 
     * @return the communication profile.
     */
    private CommProfileDTO getCommunicationProfile() {
        return this.currentCommProfile;
    }

    /**
     * Sets up web service interfaces based on dabs location.
     */
    private void setupWebServiceInterfaces() {
        this.agentServicePortAddress = this.dabsLocation.toString() + AGENT_SERVICE_SUFFIX;
        this.logServicePortAddress = this.dabsLocation.toString() + LOG_SERVICE_SUFFIX;        
    }

    /**
     * Set up the push listener
     * 
     * @param commProfile
     *            communication profile
     */
    private void setupPushListener(CommProfileDTO commProfile) {
        this.isPushEnabled = commProfile.getPushEnabled();
        if (this.isPushEnabled) {
            this.defaultPort = commProfile.getDefaultPushPort().intValue();
            ComponentInfo<PushListener> pushListenerInfo = new ComponentInfo<PushListener>(PushListener.class, LifestyleType.SINGLETON_TYPE);
            this.pushListener = ComponentManagerFactory.getComponentManager().getComponent(pushListenerInfo, this.config);
        }
    }

    /**
     * @return port number that push listener is waiting on.
     */
    public int getPort() {
        if (this.isPushEnabled && this.pushListener != null) {
            return (this.pushListener.getPort());
        } else {
            return -1;
        }
    }

    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#setLog(org.apache.commons.logging.Log)
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#getLog()
     */
    public Log getLog() {
        return this.log;
    }

    /**
     * Returns the AgentServiceStub.
     * 
     * @return the AgentServiceIF.
     * @throws ServiceException
     */
    public AgentServiceIFPortStub getAgentServiceStub() throws AxisFault {
    	AgentServiceIFPortStub stub = new AgentServiceIFPortStub(null, agentServicePortAddress);
        stub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, AGENT_SERVICE_CONNECT_TIMEOUT);
        stub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, AGENT_SERVICE_READ_TIMEOUT);

        Registry<ConnectionSocketFactory> socketFactoryRegistry =
            RegistryBuilder.<ConnectionSocketFactory> create().register("https", new AxisSocketFactory()).build();

        BasicHttpClientConnectionManager connMgr = new BasicHttpClientConnectionManager(socketFactoryRegistry);

        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connMgr).build();
        
        stub._getServiceClient().getOptions().setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
        return stub;
    }

    /**
     * Returns the LogServiceIFPortV5Stub.
     * 
     * @return the LogServiceIFPortV5Stub.
     * @throws ServiceException
     */
    public LogServiceIFPortV5Stub getLogServiceStub() throws AxisFault {
        LogServiceIFPortV5Stub stub = new LogServiceIFPortV5Stub(null, logServicePortAddress);
        stub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, LOG_SERVICE_CONNECT_TIMEOUT);
        stub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, LOG_SERVICE_READ_TIMEOUT);
        
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
            RegistryBuilder.<ConnectionSocketFactory> create().register("https", new AxisSocketFactory()).build();

        BasicHttpClientConnectionManager connMgr = new BasicHttpClientConnectionManager(socketFactoryRegistry);

        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connMgr).build();
        
        stub._getServiceClient().getOptions().setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
        return stub;
    }

    /**
     * @see com.bluejungle.framework.comp.IConfigurable#setConfiguration(com.bluejungle.framework.comp.IConfiguration)
     */
    public void setConfiguration(IConfiguration config) {
        this.config = config;
        this.currentCommProfile = config.get(COMM_PROFILE_CONFIG_KEY);

        if (this.currentCommProfile == null) {
            // the currentCommProfile is required, otherwise it will fail on init or reinit
            throw new NullPointerException("The communication profile was not provided");
        }
    }

    /**
     * @see com.bluejungle.framework.comp.IConfigurable#getConfiguration()
     */
    public IConfiguration getConfiguration() {
        return this.config;
    }
}
