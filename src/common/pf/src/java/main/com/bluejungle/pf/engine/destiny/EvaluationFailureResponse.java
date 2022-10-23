package com.bluejungle.pf.engine.destiny;

/*
 * Created on Apr 07, 2017
 *
 * All sources, binaries and HTML pages (C) copyright 2017 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

import com.bluejungle.framework.patterns.EnumBase;

/**
 * This descibes the various response to evaluation failure of a
 * single policy (which is almost always an exception thrown during
 * policy evaluation)
 *
 * THROW - throw/rethrow the exception
 * IGNORE - ignore this policy (treat it as not applicable). Continue evaluating other policies
 * DENY - treat this policy as having denied
 * ALLOW - treat this policy as having allowed
 * MAIN_EFFECT - treat this policy as whatever its main effect is (DENY for DENY policies, ALLOW for ALLOW)
 * OTHERWISE_EFFECT - treat this policy as whatever its otherwise effect is
 */
public class EvaluationFailureResponse extends EnumBase {
    public static final EvaluationFailureResponse THROW = new EvaluationFailureResponse("THROW");
    public static final EvaluationFailureResponse IGNORE = new EvaluationFailureResponse("IGNORE");
    public static final EvaluationFailureResponse DENY = new EvaluationFailureResponse("DENY");
    public static final EvaluationFailureResponse ALLOW = new EvaluationFailureResponse("ALLOW");
    public static final EvaluationFailureResponse MAIN_EFFECT = new EvaluationFailureResponse("MAIN_EFFECT");
    public static final EvaluationFailureResponse OTHERWISE_EFFECT = new EvaluationFailureResponse("OTHERWISE_EFFECT");

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Prevent additional instances of this type
     */ 
    private EvaluationFailureResponse(String name) {
        super(name);
    }

    /**
     * Retrieve an EvaluationFailureResponse by name
     *
     * @param name the name of the EvaluationFailureResponse
     * @return the EvaluationFailureResponse associated with the name
     * @throws IllegalArgumentException if no such response exists
     */

    public static EvaluationFailureResponse getEvaluationFailureResponseEnum(String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        return getElement(name, EvaluationFailureResponse.class);
    }
}
