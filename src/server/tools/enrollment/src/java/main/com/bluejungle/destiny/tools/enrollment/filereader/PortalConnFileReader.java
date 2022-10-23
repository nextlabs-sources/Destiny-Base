/*
 * Created on Feb 14, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.tools.enrollment.filereader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.sharepoint.impl.SharePointEnrollmentProperties;
import com.bluejungle.destiny.services.enrollment.types.EnrollmentProperty;
import com.bluejungle.destiny.tools.enrollment.filter.FileFormatException;
import com.bluejungle.destiny.tools.enrollment.util.ValidationHelper;
import com.bluejungle.framework.crypt.ReversibleEncryptor;

/**
 * @author atian
 */

public class PortalConnFileReader extends BaseFileReader{
    public PortalConnFileReader(File file, boolean noprompt) throws FileNotFoundException, IOException,
            FileFormatException {
        super(file, noprompt);
    }
    
    @Override
    protected String getFileDescription() {
        return "The portal connection file";
    }

    @Override
    protected void validate(Properties properties) throws FileFormatException {
        String loginUser = getRequireString(properties, SharePointEnrollmentProperties.LOGIN_PROPERTY);
        String password = properties.getProperty(SharePointEnrollmentProperties.PASSWORD_PROPERTY);
        
        // if the password is not stored in connection file, read it from command line prompt
        if ((password == null) || (password.length() == 0)) {
            password = getPasswordFromUser("portal user " + loginUser);
        }
        
        // encrypt password
        ReversibleEncryptor encryptor = new ReversibleEncryptor();
        password = encryptor.encrypt(password);
        properties.setProperty(SharePointEnrollmentProperties.PASSWORD_PROPERTY, password);
        
        // comma seperated DNs
        properties.getProperty(SharePointEnrollmentProperties.PORTALS_PROPERTY);
        
        String syncInterval = properties.getProperty(SharePointEnrollmentProperties.PULL_INTERVAL);
        if (syncInterval != null) {
            try {
                long interval = ValidationHelper.validateSyncInterval(syncInterval);
                if (interval > 0) {
                    String startTime = properties.getProperty(SharePointEnrollmentProperties.START_TIME);
                    String timeFormat = properties.getProperty(SharePointEnrollmentProperties.TIME_FORMAT);
                    ValidationHelper.validateSyncStartTime(startTime,timeFormat);
                }
            } catch (IllegalArgumentException e) {
                throw new FileFormatException(e.getMessage());
            }
        }
    }
    
    @Override
    protected List<EnrollmentProperty> convert(Properties properties) {
        List<EnrollmentProperty> propertyList =
                new ArrayList<EnrollmentProperty>(properties.size());
        for (Map.Entry<?, ?> e : properties.entrySet()) {
            String key = (String) e.getKey();
            String value = ((String) e.getValue()).trim();
            if (key.equals(SharePointEnrollmentProperties.PORTALS_PROPERTY)) {
                String[] roots = value.split("\n");
                EnrollmentProperty enrollP = new EnrollmentProperty();
                enrollP.setKey(key.toLowerCase());
                enrollP.setValue(roots);
                propertyList.add(enrollP);
            } else {
            	EnrollmentProperty enrollP = new EnrollmentProperty();
                enrollP.setKey(key.toLowerCase());
                enrollP.setValue(new String[] {value});
                propertyList.add(enrollP);
            }
        }
        return propertyList;
    }
}
