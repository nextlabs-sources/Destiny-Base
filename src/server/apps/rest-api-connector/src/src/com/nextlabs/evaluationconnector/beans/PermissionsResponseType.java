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
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.bluejungle.pf.domain.destiny.misc.EffectType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PermissionsResponse", propOrder= { "actionObligationsListMap" })
public class PermissionsResponseType implements Serializable {
    public static final String[] EFFECTS_TO_REPORT = new String[] { EffectType.ALLOW.getName(), EffectType.DENY.getName(), EffectType.DONT_CARE.getName() };
    
    public static final QName QNAME = new QName("urn:nextlabs:names:permsvc:1.0:response", "PermissionsResponseType");
    private static final long serialVersionUID = 4789092403738865822L;
    
    @XmlElement(name = "actionObligationsMap", required=true)
    private Map<String, PermissionsActionObligationsList> actionObligationsListMap = new HashMap<>();

    public Map<String, PermissionsActionObligationsList> getActionObligationsListMap() {
        return actionObligationsListMap;
    }

    public void setActionsObligationsListMap(Map<String, PermissionsActionObligationsList> actionObligationsListMap) {
        this.actionObligationsListMap = actionObligationsListMap;
    }

    public void setActionsObligationsList(String effect, PermissionsActionObligationsList actionObligationsList) {
        actionObligationsListMap.put(effect, actionObligationsList);
    }

    public PermissionsActionObligationsList getActionObligationsForEffect(String effect) {
        PermissionsActionObligationsList ret = actionObligationsListMap.get(effect);

        if (ret == null) {
            return new PermissionsActionObligationsList();
        }
        
        return ret;
    }
}
