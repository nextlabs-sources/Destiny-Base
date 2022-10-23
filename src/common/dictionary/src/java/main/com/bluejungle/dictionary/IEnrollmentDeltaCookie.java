/*
 * Created on Jun 21, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.bluejungle.dictionary;

public interface IEnrollmentDeltaCookie {
    public static final String USERS = "users";
    public static final String GROUPS = "groups";
    
    /**
     * Obtains the <code>IEnrollment</code> with which this information
     * is associated 
     * @return associated <code>IEnrollment</code>
     */
    IEnrollment getEnrollment();

    /**
     * Obtains the delta type
     * @return the delta type
     */
    String getDeltaType();
    
    /**
     * Obtains the delta cookie
     * @return the delta cookie
     */
    String getCookieAsString();

    /**
     * Set the delta cookie
     */
    void setCookie(String s);
}
