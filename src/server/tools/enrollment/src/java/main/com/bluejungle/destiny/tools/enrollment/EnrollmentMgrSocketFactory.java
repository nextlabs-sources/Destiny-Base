/*
 * Created on Mar 1, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.tools.enrollment;

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

/**
 * @author ihanen
 */
public class EnrollmentMgrSocketFactory extends SecureClientSocketFactory {

    private static final String KEYSTORE_NAME = "/security/enrollment-keystore.p12";

    /**
     * Constructor
     * 
     * @param params
     */
    public EnrollmentMgrSocketFactory() {
        super();
    }

    /**
     * @see com.bluejungle.destiny.tools.common.SecureClientSocketFactory#getKeystoreName()
     */
    public String getKeystoreName() {
        String enrollToolHome = System.getProperty(EnrollmentMgr.ENROLLMENT_TOOL_HOME);
        return enrollToolHome + KEYSTORE_NAME;
    }

	/* (non-Javadoc)
	 * @see com.bluejungle.destiny.tools.common.SecureClientSocketFactory#getPassword()
	 */
	@Override
	public String getPassword() {
		return System.getProperty(EnrollmentMgr.ENROLL_TOOL_KEYSTORE_PASSWORD);
	}
    
    public static void attachToStub(Stub stub) throws IOException {
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
            RegistryBuilder.<ConnectionSocketFactory> create().register("https", new EnrollmentMgrSocketFactory()).build();

        BasicHttpClientConnectionManager connMgr = new BasicHttpClientConnectionManager(socketFactoryRegistry);

        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connMgr).build();
        
        stub._getServiceClient().getOptions().setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
    }
}
