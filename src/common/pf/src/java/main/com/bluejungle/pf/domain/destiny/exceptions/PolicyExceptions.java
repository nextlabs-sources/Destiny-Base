/*
 * Created on Feb 27, 2013
 *
 * All sources, binaries and HTML pages (C) copyright 2013 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.bluejungle.pf.domain.destiny.exceptions;

import java.util.ArrayList;
import java.util.List;

import com.bluejungle.pf.domain.epicenter.exceptions.ICombiningAlgorithm;
import com.bluejungle.pf.domain.epicenter.exceptions.IPolicyExceptions;
import com.bluejungle.pf.domain.epicenter.exceptions.IPolicyReference;

public class PolicyExceptions implements IPolicyExceptions {
    private ICombiningAlgorithm combiningAlgorithm = CombiningAlgorithm.ALLOW_OVERRIDES;
    private List<IPolicyReference> exceptions = new ArrayList<>();

    public PolicyExceptions() {
    }

    public PolicyExceptions(List<IPolicyReference> exceptions, ICombiningAlgorithm combiningAlgorithm) {
        this.exceptions = exceptions;
        this.combiningAlgorithm = combiningAlgorithm;
    }

    public void setCombiningAlgorithm(ICombiningAlgorithm combiningAlgorithm) {
        this.combiningAlgorithm = combiningAlgorithm;
    }

    public ICombiningAlgorithm getCombiningAlgorithm() {
        return combiningAlgorithm;
    }

    public void setPolicies(List<IPolicyReference> exceptions) {
        if (exceptions == null) {
            exceptions = new ArrayList<>();
        }
        
        this.exceptions = exceptions;
    }

    public void addPolicy(IPolicyReference exception) {
        exceptions.add(exception);
    }

    public List<IPolicyReference> getPolicies() {
        return exceptions;
    }
}
