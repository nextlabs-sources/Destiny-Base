/*
 * Created on Feb 27, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.dictionary.enrollment.enroller.ad.impl;

/**
 * @author safdar
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/container/shared/src/java/main/com/bluejungle/destiny/container/shared/dictionary/enrollment/enroller/ad/impl/InvalidCriteriaFormatException.java#1 $
 */

public class InvalidCriteriaFormatException extends Exception {

    /**
     * Constructor
     * 
     */
    public InvalidCriteriaFormatException() {
        super();
    }

    /**
     * Constructor
     * @param arg0
     */
    public InvalidCriteriaFormatException(String arg0) {
        super(arg0);
    }

    /**
     * Constructor
     * @param arg0
     * @param arg1
     */
    public InvalidCriteriaFormatException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Constructor
     * @param arg0
     */
    public InvalidCriteriaFormatException(Throwable arg0) {
        super(arg0);
    }

}
