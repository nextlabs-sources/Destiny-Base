package com.bluejungle.framework.expressions;

import java.util.Set;

import com.bluejungle.framework.patterns.EnumBase;

//All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
//Redwood City CA,
//Ownership remains with Blue Jungle Inc, All rights reserved worldwide.

/**
 * Operators used in the Epicenter/Destiny policy framework.
 * 
 * @author pkeni
 */

public class RelationOp extends EnumBase {
    private static final long serialVersionUID = 1L;

    public static final int verboseNames = 1;

    public static final RelationOp EQUALS = new RelationOp("equals", "=") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp NOT_EQUALS = new RelationOp("not_equals", "!=") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp GREATER_THAN = new RelationOp("greater_than", ">") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp LESS_THAN = new RelationOp("less_than", "<") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp GREATER_THAN_EQUALS = new RelationOp("greater_than_equals", ">=") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp LESS_THAN_EQUALS = new RelationOp("less_than_equals", "<=") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp HAS = new RelationOp("has", "has") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp DOES_NOT_HAVE = new RelationOp("does_not_have", "does_not_have") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp INCLUDES = new RelationOp("includes", "includes") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp EQUALS_UNORDERED = new RelationOp("equals_unordered", "equals_unordered") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_EQUALS = new RelationOp("string_equals", "eq") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_NOT_EQUALS = new RelationOp("string_not_equals", "neq") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_LT = new RelationOp("string_less_than", "lt") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_GT = new RelationOp("string_greater_than", "gt") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_LTE = new RelationOp("string_less_than_equal_to", "lte") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_GTE = new RelationOp("string_greater_than_equal_to", "gte") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_EQUALS_CASE_INSENSITIVE = new RelationOp("string_equals_case_insensitive", "eq_ci") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_NOT_EQUALS_CASE_INSENSITIVE = new RelationOp("string_not_equals", "neq_ci") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_LT_CASE_INSENSITIVE = new RelationOp("string_less_than_case_insensitive", "lt_ci") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_GT_CASE_INSENSITIVE = new RelationOp("string_greater_than_case_insensitive", "gt_ci") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_LTE_CASE_INSENSITIVE = new RelationOp("string_less_than_equal_to_case_insensitive", "lte_ci") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp STRING_GTE_CASE_INSENSITIVE = new RelationOp("string_greater_than_equal_to_case_insensitive", "gte_ci") {
        private static final long serialVersionUID = 1L;
    };
    public static final RelationOp WITHIN_LAST = new RelationOp("within_last", "within_last") {
        private static final long serialVersionUID = 1L;
    };
    
    private final String altName; // non-verbose name

    private RelationOp(String altName, String name) {
        super(name, RelationOp.class);
        this.altName = altName;
    }

    public String toString() {
        return (RelationOp.verboseNames == 0) ? this.altName : this.enumName;
    }

    public static RelationOp getElement(String name) {
        return getElement(name, RelationOp.class);
    }

    public static Set<RelationOp> elements() {
        return elements(RelationOp.class);
    }

    public static int numElements() {
        return numElements( RelationOp.class );
    }
}
