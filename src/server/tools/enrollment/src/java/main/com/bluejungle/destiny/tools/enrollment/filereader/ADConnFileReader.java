/* 
 * Created on Mar 21, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
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

import com.bluejungle.destiny.tools.enrollment.filter.FileFormatException;
import com.bluejungle.destiny.tools.enrollment.util.ValidationHelper;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.ad.impl.ActiveDirectoryEnrollmentProperties;
import com.bluejungle.destiny.services.enrollment.types.EnrollmentProperty;
import com.bluejungle.framework.crypt.ReversibleEncryptor;

/**
 * @author safdar
 */

public class ADConnFileReader extends BaseFileReader{
    public ADConnFileReader(File file, boolean promptForMissingData) throws FileNotFoundException, IOException,
            FileFormatException {
        super(file, promptForMissingData);
    }

    @Override
    protected String getFileDescription() {
        return "The Active Directory connection file";
    }
    
    protected void validate(Properties properties) throws FileFormatException{
        getRequireString(properties, ActiveDirectoryEnrollmentProperties.SERVER);
        getRequireString(properties, ActiveDirectoryEnrollmentProperties.PORT);
        String loginUser = getRequireString(properties, ActiveDirectoryEnrollmentProperties.LOGIN);
        String password = properties.getProperty(ActiveDirectoryEnrollmentProperties.PASSWORD);
        String encryptedPassword = properties.getProperty(ActiveDirectoryEnrollmentProperties.ENCRYPTED_PASSWORD);
        String SecureTransportMode = properties.getProperty(ActiveDirectoryEnrollmentProperties.SECURE_TRANSPORT_MODE);
        if (SecureTransportMode != null) {
        	if (SecureTransportMode.compareToIgnoreCase("SSL")!=0) {
        		throw new FileFormatException("Optional property: " + ActiveDirectoryEnrollmentProperties.SECURE_TRANSPORT_MODE +
        				", must have a value of SSL.");
        	}
        }

        // if we have an encrypted password, use it
        if (encryptedPassword != null && encryptedPassword.length() > 0) {
            properties.setProperty(ActiveDirectoryEnrollmentProperties.PASSWORD, encryptedPassword);
        } else {
            // if the password is not stored in connection file, read it from command line prompt
            if ((password == null) || (password.length() == 0)) {
                password = getPasswordFromUser("LDAP user " + loginUser);
            }

            // encrypt password
            ReversibleEncryptor encryptor = new ReversibleEncryptor();
            password = encryptor.encrypt(password);
            properties.setProperty(ActiveDirectoryEnrollmentProperties.PASSWORD, password);
        }
        
        // comma separated DNs
        getRequireString(properties, ActiveDirectoryEnrollmentProperties.ROOTS);

        String syncInterval = properties.getProperty(ActiveDirectoryEnrollmentProperties.PULL_INTERVAL);
        if (syncInterval != null) {
            try {
                long interval = ValidationHelper.validateSyncInterval(syncInterval);
                if (interval > 0) {
                    String startTime = properties.getProperty(ActiveDirectoryEnrollmentProperties.START_TIME);
                    String timeFormat = properties.getProperty(ActiveDirectoryEnrollmentProperties.TIME_FORMAT);
                    ValidationHelper.validateSyncStartTime(startTime,timeFormat);
                }
            } catch (IllegalArgumentException e) {
                throw new FileFormatException(e.getMessage());
            }
        }
    }
    
    @Override
    protected List<EnrollmentProperty> convert(Properties properties) {
        List<EnrollmentProperty> propertyList = new ArrayList<EnrollmentProperty>(properties.size());
        for(Map.Entry<?,?> e : properties.entrySet()){
            String key = (String) e.getKey();
            String value = ((String) e.getValue()).trim();
            if (key.equals(ActiveDirectoryEnrollmentProperties.ROOTS)) {
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
