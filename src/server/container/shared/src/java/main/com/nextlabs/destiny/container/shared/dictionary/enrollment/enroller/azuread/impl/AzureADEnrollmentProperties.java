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

import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.common.BasicLDAPEnrollmentProperties;

public interface AzureADEnrollmentProperties extends BasicLDAPEnrollmentProperties {
    String TENANT = "tenant";
    String OAUTH_AUTHORITY = "azure-oauth-authority";
    String APPLICATION_KEY = "application-key";
    String APPLICATION_ID = "application-id";
    String USER_EXTENDED_ATTRIBUTES_QUERY = "user.extended.attributes.query";
}
