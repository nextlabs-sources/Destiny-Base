/*
 * Created on Jun 01, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.common.ILDAPEnrollmentWrapper;

public interface IAzureADEnrollmentWrapper extends ILDAPEnrollmentWrapper {
    String getApplicationKey();

    String getTenant();

    String getApplicationId();

    String getOauthAuthority();

    String getUserExtendedAttributesQuery();
}
