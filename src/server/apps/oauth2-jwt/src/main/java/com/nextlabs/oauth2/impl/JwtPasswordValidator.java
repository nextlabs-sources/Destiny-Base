/*
 * Created on Aug 20, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.oauth2.impl;

import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.util.EncodingUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

import static org.springframework.security.crypto.util.EncodingUtils.concatenate;

import com.nextlabs.oauth2.IJwtPasswordValidator;

/**
 * Largely taken from SecurePbkdf2PasswordEncoder from serverapps code. We just need
 * validation, not creation.
 */
public class JwtPasswordValidator extends Pbkdf2PasswordEncoder implements IJwtPasswordValidator {
    private static final String PASSWORD_PREFIX = "{pbkdf2}";
    
    private int saltLength;
    private final byte[] secret;
    private final int hashWidth;
    private final int iterations;
    private final BytesKeyGenerator saltGenerator;
    private final SecretKeyFactoryAlgorithm algorithm = SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512;

    public JwtPasswordValidator(CharSequence secret, int iterations, int hashWidth, int saltLength) {
        super(secret, iterations, hashWidth);
        
        this.saltLength = saltLength / 8;
        this.secret = Utf8.encode(secret);
        this.iterations = iterations;
        this.hashWidth = hashWidth;
        this.saltGenerator = KeyGenerators.secureRandom(this.saltLength);
        setEncodeHashAsBase64(true);
    }
    
    public boolean matches(String guess, String encodedPassword) {
        if (!encodedPassword.startsWith(PASSWORD_PREFIX)) {
            // Badly formed password
            return false;
        }

        byte[] digested = decodeFromBase64(encodedPassword.substring(PASSWORD_PREFIX.length()));
        byte[] salt = EncodingUtils.subArray(digested, 0, this.saltGenerator.getKeyLength());
        return matches(digested, encodeWithSalt(guess, salt));
    }

    /**
     * Constant time comparison to prevent against timing attacks.
     */
    private static boolean matches(byte[] expected, byte[] actual) {
        if (expected.length != actual.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < expected.length; i++) {
            result |= expected[i] ^ actual[i];
        }
        return result == 0;
    }

    private byte[] encodeWithSalt(String rawPassword, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(),
                                             concatenate(salt, this.secret),
                                             this.iterations,
                                             this.hashWidth);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm.name());
            return concatenate(salt, skf.generateSecret(spec).getEncoded());
        }
        catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not create hash", e);
        }
    }

    private static byte[] decodeFromBase64(String encodedBytes) {
        return Base64.getDecoder().decode(encodedBytes);
    }

    private static String encodeToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
    
}
