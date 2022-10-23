/*
 * Created on Jan 16, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */
package com.nextlabs.pf.engine.destiny;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Node;
import org.wso2.balana.ObligationResult;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.AttributeAssignment;
import org.wso2.balana.ctx.Attribute;
import org.xml.sax.SAXException;

import static org.wso2.balana.ctx.AbstractResult.DECISIONS;
import static org.wso2.balana.ctx.AbstractResult.DECISION_INDETERMINATE;


/**
 * This class parses an XML XACML response for log obligations. There are two complications
 *
 * 1. There is no such thing as a standard log obligation in
 * XACML. The customer can use whatever they like (obligations in
 * XACML-land are handled by the PDP). We get around this by asking
 * the customer to define a list of obligation ids that represent "log
 * obligations".
 *
 * 2. Our log obligations are associated with policies, but XACML
 * obligations are not. They are just dumped in the response. This is
 * solved by looking for a "PolicyId" attribute in the obligation
 * attributes (this is not in any way a standard). If that's there
 * then we assume that's the policy id. If we can't find that we'll
 * just give the policy id as a generic "XACML Policies":1
 */
public class XACMLObligationParser {
    private static final Log log = LogFactory.getLog(XACMLObligationParser.class.getName());
    
    // If we get log obligations and can not determine which policies they applied to, use this this name
    private static final String UNKNOWN_XACML_POLICIES = "XACML Policies";
    private static final String POLICY_ID_ATTRIBUTE_VALUE_NAME = "PolicyId";
    
    private Set<String> logObligationIds;
    private ResponseCtx responseCtx;
    
    public XACMLObligationParser(ResponseCtx responseCtx) throws IOException, SAXException, ParserConfigurationException {
        this.responseCtx = responseCtx;
        this.logObligationIds = new HashSet<String>();
        logObligationIds.add("CentralizedPolicyLog");
        logObligationIds.add("NextLabsPolicyLog");
    }

    public String getXACMLDecision() {
        for (AbstractResult abstractResult : responseCtx.getResults()) {
            // We don't, ATM, have a way to handle multiple
            // responses. Take the first one
            return DECISIONS[abstractResult.getDecision()];
        }
        
        log.warn("Unable to find Decision in response");
        return DECISIONS[DECISION_INDETERMINATE];
    }
    
    public Set<String> getLogObligationPolicies() {
        Set<String> policyIds = new HashSet<>();

        for (AbstractResult abstractResult : responseCtx.getResults()) {
            for (ObligationResult obligationResult : abstractResult.getObligations()) {
                if (obligationResult instanceof org.wso2.balana.xacml3.Obligation) {
                    org.wso2.balana.xacml3.Obligation xacml3Obligation = (org.wso2.balana.xacml3.Obligation)obligationResult;

                    String obligationId = xacml3Obligation.getObligationId().toString();

                    if (logObligationIds.contains(obligationId)) {
                        for (AttributeAssignment attributeAssignment: xacml3Obligation.getAssignments()) {
                            if (attributeAssignment.getAttributeId().toString().equals(POLICY_ID_ATTRIBUTE_VALUE_NAME)) {
                                policyIds.add(attributeAssignment.getContent());
                            }
                        }
                    }
                } else if (obligationResult instanceof org.wso2.balana.xacml2.Obligation) {
                    org.wso2.balana.xacml2.Obligation xacml2Obligation = (org.wso2.balana.xacml2.Obligation)obligationResult;

                    String obligationId = xacml2Obligation.getId().toString();
                    
                    if (logObligationIds.contains(obligationId)) {
                        for (Attribute attribute: xacml2Obligation.getAssignments()) {
                            if (attribute.getId().toString().equals(POLICY_ID_ATTRIBUTE_VALUE_NAME)) {
                                AttributeValue attributeValue = attribute.getValue();
                                
                                if (attributeValue != null) {
                                    policyIds.add(attributeValue.toString());
                                } else {
                                    log.warn("Policy id found with no policy specified");
                                    policyIds.add(UNKNOWN_XACML_POLICIES);
                                }
                            }
                        }
                    }
                }
            }
        }

        return policyIds;
    }
}
