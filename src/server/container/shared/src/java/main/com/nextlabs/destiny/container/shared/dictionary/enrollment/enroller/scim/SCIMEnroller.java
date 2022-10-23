/*
 * Created on Mar 12, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.scim;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentSyncException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.EnrollerBase;
import com.bluejungle.dictionary.IDictionary;
import com.bluejungle.dictionary.IEnrollmentSession;
import com.bluejungle.dictionary.IEnrollment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SCIMEnroller extends EnrollerBase {
	private static final Log LOG = LogFactory.getLog(SCIMEnroller.class);
    
	@Override
    protected Log getLog() {
        return LOG;
    }

    @Override
    protected void internalSync(IEnrollment enrollment,
                                IDictionary dictionary,
                                IEnrollmentSession session,
                                SyncResult syncResult) throws EnrollmentSyncException {
        return;
    }

    @Override
    public String getEnrollmentType() {
        return "SCIM";
    }
}
