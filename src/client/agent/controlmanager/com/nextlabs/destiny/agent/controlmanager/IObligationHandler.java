/*
 * Created on Oct 05, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */
package com.nextlabs.destiny.agent.controlmanager;

import java.util.List;

import com.bluejungle.destiny.agent.activityjournal.IActivityJournal;
import com.bluejungle.destiny.agent.controlmanager.IControlManager;
import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.comp.PropertyKey;
import com.bluejungle.pf.engine.destiny.EvaluationResult;
import com.bluejungle.pf.domain.destiny.obligation.IDObligation;
import com.nextlabs.destiny.agent.controlmanager.PolicyEvaluationResult;

public interface IObligationHandler {
    ComponentInfo<IObligationHandler> COMP_INFO = new ComponentInfo<IObligationHandler>(
        IObligationHandler.class.getName(),
        ObligationHandler.class,
        IObligationHandler.class,
        LifestyleType.SINGLETON_TYPE);
    
    PropertyKey<IActivityJournal> ACTIVITY_JOURNAL_NAME = new PropertyKey<>("activityJournal");
    PropertyKey<IControlManager> CONTROL_MANAGER_NAME = new PropertyKey<>("controlmanager");

    void executeObligations(ObligationContext context);
    void addTask(Runnable task);
}
