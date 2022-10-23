/*
 * Created on Apr 24, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
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

/**
 * Use with caution. This is an object stream that will read objects
 * from a stream based on their serialized class name, ignoring the
 * serialVersionUID. We need this in some cases because we didn't
 * declare serialVersionUIDs for some classes and allatori obfuscation
 * changed their ids, introducing an "incompatibility".
 */
public class NameBasedObjectInputStream extends ObjectInputStream {
    private Class clazz;
    
    public NameBasedObjectInputStream (InputStream in, Class clazz) throws IOException {
        super(in);
        this.clazz = clazz;
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();

        if (resultClassDescriptor.getName().equals(clazz.getName())) {
            resultClassDescriptor = ObjectStreamClass.lookup(clazz);
        }

        return resultClassDescriptor;
    }
}
