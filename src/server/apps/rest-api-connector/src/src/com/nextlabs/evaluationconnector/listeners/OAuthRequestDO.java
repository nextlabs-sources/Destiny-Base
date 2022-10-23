/*
 * Created on Jul 09, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.evaluationconnector.listeners;

public class OAuthRequestDO {
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String expiresIn;

    public OAuthRequestDO(String grantType, String clientId, String clientSecret, String expiresIn) {
        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.expiresIn = expiresIn;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getExpiresIn() {
        return expiresIn;
    }
}
