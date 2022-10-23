package com.nextlabs.pf.engine.destiny;

/*
 * Created on Dec 10, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.DOMHelper;
import org.wso2.balana.MatchResult;
import org.wso2.balana.ParsingException;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.PolicyReference;
import org.wso2.balana.PolicySet;
import org.wso2.balana.PolicyTreeElement;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.VersionConstraints;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.xacml2.DenyOverridesPolicyAlg;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;
import org.wso2.balana.utils.Utils;

import org.xml.sax.SAXException;

import com.bluejungle.framework.comp.ComponentManagerFactory;


/**
 * This class is similar to
 * org.wso2.balana.finder.impl.FileBasedPolicyFinderModule and ideally
 * we'd be able to extend that class, but that won't work, because the
 * members that we'd need in our extension functions are private
 */

public class XACMLPolicyFinderModule extends PolicyFinderModule implements IXACMLPolicyFinderModule {
    private static final Log log = LogFactory.getLog(XACMLPolicyFinderModule.class.getName());

    /* Policies can be referenced by id in a policyset. It is unclear
     * from the XACML standard if these policies can be inside the
     * policy set or if they have to be separate policies (in their
     * own xml file). The examples I've seen assume the latter, so
     * that's what the default is. See below for how to configure this
     */
    private final boolean addSubPoliciesToMap;
    
    private PolicyFinder finder = null;
    private Map<URI, AbstractPolicy> policies;
    private PolicyCombiningAlgorithm defaultCombiningAlgorithm = new DenyOverridesPolicyAlg();
    public static final String XACML_POLICY_FILE_NAME_PROPERTY = "nextlabs.xacml.policy.file.name";
    
    public XACMLPolicyFinderModule() {
        this("false");
    }

    /**
     * These constructors are called by the Balana configuration
     * process based on the information in the configuration file
     * (which is usually going to be config/xacmlpdpconfig) and should
     * look something like this:
     *
     *   <policyFinderModule class="com.nextlabs.pf.engine.destiny.XACMLPolicyFinderModule">
     *       <string>true</string>
     *   </policyFinderModule>
     *
     * The argument is optional and will default to "false" if not
     * provided. It enables sub-policy reference id lookup
     */
    
    /**
     * Constructor specifying whether or not to include sub-policies of a policy
     * set in the policy id map
     */
    public XACMLPolicyFinderModule(String addSubPoliciesToMap) {
        policies = new HashMap<URI, AbstractPolicy>();
        this.addSubPoliciesToMap = Boolean.valueOf(addSubPoliciesToMap);
    }
    
    @Override
    public void init(PolicyFinder finder) {
        this.finder = finder;
        
        IAgentXACMLPolicyContainer policyContainer = ComponentManagerFactory.getComponentManager().getComponent(IAgentXACMLPolicyContainer.COMP_INFO);
        policyContainer.setPolicyFinder(this);
    }

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx ctx) {
        ArrayList<AbstractPolicy> selectedPolicies = new ArrayList<AbstractPolicy>();
        Set<Map.Entry<URI, AbstractPolicy>> entrySet = policies.entrySet();

        // iterate through all the policies we currently have loaded
        for (Map.Entry<URI, AbstractPolicy> entry : entrySet) {

            AbstractPolicy policy = entry.getValue();
            
            MatchResult match = matchPolicy(policy, ctx);

            int result = match.getResult();
            
            // if target matching was indeterminate, then return the error
            if (result == MatchResult.INDETERMINATE) {
                return new PolicyFinderResult(match.getStatus());
            }
            
            // see if the target matched
            if (result == MatchResult.MATCH) {
                if ((defaultCombiningAlgorithm == null) && (selectedPolicies.size() > 0)) {
                    // we found a match before, so this is an error
                    ArrayList<String> code = new ArrayList<String>();
                    code.add(Status.STATUS_PROCESSING_ERROR);
                    Status status = new Status(code, "too many applicable " + "top-level policies");
                    return new PolicyFinderResult(status);
                }
                
                // this is the first match we've found, so remember it
                selectedPolicies.add(policy);
            }
        }

        // no errors happened during the search, so now take the right
        // action based on how many policies we found
        switch (selectedPolicies.size()) {
            case 0:
                if (log.isDebugEnabled()) {
                    log.debug("No matching XACML policy found");
                }
                return new PolicyFinderResult();
            case 1:
                return new PolicyFinderResult((selectedPolicies.get(0)));
            default:
                return new PolicyFinderResult(new PolicySet(null, defaultCombiningAlgorithm, null, selectedPolicies));
        }
    }
    
    private MatchResult matchPolicy(AbstractPolicy policy, EvaluationCtx ctx) {
        try {
            return policy.match(ctx);
        } catch (Exception e) {
            // Balana can't be relied on to throw nice exceptions, so we catch everything
            log.warn("Exception thrown while evaluating policy " + policy.getId().toString());
            ArrayList<String> code = new ArrayList<>();
            code.add(Status.STATUS_PROCESSING_ERROR);
            return new MatchResult(MatchResult.INDETERMINATE, new Status(code, "Exception thrown while evaluating policies"));
        }
    }

    @Override
    public PolicyFinderResult findPolicy(URI idReference, int type, VersionConstraints constraints,
                                         PolicyMetaData parentMetaData) {
        AbstractPolicy policy = policies.get(idReference);
        if (policy != null) {
            if (type == PolicyReference.POLICY_REFERENCE) {
                if (policy instanceof Policy) {
                    return new PolicyFinderResult(policy);
                }
            } else {
                if (policy instanceof PolicySet) {
                    return new PolicyFinderResult(policy);
                }
            }
        }

        // if there was an error loading the policy, return the error
        ArrayList<String> code = new ArrayList<String>();
        code.add(Status.STATUS_PROCESSING_ERROR);
        Status status = new Status(code, "couldn't load referenced policy");
        return new PolicyFinderResult(status);
    }
    
    @Override
    public boolean isIdReferenceSupported() {
        return true;
    }

    @Override
    public boolean isRequestSupported() {
        return true;
    }


    /**
     * Set the policy list to be used by the finder
     */
    public void setPolicies(List<String> policiesAndPolicySets) {
        Map<URI, AbstractPolicy> newPolicies = new HashMap<>();
        
        try {
            for (String policyXML : policiesAndPolicySets) {
                InputStream stream = null;
                try {
                    // We use the document factory to determine if it's a policy or policy set
                    DocumentBuilderFactory factory = Utils.getSecuredDocumentBuilderFactory();
                    factory.setIgnoringComments(true);
                    factory.setNamespaceAware(true);
                    factory.setValidating(false);
                    
                    DocumentBuilder db = factory.newDocumentBuilder();
                    stream = IOUtils.toInputStream(policyXML);
                    Document doc = db.parse(stream);
                    
                    Element root = doc.getDocumentElement();

                    if (validationCheck(root)) {
                        AbstractPolicy policy = createPolicyOrSet(root, newPolicies);
                        
                        if (policy != null) {
                            log.info("Adding policy " + policy.getId());
                            newPolicies.put(policy.getId(), policy);
                        }
                    }
                } catch (SAXException | ParsingException e) {
                    log.error("Unable to parse policy/policy set: " + policyXML, e);
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            log.error("Error while closing input stream");
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            log.error("Unable to initialize document builder");
        } catch (IOException e) {
            log.error("Unable to read policies");
        }

        policies = newPolicies;
    }

    private AbstractPolicy createPolicyOrSet(Element root, Map<URI, AbstractPolicy> policyMap) throws ParsingException {
        String name = DOMHelper.getLocalName(root);
        
        if (name.equals("Policy")) {
            return Policy.getInstance(root);
        } else if (name.equals("PolicySet")) {
            AbstractPolicy policySet =  PolicySet.getInstance(root, finder);

            if (addSubPoliciesToMap) {
                addSubPoliciesToPolicyMap(policySet, policyMap);
            }
            
            return policySet;
        }

        return null;
    }

    private void addSubPoliciesToPolicyMap(AbstractPolicy policySet, Map<URI, AbstractPolicy> policyMap) {
        for (PolicyTreeElement element : policySet.getChildren()) {
            if (element instanceof Policy) {
                log.info("Adding sub-policy " + element.getId() + " of parent " + policySet.getId());
                policyMap.put(element.getId(), (AbstractPolicy) element);
            } else if (element instanceof PolicySet) {
                log.info("Adding sub-policyset " + element.getId() + " of parent " + policySet.getId());
                policyMap.put(element.getId(), (AbstractPolicy)element);
                
                addSubPoliciesToPolicyMap((AbstractPolicy)element, policyMap);
            }
        }
    }
    
    private boolean validationCheck(Element root) {
        // Balana will throw an IllegalArgumentException if the xmlns doesn't match a known
        // XACML version. You aren't supposed to catch that exception and technically we can
        // check for it here, so let's do that
        String namespaceURI = root.getNamespaceURI();

        boolean valid = namespaceURI == null ||
                        namespaceURI.equals(XACMLConstants.XACML_1_0_IDENTIFIER) ||
                        namespaceURI.equals(XACMLConstants.XACML_2_0_IDENTIFIER) ||
                        namespaceURI.equals(XACMLConstants.XACML_3_0_IDENTIFIER);

        if (!valid) {
            log.warn("Policy/PolicySet with invalid namespace: " + namespaceURI);
        }

        // Add additional checks as needed. Alternatively, give up and catch the exceptions
        // ...
        
        return valid;
                                
    }
}
