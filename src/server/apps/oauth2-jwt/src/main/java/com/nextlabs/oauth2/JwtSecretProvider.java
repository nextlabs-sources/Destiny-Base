/*
 * Copyright 2017 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 2017
 * 
 * Author: sduan
 *
 */
package com.nextlabs.oauth2;

import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.securestore.ISecureStore;
import com.bluejungle.framework.securestore.SecureFileStore;
import com.nextlabs.oauth2.impl.JwtPasswordValidator;

public class JwtSecretProvider {
	private static final Log log = LogFactory
            .getLog(JwtSecretProvider.class);
	
	private static JwtSecretProvider singleton = null;
	private static final String DEFAULT_SECRET_FILE_NAME = "jwt_secret.bin";
	private String secretFileName = DEFAULT_SECRET_FILE_NAME; 
    private IJwtPasswordValidator jwtPasswordValidator = null;
    private JwtSecretsData jwtSecretsData;
	private boolean initialized;
	
	protected JwtSecretProvider() {
		
	}
	
	public void init() {
		this.init(null);
	}
	
	/**
	 * @param baseDir: dpc path
	 */
	public void init(String baseDir) {
		if (initialized) {
			return;
		}
		
		if ( baseDir != null) {
			secretFileName = baseDir + File.separator + DEFAULT_SECRET_FILE_NAME;
			if (log.isDebugEnabled()) {
				log.debug("Set Jwt Secret store file path to: " + secretFileName);
			}
		}

        readSecrets();
        
		initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public static JwtSecretProvider getInstance() {
		if(singleton == null) {
			synchronized(JwtSecretProvider.class) {
				if(singleton == null) {
					singleton = new JwtSecretProvider();
				}
			}
		}
		return singleton;
	}
	
	/**
	 * Get the secret associate with the id specified, null if not found
	 * If init method is not called, IllegalStateException will be thrown
	 * @param id
	 * @return
	 */
	public String getSecret(String id) {
		if (initialized) {

            if (jwtSecretsData == null || jwtSecretsData.getUserData() == null || jwtSecretsData.getUserData().get(id) == null) {
                return null;
            }

            return jwtSecretsData.getUserData().get(id).getJwtSecret();
		} else {
			throw new IllegalStateException("JwtSecretProvider not initialized, please call init method first");
		}
	}

    /**
     * Get the timestamp of the latest batch of secrets. Can be used
     * when heartbeating to request changes as of a particular time
     *
     * @return timestamp (0 if no data has been recorded)
     */
    public long getCurrentSecretsTimestamp() {
        if (jwtSecretsData == null) {
            return 0L;
        }

        return jwtSecretsData.getTimestamp();
    }
    
    /**
     * Check the password for validity
     *
     * @param id
     * @return
     */
    public boolean validatePassword(String id, String password) {
        if (jwtPasswordValidator == null) {
            log.warn("Unable to validate password as password validator has not been initialized");
            // throw exception?
            return false;
        }

        if (jwtSecretsData == null || jwtSecretsData.getUserData() == null || jwtSecretsData.getUserData().get(id) == null || jwtPasswordValidator == null) {
            log.info("No user secrets data received, so unable to validate password");
            return false;
        }
        
        JwtUserData userData = jwtSecretsData.getUserData().get(id);

        byte[] encodedPassword = userData.getPassword();

        if (encodedPassword == null) {
            return false;
        }
        
        return jwtPasswordValidator.matches(password, new String(encodedPassword));
    }
    
    /**
     * Update with new format of data
     */
    public synchronized void update(long timestamp, CharSequence secret, int iterations, int hashWidth, int saltLength, Map<String, JwtUserData> updatedSecrets) {
        if (initialized) {
            jwtSecretsData = new JwtSecretsData(timestamp, secret, iterations, hashWidth, saltLength, updatedSecrets);
            saveSecrets();
            
            jwtPasswordValidator = new JwtPasswordValidator(jwtSecretsData.getSecret(),
                                                            jwtSecretsData.getIterations(),
                                                            jwtSecretsData.getHashWidth(),
                                                            jwtSecretsData.getSaltLength());
        } else {
            log.warn("JwtSecretProvider not initializd, skip update");
        }
    }

    private void readSecrets() {
        try {
            jwtSecretsData = getSecretsSecureStore().read();

            if (jwtSecretsData == null) {
                // Create dummy object?
                return;
            }
            
            jwtPasswordValidator = new JwtPasswordValidator(jwtSecretsData.getSecret(),
                                                            jwtSecretsData.getIterations(),
                                                            jwtSecretsData.getHashWidth(),
                                                            jwtSecretsData.getSaltLength());
        } catch (Exception exception) {
            log.error("Failed to load secrets information", exception);
        }
    }
    
    private void saveSecrets() {
        try {
            getSecretsSecureStore().save(jwtSecretsData);
            log.info("Saved new jwt secret file: " + secretFileName);
        } catch (IOException exception) {
            log.error("Failed to store jwt secrets to file: ", exception);
        }
    }

    
    private ISecureStore<JwtSecretsData> getSecretsSecureStore() throws IOException {
        return new SecureFileStore<JwtSecretsData>(new File(secretFileName).getAbsoluteFile(), "bundleKey");
    }

    private static class JwtSecretsData implements Externalizable {
        private static final long serialVersionUID = -3731846365243189745L;
        
        private long timestamp; // when data was generated by console
        private CharSequence secret;
        private int hashWidth;
        private int iterations;
        private int saltLength;
        
        private Map<String, JwtUserData> userData;

        public JwtSecretsData() {
        }
        
        public JwtSecretsData(long timestamp, CharSequence secret, int iterations, int hashWidth, int saltLength, Map<String, JwtUserData> userData) {
            this.timestamp = timestamp;
            this.secret = secret;
            this.iterations = iterations;
            this.hashWidth = hashWidth;
            this.saltLength = saltLength;
            this.userData = userData;
        }
        
        public long getTimestamp() {
            return timestamp;
        }

        public CharSequence getSecret() {
            return secret;
        }

        public int getHashWidth() {
            return hashWidth;
        }

        public int getIterations() {
            return iterations;
        }

        public int getSaltLength() {
            return saltLength;
        }

        public Map<String, JwtUserData> getUserData() {
            return userData;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeLong(timestamp);
            out.writeObject(secret);
            out.writeInt(hashWidth);
            out.writeInt(iterations);
            out.writeInt(saltLength);
            out.writeObject(userData);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            timestamp = in.readLong();
            secret = (CharSequence)in.readObject();
            hashWidth = in.readInt();
            iterations = in.readInt();
            saltLength = in.readInt();
            userData = (Map<String, JwtUserData>)in.readObject();
        }
    }
    
    public static class JwtUserData implements Externalizable {
        private static final long serialVersionUID = 3092672297225611712L;
        private String jwtSecret;
        private byte[] clientPassword;

        public JwtUserData() {
        }
        
        public JwtUserData(String jwtSecret, byte[] clientPassword) {
            this.jwtSecret = jwtSecret;
            this.clientPassword = clientPassword;
        }

        public byte[] getPassword() {
            return clientPassword;
        }

        public String getJwtSecret() {
            return jwtSecret;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeUTF(jwtSecret);
            out.writeObject(clientPassword);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            jwtSecret = in.readUTF();
            clientPassword = (byte[])in.readObject();
        }
    }
}
