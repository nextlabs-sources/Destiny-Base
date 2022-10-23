/*
 * Created on Apr 12, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.axis;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.axis2.client.Stub;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

/**
 * Wrap the Axis2 SSLConnectionSocketFactory so we can supply our own keystore/truststore
 * 
 * @author hchan
 */

public class JSSESocketFactoryWrapper extends SSLConnectionSocketFactory {
    public JSSESocketFactoryWrapper() throws IOException {
        super(createContext(), NoopHostnameVerifier.INSTANCE);
    }

    private static SSLContext createContext() throws IOException {
        try {
            // Open the key store
            String keyStoreFileName = System.getProperty("nextlabs.javax.net.ssl.keyStore");
            char[] keyStorePassword = System.getProperty("nextlabs.javax.net.ssl.keyStorePassword").toCharArray();
            String keyAlias = System.getProperty("nextlabs.javax.net.ssl.keyAlias", "dcc");

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream keyStoreFile = new FileInputStream(keyStoreFileName);
            keyStore.load(keyStoreFile, keyStorePassword);
            keyStoreFile.close();

            // There are multiple keys in the keystore, but we want axis to use "dcc". The "keyAlias" attribute in
            // Connector in the server.xml file controls the Tomcat JSSESocketFactory, but not the Axis one, so we
            // have to ensure that we extract the right key
            KeyStore dccKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            dccKeyStore.load(null, keyStorePassword);
            Key dccKey = keyStore.getKey(keyAlias, keyStorePassword);
            Certificate[] certChain = keyStore.getCertificateChain(keyAlias);
            dccKeyStore.setKeyEntry(keyAlias, dccKey, keyStorePassword, certChain);
            
            // Create the key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(dccKeyStore, keyStorePassword);
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
        
            // Open the trust store
            String trustStoreFileName = System.getProperty("nextlabs.javax.net.ssl.trustStore");
            char[] trustStorePassword = System.getProperty("nextlabs.javax.net.ssl.trustStorePassword").toCharArray();
            
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream trustStoreFile = new FileInputStream(trustStoreFileName);
            trustStore.load(trustStoreFile, trustStorePassword);
            trustStoreFile.close();

            // Create the trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(keyManagers, trustManagers, new SecureRandom());

            return sslContext;
        } catch (KeyStoreException e) {
            throw new IOException(e);
        } catch (KeyManagementException e) {
            throw new IOException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } catch (UnrecoverableKeyException e) {
            throw new IOException(e);
        } catch (CertificateException e) {
            throw new IOException(e);
        }
    }

    public static void attachToStub(Stub stub) throws IOException {
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
            RegistryBuilder.<ConnectionSocketFactory> create().register("https", new JSSESocketFactoryWrapper()).build();

        BasicHttpClientConnectionManager connMgr = new BasicHttpClientConnectionManager(socketFactoryRegistry);

        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connMgr).build();
        
        stub._getServiceClient().getOptions().setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
    }
}
