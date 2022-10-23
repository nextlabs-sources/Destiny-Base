package com.nextlabs.pf.engine.destiny;

/*
 * Created on Dec 05, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.IManagerEnabled;
import com.bluejungle.framework.heartbeat.IHeartbeatListener;
import com.bluejungle.framework.heartbeat.IHeartbeatManager;
import com.bluejungle.framework.securestore.ISecureStore;
import com.bluejungle.framework.securestore.SecureFileStore;
import com.bluejungle.framework.utils.IPair;
import com.bluejungle.framework.utils.SerializationUtils;
import com.bluejungle.pf.domain.destiny.obligation.AgentObligationManager;
import com.bluejungle.pf.domain.destiny.obligation.LogObligation;
import com.bluejungle.pf.domain.destiny.policy.IDPolicyManager;
import com.nextlabs.domain.log.PolicyActivityInfoV5;
import com.nextlabs.pf.destiny.lib.XACMLPoliciesRequestDTO;
import com.nextlabs.pf.destiny.lib.XACMLPoliciesResponseDTO;
import com.nextlabs.pf.engine.destiny.WrappedXACMLResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;

import org.wso2.balana.Balana;
import org.wso2.balana.ConfigurationStore;
import org.wso2.balana.ParsingException;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.RequestCtxFactory;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.ctx.xacml3.Result;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;

public class AgentXACMLPolicyContainer implements IAgentXACMLPolicyContainer, IInitializable, IManagerEnabled, ILogEnabled, IConfigurable, IHeartbeatListener {
    private static final String DEFAULT_BASE_DIR = ".";
    private static final String BALANA_CONFIG_FILE = "config/xacmlpdpconfig.xml";
    private IComponentManager manager;
    private IConfiguration config;
    private Log log;
    private PDP pdpInstance = null;
    private AgentObligationManager obligationManager = null;
    private IDPolicyManager policyManager = null;
    private XACMLPoliciesResponseDTO activeResponse;
    private String policiesFileName;
    private Map<String, Long> policyNameIdMap;
    
    public void init() {
        log.info("Initializing XACML policy container");
        
        IConfiguration configuration = getConfiguration();

        String baseDir = (configuration != null) ? configuration.get(BASE_DIR_PROPERTY_NAME, DEFAULT_BASE_DIR) : DEFAULT_BASE_DIR;
        System.setProperty(ConfigurationStore.PDP_CONFIG_PROPERTY, baseDir + File.separator + BALANA_CONFIG_FILE);
        
        policiesFileName = baseDir + File.separator + "xacmlpolicies.bin";

        log.info("Getting obligation manager");
        obligationManager = getManager().getComponent(AgentObligationManager.COMP_INFO);

        log.info("Getting policy manager");
        policyManager = getManager().getComponent(IDPolicyManager.COMP_INFO);

        try {
            log.info("Reading saved XACML policies bundle");
            activeResponse = readResponseDTO();
            log.info("Done reading XACML policies bundle");
        } catch (IOException ioe) {
            log.info("No active XACML policies");
        }
        
        if (activeResponse == null) {
            // Dummy data that we can use until we get something real
            activeResponse = new XACMLPoliciesResponseDTO(new Date(0));
        }

        buildIdMap();
        
        log.info("Initializing XACML heartbeat plug-in");
        IHeartbeatManager heartbeatManager = (IHeartbeatManager)manager.getComponent(IHeartbeatManager.COMP_INFO);
        heartbeatManager.register(XACMLPoliciesRequestDTO.XACML_POLICIES_SERVICE, this);
        
        log.info("Finished initializing XACML policy container");
    }

    /**
     * @see IAgentXACMLPolicyContainer#setPolicyFinder(IXACMLPolicyFinderModule)
     */
    public void setPolicyFinder(IXACMLPolicyFinderModule policyFinder) {
        if (policyFinder == null) {
            throw new NullPointerException("policyFinder is null");
        }

        List<String> policies = new ArrayList<>();
        for (IPair<Long, String> pair : activeResponse.getPoliciesAndPolicySets()) {
            policies.add(pair.second());
        }
        policyFinder.setPolicies(policies);
    }

    /**
     * @see IManagerEnabled#setManager(IComponentManager)
     */
    public void setManager(IComponentManager manager) {
        this.manager = manager;
    }

    /**
     * @see IManagerEnabled#getManager()
     */
    public IComponentManager getManager() {
        return manager;
    }

    /**
     * @see ILogEnabled#setLog(Log)
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @see ILogEnabled#getLog()
     */
    public Log getLog() {
        return log;
    }
    
    /**
     * @see IConfigurable#setConfiguration(IConfiguration)
     */
    public void setConfiguration(IConfiguration config) {
        this.config = config;
    }

    /**
     * @see IConfigurable#getConfiguration()
     */
    public IConfiguration getConfiguration() {
        return config;
    }


    /**
     * See IHeartbeatListener#prepareRequest(String)
     */
    @Override
    public Serializable prepareRequest(String id) {
        return new XACMLPoliciesRequestDTO(activeResponse.getBuildTime());
    }

    /**
     * See IHeartbeatListener#processResponse(String, Serializable)
     *
     * Make synchonrized to avoid race condition when writing policies file data
     */
    @Override
    synchronized public void processResponse(String id, String data) {
        XACMLPoliciesResponseDTO dto = (XACMLPoliciesResponseDTO) SerializationUtils.unwrapSerialized(data);

        // Save data to policies file
        if (dto != null) {
            try {
                activeResponse = dto;
                buildIdMap();
                saveResponseDTO(dto);

                // new PDP for new policies
                forceUpdatePDP();
            } catch (IOException e) {
                log.error("Unable to save XACML policy information", e);
                return;
            }
        }
    }

    /**
     * See IAgentXACMLPolicyContainer#evaluate(String)
     */
    @Override
    public XACMLEvaluationResult evaluate(String request, String dataType) {
        long requestTimestamp = System.currentTimeMillis();
        XACMLPoliciesResponseDTO dto = activeResponse;

        ResponseCtx responseCtx;
        
        try {
            AbstractRequestCtx requestCtx = RequestCtxFactory.getFactory().getRequestCtx(request.replaceAll(">\\s+<", "><"));
            
            responseCtx = getPDP().evaluate(requestCtx);
        } catch (ParsingException pe) {
            String errorMessage = "Invalid request: " + pe.getMessage();
            getLog().error(errorMessage);

            ArrayList<String> code = new ArrayList<>();
            code.add(Status.STATUS_SYNTAX_ERROR);

            responseCtx = new ResponseCtx(new Result(AbstractResult.DECISION_INDETERMINATE, new Status(code, errorMessage)));
        }

        XACMLEvaluationResult result = new XACMLEvaluationResult(new WrappedXACMLResponse(responseCtx));
        
        try {
            // Check for log obligations. If we have obligations then we'll need
            // a PolicyActivityInfoV5, as that's sent with the log
            if (dto != null) {
                XACMLObligationParser obligationParser = new XACMLObligationParser(responseCtx);
            
                Set<String> policiesWithLogObligations = obligationParser.getLogObligationPolicies();
                
                for (String policyName : policiesWithLogObligations) {
                    LogObligation obligation = obligationManager.createLogObligation();

                    Long policyId = null;
                    synchronized(this) {
                        policyId = policyNameIdMap.get(policyName);
                    }
                    
                    if (policyId == null) {
                        getLog().warn("Unable to find policy id for policy " + policyName + " mentioned in log obligation");
                        policyId = -1L;
                    } else {
                        getLog().debug("Using policy id " + policyId + " for policy " + policyName + " in XACML obligation");
                    }

                    // Generate a temporary, bogus policy
                    obligation.setPolicy(policyManager.newPolicy(policyId, policyName));
                    
                    result.addObligation(obligation);
                }
                
                if (!result.getObligations().isEmpty()) {
                    PolicyActivityInfoV5 info = XACMLActivityInfoBuilder.parse(request, obligationParser.getXACMLDecision(), requestTimestamp);
                    result.setPolicyActivityInfo(info);
                }
            }
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            log.warn("Error parsing obligations", ex);
        }
        return result;
    }
    
    private void saveResponseDTO(XACMLPoliciesResponseDTO dto) throws IOException {
        getPoliciesSecureStore().save(dto);
    }

    private XACMLPoliciesResponseDTO readResponseDTO() throws IOException {
        return getPoliciesSecureStore().read();
    }
    
    private ISecureStore<XACMLPoliciesResponseDTO> getPoliciesSecureStore() throws IOException {
        return new SecureFileStore<XACMLPoliciesResponseDTO>(new File(policiesFileName).getAbsoluteFile(), "bundleKey");
    }

    private PDP getPDP() {
        return getPDP(false);
    }

    private void forceUpdatePDP() {
        getPDP(true);
    }
    
    synchronized private PDP getPDP(boolean forceCreate) {
        if (forceCreate) {
            pdpInstance = null;
        }
        
        if (pdpInstance == null) {
            Balana balanaInstance = Balana.getInstance();
            
            pdpInstance = new PDP(balanaInstance.getPdpConfig());
        }

        return pdpInstance;
    }

    private void buildIdMap() {
        Map<String, Long> idMap = new HashMap<>();
        
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            
            for (IPair<Long, String> pair : activeResponse.getPoliciesAndPolicySets()) {
                String xacml = pair.second();
                
                try {
                    Document doc = builder.parse(IOUtils.toInputStream(xacml));
                    Element root = doc.getDocumentElement();
                    
                    String policyId = root.getAttribute("PolicyId");
                    
                    if (policyId == null) {
                        policyId = root.getAttribute("PolicySetId");
                    }
                    
                    if (policyId == null) {
                        getLog().warn("Unable to parse policy id from " + xacml.substring(0, Math.min(40, xacml.length())) + " ... ");
                    } else {
                        getLog().info("Creating id map from " + policyId + " to " + pair.first());
                        idMap.put(policyId, pair.first());
                    }
                } catch (IOException | SAXException se) {
                    getLog().warn("Unable to parse policy " + xacml.substring(0, Math.min(40, xacml.length())) + " ... ");
                }
            }
        } catch (ParserConfigurationException e) {
            getLog().warn("Unable to configure XML parser!");
            return;
        }

        synchronized(this) {
            policyNameIdMap = idMap;
        }
    }
}
