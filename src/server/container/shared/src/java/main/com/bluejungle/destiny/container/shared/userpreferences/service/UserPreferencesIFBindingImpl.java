/*
 * Created on Sep 15, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc., Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved worldwide.
 */
package com.bluejungle.destiny.container.shared.userpreferences.service;

import com.bluejungle.destiny.container.shared.applicationusers.core.ApplicationUserManagerFactoryImpl;
import com.bluejungle.destiny.container.shared.applicationusers.core.AuthenticationFailedException;
import com.bluejungle.destiny.container.shared.applicationusers.core.IApplicationUserManager;
import com.bluejungle.destiny.container.shared.applicationusers.core.IApplicationUserManagerFactory;
import com.bluejungle.destiny.container.shared.applicationusers.core.UserManagementAccessException;
import com.bluejungle.destiny.container.shared.applicationusers.core.UserNotFoundException;
import com.bluejungle.destiny.container.shared.applicationusers.repository.IApplicationUser;
import com.bluejungle.destiny.container.shared.securesession.ISecureSession;
import com.bluejungle.destiny.container.shared.securesession.service.axis.AuthenticationHandlerConstants;
import com.bluejungle.destiny.interfaces.user_preferences.v1.AccessDeniedFault;
import com.bluejungle.destiny.interfaces.user_preferences.v1.InvalidPasswordFault;
import com.bluejungle.destiny.interfaces.user_preferences.v1.UserPreferencesIF;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author sasha
 */

public class UserPreferencesIFBindingImpl implements UserPreferencesIF {
    private IComponentManager cm;
    private IApplicationUserManager appUserManager;
    private Log log;

    /**
     * Constructor
     *  
     */
    public UserPreferencesIFBindingImpl() {
        super();
        this.cm = ComponentManagerFactory.getComponentManager();
        IApplicationUserManagerFactory aumf = (IApplicationUserManagerFactory) cm.getComponent(ApplicationUserManagerFactoryImpl.class);
        this.appUserManager = aumf.getSingleton();
        
        this.log = LogFactory.getLog(UserPreferencesIFBindingImpl.class.getName());
    }

    /**
     * @see com.bluejungle.destiny.interfaces.report.v1.UserManagementIF#changePassword(java.lang.String, java.lang.String)
     */
    public String changePassword(String oldPassword, String newPassword) throws AccessDeniedFault, InvalidPasswordFault {
        MessageContext context = MessageContext.getCurrentMessageContext();
        ISecureSession secureSession = (ISecureSession) context.getProperty(AuthenticationHandlerConstants.SECURE_SESSION_PROPERTY_NAME);
        String principalName;
        if (secureSession == null) {
            this.log.error("Unable to find the current user id in secure session");
            throw new AccessDeniedFault();
        } else {
            principalName = secureSession.getProperty(ISecureSession.PRINCIPALNAME_PROP_NAME);
        }        

        try {
            IApplicationUser appUser = this.appUserManager.getApplicationUser(principalName);
            this.appUserManager.authenticateUser(appUser.getLogin(), oldPassword);
            this.appUserManager.updateApplicationUser(appUser, newPassword);
        } catch (AuthenticationFailedException e) {
            throw new InvalidPasswordFault();
        } catch (UserNotFoundException e) {
            throw new AccessDeniedFault("error changing user password for user " + principalName, e);
        } catch (UserManagementAccessException e) {
            throw new AccessDeniedFault("error changing user password for user " + principalName, e);
        }

        return "Success";
    }

}
