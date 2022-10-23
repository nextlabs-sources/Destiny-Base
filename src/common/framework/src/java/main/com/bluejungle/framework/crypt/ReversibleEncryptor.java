/*
 * Created on Feb 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.framework.crypt;

public class ReversibleEncryptor implements IDecryptor, IEncryptor {
    // When the config client gives us an encrypted string it may (will?) have this at the beginning
    // It should be removed before decrypting
    private static final String CONFIG_CLIENT_CIPHER_PREFIX = "{cipher}";
    
    /**
     * must use a single character and nothing from base16, 0-9, a-f, A-F
     */
	static enum Crypto {
		n() { //nextlabs
			@Override
			IDecryptor getDecryptor() {
				return new NextlabsCrypto();
			}

			@Override
			IEncryptor getEncryptor() {
				return new NextlabsCrypto();
			}
		}
	  , s() { //AES128
		    @Override
			IDecryptor getDecryptor() {
				return new AESCrypto();
			}

			@Override
			IEncryptor getEncryptor() {
				return new AESCrypto();
			}
	    }
	    ;
		
		abstract IEncryptor getEncryptor();
		
		abstract IDecryptor getDecryptor();
	}
	
	public String decrypt(String cryptedMsg) {
		if (cryptedMsg == null) {
			return null;
		}

        if (cryptedMsg.startsWith(CONFIG_CLIENT_CIPHER_PREFIX)) {
            cryptedMsg = cryptedMsg.substring(CONFIG_CLIENT_CIPHER_PREFIX.length());
        }
        
		//default is nextlabs which is the old algorithm
		Crypto crypto = Crypto.n;
		
		if (cryptedMsg.length() > 0) {
		    char c = cryptedMsg.charAt(0);
		    
            try {
                crypto = Crypto.valueOf(Character.toString(Character.toLowerCase(c)));
                cryptedMsg = cryptedMsg.substring(1);
            } catch (IllegalArgumentException e) {
                //ignore, use default
            }
		}
		
		return crypto.getDecryptor().decrypt(cryptedMsg);
	}

	public String encrypt(String original) {
		return encrypt(original, Crypto.s.name());
	}

	public String encrypt(String original, String algorithm) {
		if (original == null) {
			return null;
		}
		Crypto crypto = Crypto.valueOf(algorithm.toLowerCase());
		return crypto.getEncryptor().encrypt(original);
	}

}
