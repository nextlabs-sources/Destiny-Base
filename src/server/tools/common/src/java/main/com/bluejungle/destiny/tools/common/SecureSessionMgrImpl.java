/*
 * Created on Mar 3, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.tools.common;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;

import com.bluejungle.destiny.appframework.appsecurity.axis.AuthenticationContext;
import com.bluejungle.destiny.interfaces.secure_session.v1.AccessDeniedFault;
import com.bluejungle.destiny.interfaces.secure_session.v1.InvalidPasswordFault;
import com.bluejungle.destiny.interfaces.secure_session.v1.SecureSessionServiceStub;
import com.bluejungle.destiny.types.secure_session.v1.SecureSession;

import com.nextlabs.axis.JSSESocketFactoryWrapper;

/**
 * This is the secure session manager implementation. This class allows a
 * standalone client to connect securely to a remote web service.
 * 
 * @author ihanen
 */

public class SecureSessionMgrImpl implements ISecureSessionMgr {

    /**
     * Constructor
     */
    public SecureSessionMgrImpl() {
        super();
    }

    public void login(URL location, String username, String password) throws SecureLoginException {
        AuthenticationContext authContext = AuthenticationContext.getCurrentContext();
        authContext.setUsername(username);
        authContext.setPassword(password);

        try {
            SecureSessionServiceStub stub = new SecureSessionServiceStub(null, location.toString());

            JSSESocketFactoryWrapper.attachToStub(stub);
            
            SecureSession s = stub.initSession();
        } catch (IOException | AccessDeniedFault | InvalidPasswordFault e) {
        	e.printStackTrace();
        } 
    }

    /**
     * @see com.bluejungle.destiny.tools.common.ISecureSessionMgr#logout()
     */
    public void logout() {
        AuthenticationContext.clearCurrentContext();
    }

}


