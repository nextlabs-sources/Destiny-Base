/*
 * Created on Jun 21, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.bluejungle.dictionary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.utils.IStreamable;

/**
 * Implementation of <code>IEnrollmentDeltaCookie</code> interface
 *
 * Data sources that support a "delta" enrollment may return a cookie
 * of some sort that must be given in the next update. These can be
 * stored here. Currently this exists for AAD only. AAD stores groups
 * and users separately, so we need separate cookies for both. Thus,
 * the delta type.
 */

public class EnrollmentDeltaCookie implements IEnrollmentDeltaCookie {
    /** Package-private ID for use by Hibernate */
    Long id;

    /** The enrollment with which the record is associated */
    private IEnrollment enrollment;

    /** The type of delta */
    private String deltaType;

    /** The cookie returned by the data source */
    private StreamableString cookie;

    /**
     * Package-private constructor for hibernate
     */
    EnrollmentDeltaCookie() {
    }

    /**
     * This constructor is used by the enrollment session when a commit or
     * rollback operation is performed
     *
     * @param enrollment the enrollment with which the record is associated
     */
    public EnrollmentDeltaCookie(IEnrollment enrollment) {
        this.enrollment = enrollment;
        this.deltaType = null;
        this.cookie = null;
    }

    public IEnrollment getEnrollment() {
        return enrollment;
    }

    public void setDeltaType(String deltaType) {
        this.deltaType = deltaType;
    }
    
    public String getDeltaType() {
        return deltaType;
    }

    public void setCookie(StreamableString cookie) {
        this.cookie = cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = new StreamableString(cookie);
    }
    
    public StreamableString getCookie() {
        return cookie;
    }

    public String getCookieAsString() {
        return cookie.getString();
    }
    
    // The cookie string might be really, really long:w
    public static class StreamableString implements IStreamable {
        private final ByteArrayOutputStream os = new ByteArrayOutputStream();
        private final Log log = LogFactory.getLog(StreamableString.class.getName());
        private String s;

        public StreamableString() {
            s = null;
        }
        
        public StreamableString(String s) {
            this.s = s;
        }

        public void setString(String s) {
            this.s = s;
        }

        public String getString() {
            return s;
        }
        
        public int getSize() {
            try {
                writeOutput();
            } catch (IOException ioe) {
                log.error("Exception thrown by writeOutput: " + ioe.getMessage());
            }
            return os.size();
        }

        public InputStream getStream() {
            try {
                writeOutput();
            } catch(IOException ioe) {
                log.error("Exception thrown by writeOutput: " + ioe.getMessage());
                return new ByteArrayInputStream(new byte[0]);
            }

            return new ByteArrayInputStream(os.toByteArray());
        }
        
        private void writeOutput() throws IOException {
            if (os.size() != 0) {
                return;
            }
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeUTF(getClass().getName());
            writeExternal(oos);
            oos.flush();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // writeUTF can't handle strings > 64K in size, which this might be.
            // Use writeObject for safety
            out.writeObject(s);
        }

        public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException {
            s = (String)in.readObject();
        }
    }
}
