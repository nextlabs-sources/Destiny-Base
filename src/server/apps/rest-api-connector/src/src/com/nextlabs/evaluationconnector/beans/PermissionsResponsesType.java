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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PermissionsResponsesType", propOrder={"permissionsResponses"})
public class PermissionsResponsesType implements Serializable {
    private static final long serialVersionUID = 3023248316385765926L;
    
    public static final QName QNAME = new QName("urn:nextlabs:names:permsvc:1.0:response", "PermissionsResponsesType");
    
    @XmlElement(name="permissionsResponse", required=true)
    private List<PermissionsResponseType> permissionsResponses;

    public void setPermissionsResponses(List<PermissionsResponseType> permissionsResponses) {
        this.permissionsResponses = permissionsResponses;
    }
    
    public List<PermissionsResponseType> getPermissionsResponses() {
        return permissionsResponses;
    }
}
