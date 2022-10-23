/*
 * Created on Apr 18, 2014
 *
 * All sources, binaries and HTML pages (C) copyright 2014 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.bluejungle.pf.engine.destiny;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.domain.log.FromResourceInformation;
import com.bluejungle.domain.log.ToResourceInformation;
import com.bluejungle.domain.policydecision.PolicyDecisionEnumType;
import com.bluejungle.framework.expressions.EvalValue;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.IMultivalue;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.Multivalue;
import com.bluejungle.framework.expressions.ValueType;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.bluejungle.framework.utils.MultipartKey;
import com.bluejungle.pf.domain.destiny.common.SpecAttribute;
import com.bluejungle.pf.domain.destiny.exceptions.CombiningAlgorithm;
import com.bluejungle.pf.domain.destiny.misc.EffectType;
import com.bluejungle.pf.domain.destiny.misc.IDEffectType;
import com.bluejungle.pf.domain.destiny.misc.IDTarget;
import com.bluejungle.pf.domain.destiny.obligation.CustomObligation;
import com.bluejungle.pf.domain.destiny.obligation.DObligation;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.destiny.policy.Policy;
import com.bluejungle.pf.domain.destiny.resource.ResourceAttribute;
import com.bluejungle.pf.domain.destiny.subject.IDSubject;
import com.bluejungle.pf.domain.destiny.subject.SubjectAttribute;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;
import com.bluejungle.pf.domain.epicenter.exceptions.ICombiningAlgorithm;
import com.bluejungle.pf.domain.epicenter.exceptions.IPolicyReference;
import com.bluejungle.pf.domain.epicenter.misc.IObligation;
import com.bluejungle.pf.domain.epicenter.resource.IMResource;
import com.bluejungle.pf.domain.epicenter.resource.IResource;
import com.nextlabs.domain.log.PolicyActivityInfoV5;

class EvaluationContext {
    private static final Log log = LogFactory.getLog(EvaluationContext.class.getName());

    private final MultipartKey subjectKey;
    private final Map<String, Integer> exceptionsNameIndexMap;
    private final StringBuilder sb;

    // These track the complete set of allow/deny results. Used at the end by obligations
    private final BitSet allows;
    private final BitSet denies;

    private final IDPolicy policies[];

    // Ad-Hoc policies are passed in as part of the query. They are
    // evaluated normally, but we don't want to cache their evaluation
    // results as we would for bundle policies
    private final int indexFirstAdHocPolicy;
    
    private final SubjectCache subjectCache;

    private final MatchedResources[] matchedResources;

    public EvaluationContext(MultipartKey subjectKey, Map<String, Integer> exceptionsNameIndexMap, IDPolicy[] policies, int indexFirstAdHocPolicy, SubjectCache subjectCache) {
        this.subjectKey = subjectKey;
        this.exceptionsNameIndexMap = exceptionsNameIndexMap;
        this.policies = policies;
        this.subjectCache = subjectCache;
        this.indexFirstAdHocPolicy = indexFirstAdHocPolicy;
        
        sb = new StringBuilder();
        matchedResources = new MatchedResources[policies.length];

        allows = new BitSet(policies.length);
        denies = new BitSet(policies.length);

    }

    public EvaluationResult evaluate(EvaluationRequest request, BitSet evaluationCandidates) throws PolicyEvaluationException {
        EvaluationResult  res = new EvaluationResult(request);
        
        EvaluationSubcontext subcontext = new EvaluationSubcontext(res, sb);

        IDEffectType effect = subcontext.evaluate(request, evaluationCandidates, CombiningAlgorithm.DENY_OVERRIDES);

        res.setEffectType(effect);
        
        if (request.getExecuteObligations()) {
            generateObligations(request, res);
        }
        
        return res;
    }


    public String getLogString() {
        return sb.toString();
    }

    public void appendToLogString(String s) {
        sb.append(s);
    }

    public void generateObligations(EvaluationRequest request, EvaluationResult res) {
        MatchedResources resMatch = null;
        for (int i = 0; i < policies.length; i++) {
            if ((resMatch = matchedResources[i]) != null) {
                break;
            }
        }
            
        if (res.getEffectType() == EffectType.DENY) {
            allows.or(denies);
            generateObligations(res, EffectType.DENY, allows, denies, request, resMatch);
        } else if (res.getEffectType() == EffectType.ALLOW) {
            denies.or(allows);
            generateObligations(res, EffectType.ALLOW, denies, allows, request, resMatch);
        }
    }

    private void generateObligations(EvaluationResult evalResult, IDEffectType effectType, BitSet applicables, BitSet supporting, EvaluationRequest request, MatchedResources matched) {
        final IResource fromResource = request.getFromResource();
        if (fromResource == null) {
            return;
        }

        IEvalValue fromResourcesVal = fromResource.getAttribute(SpecAttribute.NAME_ATTR_NAME);

        if (fromResourcesVal.getType() == ValueType.NULL) {
            fromResourcesVal = fromResource.getAttribute(SpecAttribute.ID_ATTR_NAME);
        }

        IMultivalue fromResources = IMultivalue.EMPTY;
        if (fromResourcesVal != null) {
            if (fromResourcesVal.getType() == ValueType.MULTIVAL) {
                fromResources = (IMultivalue)fromResourcesVal.getValue();
            } else {
                fromResources = Multivalue.create(new Object[] {fromResourcesVal.getValue()});
            }
        }

        final IResource toResource = request.getToResource();
        IMultivalue toResources = IMultivalue.EMPTY;
        if (toResource != null) {
            IEvalValue toResourcesVal = toResource.getAttribute(SpecAttribute.NAME_ATTR_NAME);
            if (toResourcesVal.getType() == ValueType.NULL) {
                toResourcesVal = toResource.getAttribute(SpecAttribute.ID_ATTR_NAME);
            }
            if (toResourcesVal != null) {
                if (toResourcesVal.getType() == ValueType.MULTIVAL) {
                    toResources = (IMultivalue)toResourcesVal.getValue();
                } else {
                    toResources = Multivalue.create(new Object[] {toResourcesVal.getValue()});
                }
            }
        }

        final IDSubject host = request.getHost();
        final IDSubject app = request.getApplication();

        final String actionName = request.getAction().getName();
        final String effectName =  effectType.getName();

        final IEvalValue fromSizeValue = convertToLong(fromResource.getAttribute("size"));
        IEvalValue fromOwnerSid = fromResource.getAttribute("owner");
        if (fromOwnerSid == null) {
            fromOwnerSid = fromResource.getAttribute("created_by");
        }

        final IEvalValue fromCreatedValue = convertToDate(fromResource.getAttribute("created_date"));
        final IEvalValue fromModifiedValue = convertToDate(fromResource.getAttribute("modified_date"));

        long fromSize = 0;
        long fromCreated = 0;
        long fromModified = 0;
        String fromOwner = "";

        if (fromSizeValue != null && fromSizeValue != IEvalValue.NULL) {
            fromSize = ((Long) fromSizeValue.getValue()).longValue();
        }
        if (fromCreatedValue != null && fromCreatedValue != IEvalValue.NULL) {
            fromCreated = ((Long) fromCreatedValue.getValue()).longValue();
        }
        if (fromModifiedValue != null && fromModifiedValue != IEvalValue.NULL) {
            fromModified = ((Long) fromModifiedValue.getValue()).longValue();
        }
        if (fromOwnerSid != null && fromOwnerSid != IEvalValue.NULL) {
            fromOwner = (String)fromOwnerSid.getValue();
        }

        if (matched.fromResourceIdx >= fromResources.size()) {
            matched.fromResourceIdx = 0;
        }

        String resName = (String)fromResources.get(matched.fromResourceIdx);
        final FromResourceInformation fromInfo = new FromResourceInformation(resName, fromSize, fromCreated, fromModified, fromOwner);

        ToResourceInformation toInfo =null;
        if (toResource != null) {
            if (matched.toResourceIdx >= toResources.size()) {
                matched.toResourceIdx = 0;
            }
            resName = (String)toResources.get(matched.toResourceIdx);
            toInfo = new ToResourceInformation(resName);
        }

        Map<String, DynamicAttributes> attributesMap = new HashMap<String, DynamicAttributes>();
        DynamicAttributes fromAttributes = new DynamicAttributes();
        for (Map.Entry<String, IEvalValue> attr : fromResource.getEntrySet()) {
            fromAttributes.put(attr.getKey(), attr.getValue());
        }
        attributesMap.put(PolicyActivityInfoV5.FROM_RESOURCE_ATTRIBUTES_TAG, fromAttributes);
        
        final IDSubject user = request.getUser();
        DynamicAttributes userAttributes = new DynamicAttributes();
        for (Map.Entry<String, IEvalValue> attr : user.getEntrySet()) {
        	userAttributes.put(attr.getKey(), attr.getValue());
        }
        attributesMap.put(PolicyActivityInfoV5.USER_ATTRIBUTES_TAG, userAttributes);

        
        final IDSubject[] recipients = request.getSentTo();
        if (recipients != null && recipients.length > 0) {
            DynamicAttributes recipientAttributes = new DynamicAttributes();

            // If we have multiple recipients then they must have been sent using
            // the old approach, where recipients have just email addresses and
            // nothing else. Collect them into one entry. If we just have one then
            // it's either the old approach (and there was just one recipient) or
            // the new (one recipient + attributes) and either way we send the same thing.

            if (recipients.length == 1) {
                for (Map.Entry<String, IEvalValue> attr : recipients[0].getEntrySet()) {
                    recipientAttributes.put(attr.getKey(), attr.getValue());
                }
            } else {
                ArrayList<String> emailAddresses = new ArrayList<String>();
                
                for (IDSubject recipient : recipients) {
                    emailAddresses.add(recipient.getUid());
                }

                recipientAttributes.put("email", emailAddresses);
            }
           
            attributesMap.put(PolicyActivityInfoV5.RECIPIENTS_ATTRIBUTES_TAG, recipientAttributes);
        }

        
        final PolicyActivityInfoV5 args = new PolicyActivityInfoV5(fromInfo,
                                                                   toInfo,
                                                                   request.getUserName(),
                                                                   request.getUser().getId().longValue(),
                                                                   host.getName(),
                                                                   request.getHostIPAddress(),
                                                                   host.getId().longValue(),
                                                                   app.getName(),
                                                                   app.getId().longValue(),
                                                                   actionName,
                                                                   PolicyDecisionEnumType.getPolicyDecisionEnum(effectName),
                                                                   request.getRequestId().longValue(),
                                                                   request.getTimestamp(),
                                                                   request.getLevel(),
                                                                   attributesMap,
                                                                   null
                                                                   );
            
        evalResult.setPAInfo(args);

        for (int i = applicables.nextSetBit(0); i >= 0; i = applicables.nextSetBit(i + 1)) {
            IDPolicy policy = policies[i];

            // Deal with obligations applicable to the given effect
            // only when the individual effect of the given policy
            // matches the overall outcome of the evaluation
            if ( supporting.get(i) ) {
                for (IObligation o : policy.getObligations(effectType)) {
                    DObligation obligation = (DObligation)o;
                    if (obligation.isActivityAcceptable(evalResult, args)) {
                        parseObligationForArguments(request, obligation);
                        evalResult.addObligation(obligation);
                    }
                }
            }
                
            // Do these even exist??
            for (IObligation o : policy.getObligations(EffectType.DONT_CARE)) {
                DObligation obligation = (DObligation)o;
                if (obligation.isActivityAcceptable(evalResult, args)) {
                    parseObligationForArguments(request, obligation);
                    evalResult.addObligation(obligation);
                }
            }
        }
    }

    /*
     * Parse out the $a.b.c (e.g. user.department) arguments from the
     * obligation arguments. ObligationHandler expects that all these
     * values have been set at policy evaluation time, but that's not
     * guaranteed. Here we get them all and look them up, just to be
     * sure
     *
     * The code is slightly different, however, because we don't
     * bother with the old $CE arguments as they don't involve subject
     * or resource attributes
     *
     * See com.nextlabs.destiny.agent.controlmanager.ObligationHandler
     * for where this is used
     */

    private static final String argumentRegex = "(\\$((application|environment|from|resource|to|user)(\\.[A-Za-z0-9:_-]+){1,2}))";
    private static final Pattern argumentPattern = Pattern.compile(argumentRegex);
    
    private void parseObligationForArguments(EvaluationRequest req, DObligation obligation) {
        if (!(obligation instanceof CustomObligation)) {
            return;
        }

        CustomObligation customObligation = (CustomObligation)obligation;

        for (Object arg : customObligation.getCustomObligationArgs()) {
            Matcher m = argumentPattern.matcher((String)arg);

            while (m.find()) {
                String attribute = m.group(0);

                String[] attributeParts = attribute.split("\\.");

                
                // Attributes are either a.b.c or a.b, although the
                // former is only for resource attributes
                if (attributeParts.length < 2 || attributeParts.length > 3 ||
                    (attributeParts.length == 3 && !(attributeParts[0].equals("$from") ||
                                                     attributeParts[0].equals("$to") ||
                                                     attributeParts[0].equals("$resource")))) {
                    continue;
                }

                String dimension = attributeParts[0];
                String attributeName = attributeParts[attributeParts.length-1];
                
                if (dimension.equals("$user")) {
                    SubjectAttribute userAttr = SubjectAttribute.forNameAndType(attributeName, SubjectType.USER);
                    // We don't care about the return value. The evaluation call will update the request
                    userAttr.evaluate(req);
                } else if (dimension.equals("$resource") || dimension.equals("$from")) {
                    String destinyType = (String)req.getFromResource().getAttribute(SpecAttribute.DESTINYTYPE_ATTR_NAME).getValue();
                    ResourceAttribute resAttr = ResourceAttribute.forNameAndType(attributeName, destinyType);
                    resAttr.evaluate(req);
                } else if (dimension.equals("$application")) {
                    // Probably doesn't exist, but we'll give it a shot
                    SubjectAttribute applicationAttr = SubjectAttribute.forNameAndType(attributeName, SubjectType.APP);
                    applicationAttr.evaluate(req);
                } else if (dimension.equals("$host")) {
                    // Also unlikely to exist
                    SubjectAttribute hostAttr = SubjectAttribute.forNameAndType(attributeName, SubjectType.HOST);
                    hostAttr.evaluate(req);
                }

                // Deliberately skipping $to. The "to" resource is
                // special. We evaluate it by creating a new request
                // object with a cloned "to" in the "from"
                // position. Unfortunately, that means that if we use
                // a plugin to get resource attributes, those
                // attributes are recorded in the cloned "to" object
                // rather than the original. The "to" resource is
                // hardly ever used, so this isn't much of a problem.
                //
                // The solution would probably be to copy over the
                // attributes from the cloned "to" to the
                // original. See "HACK ALERT" in evaluateSinglePolicy
            }
        }
        return;
    }
    
    public boolean getAllow(int i) {
        return allows.get(i);
    }

    private void setAllow(int i) {
        allows.set(i);
    }

    public boolean getDeny(int i) {
        return denies.get(i);
    }

    private void setDeny(int i) {
        denies.set(i);
    }

    private IEvalValue convertToLong(IEvalValue val) {
        if (val.getType() != ValueType.LONG) {
            long asInteger = 0;
            try {
                asInteger = Long.parseLong((String)val.getValue());
            } catch (NumberFormatException e) {
            }
            return EvalValue.build(new Long(asInteger));
        }
            
        return val;
    }
        
    private IEvalValue convertToDate(IEvalValue val) {
        if (val.getType() != ValueType.DATE) {
            long asDate = 0;
            try {
                asDate = Long.parseLong((String)val.getValue());
            } catch (NumberFormatException e) {
            }
            return EvalValue.build(new Date(asDate));
        }
        return val;
    }

    private MultipartKey getKey() {
        return subjectKey;
    }

    private Integer getExceptionIndex(String name) {
        return exceptionsNameIndexMap.get(name);
    }
    

    private static final class MatchedResources {
        private boolean fromMatched = false;
        private int fromResourceIdx;
        private boolean toMatched = false;
        private int toResourceIdx;
    }
    
    private class EvaluationSubcontext {
        private final EvaluationResult res;
        private final StringBuilder sb;

        public EvaluationSubcontext(EvaluationResult res, StringBuilder sb) {
            this.res = res;
            this.sb = sb;
        }

        public IDEffectType evaluate(EvaluationRequest request, BitSet evaluationCandidates, ICombiningAlgorithm combiningAlgorithm) throws PolicyEvaluationException {
            int numDenies = 0;
            int numAllows = 0;
            for (int i = evaluationCandidates.nextSetBit(0); i >=0; i = evaluationCandidates.nextSetBit(i+1)) {
                IDPolicy policy = policies[i];

                IDEffectType effect;
                
                try {
                    effect = evaluateSinglePolicy(i, request);
                } catch (RuntimeException t) {
                    // Two possibilites. Treat it as a normally evaluated policy or freak out.
                    if (request.getEvaluationFailureResponse() == EvaluationFailureResponse.THROW) {
                        if (log.isInfoEnabled()) {
                            sb.append("!: " + policy.getName() + "\n");
                        }
                        throw new PolicyEvaluationException("Error evaluating policy " + policy.getName(), t);
                    }
                    
                    // Treat it as a normally evaluated policy, but mark it with a '!' for visibility
                    sb.append("!");

                    // Determine how to handle it
                    if (request.getEvaluationFailureResponse() == EvaluationFailureResponse.DENY) {
                        effect = EffectType.DENY;
                    } else if (request.getEvaluationFailureResponse() == EvaluationFailureResponse.ALLOW) {
                        effect = EffectType.ALLOW;
                    } else if (request.getEvaluationFailureResponse() == EvaluationFailureResponse.MAIN_EFFECT) {
                        effect = (IDEffectType)policy.getMainEffect();
                    } else if (request.getEvaluationFailureResponse() == EvaluationFailureResponse.OTHERWISE_EFFECT) {
                        effect = (IDEffectType)policy.getOtherwiseEffect();
                    } else {
                        // Presumably EvaluationFailureResponse.IGNORE
                        effect = EffectType.DONT_CARE;
                    }
                }
                
                switch(effect.getType()) {
                    case IDEffectType.DENY_TYPE:
                        setDeny(i);
                        numDenies++;
                        if (log.isInfoEnabled()) {
                            sb.append("D: " + policy.getName() + "\n");
                        }
                        break;
                    case IDEffectType.ALLOW_TYPE:
                        setAllow(i);
                        numAllows++;
                        if (log.isInfoEnabled()) {
                            sb.append("A: " + policy.getName() + "\n");
                        }
                        break;
                    default:
                        if (log.isInfoEnabled()) {
                            sb.append("X: " + policy.getName() + "\n");
                        }
                        break;
                }

                if (request.getRecordMatchingPolicies()) {
                    res.addToApplicablePolicies(effect.getName(), policy.getName());
                }
            }

            return combiningAlgorithm.getEffect(numAllows, numDenies);
        }

        private IDEffectType evaluateSinglePolicy(int policyOrdinal, EvaluationRequest request) throws PolicyEvaluationException {
            final IDPolicy policy = policies[policyOrdinal];
            final IDTarget target = policy.getEvaluationTarget();
            
            Boolean applicable = null;
        
            final MatchedResources matching = new MatchedResources();
            matchedResources[policyOrdinal] = matching;

            boolean match = false;
        
            final IResource fromResource = request.getFromResource();
        
            if (fromResource != null) {
                final EngineResourceInformation fromInfo = request.getFromResourceInfo();
                
                applicable = (policyOrdinal < indexFirstAdHocPolicy) ? fromInfo.isApplicableFrom(policyOrdinal) : null;
                
                if (applicable != null) {
                    match = applicable.booleanValue();
                    if (match) {
                        matching.fromMatched = true;
                        matching.fromResourceIdx = fromInfo.getFromResourceIndex(policyOrdinal).intValue();
                    }
                } else {
                    final IPredicate fromResourcePred = target.getFromResourcePred();
                    match = fromResourcePred.match(request);
                    if (match) {
                        IMResource tmpFrom = fromResource.clone();
                        request.setFromResource(tmpFrom);
                        IEvalValue fromResourcesVal = fromResource.getAttribute("name");
                        IMultivalue fromResources = IMultivalue.EMPTY;
                        if (fromResourcesVal != null && fromResourcesVal.getType() == ValueType.MULTIVAL) {
                            fromResources = (IMultivalue)fromResourcesVal.getValue();
                        }
                        int i = 0;
                        for (final IEvalValue name : fromResources) {
                            tmpFrom.setAttribute("name", name);
                            if (fromResourcePred.match(request)) {
                                matching.fromMatched = true;
                                matching.fromResourceIdx = i;
                                break;
                            } else {
                                i++;
                            }
                        }
                        if (matching.fromMatched == false) {
                            matching.fromMatched = true;
                            matching.fromResourceIdx = 0;
                        }
                        request.setFromResource(fromResource);
                    }

                    if (policyOrdinal < indexFirstAdHocPolicy) {
                        fromInfo.setApplicableFrom(policyOrdinal, match, matching.fromResourceIdx);
                    }
                }

                if (!match) {
                    return EffectType.DONT_CARE;
                }
            }

            final IResource toResource = request.getToResource();
            if (toResource != null) {
                final EngineResourceInformation toInfo = request.getToResourceInfo();
                final IPredicate toResourcePred = target.getToResourcePred();
                if (toResourcePred != null) {
                    applicable = (policyOrdinal < indexFirstAdHocPolicy) ? toInfo.isApplicableTo(policyOrdinal) : null;
                    
                    if (applicable != null) {
                        match = applicable.booleanValue();
                        if (match) {
                            matching.toMatched = true;
                            matching.toResourceIdx = toInfo.getToResourceIndex(policyOrdinal).intValue ();
                        }
                    } else {
                        // HACK ALERT. Not sure how to improve
                        // this. We create a new EvaluationRequest
                        // object with the "to" resource in the "from"
                        // position and then evaluate the the name. We
                        // have to do this because we don't have a way
                        // in "match" to see which resource entity is
                        // being queried against (unlike user,
                        // application, and host, which have a type)
                        final IMResource tmpTo = request.getToResource().clone();
                        final EvaluationRequest toReq = EvaluationRequest.createToRequest(request, tmpTo);
                        match = toResourcePred.match(toReq);
                        if (match) {
                            final IEvalValue toResourcesVal = tmpTo.getAttribute("name");
                            IMultivalue toResources = IMultivalue.EMPTY;
                            if (toResourcesVal != null && toResourcesVal.getType() == ValueType.MULTIVAL) {
                                toResources = (IMultivalue)toResourcesVal.getValue();
                            }
                            int i = 0;
                            for (final IEvalValue name : toResources) {
                                tmpTo.setAttribute("name", name);
                                if (toResourcePred.match(toReq)) {
                                    matching.toMatched = true;
                                    matching.toResourceIdx = i;
                                    break;
                                } else {
                                    i++;
                                }
                            }
                            if (matching.toMatched == false) {
                                matching.toMatched = true;
                                matching.toResourceIdx = 0;
                            }
                        }
                    }
                } else {
                    matching.toMatched = true;
                    matching.toResourceIdx = 0;
                }

                if (policyOrdinal < indexFirstAdHocPolicy) {
                    toInfo.setApplicableTo(policyOrdinal, match, matching.toResourceIdx);
                }

                if (!match) {
                    return EffectType.DONT_CARE;
                }
            } else if (target.getToResourcePred() != null) {
                // a null resource won't match "something", only missing predicates,
                // and predicates that always evaluate to "true".
                try {
                    if (!target.getToResourcePred().match(null)) {
                        // Only predicates that match with null evaluation environment are always true;
                        // other predicates are "real," and they must not match a null resource
                        return EffectType.DONT_CARE;
                    }
                } catch (Exception all) {
                    // This should never happen assuming that all attribute classes are well-behaved.
                    // If an exception does happen, it indicates that the predicate is not always true.
                    log.error("Misbehaving attribute class in empty predicate match");
                    return EffectType.DONT_CARE;
                }
            }

            /*
             * The logic we are trying to encode is a little clunky,
             * thanks to ALLOW ONLY policies (which are deprecated,
             * but still around for backwards compatibility reasons).
             *
             * ALLOW ONLY
             *   Subject matches, condition matches - return allow (main effect)
             *   Subject matches, condition doesn't - return deny  (otherwise effect)
             *   Subject doesn't, condition matches - return deny  (otherwise effect)
             *   Subject doesn't, condition doesn't - return n/a
             *
             * ALL OTHERS
             *   Subject matches, condition matches - return main effect
             *   Subject matches, condition doesn't - return otherwise effect
             *   Subject doesn't                    - return n/a
             *
             * For allow only policies we will evaluate the conditions
             * even if the subject didn't apply. For all other
             * policies we will evaluate the conditions only if the
             * subject applied.
             *
             * Originally we had three policy "types". Allow only,
             * deny, and monitor. Monitor policies were default allow,
             * but had no "otherwise" effect, which is how we
             * distinguished them from allow only policies.  We renamed
             * "monitor" to "allow" and then removed the ability to
             * create allow only policies in Policy Studio, but we
             * still have the problem that allow (formerly monitor)
             * policies, don't have an otherwise effect, which makes
             * them not quite the opposite of deny.
             *
             * Now we have a flag that indicates that a policy with
             * default allow and an otherwise effect is really an
             * allow policy and *not* allow only. We still support the
             * old style allow and allow only (and deny, obviously).
             * As can be seen from the chart above, the evaluation for
             * everything that isn't allow only is very consistent and
             * coherent.
             *
             * Allow only will be removed in a future release.
             */

            final Boolean subjectApplicable = subjectCache.match(getKey(), policyOrdinal);
            boolean subjectMatch = true;
            if (subjectApplicable != null) {
                subjectMatch = subjectApplicable.booleanValue();
            } else {
                final IPredicate subjectPred = target.getSubjectPred();
                subjectMatch = subjectPred.match(request);
                if (getKey().isCacheable() && policyOrdinal < indexFirstAdHocPolicy) {
                    subjectCache.setMatches(getKey(), policyOrdinal, subjectMatch);
                }
            }

            if (subjectMatch) {
                subjectMatch = toSubjectMatch(target.getToSubjectPred(), request, policy.getName());
            }

            boolean isAllowOnly = isAllowOnlyPolicy(policy);

            if (!subjectMatch && !isAllowOnly) {
                return EffectType.DONT_CARE;
            }
            
            // Get all the exceptions and evaluate them. If they collectively don't apply then continue on with
            // this policy's conditions. If they do apply, return their result
            if (!policy.getPolicyExceptions().getPolicies().isEmpty()) {
                final BitSet references = new BitSet(policies.length);

                for (IPolicyReference ref : policy.getPolicyExceptions().getPolicies()) {
                    Integer index = getExceptionIndex(ref.getReferencedName());

                    // If we don't find the name then the exception policy is inapplicable
                    if (index != null) {
                        references.set(index);
                    }
                }

                EvaluationSubcontext subContext = new EvaluationSubcontext(res, sb);

                final IDEffectType exceptionResult = subContext.evaluate(request, references, policy.getPolicyExceptions().getCombiningAlgorithm());

                if (exceptionResult != EffectType.DONT_CARE) {
                    return exceptionResult;
                }
            }

            // The exceptions didn't apply, so move on to the conditions
            final IPredicate conditions = policy.getConditions();
        
            IDEffectType effect = null;

            if (subjectMatch) {
                if (conditions == null || conditions.match(request)) {
                    effect = (IDEffectType)policy.getMainEffect(); 
                } else {
                    if (isTrueAllowPolicy(policy)) {
                        // "true allow" policies should always have an
                        // otherwise deny clause, but sometimes it's
                        // omitted. This is an oversight and we should
                        // act as if it is there
                        effect = EffectType.DENY;
                    } else {
                        effect = (IDEffectType)policy.getOtherwiseEffect();
                    }
                }
            } else if (isAllowOnly) {
                // If the subject doesn't match then the only time we care about
                // the conditions is if it's an allow only policy
                if (conditions == null || conditions.match(request)) {
                    effect = (IDEffectType)policy.getOtherwiseEffect();
                }
            }

            if (effect == null) {
                effect = EffectType.DONT_CARE;
            }

            return effect;
        }


        private boolean toSubjectMatch(final IPredicate toSubjPred, EvaluationRequest request, String policyName) {
            boolean hasToSubjMatch = true;

            if (toSubjPred != null) {
                hasToSubjMatch = false;
                // Go through the to-subject and see if there is a match
                IDSubject[] sentTo = request.getSentTo();
                if (sentTo == null) {
                    hasToSubjMatch = false;
                } else {
                    try {
                        for (int i = 0; i != sentTo.length; i++) {
                            request.setSentToPosition(i);
                            if (toSubjPred.match(request)) {
                                hasToSubjMatch = true;
                                request.addSentToMatchForCurrentPosition(policyName);
                            }
                        }
                    } finally {
                        request.resetSentToPosition();
                    }
                }
            }

            return hasToSubjMatch;
        }

        /**
         * Determine if a policy is a "true allow" policy
         */
        private boolean isTrueAllowPolicy(IDPolicy policy) {
            return policy.hasAttribute(IDPolicy.TRUE_ALLOW_ATTRIBUTE);
        }
        
        /**
         * A policy is an allow only policy if its primary effect is
         * allow, the otherwise effect is deny, and it's not marked as
         * a "true allow" policy.
         */
        private boolean isAllowOnlyPolicy(IDPolicy policy) {
            return IDEffectType.ALLOW_NAME.equals(policy.getMainEffect().getName()) &&
                policy.getOtherwiseEffect() != null &&
                IDEffectType.DENY_NAME.equals(policy.getOtherwiseEffect().getName()) &&
                !isTrueAllowPolicy(policy);
        }
        
    }
}
