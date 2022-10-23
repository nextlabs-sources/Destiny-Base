/*
 * Created on May 13, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.bluejungle.destiny.container.shared.dictionary.enrollment.common;

/**
 * Attribute name is reserved (used by a built-in attribute or some
 * other reserved expression)
 *
 * @author amorgan
 */

public class ReservedAttributeNameException extends EnrollmentException {
    private static final long serialVersionUID = 1L;

    public ReservedAttributeNameException(String message) {
        super(message);
    }
}
