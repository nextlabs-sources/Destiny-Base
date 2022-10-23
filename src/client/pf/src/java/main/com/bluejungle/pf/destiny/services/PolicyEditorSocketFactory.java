package com.bluejungle.pf.destiny.services;

/* All sources, binaries and HTML pages (C) Copyright 2005 by Blue Jungle Inc,
 * Redwood City, CA.
 * Ownership remains with Blue Jungle Inc. All rights reserved worldwide.
 *
 * @author sergey
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import com.bluejungle.framework.crypt.ReversibleEncryptor;


/**
 * This is a socket factory for the Policy Editor.
 *
 * Curently this is unused and the PolicyEditorClient is using the
 * JSSESocketFactoryWrapper, although this may be an
 * error. Investigate
 */
public class PolicyEditorSocketFactory extends SSLConnectionSocketFactory {
    private static final char[] KEYSTORE_PASSWORD = "password".toCharArray();
    private static final String ENCRYPTED_PASSWORD_FILE_PATH = "/security/config.dat";
	
	private static final String DEFAULT_ALGORITHM = "pkcs12";
    private static final String FACTORY_ALGORITHM = "SunX509";
    private static final String KEYSTORE_RESOURCE = "security/policyAuthor-keystore.p12";

    private static Log log = LogFactory.getLog(PolicyEditorSocketFactory.class.getName());

    public PolicyEditorSocketFactory() throws NoSuchAlgorithmException, KeyManagementException, IOException {
        super(createSSLContext());
    }
    
    /**
     * create SSLContext for this factory
     */
    private static SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyManagementException, IOException {
        KeyManager[] kms = { KEY_MANAGER };

        //Place our own Trust Manager
        TrustManager[] tms = { TRUST_MANAGER };
        SSLContext context = null;
        context = SSLContext.getInstance("TLSv1.2");
        context.init(kms, tms, null);

        return context;
    }
    
    /**
     * Reads password from file.
     * 
     * @return password
     */
    private static char[] getKeystorePassword() {
        return KEYSTORE_PASSWORD;
    }

    private static X509TrustManager TRUST_MANAGER = new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }
        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }
    };

    private static X509KeyManager KEY_MANAGER = new X509KeyManager() {
        private X509KeyManager realKeyManager = null;

        {
            KeyManagerFactory kmFact = null;
            try {
                //Get the key manager
                kmFact = KeyManagerFactory.getInstance(FACTORY_ALGORITHM);
                // Next, set up the KeyStore to use. We need to load the file into
                // a KeyStore instance.
                KeyStore ks = KeyStore.getInstance(DEFAULT_ALGORITHM);
            	InputStream input = getClass().getClassLoader().getResourceAsStream( KEYSTORE_RESOURCE );
            	
                char[] keyStorePassword = getKeystorePassword();
                try {
                    ks.load(input, keyStorePassword );
                } finally {
                    input.close();
                }
                kmFact.init(ks, keyStorePassword );
            } catch (KeyStoreException e2) {
                log.error("Key store cannot be loaded. Cannot connect to server.", e2);
            } catch (NoSuchAlgorithmException e3) {
                log.error("Key store cannot be loaded (No Such Algorithm). Cannot connect to server.", e3);
            } catch (CertificateException e3) {
                log.error("Key store cannot be loaded. Cannot connect to server.", e3);
            } catch (IOException e3) {
                log.error("Key store cannot be loaded. Cannot connect to server.", e3);
            } catch (UnrecoverableKeyException e) {
                log.error("Key store cannot be loaded. Cannot connect to server.", e);
            }

            if ( kmFact != null ) {
                // And now get the KeyManager
                KeyManager[] kms = kmFact.getKeyManagers();
                this.realKeyManager = (X509KeyManager) kms[0];
            }
        }
        /**
         * @see javax.net.ssl.X509KeyManager#getPrivateKey(java.lang.String)
         */
        @Override
        public synchronized PrivateKey getPrivateKey(String arg0) {
            return this.realKeyManager.getPrivateKey(arg0);
        }

        /**
         * @see javax.net.ssl.X509KeyManager#getCertificateChain(java.lang.String)
         */
        @Override
        public synchronized X509Certificate[] getCertificateChain(String arg0) {
            return this.realKeyManager.getCertificateChain(arg0);
        }

        /**
         * @see javax.net.ssl.X509KeyManager#getClientAliases(java.lang.String,
         *      java.security.Principal[])
         */
        @Override
        public synchronized String[] getClientAliases(String arg0, Principal[] arg1) {
            return this.realKeyManager.getClientAliases(arg0, arg1);
        }

        /**
         * @see javax.net.ssl.X509KeyManager#getServerAliases(java.lang.String,
         *      java.security.Principal[])
         */
        @Override
        public synchronized String[] getServerAliases(String arg0, Principal[] arg1) {
            return this.realKeyManager.getServerAliases(arg0, arg1);
        }

        /**
         * @see javax.net.ssl.X509KeyManager#chooseServerAlias(java.lang.String,
         *      java.security.Principal[], java.net.Socket)
         */
        @Override
        public synchronized String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
            return this.realKeyManager.chooseServerAlias(arg0, arg1, arg2);
        }

        /**
         * @see javax.net.ssl.X509KeyManager#chooseClientAlias(java.lang.String[],
         *      java.security.Principal[], java.net.Socket)
         */
        @Override
        public synchronized String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
            return this.realKeyManager.chooseClientAlias(arg0, arg1, arg2);
        }
    };

}
