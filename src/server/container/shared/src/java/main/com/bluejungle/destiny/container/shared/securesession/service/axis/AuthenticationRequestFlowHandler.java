/*
 * Created on Feb 28, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.shared.securesession.service.axis;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.impl.soap.SOAPHeaderElement;

import com.bluejungle.destiny.appframework.appsecurity.loginmgr.ILoggedInUser;
import com.bluejungle.destiny.container.shared.applicationusers.core.ApplicationUserManagerFactoryImpl;
import com.bluejungle.destiny.container.shared.applicationusers.core.AuthenticationFailedException;
import com.bluejungle.destiny.container.shared.applicationusers.core.IApplicationUserManager;
import com.bluejungle.destiny.container.shared.applicationusers.core.IApplicationUserManagerFactory;
import com.bluejungle.destiny.container.shared.applicationusers.core.UserManagementAccessException;
import com.bluejungle.destiny.container.shared.applicationusers.repository.IAuthenticatedUser;
import com.bluejungle.destiny.container.shared.securesession.ISecureSession;
import com.bluejungle.destiny.container.shared.securesession.ISecureSessionManager;
import com.bluejungle.destiny.container.shared.securesession.SecureSessionPersistenceException;
import com.bluejungle.destiny.interfaces.secure_session.v1.InvalidPasswordFault;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.bluejungle.framework.datastore.hibernate.HibernateUtils;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.bluejungle.framework.expressions.EvalValue;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.ValueType;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.pf.destiny.lifecycle.EntityManagementException;
import com.bluejungle.pf.destiny.lifecycle.LifecycleManager;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.action.DAction;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;
import com.bluejungle.pf.engine.destiny.EvaluationEngine;
import com.bluejungle.pf.engine.destiny.EvaluationRequest;
import com.bluejungle.pf.engine.destiny.EvaluationResult;
import com.bluejungle.pf.engine.destiny.PolicyEvaluationException;
import com.nextlabs.destiny.configclient.Config;
import com.nextlabs.destiny.configclient.ConfigClient;
import com.nextlabs.destiny.web.delegadmin.helper.AppUserSubject;
import com.nextlabs.destiny.web.delegadmin.helper.DelegationRuleReferenceResolver;
import com.nextlabs.destiny.web.delegadmin.helper.DelegationTargetResolver;
import com.nextlabs.framework.ssl.ConfigurableSSLSocketFactory;

/**
 * Request flow handler responsible for reading and processing the security
 * headers required for web services calls to DAC. These headers can either
 * contain a WS Security compliant username token (non hashed password) or a
 * secure session token
 * 
 * @author sgoldstein
 */
// FIX ME - Should eventually be refactored into multiple classes. Also, should
// be modified to account for additional WS Security tokens
public class AuthenticationRequestFlowHandler extends AbstractHandler {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log LOG = LogFactory.getLog(AuthenticationRequestFlowHandler.class);
    private static final String CLIENT_APPLICATION_OPTION_NAME = "clientApplication";
    
    private static final String CLIENT_APP_ADMINISTRATOR = "Management Console";
	private static final String CLIENT_APP_POLICY_AUTHOR = "Policy Author";
	private static final String CLIENT_APP_REPORTER= "Inquiry Center";
	private static final String CLIENT_APP_REPORTER_ADMIN = "Inquiry Center Admin";
	private static final String CLIENT_APP_REPORTER_USER = "Inquiry Center User";

    private static final String ADMIN_USER_CATEGORY = "ADMIN";
    private static final String USER_CATEGORY_KEY = "::usercategory";

    private LifecycleManager lifecycleManager;
    private List<String> applicableServices = new ArrayList<>();
    private static final Map<String, String> serviceToClientApplication = new HashMap<>();
    private static final Map<String, Log> serviceToAuditLog = new HashMap<>();

    /**
     * @see org.apache.axis.Handler#init()
     */
    public void init(HandlerDescription handlerdesc) {
        super.init(handlerDesc);
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        this.lifecycleManager = componentManager.getComponent(LifecycleManager.COMP_INFO);
        handlerdesc.getParent().getParameters().forEach(parameter -> {
            String parameterName = parameter.getName();
            if (parameterName.startsWith("AuthenticationRequestFlowHandler-ClientApplication")) {
                String serviceName = parameterName.split("-")[2];
                String clientApplication = parameter.getValue().toString();
                serviceToClientApplication.put(serviceName, clientApplication);
                String auditLoggerName = "com.nextlabs.audit.AuthenticationRequestFlowHandler."
                        + clientApplication.toLowerCase().replaceAll("\\s+", "_");
                Log auditLog = LogFactory.getLog(auditLoggerName);
                serviceToAuditLog.put(serviceName, auditLog);
                if (auditLog.isInfoEnabled()) {
                    LOG.info("audit is on for " + auditLoggerName);
                } else {
                    LOG.info("audit is off for " + auditLoggerName);
                }
            } else if ("AuthenticationRequestFlowHandler-Services".equals(parameterName)) {
                applicableServices = Arrays.asList(parameter.getValue().toString().split(","));
            }
        });
        if (serviceToClientApplication.isEmpty()) {
            throw new IllegalStateException("Client applications not specified.");
        }
    }

    /**
     * @return 
     * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
     */
    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
        if (!applicableServices.contains(msgContext.getAxisService().getName())) {
            return InvocationResponse.CONTINUE;
        }
        boolean passedAuthenticationCheckpoint;
        try {
            passedAuthenticationCheckpoint = isAllowedThroughSessionCheckPoint(msgContext)
                    || isAllowedThroughAuthenticationCheckpoint(msgContext)
                    || isAllowedThroughServiceAuthenticationCheckpoint(msgContext);
        } catch (SOAPException exception) {
            throw new AxisFault("Failed to parse SOAP message", exception);
        } catch (InvalidPasswordFault e) {
        	throw new AxisFault("InvalidPasswordFault, Access Denied");
		} catch (ServiceNotReadyFault e) {
			throw new AxisFault("ServiceNotReadyFault, Access Denied");
		}
        
        if (!passedAuthenticationCheckpoint) {
        	throw new AxisFault("Failed Authentication, Access Denied");
        	//throw new AccessDeniedFault();
        }
        return InvocationResponse.CONTINUE;
    }

    /**
     * Look for authentication credentials (username/password) and determine if
     * the credentials are valid
     * 
     * @param msgContext
     * @throws AxisFault
     * @throws SOAPException
     * @throws InvalidPasswordFault 
     * @throws ServiceNotReadyFault 
     * @throws EntityManagementException
     * @throws PQLException
     */
    private boolean isAllowedThroughAuthenticationCheckpoint(MessageContext msgContext) throws AxisFault, SOAPException, InvalidPasswordFault, ServiceNotReadyFault {
        boolean isValidAuthentication = false;
        AxiomElement usernameTokenElement = findUsernameTokenNode(msgContext);
        if (usernameTokenElement != null) {
            final String username = extractUsername(usernameTokenElement);
            final String password = extractPassword(usernameTokenElement);

            IAuthenticatedUser authenticatedUser = null;
            
            try {
                getLog().trace("Attempting authentication for user '" + username + "'");
                IApplicationUserManager userManager = getApplicationUserManager();

                // Authenticate user:
                authenticatedUser = userManager.authenticateUser(username, password);
                boolean hasClientAccess;
                if (authenticatedUser.getLogin().equals(IApplicationUserManager.SUPER_USER_USERNAME)) {
                    // The Super-User can always log-in to the application:
                    hasClientAccess = true;
                } else {
                    // For all other users, we check delegation policies for access
                    getLog().trace("Checking policy based permission for user '" + authenticatedUser.getUniqueName() + "'");
                    hasClientAccess = isAuthorizedForClientApplication(authenticatedUser.getLogin());
                    getLog().trace("Roles permission check for user '" + username + "' returned " + hasClientAccess);
                }
                if (hasClientAccess) {
                    Properties properties = new Properties();
                    properties.setProperty(ISecureSession.USERNAME_PROP_NAME, username);
                    properties.setProperty(ISecureSession.PRINCIPALNAME_PROP_NAME, authenticatedUser.getUniqueName());
                    properties.setProperty(ISecureSession.ID_PROP_NAME, authenticatedUser.getDestinyId().toString());
                    properties.setProperty(ISecureSession.CAN_CHANGE_PASSWORD, 
                            String.valueOf(userManager.canChangePassword(authenticatedUser)));
                    ISecureSession secureSession = getSecureSessionManager().createSession(properties);
                    setSessionForCall(secureSession, msgContext);
                    isValidAuthentication = true;
                }
            } catch (AuthenticationFailedException exception) {
                getLog().debug("Authentication failed.  Invalid username/password.");
            } catch (UserManagementAccessException e) {
                getLog().debug("Could not access the user management system." 
                        + " Perhaps the directory server is down or temporarily inaccessible." 
                        + " Please contact the System Administrator and/or try again later.");
            } catch (SecureSessionPersistenceException exception) {
                getLog().trace("SecureSessionPersistenceException thrown when authenticating '" + username + "'", exception);
                /**
                 * FIX ME - Reusing AxisFault not ideal here
                 */
                throw new AxisFault("Failed to create secure session", exception);
            } catch (ServiceNotReadyFault e) {
            	getLog().debug("Service not ready.");
            	throw new ServiceNotReadyFault("Service not ready.");
			} finally {
                String clientApplication = serviceToClientApplication.get(msgContext.getAxisService().getName());
                Log auditLog = serviceToAuditLog.get(msgContext.getAxisService().getName());
                if (auditLog.isInfoEnabled()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(isValidAuthentication ? "LOGIN_SUCCESS: " : "LOGIN_FAIL: ")
                      .append("app = " + clientApplication)
                      .append(", username = " + username);
                    Long userId;
                    if (authenticatedUser != null) {
                        userId = authenticatedUser.getDestinyId();
                        sb.append(", userid = " + userId);
                    }
                    String source = msgContext.getProperty(MessageContext.REMOTE_ADDR).toString();
                    sb.append(", remote-addr ess = " + source);
                    auditLog.info(sb.toString());
                }
            }
        } else {
            getLog().warn("Authentication request flow handler could not extract user name token element from message");
        }
        return isValidAuthentication;
    }

    /**
     * Determine is a Secure Session is present in the soap header and if it is
     * valid
     * 
     * @param msgContext
     * @return
     * @throws ServiceNotReadyFault
     * @throws SOAPException
     */
    private boolean isAllowedThroughSessionCheckPoint(MessageContext msgContext)
            throws SOAPException, AxisFault, ServiceNotReadyFault {
        boolean isSecureSession = false;

        ISecureSessionManager secureSessionManager = getSecureSessionManager();

        SOAPEnvelope soapEnvelope = msgContext.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();

        Iterator secureSessionElements = soapHeader.getChildrenWithName(new QName(
                AuthenticationHandlerConstants.SECURE_SESSION_HEADER_ELEMENT_NAME
                , ""
                , AuthenticationHandlerConstants.SECURE_SESSION_TYPE_NAMESPACE
        ));

        // Assume only one secure session header element
        if (secureSessionElements.hasNext()) {
            SOAPHeaderElement secureSessionElement = (SOAPHeaderElement) secureSessionElements.next();

            // Currently, the secure session has one child element - the key
            javax.xml.soap.Node secureSessionKeyElement = (javax.xml.soap.Node) secureSessionElement.getFirstChild();
            if (secureSessionKeyElement != null) {
                String secureSessionKey = secureSessionKeyElement.getValue();
                if (secureSessionKey != null) {
                    try {
                        ISecureSession secureSession = secureSessionManager.getSessionByKey(secureSessionKey);
                        if (secureSession != null) {
                            isSecureSession = true;
                            setSessionForCall(secureSession, msgContext);
                        }
                    } catch (SecureSessionPersistenceException exception) {
                        getLog().error("Error while retrieving secure session.  User request may be denied.", exception);
                    }
                }
            }

            // Detach Secure Session Header Element
            secureSessionElement.detachNode();
        }

        return isSecureSession;
    }

    /**
     * Authenticate using client id and secret.
     *
     * @param msgContext message context to get request details
     * @return true if authentication is successful
     */
    private boolean isAllowedThroughServiceAuthenticationCheckpoint(MessageContext msgContext) {
        boolean valid = false;
        String serviceName = msgContext.getAxisService().getName();
        AxiomElement usernameTokenElement = findUsernameTokenNode(msgContext);
        if (usernameTokenElement != null) {
            String clientId = extractUsername(usernameTokenElement);
            String clientSecret = extractPassword(usernameTokenElement);
            LOG.trace(String.format("Attempting authentication for client id '%s'", clientId));
            Config validClientIdConfig = ConfigClient.get(String.format("%s.clientId", serviceName.toLowerCase()));
            Config validClientSecretConfig = ConfigClient.get(String.format("%s.clientSecret", serviceName.toLowerCase()));
            valid = validClientIdConfig != null
                    && validClientSecretConfig != null
                    && clientId != null && !clientId.isEmpty()
                    && clientSecret != null && !clientSecret.isEmpty()
                    && validClientIdConfig.toString().equals(clientId)
                    && validClientSecretConfig.toString().equals(clientSecret);
            Log auditLog = serviceToAuditLog.get(serviceName);
            if (auditLog.isInfoEnabled()) {
                auditLog.info(String.format("%s: app = %s, username = %s, remote-address = %s",
                        valid ? "LOGIN_SUCCESS: " : "LOGIN_FAIL: ",
                        serviceToClientApplication.get(serviceName), clientId,
                        msgContext.getProperty(MessageContext.REMOTE_ADDR).toString()));
            }
        } else {
            LOG.warn("Authentication request flow handler could not extract user name token element from message.");
        }
        if (!valid) {
            LOG.debug("Authentication failed. Invalid client id or secret.");
        }
        return valid;
    }

    /**
     * If a valid username token node is present, find it and return it.
     * Otherwise, return null
     * 
     * @param msgContext
     *            the message context for the current web service call
     * @return the username token node or null if it is not pressent in the
     *         current message
     */
    private AxiomElement findUsernameTokenNode(MessageContext msgContext) {

        SOAPEnvelope soapEnvelope = msgContext.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();

        Iterator securityElements = soapHeader.getChildrenWithName(new QName(
                AuthenticationHandlerConstants.WS_SECURITY_NAMESPACE,
                AuthenticationHandlerConstants.WS_SECURITY_HEADER_ELEMENT_NAME,
                ""
        ));
        while (securityElements.hasNext()) {
            AxiomSOAPHeaderBlock securityElement = (AxiomSOAPHeaderBlock) securityElements.next();
            Iterator usernameTokenElements = securityElement.getChildElements();
            while (usernameTokenElements.hasNext()) {
                AxiomElement axiomElement = (AxiomElement) usernameTokenElements.next();
                if (AuthenticationHandlerConstants.WS_SECURITY_USERNAME_TOKEN_ELEMENT_NAME
                        .equals(axiomElement.getLocalName())) {
                    return axiomElement;
                }
            }
        }
        return null;
    }

    /**
     * Returns the log object
     * 
     * @return the log object
     */
    protected Log getLog() {
        return LOG;
    }

    /**
     * Extract username from username token
     * 
     * @param usernameTokenNode
     *            the username token from which to extract the username
     * @return the extracted username of null if one if not present
     */
    private String extractUsername(AxiomElement usernameTokenNode) {
        if (usernameTokenNode == null) {
            throw new NullPointerException("usernameTokenNode cannot be null.");
        }
        OMElement usernameElement = usernameTokenNode.getFirstChildWithName(new QName(AuthenticationHandlerConstants.WS_SECURITY_NAMESPACE,
                        AuthenticationHandlerConstants.WS_SECURITY_USERNAME_ELEMENT_NAME, ""));
        if (usernameElement != null) {
            return usernameElement.getText();
        }
        return null;
    }

    /**
     * Extract password from username token
     * 
     * @param usernameTokenNode
     *            the username token from which to extract the password
     * @return the extracted password or null if one if not present
     */
    private String extractPassword(AxiomElement usernameTokenNode) {
        if (usernameTokenNode == null) {
            throw new NullPointerException("usernameTokenNode cannot be null.");
        }
        if (usernameTokenNode == null) {
            throw new NullPointerException("usernameTokenNode cannot be null.");
        }
        OMElement passwordElement = usernameTokenNode.getFirstChildWithName(new QName(AuthenticationHandlerConstants.WS_SECURITY_NAMESPACE,
                AuthenticationHandlerConstants.WS_SECURITY_PASSWORD_ELEMENT_NAME, ""));
        if (passwordElement != null) {
            return passwordElement.getText();
        }
        return null;
    }

    /**
     * Set the provided session as the current session for this web service call
     * 
     * @param secureSession
     * @param msgContext
     */
    private void setSessionForCall(ISecureSession secureSession, MessageContext msgContext) {
        msgContext.setProperty(AuthenticationHandlerConstants.SECURE_SESSION_PROPERTY_NAME, secureSession);
    }

    /**
     * Retrieve the Secure Session Manager
     * 
     * @return
     * @throws ServiceNotReadyFault
     */
    private ISecureSessionManager getSecureSessionManager() throws ServiceNotReadyFault {
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();

        if (!componentManager.isComponentRegistered(ISecureSessionManager.COMPONENT_NAME)) {
            throw new ServiceNotReadyFault();
        }

        return (ISecureSessionManager) componentManager.getComponent(ISecureSessionManager.COMPONENT_NAME);
    }

    /**
     * Retrieve the Application user manager
     * 
     * @return the Application User Manager
     * @throws ServiceNotReadyFault
     */
    private IApplicationUserManager getApplicationUserManager() throws ServiceNotReadyFault {
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
        IApplicationUserManager appManager = null;

        if (!compMgr.isComponentRegistered(IApplicationUserManagerFactory.COMP_NAME)) {
            throw new ServiceNotReadyFault();
        }

        try {
            IApplicationUserManagerFactory appUserManagerFactory = compMgr.getComponent(ApplicationUserManagerFactoryImpl.class);
            appManager = appUserManagerFactory.getSingleton();
        } catch (RuntimeException e) {
            throw new ServiceNotReadyFault();
        }
        return appManager;
    }
    
    private boolean isAuthorizedForClientApplication(String username) {
        Set<String> allowedActions = retrieveActions(username);

        return !allowedActions.isEmpty();
    }
    
    
	private Set<String> retrieveActions(String username) {
		ILoggedInUser loggedInUser = getAuthenticatedUser(username);
		getLog().debug("loggedInUser principal name = " + loggedInUser.getPrincipalName() + " and username = "
				+ loggedInUser.getUsername());
		return retrieveActions(loggedInUser);
	}
    
	public Set<String> retrieveActions(ILoggedInUser loggedInUser) {
		Set<String> allowedActions = new TreeSet<String>();
		try {
			getLog().debug("Start evaluating delegation rules to get access controls for the user, [User :{"
					+ loggedInUser.getPrincipalName() + "}]");

			Set<String> allActions = loadAllActions();

			// grant all the permission to super user
			if (loggedInUser.getPrincipalName().equalsIgnoreCase(IApplicationUserManager.SUPER_USER_USERNAME)) {
				return allActions;
			} else {
                getApplicationUserAuthUsingDAPolicies(loggedInUser, allowedActions, allActions);
			}
		} catch (Exception e) {
			getLog().error("Error encountered in delegation rule evaluation for access control,", e);
		}
		return allowedActions;
	}
    
	private void getApplicationUserAuthUsingDAPolicies(ILoggedInUser loggedInUser, Set<String> allowedActions,
			Set<String> allActions) throws PQLException {
		List<IDPolicy> parsedRules = resolveRules();
		DelegationTargetResolver resolver = new DelegationTargetResolver(parsedRules);

		// Subject with attributes
		AppUserSubject subject = getSubjectWithAttributes(loggedInUser);

        if (hasAdminPrivileges(subject)) {
            // Not "Administrator", but has all the admin powers anyway
            allowedActions.addAll(allActions);
            return;
        }
        
		EvaluationEngine engine = null;

		for (String actionName : allActions) {
			EvaluationRequest evalRequest = new EvaluationRequest();
			evalRequest.setRequestId(System.nanoTime());
			evalRequest.setAction(DAction.getAction(actionName));
			evalRequest.setUser(subject);

			engine = new EvaluationEngine(resolver);
			evaluateRule(allowedActions, resolver, engine, actionName, evalRequest);
		}
	}
    
    private boolean hasAdminPrivileges(AppUserSubject subject) {
        IEvalValue category = subject.getAttribute(USER_CATEGORY_KEY);

        if (category != null && category.getType() == ValueType.STRING) {
            return ADMIN_USER_CATEGORY.equals((String)category.getValue());
        }

        return false;
    }
    
	private Set<String> loadAllActions() {
		Set<String> allActions = new TreeSet<String>();
		allActions.add("MANAGE_ENROLLMENT");
		allActions.add("PERFORM_ENROLLMENT");
		allActions.add("ENROLLMENT_MANAGEMENT");
		return allActions;
	}
    
	private AppUserSubject getSubjectWithAttributes(ILoggedInUser loggedInUser) {
		AppUserSubject subject = new AppUserSubject(loggedInUser.getUsername(), loggedInUser.getPrincipalName(),
				loggedInUser.getUsername(), loggedInUser.getPrincipalId(), SubjectType.USER, new DynamicAttributes());
		Map<String, String> propsMap = fetchAppUserProperties(loggedInUser.getPrincipalId());
		
		if (propsMap.get("userType").equals("imported")) {
			// take the properties from AD
			Long authHandlerId = Long.valueOf(propsMap.get("authHandlerId"));
			String username = propsMap.get("username");
			Map<String, String> adPropsMap = fetchExternalUserProperties(authHandlerId, username);
			propsMap.putAll(adPropsMap);
		}
		
		
		for (Map.Entry<String, String> prop : propsMap.entrySet()) {
			subject.setAttribute(prop.getKey(), EvalValue.build(prop.getValue()));
		}
		return subject;
	}
	
	private List<IDPolicy> resolveRules() throws PQLException {
		List<String> policies = findEntitiesByType("DP");
		List<String> components = findEntitiesByType("DC");

		DelegationRuleReferenceResolver resolver = DelegationRuleReferenceResolver.create(policies, components);
		List<IDPolicy> parsedPolicies = resolver.resolve();
		return parsedPolicies;
	}
    
	private void evaluateRule(Set<String> allowedActions, DelegationTargetResolver resolver, EvaluationEngine engine,
			String actionName, EvaluationRequest evalRequest) {
        try {
            EvaluationResult evalResult = engine.evaluate(evalRequest);
            if (EvaluationResult.ALLOW.equals(evalResult.getEffectName())) {
                allowedActions.add(actionName);
            }
        } catch (PolicyEvaluationException e) {
            // Nothing to do. Just ignore policy
        }
	}
    
	private Map<String, String> fetchAppUserProperties(final Long userId) {
		Session session = null;
		Transaction t = null;
		String query = "";
		Map<String, String> propsMap = new HashMap<String, String>();
		try {
			query = "SELECT u.ID, u.USERNAME, u.FIRST_NAME, u.LAST_NAME, u.EMAIL, u.USER_TYPE, u.AUTH_HANDLER_ID, u.USER_CATEGORY, p.PROP_KEY, p.PROP_VALUE "
					+ "FROM APPLICATION_USER u left outer join APP_USER_PROPERTIES p on u.ID = p.USER_ID "
					+ "WHERE u.ID = ?";

			IHibernateRepository dataSource = getActivityDataSource();
			session = dataSource.getSession();
			t = session.beginTransaction();
			Connection conn = session.connection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setLong(1, userId);

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {	
				propsMap.put("userType", rs.getString("USER_TYPE").trim());
				propsMap.put("authHandlerId", rs.getString("AUTH_HANDLER_ID"));
				propsMap.put("username", rs.getString("USERNAME").trim());
				propsMap.put("firstname", rs.getString("FIRST_NAME").trim());
				
				final String lastName = rs.getString("LAST_NAME");
				if (lastName != null){
					propsMap.put("lastname", lastName.trim());
				}
				final String email = rs.getString("EMAIL");
				if (email != null){
					propsMap.put("email", email.trim());
				}

                final String userCategory = rs.getString("USER_CATEGORY");
                if (userCategory != null) {
                    propsMap.put(USER_CATEGORY_KEY, userCategory.trim());
                }
                
				String key = rs.getString("PROP_KEY");
				final String value = rs.getString("PROP_VALUE");
				if (key != null){
					key = key.trim();
					propsMap.put(key, value);
				}
				
			}
			
	
			rs.close();
			stmt.close();
			t.commit();
		} catch (Exception e) {
			getLog().error("Error occurred during fetch application user properties", e);
		} finally {
			HibernateUtils.closeSession(session, getLog());
		}
		return propsMap;
	}
    
	private List<String> findEntitiesByType(final String type) {
		Session session = null;
		Transaction t = null;
		String query = "";
		List<String> devEntities = new ArrayList<String>();
		try {
			query = "SELECT d.ID, d.NAME, d.DESCRIPTION, d.PQL, d.STATUS, d.TYPE"
					+ " FROM DEVELOPMENT_ENTITIES d WHERE d.TYPE = ? AND d.STATUS = ?";
			IHibernateRepository dataSource = getActivityDataSource();

			session = dataSource.getSession();
			t = session.beginTransaction();
			Connection conn = session.connection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, type);
			stmt.setString(2, "DR");

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				final String pql = rs.getString("PQL");
				devEntities.add(pql);
			}
			rs.close();
			stmt.close();
			t.commit();
		} catch (Exception e) {
			getLog().error("Error occurred during delegation rule loading", e);
		} finally {
			HibernateUtils.closeSession(session, getLog());
		}
		return devEntities;
	}
    
	private ILoggedInUser getAuthenticatedUser(final String username) {
		Session session = null;
		Transaction t = null;
		String query = "";
		ILoggedInUser loggedInUser = null;
		try {
			if (username.equalsIgnoreCase(IApplicationUserManager.SUPER_USER_USERNAME)) {
				query = "SELECT u.ID, u.USERNAME, u.FIRST_NAME, u.LAST_NAME, u.DOMAIN_ID, d.NAME  as DOMAIN, 'internal' as USER_TYPE"
						+ " FROM SUPER_APPLICATION_USER u LEFT JOIN APPLICATION_USER_DOMAIN d ON ( d.ID = u.DOMAIN_ID)"
						+ "  WHERE LOWER(u.USERNAME) = ? ";
			} else {
				query = "SELECT u.ID, u.USERNAME, u.FIRST_NAME, u.LAST_NAME, u.DOMAIN_ID, d.NAME as DOMAIN, u.USER_TYPE "
						+ " FROM APPLICATION_USER u LEFT JOIN APPLICATION_USER_DOMAIN d ON ( d.ID = u.DOMAIN_ID)"
						+ " WHERE LOWER(u.USERNAME) = ? ";
			}
			IHibernateRepository dataSource = getActivityDataSource();

			session = dataSource.getSession();
			t = session.beginTransaction();
			Connection conn = session.connection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, username.toLowerCase());

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				final Long userId = rs.getLong("ID");
				final String principalName = rs.getString("USERNAME").trim();
				final String firstName = rs.getString("FIRST_NAME");
				final String lastName = rs.getString("LAST_NAME");
				final String name = firstName + ((lastName != null) ? " " + lastName : "");
				final String domainName = rs.getString("DOMAIN");
				final String userType = rs.getString("USER_TYPE");

				loggedInUser = new ILoggedInUser() {

					@Override
					public Long getPrincipalId() {
						return userId;
					}

					@Override
					public String getUsername() {
						return name;
					}

					@Override
					public String getPrincipalName() {
						return (username.equalsIgnoreCase(IApplicationUserManager.SUPER_USER_USERNAME)) ? principalName
								: principalName + "@" + domainName;
					}

					@Override
					public boolean isPasswordModifiable() {
						if ("internal".equals(userType))
							return true;
						else
							return false;
					}

					@Override
					public String toString() {
						return "LoggedInUser [ User Id:" + this.getPrincipalId() + ", Principal name: "
								+ this.getPrincipalName() + "]";
					}
				};
			}

			rs.close();
			stmt.close();
			t.commit();
		} catch (Exception e) {
			getLog().error("Error occurred during fetch logged in user details", e);
		} finally {
			HibernateUtils.closeSession(session, getLog());
		}
		return loggedInUser;
	}
    
    
	@SuppressWarnings("unchecked")
	private Map<String, String> fetchExternalUserProperties(Long authHandlerId, String username) {

		getLog().info("UserProperties : [ authHandlerId = " + authHandlerId + ", username = " + username + "] ");

		Map<String, String> propsMap = new HashMap<String, String>();
		LdapContext ldapContext = null;
		NamingEnumeration<SearchResult> resultsEnum = null;

		try {
			Map<String, String> extSourceDetail = getExtSourceDetailsById(authHandlerId);

			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, String> configData = mapper.readValue(extSourceDetail.get("CONFIG_DATA_JSON"),
					HashMap.class);
			getLog().debug("Config data map :" + configData);
			HashMap<String, String> userAttrs = mapper.readValue(extSourceDetail.get("USER_ATTRS_JSON"), HashMap.class);
			getLog().debug("userAttrs data map :" + userAttrs);

			ldapContext = getLdapConnection(configData);
			SearchControls controls = getSearchControls(userAttrs, 100);
			resultsEnum = ldapContext.search(configData.get("baseDn"), userAttrs.get("username") + "=" + username,
					controls);

			if (resultsEnum.hasMore()) {
				Attributes attrs = resultsEnum.next().getAttributes();
				for (String attributeId : controls.getReturningAttributes()) {
					if (userAttrs.containsValue(attributeId)) {
						String internalAttr = getKeyFromValue(userAttrs, attributeId);
						propsMap.put(internalAttr,
								attrs.get(attributeId) == null ? "" : String.valueOf(attrs.get(attributeId).get()));
					}
				}
			}
			resultsEnum.close();
		} catch (Exception e) {
			getLog().error("Error occured while getting AD user details", e);
		} finally {
			try {
				if (ldapContext != null)
					ldapContext.close();
			} catch (NamingException e) {
				getLog().warn("Error occured while closing LDAP connection", e);
			}
		}
		return propsMap;
	}
	
	private Map<String, String> getExtSourceDetailsById(Long authHandlerId) {
		Map<String, String> authHandlerData = new HashMap<String, String>();
		Session session = null;
		try {
			String query = "SELECT ID, TYPE, CONFIG_DATA_JSON, USER_ATTRS_JSON FROM AUTH_HANDLER_REGISTRY WHERE ID = ?";

			IHibernateRepository dataSource = getActivityDataSource();
			IHibernateRepository.DbType dbType = dataSource.getDatabaseType();
			session = dataSource.getSession();
			Connection conn = session.connection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setLong(1, authHandlerId);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				switch (dbType) {
				case MS_SQL: {
					authHandlerData.put("CONFIG_DATA_JSON", rs.getString("CONFIG_DATA_JSON"));
					authHandlerData.put("USER_ATTRS_JSON", rs.getString("USER_ATTRS_JSON"));
					break;
				}
				case ORACLE: {
					Clob configDataclob = rs.getClob("CONFIG_DATA_JSON");
					Clob userAttrclob = rs.getClob("USER_ATTRS_JSON");
					authHandlerData.put("CONFIG_DATA_JSON", configDataclob.getSubString(1, (int)configDataclob.length()));
					authHandlerData.put("USER_ATTRS_JSON", userAttrclob.getSubString(1, (int)userAttrclob.length()));
					break;
				}
				case POSTGRESQL:
				default: {
				}

				}
			}

			rs.close();
			stmt.close();
		} catch (Exception e) {
			getLog().error("Error occurred during fetch application user properties", e);
		} finally {
			HibernateUtils.closeSession(session, getLog());
		}
		return authHandlerData;
	}
	
    
	private LdapContext getLdapConnection(Map<String,String> configData) throws Exception {

		String ldapURL = configData.get("ldapUrl");
		String ldapDomain = configData.get("ldapDomain");
		String username = configData.get("username");
		String password = configData.get("password");

		LdapContext ctx = null;
		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		String securityPrincipal = username + "@" + ldapDomain;
		env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
		ReversibleEncryptor encryptor = new ReversibleEncryptor();
		env.put(Context.SECURITY_CREDENTIALS, encryptor.decrypt(password));
		env.put(Context.PROVIDER_URL, ldapURL);
		// This is LDAP via SSL
		if(isSSL(ldapURL)) {
			env.put(Context.SECURITY_PROTOCOL, "ssl");
			env.put("java.naming.ldap.ref.separator", ":");
			env.put("java.naming.ldap.factory.socket", ConfigurableSSLSocketFactory.class.getName());
		}
		// env.put(Context.REFERRAL,"follow");
		ctx = new InitialLdapContext(env, null);
		getLog().info("LDAP Connection Successful.");
		return ctx;
	}
	
	private SearchControls getSearchControls(Map<String,String> userAttrs, int pageSize) {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setCountLimit(pageSize);
		searchControls.setTimeLimit(30000);
		Set<String> attrsIdsList = new HashSet<String>();
		String[] attrIDs = new String[userAttrs.size()];
		for (Map.Entry<String, String> entry : userAttrs.entrySet()) {
			attrsIdsList.add(entry.getValue());
		}
		attrIDs = attrsIdsList.toArray(attrIDs);
		searchControls.setReturningAttributes(attrIDs);
		return searchControls;
	}

	private String getKeyFromValue(Map<String, String> attributesMap, String value) {

		for (String key : attributesMap.keySet()) {
			if (value.equalsIgnoreCase(attributesMap.get(key))) {
				return key;
			}
		}
		return null;
	}
	
	private boolean isSSL(String ldapURL) {
		if(ldapURL.toLowerCase().startsWith("ldaps://")) {
			return true;
		}
		
		return false;
	}

	private IHibernateRepository getActivityDataSource()
	{
		IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
		return (IHibernateRepository) compMgr.getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());
	}
    

}
