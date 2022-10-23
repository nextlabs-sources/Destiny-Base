/*
 * Created on Oct 08, 2020
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
@XmlType(name = "PermissionsActionObligationsList", propOrder={ "actionObligations" })
public class PermissionsActionObligationsList implements Serializable {
    public static final QName QNAME = new QName("urn:nextlabs:names:permsvc:1.0:response", "PermissionsActionObligationsList");
    private static final long serialVersionUID = 1312854246626259296L;

    @XmlElement(name = "actionObligations", required=true)
    private List<PermissionsActionObligations> actionObligations;

    public void setActionObligations(List<PermissionsActionObligations> actionObligations) {
        this.actionObligations = actionObligations;
    }

    public List<PermissionsActionObligations> getActionObligations() {
        return actionObligations;
    }
                                     
}
