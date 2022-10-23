/*
 * Created on Sep 14, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.framework.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordHashUtils {
    public static final String PBKDF2_HASH_PREFIX = "{pbkdf2}";
    
    public static final String HASH_WIDTH = "pbkdf2.encoding.hash.width";
    public static final String SALT_WIDTH = "pbkdf2.encoding.salt.width";
    public static final String NUM_ITERATIONS = "pbkdf2.encoding.iteration.count";
    public static final String SECRET_KEY = "pbkdf2.encoding.secret.key";

    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA512";
    private static PasswordHashUtils hashEngine = null;

    private final int numIterations;
    private final String secretKey;
    private final int saltWidth;
    private final int hashWidth;
    
    private PasswordHashUtils(int numIterations, String secretKey, int saltWidth, int hashWidth) {
        this.numIterations = numIterations;
        this.secretKey = secretKey;
        this.saltWidth = saltWidth;
        this.hashWidth = hashWidth;
    }
    
    public synchronized static PasswordHashUtils getInstance(int numIterations, String secretKey, int saltWidth, int hashWidth) {
        if (hashEngine == null) {
            hashEngine = new PasswordHashUtils(numIterations, secretKey, saltWidth, hashWidth);
        }
        return hashEngine;
    }
    
    public synchronized static PasswordHashUtils getInstance() {
        if (hashEngine == null) {
            throw new IllegalStateException("PasswordHashUtils has not been initialized");
        }
        return hashEngine;
    }

    public String hashPassword(String password) {
        try {
            byte[] salt = getSalt();
            return PBKDF2_HASH_PREFIX + encodeToBase64(encodeWithSalt(password.toCharArray(), salt));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Error hashing password", e);
        }
    }

    public boolean validatePassword(String originalPassword, String storedPassword) {
        try {
            byte[] digested = decodeFromBase64(storedPassword.substring(PBKDF2_HASH_PREFIX.length()));
            byte[] salt = subArray(digested, 0, saltWidth/ 8);
            return matches(digested, encodeWithSalt(originalPassword.toCharArray(), salt));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Error validating password", e);
        }
    }
    
    private byte[] encodeWithSalt(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, concatenate(salt, secretKey.getBytes()), numIterations, hashWidth);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_ALGORITHM);
        return concatenate(salt, skf.generateSecret(spec).getEncoded());
    }

    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[saltWidth / 8];
        sr.nextBytes(salt);
        return salt;
    }
    
    private static boolean matches(byte[] expected, byte[] actual) {
        if (expected.length != actual.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < expected.length; i++) {
            // Constant time comparison to avoid timing attack
            result |= expected[i] ^ actual[i];
        }
        return result == 0;
    }

    /**
     * Extract a sub array of bytes out of the byte array.
     */
    private static byte[] subArray(byte[] array, int beginIndex, int endIndex) {
        int length = endIndex - beginIndex;
        byte[] subarray = new byte[length];
        System.arraycopy(array, beginIndex, subarray, 0, length);
        return subarray;
    }

    /**
     * Combine the individual byte arrays into one array.
     */
    private static byte[] concatenate(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }
        byte[] newArray = new byte[length];
        int destPos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, newArray, destPos, array.length);
            destPos += array.length;
        }
        return newArray;
    }

    private static byte[] decodeFromBase64(String encodedBytes) {
        return Base64.getDecoder().decode(encodedBytes);
    }

    private static String encodeToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
