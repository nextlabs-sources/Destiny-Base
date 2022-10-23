/*
 * Created on Jun 04, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread;

public class AzureADConnectionException extends Exception {

    public AzureADConnectionException(String message) {
        super(message);
    }

    public AzureADConnectionException(String message, Throwable t) {
        super(message, t);
    }

    public AzureADConnectionException(Throwable t) {
        super(t);
    }
      
}
