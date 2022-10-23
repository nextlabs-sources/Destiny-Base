/*
 * Created on Aug 11, 2016
 *
 * All sources, binaries and HTML pages (C) copyright 2016 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author sduan
 */
package com.nextlabs.openaz.pepapi;

import org.apache.openaz.xacml.api.Identifier;

import com.nextlabs.openaz.utils.Constants;

public class Application extends CategoryContainer {
	public static final Identifier CATEGORY_ID = Constants.ID_NEXTLABS_ATTRIBUTE_CATEGORY_APPLICATION;
    
    private String applicationID;
	
    private Application() {
        super(CATEGORY_ID);
    }
    
    public static Application newInstance() {
        return new Application();
    }
    
	public static Application newInstance(String applicationID) {
		Application a = new Application();
        a.setApplicationID(applicationID);

        return a;
	}

	public String getApplicationID() {
		return applicationID;
	}

	public void setApplicationID(String applicationID) {
		this.applicationID = applicationID;
	}
	
}
