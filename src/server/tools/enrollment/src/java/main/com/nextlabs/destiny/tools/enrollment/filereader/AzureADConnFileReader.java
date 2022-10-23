/*
 * Created on Jun 13, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.tools.enrollment.filereader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.bluejungle.destiny.tools.enrollment.filereader.BaseFileReader;
import com.bluejungle.destiny.tools.enrollment.filter.FileFormatException;
import com.bluejungle.destiny.tools.enrollment.util.ValidationHelper;
import com.bluejungle.framework.crypt.ReversibleEncryptor;

import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.impl.AzureADEnrollmentProperties;

public class AzureADConnFileReader extends BaseFileReader {
    public AzureADConnFileReader(File file, boolean promptForMissingData) throws FileNotFoundException, IOException, FileFormatException {
        super(file, promptForMissingData);
    }
    
    @Override
    protected String getFileDescription() {
        return "The Azure AD connection file";
    }

    @Override
    protected void validate(Properties properties) throws FileFormatException {
        ReversibleEncryptor encryptor = new ReversibleEncryptor();

        String applicationKey = properties.getProperty(AzureADEnrollmentProperties.APPLICATION_KEY);
        if (applicationKey == null || applicationKey.length() == 0) {
            applicationKey = getProtectedInput("Application key: ", "Application key is missing from configuration file");
        }
        
        applicationKey = encryptor.encrypt(applicationKey);
        properties.setProperty(AzureADEnrollmentProperties.APPLICATION_KEY, applicationKey);
        
        String syncInterval = properties.getProperty(AzureADEnrollmentProperties.PULL_INTERVAL);
        if (syncInterval != null) {
            try {
                long interval = ValidationHelper.validateSyncInterval(syncInterval);
                if (interval > 0) {
                    String startTime = properties.getProperty(AzureADEnrollmentProperties.START_TIME);
                    String timeFormat = properties.getProperty(AzureADEnrollmentProperties.TIME_FORMAT);
                    ValidationHelper.validateSyncStartTime(startTime,timeFormat);
                }
            } catch (IllegalArgumentException e) {
                throw new FileFormatException(e.getMessage());
            }
        }
    }
}

