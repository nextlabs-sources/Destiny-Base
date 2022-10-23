/*
 * Created on Oct 25, 2004
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 *  
 */
package com.bluejungle.destiny.container.dcsf;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.logging.Log;

import com.bluejungle.destiny.server.shared.registration.IDCCRegistrationInfo;
import com.bluejungle.destiny.server.shared.registration.IDMSRegistrationListener;
import com.bluejungle.destiny.services.management.ComponentServiceStub;
import com.bluejungle.destiny.services.management.UnauthorizedCallerFault;
import com.bluejungle.destiny.services.management.types.DCCRegistrationStatus;
import com.bluejungle.destiny.services.management.types.DMSRegistrationOutcome;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.threading.ITask;
import com.bluejungle.framework.threading.IThreadPool;
import com.nextlabs.axis.JSSESocketFactoryWrapper;

/**
 * This is the DMS registration worker class. This thread tries to reach the DMS
 * and registers or unregisters a given DCC component with DMS. If the DMS
 * cannot be reached, the thread waits for some time and then keeps retrying. If
 * the DMS is finally reached, the thread waits for its next registration
 * assignment.
 * 
 * @author ihanen
 */
public class DMSRegistrationWorkerImpl implements IDMSRegistrationWorker {

    private ComponentServiceStub componentServiceStub;
    private IConfiguration config;
    private URL dmsLocation;
    private long sleepTime = 10000;
    private Log log;

    /**
     * This function cleans up the worker object.
     */
    public void dispose() {
        this.componentServiceStub = null;
    }

    /**
     * Performs the registration work. If DMS cannot be contacted, this function
     * keeps trying until DMS can be reached. Between each trial, the worker
     * class waits for a little while.
     * 
     * @param taskDefinition
     *            registration task definition. This class expects a
     *            registration info object instance to be passed.
     */
    public synchronized void doWork(ITask taskDefinition) {

        if (!isRequestValid(taskDefinition)) {
            throw new IllegalArgumentException("Invalid request object given to the worker");
        }

        DMSRegistrationRequest regRequest = (DMSRegistrationRequest) taskDefinition;
        IDCCRegistrationInfo regInfo = regRequest.getRegistrationInfo();
        IDMSRegistrationListener callback = regRequest.getListener();
        boolean registration = regRequest.isRegistration();

        DCCRegistrationStatus regStatus = new DCCRegistrationStatus();
        regStatus.setResult(DMSRegistrationOutcome.Failed);

        Thread currentThread = Thread.currentThread();
        while (!currentThread.isInterrupted()) {
            try {
                getLog().debug("Contacting DMS...");
                if (registration) {
                    regStatus = componentServiceStub.registerComponent(WebServiceHelper.convertRegistatrationInfoToWSType(regInfo));
                    //return the registration status to the caller
                    callback.onDMSRegistration(WebServiceHelper.convertDCCRegistrationStatus(regStatus));
                } else {
                	componentServiceStub.unregisterComponent(WebServiceHelper.convertRegistatrationInfoToWSType(regInfo));
                }
                return;

            } catch (UnauthorizedCallerFault wrongCallerEx) {
                getLog().error("Error when performing DMS registration : certificate refused");
                //This is a fatal error, there is no point retrying
                return;
            } catch (Exception e) {
                getLog().info("Error when performing DMS registration");
                getLog().debug("Error when performing DMS registration", e);
            }

            //if this is unregistration and it failed, do not retry. DMS may be
            // already
            //dead anyway. Worst case, the component will be marked missing.
            if (!registration) {
                return;
            }
            //if the registration failed because DMS did not answer,
            //retry after sleeping for some time
            try {
                getLog().debug("Failed to reach DMS, trying again in " + (this.sleepTime / 1000) + " seconds...");
                wait(this.sleepTime);
            } catch (InterruptedException e) {
                // the interrupt status will be reset after InterruptedException
                // in order to shutdown the thread correctly, 
                // the status must be maintain interrupted at all time
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /**
     * Returns the configuration
     * 
     * @return the configuration
     */
    public IConfiguration getConfiguration() {
        return this.config;
    }

    /**
     * This function is written to support JUNIT. It returns the component
     * service locator object.
     * @param string 
     * 
     * @return the component service locator
     * @throws IOException 
     */
    protected ComponentServiceStub getComponentServiceStub(String dmsLocation) throws IOException {
        ComponentServiceStub stub = new ComponentServiceStub(dmsLocation);

        JSSESocketFactoryWrapper.attachToStub(stub);
        
        return stub;
    }

    /**
     * Returns the log object
     * 
     * @return the log object
     */
    public Log getLog() {
        return this.log;
    }

    /**
     * Initializes the worker. This function sets up the web service locator
     * based on the values given in the configuration.
     */
    public void init() {
        try {
			this.componentServiceStub = getComponentServiceStub(this.dmsLocation.toString());
		} catch (IOException e) {
			getLog().debug("Failed to init stub with error " + e);
		}
    }

    /**
     * This function verifies whether the registration or unregistration request
     * given to the worker is valid.
     * 
     * @param request
     *            task information
     * @return true of the request is valid and false otherwise
     */
    private boolean isRequestValid(ITask request) {

        //Make sure the task definition is correct
        if (!(request instanceof DMSRegistrationRequest)) {
            return (false);
        }

        DMSRegistrationRequest regRequest = (DMSRegistrationRequest) request;
        IDCCRegistrationInfo regInfo = regRequest.getRegistrationInfo();
        IDMSRegistrationListener callback = regRequest.getListener();
        boolean registration = regRequest.isRegistration();

        if (registration) {
            //Checks that the registration information contains the right
            // information
            if (regInfo.getEventListenerURL() == null || callback == null || regInfo.getComponentName() == null) {
                return (false);
            }
        } else {
            //Check the unregistration request
            if (regInfo.getComponentName() == null || regInfo.getComponentType() == null) {
                return (false);
            }
        }
        return (true);
    }

    /**
     * Sets the configuration for the worker class
     * 
     * @param conf
     *            configuration object
     */
    public void setConfiguration(IConfiguration conf) {
        this.config = conf;
        IConfiguration workerConf = (IConfiguration) conf.get(IThreadPool.THREADPOOL_CONFIG);
        Long confSleepTime = (Long) workerConf.get(SLEEP_TIME_CONFIG_PARAM);
        if (confSleepTime != null) {
            this.sleepTime = confSleepTime.longValue();
        }
        this.dmsLocation = (URL) workerConf.get(IDMSRegistrationMgr.DMS_LOCATION_CONFIG_PARAM);
    }

    /**
     * Sets the log object on the component
     * 
     * @param newLog
     *            new log object to use
     */
    public void setLog(Log newLog) {
        this.log = newLog;
    }
}
