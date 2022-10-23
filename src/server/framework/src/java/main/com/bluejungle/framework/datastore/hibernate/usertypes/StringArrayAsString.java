/*
 * All sources, binaries and HTML pages (C) Copyright 2006 by Blue Jungle Inc,
 * Redwood City, CA. Ownership remains with Blue Jungle Inc.
 * All rights reserved worldwide.
 *
 * @author sergey
 */

package com.bluejungle.framework.datastore.hibernate.usertypes;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;
import net.sf.hibernate.type.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class lets you save small String arrays as a single string.
 * 
 * See also MultivalueHelper, which has a lot of the same
 * functionality. Refactor?
 */
public class StringArrayAsString implements UserType {
    public static final char SEPARATOR = ':';
    private static final Log LOG = LogFactory.getLog(StringArrayAsString.class.getName());
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
     * @see net.sf.hibernate.UserType#deepCopy(java.lang.Object)
     */
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    /**
     * @see net.sf.hibernate.UserType#equals(java.lang.Object, java.lang.Object)
     */
    public boolean equals(Object x, Object y) throws HibernateException {
        String[] lhs = null, rhs = null;
        if ( x instanceof String[] ) {
            lhs = (String[])x;
        }
        if ( y instanceof String[] ) {
            rhs = (String[])y;
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
            if ( lhs[i] != null ) {
                if ( !lhs[i].equals(rhs[i]) ) {
                    return false;
                }
            } else {
                if ( rhs[i] != null ) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @see net.sf.hibernate.UserType#isMutable()
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * @see net.sf.hibernate.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
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
     * @see net.sf.hibernate.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
     */
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        st.setString(index, toString(value));
    }

    /**
     * @see net.sf.hibernate.UserType#returnedClass()
     */
    public Class<String[]> returnedClass() {
        return String[].class;
    }

    /**
     * @see net.sf.hibernate.UserType#sqlTypes()
     */
    public int[] sqlTypes() {
        return SQL_TYPES;
    }
    
    public static Object fromStringValue( String value ) {
        if ( value == null ) {
            return null;
        }
        List<String> res = new ArrayList<String>();
        String tmp = null;
        for ( int i = 0 ; i != value.length() ; i++ ) {
            if ( tmp == null ) {
                tmp = "";
            }
            if ( value.charAt(i) == SEPARATOR ) {
                res.add(tmp);
                tmp = null;
            } else {
                if ( value.charAt(i) == '\\' && i != value.length()-1 ) {
                    i++;
                }
                tmp += value.charAt(i);
            }
        }
        if (( tmp != null ) && (tmp.length() > 0)) {
            res.add(tmp);
        }
        return res.toArray(new String[res.size()]);
    }
    
    /**
     * 
     * @param value
     * @return
     * @throws IllegalArgumentException if <code>value</code> is not a String[]
     * @throws NullPointerException if the String array contains null.
     */
    public static String toString(Object value) throws IllegalArgumentException,
            NullPointerException {
        if ( value == null ) {
            return null;
        }
        if ( !(value instanceof String[]) ) {
            throw new IllegalArgumentException("value is not a String[]");
        }
        String[] strValue = (String[])value;

        // Order this so that map building has a way to implement
        // "equals_unordered" (exact match). This is expensive unless
        // the value is in some canonical order
        Arrays.sort(strValue);
        StringBuilder buf = new StringBuilder();
        for ( int i = 0 ; i != strValue.length ; i++ ) {
            if ( strValue[i] == null ) {
                throw new NullPointerException("strValue["+i+"]");
            }
            if ( i != 0 ) {
                buf.append(SEPARATOR);
            }
            for ( int j = 0; j != strValue[i].length() ; j++ ) {
                char ch = strValue[i].charAt(j);
                if ( ch == SEPARATOR || ch == '\\' ) {
                    buf.append('\\');
                }
                buf.append(ch);
            }
        }
        
        buf.append(SEPARATOR);
        
        return buf.toString();
    }
}
