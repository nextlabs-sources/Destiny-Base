/*
 * Created on Oct 08, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */
package com.nextlabs.framework.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.DataSource;

public class ByteArrayDataSource implements DataSource {
    private final String contentType;
    private final byte[] bytes;

    public ByteArrayDataSource(byte[] bytes) {
        this("application/octet-stream", bytes);
    }
       
    public ByteArrayDataSource(String contentType, byte[] bytes) {
        super();
        this.contentType = contentType;
        this.bytes = bytes;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }
    
    public String getName() {
        return ByteArrayDataSource.class.getName();
    }
    
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
}
