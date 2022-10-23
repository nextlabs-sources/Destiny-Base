package com.nextlabs.destiny.agent.pdpapi;

/*
 * Created on Jul 31, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import java.util.Collection;
import java.util.Set;

import com.nextlabs.destiny.agent.controlmanager.IObligationResultData;

public interface IPDPPermissionsResponse {
    void addAction(String effect, String action);
    
    void addAction(String effect, String action, Collection<IObligationResultData> obligations, Collection<String> policies);
    
    Collection<IObligationResultData> getObligationsForAction(String effect, String action);

    Collection<String> getPoliciesForAction(String effect, String action);

    Collection<String> getPermittedActionsForEffect(String effect);
}
