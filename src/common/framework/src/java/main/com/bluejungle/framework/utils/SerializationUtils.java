/*
 * Created on Apr 10, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2008 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc., All rights reserved
 * worldwide.
 *
 * @author amorgan
 */
package com.bluejungle.framework.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Some handy functions for wrapping objects up as strings
 */
  
public class SerializationUtils {
    private static final Log LOG = LogFactory.getLog(SerializationUtils.class);

    public static String wrapSerializable(Serializable obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
            return null;
        }

        return Base64.getMimeEncoder().encodeToString(baos.toByteArray());
    }
    
    public static Serializable unwrapSerialized(String str) {
        Serializable ret = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getMimeDecoder().decode(str)));
            ret = (Serializable)ois.readObject();
        } catch(IOException e) {
            LOG.info(str, e);
        } catch (ClassNotFoundException e) {
            LOG.info(str, e);
        }
                           
        return ret;
    }

    public static Serializable unwrapSerialized(String str, ClassLoader classLoader) {
        Serializable ret = null;
        try {
            ObjectInputStream ois = new ObjectInputStreamWithClassLoader(new ByteArrayInputStream(Base64.getMimeDecoder().decode(str)), classLoader);

            ret = (Serializable)ois.readObject();
        } catch (IOException e) {
            LOG.info(str, e);
        } catch (ClassNotFoundException e) {
            LOG.info(str, e);
        }

        return ret;
    }

    public static String wrapExternalizable(Externalizable obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            obj.writeExternal(oos);
            oos.flush();
        } catch (IOException e) {
            return null;
        }

        return Base64.getMimeEncoder().encodeToString(baos.toByteArray());
    }

    public static void unwrapExternalized(String str, Externalizable obj) {
        Externalizable ret = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getMimeDecoder().decode(str)));
            obj.readExternal(ois);
        } catch (IOException e) {
            LOG.info(str, e);
        } catch (ClassNotFoundException e) {
            LOG.info(str, e);
        }
    }

    public static void unwrapExternalized(String str, Externalizable obj, ClassLoader classLoader) {
        Externalizable ret = null;
        try {
            ObjectInputStream ois = new ObjectInputStreamWithClassLoader(new ByteArrayInputStream(Base64.getMimeDecoder().decode(str)), classLoader);
            obj.readExternal(ois);
        } catch (IOException e) {
            LOG.info(str, e);
        } catch (ClassNotFoundException e) {
            LOG.info(str, e);
        }
    }
}
