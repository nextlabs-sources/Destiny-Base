package com.nextlabs.destiny.web.delegadmin.helper;

import java.util.BitSet;
import java.util.List;

import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.engine.destiny.EvaluationRequest;
import com.bluejungle.pf.engine.destiny.ITargetResolver;

/**
 * Delegation target resolver
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class DelegationTargetResolver implements ITargetResolver {

	// private final String INAPPLICABLE_POLICY = "NOT_APPLICABLE";
	private IDPolicy[] policies;
	private BitSet applicables;

	public DelegationTargetResolver(List<IDPolicy> parsedPolicies) {
		this.policies = parsedPolicies.toArray(new IDPolicy[parsedPolicies.size()]);
	}

	public DelegationTargetResolver(IDPolicy[] policies) {
		this.policies = policies;
	}

	/**
	 * @see ITargetResolver#getApplicablePolicies(EvaluationRequest)
	 */
	@Override
	public BitSet getApplicablePolicies(EvaluationRequest request) {
		this.applicables = getApplicables(this.policies, request);
		return (BitSet) applicables.clone();
	}

	public BitSet getApplicables() {
		return applicables;
	}

	/**
	 * @see ITargetResolver#getPolicies()
	 */
	@Override
	public IDPolicy[] getPolicies() {
		return policies;
	}

	public BitSet getApplicables(IDPolicy[] policies, EvaluationRequest request) {
		BitSet applicables = new BitSet(policies.length);
		int i = 0;
		for (IDPolicy policy : policies) {
			applicables.set(i, policy.getTarget().getActionPred().match(request));
			i++;
		}

		return applicables;
	}
}
