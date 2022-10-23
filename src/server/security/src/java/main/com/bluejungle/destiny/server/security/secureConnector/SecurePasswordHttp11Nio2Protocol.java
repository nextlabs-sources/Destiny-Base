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

import org.apache.coyote.http11.Http11Nio2Protocol;

import com.bluejungle.framework.crypt.IDecryptor;
import com.bluejungle.framework.crypt.ReversibleEncryptor;

public class SecurePasswordHttp11Nio2Protocol extends Http11Nio2Protocol {
	
    /**
     * Decryptor object
     */
    private static final IDecryptor DECRYPTOR = new ReversibleEncryptor();

    /**
     * Constructor that setup key store and trust store
     */
    public SecurePasswordHttp11Nio2Protocol() {
		super();
		
		SecureStore secureStore = new SecureStore();
		if(secureStore.getKeystorePass() != null) {
			setKeystorePass(secureStore.getKeystorePass());
		}
		
		if(secureStore.getTruststorePass() != null) {
			setTruststorePass(secureStore.getTruststorePass());
		}
    }
    
    /**
     * Returns the decryptor object
     * 
     * @return the decryptor object
     */
    protected IDecryptor getDecryptor() {
        return DECRYPTOR;
    }

    /**
     * @see org.apache.coyote.http11.AbstractHttp11JsseProtocol#setKeystorePass(java.lang.String)
     */
    @Override
    public void setKeystorePass(String cryptedPass) {
        String realPassword = getDecryptor().decrypt(cryptedPass);
        super.setKeystorePass(realPassword);        
    }

    /**
     * @see org.apache.coyote.http11.AbstractHttp11JsseProtocol#setTruststorePass(java.lang.String)
     */
    @Override
    public void setTruststorePass(String cryptedPass) {
        String realPassword = getDecryptor().decrypt(cryptedPass);
        super.setTruststorePass(realPassword);        
    }
}
