/*
 * All sources, binaries and HTML pages (C) Copyright 2006 by Blue Jungle Inc,
 * Redwood City, CA. Ownership remains with Blue Jungle Inc.
 * All rights reserved worldwide.
 *
 * @author sergey
 */

package com.bluejungle.framework.datastore.hibernate.usertypes;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;
import net.sf.hibernate.type.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class lets you save arrays of longs as a single string.
 */
public class LongArrayAsString implements UserType {
    private static final Log LOG = LogFactory.getLog(LongArrayAsString.class.getName());
    public static final Type TYPE;
    private static final int[] SQL_TYPES =  new int[] {Types.VARCHAR};

    static {
        Type typeCreated = null;
        try {
            typeCreated = Hibernate.custom(StringArrayAsString.class);
        } catch (HibernateException exception) {
            LOG.error("Failed to create StringArrayAsString Type", exception);
            typeCreated = null;
        }
        
        TYPE = typeCreated;
    }

    /**
     * @see UserType#deepCopy(Object)
     */
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    /**
     * @see UserType#equals(Object, Object)
     */
    public boolean equals(Object x, Object y) throws HibernateException {
        long[] lhs = null, rhs = null;
        if ( x instanceof long[] ) {
            lhs = (long[])x;
        }
        if ( y instanceof long[] ) {
            rhs = (long[])y;
        }
        if ( lhs == null && rhs == null ) {
            return true;
        }
        if ( lhs == null || rhs == null ) {
            return false;
        }
        if ( lhs.length != rhs.length ) {
            return false;
        }
        for ( int i = 0 ; i != lhs.length ; i++ ) {
            if (lhs[i] != rhs[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see UserType#isMutable()
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * @see UserType#nullSafeGet(ResultSet, String[], Object)
     */
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        String str = rs.getString(names[0]);
        if ( str != null && !rs.wasNull() ) {
            return fromStringValue(str);
        } else {
            return null;
        }
    }

    /**
     * @see UserType#nullSafeSet(PreparedStatement, Object, int)
     */
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        st.setString(index, toString(value));
    }

    /**
     * @see UserType#returnedClass()
     */
    public Class<long[]> returnedClass() {
        return long[].class;
    }

    /**
     * @see UserType#sqlTypes()
     */
    public int[] sqlTypes() {
        return SQL_TYPES;
    }
    
    private Object fromStringValue( String value ) throws HibernateException {
        if ( value == null ) {
            return null;
        }
        LongBuffer buf = ByteBuffer.wrap(Base64.getMimeDecoder().decode(value)).asLongBuffer();
        long[] res = new long[buf.limit()];
        buf.get(res);
        return res;
    }
    
    private String toString( Object value ) throws HibernateException {
        if ( value == null ) {
            return null;
        }
        if ( !(value instanceof long[]) ) {
            throw new IllegalArgumentException("value is not a long[]");
        }
        long[] longValue = (long[])value;
        ByteBuffer buf = ByteBuffer.allocate(8*longValue.length);
        buf.asLongBuffer().put(longValue);
        return Base64.getMimeEncoder().encode(buf).toString();
    }

}
