/*
 * Created on Feb 04, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread;

import com.bluejungle.destiny.container.shared.dictionary.enrollment.common.EnrollmentSyncException;
import com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.util.SyncResultEnum;
import com.bluejungle.dictionary.DictionaryException;
import com.bluejungle.dictionary.IElementBase;
import com.bluejungle.framework.utils.IPair;

import com.google.gson.JsonObject;

public interface IAzureADElementCreator {
    enum AzureADElementType { USER_TYPE, GROUP_TYPE };

    IPair<SyncResultEnum, ? extends IElementBase> createElement(AzureADElementType type, JsonObject object) throws DictionaryException, EnrollmentSyncException;
}
