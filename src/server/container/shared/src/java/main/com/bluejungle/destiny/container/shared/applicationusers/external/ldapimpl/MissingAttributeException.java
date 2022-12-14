/*
 * Created on Oct 12, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.applicationusers.external.ldapimpl;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/container/shared/src/java/main/com/bluejungle/destiny/container/shared/applicationusers/external/ldapimpl/MissingAttributeException.java#1 $
 */

public class MissingAttributeException extends Exception {

    /**
     * Constructor
     * 
     */
    public MissingAttributeException() {
        super();
    }

    /**
     * Constructor
     * @param message
     */
    public MissingAttributeException(String message) {
        super(message);
    }

    /**
     * Constructor
     * @param cause
     */
    public MissingAttributeException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     * @param message
     * @param cause
     */
    public MissingAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

}
