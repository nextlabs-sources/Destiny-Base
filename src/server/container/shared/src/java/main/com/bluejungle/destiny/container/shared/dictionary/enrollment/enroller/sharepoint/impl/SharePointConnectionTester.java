/*
 * Created on Jan 19, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.sharepoint.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.impl.httpclient4.HttpTransportPropertiesImpl;
import org.apache.commons.httpclient.auth.AuthPolicy;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentValidationException;
import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.microsoft.schemas.sharepoint.soap.SiteDataStub;
import com.microsoft.schemas.sharepoint.soap.GetListCollectionResponse;
import com.microsoft.schemas.sharepoint.soap.GetListCollection;

/**
 * @author atian
 */

public class SharePointConnectionTester {
	
    // password decoder
    private static final ReversibleEncryptor CIPHER = new ReversibleEncryptor();
    private static final String SITEDATA_ENDPOINT = "/_vti_bin/SiteData.asmx";
    
    public static void testConnection(String portalUrl, String login, String password, String domain)
			throws EnrollmentValidationException {
        
        
        try {
        	SiteDataStub binding = getSiteDataSoap12Stub(portalUrl, login, password, domain);
			// Test operation
			GetListCollectionResponse value = binding.getListCollection(new GetListCollection());
			value.getVLists();
		} catch (Exception e) {
			throw new EnrollmentValidationException("Connection to Sharepoint server failed." + portalUrl, e);
		}
        
    }
    
    public static SiteDataStub getSiteDataSoap12Stub(String portalURL, String login, String password, String domain) throws EnrollmentValidationException {

    	String endpointAddress = fixHttpsPort(portalURL) + SITEDATA_ENDPOINT;
    	SiteDataStub stub = null;
        
        try {
        	stub = new SiteDataStub(endpointAddress);
        	
        	String passwd = CIPHER.decrypt(password);
        	
        	// register the auth scheme
        	AuthPolicy.registerAuthScheme(AuthPolicy.BASIC, JCIFSNTLMScheme.class);
        	
            HttpTransportPropertiesImpl.Authenticator authenticator = new HttpTransportPropertiesImpl.Authenticator();
            List<String> authScheme = new ArrayList<String>();
            authScheme.add(HttpTransportPropertiesImpl.Authenticator.NTLM);
            authScheme.add(HttpTransportPropertiesImpl.Authenticator.BASIC);
            authenticator.setAuthSchemes(authScheme);
            authenticator.setUsername(login);
            authenticator.setPassword(passwd);
            authenticator.setHost(getDomainName(portalURL));
            authenticator.setDomain(domain);       
            authenticator.setPreemptiveAuthentication(true);
       
            
            stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);
            stub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, Boolean.FALSE);
            stub._getServiceClient().getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
            
            //This is for basic authentication
            stub._getServiceClient().getOptions().setUserName(domain + "\\" + login);
            stub._getServiceClient().getOptions().setPassword(passwd);
            
            // Time out after 5 minutes
            stub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, 600000);
            return stub;
        }
        catch (Exception jre) {
            throw new EnrollmentValidationException("Failed to conntect sharepoint server " + endpointAddress, jre);
        }
        
    }
    
    private static String fixHttpsPort(String urlStr){
    	//    	bug fix 5549
		//set the https default port to 443
		try {
			URL url = new URL(urlStr);
			if (url.getProtocol().equalsIgnoreCase("https") && url.getPort() == -1) {
				url = new URL(url.getProtocol(), url.getHost(), 443, url.getFile());
				return url.toExternalForm();
			}else{
				return urlStr;
			}
		} catch (MalformedURLException e) {
			return urlStr;
		}
    }
    
    public static SiteDataStub getSiteDataSoapStub(String portalURL, String login, String password, 
            String domain) throws EnrollmentValidationException {
    	
        try {
        	String endpointAddress = fixHttpsPort(portalURL) + SITEDATA_ENDPOINT;
        	SiteDataStub stub = null;
            try {
            	stub = new SiteDataStub(endpointAddress);
            	
            	String passwd = CIPHER.decrypt(password);
            	
            	// register the auth scheme
            	AuthPolicy.registerAuthScheme(AuthPolicy.BASIC, JCIFSNTLMScheme.class);
            	
                HttpTransportPropertiesImpl.Authenticator authenticator = new HttpTransportPropertiesImpl.Authenticator();
                List<String> authScheme = new ArrayList<String>();
                authScheme.add(HttpTransportPropertiesImpl.Authenticator.NTLM);
                authScheme.add(HttpTransportPropertiesImpl.Authenticator.BASIC);
                authenticator.setAuthSchemes(authScheme);
                authenticator.setUsername(login);
                authenticator.setPassword(passwd);
                authenticator.setHost(getDomainName(portalURL));
                authenticator.setDomain(domain);
                authenticator.setPreemptiveAuthentication(true);
           
                
                stub._getServiceClient().getOptions().setProperty(HTTPConstants.AUTHENTICATE, authenticator);
                stub._getServiceClient().getOptions().setProperty(HTTPConstants.CHUNKED, Boolean.FALSE);
                stub._getServiceClient().getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.TRUE);
                
                stub._getServiceClient().getOptions().setUserName(domain + "\\" + login);
                stub._getServiceClient().getOptions().setPassword(passwd);
                
                // Time out after 5 minutes
                stub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, 600000);
                return stub;
            }
            catch (Exception jre) {
                throw new EnrollmentValidationException("Failed to conntect sharepoint server " + endpointAddress, jre);
            }
        }
        catch ( Exception e ) {
            throw new EnrollmentValidationException("Failed to conntect sharepoint server", e);
        }
    }
    
    public static String getDomainName(String url) throws MalformedURLException{
	    if(!url.startsWith("http") && !url.startsWith("https")){
	         url = "http://" + url;
	    }        
	    URL netUrl = new URL(url);
	    String host = netUrl.getHost();
	    if(host.startsWith("www")){
	        host = host.substring("www".length()+1);
	    }
	    return host;
	}
}

