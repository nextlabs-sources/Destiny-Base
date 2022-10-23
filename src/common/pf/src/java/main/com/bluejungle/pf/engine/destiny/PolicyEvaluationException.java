/*
 * Created on Apr 10, 2017
 *
 * All sources, binaries and HTML pages (C) copyright 2017 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.bluejungle.pf.engine.destiny;

public class PolicyEvaluationException extends Exception {
    public PolicyEvaluationException (String message, Throwable cause) {
        super (message, cause);
    }
    
    public PolicyEvaluationException (Throwable cause) {
        super(cause);
    }
}
