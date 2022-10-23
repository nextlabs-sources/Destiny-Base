package com.bluejungle.pf.domain.destiny.policy;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.bluejungle.framework.comp.ComponentInfo;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IHasComponentInfo;

/**
 * Originally this class did more management of existing policies, but those
 * capabilities were never used and now it's just a simple factory class (and doesn't
 * need to be a component, either)
 * 
 * @author Sasha Vladimirov
 */

public class PolicyManager implements IHasComponentInfo<PolicyManager>, IDPolicyManager {
    private static final String CLASSNAME = PolicyManager.class.getName();
    private final Set<IDPolicy> allPolicies = new HashSet<>();
    private IComponentManager manager;
    
    public ComponentInfo<PolicyManager> getComponentInfo() {
        return COMP_INFO;
    }

    /**
     * Creates a new named policy.
     * 
     * @requires name is not null.
     * @param name the name of the new policy.
     */
    public IDPolicy newPolicy( Long id, String name ) {
        return new Policy( id, name );
    }
}
