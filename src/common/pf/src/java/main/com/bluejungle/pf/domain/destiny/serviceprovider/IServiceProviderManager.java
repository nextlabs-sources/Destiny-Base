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
import java.util.Properties;
import java.util.List;

import com.bluejungle.framework.comp.PropertyKey;
import com.bluejungle.pf.domain.destiny.serviceprovider.ServiceProviderException;
import com.nextlabs.pf.domain.destiny.serviceprovider.ServiceProviderFileType;

/**
 * @author hchan
 */

public interface IServiceProviderManager extends INextlabsExternalSPResponseCode {
    public static final IServiceProviderManager DEFAULT = new IServiceProviderManager() {
        @Override
        public void invoke(ArrayList inputArgs, ArrayList outputArgs) {
            outputArgs.add(CE_RESULT_GENERAL_FAILED);
            outputArgs.add(EMPTY_FORMAT_STRING);
        }

        @Override
        public IServiceProvider getServiceProvider(String name) {
            return null;
        }

        @Override
        public List<IServiceProvider> getAllServiceProviders() {
            return Collections.<IServiceProvider>emptyList();
        }

        @Override
        public <T extends IServiceProvider> List<T> getAllServiceProvidersByType(Class<T> clazz) {
            return Collections.<T>emptyList();
        }

        @Override
        public boolean isRegistered(String name) {
            return false;
        }
        
        @Override
        public void register(String name, IServiceProvider service) {
            return;
        }
        
        @Override
        public void unregister(String name) {
            return;
        }

        @Override
        public void addServiceProviderFile(String pluginName, ServiceProviderFileType type, String fileName, byte[] data) {
            return;
        }

        @Override
        public void restartServiceProvider(String pluginName) {
            return;
        }

        @Override
        public void removeServiceProvider(String pluginName) {
            return;
        }
    };

    String EMPTY_FORMAT_STRING = "";
    PropertyKey<String> BASE_DIR_PROPERTY_NAME= new PropertyKey<String>("baseDirectory");
    
    /**
     * The manager will call the corresponding service with an array constructed 
     *   from elements 1...n of <code>inputArgs</code>
     * If the service can't be found, it will return error <code>CE_FUNCTION_NOT_AVAILABLE</code> 
     *   at the first element in <code>outputArgs</code>
     * @param inputArgs the first element is the service provider name. 
     * @param outputArgs recommend to return <code>CE_RESULT_SUCCESS</code> if everything is ok.
     *                   The implementor is free to use the error codes defined in CESdk.h or 
     *                   others as they see fit.
     */
    void invoke(ArrayList inputArgs, ArrayList outputArgs);
    
    /**
     * Return the named service provider or null if one does not exist
     * @param name the name of the service provider
     */
    IServiceProvider getServiceProvider(String name);

    /**
     * Return all service providers
     */
    List<IServiceProvider> getAllServiceProviders();

    /**
     * Get all service providers of the specified type
     */
    <T extends IServiceProvider> List<T> getAllServiceProvidersByType(Class<T> clazz);

    /**
     * Indicates if a service by that name is registered
     * 
     * @param name the service provider's name
     * @return true if there is a registered service by that name
     */
    boolean isRegistered(String name);
    
    /**
     * Register a new service provider with the given name. This will
     * not replace any existing one of the same name
     *
     * @param name the service provider's name
     * @param service the service provider object
     */
    void register(String name, IServiceProvider service);
    
    /**
     * Unregister the specified provider. It is not an error to unregister a provider that
     * does not exist
     *
     * @param name the provider's name
     */
    void unregister(String name);

    /**
     * Allows a caller to add a file to a particular plugin's collection of files.
     *
     * @param pluginName the name of the service provider plugin
     * @param type the type of the file
     * @param filename the name under which it should be saved (the
     *                 full path will be determined by the type)
     * @param data the file data
     */
    void addServiceProviderFile(String pluginName, ServiceProviderFileType type, String fileName, byte[] data) throws IOException;

    /**
     * This will both unregister the service provider and remove it
     * from the registry of service providers so that it will not run
     * again. There is no recovery from this
     */
    void removeServiceProvider(String name);

    /**
     * Restart (or start for the first time, if new) the plug-in with the
     * given name
     */
    void restartServiceProvider(String name) throws ServiceProviderException;
}
