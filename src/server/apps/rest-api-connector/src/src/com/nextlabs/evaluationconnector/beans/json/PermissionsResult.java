/*
 * Created on Aug 05, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.evaluationconnector.beans.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PermissionsResult implements Serializable {
    private static final long serialVersionUID = 2779453770017380882L;
    
    private Status Status;
    private Map<String, List<PermissionsActionObligations>> ActionsAndObligations;

    public Status getStatus() {
        return Status;
    }

    public void setStatus(Status status) {
        Status = status;
    }

    public Map<String, List<PermissionsActionObligations>> getActionsAndObligations() {
        return ActionsAndObligations;
    }

    public void setActionsAndObligations(Map<String, List<PermissionsActionObligations>> actionsAndObligations) {
        if (actionsAndObligations == null) {
            actionsAndObligations = new HashMap<String, List<PermissionsActionObligations>>();
        }

        ActionsAndObligations = actionsAndObligations;
    }

    public void setActionsAndObligationsForEffect(String effect, List<PermissionsActionObligations> actionsAndObligations) {
        if (ActionsAndObligations == null) {
            ActionsAndObligations = new HashMap<>();
        }

        ActionsAndObligations.put(effect, actionsAndObligations);
    }
}
