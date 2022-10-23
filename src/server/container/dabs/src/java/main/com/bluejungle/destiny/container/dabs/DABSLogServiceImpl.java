/*
 * Created on Nov 16, 2004
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.container.dabs;

import com.bluejungle.destiny.container.dabs.components.log.ILogWriter;
import com.bluejungle.destiny.services.log.LogServiceIF;
import com.bluejungle.destiny.services.log.types.LogStatus;
import com.bluejungle.destiny.services.management.types.ServiceNotReadyFault;
import com.bluejungle.domain.log.PolicyActivityLogEntry;
import com.bluejungle.domain.log.TrackingLogEntry;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.datastore.hibernate.exceptions.DataSourceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Implementation of the DABS Log Service
 * 
 * @author ihanen
 * @author sgoldstein
 * @author sasha
 */
public class DABSLogServiceImpl implements LogServiceIF {

   
    private static final Log LOG = LogFactory.getLog(DABSLogServiceImpl.class.getName());

    /**
     * Create an instance of the DABS Log Service
     */
    public DABSLogServiceImpl() {
        super();
    }

    /**
     * This function is called by the agent to submit a log request to the service.
     * 
     * @param logInfo structure containing the logs to be stored in the database
     * @return a log status (saying whether logging was ok or not)
     */
    public LogStatus logPolicyActivity(String request) {

        long before = System.currentTimeMillis();
        try {
            ILogWriter logWriter = getLogWriter();
            long beforeDeserialization = System.currentTimeMillis();
            PolicyActivityLogEntry[] logEntries = decodePARequest(request);
            long afterDeserialization = System.currentTimeMillis();
            logWriter.log(logEntries);
            long after = System.currentTimeMillis();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Deserializing decoded stream took " + (afterDeserialization - beforeDeserialization) + " ms.");                                
                LOG.trace("Writing to DB took " + (after - afterDeserialization) + " ms.");                                                
                LOG.trace("The entire operation took " + (after - before) + " ms.");
            }
            return LogStatus.Success;
        } catch (DataSourceException exception) {
            // Log it
            LOG.warn("Persistence failure while attempting to record policy activity log entries.", exception);
        } catch (IOException | ServiceNotReadyFault e) {
            LOG.warn("Exception while attempting to record policy activity log entries.", e);            
        }
        return LogStatus.Failed;
    }
    
    public LogStatus logTracking(String request) {
        
        try {
            TrackingLogEntry[] entries = decodeTRRequest(request);            
            ILogWriter logWriter = getLogWriter();
            logWriter.log(entries);
            return LogStatus.Success;
        } catch (DataSourceException exception) {
            LOG.warn("Persistence failure while attempting to record tracking log entries.", exception);            
        } catch (IOException | ServiceNotReadyFault e) {
            LOG.warn("Exception while attempting to record tracking log entries.", e);            
        }
        
        return LogStatus.Failed;
    }

    private TrackingLogEntry[] decodeTRRequest(String request) throws IOException {
        ObjectInputStream ois = DABSLogServiceWSConverter.decodeData(request);
        int numEntries = ois.readInt();
        TrackingLogEntry[] rv = new TrackingLogEntry[numEntries];
        for (int i = 0; i < numEntries; i++) {
            rv[i] = DABSLogServiceWSConverter.readExternalTrackingLog(ois);
        }
        return rv;
    }
    
    private PolicyActivityLogEntry[] decodePARequest(String request) throws IOException {
        ObjectInputStream ois = DABSLogServiceWSConverter.decodeData(request);
        int numEntries = ois.readInt();
        PolicyActivityLogEntry[] rv = new PolicyActivityLogEntry[numEntries];
        for (int i = 0; i < numEntries; i++) {
            rv[i] = DABSLogServiceWSConverter.readExternalPolicyLog(ois);
        }
        return rv;
    }
    
    /**
     * Returns the log object
     * 
     * @return the log object
     */
    protected Log getLog() {
        return LOG;
    }

    /**
     * Retrieve the ILogWriter component.
     * 
     * @return The ILogWriter component
     * @throws ServiceNotReadyFault if the LogWriter is not available
     */
    private ILogWriter getLogWriter() throws ServiceNotReadyFault {
        IComponentManager componentManager = ComponentManagerFactory.getComponentManager();
        if (!componentManager.isComponentRegistered(ILogWriter.COMP_NAME)) {
            throw new ServiceNotReadyFault();
        }

        return (ILogWriter) componentManager.getComponent(ILogWriter.COMP_NAME);
    }
}
