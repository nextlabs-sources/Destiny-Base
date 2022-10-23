package com.nextlabs.destiny.agent.pdpapi;

import java.util.ArrayList;
import java.util.List;

import com.bluejungle.destiny.agent.pdpapi.IPDPEnforcement;

/*
 * Created on Jul 28, 2022
 *
 * All sources, binaries and HTML pages (C) copyright 2022 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

public class PDPMultiEnforcement implements IPDPMultiEnforcement {
    private List<IPDPEnforcement> enforcements;

    public PDPMultiEnforcement() {
        enforcements = new ArrayList<>();
    }
    
    public void addEnforcement(IPDPEnforcement enforcement) {
        enforcements.add(enforcement);
    }

    public List<IPDPEnforcement> getEnforcements() {
        return enforcements;
    }
}
