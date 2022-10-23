package com.bluejungle.destiny.agent.pdpapi;

/*
 * Created on Jan 19, 2011
 *
 * All sources, binaries and HTML pages (C) copyright 2011 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import com.nextlabs.destiny.agent.pdpapi.IPDPGenericCallback;

/**
 * A callback for the <code>PDPQueryDecisionEngine</code>
 */
public interface IPDPSDKCallback extends IPDPGenericCallback<IPDPEnforcement>
{
    IPDPSDKCallback NONE = new IPDPSDKCallback() {
        @Override
        public void callback(IPDPEnforcement ignore) { }
    };
}
