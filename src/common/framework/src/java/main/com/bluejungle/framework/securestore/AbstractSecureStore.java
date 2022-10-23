package com.bluejungle.framework.securestore;

/*
 * All sources, binaries and HTML pages (C) copyright 2008 by NextLabs, Inc.
 * San Mateo CA, Ownership remains with NextLabs, Inc.
 * All rights reserved worldwide.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.security.IKeyManager;
import com.bluejungle.framework.utils.ObjectInputStreamWithClassLoader;

/**
 * This abstract class provides a base for implementing
 * the SecureStore<T> interface.
 *
 * @author Sergey Kalinichenko
 */
public abstract class AbstractSecureStore<T extends Serializable> implements ISecureStore<T>, ILogEnabled {
    private static final String AGENT_PRIVATE_KEY_STORE_ALIAS = "secretKeystore";
    private static final String SYMMETRIC_CIPHER_ALGORITHM = "AES";
    private static final String SYMMETRIC_CIPHER_TRANSFORMATION = SYMMETRIC_CIPHER_ALGORITHM + "/CBC/PKCS5Padding";
    private static final int SYMMETRIC_KEY_LENGTH = 256;
    private static final int ALT_SYMMETRIC_KEY_LENGTH = 128;

    private final ClassLoader classLoader;
    private final String privateKeyName;
    private static Log log = LogFactory.getLog(AbstractSecureStore.class.getName());

    public AbstractSecureStore(String privateKeyName) throws IOException {
        this(privateKeyName, AbstractSecureStore.class.getClassLoader());
    }
    
    public AbstractSecureStore(String privateKeyName, ClassLoader classLoader) throws IOException {
        this.privateKeyName = privateKeyName;
        this.classLoader = classLoader;
    }

    public String getPrivateKeyName() {
        return privateKeyName;
    }

    /**
     * @see ISecureStore#read()
     */
    @SuppressWarnings("unchecked")
    public T read() throws IOException {
        InputStream bin = getInputStream();
        if (bin == null) {
            return null;
        }
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            GZIPInputStream zin = new GZIPInputStream(bin);
            ObjectInputStream in = new ObjectInputStream(zin);
            SealedObject sealed = (SealedObject)in.readObject();
            SecretKey secretKey = getSecretKey();
            in.close();
            zin.close();
            Thread.currentThread().setContextClassLoader(classLoader);
            T unsealedObject = (T)sealed.getObject(secretKey);
            return unsealedObject;
        } catch (ClassNotFoundException e) {
            log.warn("Unable to decrypt", e);
        } catch (InvalidKeyException e) {
            log.warn("Unable to decrypt", e);
        } catch (NoSuchAlgorithmException e) {
            log.warn("Unable to decrypt", e);
        } finally {
            bin.close();
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }
        return null;
    }

    /**
     * @see ISecureStore#save(T)
     */
    public void save(T obj) throws IOException {
        OutputStream bout = getOutputStream();
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(SYMMETRIC_CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            SealedObject sealed = new SealedObject(obj, cipher);
            GZIPOutputStream zout = new GZIPOutputStream(bout);
            ObjectOutputStream out = new ObjectOutputStream(zout);
            out.writeObject(sealed);
            out.flush();
            zout.flush();
            bout.flush();
            out.close();
            zout.close();
        } catch (IllegalBlockSizeException e) {
            log.warn("Unable to encrypt", e);
        } catch (NoSuchAlgorithmException e) {
            log.warn("Unable to encrypt", e);
        } catch (NoSuchPaddingException e) {
            log.warn("Unable to encrypt", e);
        } catch (InvalidKeyException e) {
            log.warn("Unable to encrypt", e);
        } finally {
            bout.close();
        }
    }

    public abstract InputStream getInputStream() throws IOException;

    public abstract OutputStream getOutputStream() throws IOException;

    private SecretKey getSecretKey() throws IOException {
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        IKeyManager keyManager = (IKeyManager) componentManager.getComponent(IKeyManager.COMPONENT_NAME);
        SecretKey agentSecretKey = null;
        if (!keyManager.containsSecretKey(privateKeyName)) {
            try {
                agentSecretKey = generateSymmetricKey();
            } catch (GeneralSecurityException gse) {
                throw new IllegalStateException("Unable to create secret key", gse); 
            }
            keyManager.addSecretKey(privateKeyName, agentSecretKey, AGENT_PRIVATE_KEY_STORE_ALIAS);
        } else {
            agentSecretKey = keyManager.getSecretKey(privateKeyName);
        }

        return agentSecretKey;
    }

    public static String defaultAlgorithmType() {
        return SYMMETRIC_CIPHER_TRANSFORMATION;
    }
    
    public static SecretKey generateSymmetricKey() throws GeneralSecurityException {
        SecretKey key;
        try {
            key = generateSymmetricKey(SYMMETRIC_CIPHER_ALGORITHM, SYMMETRIC_KEY_LENGTH);
        } catch (GeneralSecurityException gse) {
            log.warn("Unable to create " + SYMMETRIC_CIPHER_ALGORITHM + " of length " + SYMMETRIC_KEY_LENGTH);
            
            try {
                key = generateSymmetricKey(SYMMETRIC_CIPHER_ALGORITHM, ALT_SYMMETRIC_KEY_LENGTH);
            } catch (GeneralSecurityException gse2) {
                log.warn("Unable to create " + SYMMETRIC_CIPHER_ALGORITHM + " of length " + ALT_SYMMETRIC_KEY_LENGTH);

                throw gse2;
            }
        }
        
        return key;
    }
    
    private static SecretKey generateSymmetricKey(String algorithm, int length) throws GeneralSecurityException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        
        keyGenerator.init(length);
        SecretKey key = keyGenerator.generateKey();
        
        // Just because we can generate a key doesn't mean we can actually *use* it.
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        return key;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Log getLog() {
        return log;
    }
}
