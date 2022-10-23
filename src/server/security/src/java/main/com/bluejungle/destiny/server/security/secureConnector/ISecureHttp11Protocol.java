/*
 * Created on Mar 22, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.bluejungle.destiny.server.security.secureConnector;

interface ISecureHttp11Protocol {
    String KEYSTORE_SYSTEM_PROPERTY = "nextlabs.javax.net.ssl.keyStore";
    String KEYSTORE_PASSWORD_SYSTEM_PROPERTY = "nextlabs.javax.net.ssl.keyStorePassword";
    String TRUSTSTORE_SYSTEM_PROPERTY = "nextlabs.javax.net.ssl.trustStore";
    String TRUSTSTORE_PASSWORD_SYSTEM_PROPERTY = "nextlabs.javax.net.ssl.trustStorePassword";
    String KEYALIAS_SYSTEM_PROPERTY = "nextlabs.javax.net.ssl.keyAlias";
}
