/*
 * Created on Sep 24, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */
package com.nextlabs.destiny.tools.keymanagement;

import java.io.IOException;

import org.apache.axis2.client.Stub;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import com.bluejungle.destiny.tools.common.SecureClientSocketFactory;

public class KeyManagementMgrSocketFactory extends SecureClientSocketFactory {

    private static final String KEYSTORE_NAME = "/security/keymanagement-keystore.p12";

    /**
     * Constructor
     * 
     * @param params
     */
    public KeyManagementMgrSocketFactory() {
        super();
    }

    /**
     * @see com.bluejungle.destiny.tools.common.SecureClientSocketFactory#getKeystoreName()
     */
    public String getKeystoreName() {
        String toolHome = System.getProperty(KeyManagementMgr.KM_TOOL_HOME, "");
        return toolHome + KEYSTORE_NAME;
    }
    
	/* (non-Javadoc)
	 * @see com.bluejungle.destiny.tools.common.SecureClientSocketFactory#getPassword()
	 */
	@Override
	public String getPassword() {
		return System.getProperty(KeyManagementMgr.KM_TOOL_KEYSTORE_PASSWORD);
	}
    
    public static void attachToStub(Stub stub) throws IOException {
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
            RegistryBuilder.<ConnectionSocketFactory> create().register("https", new KeyManagementMgrSocketFactory()).build();

        BasicHttpClientConnectionManager connMgr = new BasicHttpClientConnectionManager(socketFactoryRegistry);

        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connMgr).build();
        
        stub._getServiceClient().getOptions().setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
    }
}

