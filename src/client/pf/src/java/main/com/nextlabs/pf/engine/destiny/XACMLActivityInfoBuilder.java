package com.nextlabs.pf.engine.destiny;

/*
 * Created on Jan 17, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.wso2.balana.XACMLConstants;

import com.bluejungle.domain.log.FromResourceInformation;
import com.bluejungle.domain.policydecision.PolicyDecisionEnumType;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.pf.domain.destiny.obligation.ILoggingLevel;
import com.nextlabs.domain.log.PolicyActivityInfoV5;

/**
 * This class is used to parse XML XACML requests and extract data that is useful for
 * the log obligation
 */
public class XACMLActivityInfoBuilder {
    private static final FromResourceInformation DUMMY_FROM_RESOURCE = new FromResourceInformation("Unknown Resource", 0, 0, 0, "Unknown owner");
    
    public static PolicyActivityInfoV5 parse(String request, String xacmlDecision, long requestTimestamp) throws IOException, SAXException, ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(IOUtils.toInputStream(request));
        Element root = doc.getDocumentElement();

        FromResourceInformation fromResourceInfo = DUMMY_FROM_RESOURCE;
        String userName = "Unknown user";
        String action = "Unknown action";
        PolicyDecisionEnumType policyDecision = convertDecision(xacmlDecision);
        Map<String, DynamicAttributes> attributesMap = new HashMap<>();

        // This is going to end up being confusing, because the XACML Attributes are
        // Elements of the XML (the Elements also have Attributes, of course). I'm going
        // to give things names appropriate to their XACML meanings
        
        NodeList attributeCategories = root.getElementsByTagName("Attributes");

        for (int i = 0; i < attributeCategories.getLength(); i++) {
            Node singleAttributeCategory = attributeCategories.item(i);

            String category = singleAttributeCategory.getAttributes().getNamedItem("Category").getTextContent();

            if (category.equals(XACMLConstants.ACTION_CATEGORY)) {
                if (singleAttributeCategory.hasChildNodes()) {
                    NodeList attributes = singleAttributeCategory.getChildNodes();

                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attribute = attributes.item(j);

                        String key = getKey(attribute);

                        if (key != null) {
                            // We can probably be more specific, but I don't know how much. Might
                            // as well take whatever looks like the action name
                            if (key.endsWith(":action-id")) {
                                // With this match an existing action? Who knows??
                                action = getValue(attribute).toUpperCase();
                                break;
                            }
                        }
                    }
                }
            } else if (category.equals(XACMLConstants.SUBJECT_CATEGORY)) {
                if (singleAttributeCategory.hasChildNodes()) {
                    NodeList attributes = singleAttributeCategory.getChildNodes();

                    DynamicAttributes userAttributes = new DynamicAttributes();
                    
                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attribute = attributes.item(j);

                        String key = getKey(attribute);

                        if (key != null) {
                            if (key.endsWith(":subject-id")) {
                                userName = getValue(attribute);
                            } else {
                                userAttributes.put(key, getValue(attribute));
                            }
                        }
                    }

                    attributesMap.put(PolicyActivityInfoV5.USER_ATTRIBUTES_TAG, userAttributes);
                }
            } else if (category.equals(XACMLConstants.RESOURCE_CATEGORY)) {
                if (singleAttributeCategory.hasChildNodes()) {
                    NodeList attributes = singleAttributeCategory.getChildNodes();

                    DynamicAttributes resourceAttributes = new DynamicAttributes();

                    for (int j = 0; j < attributes.getLength(); j++) {
                        Node attribute = attributes.item(j);

                        String key = getKey(attribute);

                        if (key != null) {
                            if (key.endsWith(":resource-id")) {
                                fromResourceInfo = new FromResourceInformation(getValue(attribute), 0, 0, 0, "Unknown owner");
                            } else {
                                resourceAttributes.put(key, getValue(attribute));
                            }
                        }
                    }
                    attributesMap.put(PolicyActivityInfoV5.FROM_RESOURCE_ATTRIBUTES_TAG, resourceAttributes);
                }
            }
        }
        return new PolicyActivityInfoV5(fromResourceInfo,
                                        null,          // ToResourceInformation
                                        userName,  
                                        -1L,            // destiny user id, which we don't have
                                        "Unknown host",
                                        "0.0.0.0",
                                        -1L,            // host id, also not available
                                        "Unknown application",
                                        -1L,            // application id
                                        action,
                                        policyDecision,
                                        0L,             // request id
                                        requestTimestamp,
                                        ILoggingLevel.LOG_LEVEL_USER,
                                        attributesMap,
                                        null);
    }

    private static PolicyDecisionEnumType convertDecision(String xacmlDecision) {
        PolicyDecisionEnumType ret = PolicyDecisionEnumType.POLICY_DECISION_DENY;
        
        if (xacmlDecision != null && xacmlDecision.toLowerCase().equals("permit")) {
            ret = PolicyDecisionEnumType.POLICY_DECISION_ALLOW;
        }
        
        return ret;
    }
    
    private static String getKey(Node attribute) {
        if (!attribute.hasAttributes() || attribute.getAttributes().getNamedItem("AttributeId") == null) {
            return null;
        }
        
        return attribute.getAttributes().getNamedItem("AttributeId").getTextContent();
    }

    private static String getValue(Node attribute) {
        NodeList childNodes = attribute.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            if ("AttributeValue".equals(childNode.getNodeName())) {
                return childNode.getTextContent();
            }
        }

        return "";
    }
}
