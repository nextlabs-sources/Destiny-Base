/*
 * Created on Jun 13, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.impl;

import java.util.Map;

import org.apache.commons.logging.LogFactory;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentValidationException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.common.BaseLDAPEnrollmentInitializer;
import com.bluejungle.dictionary.DictionaryException;
import com.bluejungle.dictionary.IDictionary;
import com.bluejungle.dictionary.IEnrollment;

public class AzureADEnrollmentInitializer extends BaseLDAPEnrollmentInitializer implements AzureADEnrollmentProperties {
    public AzureADEnrollmentInitializer(IEnrollment enrollment, Map<String, String[]> properties,
                                    IDictionary dictionary) {
        super(enrollment, properties, dictionary, LogFactory.getLog(AzureADEnrollmentInitializer.class));
                                                                    
    }

    public void setup() throws EnrollmentValidationException, DictionaryException {
        // Don't call super.setup(), because some of the properties it requires
        // are inapplicable here (e.g. user.requirements)
        
        // Properties related to Azure AD
        String tenant = setStringProperty(TENANT);
        String oauthAuthority = setStringProperty(OAUTH_AUTHORITY);
        String applicationKey = setStringProperty(APPLICATION_KEY);
        String applicationId = setStringProperty(APPLICATION_ID);

        // OTOH, there are a few things we do need from Base. If the
        // Azure AD .def file starts looking more like regular AD then
        // in the future we should look at removing this code and just
        // calling super.setup() instead.
        
        boolean enrollUsers = setBooleanProperty(ENROLL_USERS, true);
        if (enrollUsers) {
            processExternalUserMapping();
        }
        
        boolean enrollGroups = setBooleanProperty(ENROLL_GROUPS, true);

        // The only reason we might want to set this is if MS changes
        // the Graph API call to get extended attributes for users. As
        // the existing call is a "beta" call, that's not too
        // unlikely.
        setStringProperty(USER_EXTENDED_ATTRIBUTES_QUERY, null);
        
        setBooleanProperty(STORE_MISSING_ATTRIBUTES, false);
        
		setupAutoEnrollment();
        
        // We should test the connection here
        //...
        
        StringBuilder sb = new StringBuilder();
        sb.append("Azure AD connection:\n")
            .append("\n tenant: ").append(tenant)
            .append("\n oauth authority: ").append(oauthAuthority)
            .append("\n application id: ").append(applicationId)
            .append("\n application key: ").append(applicationKey == null ? "<NULL>" : "<HIDDEN>");

        log.info(sb.toString());
    }
}
