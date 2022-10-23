package com.bluejungle.pf.destiny.lib;

/*
 * All sources, binaries and HTML pages (C) copyright 2008 by NextLabs, Inc.
 * San Mateo CA, Ownership remains with NextLabs, Inc.
 * All rights reserved worldwide.
 *
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/common/pf/src/java/main/com/bluejungle/pf/destiny/lib/ClientInformationRequestDTO.java#1 $
 */

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Date;

/**
 * This is a Data Transfer Object for the client information request.
 *
 * @author Sergey Kalinichenko, Alan Morgan
 */
public class ClientInformationRequestDTO implements Externalizable, Serializable {
    // Allatori obfuscation can change this and the actual value right
    // now is different, but for backwards compabilitiy we should use
    // the "old" value. This is also the value you get from the latest
    // *un*obfuscated class files
    //
    // We have a problem here - this should have been done in 9.0, but
    // wasn't. That means that this value is the same as < 9.0 and
    // will continue to work >= 9.1, but does not work for 9.0
    //
    // This means that a 9.0 PDP won't get the client
    // information. This is only relevant for WDE, which didn't have a
    // 9.0 version and this was an obscure feature at the best of
    // times, so (fingers crossed) not a problem.
    public static final long serialVersionUID = -4553642011986453409L;
    
    /**
     * The name of the service registered with the heartbeat manager
     * and the heartbeat provider.
     */
    public static String CLIENT_INFORMATION_SERVICE = "ClientInformationService";

    /**
     * The time when the current client info was obtained.
     */
    private long timestamp;

    /**
     * The type of the UID stored in the uids array.
     */
    private String uidType;

    /**
     * An array of UIDs of the users who logged into the requesting computer.
     */
    private String[] uids;

    /**
     * Serialization constructor
     */
    public ClientInformationRequestDTO() {
    }

    /**
     * Constructs a DTO with the timestamp and the UIDs
     *
     * @param timestamp the timestamp.
     * @param uids the array of UIDs.
     */
    public ClientInformationRequestDTO(Date timestamp, String uidType, String[] uids) {
        this.timestamp = timestamp.getTime();
        this.uidType = uidType;
        this.uids = uids;
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
     * Obtains the type of the UID.
     *
     * @return the type of the UID.
     */
    public String getUidType() {
        return uidType;
    }

    /**
     * Obtains the UIDs.
     *
     * @return the UIDs.
     */
    public String[] getUids() {
        return uids;
    }

    /**
     * Reads the content of this object from the ObjectInput.
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        timestamp = in.readLong();
        uidType = in.readUTF();
        uids = new String[in.readInt()];
        for (int i = 0 ; i != uids.length ; i++) {
            uids[i] = in.readUTF();
        }
    }

    /**
     * Writes the content of this object to the ObjectOutput.
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(timestamp);
        out.writeUTF(uidType);
        out.writeInt(uids.length);
        for (String s : uids) {
            out.writeUTF(s);
        }
    }

}
