/*
 * All sources, binaries and HTML pages (C) Copyright 2006 by Blue Jungle Inc,
 * Redwood City, CA. Ownership remains with Blue Jungle Inc.
 * All rights reserved worldwide.
 *
 * @author sergey
 */

package com.bluejungle.dictionary;

import java.util.Date;

/**
 * This interface establishes the contract for obtaining
 * information about the changes to enrollments.
 */
public interface IUpdateRecord {
    String STATUS_DRAFT = "DRAFT";
    String STATUS_ENROLLED = "ENROLLED";
    String STATUS_IN_PROGRESS = "IN_PROGRESS";
    String STATUS_SUCCEEDED = "SUCCESS";
    String STATUS_FAILED = "FAILED";

    /**
     * Obtains the <code>IEnrollment</code> with which this record
     * is associated.
     * @return the <code>IEnrollment</code> with which this record
     * is associated.
     */
    IEnrollment getEnrollment();

    /**
     * Obtains the time when the update has been started.
     * @return the time when the update has been started.
     */
    Date getStartTime();

    /**
     * Obtains the time when the update has been finished
     * or abandones.
     * @return the time when the update has been finished
     * or abandoned.
     */
    Date getEndTime();

    /**
     * gets whether or not the update was successful
     * @obsolete (see getStatus)
     * @return whether this update was successful
     */
    boolean isSuccessful();

    /**
     * obtains the error message of this update
     * @return String of error message
     */
    String getErrorMessage();
    
    /**
     * obtains the next Scheduled Sync Time of this update
     * @return String of nextSyncTime.
     */
    String getNextSyncTime();

}
