/*
 * Created on Jan 23, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.ind.impl.sharepoint;

import com.bluejungle.ind.INDException;
import com.microsoft.schemas.sharepoint.soap.GetListCollection;
import com.microsoft.schemas.sharepoint.soap.GetListCollectionResponse;
import com.microsoft.schemas.sharepoint.soap.SiteDataStub;
import com.microsoft.schemas.sharepoint.soap.WebsStub;

/**
 * @author atian
 */

public class SharePointDataSourceConnectionTester {
    private static final String SITEDATA_ENDPOINT = "/_vti_bin/SiteData.asmx";
    private static final String GETWEB_ENDPOINT = "/_vti_bin/Webs.asmx";
    
    public static void testConnection(String portalURL, String login, String password, String domain) throws INDException {
    	
    	SiteDataStub binding = getSiteDataSoap12Stub(portalURL, login, password, domain);
            
            try {
            	// Test operation
            	GetListCollectionResponse value = null;
            	value = binding.getListCollection(new GetListCollection());
            	value.getVLists();  
            }
            catch (Exception jre) {
                throw new INDException("Failed to list collection"+ jre);
            }
        
    }
    
    public static SiteDataStub getSiteDataSoap12Stub(String portalURL, String login, String password, String domain) throws INDException {
        
            String endpointAddress = portalURL + SITEDATA_ENDPOINT;
            
            SiteDataStub locator = null;
            try {
            	locator = new SiteDataStub(endpointAddress);
            	// TODO set username and password
            	//locator.setUsername(domain + "\\" + login);
                String passwd = password;
               // locator.setPassword(passwd);
            }
            catch (Exception jre) {
                throw new INDException("Failed to conntect sharepoint server " + endpointAddress, jre);
            }
            //TODO
            // Time out after 5 minutes
            //locator.setTimeout(300000);
            return locator;
        
    }
    
    public static WebsStub getWebsSoap12Stub(String portalURL, 
            String login, String password, String domain) throws INDException {
        
        try {
        	String endpointAddress = portalURL + GETWEB_ENDPOINT;
        	WebsStub stub = new WebsStub(endpointAddress);
            try {
                //TODO pass in username and password
                //binding.setUsername(domain + "\\" + login);
                String passwd = password;
                //binding.setPassword(passwd);
            }
            catch (Exception jre) {
                throw new INDException("Failed to conntect sharepoint server " + endpointAddress, jre);
            }
            
            //TODO Time out after 5 minutes
            //stub.setTimeout(300000);
            return stub;
        }
        catch ( Exception e ) {
            throw new INDException("Failed to conntect sharepoint server", e);
        }
        
    }
    
}
