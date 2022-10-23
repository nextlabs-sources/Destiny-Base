/*
 * Created on Mar 22, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author shah
 */
package com.bluejungle.destiny.server.security.secureConnector;

public class SecureHttp11NioProtocol extends SecurePasswordHttp11NioProtocol implements ISecureHttp11Protocol {
    /**
     * @see com.bluejungle.destiny.server.secureConnector.SecureHttp11Nio2Protocol#setKeystorePass(java.lang.String)
     */
    @Override
    public void setKeystorePass(String cryptedPass) {
        super.setKeystorePass(cryptedPass);
        System.setProperty(KEYSTORE_PASSWORD_SYSTEM_PROPERTY, super.getKeystorePass());
    }
    

    /**
     * @see org.apache.coyote.http11.AbstractHttp11JsseProtocol#setKeystoreFile(java.lang.String)
     */
    @Override
    public void setKeystoreFile(String file) {
        super.setKeystoreFile(file);
        System.setProperty(KEYSTORE_SYSTEM_PROPERTY, super.getKeystoreFile());
    }

    /**
     * @see com.bluejungle.destiny.server.secureConnector.SecureHttp11Nio2Protocol#setTruststorePass(java.lang.String)
     */
    @Override
    public void setTruststorePass(String cryptedPass) {
        super.setTruststorePass(cryptedPass);
        System.setProperty(TRUSTSTORE_PASSWORD_SYSTEM_PROPERTY, super.getTruststorePass());
    }

    /**
     * @see org.apache.coyote.http11.AbstractHttp11JsseProtocol#setTruststoreFile(java.lang.String)
     */
    @Override
    public void setTruststoreFile(String file) {
        super.setTruststoreFile(file);
        System.setProperty(TRUSTSTORE_SYSTEM_PROPERTY, super.getTruststoreFile());
    }

    /**
     * @see org.apache.coyote.http11.AbstractHttp11JsseProtocol#setKeyAlias(java.lang.String)
     */
    @Override
    public void setKeyAlias(String keyAlias) {
        super.setKeyAlias(keyAlias);
        System.setProperty(KEYALIAS_SYSTEM_PROPERTY, super.getKeyAlias());
    }

    /**
     * @see com.bluejungle.destiny.server.secureConnector.SecureHttp11Nio2Protocol#setProperty(java.lang.String, java.lang.String)
     */
    @Override
    public boolean setProperty(String name, String value) {
        boolean ret = super.setProperty(name, value);

        return ret;
    }
}
