package com.nextlabs.pf.destiny.lib;

/*
 * Created on Dec 12, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Date;

/**
 * This is a DTO for the XACML policies heartbeat request
 */
public class XACMLPoliciesRequestDTO implements Externalizable, Serializable {
    public static final String XACML_POLICIES_SERVICE = "XACMLPoliciesService";
    
    private static final long serialVersionUID = 4181787805981627512L;

    public static final long NO_PREVIOUS_POLICIES_TIMESTAMP = -1;
    
    /**
     * The time of the last policy update.
     */
    private long timestamp;
    
    /**
     * Serialization constructor
     */
    public XACMLPoliciesRequestDTO() {
        timestamp = NO_PREVIOUS_POLICIES_TIMESTAMP; 
    }

    public XACMLPoliciesRequestDTO(Date timestamp) {
        this.timestamp = timestamp == null ? NO_PREVIOUS_POLICIES_TIMESTAMP : timestamp.getTime();
    }

    /**
     * Get time of last request
     */
    public Date getTimestamp() {
        return new Date(timestamp);
    }

    /**
     * Reads the contents of this object from the ObjectInput
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        timestamp = in.readLong();
    }

    /**
     * Writes the contents of this object to the ObjectOutput
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(timestamp);
    }
}
