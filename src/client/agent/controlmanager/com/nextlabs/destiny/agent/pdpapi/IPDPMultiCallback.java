package com.nextlabs.destiny.agent.pdpapi;

/*
 * Created on Jul 28, 2022
 *
 * All sources, binaries and HTML pages (C) copyright 2022 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

public interface IPDPMultiCallback extends IPDPGenericCallback<IPDPMultiEnforcement>
{
    IPDPMultiCallback NONE = new IPDPMultiCallback() {
        @Override
        public void callback(IPDPMultiEnforcement ignore) { }
    };
}
