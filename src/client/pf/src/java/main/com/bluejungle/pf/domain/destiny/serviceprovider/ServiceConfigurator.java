/*
 * Created on Jan 14, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.bluejungle.pf.domain.destiny.serviceprovider;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Integer;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.bluejungle.pf.domain.destiny.serviceprovider.IServiceProvider;
import com.bluejungle.pf.domain.destiny.serviceprovider.ServiceProviderException;
import com.nextlabs.pf.domain.destiny.serviceprovider.IConfigurableServiceProvider;
import com.nextlabs.pf.domain.destiny.serviceprovider.ServiceProviderFileType;

/**
 * @author hchan
 */

public class ServiceConfigurator {
    private static final Log LOG = LogFactory.getLog(ServiceConfigurator.class);

    /*
     * all the key is case sensitive.
     */
    private static final String NAME = "name";
    private static final String PRIORITY = "priority";
    private static final String JAR_PATH = "jar-path";
    private static final String JAR_FILE_NAME = "jar-file";
    private static final String PROVIDER_CLASS_MANIFEST = "Provider-Class";
    //the token is case insensitive
    private static final String NEXTLABS_FOLDER_TOKEN = "[nextlabs]";
    
    private FileClassLoader classLoader;
    private final IServiceProviderManager manager;
    private final String jserviceRoot;
    
    private Map<String, File> serviceProperties = new HashMap<>();
    
    ServiceConfigurator(ServiceProviderManager manager, FileClassLoader classLoader, String jserviceRoot){
        this.classLoader = classLoader;
        this.manager = manager;
        this.jserviceRoot = jserviceRoot;
    }
    
    ServiceConfigurator(ServiceProviderManager manager, String jserviceRoot){
        this(manager, new FileClassLoader(Thread.currentThread().getContextClassLoader()), jserviceRoot);
    }
    
    protected FileClassLoader getFileClassLoader(){
        return classLoader;
    }
    
    /**
     * read all configuration files under
     * <code>jserviceRoot/config</code>, initialize each service
     * provider.  Continue as much as possible after errors
     */
    public void init(){
        File configFolder = new File(jserviceRoot + "/config");
        assert configFolder != null;
        //check folder
        if (!configFolder.exists()) {
            LOG.warn("The directory " + configFolder.getAbsolutePath() + " is configured for PDP plug-ins, but does not exist");
            return;
        } else if (!configFolder.canRead()) {
            LOG.error("The directory " + configFolder.getAbsolutePath() + " is configured for PDP plug-ins, but can not be read");
            return;
        } else if( !configFolder.isDirectory() ){
            LOG.error("The path " + configFolder.getAbsolutePath() + " is configured for PDP plug-ins, but is not a folder");
            return;
        }

        LOG.debug("Adding all jars under ext to classloader");
        addThirdPartyJarsToClassLoader();
        
        LOG.debug("Loading properties files in " + configFolder.getAbsolutePath());

        //get all configuration
        File[] propertiesFiles = getAllConfigurationFiles(configFolder);

        Map<Integer, ArrayList<ConfigurationSummary>> configMap = new TreeMap<Integer, ArrayList<ConfigurationSummary>>();

        
        //and load each configuration
        for(File propertiesFile : propertiesFiles){
            try {
                LOG.debug("Loading " + propertiesFile.getAbsolutePath());
                ConfigurationSummary config = loadConfiguration(propertiesFile);

                // Removing a service requires that we can get 
                serviceProperties.put(config.getServiceName(), propertiesFile);
                
                ArrayList<ConfigurationSummary> configList = configMap.get(config.getPriority());

                if (configList == null) {
                    configList = new ArrayList<ConfigurationSummary>();
                    configMap.put(config.getPriority(), configList);
                }

                configList.add(config);
            } catch (ServiceConfiguratorException e) {
                LOG.error(e.getMessage() + " Location: " + propertiesFile.getAbsolutePath()
                        + " Skip to next file.");
            }
        }

        
        for (ArrayList<ConfigurationSummary> configList : configMap.values()) {
            for (ConfigurationSummary config : configList) {
                try {
                    LOG.debug("Loading " + config.getPath());
                    Class<?> clazz = loadJar(new File(config.getPath()), getFileClassLoader());

                    initClass(config.getServiceName(), clazz, config.getProperties());
                } catch (ServiceConfiguratorException e) {
                    LOG.error(e.getMessage() + " when loading jar at " + config.getPath() + ". Skipping to next file");
                }
            }
        }
    }

    /**
     * Third party jars will be stored under "ext", possibly in sub-directories.
     * Find all of them and add them to the classloader
     */
    private void addThirdPartyJarsToClassLoader() {
        String externalJarsFolder = jserviceRoot + "/ext";

        File external = new File(externalJarsFolder);

        addJarsToClassLoader(external);
    }

    private void addJarsToClassLoader(File folder) {
        File[] allFiles = folder.listFiles();

        if (allFiles == null) {
            return;
        }

        for (File f : allFiles) {
            if (f.isDirectory()) {
                addJarsToClassLoader(f);
            } else if (f.isFile()) {
                // Is it a jar?
                if (f.getName().endsWith(".jar")) {
                    try {
                        LOG.debug("Adding " + f.getName() + " to service provider classloader");
                        classLoader.addFile(f);
                    } catch (MalformedURLException e) {
                        LOG.warn("Unable to add jar " + f.getName() + " to service provider classloader");
                    }
                }
            }
        }
    }
    
    /**
     * Deletes the provider from the system
     */
    public void deleteProvider(String name) {
        LOG.debug("Deleting service provider " + name);
        
        File propertiesFile = serviceProperties.get(name);

        if (propertiesFile == null) {
            LOG.warn("Unable to delete provider " + name + " because its properties file is unknown");
            return;
        }

        try {
            propertiesFile.delete();
        } catch (SecurityException se) {
            LOG.warn("Unable to delete properties file of provider " + name, se);
        }
    }
    
    public void restartServiceProvider(String name) throws ServiceProviderException {
        LOG.debug("Restarting service provider " + name);
        
        // These might have changed, so reload
        addThirdPartyJarsToClassLoader();
        
        File properties = serviceProperties.get(name);

        if (properties == null) {
            LOG.warn("No properties file for provider " + name + ". Can not restart");
            return;
        }

        LOG.debug("Loading properties file for " + name + " from " + properties.getAbsolutePath());
        
        try {
            ConfigurationSummary config = loadConfiguration(properties);
            
            Class<?> clazz = loadJar(new File(config.getPath()), getFileClassLoader());

            initClass(config.getServiceName(), clazz, config.getProperties());
        } catch (ServiceConfiguratorException e) {
            throw new ServiceProviderException("Unable to create provider " + name, e);
        }
    }
    
    public void addServiceProviderFile(String pluginName, ServiceProviderFileType type, String fileName, byte[] data) throws IOException {
        String fullFileName = jserviceRoot;

        if (type == ServiceProviderFileType.MAIN_JAR) {
            fullFileName += "/jar/" + fileName;
        } else if (type == ServiceProviderFileType.MISC_FILE) {
            fullFileName += "/config/" + fileName;
        } else if (type == ServiceProviderFileType.PROPERTIES_FILE) {
            fullFileName += "/config/" + fileName;

            File prevPropertiesFile = serviceProperties.get(pluginName);

            if (prevPropertiesFile != null) {
                // Delete it, just in case someone is updating a properties file with a different name. We'll put it back later
                try {
                    prevPropertiesFile.delete();
                } catch (SecurityException e) {
                    LOG.warn("Unable to delete old properties file " + prevPropertiesFile.getAbsolutePath());
                }
            }
            // New properties file. Remember this so that we can restart (if necessary)
            serviceProperties.put(pluginName, new File(fullFileName));
        } else if (type == ServiceProviderFileType.SUPPORTING_JAR) {
            fullFileName += "/ext/" + fileName;
        }

        LOG.info("Writing " + type.getName() + " information for plug-in " + pluginName + " to " + fullFileName);

        // Ensure the sub-directories exist
        File addedFile = new File(fullFileName);
        File parent = addedFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            LOG.warn("Unable to create folders for " + fullFileName);
            throw new IOException("Unable to create folders for " + fullFileName);
        }

        // And now we write
        OutputStream os = new FileOutputStream(addedFile);
        os.write(data);
        os.close();
    }

    private class ServiceConfiguratorException extends Exception{
        public ServiceConfiguratorException(String message, Throwable cause) {
            super(message, cause);
        }

        public ServiceConfiguratorException(String message) {
            super(message);
        }

        @Override
        public String getMessage() {
            Throwable cause = getCause();
            return cause != null
                ? super.getMessage() + " " + cause.getMessage()
                : super.getMessage();
        }
    }
    
    private class ConfigurationSummary {
        private final int loadPriority;
        private final String serviceName;
        private final String filePath;
        private final Properties properties;

        private ConfigurationSummary(String serviceName, String filePath, Properties properties) {
            this(-1, serviceName, filePath, properties);
        }

        private ConfigurationSummary(int loadPriority, String serviceName, String filePath, Properties properties) {
            this.loadPriority = loadPriority;
            this.serviceName = serviceName;
            this.filePath = filePath;
            this.properties = properties;
        }

        public int getPriority() {
            return loadPriority;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getPath() {
            return filePath;
        }

        public Properties getProperties() {
            return properties;
        }
    }

    /**
     * return all configuration file under <code>folder</code>.
     * Currently, they are files that ends with .properties. Case-insensitive. And doesn't lookup subdirectory.
     * @param folder to look up the configuration files.
     * @return
     */
    protected File[] getAllConfigurationFiles(File folder){
        File[] propertiesFiles = folder.listFiles(new FileFilter() {
            public boolean accept(File f) {
                //only accept readable files.
                if (f.isFile() && f.canRead()) {
                    String name = f.getName();
                    //case insensitive
                    name = name.toLowerCase();
                    if (name.endsWith(".properties")) {
                        return true;
                    }
                }
                return false;
            }
        });
        
        return propertiesFiles;
    }

    /**
     * load the configuration file and initialize the service.
     * @param file is the configuration file.
     * @throws ServiceConfiguratorException
     */
    protected ConfigurationSummary loadConfiguration(File file) throws ServiceConfiguratorException{
        //load the configuration as java properties file.
        Properties properties = new Properties();
        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ServiceConfiguratorException(file + " doesn't exist.", e);
        }
        try {
            properties.load(is);
        } catch (IOException e) {
            throw new ServiceConfiguratorException("Unable to load configuration.", e);
        }finally{
            try {
                is.close();
            } catch (IOException e) {
                throw new ServiceConfiguratorException("Fail to close inputstream", e);
            }
        }

        return loadConfiguration(properties);
    }
    
    /**
     * load the configuration from the given properties and initialize the service.
     * @param properties properties
     * @throws ServiceConfiguratorException
     */
    protected ConfigurationSummary loadConfiguration(Properties properties) throws ServiceConfiguratorException {
        //check the content, such as name, path, priority, etc
        int priority = 0;
        String priorityStr = properties.getProperty(PRIORITY);

        if (priorityStr != null) {
            try {
                priority = Integer.parseInt(priorityStr);
            } catch (NumberFormatException e) {
                priority = 0;
            }
        }

        String name = properties.getProperty(NAME);
        if(name == null){
            throw new ServiceConfiguratorException("The key, '" + NAME + "' is missing in the configuration.");
        }

        if(manager.isRegistered(name)){
            throw new ServiceConfiguratorException("'" + name + "' is already registered." );
        }
        
        String path = properties.getProperty(JAR_PATH);
        if(path == null){
            String jarName = properties.getProperty(JAR_FILE_NAME);

            if (jarName == null) {
                throw new ServiceConfiguratorException("One of " + JAR_PATH + " or " + JAR_FILE_NAME + " must be defined in the configuration for " + name);
            }

            // Assume it's in the jar directory
            path = jserviceRoot + "/jar/" + jarName;
        }
        
        if (path.toLowerCase().startsWith(NEXTLABS_FOLDER_TOKEN)) {
            String currentWorkingDirStr = System.getProperty("user.dir");
            File currentWorkingDir = new File(currentWorkingDirStr);
            String parent = currentWorkingDir.getParent();
            //The current working dir is the root
            if (parent == null) {
                parent = currentWorkingDirStr;
            }
            path = parent + path.substring(NEXTLABS_FOLDER_TOKEN.length());
        }
    
        return new ConfigurationSummary(priority, name, path, properties);
    }
    
    /**
     * load the jar that specified in the configuration file.
     * @param file is the jar file
     * @param classLoader will be used to load the service class 
     * @throws ServiceConfiguratorException
     */
    protected Class<?> loadJar(File file, FileClassLoader classLoader)
            throws ServiceConfiguratorException {
        // check the jar file
        if (!file.exists()) {
            throw new ServiceConfiguratorException("The file, " + file.getAbsolutePath() + ", doesn't exist.");
        } else if (!file.canRead()) {
            throw new ServiceConfiguratorException("The file, " + file.getAbsolutePath() + ", can not be read.");
        } else if( !file.isFile() ){
            throw new ServiceConfiguratorException("The path, " + file.getAbsolutePath() + ", is not a file.");
        }
        
        //read jar manifest
        Attributes attrs;
        try {
            JarFile jarFile = new JarFile(file);
            Manifest manifest = jarFile.getManifest();
            attrs = manifest.getMainAttributes();
        } catch (IOException e) {
            throw new ServiceConfiguratorException("Unable to load manifest.", e);
        } catch (RuntimeException e){
            throw new ServiceConfiguratorException("Unexpected exception while reading manifest.", e);
        }

        //check manifest
        String providerClass = attrs.getValue(PROVIDER_CLASS_MANIFEST);
        if (providerClass == null || providerClass.trim().length() == 0) {
            throw new ServiceConfiguratorException("providerClass is not definied in the jar.");
        }
        
        //load the jar
        try {
            classLoader.addFile(file);
        } catch (MalformedURLException e) {
            throw new ServiceConfiguratorException("Fail to load the jar.", e);
        }

        //check the class exists in the jar
        Class<?> clazz;
        try {
            clazz = Class.forName(providerClass, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new ServiceConfiguratorException(PROVIDER_CLASS_MANIFEST + " doesn't define correctly in manifest. " + providerClass + " is not the jar.", e);
        }
        
        return clazz;
    }

    /**
     * Initialize and register the given plugin
     *
     * @param name the name under which the plugin should be
     * registered (TODO: Ideally we should get this from the plugin
     * itself)
     * @param clazz the plugin class
     * @param properties configuration properties (if the class uses them)
     *
     * Plugins can fail to start for many reasons. We handle some of
     * them, but if it fails for a reason we don't handle then the PDP
     * itself will fail to start. Catching <code>Throwable</code> is
     * frowned upon, but given that we are executing uknown customer
     * code and anything could go wrong, this would seem like the
     * place to do it.
     */
    @SuppressWarnings("unchecked")
    protected void initClass(String name, Class<?> clazz, Properties properties) throws ServiceConfiguratorException {
        try {
            //check if the class has a correct interface
            if (!IServiceProvider.class.isAssignableFrom(clazz)) {
                throw new ServiceConfiguratorException("providerClass doesn't implement expected interface in the jar.");
            }
            
            //create instance
            Class<IServiceProvider> spClass = (Class<IServiceProvider>)clazz;
            IServiceProvider serviceProvider;
            try {
                serviceProvider = spClass.newInstance();
            } catch (InstantiationException e) {
                throw new ServiceConfiguratorException("Unable to create an instance of " + clazz + ".", e);
            } catch (IllegalAccessException e) {
                throw new ServiceConfiguratorException("Unable to create an instance of " + clazz + ".", e);
            }
            
            if (serviceProvider instanceof IConfigurableServiceProvider) {
                ((IConfigurableServiceProvider)serviceProvider).setProperties(properties);
            }
            
            // init the service
            try {
                serviceProvider.init();
            } catch (NoClassDefFoundError | Exception e){
                throw new ServiceConfiguratorException("Exception during serviceProvider init.", e);
            }
            
            //everything is ok, register it.
            try {
                manager.register(name, serviceProvider);
            } catch (IllegalArgumentException e) {
                throw new ServiceConfiguratorException("'" + name + "' is already registered.", e);
            }
        } catch (ServiceConfiguratorException e) {
            throw e;
        } catch (Throwable t) {
            throw new ServiceConfiguratorException("Error initializing plugin", t);
        }
    }
}



