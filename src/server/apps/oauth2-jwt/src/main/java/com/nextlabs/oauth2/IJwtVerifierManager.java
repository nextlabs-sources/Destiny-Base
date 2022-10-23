/*
 * Created on Sep 08, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.oauth2;

import com.bluejungle.framework.comp.PropertyKey;

import com.auth0.jwt.JWTVerifier;

public interface IJwtVerifierManager {
    PropertyKey<String> ROOT_DIRECTORY_CONFIG_PARAM = new PropertyKey<>("RootDirectory");
    
    String NAME = "JWT Verifier Manager";

    JWTVerifier getVerifier(String algorithm, String clientId);
}
