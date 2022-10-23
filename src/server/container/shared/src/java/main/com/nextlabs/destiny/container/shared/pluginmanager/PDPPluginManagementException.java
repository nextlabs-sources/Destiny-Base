/*
 * Created on Jan 07, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.container.shared.pluginmanager;

public class PDPPluginManagementException extends Exception {
    public PDPPluginManagementException() {
    }

    public PDPPluginManagementException(String message) {
        super(message);
    }

    public PDPPluginManagementException(String message, Throwable cause) {
        super(message, cause);
    }

    public PDPPluginManagementException(Throwable cause) {
        super(cause);
    }
}
