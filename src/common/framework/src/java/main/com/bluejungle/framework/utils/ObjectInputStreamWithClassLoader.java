/*
 * Created on Feb 08, 2017
 *
 * All sources, binaries and HTML pages (C) copyright 2017 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */


package com.bluejungle.framework.utils;

import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ObjectInputStreamWithClassLoader extends ObjectInputStream {
    private ClassLoader classLoader;
    
    public ObjectInputStreamWithClassLoader(InputStream in) throws IOException {
        this(in, ObjectInputStreamWithClassLoader.class.getClassLoader());
    }
    
    public ObjectInputStreamWithClassLoader(InputStream in, ClassLoader classLoader) throws IOException {
        super(in);
        this.classLoader = classLoader;
    }
    
    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
        if (classLoader == null) {
            return Class.forName(desc.getName());
        }
        
        return Class.forName(desc.getName(), false, classLoader);
    }
}
