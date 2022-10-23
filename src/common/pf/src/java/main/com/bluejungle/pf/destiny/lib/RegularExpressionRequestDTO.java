package com.bluejungle.pf.destiny.lib;

/*
 * Created on Nov 03, 2008
 *
 * All sources, binaries and HTML pages (C) copyright 2008 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 */

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Date;

/**
 * This is a Data Transfer Object for the reqular expression request.
 *
 * @author Alan Morgan
 */

public class RegularExpressionRequestDTO implements Externalizable, Serializable {
    public static final String REGULAR_EXPRESSION_SERVICE = "RegularExpressionInformationService";

    // Previously we did not declare this value, which led to some problems
    // when allatori obfuscation changed the (computed) serialVersionUID. Now
    // we have it fixed, so there should be no problems going forward.
    //
    // For backwards compatibility (old and new pdps have classes with different
    // ids) we use the NameBasedObjectInputStream to read this class.
    public static final long serialVersionUID = -8415589154327270332L;


    /**
     * The time when the current client info was obtained.
     */
    private long timestamp;

    /**
     * Serialization constructor
     */
    public RegularExpressionRequestDTO() {
    }

    public RegularExpressionRequestDTO(Date timestamp) {
        this.timestamp = timestamp.getTime();
    }

    /**
     * Obtains the timestamp.
     *
     * @return the timestamp.
     */
    public Date getTimestamp() {
        return new Date(timestamp);
    }

    /**
     * Reads the content of this object from the ObjectInput.
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        timestamp = in.readLong();
    }

    /**
     * Writes the content of this object to the ObjectOutput.
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(timestamp);
    }

}
