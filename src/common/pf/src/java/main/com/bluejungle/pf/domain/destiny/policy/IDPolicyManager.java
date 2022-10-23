package com.bluejungle.pf.domain.destiny.policy;

/*
 * Created on Jan 25, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */

import java.util.Collection;
import java.util.Set;

import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.LifestyleType;

/**
 * @author sasha
 */

public interface IDPolicyManager {
    String CLASSNAME = IDPolicyManager.class.getName();
    
    ComponentInfo<PolicyManager> COMP_INFO = 
        new ComponentInfo<PolicyManager>(CLASSNAME, PolicyManager.class.getName(), CLASSNAME, LifestyleType.SINGLETON_TYPE);
    
    /**
     * Creates a new named policy.
     * 
     * @requires name is not null.
     * @param name name of the new policy.
     */
    public IDPolicy newPolicy( Long id, String name);
}
