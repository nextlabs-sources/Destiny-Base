package com.nextlabs.destiny.agent.pdpapi;

/*
 * Created on Jan 19, 2011
 *
 * All sources, binaries and HTML pages (C) copyright 2011 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

/**
 * A callback for the <code>PDPPermissions</code>
 */
public interface IPDPPermissionsCallback extends IPDPGenericCallback<IPDPPermissionsResponse>
{
    IPDPPermissionsCallback NONE = new IPDPPermissionsCallback() {
        @Override
        public void callback(IPDPPermissionsResponse ignore) { }
    };
}
