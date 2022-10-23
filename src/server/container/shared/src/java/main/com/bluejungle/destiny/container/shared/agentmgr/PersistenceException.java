/*
 * Created on Mar 14, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.agentmgr;



/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/container/shared/src/java/main/com/bluejungle/destiny/container/shared/agentmgr/PersistenceException.java#1 $
 */

public class PersistenceException extends Exception {
    /**
     * Constructor
     * @param cause
     */
    public PersistenceException(Throwable cause) {
        super(cause);
    }

    /**
     * Create an instance of PersistenceException
     * @param message
     * @param cause
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
