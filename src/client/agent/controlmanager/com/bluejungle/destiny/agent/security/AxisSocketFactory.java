/*
 * Created on Feb 4, 2005 All sources, binaries and HTML pages (C) copyright
 * 2004 by Blue Jungle Inc., Redwood City CA, Ownership remains with Blue Jungle
 * Inc, All rights reserved worldwide.
 */
package com.bluejungle.destiny.agent.security;

import com.bluejungle.destiny.agent.controlmanager.IAgentKeyManagerComponentBuilder;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.security.IKeyManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpHost;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * @author fuad
 *
 * Yet another socket factory. The main gimmick with this is that is
 * is reloadable. The agent will use a particular truststore for
 * registration, but then will switch to the keystore/truststore
 * returned by the registration request
 */

public class AxisSocketFactory implements ConnectionSocketFactory, ILogEnabled {
    public static final String NAME = AxisSocketFactory.class.getName();
    public static final ComponentInfo<AxisSocketFactory> COMP_INFO =
        new ComponentInfo<AxisSocketFactory>(
            AxisSocketFactory.NAME, 
            AxisSocketFactory.class,
            LifestyleType.SINGLETON_TYPE);

    private SSLConnectionSocketFactory socketFactory = null;
    private Log log = null;

    private static AxisSocketFactory instance = null;

    /**
     * Constructor
     *  
     */
    public AxisSocketFactory() {
        this.log = LogFactory.getLog(AxisSocketFactory.class.getName());

        try {
            setupSocketFactory();
        } catch (KeyManagementException e) {
            this.log.error("Unable to initialize Socket Factory. Connection to server could not be established.", e);
        } catch (NoSuchAlgorithmException e) {
            this.log.error("Unable to initialize Socket Factory. Connection to server could not be established.", e);
        }

        AxisSocketFactory.instance = this;
    }

    @Override
    public Socket connectSocket(int connectionTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
        return socketFactory.connectSocket(connectionTimeout, socket, host, remoteAddress, localAddress, context);
    }


    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        return socketFactory.createSocket(context);
    }

    /**
     * Calls key manager and trust manager to load the new keys.
     */
    public void reloadKeyStore() {
        try {
            setupSocketFactory(); // this is needed for the new keys to take effect.
        } catch (KeyManagementException e) {
            this.log.error("Unable to initialize Socket Factory. Connection to server could not be established.", e);
        } catch (NoSuchAlgorithmException e) {
            this.log.error("Unable to initialize Socket Factory. Connection to server could not be established.", e);
        }
    }

    /**
     * sets up the socket factory. 
     * 
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private void setupSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        IKeyManager keyManager = (IKeyManager) componentManager.getComponent(IKeyManager.COMPONENT_NAME);
        KeyManager[] kms = { keyManager.getCertificateKeyManager(IAgentKeyManagerComponentBuilder.KEYSTORE_NAME) };

        TrustManager trustManager = null;
        
        if (keyManager.containsKeystore(IAgentKeyManagerComponentBuilder.TRUSTSTORE_NAME)) {
            trustManager = keyManager.getCertificateTrustManager(IAgentKeyManagerComponentBuilder.TRUSTSTORE_NAME);
        } else {
            trustManager = new EmptyTrustManager();
        }
        
        //Place our own Trust Manager
        TrustManager[] tms = { trustManager };
        SSLContext context = null;
        context = SSLContext.getInstance("TLSv1.2");
        context.init(kms, tms, null);

        socketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
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
     * Returns the instance.
     * 
     * @return the instance.
     */
    public static AxisSocketFactory getInstance() {
        return AxisSocketFactory.instance;
    }
}
