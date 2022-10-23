/*
 * Created on Jun 01, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.impl;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentValidationException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.common.BaseLDAPEnrollmentWrapper;
import com.bluejungle.dictionary.DictionaryException;
import com.bluejungle.dictionary.IDictionary;
import com.bluejungle.dictionary.IEnrollment;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.IAzureADEnrollmentWrapper;

public class AzureADEnrollmentWrapperImpl extends BaseLDAPEnrollmentWrapper implements IAzureADEnrollmentWrapper {
    private static final String OAUTH_AUTHORITY = "https://login.microsoftonline.com/";
    private String tenant;
    private String applicationKey;
    private String applicationId;
    private String oauthAuthority;
    private String userExtendedAttributesQuery;
    
    public AzureADEnrollmentWrapperImpl(IEnrollment enrollment, IDictionary dictionary) throws
        EnrollmentValidationException, DictionaryException {
        super(enrollment, dictionary);

        oauthAuthority = enrollment.getStrProperty(AzureADEnrollmentProperties.OAUTH_AUTHORITY);
        if (oauthAuthority == null) {
            oauthAuthority = OAUTH_AUTHORITY;
        }
        
        tenant = enrollment.getStrProperty(AzureADEnrollmentProperties.TENANT);
        applicationKey = enrollment.getStrProperty(AzureADEnrollmentProperties.APPLICATION_KEY);
        applicationId = enrollment.getStrProperty(AzureADEnrollmentProperties.APPLICATION_ID);

        userExtendedAttributesQuery = enrollment.getStrProperty(AzureADEnrollmentProperties.USER_EXTENDED_ATTRIBUTES_QUERY);
    }

    public String getApplicationKey() {
        return applicationKey;
    }

    public String getTenant() {
        return tenant;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getOauthAuthority() {
        return oauthAuthority;
    }

    public String getUserExtendedAttributesQuery() {
        return userExtendedAttributesQuery;
    }
    
    /**
     * Should this enrollment be considered an update (resync,
     * actually. No relation to -update) of a prior enrollment.
     * 
     * This is used by ElementCreator to see if it should look up
     * group references in the dictionary or just create a new one.
     * One glitch of the AAD api is that group information for a
     * single group can come in multiple parts (if they are very
     * large), so if the very first sync has this, the second part
     * will be an addition to the first part, so we need to look up
     * the first part.
     * 
     * There's no harm in this, other than a minor performance hit but
     * we have to do this for every sync after the first anyway, so
     * what's the problem?
     */
    @Override
    public boolean isUpdate() {
        return true;
    }
}
