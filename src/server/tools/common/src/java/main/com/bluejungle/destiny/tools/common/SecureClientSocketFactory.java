/*
 * Created on Mar 1, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.tools.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpHost;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author ihanen
 */
public abstract class SecureClientSocketFactory implements ConnectionSocketFactory {
    private static final String SSL_CONTEXT_NAME = "TLSv1.2";
    private static final TrustManager TRUST_ALL_MANAGER = new TrustAllManager();
    private static final String DEFAULT_KEYSTORE_NAME = "keystore.p12";

    private SSLConnectionSocketFactory socketFactory;
    private Log log = LogFactory.getLog(SecureClientSocketFactory.class.getName());

    @Override
    public Socket connectSocket(int connectionTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
        return getSocketFactory().connectSocket(connectionTimeout, socket, host, remoteAddress, localAddress, context);
    }
    
    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        return getSocketFactory().createSocket(context);
    }

    /**
     * Returns the keystore name
     * 
     * @return the keystore name
     */
    public String getKeystoreName() {
        return DEFAULT_KEYSTORE_NAME;
    }

    /**
     * Returns the log object
     * 
     * @return the log object
     */
    public Log getLog() {
        return this.log;
    }

    /**
     * @return the default password for the keystore
     */
    abstract public String getPassword();

    /**
     * Create a socket factory when needed
     */
    private synchronized ConnectionSocketFactory getSocketFactory() throws IOException {
        if (socketFactory == null) {
            setupSocketFactory();
        }

        return socketFactory;
    }
    
    /**
     * sets up the socket factory.
     * 
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private void setupSocketFactory() throws IOException {
        try {
            KeyManager[] kms = { new SecureKeyManager(SecureKeyManager.DEFAULT_ALGORITHM, SecureKeyManager.DEFAULT_FACTORY_ALGORITHM, getKeystoreName(), getPassword()) };
            
            //Place our own Trust Manager
            TrustManager[] tms = { TRUST_ALL_MANAGER };
            SSLContext context = null;
            context = SSLContext.getInstance(SSL_CONTEXT_NAME);
            context.init(kms, tms, null);

            socketFactory = new SSLConnectionSocketFactory(context);
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new IOException("Unable to initialize socket factory", e);
        }
    }
}
    
