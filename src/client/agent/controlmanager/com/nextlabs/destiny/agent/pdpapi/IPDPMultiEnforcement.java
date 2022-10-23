package com.nextlabs.destiny.agent.pdpapi;

import com.bluejungle.destiny.agent.pdpapi.IPDPEnforcement;

import java.util.List;

/*
 * Created on Jul 28, 2022
 *
 * All sources, binaries and HTML pages (C) copyright 2022 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

public interface IPDPMultiEnforcement {
    void addEnforcement(IPDPEnforcement enf);
    
    List<IPDPEnforcement> getEnforcements();
}
