/*
 * Created on Oct 26, 2004
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 *  
 */
package com.bluejungle.destiny.container.dms.components.compmgr;

import java.util.List;

import com.bluejungle.destiny.container.dms.data.ComponentDO;
import com.bluejungle.destiny.container.dms.data.EventRegistration;
import com.bluejungle.framework.comp.IDisposable;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.datastore.hibernate.exceptions.DataSourceException;

/**
 * CRUD library interface for the logical Event Registration Manager
 * 
 * @author safdar
 */
public interface IDCCEventRegistrationMgr extends IInitializable, ILogEnabled, IDisposable {

    public static final String COMP_NAME = "eventRegComponent";

    /**
     * Register a new event in the system
     * 
     * @param eventName
     *            name of the event
     * @param dcsf
     *            dcsf that will consume this event
     * @throws DataSourceException
     */
    public void registerConsumerForEvent(String eventName, ComponentDO dcsf) throws DataSourceException;

    /**
     * Unregister an existing event in the system
     * 
     * @param eventName
     *            name of the event
     * @param dcsf
     *            dcsf that will consume this event
     * @throws DataSourceException
     */
    public void unregisterConsumerForEvent(String eventName, ComponentDO dcsf) throws DataSourceException;

    /**
     * Unregisters the given consumer from all events in the system.
     * 
     * @param dcsf
     * @throws DataSourceException
     */
    public void unregisterConsumerForAllEvents(ComponentDO dcsf) throws DataSourceException;

    /**
     * Returns an array of event registrations that occured since
     * 'date' for all components except the given one
     * 
     * @param date
     * @param exceptComponent
     * @return List of EventRegistration
     * @throws DataSourceException
     */
    public List<EventRegistration> getRegistrationsSince(long timestamp, ComponentDO exceptComponent) throws DataSourceException;
    
    /**
     * Returns an array of event registrations ever made for this component
     * 
     * @param component
     * @return List of EventRegistration
     * @throws DataSourceException
     */
    public List<EventRegistration> getRegistrationsForComponentEver(ComponentDO component) throws DataSourceException;
    
    /**
     * Returns an array of event registrations that occured since
     * 'date' for the given component
     * 
     * @param date
     * @param component
     * @return List of EventRegistration
     * @throws DataSourceException
     */
    public List<EventRegistration> getRegistrationsForComponentSince(long timestamp, ComponentDO component) throws DataSourceException;

    /**
     * Cleans the database tables associated with component registration.
     * Currently the only use for this is with JUnit testing.
     * 
     * @throws DataSourceException
     */
    public void clearAll() throws DataSourceException;
}
