package com.nextlabs.pf.engine.destiny;

/*
 * Created on Dec 13, 2019
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import java.io.IOException;
import java.util.List;

public interface IXACMLPolicyFinderModule {
    public void setPolicies(List<String> policiesAndPolicySets);
}
