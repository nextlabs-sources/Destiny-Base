package com.bluejungle.destiny.server.shared.configuration.impl;

/*
 * All sources, binaries and HTML pages (C) Copyright 2006 by Blue Jungle Inc,
 * Redwood City, CA. Ownership remains with Blue Jungle Inc.
 * All rights reserved worldwide.
 *
 * @author sergey
 */

import java.util.ArrayList;
import java.util.List;

import com.bluejungle.destiny.server.shared.configuration.ITrustedDomainsConfigurationDO;

/**
 * This class implements the trusted domains configuration object.
 */
public class TrustedDomainsConfigurationDO implements ITrustedDomainsConfigurationDO {
    private List<String> trustedDomains = new ArrayList<>();

    /**
     * @see com.bluejungle.destiny.server.shared.configuration.ITrustedDomainsConfigurationDO#getTrustedDomains()
     */
    public String[] getTrustedDomains() {
        return trustedDomains.toArray(new String[trustedDomains.size()]);
    }

    /**
     * Adds a list of mutually trusted domains to the collection.
     *
     * Originally the trusted domains were separated by commas and
     * multiple calls to this method would be made. Now, due to
     * changes in the configuration management code, it might be the
     * case that one call can include multiple sets of mutually
     * trusted domains. These sets might be separated by either
     * semi-colons or carriage returns
     *
     * @param one or more sets of mutually trusted domains
     */
    public void addMutuallyTrusted(String domains) {
        if (domains == null) {
            return;
        }

        for (String trustedSet : domains.split("[;\n]")) {
            trustedDomains.add(trustedSet);
        }
        
    }
}
