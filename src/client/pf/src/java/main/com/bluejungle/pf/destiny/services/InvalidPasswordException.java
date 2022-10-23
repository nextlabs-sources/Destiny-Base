/*
 * Created on Sep 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.pf.destiny.services;

import com.bluejungle.destiny.services.policy.PolicyServiceFault;


/**
 * This exception is thrown when a user is trying to change his/her password
 * and supplies a wrong old password.
 * 
 * @author sasha
 */

public class InvalidPasswordException extends PolicyEditorException {

    /**
     * Constructor
     * 
     */
    public InvalidPasswordException() {
        super();
    }

    /**
     * Constructor
     * @param message
     */
    public InvalidPasswordException(String message) {
        super(message);
    }

    /**
     * Constructor
     * @param message
     * @param cause
     */
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     * @param cause
     */
    public InvalidPasswordException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     * @param cause
     */
    public InvalidPasswordException(PolicyServiceFault cause) {
        super(cause);
    }

}
