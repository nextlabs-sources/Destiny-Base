package com.bluejungle.destiny.agent.controlmanager;

/*
 * Created on May 11, 2006
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.bluejungle.destiny.agent.activityjournal.IActivityJournal;
import com.bluejungle.destiny.agent.activityjournal.LogIdGenerator;
import com.bluejungle.destiny.agent.ipc.OSType;
import com.bluejungle.destiny.agent.pdpapi.PDPSDK;
import com.bluejungle.domain.action.ActionEnumType;
import com.bluejungle.domain.agenttype.AgentTypeEnumType;
import com.bluejungle.framework.comp.HashMapConfiguration;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.IManagerEnabled;
import com.bluejungle.framework.expressions.BooleanOp;
import com.bluejungle.framework.expressions.CompositePredicate;
import com.bluejungle.framework.expressions.EvalValue;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.framework.utils.Pair;
import com.bluejungle.framework.utils.StringUtils;
import com.bluejungle.pf.destiny.services.UNCUtil;
import com.bluejungle.pf.domain.destiny.common.SpecAttribute;
import com.bluejungle.pf.domain.destiny.misc.EffectType;
import com.bluejungle.pf.domain.destiny.obligation.AgentLogObligation;
import com.bluejungle.pf.domain.destiny.obligation.DObligation;
import com.bluejungle.pf.domain.destiny.obligation.IDObligation;
import com.bluejungle.pf.domain.destiny.obligation.ILoggingLevel;
import com.bluejungle.pf.domain.destiny.resource.ResourceAttribute;
import com.bluejungle.pf.domain.destiny.subject.IDSubject;
import com.bluejungle.pf.domain.destiny.subject.IDSubjectManager;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;
import com.bluejungle.pf.domain.epicenter.resource.IMResource;
import com.bluejungle.pf.domain.epicenter.resource.Resource;
import com.bluejungle.pf.engine.destiny.EvaluationRequest;
import com.bluejungle.pf.engine.destiny.EvaluationResult;
import com.bluejungle.pf.engine.destiny.IAgentPolicyContainer;
import com.bluejungle.pf.engine.destiny.IBundleVault;
import com.nextlabs.destiny.agent.controlmanager.IObligationHandler;
import com.nextlabs.destiny.agent.controlmanager.IObligationResultData;
import com.nextlabs.destiny.agent.controlmanager.IPolicyEvaluationResult;
import com.nextlabs.destiny.agent.controlmanager.ObligationContext;
import com.nextlabs.destiny.agent.controlmanager.ObligationResultData;
import com.nextlabs.destiny.agent.controlmanager.PerformObligationsEnumType;
import com.nextlabs.destiny.agent.controlmanager.PolicyActionParser;
import com.nextlabs.destiny.agent.controlmanager.PolicyEvaluationResult;
import com.nextlabs.destiny.agent.pdpapi.IPDPPermissionsResponse;
import com.nextlabs.destiny.agent.pdpapi.PDPPermissionsResponse;
import com.nextlabs.domain.log.PolicyActivityInfoV5;
import com.nextlabs.domain.log.PolicyActivityLogEntryV5;
import com.nextlabs.oauth2.IJwtVerifierManager;
import com.nextlabs.oauth2.impl.JwtVerifierManager;
import com.nextlabs.pf.engine.destiny.IAgentXACMLPolicyContainer;
import com.nextlabs.pf.engine.destiny.IWrappedXACMLResponse;
import com.nextlabs.pf.engine.destiny.XACMLEvaluationResult;
import com.nextlabs.pf.engine.destiny.WrappedXACMLResponse;

/**
 * @author ihanen
 */
public class PolicyEvaluatorImpl implements IPolicyEvaluator, ILogEnabled, IManagerEnabled, IInitializable, IConfigurable {

    /**
     * Sid of the system user
     */
    private static final String LOCAL_SYSTEM_USER = "S-1-5-18";

    /**
     * 127.0.0.1 in network byte order
     */
    private static final long LOCALHOST_BYTE_ADDRESS = 0x7f000001;

    /**
     * Caching parameters
     */
    private static final String UNRESOLVED_DNS_CACHE_SUFFIX = "UnresolvedDNSCache";

    /**
     * Public bin folder name for the control module
     */
    private static final String PUBLIC_BIN_FOLDER = "public_bin";

    /**
     * Icon file.  Not tamper proofed
     */
    private static final String ICON_FILE_NAME = "app-icon.ico";

    /**
     * Ignored file
     */
    private static final String IGNORED_RESOURCES_FILE = "config/folder.info";

    /**
     * Token for policy expression
     */
    private static final String MY_DOCUMENTS_TOKEN = "[mydocuments]";
    private static final String MY_DOCUMENTS_PATTERN = "\\[mydocuments\\]";
    private static final String MY_DESKTOP_TOKEN = "[mydesktop]";
    private static final String MY_DESKTOP_PATTERN = "\\[mydesktop\\]";
    private static final String NO_ATTACHMENT = "[no attachment]";

    private static final String DONT_CARE_RESPONSE_ACCEPTABLE_KEY = "nextlabs.dont.care.acceptable";
    private static final String ERROR_RESPONSE_ACCEPTABLE_KEY = "nextlabs.error.result.acceptable";
    private static final String USE_RESOURCE_TYPE_WHEN_EVALUATING_KEY = "nextlabs.evaluation.uses.resource.type";
    private boolean dontCareAcceptable = false;  // Can be overridden by the query
    private boolean errorAcceptable = false;  // Can be overridden by the query
    private boolean useResourceTypeWhenEvaluating = true;
    private PolicyActionParser policyActionParser;
    
    private static final long UNKNOWN_POLICY_ID = -1L;
    
    private Log log;
    private IConfiguration configuration;
    private IControlManager controlManager;
    private IComponentManager manager;
    private IActivityJournal activityJournal;
    private IJwtVerifierManager jwtVerifierManager;
    private IObligationHandler obligationHandler;
    
    /**
     * Subject manager
     */
    private IDSubjectManager subjectManager;

    /**
     * Policy containers for ACPL and XACML (the latter is optional)
     */
    private IAgentPolicyContainer policyContainer;
    
    private IAgentXACMLPolicyContainer xacmlPolicyContainer;

    // caching of reverse DNS lookups
    private Cache dnsCache;

    // temporary storage for names that we haven't resolved but are unwilling
    // to declare permanently unresolveable
    private static final int MAX_DNS_ATTEMPTS=3;
    private Map<String, Integer> undecidedDnsCache;
    
    // names that we can't resolve
    private Cache unresolvedDnsCache;

    // my type
    private AgentTypeEnum agentType;
    
    /**
     * Predicates to ignore or track file resources
     */
    private CompositePredicate ignoredResourceCondition = null;
    
    /**
     * @param fileName
     * @return canonicalized version of file name
     */
    private String canonicalizeFileName(String fileName, String resourceType) {
        if (fileName == null || fileName.length() == 0 ||
            (resourceType != null && !resourceType.equals("fso"))) {
            return fileName;
        }

        String ret = fileName.replace('\\', '/');

        // There has to be a better way to do this.  We need the concept of an empty resource
        if (ret.equals("c:/no_attachment.ice")) {
            return NO_ATTACHMENT;
        }

        if (ret.startsWith("//")) {
            return ("file:" + ret);
        } else {
            return ("file:///" + ret);
        }
    }

    /**
     * @param nativeResources
     * @return
     */
    private List<String> canonicalizeResources(List<String> nativeResources, String resourceType) {
        if (nativeResources == null) {
            return null;
        }
        List<String> res = new ArrayList<String>();
        for (String name : nativeResources) {
            res.add(canonicalizeFileName(name.toLowerCase(), resourceType));
        }
        return res;
    }

    /**
     * Returns the agent type
     * 
     * @return the agent type
     */
    protected AgentTypeEnum getAgentType() {
        return this.agentType;
    }

    /**
     * @see com.bluejungle.framework.comp.IConfigurable#getConfiguration()
     */
    public IConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * Returns the control manager
     * 
     * @return the control manager
     */
    protected IControlManager getControlManager() {
        return controlManager;
    }

    /**
     * Returns the activity journal
     *
     * @return the activity journal
     */
    protected IActivityJournal getActivityJournal() {
        return activityJournal;
    }

    /**
     * Returns the obligation handler
     * 
     * @return the obligation handler
     */
    protected IObligationHandler getObligationHandler() {
        return obligationHandler;
    }
    
    /**
     * Returns the verifier manager
     */
    protected IJwtVerifierManager getJwtVerifierManager() {
        return jwtVerifierManager;
    }
    
    /**
     * @param fileNameList
     *            filename with variable
     * @return ArrayList containing all possible variations of the name
     */
    private List<String> getEquivalentFileNames(String fileName, boolean resolveHostNames) {
        if (fileName == null || fileName.length() == 0) {
            return null;
        }

        List<String> fileNameList = null;

        FolderTrie sharedFolderTrie = controlManager.getSharedFolderTrie();
        if (sharedFolderTrie != null) {
            if (this.agentType == AgentTypeEnum.DESKTOP) {
                List<String> names = new ArrayList<String>();
                if (resolveHostNames) {
                    names.addAll(UNCUtil.getEquivalentHostNames(fileName));
                }
                else {
                    names.add(fileName);
                }

                for (String name : names) {
                    List<String> nameList = sharedFolderTrie.getFolderList(name);
                    if (fileNameList == null) {
                        fileNameList = nameList;
                    } else {
                        fileNameList.addAll(nameList);
                    }
                }
            } else {
                fileNameList = sharedFolderTrie.getFolderList(fileName);
            }
        } else {
            fileNameList = new ArrayList<String>();
            if (this.agentType == AgentTypeEnum.DESKTOP && resolveHostNames) {
                fileNameList.addAll(UNCUtil.getEquivalentHostNames(fileName));
            } else {
                fileNameList.add(fileName);
            }
        }
        
        return fileNameList;
    }

    synchronized private void removeFromUndecided(String hostArg) {
        undecidedDnsCache.remove(hostArg);
    }

    synchronized private boolean exceededMaxAttempts(String hostArg) {
        Integer count = undecidedDnsCache.get(hostArg);
        boolean exceededCount = false;

        if (count == null) {
            undecidedDnsCache.put(hostArg, 1);
        } else if (count >= MAX_DNS_ATTEMPTS) {
            removeFromUndecided(hostArg);
            exceededCount = true;
        } else {
            undecidedDnsCache.put(hostArg, count+1);
        }

        return exceededCount;
    }

    /**
     * Returns some host information
     * 
     * @param hostArg
     *            host argument (as a string for both hostname or IP address)
     * @param hostName
     *            name of the host
     * @return host information
     * @throws UnknownHostException
     */
    private HostInfo getHostInfo(String hostArg) {
        HostInfo hostInfo;
        boolean isHostName = false;
        // Technically the ip address should fit in an int, but it can come
        // in either signed or unsigned (in which case we might need a long)
        long ip = 0;

        try {
            ip = Long.parseLong(hostArg);
        } catch (NumberFormatException e) {
            isHostName = true;
        }

        if (isHostName) {
            try {
                Element elem = getCachedDNSResult(hostArg);
                if (elem != null) {
                    hostInfo = (HostInfo)elem.getValue();
                } else {
                    boolean resolvedHostName = true;
                    InetAddress addr = InetAddress.getByName(hostArg);
                    String hostName  = addr.getCanonicalHostName().toLowerCase();
                    String hostIP    = addr.getHostAddress();
                    if (hostIP.equals (hostName)) {
                        resolvedHostName = false;
                    }
                    hostInfo = new HostInfo(hostName, hostIP);
                    elem = new Element(hostArg, hostInfo);
                    cacheDNSResult(elem, hostArg, resolvedHostName);
                }
            } catch (UnknownHostException e) {
                getLog().error("Invalid host name/identifier " + hostArg, e);
                hostInfo = new HostInfo (hostArg, "");
            }
        } else {
            byte[] hostAddress = new byte[4];
            for (int i = 24, j = 0; i >= 0; i -= 8, j++) {
                hostAddress[j] = new Long((ip >> i) & 0xff).byteValue();
            }
            if ((LOCALHOST_BYTE_ADDRESS == ip) || (ip == 0)) {
                hostInfo = new HostInfo(getControlManager().getHostName(), getControlManager().getHostIP());
            } else {
                Long longAddr = new Long(ip);
                try {
                    Element elem = getCachedDNSResult(longAddr);
                    if (elem != null) {
                        hostInfo = (HostInfo) elem.getValue();
                    } else {
                        InetAddress addr = InetAddress.getByAddress(hostAddress);
                        String hostName = addr.getCanonicalHostName().toLowerCase();
                        String hostIP = addr.getHostAddress();
                        boolean resolvedHostName = true;
                        if (hostIP.equals(hostName)) {
                            resolvedHostName = false;
                        }
                        hostInfo = new HostInfo(hostName, hostIP);
                        elem = new Element(longAddr, hostInfo);
                        cacheDNSResult(elem, hostArg, resolvedHostName);
                    }
                } catch (UnknownHostException e) {
                    getLog().error("Invalid host IP address", e);
                    hostInfo = new HostInfo("", "");
                }
            }
        }
        return hostInfo;
    }

    /*
     * We have two caches and a waiting list.  The resolved cache is
     * for names that we have found.  The unresolved cache is for
     * names that we've looked for a few times and not found, so we
     * won't bother looking for them in the future.  There is also an
     * undecided list, where we have the items that we have failed to
     * find, along with a count of how many times we have failed to
     * find them.
     *
     * TODO -
     * This is likely overengineered.  We could probably do this with
     * one cache.  We could also utilize this in the UNCUtil function
     * that looks up the host name.
     */

    /**
     * Look up element in the resolved and then unresolved caches
     * @param key the key (either a Long or a String)
     * @return the element from the cache (or null if error/it doesn't exist)
     */
    private Element getCachedDNSResult(Serializable key) {
        if (dnsCache == null || key == null) {
            return null;
        }

        try {
            Element element = dnsCache.get(key);
            if (element == null) {
                element = unresolvedDnsCache.get(key);
            }
            return element;
        } catch (CacheException ce) {
            getLog().warn("Unable to use dns cache (internal error).  Continuing as if element was not found.");
            return null;
        }
    }

    /**
     * Cache result of dns lookup.  If the entry was found it is cached in the resolved cache.
     * If it wasn't then we will see if it's bee unresolved too many times (the undecided list)
     * and, if so, move it into the unresolved cache.
     * @param elem the element to cache (includes both key and value)
     * @param hostArg the host identification (name or ip address)
     * @param wasResolved was the host information successfully resolved or not
     */
    private void cacheDNSResult(Element elem, String hostArg, boolean wasResolved) {
        if (dnsCache == null || unresolvedDnsCache == null) {
            return;
        }

        if (wasResolved) {
            dnsCache.put(elem);
            removeFromUndecided(hostArg);
        } else if (exceededMaxAttempts(hostArg)) {
            getLog().info("Unable to find " + hostArg + " after multiple attempts.  Putting in unresolved cache\n");
            unresolvedDnsCache.put(elem);
        }
    }

    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#getLog()
     */
    public Log getLog() {
        return log;
    }

    /**
     * @see com.bluejungle.framework.comp.IManagerEnabled#getManager()
     */
    public IComponentManager getManager() {
        return manager;
    }

    /**
     * Returns the ACPL policy container
     * 
     * @return the policy container
     */
    protected IAgentPolicyContainer getPolicyContainer() {
        return policyContainer;
    }

    /**
     * Returns the XACML policy container
     */
    protected IAgentXACMLPolicyContainer getXACMLPolicyContainer() {
        return xacmlPolicyContainer;
    }
    
    /**
     * Returns the subject manager
     * 
     * @return the subject manager
     */
    protected IDSubjectManager getSubjectManager() {
        return subjectManager;
    }

    /**
     * @see IInitializable#init()
     */
    public void init() {
        controlManager = getConfiguration().get(CONTROL_MANAGER_CONFIG_PARAM);
        if (getControlManager() == null) {
            throw new NullPointerException("Unable to get IControlManager component");
        }

        this.subjectManager = getManager().getComponent(IDSubjectManager.COMP_INFO);
        if (getSubjectManager() == null) {
            throw new NullPointerException("Unable to get IDSubjectManager component");
        }

        this.policyContainer = getConfiguration().get(AGENT_POLICY_CONTAINER_CONFIG_PARAM);
        if (getPolicyContainer() == null) {
            throw new NullPointerException("Unable to get IAgentPolicyContainer component");
        }

        this.xacmlPolicyContainer = getConfiguration().get(AGENT_XACML_POLICY_CONTAINER_CONFIG_PARAM);
        if (getXACMLPolicyContainer() == null) {
            throw new NullPointerException("Unable to get IAgentPolicyContainer component");
        }

        this.activityJournal = getConfiguration().get(ACTIVITY_JOURNAL_CONFIG_PARAM);
        if (getActivityJournal() == null) {
            throw new NullPointerException("Unable to get IActivityJournal component");
        }
        
        HashMapConfiguration obligationHandlerConfiguration = new HashMapConfiguration();
        obligationHandlerConfiguration.setProperty(IObligationHandler.CONTROL_MANAGER_NAME, getControlManager());
        obligationHandlerConfiguration.setProperty(IObligationHandler.ACTIVITY_JOURNAL_NAME, getActivityJournal());
        this.obligationHandler = getManager().getComponent(IObligationHandler.COMP_INFO, obligationHandlerConfiguration);
        if (getObligationHandler() == null) {
            throw new NullPointerException("Unable to get ObligationHandler component");
        }

        this.jwtVerifierManager = getManager().getComponent(JwtVerifierManager.class);
        if (getJwtVerifierManager() == null) {
            throw new NullPointerException("Unable to get IJwtVerifierManager");
        }
        
        String allowDontCareForAllQueries = System.getProperty(DONT_CARE_RESPONSE_ACCEPTABLE_KEY);
        if (allowDontCareForAllQueries != null) {
            dontCareAcceptable = Boolean.parseBoolean(allowDontCareForAllQueries);
        }

        String useResourceType = System.getProperty(USE_RESOURCE_TYPE_WHEN_EVALUATING_KEY);
        if (useResourceType != null) {
            useResourceTypeWhenEvaluating = Boolean.parseBoolean(useResourceType);
        }
        
        String allowErrorForAllQueries = System.getProperty(ERROR_RESPONSE_ACCEPTABLE_KEY);
        if (allowErrorForAllQueries != null) {
            errorAcceptable = Boolean.parseBoolean(allowErrorForAllQueries);
        }

        IBundleVault bundleVault = (IBundleVault) this.getManager().getComponent(IBundleVault.COMPONENT_NAME);

        //      setup EHCache
        CacheManager cacheManager;
        try {
            cacheManager = CacheManager.create();
        } catch (CacheException e) {
            if (getLog().isWarnEnabled()) {
                getLog().warn("Unable to create reverse DNS cache, proceeding in uncached mode.", e);
            }
            // no big deal, no caching
            return;
        }

        //This will get called a few times but should be ok.
        UNCUtil.setLocalHost(controlManager.getHostName());
        this.agentType = AgentTypeEnum.getAgentTypeEnum(getControlManager().getAgentType().getValue());

        this.dnsCache = cacheManager.getCache(CMRequestHandler.class.getName());
        if (this.dnsCache == null) {
            int resolvedDNSCacheTimeout = getControlManager().getResolvedDNSCacheTimeout();
            this.dnsCache = new Cache(CMRequestHandler.class.getName(), 20000, false, false, resolvedDNSCacheTimeout, 0);
            try {
                cacheManager.addCache(this.dnsCache);
            } catch (CacheException e1) {
                if (getLog().isWarnEnabled()) {
                    getLog().warn("unable to create reverse DNS cache, proceeding in uncached mode.", e1);
                }
                this.dnsCache = null;
            }
        }

        this.undecidedDnsCache = new HashMap<String, Integer>();

        this.unresolvedDnsCache = cacheManager.getCache(CMRequestHandler.class.getName() + UNRESOLVED_DNS_CACHE_SUFFIX);
        if (this.unresolvedDnsCache == null) {
            // XXX fix the hard-coded values
            this.unresolvedDnsCache = new Cache(CMRequestHandler.class.getName() + UNRESOLVED_DNS_CACHE_SUFFIX, 20000, false, false, 10 * 60, 0);
            try {
                cacheManager.addCache(this.unresolvedDnsCache);
            } catch (CacheException e1) {
                if (getLog().isWarnEnabled()) {
                    getLog().warn("unable to create (unresolved) reverse DNS cache, proceeding in uncached mode.", e1);
                }
                this.unresolvedDnsCache = null;
            }
        }

        final List<IPredicate> conditions = new ArrayList<IPredicate>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(IGNORED_RESOURCES_FILE));
            String resource = null;
            while ((resource = br.readLine()) != null) {
                conditions.add(ResourceAttribute.NAME.buildRelation(RelationOp.EQUALS, resource));
            }
            if (conditions.size() > 0) {
                this.ignoredResourceCondition = new CompositePredicate(BooleanOp.OR, conditions);
            }
        } catch (FileNotFoundException e2) {
            this.log.info("Resource.info file not found.");
        } catch (IOException e) {
            this.log.error("Resource.info file read error.", e);
        }
        
        policyActionParser = new PolicyActionParser();
    }

    /**
     * Convert input parameters to a string (to be printed later)
     * 
     * @param paramArray
     *            Parameter Array from C++ callback
     * @return void
     */
    private Supplier<String> packageInputParams(Long sequenceID, Map<String, DynamicAttributes> context, Long processToken, int loggingLevel, PerformObligationsEnumType performObligations) {
        return () -> {
            StringBuilder buffer = new StringBuilder("Request " + sequenceID + " input params\n");
            Set<String> keys = context.keySet();
            
            for (String name : keys) {
                buffer.append("  " + name + "\n");
                
                Set<String> dynKeys = context.get(name).keySet();
                
                for (String key : dynKeys) {
                    String[] vals = context.get(name).getStrings(key);
                    
                    buffer.append("\t" + key + ":");
                    for (String v : vals) {
                        buffer.append(" " + v);
                    }
                    buffer.append("\n");
                }
            }
            
            buffer.append("  Ignore obligation = " + performObligations.toString() + "\n");
            return buffer.toString();
        };
    }

    private int decodeLoggingLevel(Object[] paramArray)
    {
        int loggingLevel = 0;
        String loggingVal = (String)paramArray[LOG_LEVEL_INDEX];

        try {
            loggingLevel = Integer.decode(loggingVal).intValue();
        } catch (NumberFormatException e) {
            //Error in converting - defaulting to LOG_LEVEL_USER
            getLog().error("Error converting log level - value was " + loggingVal);
        } finally {
            //0 is invalid value - defaults to LOG_LEVEL_USER
            if (loggingLevel == 0) {
                loggingLevel = ILoggingLevel.LOG_LEVEL_USER;
            }
        }

        return loggingLevel;
    }

    private Long decodeProcessToken(Object[] paramArray) {
        String processVal = (String)paramArray[PROCESS_TOKEN_INDEX];
        Long processToken = null;

        try {
            processToken = Long.decode(processVal);
        } catch (NumberFormatException e) {
            getLog().error("Error converting process token - value was " + processVal);
        }

        return processToken;
    }

    private DynamicAttributes decodeAttributes(Object[] attrArray) {
        DynamicAttributes attrs = new DynamicAttributes();

        for (int i = 0; i < attrArray.length; i+=2) {
            String key = (String)attrArray[i];
            try {
                String value = (String)attrArray[i+1];
                attrs.add(key.toLowerCase(), value);
            } catch (ArrayIndexOutOfBoundsException e) {
                getLog().error("Found key " + key + ", but no value");
            }
        }

        return attrs;
    }

    private String decodeResolvedFromFileName(DynamicAttributes fromContext, String fromFileName) {
        String resolvedFromFileName = fromContext.getString(SpecAttribute.RESOLVEDNAME_ATTR_NAME);

        if (resolvedFromFileName == null || resolvedFromFileName.length() == 0) {
            resolvedFromFileName = fromFileName;
        }

        return (OSType.getSystemOS() == OSType.OS_LINUX) ? resolvedFromFileName : resolvedFromFileName.toLowerCase();
    }

    private boolean hostNameResolutionRequested(DynamicAttributes attrs) {
        // Anything other than an explicit "no" is a "yes"
        return (!("no".equals(attrs.getString(SpecAttribute.GET_EQUIVALENT_HOST_NAMES))));
    }

    // TODO - We shouldn't need separate code from from and to resources
    private List<String> buildEquivalentToNamesList(DynamicAttributes toContext, String toFileName) {
        List<String> toFileNameList = null;

        boolean performResolveNames = hostNameResolutionRequested(toContext);

        if (AgentTypeEnum.DESKTOP.equals(getAgentType())) {
            toFileNameList = getEquivalentFileNames(toFileName, performResolveNames);
            if (toFileNameList != null) {
                String resolvedToFileName = toContext.getString(SpecAttribute.RESOLVEDNAME_ATTR_NAME);

                if (resolvedToFileName == null || resolvedToFileName.length() == 0) {
                    resolvedToFileName = toFileName;
                } else if (!toFileName.equals(resolvedToFileName)) {
                    /* A distinct resolved file name, should be added to the filelist for evaulation */
                    toFileNameList.addAll(getEquivalentFileNames (resolvedToFileName, performResolveNames));
                }
                String localToFileName = null;
                if (performResolveNames) {
                    localToFileName = UNCUtil.resolveLocalPath(resolvedToFileName);
                }
                if (localToFileName != null) {
                    toFileNameList.add(localToFileName);
                }
            }
        } else {
            // At least fill out one element for the File server agent
            toFileNameList = getEquivalentFileNames(toFileName, performResolveNames);
        }

        return toFileNameList;
    }

    private void expandResourceURL(DynamicAttributes resourceContext) {
        if (hostNameResolutionRequested(resourceContext)) {
            // This should only appear once, but there is no way to ensure that.  Hence getStrings().
            String[] resourceURLs = resourceContext.getStrings("url");
            
            if (resourceURLs.length > 0) {
                List<String> equivalentURLs = new ArrayList<String>();
                for (String url : resourceURLs) {
                    equivalentURLs.addAll(UNCUtil.getEquivalentHostNames(url));
                }
                
                resourceContext.put("url", equivalentURLs);
            }
        }
    }

    private Map<String, DynamicAttributes> decodeAllDynamicArgs(Object[] paramArray) {
        Map<String, DynamicAttributes> ret = new HashMap<String, DynamicAttributes>();
        Object[] dimensions = (Object[])paramArray[DIMENSION_DEFS_INDEX];

        for (int i = 0; i < dimensions.length; i++) {
            ret.put((String)dimensions[i], decodeAttributes((Object[])paramArray[i+DIMENSION_DEFS_INDEX+1]));
        }

        return ret;
    }
 
    private void addFileListToAttr(List<String> fileNameList, DynamicAttributes context, String resourceDestinyType)
    {
        if (context != DynamicAttributes.EMPTY && fileNameList != null) {
            if (resourceDestinyType.equals("fso")) {
                fileNameList = canonicalizeResources(fileNameList, resourceDestinyType);
            }
            context.put(SpecAttribute.NAME_ATTR_NAME, fileNameList);
        }
    }

    private boolean accessDeniedByPEP(DynamicAttributes context) {
        if (context == null) {
            return false;
        }

        String decision = context.getString("tamper-resistance-decision");

        if (decision == null || !decision.equals("deny")) {
            return false;
        }

        return true;
    }

    private static DynamicAttributes getEnvironmentContext(Map<String, DynamicAttributes> context) {
        DynamicAttributes environmentContext = context.get("environment");
        
        if (environmentContext == null) {
            environmentContext = new DynamicAttributes();
            context.put("environment", environmentContext);
        }
        
        return environmentContext;
    }

    private void addHeartbeatInfo(DynamicAttributes environmentContext) {
        environmentContext.add("time_since_last_successful_heartbeat", String.valueOf(getControlManager().getTimeSinceLastSuccessfulHeartbeat()));
    }

    private boolean isDontCareAcceptable(DynamicAttributes environmentContext) {
        // If the request has a preference, use it, otherwise use the global setting
        String requestDontCareFlag = environmentContext.getString(DONT_CARE_ACCEPTABLE_KEY);

        if (requestDontCareFlag == null) {
            return dontCareAcceptable;
        } else {
            return !requestDontCareFlag.equals("no");
        }
    }
    
    private boolean isErrorAcceptable(DynamicAttributes environmentContext) {
        // If the request has a preference, use it, otherwise use the global setting
        String requestErrorFlag = environmentContext.getString(ERROR_RESULT_ACCEPTABLE_KEY);

        if (requestErrorFlag == null) {
            return errorAcceptable;
        } else {
            return !requestErrorFlag.equals("no");
        }
    }

    private int getLoggingLevel(DynamicAttributes environmentContext) {
        String level = environmentContext.getString(LOGGING_LEVEL_KEY);

        if (level == null) {
            return ILoggingLevel.LOG_LEVEL_USER;
        }

        try {
            return Integer.parseInt(level);
        } catch (NumberFormatException nfe) {
            return ILoggingLevel.LOG_LEVEL_USER;
        }
    }

    private Long getProcessToken(DynamicAttributes environmentContext) {
        String token = environmentContext.getString(PROCESS_TOKEN_KEY);

        if (token == null) {
            return 0L;
        }

        try {
            return Long.parseLong(token);
        } catch (NumberFormatException nfe) {
            return 0L;
        }
    }

    private PerformObligationsEnumType getPerformObligations(DynamicAttributes environmentContext) {
        String val = environmentContext.getString(PERFORM_OBLIGATIONS_KEY);

        if (val == null || PerformObligationsEnumType.getByName(val) == null) {
            return PerformObligationsEnumType.ALL;
        }
        
        return PerformObligationsEnumType.getByName(val);
    }
    
    private void addCacheHint(PolicyEvaluationResult policyEvaluationResult,
                              DynamicAttributes resourceContext,
                              boolean computeValue) {
        String cacheHint = resourceContext.getString(SpecAttribute.REQUEST_CACHE_HINT);
        
        if (cacheHint != null && cacheHint.equals("yes")) {
            long cacheHintValue = 0;
            if (computeValue) {
                cacheHintValue = getControlManager().getSDKCacheHintValue() ;
                long timeToNextHeartbeat = getControlManager().getSecondsUntilNextHeartbeat();
                
                if (cacheHintValue > timeToNextHeartbeat) {
                    cacheHintValue = timeToNextHeartbeat;
                }
            }

            policyEvaluationResult.setCacheHint(cacheHintValue);
        }
    }

    private long generateRequestID() {
        return System.nanoTime();
    }

    private Set<String> attributePrefixesToStrip = null;

    private synchronized void initializeAttributePrefixes() {
        if (attributePrefixesToStrip != null) {
            return;
        }

        attributePrefixesToStrip = new HashSet<String>();

        String prefixes = controlManager.getProperty("ignored.attr.prefixes");

        if (prefixes != null) {
            for (String prefix : prefixes.split(",")) {
                attributePrefixesToStrip.add(prefix + (prefix.endsWith(":") ? "" : ":"));
            }
        }
    }
    
    private void rewriteContext(DynamicAttributes attrs) {
        Set<Pair<String, String>> rewrite = new HashSet<>();

        initializeAttributePrefixes();
        
        if (attributePrefixesToStrip.isEmpty()) {
            return;
        }
        
        for (String key : attrs.keySet()) {
            for (String prefix : attributePrefixesToStrip) {
                if (key.startsWith(prefix)) {
                    rewrite.add(new Pair<String, String>(key, prefix));
                    break;
                }
            }
        }

        if (rewrite.isEmpty()) {
            return;
        }

        for (Pair<String, String> keyAndPrefix : rewrite) {
            String key = keyAndPrefix.first();
            String prefix = keyAndPrefix.second();
            
            IEvalValue val = attrs.remove(key);

            attrs.put(key.substring(prefix.length()), val);
        }

        return;
    }

    @Override
    public IWrappedXACMLResponse queryXacmlDecisionEngine(String request, String dataType) {
        final XACMLEvaluationResult result = xacmlPolicyContainer.evaluate(request, dataType);

        for (final IDObligation obligation : result.getObligations()) {
            getObligationHandler().addTask(new Runnable() {
                @Override
                public void run() {
                    if (obligation instanceof AgentLogObligation) {
                        if (!activityJournal.isEnabled()) {
                            getLog().warn("Unable to create XACML log as ActivityJournal is not enabled (agent not registered?)");
                            return;
                        }
                        
                        AgentLogObligation logObligation = (AgentLogObligation) obligation;
                        
                        Long policyId = logObligation.getPolicy().getId();
                        if (policyId == null) {
                            policyId = UNKNOWN_POLICY_ID;
                        }
                        
                        PolicyActivityLogEntryV5 entry = new PolicyActivityLogEntryV5(result.getPolicyActivityInfo(),
                                                                                      policyId,
                                                                                      activityJournal.getLogIdGenerator().getNextId());
                        
                        logObligation.getExecutor().logActivity(entry);
                    }
                }
            });
        }
        
        return result.getXACMLResponse();
    }

    @Override
    public boolean queryPermissionsEngine(Object[] paramArray, List resultParamArray) {
        Map<String, DynamicAttributes> context = decodeAllDynamicArgs(paramArray);

        IPDPPermissionsResponse response = queryPermissionsEngine(context);

        ArrayList<String> effectActionEnumerations = new ArrayList<>();
        ArrayList<ArrayList<String>> permissionsDetails = new ArrayList<>();
        
        for (EffectType effectType : EffectType.elements()) {
            String effect = effectType.getName();

            for (String action: response.getPermittedActionsForEffect(effect)) {
                effectActionEnumerations.add(effect + ":" + action + ":policies");
                effectActionEnumerations.add(effect + ":" + action + ":obligations");

                permissionsDetails.add(new ArrayList<String>(response.getPoliciesForAction(effect, action)));

                ArrayList<String> allObligationsDetails = new ArrayList<>();

                addObligationDetails(response.getObligationsForAction(effect, action), allObligationsDetails);
                permissionsDetails.add(allObligationsDetails);
            }
        }

        resultParamArray.add(effectActionEnumerations);
        resultParamArray.add(permissionsDetails);

        return true;
    }
    
    @Override
    public IPDPPermissionsResponse queryPermissionsEngine(Map<String, DynamicAttributes> context) {
        IPDPPermissionsResponse response = new PDPPermissionsResponse();

        DynamicAttributes environmentCtx = context.get("environment");
        boolean useResourceTypeWhenEvaluating = true;
        if (environmentCtx != null && "false".equals(environmentCtx.getString(USE_RESOURCE_TYPE_WHEN_EVALUATING_KEY))) {
            useResourceTypeWhenEvaluating = false;
        }

        DynamicAttributes resourceContext = context.get("from");
        if (resourceContext == null) {
            throw new IllegalArgumentException("No [from] context in permissions query");
        }

        String resourceType = resourceContext.getString(SpecAttribute.DESTINYTYPE_ATTR_NAME);
        
        Set<String> knownActions = useResourceTypeWhenEvaluating ?
                                   policyContainer.getBundlePolicyActionsForType(resourceType):
                                   policyContainer.getBundlePolicyActions();

        // Check for ad-hoc policies
        DynamicAttributes extraPolicies = context.get("policies");
        if (extraPolicies != null) {
            String pql = extraPolicies.getString("pql");

            knownActions.addAll(policyActionParser.getActions(pql));
        }
        
        // Make multiple queries, one for each action. This is not
        // especially efficient, but probably isn't too bad
        //
        // TODO - A more efficient solution might go as follows:
        // * Evaluate each policy, ignoring the action
        // * If the policy denies, add its actions to deniedSet and remove them from allowedSet
        // * If the policy allows, add all actions not in deniedSet to allowedSet
        // * allowedSet contains the actions the user can perform
        for (String action : knownActions) {
            context.remove("action");
            DynamicAttributes actionMap = new DynamicAttributes(1);
            actionMap.put("name", action);
            context.put("action", actionMap);

            List<Object> resultParamArray = new ArrayList<>();

            IPolicyEvaluationResult policyEvaluationResult = queryDecisionEngine(context);

            String effect = policyEvaluationResult.getEffectName();

            response.addAction(effect, action, policyEvaluationResult.getObligations(), policyEvaluationResult.getMatchingPolicies(effect));
        }

        return response;
    }

    @Override
    public boolean queryDecisionEngine(Object[] paramArray, List resultParamArray) {
        Map<String, DynamicAttributes> context = decodeAllDynamicArgs(paramArray);
        
        Long processToken = decodeProcessToken(paramArray);
        int loggingLevel = decodeLoggingLevel(paramArray);
        boolean ignoreObligations = Boolean.valueOf(((String) paramArray[IGNORE_OBLIGATIONS_INDEX])).booleanValue();

        return queryDecisionEngine(context, processToken, loggingLevel, ignoreObligations, resultParamArray);
    }

    @Override
    public boolean queryDecisionEngine(Map<String, DynamicAttributes> context, Long processToken, int loggingLevel, boolean ignoreObligations, List resultParamArray) {

        if (processToken != null && processToken != 0) {
            getEnvironmentContext(context).add(PROCESS_TOKEN_KEY, String.valueOf(processToken));
        }

        getEnvironmentContext(context).add(LOGGING_LEVEL_KEY, String.valueOf(loggingLevel));

        if (!getEnvironmentContext(context).containsKey(PERFORM_OBLIGATIONS_KEY)) {
            getEnvironmentContext(context).add(PERFORM_OBLIGATIONS_KEY, ignoreObligations ? "none" : "all");
        }
        
        IPolicyEvaluationResult result = queryDecisionEngine(context);

        resultParamArray.add(result.getEffectName());

        List<String> resultArguments = new ArrayList<>();

        if (result.getCacheHint() >= 0) {
            resultArguments.add("CE_CACHE_HINT");
            resultArguments.add(Long.toString(result.getCacheHint()));
        }

        addObligationDetails(result.getObligations(), resultArguments);
        
        resultParamArray.add(resultArguments.toArray(new String[resultArguments.size()]));
        
        return true;
    }
    
    /**
     * @see com.bluejungle.destiny.agent.controlmanager.IPolicyEvaluator#queryDecisionEngine(java.util.ArrayList,
     *      java.util.ArrayList)
     */
    // public IPolicyEvaluationResult queryDecisionEngine(Map<String, DynamicAttributes> context, Long processToken, int loggingLevel, boolean ignoreObligations) {
    @Override
    public IPolicyEvaluationResult queryDecisionEngine(Map<String, DynamicAttributes> context) {
        PolicyEvaluationResult policyEvaluationResult = new PolicyEvaluationResult(EvaluationResult.ALLOW);

        long enterTime = System.nanoTime();
        Supplier<String> inputParamsAsString = () -> "";

        ProcessTokenWrapper processTokenWrapper = ProcessTokenWrapper.NONE;

        for (DynamicAttributes attrs : context.values()) {
            rewriteContext(attrs);
        }
        
        try {
            DynamicAttributes environmentContext = getEnvironmentContext(context);
            errorAcceptable = isErrorAcceptable(environmentContext);
            int loggingLevel = getLoggingLevel(environmentContext);
            Long processToken = getProcessToken(environmentContext);
            PerformObligationsEnumType performObligations = getPerformObligations(environmentContext);
            addHeartbeatInfo(environmentContext);

            String useResourceType = environmentContext.getString("use_resource_type_when_evaluating");

            if (useResourceType == null) {
                environmentContext.add(IAgentPolicyContainer.USE_RESOURCE_TYPE_WHEN_EVALUATING_KEY, useResourceTypeWhenEvaluating ? "true" : "false");
            }
            
            long requestID = generateRequestID();
            environmentContext.put("request_id", EvalValue.build(Long.toString(requestID)));

            if (getLog().isInfoEnabled()) {
                inputParamsAsString = packageInputParams(requestID, context, processToken, loggingLevel, performObligations);
            }
            
            String actionName = context.get("action").getString("name");
            if (actionName.equals("NEW_EMAIL") ||
                actionName.equals("FORWARD") ||
                actionName.equals("REPLY")) {
                actionName = "EMAIL";
                context.get("action").put("name", actionName);
            }


            DynamicAttributes appContext = context.get("application");
            String appName = null;
            if (appContext != null) {
                appName = appContext.getString("name");
                
                // Mark the process as trusted unless this is a RUN action.  RUN actions come from the procdetect
                // driver and we'll get that even if there are no other enforcers on the system
                String pid = appContext.getString("pid");
                if (pid != null && !actionName.equals("RUN")) {
                    try {
                        getControlManager().getTrustedProcessManager().addTrustedProcess(Integer.parseInt(pid));
                    } catch (NumberFormatException e) {
                    }
                }
            }

            if (appName == null) {
                appName = "";
            }

            // Special query
            if (actionName.equals(PDPSDK.MONITOR_APP_ACTION)) {
                String effect = EvaluationResult.ALLOW;

                if (policyContainer.isApplicationIgnorable(appName)) {
                    effect = EvaluationResult.DENY;
                }

                policyEvaluationResult.setEffectName(effect);
                getLog().info(inputParamsAsString.get() + "  Result: " + effect);
                return policyEvaluationResult;
            }

            DynamicAttributes userContext = context.get("user");
            if (userContext.getString("jwt") != null) {
                userContext = decodeJWT(userContext);
            }
            String userId = userContext.getString("id");
            String userName = userContext.getString("name");

            if (userName == null || userName.equals("")) {
                if (userId == null || userId.equals("")) {
                    // Problem - no user information at all
                    userId = "S-1-0-0";
                    userContext.put("id", userId);

                    userName = "Nobody";
                } else {
                    userName = UserNameCache.getUserName(userId);
                }
                userContext.put("name", userName);
            }

            processTokenWrapper = ProcessTokenMap.substituteToken(userId, appName, processToken);

            // do not enforce policies for local system user.
            if (LOCAL_SYSTEM_USER.equals(userId)) {
                return policyEvaluationResult;
            }

            // FIXME This is a hack to get the integration going
            if (context.get("action") != null) {
                String tmp = context.get("action").getString("name");
                if ("NEW_EMAIL".equals(tmp)
                    || "FORWARD".equals(tmp)
                    || "REPLY".equals(tmp)) {
                    actionName = "EMAIL";
                    context.get("action").put("name", actionName);
                }
            }

            if ("CLOSE".equals(actionName)) {
                return policyEvaluationResult;
            }

            DynamicAttributes fromContext = context.get("from");
            String fromResourceDestinyType = fromContext.getString(SpecAttribute.DESTINYTYPE_ATTR_NAME);

            // Fix host name in "url", if attribute exists
            expandResourceURL(fromContext);

            String fromFileName = fromContext.getString(SpecAttribute.ID_ATTR_NAME);

            if (OSType.getSystemOS() == OSType.OS_LINUX) {
                fromContext.put(SpecAttribute.ORIG_ATTR_NAME, canonicalizeFileName(fromFileName, fromResourceDestinyType));
            } else {
                fromFileName = fromFileName.toLowerCase();
            }

            DynamicAttributes hostContext = context.get("host");
            String hostIP = hostContext.getString("inet_address");
            String hostName = hostContext.getString("name");

            if (hostName == null || hostIP == null) {
                if (getControlManager().resolveHostInformation()) {
                    getLog().debug("Resolving host name info");
                    HostInfo hostInfo = getHostInfo(hostIP);
                    
                    hostName = hostInfo.getHostName();
                    hostContext.put("name", hostName);
                    
                    hostIP = hostInfo.getHostIP();
                    hostContext.put("inet_address", hostIP);
                } else {
                    getLog().debug("Not resolving host name info");
                    long ip = 0;
                    try {
                        ip = Long.parseLong(hostIP);
                    } catch (NumberFormatException e) {
                    }
                    if (ip == 0) {
                        ip = LOCALHOST_BYTE_ADDRESS;
                    }
                    String dottedIP = HostInfo.toIP(ip);
                    hostContext.put("inet_address", dottedIP);
                    hostContext.put("name", dottedIP);
                }
            }


            DynamicAttributes toContext = context.get("to");

            if (toContext == null) {
                toContext = DynamicAttributes.EMPTY;
            }

            // Fix host name in "url", if attribute exists
            expandResourceURL(toContext);

            String resolvedFromFileName = decodeResolvedFromFileName(fromContext, fromFileName);

            String localFromFileName = null;
            if (AgentTypeEnum.DESKTOP.equals(getAgentType())) {
                localFromFileName = UNCUtil.resolveLocalPath(resolvedFromFileName);
            }

            String nameToUse;
            if (localFromFileName != null) {
                nameToUse = localFromFileName;
            } else if (resolvedFromFileName.charAt(0) != '[') {
                nameToUse = resolvedFromFileName;
            } else {
                nameToUse = fromFileName;
            }

            nameToUse = nameToUse.toLowerCase();

            String toResourceDestinyType = toContext.getString(SpecAttribute.DESTINYTYPE_ATTR_NAME);

            String canonicalizedFromFileName = canonicalizeFileName(nameToUse, fromResourceDestinyType);

            // A couple of names indicate that we aren't actually looking at a real file.  We should mark it
            // accordingly.
            if (canonicalizedFromFileName.endsWith("*") || canonicalizedFromFileName.equals(NO_ATTACHMENT)) {
                fromContext.put(SpecAttribute.FSCHECK_ATTR_NAME, "no");
                // If the file doesn't exist then it really shouldn't have a modified date.  It shouldn't have
                // a modified date either, but that's never passed in by the enforcer, so I'm going to ignore that
                fromContext.remove("modified_date");
            }

            boolean resourceAccessDeniedByPEP = accessDeniedByPEP(fromContext);

            if (!resourceAccessDeniedByPEP && this.ignoredResourceCondition != null) {
                IMResource resource = new Resource(canonicalizedFromFileName);
                resource.setAttribute(SpecAttribute.NAME_ATTR_NAME, canonicalizedFromFileName);
                EvaluationRequest request = new EvaluationRequest(resource);
                
                if (this.ignoredResourceCondition.match(request)) {
                    getLog().debug("Action ignored for: " + fromFileName);
                    return policyEvaluationResult;
                }
            }

            final String toFileName = toContext.getString(SpecAttribute.ID_ATTR_NAME);

            String effect = null;
            ActionEnumType action = ActionEnumType.getActionEnum(actionName);
            boolean isTracked = false;

            if (resourceAccessDeniedByPEP) {
                isTracked = true;
                effect = EvaluationResult.DENY;
            } else {
                if (loggingLevel == ILoggingLevel.LOG_LEVEL_SYSTEM) {
                    effect = EvaluationResult.ALLOW;
                }
                isTracked = getControlManager().isTrackedAction(action);
            }

            List<String> fromFileNameList = getEquivalentFileNames(fromFileName, hostNameResolutionRequested(fromContext));
            if (!fromFileName.equals(resolvedFromFileName)) {
                fromFileNameList.addAll(getEquivalentFileNames(resolvedFromFileName, hostNameResolutionRequested(fromContext)));
            }
            if (localFromFileName != null) {
                fromFileNameList.add(localFromFileName);
            }

            List<String> toFileNameList = buildEquivalentToNamesList(toContext, toFileName);

            long querySetupTime = 0;
            long obligationExecutionTime = 0;

            //skip if we already know the effect
            if (effect == null) {
                if (action.equals(ActionEnumType.ACTION_STOP_AGENT)) {
                    effect = EvaluationResult.DENY;
                    getLog().error("Agent Termination Attempted");
                } else {
                    addFileListToAttr(fromFileNameList, fromContext, fromResourceDestinyType);

                    if (toFileNameList != null) {
                        addFileListToAttr(toFileNameList, toContext, toResourceDestinyType);
                    } else {
                        toContext = DynamicAttributes.EMPTY;
                    }

                    querySetupTime = (System.nanoTime() - enterTime)/1000000;

                    //Do the real policy evaluation
                    EvaluationResult result = getPolicyContainer().evaluate(
                        requestID
                        ,   AgentTypeEnumType.getAgentType(getAgentType().getName())
                        ,   processTokenWrapper.getToken()
                        ,   context
                        ,   System.currentTimeMillis()
                        ,   loggingLevel
                        ,   performObligations != PerformObligationsEnumType.NONE);
                    effect = result.getEffectName();

                    policyEvaluationResult.setMatchingPolicies(result.getApplicablePolicies());
                    
                    long obligationStart = System.nanoTime();

                    List<IDObligation> obligations = result.getObligations();

                    if (result.getPAInfo() != null) {
                        IEvalValue url = context.get("application") != null ? context.get("application").get("url") : null;

                        if (url != null) {
                            result.getPAInfo().addAttribute("RF", "URL", url);
                        }

                        DynamicAttributes sendTo = context.get("sendto");
                        if (sendTo != null) {
                            IEvalValue val = sendTo.get("email");
                            
                            if (val != null) {
                            	result.getPAInfo().addAttribute("RF", "Sent to", val);
                            }
                        }
                    }

                    // Cache hint only goes in if there are no obligations and no tracking activity logs
                    addCacheHint(policyEvaluationResult, fromContext, (performObligations != PerformObligationsEnumType.NONE || obligations == null || obligations.size() == 0) && !isTracked);

                    if (performObligations != PerformObligationsEnumType.NONE && result.getObligations() != null) {
                        ObligationContext obligationContext = new ObligationContext(policyEvaluationResult, result, performObligations);
                        getObligationHandler().executeObligations(obligationContext);
                    }
                    
                    obligationExecutionTime = (System.nanoTime() - obligationStart)/1000000;
                }
            } else {
                // Skipped evaluation, so no obligations.  However, we might still have tracking logs
                addCacheHint(policyEvaluationResult, fromContext, !isTracked);
            }

            // Only return "don't care" if the PEP says that it understand it
            if (effect.equals(EvaluationResult.DONT_CARE) && !isDontCareAcceptable(environmentContext)) {
                effect = EvaluationResult.ALLOW;
            }

            policyEvaluationResult.setEffectName(effect);

            if (performObligations != PerformObligationsEnumType.NONE && isTracked) {
                IDSubject user = getSubjectManager().getSubject(userId, SubjectType.USER);
                Long userSubjectId = user != null ? user.getId() : IDSubject.UNKNOWN_ID;
                IDSubject host = getSubjectManager().getSubject(hostName, SubjectType.HOST);
                Long hostSubjectId = host != null ? host.getId() : IDSubject.UNKNOWN_ID;
                IDSubject application = getSubjectManager().getSubject(appName, SubjectType.APP);
                Long appSubjectId = application != null ? application.getId() : IDSubject.UNKNOWN_ID;

                getControlManager().logTrackingActivity(action, userSubjectId, userName, hostSubjectId, hostName, hostIP, appSubjectId, appName, canonicalizedFromFileName, fromContext, canonicalizeFileName(toFileName, toResourceDestinyType), null, loggingLevel);
            }

            if (getLog().isInfoEnabled()) {
                long elapsedTime = (System.nanoTime() - enterTime)/1000000;
                StringBuilder sb = new StringBuilder(inputParamsAsString.get());
                sb.append("  Result: Effect = ");
                sb.append(effect);
                sb.append(" (total:");
                sb.append(elapsedTime);
                sb.append("ms, setup:");
                sb.append(querySetupTime);
                sb.append("ms, obligations:");
                sb.append(obligationExecutionTime);
                sb.append("ms)\n");

                for (IObligationResultData obligation : policyEvaluationResult.getObligations()) {
                    sb.append(obligation);
                }
                
                sb.append("  From file list: " + fromFileNameList);
                sb.append("\n  To filename list: " + toFileNameList);

                getLog().info(sb.toString());
            }
        } catch (Throwable e) {
            getLog().error(inputParamsAsString.get() + "  Result: Exception thrown\n", e);
            
            // Only return "error" if the PEP says that it understand it otherwise 'allow' so the application
            // doesn't hang. We have to be careful here. Shouldn't add any code
            // that throws exceptions that should be caught explicitly
			if (errorAcceptable) {
				policyEvaluationResult.setEffectName(EvaluationResult.ERROR);
			}
        } finally {
            processTokenWrapper.closeProcessToken();
        }

        return policyEvaluationResult;
    }

    /**
     * @see com.bluejungle.framework.comp.IConfigurable#setConfiguration(com.bluejungle.framework.comp.IConfiguration)
     */
    public void setConfiguration(IConfiguration config) {
        this.configuration = config;
    }

    /**
     * @see com.bluejungle.framework.comp.ILogEnabled#setLog(org.apache.commons.logging.Log)
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @see com.bluejungle.framework.comp.IManagerEnabled#setManager(com.bluejungle.framework.comp.IComponentManager)
     */
    public void setManager(IComponentManager manager) {
        this.manager = manager;
    }

    /**
     * Take obligation information and add it to the resultArguments
     * in the horribly convoluted format that we prefer
     */
    private void addObligationDetails(Collection<IObligationResultData> obligations, List<String> resultArguments) {
        if (obligations.size() > 0) {
            resultArguments.add("CE_ATTR_OBLIGATION_COUNT");
            resultArguments.add(Integer.toString(obligations.size()));

            int obligationIndex = 1;
            for (IObligationResultData obligation : obligations) {
                resultArguments.add("CE_ATTR_OBLIGATION_NAME:" + obligationIndex);
                resultArguments.add(obligation.getObligationName());
                resultArguments.add("CE_ATTR_OBLIGATION_POLICY:" + obligationIndex);
                resultArguments.add(obligation.getPolicyName());

                if (obligation.getArguments().size() > 0) {
                    int argIndex = 1;
                    for (String arg : obligation.getArguments()) {
                        if (obligation.getObligationName().equals("CE::NOTIFY")) {
                            // Legacy format for SPE
                            resultArguments.add("CE_ATTR_OBLIGATION_VALUE:" + obligationIndex);
                            resultArguments.add(arg);
                        } 
                        resultArguments.add("CE_ATTR_OBLIGATION_VALUE:" + obligationIndex + ":" + argIndex++);
                        resultArguments.add(arg);
                    }
                    
                    resultArguments.add("CE_ATTR_OBLIGATION_NUMVALUES:" + obligationIndex);
                    resultArguments.add(Integer.toString(obligation.getArguments().size()));
                }

                obligationIndex++;
            }
        }
        return;
    }
    
    private DynamicAttributes decodeJWT(DynamicAttributes userContext) throws JWTVerificationException {
        String jwtString = userContext.getString("jwt");

        if (jwtString == null) {
            return userContext;
        }

        try {
            DecodedJWT jwt = JWT.decode(jwtString);

            String subject = jwt.getSubject();
            String algorithm = jwt.getAlgorithm();

            if (subject != null && !subject.equals("")) {
                getLog().info("JWT: New user " + subject);
                userContext.put("id", subject);
            }

            JWTVerifier verifier = getJwtVerifierManager().getVerifier(algorithm, subject);
            if (verifier == null) {
                throw new JWTVerificationException("Unable to find verifier for " + subject + " with algorithm " + algorithm);
            }

            verifier.verify(jwt);
            
            for (Map.Entry<String, Claim> claimEntry : jwt.getClaims().entrySet()) {
                String key = claimEntry.getKey();
                Claim claim = claimEntry.getValue();
                
                // There is no way to determine the type of a
                // claim. We have to try to retrieve it as a
                // particular type and see if it succeeds
                if (claim.asString() != null) {
                    getLog().info("JWT: Extracted " + key + "=" + claim.asString());
                    userContext.put(key, claim.asString());
                } else if (claim.asList(String.class) != null) {
                    getLog().info("JWT: Extracted " + key + "= [" + StringUtils.join(claim.asList(String.class), ", ") + "]");
                    userContext.put(key, claim.asList(String.class));
                } else if (claim.asArray(String.class) != null) {
                    getLog().info("JWT: Extracted " + key + "= [" + StringUtils.join(claim.asArray(String.class), ", ") + "]");
                    userContext.put(key, claim.asArray(String.class));
                }
            }
            
            return userContext;
        } catch (JWTDecodeException e) {
            return userContext;
        }

    }
}

