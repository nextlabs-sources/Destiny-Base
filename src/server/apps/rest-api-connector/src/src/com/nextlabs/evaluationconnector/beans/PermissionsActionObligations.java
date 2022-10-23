/*
 * Created on Aug 10, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.evaluationconnector.beans;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationsType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionObligations", propOrder= { "action", "matchingPolicies", "obligations" })
public class PermissionsActionObligations implements Serializable {
    public static final QName QNAME = new QName("urn:nextlabs:names:permsvc:1.0:response", "PermissionsActionObligations");
    private static final long serialVersionUID = 5189546463128517610L;
    
    @XmlElement(name = "action", required=true)
    private String action;
    @XmlElement(name = "matchingPolicies", required=false)
    private List<String> matchingPolicies;
    @XmlElement(name = "obligations", required=false)
    private ObligationsType obligations;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getMatchingPolicies() {
        return matchingPolicies;
    }

    public void setMatchingPolicies(List<String> matchingPolicies) {
        this.matchingPolicies = matchingPolicies;
    }
    
    public ObligationsType getObligations() {
        return obligations;
    }

    public void setObligations(ObligationsType obligations) {
        this.obligations = obligations;
    }
}
