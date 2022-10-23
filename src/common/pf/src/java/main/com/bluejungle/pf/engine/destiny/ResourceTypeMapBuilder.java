package com.bluejungle.pf.engine.destiny;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bluejungle.framework.expressions.DefaultPredicateVisitor;
import com.bluejungle.framework.expressions.IExpression;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.IPredicateVisitor;
import com.bluejungle.framework.expressions.IRelation;
import com.bluejungle.pf.domain.destiny.common.SpecAttribute;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.destiny.resource.ResourceAttribute;
import com.bluejungle.pf.domain.epicenter.evaluation.IEvaluationRequest;

/*
 * Created on Aug 15, 2018
 *
 * All sources, binaries and HTML pages (C) copyright 2018 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

/**
 * This class builds the mappings which let us determine which
 * policies are relevant to a particular resource type (subtype).
 *
 * A policy is applicable to a particular type if an expression
 * contains a resource attribute with that type or if the policy
 * contains no resource expressions at all (and so is applicable
 * to all types)
 */
public class ResourceTypeMapBuilder {
    private IDPolicy[] policies;
    private HashMap<String, BitSet> policiesByResourceType = new HashMap<>();
    private BitSet policiesForAllResourceTypes;
    
    public ResourceTypeMapBuilder(IDPolicy[] policies) {
        this.policies = policies;

        policiesForAllResourceTypes = new BitSet(policies.length);
        buildResourcePolicyMappings();
    }

    private void buildResourcePolicyMappings() {
        for (int i = 0; i < policies.length; i++) {
            Set<String> types = buildResourcePolicyMapping(policies[i]);

            if (types.size() == 0) {
                policiesForAllResourceTypes.set(i);
            } else {
                for (String type : types) {
                    BitSet mapping = policiesByResourceType.get(type);

                    if (mapping == null) {
                        mapping = new BitSet(policies.length);
                        policiesByResourceType.put(type, mapping);
                    }

                    mapping.set(i);
                }
            }
        }

        // Add in the policies that apply to all resource types
        for (Map.Entry<String, BitSet> entry : policiesByResourceType.entrySet()) {
            entry.getValue().or(policiesForAllResourceTypes);
        }
    }

    /**
     * Determines if a policy is relevant to a particular resource
     * type This is the case if the resource type is found in the
     * policy or if no resource types are found (so the policy
     * applies to all types) or if the evaluation request says we
     * shouldn't bother matching the resource type
     *
     * @param policy the policy
     * @param request the request
     */
    public static boolean checkValidity(IDPolicy policy, IEvaluationRequest request) {
        if (request.getUseResourceTypeWhenEvaluating()) {
            Set<String> types = buildResourcePolicyMapping(policy);
            
            return types.size() == 0 || types.contains((String)request.getFromResource().getAttribute(SpecAttribute.DESTINYTYPE_ATTR_NAME).getValue());
        } else {
            return true;
        }
    }

    /**
     * Return policies that match all resource types
     */
    public BitSet getPoliciesForAllResourceTypes() {
        return policiesForAllResourceTypes;
    }
    
    /**
     * Get the resource->policy mapping for a particular policy
     *
     * @param policy the policy
     * @return the set of resources that appear in this policy
     */
    private static Set<String> buildResourcePolicyMapping(IDPolicy policy) {
        ResourceTypeTracker rtt = new ResourceTypeTracker();

        // Resources can appear just about everywhere, even subject components
        IPredicate from = policy.getTarget().getFromResourcePred();
        if (from != null) {
            from.accept(rtt, IPredicateVisitor.PREORDER);
        }

        IPredicate to = policy.getTarget().getToResourcePred();
        if (to != null) {
            to.accept(rtt, IPredicateVisitor.PREORDER);
        }
        
        IPredicate subj = policy.getTarget().getSubjectPred();
        if(subj != null) {
            subj.accept(rtt, IPredicateVisitor.PREORDER);
        }

        IPredicate condition = policy.getConditions();
        if (condition != null) {
            condition.accept(rtt, IPredicateVisitor.PREORDER);
        }

        return rtt.getMappings();
    }

    /**
     * Return a BitSet of all policies that are applicable to this
     * resource type.  This will include policies that specifically
     * reference that resource type and those that reference no types
     * at all (so are applicable by default)
     *
     * @param resourceType the type
     * @return a bitset indicating applicable policies
     */
    public BitSet getApplicablePolicies(String resourceType) {
        if (resourceType == null) {
            return policiesForAllResourceTypes;
        }
        
        BitSet b = policiesByResourceType.get(resourceType);

        if (b == null) {
            return policiesForAllResourceTypes;
        }

        return b;
    }

    static class ResourceTypeTracker extends DefaultPredicateVisitor {
        final HashSet<String> resources = new HashSet<>();
        
        public Set<String> getMappings() {
            return resources;
        }

        @Override
        public void visit(IRelation rel) {
            trackResourceType(rel.getLHS());
            trackResourceType(rel.getRHS());
        }

        private void trackResourceType(IExpression expr) {
            if (expr instanceof ResourceAttribute) {
                resources.add(((ResourceAttribute)expr).getObjectSubTypeName());
            }
        }
    }
}
