/*
 * Created on Feb 27, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.server.security.secureConnector;

import com.bluejungle.framework.crypt.IDecryptor;
import com.bluejungle.framework.crypt.ReversibleEncryptor;

import org.apache.coyote.http11.Http11NioProtocol;

/**
 * The secure coyote connector class makes the SSL keystore / truststore non
 * human readable. It reads an encrypted version of the password and returns the
 * real password to the caller.
 * 
 * @author ihanen
 */

public class SecurePasswordHttp11Protocol extends Http11NioProtocol {

    /**
     * Decryptor object
     */
    private static final IDecryptor DECRYPTOR = new ReversibleEncryptor();

    /**
     * Constructor that setup key store and trust store
     */
	public SecurePasswordHttp11Protocol() {
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
     * @see org.apache.coyote.http11.Http11BaseProtocol#setKeystorePass(java.lang.String)
     */
    @Override
    public void setKeystorePass(String cryptedPass) {
        String realPassword = getDecryptor().decrypt(cryptedPass);
        super.setKeystorePass(realPassword);        
    }

    /**
     * @see org.apache.coyote.http11.Http11BaseProtocol#setTruststorePass(java.lang.String)
     */
    @Override
    public void setTruststorePass(String cryptedPass) {
        String realPassword = getDecryptor().decrypt(cryptedPass);
        super.setTruststorePass(realPassword);        
    }
}
