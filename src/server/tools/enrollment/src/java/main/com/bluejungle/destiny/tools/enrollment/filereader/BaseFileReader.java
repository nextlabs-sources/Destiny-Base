/*
 * Created on Jul 20, 2009
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2008 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.tools.enrollment.filereader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.bluejungle.destiny.services.enrollment.types.EnrollmentProperty;
import com.bluejungle.destiny.tools.enrollment.filter.FileFormatException;
import com.nextlabs.shared.tools.MaskedConsolePrompt;

/**
 * @author hchan
 */

public abstract class BaseFileReader {
    private boolean promptForMissingData = true;
    protected final List<EnrollmentProperty> propertyList;
    protected final File file;

    // If the user doesn't specify a critical value on the command line or data file,
    // we can prompt for it or just throw an error
    
    protected abstract String getFileDescription();
    
    @Deprecated
    public BaseFileReader(String filePath, boolean promptForMissingData) throws FileNotFoundException, IOException,
            FileFormatException {
        this(new File(filePath), promptForMissingData);
    }
    
    /**
     * 
     * @param file
     * @throws FileNotFoundException if the file doesn't not exist
     * @throws IOException if any occur during reading the file
     * @throws FileFormatException if the file contains invalid format or missing key
     */
    public BaseFileReader(File file, boolean promptForMissingData) throws FileNotFoundException, IOException, FileFormatException {
        this.promptForMissingData = promptForMissingData;
        
        if (!file.exists()) {
            throw new FileNotFoundException(getFileDescription() + ", \"" + file.getAbsolutePath()
                    + "\", doesn't exist.");
        }
        
        if (!file.canRead()) {
            throw new IOException(getFileDescription() + ", \"" + file.getAbsolutePath()
                    + "\", is not readable.");
        }
        
        this.file = file;
        
        Properties properties = new CaseInsensitiveProperties();
        properties.load(new FileInputStream(file));
        
        validate(properties);
        
        propertyList = convert(properties);
    }
    
    private class CaseInsensitiveProperties extends Properties{
        CaseInsensitiveProperties() {
            super();
        }

        @Override
        public synchronized Object put(Object key, Object value) {
            return super.put(((String)key).toLowerCase(), value);
        }

        @Override
        public String getProperty(String key, String defaultValue) {
            return super.getProperty(key.toLowerCase(), defaultValue);
        }

        @Override
        public String getProperty(String key) {
            return super.getProperty(key.toLowerCase());
        }

        @Override
        public synchronized boolean containsKey(Object key) {
            return super.containsKey(((String) key).toLowerCase());
        }

        @Override
        public synchronized Object remove(Object key) {
            return super.remove(((String) key).toLowerCase());
        }
    }
    
    protected abstract void validate(Properties properties) throws FileFormatException;
    
    protected List<EnrollmentProperty> convert(Properties properties){
        List<EnrollmentProperty> list = new ArrayList<EnrollmentProperty>(properties.size());
        for (Map.Entry<?, ?> e : properties.entrySet()) {
            String key = ((String)e.getKey()).toLowerCase();
            String value = (String)e.getValue();
            EnrollmentProperty enrollP = new EnrollmentProperty();
            enrollP.setKey(key);
            enrollP.setValue(new String[] {value});
            list.add(enrollP);
        }
        
        return list;
    }
    
    public List<EnrollmentProperty> getProperties() {
        return this.propertyList;
    }
    
    /**
     * 
     * @param properties
     * @param key
     * @return the value that is corresponding to the key
     * @throws FileFormatException if <code>properties</code> doesn't not contain <code>key</code>
     */
    protected String getRequireString(Properties properties, String key) throws FileFormatException{
        String value = properties.getProperty(key);
        if ((value == null) || (value.trim().length() == 0)) {
            throw new FileFormatException(String.format("\"%s\" is required in file, \""
                    + file.getAbsolutePath() + "\"", key));
        }
        return value;
    }

    protected String getProtectedInput(String prompt) throws FileFormatException {
        return getProtectedInput(prompt, "Unable to read protected data");
    }

    protected String getPasswordFromUser(String user) throws FileFormatException {
        return getProtectedInput("Enter password of " + user + ":", "Password is missing from configuration file");
    }
    
    protected String getProtectedInput(String prompt, String errorString) throws FileFormatException {
        if (!promptForMissingData) {
            throw new FileFormatException(errorString);
        }
        
        char[] protectedData = null;

        try {
            protectedData = MaskedConsolePrompt.getPassword(System.in, prompt);
        } catch (IOException e) {
            throw new FileFormatException("Unable to read protected input", e);
        }
        if (protectedData == null || protectedData.length == 0) {
            throw new FileFormatException("No data entered");
        }

        return String.valueOf(protectedData);
    }
}
