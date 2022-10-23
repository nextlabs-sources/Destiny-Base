/*
 * Created on May 10, 2016
 *
 * All sources, binaries and HTML pages (C) copyright 2016 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/client/openaz/src/java/main/com/nextlabs/openaz/pepapi/Host.java#1 $:
 */

package com.nextlabs.openaz.pepapi;

import org.apache.openaz.xacml.api.Identifier;

import com.nextlabs.openaz.utils.Constants;

/**
 * Container that holds information about the host machine.
 *
 * The associated mapper is {@link com.nextlabs.openaz.pepapi.HostMapper}
 */
public final class Host extends CategoryContainer {
    public static final Identifier CATEGORY_ID = Constants.ID_NEXTLABS_ATTRIBUTE_CATEGORY_HOST;
    public static final int LOCAL_HOST = 0x7f000001;
    
    private String ipAddress;
    private String hostName;

    private Host() {
        super(CATEGORY_ID);
    }

    /**
     * Creates a new Host instance from a host name
     *
     * @param hostName a fully qualified host name
     * @return
     */
    public static Host newInstance(String hostName) {
        Host h = new Host();
        h.hostName = hostName;
        h.ipAddress = null;
        return h;
    }

    /**
     * Creates a new Host instance from the specified ip address expressed as an integer. For localhost
     * use Host.LOCAL_HOST.
     *
     * @param ipAddress
     * @return
     */
    public static Host newInstance(int ipAddress) {
        Host h = new Host();
        h.hostName = null;
        h.ipAddress = Integer.toString(ipAddress);

        return h;
    }

    /**
     * Get the ip address as a String
     *
     * @return
     * @note if the object was initialized by name, this will be null
     */
    public String getIPAddress() {
        return ipAddress;
    }

    /**
     * Get the hostname
     *
     * @return
     * @note if the object was initialized by ip address, this will be null
     */
    public String getHostName() {
        return hostName;
    }
}
