/*
 * Created on Jun 07, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */
package com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.impl;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.AzureADConnectionException;
import com.nextlabs.destiny.container.shared.dictionary.enrollment.enroller.azuread.IAzureADEnrollmentWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AzureADAuthenticator {
    private static final Log LOG = LogFactory.getLog(AzureADAuthenticator.class);
    private static final String SCOPE = "https://graph.microsoft.com/.default";
    private String authority;
    private String tenant;
    private String applicationId;
    private String applicationKey;
    private static final ReversibleEncryptor CIPHER = new ReversibleEncryptor();
    private IAuthenticationResult authenticationResult;
    
    public AzureADAuthenticator(IAzureADEnrollmentWrapper wrapper) throws AzureADConnectionException {
        this.authority = wrapper.getOauthAuthority();
        this.tenant = wrapper.getTenant();
        this.applicationId = wrapper.getApplicationId();
        this.applicationKey = wrapper.getApplicationKey();
        
        this.authenticationResult = getNewAuthenticationResult();
    }
    
    public String getAccessToken() throws AzureADConnectionException {
        if (isTokenExpiring()) {
            LOG.debug("AAD token expiring. Acquiring new token");

            // The documentation implies that we should use the
            // refresh token from the last authentication result to
            // get the next token, but that's always null. We just get
            // a fresh new token
            authenticationResult = getNewAuthenticationResult();
        }

        return authenticationResult.accessToken();
    }

    private static final int CLOSE_TO_EXPIRING_SECS = 30; // seconds

    /**
     * Returns 'true' if the token is close to expiring
     */
    private boolean isTokenExpiring() {
        assert authenticationResult != null;

        return ((authenticationResult.expiresOnDate().getTime() - (new Date()).getTime())/1000 <= CLOSE_TO_EXPIRING_SECS);
    }
    
    /**
     * Returns a new access token based on the client's application credentials
     */
    private IAuthenticationResult getNewAuthenticationResult() throws AzureADConnectionException {
        IAuthenticationResult result = null;
        ExecutorService service = null;
        String oauthURL = null;
        try {
            service = Executors.newFixedThreadPool(1);
            oauthURL = authority + tenant + "/oauth2/authorize";
            
            ConfidentialClientApplication clientApplication = ConfidentialClientApplication.builder(applicationId,
                                                                                                    ClientCredentialFactory.createFromSecret(CIPHER.decrypt(applicationKey))).authority(oauthURL).build();

            ClientCredentialParameters clientCredentialParameters = ClientCredentialParameters.builder(Collections.singleton(SCOPE)).build();

            CompletableFuture<IAuthenticationResult> future = clientApplication.acquireToken(clientCredentialParameters);

            result = future.get(30, TimeUnit.SECONDS);
            
            if (result == null) {
                throw new AzureADConnectionException("Authentication result for " + SCOPE + " from " + oauthURL + " was null");
            }
        } catch (TimeoutException te) {
            throw new AzureADConnectionException("Timed out acquiring authentication token from " + oauthURL);
        } catch (MalformedURLException mue) {
            throw new AzureADConnectionException("Unable to connect to URL " + oauthURL, mue);
        } catch (InterruptedException ie) {
            throw new AzureADConnectionException(ie);
        } catch (ExecutionException ee) {
            throw new AzureADConnectionException(ee);
        } finally {
            service.shutdown();
        }

        return result;
    }
}
