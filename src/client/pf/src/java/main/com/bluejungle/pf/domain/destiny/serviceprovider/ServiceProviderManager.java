/*
 * Created on Jan 18, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.bluejungle.pf.domain.destiny.serviceprovider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;

import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IDisposable;
import com.bluejungle.framework.comp.IHasComponentInfo;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.IManagerEnabled;
import com.bluejungle.framework.comp.LifestyleType;
import com.bluejungle.framework.heartbeat.HeartbeatManagerImpl;
import com.bluejungle.framework.heartbeat.IHeartbeatManager;
import com.bluejungle.pf.domain.destiny.serviceprovider.IDisposableServiceProvider;
import com.bluejungle.pf.domain.destiny.serviceprovider.IExternalServiceProvider;
import com.bluejungle.pf.domain.destiny.serviceprovider.IExternalServiceProviderResponse;
import com.bluejungle.pf.domain.destiny.serviceprovider.IServiceProvider;
import com.bluejungle.pf.domain.destiny.serviceprovider.IServiceProviderManager;
import com.nextlabs.pf.domain.destiny.serviceprovider.ServiceProviderFileType;

/**
 * @author hchan
 */

public class ServiceProviderManager implements IServiceProviderManager, ILogEnabled, IInitializable,
                                               IConfigurable, IHasComponentInfo<IServiceProviderManager>, IManagerEnabled, IDisposable {
    public static final ComponentInfo<IServiceProviderManager> COMP_INFO =
        new ComponentInfo<IServiceProviderManager>(
                ServiceProviderManager.class, 
                LifestyleType.SINGLETON_TYPE);

    private String jserviceRootDirectory = null;
    private IHeartbeatManager heartbeatManager = null;
    private IConfiguration config;
    private IComponentManager manager;
    private Log log;
    
    //contain all service
    private Map<String, IServiceProvider> serviceMap;
    
    //only contain external service
    private Map<String, IExternalServiceProvider> extServiceMap;
    
    private ServiceConfigurator configurator;
    
    private boolean isInitialized;
    
    @Override
    public boolean isRegistered(String name){
        return serviceMap.containsKey(name);
    }

    @Override
    public void register(String name, IServiceProvider service){
        synchronized (serviceMap) {
            if(serviceMap.containsKey(name)){
                throw new IllegalArgumentException(name);
            }
            serviceMap.put(name, service);
            
            if(service instanceof IExternalServiceProvider){
                extServiceMap.put(name, (IExternalServiceProvider)service);
            }
            if (service instanceof IHeartbeatServiceProvider) {
                heartbeatManager.register(name, (IHeartbeatServiceProvider)service);
            }
        }
        getLog().info("A new Service '" + name + "' is registered.");
    }

    @Override
    public void unregister(String name) {
        synchronized(serviceMap) {
            IServiceProvider service = serviceMap.remove(name);

            if (service == null) {
                getLog().warn("Asked to unregister service " + name + " that did not exist.");
                return;
            }

            if (service instanceof IDisposableServiceProvider) {
                ((IDisposableServiceProvider)service).dispose();
            }
        }
        getLog().info("The service '" + name + "' is no longer.");
    }
    
    @Override
    public ComponentInfo<IServiceProviderManager> getComponentInfo() {
        return COMP_INFO;
    }

    @Override
    public Log getLog() {
        return log;
    }

    @Override
    public void setLog(Log log) {
        this.log = log;
    }

    @Override
    public void init() {
        isInitialized = false;
        serviceMap = new HashMap<String, IServiceProvider>();
        extServiceMap = new HashMap<String, IExternalServiceProvider>();
        ComponentInfo heartbeatinfo = new ComponentInfo(IHeartbeatManager.class.getName(), HeartbeatManagerImpl.class.getName(), IHeartbeatManager.class.getName(), LifestyleType.SINGLETON_TYPE);
        heartbeatManager = (IHeartbeatManager) manager.getComponent(heartbeatinfo);

        final String baseDir = config != null ? config.get(BASE_DIR_PROPERTY_NAME, ".") : ".";

        configurator = new ServiceConfigurator(this, baseDir + "/jservice");
        
        try {
            configurator.init();
        } catch (Exception e) {
            getLog().error("Error during init.", e);
        } finally {
            isInitialized = true;
        }
    }

    @Override
    public void dispose() {
        getLog().info("ServiceProviderManager is shutting down all service providers");
        for (IDisposableServiceProvider sp : getAllServiceProvidersByType(IDisposableServiceProvider.class)) {
            sp.dispose();
        }
    }
    
    @Override
    public void setManager(IComponentManager manager) {
        this.manager = manager;
    }

    @Override
    public IComponentManager getManager() {
        return manager;
    }

    @Override
    public IServiceProvider getServiceProvider(String name) {
        synchronized (serviceMap) {
            return serviceMap.get(name);
        }
    }

    @Override
    public List<IServiceProvider> getAllServiceProviders() {
        return getAllServiceProvidersByType(IServiceProvider.class);
    }

    @Override
    public <T extends IServiceProvider> List<T> getAllServiceProvidersByType(Class<T> clazz) {
        List<T> providers = new ArrayList<T>();

        synchronized (serviceMap) {
            for (Map.Entry<String, IServiceProvider> entry : serviceMap.entrySet()) {
                IServiceProvider sp = entry.getValue();
                
                if (clazz.isInstance(sp)) {
                    providers.add(clazz.cast(sp));
                }
            }
        }

        return providers;
    }

    private void stopServiceProvider(String name) {
        synchronized(serviceMap) {
            IServiceProvider sp = serviceMap.get(name);

            if (sp != null) {
                // Handle shutdown
                if (sp instanceof IDisposableServiceProvider) {
                    ((IDisposableServiceProvider)sp).dispose();
                }
                
                serviceMap.remove(name);
            }
        }
    }
    
    @Override
    public void removeServiceProvider(String name) {
        stopServiceProvider(name);
        configurator.deleteProvider(name);
    }

    @Override
    public void restartServiceProvider(String name) throws ServiceProviderException {
        stopServiceProvider(name);
        configurator.restartServiceProvider(name);
    }
    
    @Override
    public IConfiguration getConfiguration() {
        return config;
    }

    @Override
    public void setConfiguration(IConfiguration config) {
        this.config = config;
    }

    @Override
    public void invoke(ArrayList inputArgs, ArrayList outputArgs) {
        assert inputArgs != null;
        assert inputArgs.size() > 0;
        assert outputArgs != null;
        assert outputArgs.isEmpty();

        String serviceName = inputArgs.get(0).toString();
        
        //at this moment, we only have external service.
        invokeExternalService(serviceName, inputArgs, outputArgs);
    }

    @Override
    public void addServiceProviderFile(String pluginName, ServiceProviderFileType type, String fileName, byte[] data) throws IOException {
        configurator.addServiceProviderFile(pluginName, type, fileName, data);
    }
    
    protected Object[] translateInput(ArrayList inputArgs){
        int inputSize = inputArgs.size() - 1;
        Object[] input = new Object[inputSize];
        for (int i = 0; i < inputSize; i++) {
            input[i] = inputArgs.get(i + 1);
        }
        return input;
    }
    
    protected void invokeExternalService(String serviceName, ArrayList inputArgs, ArrayList outputArgs){
        getLog().info("enter");
        long startTime = System.currentTimeMillis();
        try {
            IExternalServiceProvider extService = extServiceMap.get(serviceName);
            if(extService == null){
                getLog().info("Got null service name\n");
                outputArgs.add(isInitialized ? CE_RESULT_SERVICE_NOT_FOUND : CE_RESULT_SERVICE_NOT_READY);
                outputArgs.add(EMPTY_FORMAT_STRING);
                return;
            }
            
            Object[] input = translateInput(inputArgs);
            IExternalServiceProviderResponse response;
            try {
                response = extService.invoke(input);
            } catch (RuntimeException e) {
                getLog().error("Exception in service: " + serviceName, e);
                outputArgs.add(CE_RESULT_GENERAL_FAILED);
                outputArgs.add(EMPTY_FORMAT_STRING);
                return;
            }
            
            if(response == null){
                getLog().info("Response was null for " + serviceName);
                outputArgs.add(CE_RESULT_SUCCESS);
                outputArgs.add(EMPTY_FORMAT_STRING);
                return;
            }
            
            Object[] output = response.getData();
            if(output == null){
                getLog().info("Response data was null for " + serviceName);
                outputArgs.add(CE_RESULT_SUCCESS);
                outputArgs.add(EMPTY_FORMAT_STRING);
                return;
            }
            
            String formatString = response.getFormatString();
            
            outputArgs.add(CE_RESULT_SUCCESS);
            outputArgs.add(formatString != null ? formatString : EMPTY_FORMAT_STRING);
            Collections.addAll(outputArgs, output);
        } finally {
            getLog().info("round trip time of service '" + serviceName + "' = " 
                    + (System.currentTimeMillis() - startTime) + "ms.");
            getLog().info("exit");
        }
    }
}
