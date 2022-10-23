/*
 * Created on Sep 24, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.agent.controlmanager;

import java.util.Collection;
import java.util.Map;

import com.nextlabs.destiny.agent.controlmanager.IObligationResultData;

public interface IPolicyEvaluationResult {
    String getEffectName();
    
    Map<String, Collection<String>> getMatchingPolicies();
    
    Collection<String> getMatchingPolicies(String effect);

    Collection<IObligationResultData> getObligations();

    long getCacheHint();
}
