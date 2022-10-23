/*
 * Created on Aug 23, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */
package com.nextlabs.oauth2;

public interface IJwtPasswordValidator {
    boolean matches(String rawPassword, String encodedPassword);
}
