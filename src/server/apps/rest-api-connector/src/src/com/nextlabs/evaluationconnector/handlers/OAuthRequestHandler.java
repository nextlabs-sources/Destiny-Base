/*
 * Created on Jul 09, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.evaluationconnector.handlers;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.nextlabs.evaluationconnector.listeners.OAuthRequestDO;
import com.nextlabs.evaluationconnector.beans.json.OAuth2Token;

import com.nextlabs.oauth2.JwtSecretProvider;

public class OAuthRequestHandler {
    private static final Log log = LogFactory.getLog(OAuthRequestHandler.class);
    private static final long MAX_EXPIRES_IN = 3600L * 24 * 365;
    private static final long DEFAULT_EXPIRES_IN = 3600L;
    private static final String ISSUER = "nextlabs";
    private static final String REALM = "Bearer";

    public OAuth2Token getOAuth2Token(OAuthRequestDO oAuthRequestDo) {
        long expiresIn = DEFAULT_EXPIRES_IN;

        try {
            expiresIn = Math.min(Long.parseLong(oAuthRequestDo.getExpiresIn()), MAX_EXPIRES_IN);
        } catch (NumberFormatException e) {
        }
        
        OAuth2Token token = new OAuth2Token();

        JwtSecretProvider jwtSecretProvider = JwtSecretProvider.getInstance();

        String usersSecret = jwtSecretProvider.getSecret(oAuthRequestDo.getClientId());
        
        if (jwtSecretProvider.validatePassword(oAuthRequestDo.getClientId(), oAuthRequestDo.getClientSecret())) {
            String jwt= JWT.create()
                        .withIssuer(ISSUER)
                        .withSubject(oAuthRequestDo.getClientId())
                        .withExpiresAt(new Date(System.currentTimeMillis() + expiresIn * 1000))
                        .sign(Algorithm.HMAC256(usersSecret));
            token.setAccessToken(jwt);
            token.setTokenType(REALM);
            token.setExpiresIn(expiresIn);
        } else {
            token.setError("invalid_client");
        }
        
        return token;
    }
}
