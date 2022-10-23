package com.bluejungle.destiny.server.security.secureConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum Hash {
	
	MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    SHA384("SHA-384"),
    SHA512("SHA-512");

    private String name;

    Hash(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public byte[] checksum(File input) 
    		throws NoSuchAlgorithmException, IOException {
        try (InputStream in = new FileInputStream(input)) {
            MessageDigest digest = MessageDigest.getInstance(getName());
            byte[] block = new byte[1024];
            int length;
            while ((length = in.read(block)) > 0) {
                digest.update(block, 0, length);
            }
            return digest.digest();
        }
    }
    
    public byte[] checksum(byte[] input)
    		throws NoSuchAlgorithmException {
    	MessageDigest digest = MessageDigest.getInstance(getName());
    	digest.update(input);
    	
    	return digest.digest();
    }
}
