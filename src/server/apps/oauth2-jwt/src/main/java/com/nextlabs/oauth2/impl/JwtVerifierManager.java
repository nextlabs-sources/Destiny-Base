/*
 * Created on Sep 08, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.oauth2.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.LifestyleType;

import com.nextlabs.oauth2.IJwtVerifierManager;
import com.nextlabs.oauth2.JwtSecretProvider;



public class JwtVerifierManager implements IJwtVerifierManager, IHasComponentInfo<IJwtVerifierManager>, IInitializable, IConfigurable {
	private static final Log log = LogFactory.getLog(JwtVerifierManager.class);
    public static final ComponentInfo<IJwtVerifierManager> COMP_INFO =
        new ComponentInfo<IJwtVerifierManager>(IJwtVerifierManager.NAME,
                                               JwtVerifierManager.class,
                                               IJwtVerifierManager.class,
                                               LifestyleType.SINGLETON_TYPE);
    
    private IConfiguration configuration;
    private Map<String, JWTVerifier> verifierCache;
    private Map<String, String> allowedAlgorithms;
    private JwtSecretProvider jwtSecretProvider;
    
    /**
     * @see com.bluejungle.framework.comp.IInitializable#init()
     */
    public void init() {
        verifierCache = Collections.synchronizedMap(new WeakHashMap<String, JWTVerifier>());
        
    	allowedAlgorithms = new HashMap<String, String>();
    	allowedAlgorithms.put("HS256", "HMAC256");
    	allowedAlgorithms.put("HS384", "HMAC384");
    	allowedAlgorithms.put("HS512", "HMAC512");

        jwtSecretProvider = JwtSecretProvider.getInstance();

        if (!jwtSecretProvider.isInitialized()) {
            jwtSecretProvider.init(getConfiguration().get(ROOT_DIRECTORY_CONFIG_PARAM));
        }
    }
    
    /**
     * @see com.bluejungle.framework.comp.IHasComponentInfo#getComponentInfo()
     */
    @Override
    public ComponentInfo<IJwtVerifierManager> getComponentInfo() {
        return COMP_INFO;
    }
    
    /**
     * @see com.bluejungle.framework.comp.IConfigurable#setConfiguration(com.bluejungle.framework.comp.IConfiguration)
     */
    public void setConfiguration(IConfiguration config) {
        this.configuration = config;
             
    }

    /**
     * @see com.bluejungle.framework.comp.IConfigurable#getConfiguration()
     */
    public IConfiguration getConfiguration() {
        return configuration;
    }
    
    /**
     * Get a verifier instance with the algo and clientId specified,
     * or null if not available
     * @param algo
     * @param clientId
     * @return
     * @see com.nextlabs.oauth2.IJwtVerifierManager#getVerifier(String, String)
     */
    @Override
    public JWTVerifier getVerifier(String algo, String clientId) {
		// check algorithm
		if (!allowedAlgorithms.containsKey(algo)) {
			if (log.isDebugEnabled()) {
				log.debug("Algorithm specified in the token is invalid: " + algo);
			}
			return null;
		}
		// get secret
		String secret = jwtSecretProvider.getSecret(clientId);
		
		if (secret == null || secret.equals("")) {
			if (log.isDebugEnabled()) {
				log.debug("No secret found for id: " + clientId);
			}
			return null;
		}
		// use algo + secret as key
		JWTVerifier cachedVerifier = verifierCache.get(algo + secret);
		JWTVerifier verifier = null;
		if (cachedVerifier == null) {
			try {
				Method algoMethod = Algorithm.class.getMethod(
						allowedAlgorithms.get(algo),
						String.class);
				Algorithm algorithm = (Algorithm) algoMethod.invoke(null, secret);
				verifier = JWT.require(algorithm)
						.acceptExpiresAt(1)
						.build();
				// put the verifier to cache
				if (log.isDebugEnabled()) {
					log.debug("Verifier cached for client: " + clientId);
				}
				verifierCache.put(algo + secret, verifier);
			} catch (IllegalAccessException e) {
				log.error("Error creating verifier: ", e);
			} catch (InvocationTargetException e) {
				log.error("Error creating verifier: ", e);
			} catch (NoSuchMethodException e) {
				log.error("Error creating verifier: ", e);
			} catch (SecurityException e) {
				log.error("Error creating verifier: ", e);
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Cached verifier found for client: " + clientId);
			}
			verifier = cachedVerifier;
		}
		
		return verifier;
	}
}
