/*
 * Created on Nov 21, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.ldap.tools.ldifconverter.impl;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/directory/src/java/main/com/bluejungle/ldap/tools/ldifconverter/impl/InvalidDNException.java#1 $
 */

public class InvalidDNException extends RuntimeException {

    /**
     * Constructor
     * 
     */
    public InvalidDNException() {
        super();
    }

    /**
     * Constructor
     * @param message
     */
    public InvalidDNException(String message) {
        super(message);
    }

    /**
     * Constructor
     * @param cause
     */
    public InvalidDNException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     * @param message
     * @param cause
     */
    public InvalidDNException(String message, Throwable cause) {
        super(message, cause);
    }

}
