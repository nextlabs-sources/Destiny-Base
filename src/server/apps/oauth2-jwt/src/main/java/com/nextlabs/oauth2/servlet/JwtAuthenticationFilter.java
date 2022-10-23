/*
 * Copyright 2017 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 2017
 * 
 * Author: sduan
 *
 */
package com.nextlabs.oauth2.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.nextlabs.oauth2.JwtSecretProvider;
import com.nextlabs.oauth2.IJwtVerifierManager;
import com.nextlabs.oauth2.impl.JwtVerifierManager;

public class JwtAuthenticationFilter implements Filter {
    
    private static final Log log = LogFactory
            .getLog(JwtAuthenticationFilter.class);
    
    private static final String xmlUnauthorizedTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
    		"<Error>" + "\n" +
    		"    <Message>%s</Message>" + "\n" +
    		"</Error>" + "\n";
    		
    private static final String jsonUnauthorizedTemplate = "{\"Error\": {\"Message\": \"%s\"}}";
    
    // A flag to enable or disable the filter
    private Boolean enabled;
    
    private final String realm = "Bearer";
    
    private JwtSecretProvider jwtSecretProvider;
    private IJwtVerifierManager jwtVerifierManager;
    
    public void init(FilterConfig filterConfig) throws ServletException {
    	if (Boolean.parseBoolean(filterConfig.getServletContext().getInitParameter("EnableJWTAuthenticationFilter"))) {
    		enabled = true;
    		String rootDir = filterConfig.getServletContext().getInitParameter("RootDirectory");
    		enableJwtFilter(rootDir);
    	} else {
    		enabled = false;
    	}
    }

    /**
     * @param rootDir 
     */
    private void enableJwtFilter(String rootDir) {
        jwtSecretProvider = JwtSecretProvider.getInstance();
        if (!jwtSecretProvider.isInitialized()) {
            jwtSecretProvider.init(rootDir);
        }
        
        if (log.isInfoEnabled()) {
            log.info("JwtAuthenticatilter Filter initialized");
        }
        
        HashMapConfiguration jwtVerifierConfiguration = new HashMapConfiguration();
        jwtVerifierConfiguration.setProperty(IJwtVerifierManager.ROOT_DIRECTORY_CONFIG_PARAM, rootDir);
        jwtVerifierManager = ComponentManagerFactory.getComponentManager().getComponent(JwtVerifierManager.class);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (enabled) {
			doActualFilter(request, response, chain);
		} else {
			chain.doFilter(request, response);
		}
	}
    
    public void doActualFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String contentType = httpRequest.getContentType();
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        if (httpRequest.getMethod().equalsIgnoreCase("POST")) {
        	String stringToken = httpRequest.getHeader("Authorization");
            if (stringToken == null) {
            	unauthorized(httpResponse, "No Authorization header", contentType);
            } else {
                // remove schema from token
                if (stringToken.indexOf(realm) == -1) {
                	unauthorized(httpResponse, "Authorization schema " + realm + " not found", contentType);
                } else {
                	stringToken = stringToken.substring(realm.length()).trim();
                	
                	try {
                		DecodedJWT jwt = JWT.decode(stringToken);
                		
                		String algo = jwt.getAlgorithm();
                		String sub = jwt.getSubject();
                		
            			if (sub == null) {
            				unauthorized(httpResponse, "No subject found in token", contentType);
            			} else {
            				JWTVerifier verifier = jwtVerifierManager.getVerifier(algo, sub);
            				if (verifier == null) {
            					unauthorized(httpResponse, "Invalid token with algo and subject specified", contentType);
            					if (log.isDebugEnabled()) {
            						log.debug(String.format("Verifier can't be created using algo: %s and clientId: %s",
            								algo, sub));
            					}
            				} else {
            					// do the verification
            					verifier.verify(stringToken);
            					chain.doFilter(request, response);
            				}
            			}
                	} catch (JWTDecodeException e) {
                		unauthorized(httpResponse, "Invalid token", contentType);
                		if (log.isDebugEnabled()) {
                			log.debug("Invalid token recevied", e);
                		}
    				} catch (JWTVerificationException e) {
    					unauthorized(httpResponse, e.getMessage(), contentType);
    					if (log.isDebugEnabled()) {
    						log.debug("JWT verify failed: ", e);
    					}
    				}
                }
            }
        } else {
        	chain.doFilter(request, response);
        }
    }
    
    public void destroy() {
    	jwtSecretProvider = null;
        if (log.isInfoEnabled()) {
            log.info("JwtAuthenticationFilter Filter destroyed");
        }
    }
    
    private void unauthorized(HttpServletResponse response, String message, String contentType) throws IOException {
    	response.setStatus(401);
    	response.setCharacterEncoding("UTF-8");
    	// check contentType like application/json or application/json; charset=UTF-8
    	if(contentType != null && contentType.split("[,;]")[0].equals("application/json")) {
    		response.setContentType("application/json");
    		response.getWriter().write(String.format(jsonUnauthorizedTemplate, message));
    	} else {
    		// return xml data
    		response.setContentType("text/xml");
    		response.getWriter().write(String.format(xmlUnauthorizedTemplate, message));
    	}
    }

}
