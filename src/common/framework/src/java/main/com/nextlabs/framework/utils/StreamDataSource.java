/*
 * Created on Sep 23, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */
package com.nextlabs.framework.utils;

import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

/**
 * Convert a stream into a DataSource
 */

public class StreamDataSource implements DataSource {
    private InputStream is;
    private String name;
    private String contentType;
    
    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
        
    @Override
    public InputStream getInputStream() {
        return is;
    }

    public void setInputStream(InputStream is) {
        this.is = is;
    }
        
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }
}
