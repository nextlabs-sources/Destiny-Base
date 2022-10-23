package com.nextlabs.destiny.web.delegadmin.helper;

/*
 * Copyright 2016 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on May 6, 2016
 *
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.bluejungle.framework.domain.IHasId;
import com.bluejungle.framework.expressions.IArguments;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.IExpression;
import com.bluejungle.framework.expressions.IExpressionVisitor;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.IPredicateReference;
import com.bluejungle.framework.expressions.IRelation;
import com.bluejungle.framework.expressions.PredicateConstants;
import com.bluejungle.framework.expressions.Predicates;
import com.bluejungle.framework.expressions.Predicates.DefaultTransformer;
import com.bluejungle.framework.expressions.Predicates.ITransformer;
import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.pf.destiny.parser.DefaultPQLVisitor;
import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.DomainObjectDescriptor;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.common.IDSpec;
import com.bluejungle.pf.domain.destiny.common.IDSpecRef;
import com.bluejungle.pf.domain.destiny.common.SpecBase;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.epicenter.common.SpecType;

/**
 * This will resolve the delegation rule policy's reference ids to actual
 * predicates
 *
 * @author Amila Silva
 * @since 8.0
 */
public class DelegationRuleReferenceResolver {

	private Map<Long, IParsedEntity> daComponentMap = new TreeMap<Long, IParsedEntity>();
	private Map<Long, IParsedEntity> daRuleMap = new TreeMap<Long, IParsedEntity>();
	private long[] policyIds = null;
	private IDPolicy[] parsedPolicies = null;

	/**
	 * create delegation reference resolver
	 * 
	 * @throws PQLException
	 * 
	 * @throws ConsoleException
	 *             throws on any error
	 * 
	 */
	public static DelegationRuleReferenceResolver create(List<String> policyPQLs, List<String> componentPQLs)
			throws PQLException {
		DelegationRuleReferenceResolver resolver = new DelegationRuleReferenceResolver();
		resolver.loadRuleReferenceData(policyPQLs, componentPQLs);

		return resolver;
	}

	private DelegationRuleReferenceResolver() {

	}

	private void loadRuleReferenceData(List<String> policyPQLs, List<String> componentPQLs) throws PQLException {
		daComponentMap.clear();
		daRuleMap.clear();
		loadDelegationRules(policyPQLs);
		loadDelegationComponents(componentPQLs);
	}

	private void loadDelegationComponents(List<String> componentPQLs) throws PQLException {
		for (final String pql : componentPQLs) {
			DomainObjectBuilder.processInternalPQL(pql, new DefaultPQLVisitor() {

				@Override
				public void visitComponent(DomainObjectDescriptor dod, IPredicate pred) {
					final SpecBase spec = new SpecBase(null, SpecType.ILLEGAL, dod.getId(), dod.getName(),
							dod.getDescription(), dod.getStatus(), pred, dod.isHidden());
					IParsedEntity parsed = new IParsedEntity() {
						/*
						 * This flag indicates that the corresponding entity has
						 * been resolved.
						 */
						private boolean resolved = false;

						/**
						 * @see IParsedEntity#getParsed()
						 */
						public IHasId getParsed() {
							return spec;
						}

						/**
						 * @see IParsedEntity#getUnparsed()
						 */
						// public IHasPQL getUnparsed() {
						// return entity;
						// }

						/**
						 * @see IParsedEntity#getName()
						 */
						public String getName() {
							return spec.getSpecType() + " " + spec.getName();
						}

						/**
						 * @see IParsedEntity#resolveDependencies(Set)
						 */
						public void resolveDependencies(Set<Long> currentlyBeingResolved) {
							if (resolved) {
								return;
							}
							if (currentlyBeingResolved.contains(spec.getId())) {
								throw new IllegalStateException(
										"Detected circular reference while resolving " + spec.getName());
							}
							currentlyBeingResolved.add(spec.getId());
							Predicates.ITransformer transformer = new ReferenceResolvingTransformer(
									currentlyBeingResolved);
							IPredicate pred = spec.getPredicate();
							if (pred != null) {
								IPredicate transformed = Predicates.transform(pred, transformer);
								spec.setPredicate(transformed != null ? transformed : PredicateConstants.TRUE);
							}
							currentlyBeingResolved.remove(spec.getId());
							resolved = true;
						}
					};
					daComponentMap.put(spec.getId(), parsed);
				}
			});

		}
	}

	private void loadDelegationRules(List<String> policyPQLs) throws PQLException {
		for (final String pql : policyPQLs) {
			DomainObjectBuilder.processInternalPQL(pql, new DefaultPQLVisitor() {

				@Override
				public void visitPolicy(DomainObjectDescriptor descriptor, final IDPolicy policy) {
					policy.setAccessPolicy(null);
					IParsedEntity parsed = new IParsedEntity() {
						/*
						 * This flag indicates that the corresponding entity has
						 * been resolved.
						 */
						private boolean resolved = false;

						public IHasId getParsed() {
							return policy;
						}

						public String getName() {
							return "POLICY " + policy.getName();
						}

						public void resolveDependencies(Set<Long> currentlyBeingResolved) {
							if (resolved) {
								return;
							}
							if (!currentlyBeingResolved.isEmpty()) {
								throw new IllegalStateException(
										"Policies cannot be resolved as dependents of other entities.");
							}
							Predicates.ITransformer transformer = new ReferenceResolvingTransformer(
									currentlyBeingResolved);
							IPredicate pred;
							pred = policy.getTarget().getActionPred();
							if (pred != null) {
								IPredicate transformed = Predicates.transform(pred, transformer);
								policy.getTarget()
										.setActionPred(transformed != null ? transformed : PredicateConstants.TRUE);
							}
							pred = policy.getTarget().getFromResourcePred();
							if (pred != null) {
								IPredicate transformed = Predicates.transform(pred, transformer);
								policy.getTarget().setFromResourcePred(
										transformed != null ? transformed : PredicateConstants.TRUE);
							}
							pred = policy.getTarget().getToResourcePred();
							if (pred != null) {
								IPredicate transformed = Predicates.transform(pred, transformer);
								policy.getTarget()
										.setToResourcePred(transformed != null ? transformed : PredicateConstants.TRUE);
							}
							pred = policy.getTarget().getSubjectPred();
							if (pred != null) {
								IPredicate transformed = Predicates.transform(pred, transformer);
								policy.getTarget()
										.setSubjectPred(transformed != null ? transformed : PredicateConstants.TRUE);
							}
							pred = policy.getTarget().getToSubjectPred();
							if (pred != null) {
								IPredicate transformed = Predicates.transform(pred, transformer);
								policy.getTarget()
										.setToSubjectPred(transformed != null ? transformed : PredicateConstants.TRUE);
							}
							pred = policy.getDeploymentTarget();
							if (pred != null) {
								IPredicate transformed = Predicates.transform(pred, transformer);
								policy.setDeploymentTarget(transformed != null ? transformed : PredicateConstants.TRUE);
							}
							resolved = true;
						}
					};
					daRuleMap.put(policy.getId(), parsed);
				};
			});
		}
	}

	public List<IDPolicy> resolve() throws ReferenceResolutionException {
		policyIds = new long[daRuleMap.size()];
		parsedPolicies = new IDPolicy[daRuleMap.size()];
		int i = 0;

		for (Map.Entry<Long, IParsedEntity> entry : daRuleMap.entrySet()) {
			if (entry.getKey() == null) {
				throw new IllegalStateException("Policy with null ID is detected.");
			}

			// Policy IDs are expected to be in sorted order because they come
			// from a sorted map.
			policyIds[i] = entry.getKey().longValue();
			assert policyIds[i] == 0 || policyIds[i] > policyIds[i - 1];

			IParsedEntity parsed = entry.getValue();
			try {
				parsed.resolveDependencies(new HashSet<Long>());
				parsedPolicies[i++] = (IDPolicy) parsed.getParsed();
			} catch (ReferenceResolutionException e) {
				throw e;
			}
		}
		return Arrays.asList(parsedPolicies);
	}

	/**
	 * This transformer passes through all non-reference predicates, and
	 * replaces all predicate references with the referenced targets.
	 */
	class ReferenceResolvingTransformer extends Predicates.DefaultTransformer {
		/**
		 * This <code>Set</code> contains IDs of entities currently being
		 * resolved.
		 */
		private final Set<Long> currentlyBeingResolved;

		/**
		 * The constructor supplies the set of entities being resolved.
		 * 
		 * @param currentlyBeingResolved
		 *            the <code>Set</code> of IDs of entities that are currently
		 *            being resolved.
		 */
		public ReferenceResolvingTransformer(Set<Long> currentlyBeingResolved) {
			this.currentlyBeingResolved = currentlyBeingResolved;
		}

		/**
		 * @see ITransformer#transformReference(IPredicateReference)
		 */
		public IPredicate transformReference(IPredicateReference pred) {
			if (pred instanceof IDSpecRef) {
				IParsedEntity referenced = resolve((IDSpecRef) pred);
				referenced.resolveDependencies(currentlyBeingResolved);
				IHasId spec = referenced.getParsed();
				assert spec != null; // spec comes from the parser; parser does
										// not return nulls.
				if (!(spec instanceof IDSpec)) {
					throw new IllegalStateException("Unexpected reference: " + spec.getClass());
				}
				return ((IDSpec) spec).getPredicate();
			} else {
				return super.transformReference(pred);
			}
		}

		/**
		 * @see DefaultTransformer#transformRelation(IRelation)
		 */
		public IPredicate transformRelation(IRelation rel) {
			IExpression refGroup = null;
			// IExpression ref;
			if (refGroup instanceof IDSpecRef) {
				IParsedEntity referenced = resolve((IDSpecRef) refGroup);
				referenced.resolveDependencies(currentlyBeingResolved);
				new PredicateReferenceExpression(((IDSpec) referenced.getParsed()).getPredicate());
			}
			return rel;
		}

		/**
		 * Resolves spec references; throws exceptions if the reference cannot
		 * be resolved.
		 * 
		 * @param ref
		 *            the reference to resolve.
		 * @return the corresponding <code>IParsedEntity</code>.
		 */
		private IParsedEntity resolve(IDSpecRef ref) {
			if (ref.isReferenceByName()) {
				throw new IllegalStateException("Reference by name " + ref.getPrintableReference() + " is unexpected.");
			}
			IParsedEntity referenced = daComponentMap.get(ref.getReferencedID());
			if (referenced == null) {
				throw new ReferenceResolutionException(ref.getReferencedID());
			}
			return referenced;
		}
	}

	/**
	 * We use anonymous instances of this interface to store information about
	 * policies and specs.
	 */
	public static interface IParsedEntity {

		/**
		 * The parsed entity - a policy or a spec.
		 * 
		 * @return the corresponding parsed entity, which is a policy or a spec.
		 */
		IHasId getParsed();

		/**
		 * The corresponding unparsed entity - a <code>DeploymentEntity</code>
		 * object.
		 * 
		 * @return the corresponding <code>DeploymentEntity</code> object.
		 */
		// IHasPQL getUnparsed();

		/**
		 * The name of the parsed (and unparsed) entity.
		 * 
		 * @return name of the parsed (and unparsed) entity.
		 */
		String getName();

		/**
		 * Resolve the dependencies for this object. Specs simply resolve their
		 * dependencies by ID, while policies resolve dependencies of each of
		 * their predicates.
		 * 
		 * @param currentlyBeingResolved
		 *            a <code>Set</code> containing IDs (of type
		 *            <code>Long</code>) of the objects that are currently being
		 *            resolved. This <code>Set</code> is used for detecting
		 *            circular references.
		 * @throws ReferenceResolutionException
		 *             when a reference cannot be resolved.
		 */
		void resolveDependencies(Set<Long> currentlyBeingResolved);
	}

	/**
	 * Instances of this expression class hold references to predicates.
	 */
	public static class PredicateReferenceExpression implements IExpression {
		/** The referenced predicate. */
		private final IPredicate predicate;

		/**
		 * Builds a reference expression for the specific predicate.
		 * 
		 * @param predicate
		 *            the predicate to which this reference is pointing.
		 */
		public PredicateReferenceExpression(IPredicate predicate) {
			if (predicate == null) {
				throw new NullPointerException("predicate");
			}
			this.predicate = predicate;
		}

		/**
		 * @see IExpression#acceptVisitor(IExpressionVisitor,
		 *      IExpressionVisitor.Order)
		 */
		public void acceptVisitor(IExpressionVisitor visitor, IExpressionVisitor.Order order) {
			visitor.visit(this);
		}

		/**
		 * @see IExpression#buildRelation(RelationOp, IExpression)
		 */
		public IRelation buildRelation(RelationOp op, IExpression rhs) {
			throw new UnsupportedOperationException("IExpression#buildRelation(RelationOp, IExpression)");
		}

		/**
		 * @see IExpression#evaluate(IArguments)
		 */
		public IEvalValue evaluate(IArguments arg) {
			throw new UnsupportedOperationException("IExpression#evaluate(IArguments)");
		}

		/**
		 * Returns the referenced predicate.
		 * 
		 * @return the referenced predicate.
		 */
		public IPredicate getReferencedPredicate() {
			return predicate;
		}
	}

	/**
	 * This exception is thrown when a reference by ID cannot be resolved.
	 */
	public static class ReferenceResolutionException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		/**
		 * This field represents a referenced ID which is unresolved.
		 */
		private final Long unresolvedId;

		/**
		 * Creates a new <code>ReferenceResolutionException</code>.
		 * 
		 * @param unresolvedId
		 *            the unresolved ID.
		 */
		public ReferenceResolutionException(Long unresolvedId) {
			super("Unresolved ID: " + unresolvedId);
			this.unresolvedId = unresolvedId;
		}

		/**
		 * Gets the unresolved ID.
		 * 
		 * @return the unresolved ID.
		 */
		public Long getUnresolvedId() {
			return unresolvedId;
		}

		/**
		 * Makes this object printable for debugging.
		 */
		public String toString() {
			return getMessage();
		}

	}

}
