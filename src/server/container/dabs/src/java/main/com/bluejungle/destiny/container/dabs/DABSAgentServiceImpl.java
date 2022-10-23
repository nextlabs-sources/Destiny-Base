/*
 * Created on Nov 12, 2004
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.dabs;

import com.bluejungle.destiny.container.dcc.DCCResourceLocators;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentDO;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentHeartbeatData;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentManager;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentPolicyAssemblyStatusData;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentProfileStatusData;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentRegistrationData;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentShutdownData;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentStartupConfiguration;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentStartupData;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentType;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentUpdateAcknowledgementData;
import com.bluejungle.destiny.container.shared.agentmgr.IAgentUpdates;
import com.bluejungle.destiny.container.shared.agentmgr.InvalidIDException;
import com.bluejungle.destiny.container.shared.agentmgr.PersistenceException;
import com.bluejungle.destiny.container.shared.agentmgr.service.AgentServiceHelper;
import com.bluejungle.destiny.container.shared.sharedfolder.ISharedFolderInformationRelay;
import com.bluejungle.destiny.container.shared.sharedfolder.ISharedFolderInformationSource;
import com.bluejungle.destiny.framework.types.ID;
import com.bluejungle.destiny.server.shared.configuration.ICustomObligationConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.ICustomObligationsConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.impl.CustomObligationConfigurationDO;
import com.bluejungle.destiny.server.shared.configuration.impl.CustomObligationsConfigurationDO;
import com.bluejungle.destiny.server.shared.registration.ISharedFolderCookie;
import com.bluejungle.destiny.server.shared.registration.ISharedFolderData;
import com.bluejungle.destiny.server.shared.registration.impl.SharedFolderCookieImpl;
import com.bluejungle.destiny.services.agent.AgentServiceIF;
import com.bluejungle.destiny.services.agent.CommitFault;
import com.bluejungle.destiny.services.agent.ServiceNotReadyFault;
import com.bluejungle.destiny.services.agent.UnknownEntryFault;
import com.bluejungle.destiny.services.agent.types.AgentAttachment;
import com.bluejungle.destiny.services.agent.types.AgentAttachments;
import com.bluejungle.destiny.services.agent.types.AgentHeartbeatData;
import com.bluejungle.destiny.services.agent.types.AgentPluginData;
import com.bluejungle.destiny.services.agent.types.AgentPluginDataElement;
import com.bluejungle.destiny.services.agent.types.AgentProfileStatusData;
import com.bluejungle.destiny.services.agent.types.AgentRegistrationData;
import com.bluejungle.destiny.services.agent.types.AgentShutdownData;
import com.bluejungle.destiny.services.agent.types.AgentStartupConfiguration;
import com.bluejungle.destiny.services.agent.types.AgentStartupData;
import com.bluejungle.destiny.services.agent.types.AgentUpdates;
import com.bluejungle.destiny.services.agent.types.AgentUpdateAcknowledgementData;
import com.bluejungle.destiny.services.agent.types.UserNotification;
import com.bluejungle.destiny.services.agent.types.UserNotificationBag;
import com.bluejungle.destiny.services.policy.types.DeploymentRequest;
import com.bluejungle.destiny.services.policy.types.SystemUser;
import com.bluejungle.destiny.types.shared_folder.SharedFolderData;
import com.bluejungle.destiny.types.shared_folder.SharedFolderDataCookie;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.configuration.DestinyConfigurationStoreImpl;
import com.bluejungle.framework.configuration.IDestinyConfigurationStore;
import com.bluejungle.framework.crypt.ReversibleEncryptor;
import com.bluejungle.framework.environment.IResourceLocator;
import com.bluejungle.framework.heartbeat.IServerHeartbeatManager;
import com.bluejungle.framework.security.IKeyManager;
import com.bluejungle.framework.security.KeyNotFoundException;
import com.bluejungle.framework.utils.IMailHelper;
import com.bluejungle.framework.utils.SerializationUtils;
import com.bluejungle.pf.destiny.lib.DTOUtils;
import com.bluejungle.pf.destiny.lib.axis.IPolicyDeployment;
import com.bluejungle.pf.destiny.parser.DefaultPQLVisitor;
import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.DomainObjectDescriptor;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.deployment.DeploymentBundleSignatureEnvelope;
import com.bluejungle.pf.domain.destiny.deployment.IDeploymentBundle;
import com.bluejungle.pf.domain.destiny.misc.EffectType;
import com.bluejungle.pf.domain.destiny.obligation.CustomObligation;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.epicenter.misc.IEffectType;
import com.bluejungle.pf.domain.epicenter.misc.IObligation;
import com.bluejungle.version.IVersion;
import com.bluejungle.versionfactory.VersionFactory;
import com.bluejungle.versionutil.VersionUtil;
import com.nextlabs.framework.utils.ByteArrayDataSource;
import com.nextlabs.random.RandomString;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.DataHandler;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class implements the DABS Agent Web Service
 * 
 * @author ihanen
 */
public class DABSAgentServiceImpl implements AgentServiceIF {

    private AgentServiceHelper agentMgrSvcHelper = AgentServiceHelper.getInstance();
    private ReversibleEncryptor encryptor = new ReversibleEncryptor();

    // These have DFARS compliant certs
    private static final String AGENT_KEYSTORE_FILE_NAME = "agent-keystore.p12";
    private static final String AGENT_TRUSTSTORE_FILE_NAME = "agent-truststore.p12";
    private static final String BUNDLE_SIGNING_PRIVATE_KEY_ALIAS = "bundleSigning";

    // Old keystores with old certs for old systems
    // These are all created by certificate_handler.rb under the installer code
    private static final String LEGACY_AGENT_KEYSTORE_FILE_NAME = "legacy-agent-keystore.p12";
    private static final String LEGACY_AGENT_TRUSTSTORE_FILE_NAME = "legacy-agent-truststore.p12";

    // Pre-9.0, but post 8.5 systems assume that the truststore password is the same as the keystore password
    private static final String LEGACY_AGENT_TRUSTSTORE_KEYSTORE_PASSWORD_FILE_NAME = "legacy-agent-truststore-kp.p12";
    private static final IVersion VERSION_WITH_NEW_CERT_SUPPORT = VersionFactory.makeVersion(8, 5, 0, 0, 0);
    private static final IVersion VERSION_WITH_DIFFERING_KEYS_SUPPORT = VersionFactory.makeVersion(9, 0, 0, 0, 0);
    private static final IVersion VERSION_WITH_ATTACHMENTS_IN_RESPONSE = VersionFactory.makeVersion(9, 1, 0, 0, 0);
    private static final IVersion VERSION_WITH_PKCS12_SUPPORT = VersionFactory.makeVersion(2021, 5, 0, 0, 0);
    private static final String SERVER_PRIVATE_KEY_ALIAS = "DCC";
    private static final String LEGACY_SERVER_PRIVATE_KEY_ALIAS = "Legacy_DCC";

    /**
     * Log object
     */
    private static final Log LOG = LogFactory.getLog(DABSAgentServiceImpl.class.getName());

    /**
     * Constructor
     */
    public DABSAgentServiceImpl() {
        super();
    }

    /**
     * Registers a new agent with DCC
     * 
     * @param registrationData
     *            agent registration info
     * @return registration status
     */
    public AgentStartupConfiguration registerAgent(AgentRegistrationData registrationData)
            throws ServiceNotReadyFault {
        // Convert input into appropriate interface object:
        AgentRegistrationDataImpl data = new AgentRegistrationDataImpl(registrationData);
        IAgentManager agentMgr = this.getAgentManager();
        IAgentStartupConfiguration startupConfig = null;

        try {
            startupConfig = agentMgr.registerAgent(data);
        } catch (PersistenceException e) {
            // FIX ME - Should throw a checked persistence exception
            throw new RuntimeException("Registration failed.", e);
        }

        // Do post-processing - DTO and keystore attachment creation:
        AgentStartupConfiguration configuration = null;
        if (startupConfig != null) {
            configuration = this.agentMgrSvcHelper.extractStartupConfigurationData(startupConfig);

            // Enable "Soap With Attachments"
            getOutgoingMessageContext().setProperty(Constants.Configuration.ENABLE_SWA, Boolean.TRUE);
            
            if (data.getVersion().compareTo(VERSION_WITH_ATTACHMENTS_IN_RESPONSE) >= 0) {
                AgentAttachments attachments = new AgentAttachments();
                
                configuration.setAttachments(attachments);
            }
            
            // Check to see if agent is recent enough to take "new"
            // certs, which are DFARS compliant, or if it requires
            // old ones
            //
            // An alternative approach would be to look at the cert
            // that was used to register and see if it is DFARS
            // compliant and return a corresponding key/trust. I don't
            // know if we have access to the cert here, however.
            //
            // More complication! We can now use different passwords
            // for keystore and truststore.  Older systems don't like
            // that, so we keep yet another truststore around,
            // protected with the keystore password, to hand off to
            // those systems.
            //
            // This is only relevant for DFARS compliant
            // systems. Pre-DFARS are assumed to use the same password
            // for both
            String keystorePassword = getPasswordPrefix() + System.getProperty("nextlabs.javax.net.ssl.keyStorePassword");
            String truststorePassword = getPasswordPrefix() + System.getProperty("nextlabs.javax.net.ssl.trustStorePassword");
            
            if (data.getVersion().compareTo(VERSION_WITH_NEW_CERT_SUPPORT) >= 0) {
                // Attach the registered agent's keystore:
                if (data.getVersion().compareTo(VERSION_WITH_PKCS12_SUPPORT) >= 0) {
                    attachSecurityFileToResponseMessage(data, configuration, "AgentKeyStore", AGENT_KEYSTORE_FILE_NAME);
                } else {
                    // Send current keystore, but not as p12 file
                    attachPK12asJKSToResponseMessage(data, configuration, "AgentKeyStore", AGENT_KEYSTORE_FILE_NAME, keystorePassword, true);
                }

                if (data.getVersion().compareTo(VERSION_WITH_DIFFERING_KEYS_SUPPORT) >= 0) {
                    if (data.getVersion().compareTo(VERSION_WITH_PKCS12_SUPPORT) >= 0) {
                        // Attach the registered agent's truststore:
                        attachSecurityFileToResponseMessage(data, configuration, "AgentTrustStore", AGENT_TRUSTSTORE_FILE_NAME);
                    } else {
                        // Send current truststore, but not as p12
                        // file. Truststore entries are not password
                        // protected
                        attachPK12asJKSToResponseMessage(data, configuration, "AgentTrustStore", AGENT_TRUSTSTORE_FILE_NAME, truststorePassword, false);
                    }
                } else {
                    // Attach the registered agent's truststore (but
                    // one with the same password as the keystore).
                    attachSecurityFileToResponseMessage(data, configuration, "AgentTrustStore", LEGACY_AGENT_TRUSTSTORE_KEYSTORE_PASSWORD_FILE_NAME);
                }
                
            } else {
                // Attach the registered agent's keystore:
                attachSecurityFileToResponseMessage(data, configuration, "AgentKeyStore", LEGACY_AGENT_KEYSTORE_FILE_NAME);
                
                // Attach the registered agent's truststore:
                attachSecurityFileToResponseMessage(data, configuration, "AgentTrustStore", LEGACY_AGENT_TRUSTSTORE_FILE_NAME);
            }
            
            IVersion agentVersion = VersionUtil.convertWSVersionToIVersion(registrationData.getVersion());

            if (agentVersion.getMajor() >= 5 && (agentVersion.getMajor() >= 6 && agentVersion.getMinor() >= 0)) {
                // Keep around for backwards compatibility
                attachStringToResponseMessage(data, configuration, "extra", encryptor.encrypt(keystorePassword));
                
                attachStringToResponseMessage(data, configuration, "keystore_pass", encryptor.encrypt(keystorePassword));
                attachStringToResponseMessage(data, configuration, "truststore_pass", encryptor.encrypt(truststorePassword));
            } else {
                // using old logic.
                attachStringToResponseMessage(data, configuration, "extra", encryptor.encrypt(keystorePassword, "n").substring(1));
            }
        }

        return configuration;
    }

    /**
     * @see com.bluejungle.destiny.services.agent.AgentServiceIF#unregisterAgent(java.math.BigInteger)
     */
    public void unregisterAgent(ID agentId) throws ServiceNotReadyFault, CommitFault, UnknownEntryFault {
        IAgentManager agentManager = getAgentManager();
        try {
            agentManager.unregisterAgent(agentId.getID().longValue());
        } catch (PersistenceException exception) {
            getLog().error("Failed to unregister agent with id, " + agentId, exception);
            throw new CommitFault();
        } catch (InvalidIDException exception) {
            getLog().error("Failed to unregister agent with id, " + agentId + ".  No agent with this id found.", exception);
            throw new UnknownEntryFault();
        }
    }

    private MessageContext getOutgoingMessageContext() {
        try {
            MessageContext incomingMessageContext = MessageContext.getCurrentMessageContext();
            OperationContext operationContext = incomingMessageContext.getOperationContext();
            return operationContext.getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
        } catch (AxisFault e) {
            getLog().error("Unable to find outgoing message context");
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Attach a security files onto the response message only if we are on a
     * secure connection.
     */
    protected void attachSecurityFileToResponseMessage(AgentRegistrationDataImpl data, AgentStartupConfiguration configuration, String attachmentId, String fileName) {
        try {
            IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
            IResourceLocator securityFileLocator = (IResourceLocator) compMgr.getComponent(DCCResourceLocators.SECURITY_RESOURCE_LOCATOR);
            
            attachSecurityFileToResponseMessage(data, configuration, attachmentId, securityFileLocator.getResourceAsStream(fileName));
        } catch (IOException e) {
            getLog().error("Unable to attach security file " + fileName, e);
        }
    }

    /**
     * Attach a jks keystore converted from pkcs12 to the response
     * message. This is for old PDPs that expect jks
     *
     * TODO - Maybe cache the result? Although registration with old
     * PDPs is rare, so maybe it's not worth it
     */
    private void attachPK12asJKSToResponseMessage(AgentRegistrationDataImpl data,
                                                  AgentStartupConfiguration configuration,
                                                  String attachmentId,
                                                  String pkcs12KeystoreFileName,
                                                  String keystorePassword,
                                                  boolean entriesAreEncrypted) {
        IResourceLocator securityFileLocator = (IResourceLocator) ComponentManagerFactory.getComponentManager().getComponent(DCCResourceLocators.SECURITY_RESOURCE_LOCATOR);

        // The first five characters are junk, for double secret secrecy
        char[] password = keystorePassword.substring(RANDOM_PASSWORD_PREFIX_LENGTH).toCharArray();
        try {
            long startConversion = System.nanoTime();
            
            // Read the p12 keystore
            KeyStore sourceKeystore = KeyStore.getInstance("pkcs12");
            sourceKeystore.load(securityFileLocator.getResourceAsStream(pkcs12KeystoreFileName), password);

            // Create jks
            KeyStore destKeystore = KeyStore.getInstance("jks");
            destKeystore.load(null, password);
            
            // Assume aliases have the keystore password
            KeyStore.ProtectionParameter aliasPassword = entriesAreEncrypted ? new KeyStore.PasswordProtection(password) : null;

            // Copy over
            Enumeration<String> aliasList = sourceKeystore.aliases();
            while (aliasList.hasMoreElements()) {
                String alias = aliasList.nextElement();

                KeyStore.Entry entry = sourceKeystore.getEntry(alias, aliasPassword);
                destKeystore.setEntry(alias, entry, aliasPassword);
            }
            
            try (ByteArrayOutputStream boas = new ByteArrayOutputStream()) {
                destKeystore.store(boas, password);
                
                attachSecurityFileToResponseMessage(data, configuration,
                                                    attachmentId,
                                                    new ByteArrayInputStream(boas.toByteArray()));
            }
            long endConversion = System.nanoTime();
            getLog().info("Took " + (endConversion-startConversion)/1000000 + " ms to convert " + pkcs12KeystoreFileName);
        } catch (IOException e) {
            getLog().error("Unable to convert keystore " + pkcs12KeystoreFileName + " to JKS", e);
        } catch (CertificateException e) {
            getLog().error("Certificate error in " + pkcs12KeystoreFileName, e);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            getLog().error("Keystore exception when converting " + pkcs12KeystoreFileName, e);
        }
    }
    
    protected void attachSecurityFileToResponseMessage(AgentRegistrationDataImpl data, AgentStartupConfiguration configuration, String attachmentId, InputStream fileStream) throws IOException {
        try {
            if (isRequestSecure()) {
                // Legacy systems have this data as an actual WSDL
                // attachment. Newer (Axis2) systems can't read those
                // easily, so we include them as part of the response
                // instead
                if (data.getVersion().compareTo(VERSION_WITH_ATTACHMENTS_IN_RESPONSE) >= 0) {
                    AgentAttachment attachment = new AgentAttachment();
                    attachment.setId(attachmentId);
                    
                    String securityFileData = Base64.getEncoder().encodeToString(IOUtils.toByteArray(fileStream));
                    attachment.setString(securityFileData);
                    configuration.getAttachments().addAttachment(attachment);
                } else {
                    ByteArrayDataSource dataSource = new ByteArrayDataSource(IOUtils.toByteArray(fileStream));
                    getOutgoingMessageContext().addAttachment(attachmentId, new DataHandler(dataSource));
                }
            }
        } catch (IOException e) {
        }
    }
    
    /**
     * Attach a string to the response message.
     * 
     * @param attachmentId
     *            id of the attachment
     * @param stringToAttach
     *            string to attach
     */
    protected void attachStringToResponseMessage(AgentRegistrationDataImpl data, AgentStartupConfiguration configuration, String attachmentId, String stringToAttach) {
        if (isRequestSecure()) {
            // Legacy systems have this data as an actual WSDL
            // attachment. Newer (Axis2) systems can't read those
            // easily, so we include them as part of the response
            // instead
            
            if (data.getVersion().compareTo(VERSION_WITH_ATTACHMENTS_IN_RESPONSE) >= 0) {
                AgentAttachment attachment = new AgentAttachment();
                attachment.setId(attachmentId);
                attachment.setString(stringToAttach);
                
                configuration.getAttachments().addAttachment(attachment);
            } else {
                ByteArrayDataSource dataSource = new ByteArrayDataSource(stringToAttach.getBytes());
                getOutgoingMessageContext().addAttachment(attachmentId, new DataHandler(dataSource));
            }
        }
    }

    /**
     * This API is called regularly by all agents to query for updates and
     * notify that they are still online. The response to this call contains
     * notifications about changes that the agent needs to be aware of.
     * 
     * @param heartbeat
     *            heartBeat request (contains the state of the agent)
     * @return a heartbeat response structure
     */
    public AgentUpdates checkUpdates(ID id, AgentHeartbeatData heartbeat) throws ServiceNotReadyFault {
        IAgentManager agentMgr = this.getAgentManager();
        IPolicyDeployment deploymentBundleCreator = this.getDeploymentBundleCreator();

        AgentHeartbeatDataImpl heartbeatData = new AgentHeartbeatDataImpl(heartbeat);
        AgentUpdates updatesData = null;
        try {
            Long agentId = new Long(id.getID().longValue());
            IAgentUpdates updates = agentMgr.checkUpdates(agentId, heartbeatData);
            updatesData = this.agentMgrSvcHelper.extractUpdatesData(updates);
            // Now obtain the policy assembly status updates, if the status data
            // exists:
            IDeploymentBundle bundle = null;
            IAgentDO agent = agentMgr.getAgentById(agentId);

            if (heartbeat.getPolicyAssemblyStatus() != null) {
                String hostName = agent.getHost();
                String agentDomain = null;
                String[] nameAndDomain = hostName.split("[.]", 2);
                if (nameAndDomain.length == 2) {
                    agentDomain = nameAndDomain[1];
                }
                DeploymentRequest req = heartbeat.getPolicyAssemblyStatus();
                bundle = deploymentBundleCreator.getDeploymentBundle(req, agentId, agentDomain, agent.getVersion());
                if (!bundle.isEmpty()) {
                    // FIX ME - Do this once
                    IKeyManager keyManager = getKeyManager();
                    PrivateKey privateKey;

                    try {
                        privateKey = keyManager.getPrivateKey(BUNDLE_SIGNING_PRIVATE_KEY_ALIAS);
                    } catch (KeyNotFoundException knfe) {
                        if (agent.getVersion().compareTo(VERSION_WITH_NEW_CERT_SUPPORT) >= 0) {
                            privateKey = keyManager.getPrivateKey(SERVER_PRIVATE_KEY_ALIAS);
                        } else {
                            privateKey = keyManager.getPrivateKey(LEGACY_SERVER_PRIVATE_KEY_ALIAS);
                        }
                    }
                    
                    try {
                        String[] uids;
                        if (req != null) {
                            SystemUser[] users = req.getDeploymentRequestChoice_type0() != null ? req.getDeploymentRequestChoice_type0().getPolicyUsers() : null;

                            List<String> uidList = new ArrayList<String>();
                            
                            if (users != null) {
                                for (SystemUser user : users) {
                                    if (user != null) {
                                        String uid = user.getSystemId();
                                        if (uid != null) {
                                            uidList.add(uid);
                                        }
                                    }
                                }
                                uids = uidList.toArray(new String[uidList.size()]);
                            } else {
                                uids = new String[0];
                            }
                        } else {
                            uids = new String[0];
                        }
                        DeploymentBundleSignatureEnvelope deploymentBundleEnvelope = new DeploymentBundleSignatureEnvelope(bundle, uids, privateKey);

                        String bundleStr = (bundle == null) ? null : DTOUtils.encodeDeploymentBundle(deploymentBundleEnvelope);
                        updatesData.setPolicyDeploymentBundle(bundleStr);
                    } catch (InvalidKeyException exception) {
                        LOG.error("Failed to deliver bundle", exception);
                    } catch (IOException exception) {
                        LOG.error("Failed to deliver bundle", exception);
                    } catch (SignatureException exception) {
                        LOG.error("Failed to deliver bundle", exception);
                    }
                }
            }

            // Also piggyback the shared folder updates, if any:
            ISharedFolderInformationSource sharedFolderInfoSource = getSharedFolderInformationSource();
            SharedFolderDataCookie wsFolderCookie = heartbeat.getSharedFolderDataCookie();
            Calendar cookieCal = null;
            if (wsFolderCookie != null) {
                cookieCal = wsFolderCookie.getTimestamp();
            }
            ISharedFolderCookie sharedFolderCookie = new SharedFolderCookieImpl(cookieCal);
            ISharedFolderData sharedFolderData = sharedFolderInfoSource.getSharedFolderInformationUpdateSince(sharedFolderCookie);
            SharedFolderData sharedFolderDataUpdate = SharedFolderWebServiceHelper.convertFromSharedFolderData(sharedFolderData);
            updatesData.setSharedFolderData(sharedFolderDataUpdate);

            IVersion agentVersion = agent.getVersion();

            // Obligations used to be configured in configuration.xml,
            // but are now generated in the web Policy Studio.
            // However, upgraded (as opposed to freshly installed)
            // systems might still have old obligations hanging around
            // in the config file.
            //
            // Newer agents assume that any obligation they haven't
            // heard of is a PEP obligation (which is is because, as
            // of 8.5, all Policy Studio obligations are PEP
            // obligations), but older agents need explicit obligation
            // information or they ignore the obligation
            //
            // * If we have obligations in the configuration.xml file,
            //   send them
            // * If the agent is less than 8.0 (see "Newer agents" above)
            //   then any obligations not in the configuration file but
            //   mentioned in policies need dummy information
            //
            // At some point the Policy Studio will support generating
            // PDP obligations. No idea how that will work.
            
            if (agentVersion.getMajor() > 2 || (agentVersion.getMajor() == 2 && agentVersion.getMinor() >= 5)) {
                // Custom obligations are supported in versions > 2.5
                IDestinyConfigurationStore confStore = (IDestinyConfigurationStore) ComponentManagerFactory.getComponentManager().getComponent(DestinyConfigurationStoreImpl.COMP_INFO);

                ICustomObligationsConfigurationDO customObligations = confStore.retrieveCustomObligationsConfig();

                if (agentVersion.compareTo(VersionFactory.makeVersion(8, 0, 0, 0, 0)) < 0) {
                    // Find obligations mentioned in policies, remove the obligations mentioned in the configuration.xml
                    // file (if any), and create fake data for the rest
                    Set<String> policyObligationNames = extractCustomObligationNames(bundle);
                    Set<String> configuredObligationNames = new HashSet<String>();

                    if (customObligations == null) {
                        customObligations = new CustomObligationsConfigurationDO();
                    }

                    for (ICustomObligationConfigurationDO obligation : customObligations.getCustomObligations()) {
                        configuredObligationNames.add(obligation.getDisplayName());
                    }

                    policyObligationNames.removeAll(configuredObligationNames);
                    
                    for (String name : policyObligationNames) {
                        // Create dummy data.
                        CustomObligationConfigurationDO o = new CustomObligationConfigurationDO();
                        o.setDisplayName(name);
                        o.setRunAt("PEP");
                        o.setInvocationString(name);

                        customObligations.addCustomObligation(o);
                    }
                }
                
                updatesData.setCustomObligationsData(CustomObligationsWebServiceHelper.convertFromCustomObligations(customObligations));
            }

            // 3.2 and greater agents use the Heartbeat Plugin structure
            if (agentVersion.getMajor() > 3 || (agentVersion.getMajor() == 3 && agentVersion.getMinor() >= 2)) {
                IServerHeartbeatManager heartbeatManager = getHeartbeatManager();
                AgentPluginDataElement[] heartbeatPluginDataElements = new AgentPluginDataElement[0];
                if (heartbeat.getPluginData() != null && heartbeat.getPluginData().getElement() != null) {
                    heartbeatPluginDataElements = heartbeat.getPluginData().getElement();
                }

                AgentPluginDataElement[] responseData = new AgentPluginDataElement[heartbeatPluginDataElements.length];

                int i = 0;
                // Provide response for each entry in the heartbeat
                for (AgentPluginDataElement e : heartbeatPluginDataElements) {
                    try {
                        Serializable response = heartbeatManager.processHeartbeatPluginData(e.getId(), e.getString());
                        AgentPluginDataElement responseElement = new AgentPluginDataElement();
                        responseElement.setId(e.getId());
                        responseElement.setString(SerializationUtils.wrapSerializable(response));
                        responseData[i++] = responseElement;
                    } catch (Exception exc) {
                        // Something went badly wrong. We can't return any data here, but we can continue processing other
                        // items
                        getLog().warn("Unable to process heartbeat plug-in " + e.getId(), exc);
                    }
                }

                AgentPluginData agentPluginData = new AgentPluginData();
                agentPluginData.setElement(responseData);

                updatesData.setPluginData(agentPluginData);
            }
        } catch (PersistenceException agentManagerException) {
            // TODO: Throw a service-exception here
            LOG.error("Failed to checkUpdates for agent (" + id.getID().longValue() + ")", agentManagerException);
        } catch (InvalidIDException e) {
            throw new RuntimeException(e);
        }

        return updatesData;
    }

    /**
     * This API is called immediately after a call to checkUpdates if
     * checkUpdates has returned any updates and after the updates have been
     * committed on the agent. Acknowledgement of updated data is used to
     * reflect accurate status data on the management console. It is expected
     * that this data will be sent again on the next heartbeat to the agent and
     * that a call to acnowledgeUpdates ONLY serves as a quicker way to reflect
     * current status than a call to checkUpdates.
     * 
     * @param id
     * @param acknowledgementData
     */
    public void acknowledgeUpdates(ID id, AgentUpdateAcknowledgementData acknowledgementData) {
        AgentUpdateAcknowledgementDataImpl acknowledgement = new AgentUpdateAcknowledgementDataImpl(acknowledgementData);
        try {
            this.getAgentManager().acknowledgeUpdates(id.getID().longValue(), acknowledgement);
        } catch (PersistenceException | InvalidIDException | ServiceNotReadyFault e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns true if the current request is secure, false otherwise
     * 
     * @return true if the current request is secure, false otherwise
     */
    protected boolean isRequestSecure() {
        HttpServletRequest req = (HttpServletRequest) MessageContext.getCurrentMessageContext().getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        return req.isSecure();
    }

    /**
     * This API is called when the agent comes on line (when its host starts up)
     * The agent sends some information about its state to DCC.
     * 
     * @param agentId
     *            id of the agent
     * @param startupInfo
     *            startup information
     * @return a confirmation message
     */
    public void startupAgent(ID agentId, AgentStartupData startupInfo) {
        AgentStartupDataImpl startup = new AgentStartupDataImpl(startupInfo);
        try {
            this.getAgentManager().startupAgent(agentId.getID().longValue(), startup);
        } catch (PersistenceException | InvalidIDException | ServiceNotReadyFault e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This API is called when the agent goes offline (when its host shuts down)
     * 
     * @param agentId
     *            id of the agent
     * @return a confirmation message
     */
    public void shutdownAgent(ID agentId, AgentShutdownData shutdownData)
            throws com.bluejungle.destiny.services.agent.UnauthorizedCallerFault, com.bluejungle.destiny.services.agent.ServiceNotReadyFault {
        AgentShutdownDataImpl shutdown = new AgentShutdownDataImpl(shutdownData);
        try {
            this.getAgentManager().shutdownAgent(agentId.getID().longValue(), shutdown);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        } catch (InvalidIDException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see com.bluejungle.destiny.services.agent.AgentServiceIF#sendUserNotifications(com.bluejungle.destiny.services.agent.types.UserNotificationBag)
     */
    public void sendUserNotifications(UserNotificationBag notifications) {
        // TODO: make this asynchronous with a DB-based queue
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
        if (compMgr.isComponentRegistered(IMailHelper.COMP_NAME)) {
            IMailHelper mh = (IMailHelper) compMgr.getComponent(IMailHelper.COMP_NAME);
            UserNotification[] notifs = notifications.getNotifications();
            for (int i = 0; i < notifs.length; i++) {
                UserNotification notification = notifs[i];
                try {
                    mh.sendMessage(notification.getFrom(), notification.getTo(), notification.getSubject(), notification.getBody());
                    // catch all exceptions because we don't want 1 bad message
                    // to affect the rest
                } catch (Exception e) {
                    Log log = getLog();
                    if (log.isWarnEnabled()) {
                        String msgInfo = "from: " + notification.getFrom() + " to: " + notification.getTo() + "subject: " + notification.getSubject() + " body: " + notification.getBody();
                        log.warn("Unable to send notification: " + msgInfo, e);
                    }
                }
            }
        } else {
            getLog().warn("Email notification was ignored because the mail server settings are not configured");
        }
    }

    /**
     * Returns an instance of the IAgentManager implementation class
     * (AgentManager) after obtaining it from the component manager.
     * 
     * @return IAgentManager
     */
    protected IAgentManager getAgentManager() throws ServiceNotReadyFault {
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
        if (!compMgr.isComponentRegistered(IAgentManager.COMP_NAME)) {
            throw new ServiceNotReadyFault();
        }

        return (IAgentManager) compMgr.getComponent(IAgentManager.COMP_NAME);
    }

    /**
     * Retrieve the Key Manager {@link IKeyManager}
     * 
     * @return the Key Manager
     */
    private IKeyManager getKeyManager() throws ServiceNotReadyFault {
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
        if (!compMgr.isComponentRegistered(IKeyManager.COMPONENT_NAME)) {
            throw new ServiceNotReadyFault();
        }

        return (IKeyManager) compMgr.getComponent(IKeyManager.COMPONENT_NAME);
    }

    /**
     * Returns an instance of the IPolicyDeployment implementation class (for
     * policy deployment bundle creation) after obtaining it from the component
     * manager.
     * 
     * @return IAgentManager
     */
    protected IPolicyDeployment getDeploymentBundleCreator() throws ServiceNotReadyFault {
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
        if (!compMgr.isComponentRegistered(IPolicyDeployment.COMP_NAME)) {
            throw new ServiceNotReadyFault();
        }

        return (IPolicyDeployment) compMgr.getComponent(IPolicyDeployment.COMP_NAME);
    }

    /**
     * Returns the heartbeat manager (@link IServerHeartbeatManager}
     *
     */
    private IServerHeartbeatManager getHeartbeatManager() throws ServiceNotReadyFault {
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
        if (!compMgr.isComponentRegistered(IServerHeartbeatManager.COMP_NAME)) {
            throw new ServiceNotReadyFault();
        }

        return (IServerHeartbeatManager) compMgr.getComponent(IServerHeartbeatManager.COMP_NAME);
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
     * Returns an instance of the ISharedFolderInformationSource implementation
     * class after obtaining it from the component manager.
     * 
     * @return ISharedFolderInformationSource
     */
    protected ISharedFolderInformationSource getSharedFolderInformationSource() throws ServiceNotReadyFault {
        IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
        if (!compMgr.isComponentRegistered(ISharedFolderInformationRelay.COMP_NAME)) {
            throw new ServiceNotReadyFault();
        }

        return (ISharedFolderInformationSource) compMgr.getComponent(ISharedFolderInformationRelay.COMP_NAME);
    }

    private Set<String> extractCustomObligationNames(IDeploymentBundle bundle) {
        final Set<String> names = new HashSet<String>();

        if (bundle == null || bundle.isEmpty()) {
            return names;
        }

        try {
            DomainObjectBuilder.processInternalPQL(bundle.getDeploymentEntities(),
                                                   new DefaultPQLVisitor() {
                                                       public void visitPolicy(DomainObjectDescriptor descr, IDPolicy policy) {
                                                           
                                                           // For each effect, get the obligations
                                                           for (IEffectType effect : EffectType.elements()) {
                                                               // For each obligation, check to see if it's a custom obligation
                                                               for (IObligation obl : policy.getObligations(effect)) {
                                                                   if (obl.getType().equals(CustomObligation.OBLIGATION_NAME)) {
                                                                       CustomObligation cust = (CustomObligation)obl;
                                                                       
                                                                       names.add(cust.getCustomObligationName());
                                                                   }
                                                               }
                                                           }
                                                       }
                                                   });
        } catch (PQLException e) {
            getLog().warn("Error parsing policies " + bundle.getDeploymentEntities() + " for custom obligations");
        }
    
        return names;
    }

    private static int RANDOM_PASSWORD_PREFIX_LENGTH = 5;
    
    private String getPasswordPrefix() {
        return RandomString.getRandomString(RANDOM_PASSWORD_PREFIX_LENGTH, RANDOM_PASSWORD_PREFIX_LENGTH, RandomString.ALNUM);
    }
    
    /**
     * Wrapper class to create registration data from the registration DTO
     * 
     * @author safdar
     */
    private class AgentRegistrationDataImpl implements IAgentRegistrationData {

        /*
         * Delegate data
         */
        private AgentRegistrationData registrationData;
        private IAgentType agentType;
        
        /**
         * 
         * Constructor
         * 
         * @param registrationData
         */
        public AgentRegistrationDataImpl(AgentRegistrationData registrationData) throws ServiceNotReadyFault {
            this.registrationData = registrationData;
            this.agentType = getAgentManager().getAgentType(this.registrationData.getType().getValue());
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentRegistrationData#getHost()
         */
        public String getHost() {
            return this.registrationData.getHost();
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentRegistrationData#getType()
         */
        public IAgentType getType() {
            return this.agentType;
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentRegistrationData#getVersion()
         */
        public IVersion getVersion() {
            return VersionUtil.convertWSVersionToIVersion(this.registrationData.getVersion());
        }
    }

    /**
     * Wrapper class to create startup data from the startup DTO
     * 
     * @author safdar
     */
    private class AgentStartupDataImpl implements IAgentStartupData {

        /*
         * Delegate data
         */
        private AgentStartupData startupData;

        /**
         * 
         * Constructor
         * 
         * @param startupData
         */
        public AgentStartupDataImpl(AgentStartupData startupData) {
            this.startupData = startupData;
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentStartupData#getPushPort()
         */
        public Integer getPushPort() {
            Integer result = null;
            if (startupData.getPushPort() != null) {
                result = new Integer(startupData.getPushPort().intValue());
            }
            return result;
        }
    }

    /**
     * Wrapper class to create heartbeat data from the heartbeat DTO
     * 
     * @author safdar
     */
    private class AgentHeartbeatDataImpl implements IAgentHeartbeatData {

        /*
         * Delegate data
         */
        private AgentHeartbeatData heartbeatData;

        /**
         * 
         * Constructor
         * 
         * @param data
         */
        public AgentHeartbeatDataImpl(AgentHeartbeatData data) {
            this.heartbeatData = data;
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentHeartbeatData#getProfileStatus()
         */
        public IAgentProfileStatusData getProfileStatus() {
            if (this.heartbeatData.getProfileStatus() != null) {
                return new AgentProfileStatusDataImpl(this.heartbeatData.getProfileStatus());
            } else
                return null;
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentHeartbeatData#getPolicyAssemblyStatus()
         */
        public IAgentPolicyAssemblyStatusData getPolicyAssemblyStatus() {
            if (this.heartbeatData.getPolicyAssemblyStatus() != null) {
                return new AgentPolicyAssemblyStatusDataImpl(this.heartbeatData.getPolicyAssemblyStatus());
            } else
                return null;
        }
    }

    /**
     * Wrapper class to create update acknowldgement data from the update
     * acknowledgement DTO
     * 
     * @author safdar
     */
    private class AgentUpdateAcknowledgementDataImpl implements IAgentUpdateAcknowledgementData {

        /*
         * Delegate data
         */
        private AgentUpdateAcknowledgementData acknowledgementData;

        /**
         * 
         * Constructor
         * 
         * @param data
         */
        public AgentUpdateAcknowledgementDataImpl(AgentUpdateAcknowledgementData data) {
            this.acknowledgementData = data;
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentUpdateAcknowledgementData#getProfileStatus()
         */
        public IAgentProfileStatusData getProfileStatus() {
            if (this.acknowledgementData.getProfileStatus() != null) {
                return new AgentProfileStatusDataImpl(this.acknowledgementData.getProfileStatus());
            } else
                return null;
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentAcknowledgementData#getPolicyAssemblyStatus()
         */
        public IAgentPolicyAssemblyStatusData getPolicyAssemblyStatus() {
            if (this.acknowledgementData.getPolicyAssemblyStatus() != null) {
                return new AgentPolicyAssemblyStatusDataImpl(this.acknowledgementData.getPolicyAssemblyStatus());
            } else
                return null;
        }
    }

    /**
     * Wrapper class to create profile status data from the profile status DTO
     * 
     * @author safdar
     */
    private class AgentProfileStatusDataImpl implements IAgentProfileStatusData {

        /*
         * Delegate data
         */
        private AgentProfileStatusData profileStatusData;

        /**
         * 
         * Constructor
         * 
         * @param data
         */
        public AgentProfileStatusDataImpl(AgentProfileStatusData data) {
            this.profileStatusData = data;
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentProfileStatusData#getLastCommittedAgentProfileName()
         */
        public String getLastCommittedAgentProfileName() {
            return this.profileStatusData.getLastCommittedAgentProfileName();
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentProfileStatusData#getLastCommittedAgentProfileTimestamp()
         */
        public Calendar getLastCommittedAgentProfileTimestamp() {
            return this.profileStatusData.getLastCommittedAgentProfileTimestamp();
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentProfileStatusData#getLastCommittedCommProfileName()
         */
        public String getLastCommittedCommProfileName() {
            return this.profileStatusData.getLastCommittedCommProfileName();
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentProfileStatusData#getLastCommittedCommProfileTimestamp()
         */
        public Calendar getLastCommittedCommProfileTimestamp() {
            return this.profileStatusData.getLastCommittedCommProfileTimestamp();
        }
    }

    /**
     * Wrapper class to create policy assembly status data from the policy
     * assembly status DTO
     * 
     * @author safdar
     */
    private class AgentPolicyAssemblyStatusDataImpl implements IAgentPolicyAssemblyStatusData {

        /*
         * Delegate data
         */
        private DeploymentRequest policyAssemblyStatus;

        /**
         * 
         * Constructor
         * 
         * @param data
         */
        public AgentPolicyAssemblyStatusDataImpl(DeploymentRequest data) {
            this.policyAssemblyStatus = data;
        }

        /**
         * 
         * @see com.bluejungle.destiny.container.dms.components.agentmgr.IAgentPolicyAssemblyStatusData#getLastCommittedDeploymentBundleTimestamp()
         */
        public Calendar getLastCommittedDeploymentBundleTimestamp() {
            return this.policyAssemblyStatus.getTimestamp();
        }
    }

    /**
     * Wrapper class to create shutdown data from the shutdown DTO.
     * 
     * @author safdar
     */
    private class AgentShutdownDataImpl implements IAgentShutdownData {

        /*
         * Delegate data
         */
        private AgentShutdownData shutdownData;

        /**
         * 
         * Constructor
         * 
         * @param data
         */
        public AgentShutdownDataImpl(AgentShutdownData data) {
            this.shutdownData = data;
        }
    }

}
