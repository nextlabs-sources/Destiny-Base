package com.bluejungle.framework.expressions;

/*
 * Created on Feb 16, 2005
 *
 * All sources, binaries and HTML pages (C) copyright 2019 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author sasha, sergey
 * @author amorgan
 */

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import com.bluejungle.framework.patterns.EnumBase;
import com.bluejungle.framework.utils.StringUtils;
import com.nextlabs.framework.expressions.PartialDateTime;

public class Relation implements IRelation {
    private IExpression lhs;
    private IExpression rhs;
    private RelationOp op;

    private static RelFunc BAD_TYPE = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            throw new RuntimeException("Incompatible types for operation.  LHS type: " + lhs.getType() + " value "+lhs.getValue()+ " RHS type: " + rhs.getType() +" value: " + rhs.getValue());
        }
    };

    private static RelFunc TRUE_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            return true;
        }
    };

    private static RelFunc FALSE_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            return false;
        }
    };

    private static RelFunc EQUALS_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            return lhs.getValue().equals(rhs.getValue());
        }
    };

    private static RelFunc NOT_EQUALS_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            return !(lhs.getValue().equals(rhs.getValue()));
        }
    };

    // This code will do String matching if both values are strings and call .equals()
    // otherwise
    private static IMultivalue.IMultivalueEqual GENERIC_MATCHER = new IMultivalue.IMultivalueEqual() {
        public boolean equal(IEvalValue x, IEvalValue y) {
            if (x == null || y == null) {
                return x == y;
            }
            
            if (x.getType() == ValueType.STRING && y.getType() == ValueType.STRING) {
                return StringUtils.isMatch((String)x.getValue(), (String)y.getValue());
            } else {
                return y.equals(x);
            }
        }
    };

    private static RelFunc MULTIVAL_INCLUSION = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            IMultivalue mval = (IMultivalue)lhs.getValue();
            return mval.includes( rhs, GENERIC_MATCHER );
        }
    };
    
    private static RelFunc VAL_INCLUSION = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            IMultivalue mval = Multivalue.create(lhs.getValue());
            return mval.includes( (IMultivalue)rhs.getValue(), GENERIC_MATCHER );
        }
    };
    
    private static RelFunc MULTIVAL_HAS_L = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            IMultivalue mval = (IMultivalue)lhs.getValue();
            return mval.includes( rhs );
        }
    };

    private static RelFunc MULTIVAL_HAS_R = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            IMultivalue mval = (IMultivalue) rhs.getValue();
            return mval.includes(lhs );
        }
    };

    private static RelFunc MULTIVAL_HAS_NOT_L = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            IMultivalue mval = (IMultivalue)lhs.getValue();
            return !mval.includes( rhs );
        }
    };

    private static RelFunc MULTIVAL_HAS_NOT_R = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            IMultivalue mval = (IMultivalue) rhs.getValue();
            return !mval.includes(lhs);
        }
    };

    private static RelFunc MULTIVAL_INCLUDES_MULTIVAL_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            IMultivalue mval = (IMultivalue) lhs.getValue();
            IMultivalue r_mval = (IMultivalue) rhs.getValue();
            return mval.includes(r_mval, GENERIC_MATCHER);
        }
    };

    private static RelFunc MULTIVAL_EQUIV_MULTIVAL = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            IMultivalue l_mval = (IMultivalue)lhs.getValue();
            IMultivalue r_mval = (IMultivalue)rhs.getValue();
            return l_mval.includes(r_mval, GENERIC_MATCHER) && r_mval.includes(l_mval, GENERIC_MATCHER);
        }
    };

    private static RelFunc MULTIVAL_EQUIV_SINGLEVAL_L = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            IMultivalue mval = (IMultivalue)lhs.getValue();
            
            return mval.size() == 1 && mval.includes(rhs, GENERIC_MATCHER);
        }
    };

    private static RelFunc MULTIVAL_EQUIV_SINGLEVAL_R = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            IMultivalue mval = (IMultivalue)rhs.getValue();
            
            return mval.size() == 1 && mval.includes(lhs, GENERIC_MATCHER);
        }
    };

    private static RelFunc STRING_WITHIN_LAST_COMPARISON = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            String left = (String)lhs.getValue();
            
            String right = (String)rhs.getValue();

            // The left could be an integer (representing a timestamp) or a human readable
            // MM/DD/YYYY sort of thing.

            try {
                Long ts = Long.parseLong(left);
                return LONG_WITHIN_LAST_COMPARISON.evaluate(EvalValue.build(ts), rhs);
            } catch (NumberFormatException nfe) {
            }

            try {
                // Try as a UTC timestamp
                ZonedDateTime leftZonedTime = ZonedDateTime.parse(left);

                return leftZonedTime.isAfter(TimeIntervalWithUnit.subtractFromTime(ZonedDateTime.now(), right));
            } catch (DateTimeParseException dtpe) {
            }

            return false;
        }
    };

    private static RelFunc LONG_WITHIN_LAST_COMPARISON = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            Long left = (Long)lhs.getValue();
            String right = (String)rhs.getValue();

            ZonedDateTime leftZonedTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(left), ZoneId.systemDefault());

            return leftZonedTime.isAfter(TimeIntervalWithUnit.subtractFromTime(ZonedDateTime.now(), right));
        }
    };

    private static RelFunc ZDT_WITHIN_LAST_COMPARISON = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            return ((ZonedDateTime)lhs.getValue()).isAfter(TimeIntervalWithUnit.subtractFromTime(ZonedDateTime.now(), (String)rhs.getValue()));
        }
    };

    private static boolean stringMatchesMultival(String pattern, IMultivalue mval) {
        return stringMatchesMultival(pattern, mval, true);
    }
    
    /**
     * String to multi-value match. There is a match if the string
     * matches any of the values in the multi-value.  One complication
     * is the one of the string/multi-value should be assumed to be a
     * wildcard pattern match, but it could be either the string (
     * "a*" = ["abc", "def", "ghi" ]) or the multi-value ("abc" =
     * ["a*", "b*", "c*"])
     *
     * PQL doesn't have a way to specify this, so when writing
     * expressions x = y, we assume that the thing on the right
     * contains (or could contain) wildcards. The caller needs to sort
     * this out
     */
    private static boolean stringMatchesMultival(String str, IMultivalue mval, boolean stringOnRHS) {
        if (str == null) {
            return mval.includes(IEvalValue.NULL);
        }
        if (mval.getType() != ValueType.STRING) {
            return false;
        }
        for (IEvalValue val : mval ) {
            String mvString = (String)val.getValue();
            if (stringOnRHS) {
                if (mvString != null && StringUtils.isMatch(str, mvString)) {
                    return true;
                }
            } else {
                if (str != null && StringUtils.isMatch(mvString, str)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private static RelFunc MULTIVAL_EQ_MULTIVAL = new RelFunc() {
        public boolean evaluate(IEvalValue lhsVal, IEvalValue rhsVal) {
            IMultivalue lhs = (IMultivalue)lhsVal.getValue();
            IMultivalue rhs = (IMultivalue)rhsVal.getValue();
            return lhs.intersects(rhs, GENERIC_MATCHER);
        }
    };

    private static RelFunc MULTIVAL_NEQ_MULTIVAL = new RelFunc() {
        public boolean evaluate(IEvalValue lhsVal, IEvalValue rhsVal) {
            IMultivalue lhs = (IMultivalue)lhsVal.getValue();
            IMultivalue rhs = (IMultivalue)rhsVal.getValue();
            return !lhs.intersects(rhs, GENERIC_MATCHER);
        }
    };

    private static RelFunc MULTIVAL_EQ_NULL = new RelFunc() {
        public boolean evaluate(IEvalValue lhsVal, IEvalValue rhsVal) {
            return multivalRepresentsNull((IMultivalue)lhsVal.getValue());
        }
    };

    private static RelFunc MULTIVAL_NEQ_NULL = new RelFunc() {
        public boolean evaluate(IEvalValue lhsVal, IEvalValue rhsVal) {
            return !multivalRepresentsNull((IMultivalue)lhsVal.getValue());
        }
    };

    private static RelFunc NULL_EQ_MULTIVAL = new RelFunc() {
        public boolean evaluate(IEvalValue lhsVal, IEvalValue rhsVal) {
            return multivalRepresentsNull((IMultivalue)rhsVal.getValue());
        }
    };

    private static RelFunc NULL_NEQ_MULTIVAL = new RelFunc() {
        public boolean evaluate(IEvalValue lhsVal, IEvalValue rhsVal) {
            return !multivalRepresentsNull((IMultivalue)rhsVal.getValue());
        }
    };

    private static boolean multivalRepresentsNull(IMultivalue mv) {
        return mv.isEmpty() || (mv.size() == 1 && mv.includes(IEvalValue.NULL));
    }

    private static RelFunc MULTIVAL_MATCH_L = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            return stringMatchesMultival((String) rhs.getValue(), (IMultivalue) lhs.getValue());
        }
    };

    private static RelFunc MULTIVAL_MATCH_R = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            return stringMatchesMultival((String) lhs.getValue(), (IMultivalue) rhs.getValue(), false);
        }
    };

    private static RelFunc MULTIVAL_NOT_MATCH_L = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            return !stringMatchesMultival((String) rhs.getValue(), (IMultivalue) lhs.getValue());
        }
    };

    private static RelFunc MULTIVAL_NOT_MATCH_R = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            return !stringMatchesMultival((String) lhs.getValue(), (IMultivalue) rhs.getValue(), false);
        }
    };

    private static RelFunc STR_MATCH_F = new RelFunc() {
      public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
          String pat = (String) rhs.getValue();
          String str = (String) lhs.getValue();
          return StringUtils.isMatch( pat, str );
      }

    };

    private static RelFunc STR_NOT_MATCH_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            return !(STR_MATCH_F.evaluate(lhs, rhs));
        }
    };

    private static int compareZonedTimes(ZonedDateTime t1, ZonedDateTime t2) {
        return t1.toInstant().compareTo(t2.toInstant());
    }

    private static RelFunc ZONED_EQ_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime t1 = (ZonedDateTime)lhs.getValue();
            ZonedDateTime t2 = (ZonedDateTime)rhs.getValue();

            return compareZonedTimes(t1, t2) == 0;
        }
    };

    private static RelFunc ZONED_NEQ_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime t1 = (ZonedDateTime)lhs.getValue();
            ZonedDateTime t2 = (ZonedDateTime)rhs.getValue();

            return compareZonedTimes(t1, t2) != 0;
        }
    };

    private static RelFunc ZONED_LT_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime t1 = (ZonedDateTime)lhs.getValue();
            ZonedDateTime t2 = (ZonedDateTime)rhs.getValue();

            return compareZonedTimes(t1, t2) < 0;
        }
    };
    
    private static RelFunc ZONED_LTE_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime t1 = (ZonedDateTime)lhs.getValue();
            ZonedDateTime t2 = (ZonedDateTime)rhs.getValue();

            return compareZonedTimes(t1, t2) <= 0;
        }
    };
    
    private static RelFunc ZONED_GT_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime t1 = (ZonedDateTime)lhs.getValue();
            ZonedDateTime t2 = (ZonedDateTime)rhs.getValue();

            return compareZonedTimes(t1, t2) > 0;
        }
    };
    
    private static RelFunc ZONED_GTE_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime t1 = (ZonedDateTime)lhs.getValue();
            ZonedDateTime t2 = (ZonedDateTime)rhs.getValue();

            return compareZonedTimes(t1, t2) >= 0;
        }
    };
    
    private static RelFunc ZONED_PARTIAL_EQ_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zdt = (ZonedDateTime)lhs.getValue();
            PartialDateTime pdt = (PartialDateTime)rhs.getValue();

            return PartialDateTime.compareTo(zdt, pdt) == 0;
        }
    };
    
    private static RelFunc PARTIAL_ZONED_EQ_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            PartialDateTime pdt = (PartialDateTime)lhs.getValue();
            ZonedDateTime zdt = (ZonedDateTime)rhs.getValue();

            return PartialDateTime.compareTo(zdt, pdt) == 0;
        }
    };

    private static RelFunc ZONED_PARTIAL_NEQ_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zdt = (ZonedDateTime)lhs.getValue();
            PartialDateTime pdt = (PartialDateTime)rhs.getValue();

            return PartialDateTime.compareTo(zdt, pdt) != 0;
        }
    };
    
    private static RelFunc PARTIAL_ZONED_NEQ_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            PartialDateTime pdt = (PartialDateTime)lhs.getValue();
            ZonedDateTime zdt = (ZonedDateTime)rhs.getValue();

            return PartialDateTime.compareTo(zdt, pdt) != 0;
        }
    };

    public static RelFunc ZONED_PARTIAL_LT_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zdt = (ZonedDateTime)lhs.getValue();
            PartialDateTime pdt = (PartialDateTime)rhs.getValue();

            return PartialDateTime.compareTo(zdt, pdt) < 0;
        }
    };
    
    public static RelFunc ZONED_PARTIAL_LTE_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zdt = (ZonedDateTime)lhs.getValue();
            PartialDateTime pdt = (PartialDateTime)rhs.getValue();

            return PartialDateTime.compareTo(zdt, pdt) <= 0;
        }
    };
    
    public static RelFunc ZONED_PARTIAL_GT_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zdt = (ZonedDateTime)lhs.getValue();
            PartialDateTime pdt = (PartialDateTime)rhs.getValue();

            return PartialDateTime.compareTo(zdt, pdt) > 0;
        }
    };
    
    public static RelFunc ZONED_PARTIAL_GTE_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zdt = (ZonedDateTime)lhs.getValue();
            PartialDateTime pdt = (PartialDateTime)rhs.getValue();

            return PartialDateTime.compareTo(zdt, pdt) >= 0;
        }
    };
    
    private static RelFunc ZONED_STRING_EQ_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zlhs = (ZonedDateTime)lhs.getValue();
            String srhs = (String)rhs.getValue();

            try {
                Constant parsedRhs = Constant.buildDate(srhs);

                if (parsedRhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_EQ_F.evaluate(lhs, parsedRhs.getValue());
                }
            } catch (RuntimeException re) {
            }
            
            return false;
        }
    };
    
    private static RelFunc STRING_ZONED_EQ_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            String slhs = (String)lhs.getValue();
            ZonedDateTime zrhs = (ZonedDateTime)rhs.getValue();

            try {
                Constant parsedLhs = Constant.buildDate(slhs);

                if (parsedLhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_EQ_F.evaluate(parsedLhs.getValue(), rhs);
                }
            } catch (RuntimeException re) {
            }
            return false;
        }
    };
    
    private static RelFunc ZONED_STRING_NEQ_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zlhs = (ZonedDateTime)lhs.getValue();
            String srhs = (String)rhs.getValue();

            try {
                Constant parsedRhs = Constant.buildDate(srhs);

                if (parsedRhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_NEQ_F.evaluate(lhs, parsedRhs.getValue());
                }
            } catch (RuntimeException re) {
            }
            
            return true;
        }
    };
    
    private static RelFunc STRING_ZONED_NEQ_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            String slhs = (String)lhs.getValue();
            ZonedDateTime zrhs = (ZonedDateTime)rhs.getValue();

            try {
                Constant parsedLhs = Constant.buildDate(slhs);

                if (parsedLhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_NEQ_F.evaluate(parsedLhs.getValue(), rhs);
                }
            } catch (RuntimeException re) {
            }
            return true;
        }
    };
    
    private static RelFunc ZONED_STRING_LT_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zlhs = (ZonedDateTime)lhs.getValue();
            String srhs = (String)rhs.getValue();

            try {
                Constant parsedRhs = Constant.buildDate(srhs);

                if (parsedRhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_LT_F.evaluate(lhs, parsedRhs.getValue());
                }
            } catch (RuntimeException re) {
            }
            
            return false;
        }
    };
    
    private static RelFunc STRING_ZONED_LT_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            String slhs = (String)lhs.getValue();
            ZonedDateTime zrhs = (ZonedDateTime)rhs.getValue();

            try {
                Constant parsedLhs = Constant.buildDate(slhs);

                if (parsedLhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_LT_F.evaluate(parsedLhs.getValue(), rhs);
                }
            } catch (RuntimeException re) {
            }
            return false;
        }
    };
    
    private static RelFunc ZONED_STRING_LTE_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zlhs = (ZonedDateTime)lhs.getValue();
            String srhs = (String)rhs.getValue();

            try {
                Constant parsedRhs = Constant.buildDate(srhs);

                if (parsedRhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_LTE_F.evaluate(lhs, parsedRhs.getValue());
                }
            } catch (RuntimeException re) {
            }
            
            return false;
        }
    };
    
    private static RelFunc STRING_ZONED_LTE_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            String slhs = (String)lhs.getValue();
            ZonedDateTime zrhs = (ZonedDateTime)rhs.getValue();

            try {
                Constant parsedLhs = Constant.buildDate(slhs);

                if (parsedLhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_LTE_F.evaluate(parsedLhs.getValue(), rhs);
                }
            } catch (RuntimeException re) {
            }
            return false;
        }
    };
        
    
    private static RelFunc ZONED_STRING_GT_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zlhs = (ZonedDateTime)lhs.getValue();
            String srhs = (String)rhs.getValue();

            try {
                Constant parsedRhs = Constant.buildDate(srhs);

                if (parsedRhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_GT_F.evaluate(lhs, parsedRhs.getValue());
                }
            } catch (RuntimeException re) {
            }
            
            return false;
        }
    };
    
    private static RelFunc STRING_ZONED_GT_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            String slhs = (String)lhs.getValue();
            ZonedDateTime zrhs = (ZonedDateTime)rhs.getValue();

            try {
                Constant parsedLhs = Constant.buildDate(slhs);

                if (parsedLhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_GT_F.evaluate(parsedLhs.getValue(), rhs);
                }
            } catch (RuntimeException re) {
            }
            return false;
        }
    };
    
    private static RelFunc ZONED_STRING_GTE_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            ZonedDateTime zlhs = (ZonedDateTime)lhs.getValue();
            String srhs = (String)rhs.getValue();

            try {
                Constant parsedRhs = Constant.buildDate(srhs);

                if (parsedRhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_GTE_F.evaluate(lhs, parsedRhs.getValue());
                }
            } catch (RuntimeException re) {
            }
            
            return false;
        }
    };
    
    private static RelFunc STRING_ZONED_GTE_F = new RelFunc() {
        public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
            String slhs = (String)lhs.getValue();
            ZonedDateTime zrhs = (ZonedDateTime)rhs.getValue();

            try {
                Constant parsedLhs = Constant.buildDate(slhs);

                if (parsedLhs.getValue().getType() == ValueType.ZONED_DATE_TIME) {
                    return ZONED_GTE_F.evaluate(parsedLhs.getValue(), rhs);
                }
            } catch (RuntimeException re) {
            }
            return false;
        }
    };

    // dispatch table
    private static final RelFunc[][][] functions = new RelFunc[RelationOp.numElements()][ValueType.numElements()][ValueType.numElements()];
    static {

        // initialize everything to BAD_TYPE for convenience
        for ( int i = 0; i < functions.length ; i++ ) {
            for (int j = 0; j < functions[0].length; j++) {
                for (int k = 0; k < functions[0][0].length; k++) {
                    functions[i][j][k] = BAD_TYPE;
                }
            }
        }

        // Not passing in a value at all is okay, so we should return something instead
        // of blowing up
        for (int i = 0; i < functions.length; i++) {
            for (int j = 0; j < functions[0].length; j++) {
                for (int k = 0; k < functions[0][0].length; k++) {
                    if (j == ValueType.NULL.getType() || k == ValueType.NULL.getType()) {
                        // Individual functions should override this as needed
                        functions[i][j][k] = FALSE_F;
                    }
                }
            }
        }

        //*****************
        // EQUALS
        //*****************
        final RelFunc[][] equals = functions[RelationOp.EQUALS.getType()];
        for (int lhsIdx = 0; lhsIdx < equals.length; lhsIdx++) {
            if (lhsIdx != ValueType.NULL.getType()) {
                RelFunc[] equalsLHS = equals[lhsIdx];
                for (int rhsIdx = 0; rhsIdx < equalsLHS.length; rhsIdx++) {
                    equalsLHS[rhsIdx] = EQUALS_F;
                }
            }
        }

        equals[ValueType.NULL.getType()][ValueType.NULL.getType()] = TRUE_F;
        equals[ValueType.MULTIVAL.getType()][ValueType.NULL.getType()] = MULTIVAL_EQ_NULL;
        equals[ValueType.NULL.getType()][ValueType.MULTIVAL.getType()] = NULL_EQ_MULTIVAL;
        equals[ValueType.MULTIVAL.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_EQ_MULTIVAL;
        equals[ValueType.MULTIVAL.getType()][ValueType.STRING.getType()] = MULTIVAL_MATCH_L;
        equals[ValueType.STRING.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_MATCH_R;
        equals[ValueType.STRING.getType()][ValueType.STRING.getType()] = STR_MATCH_F;
        equals[ValueType.LONG.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                if ("".equals((String)rhs.getValue())) {
                    return equals[ValueType.LONG.getType()][ValueType.NULL.getType()].evaluate(lhs, rhs);
                }
                return (Long)lhs.getValue() == Long.parseLong((String)(rhs.getValue()));
            }
        };
        equals[ValueType.STRING.getType()][ValueType.LONG.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                if ("".equals((String)lhs.getValue())) {
                    return equals[ValueType.NULL.getType()][ValueType.LONG.getType()].evaluate(lhs, rhs);
                }
                return Long.parseLong((String)(lhs.getValue())) == (Long) rhs.getValue();
            }
        };
        equals[ValueType.STRING.getType()][ValueType.DATE.getType()] = equals[ValueType.STRING.getType()][ValueType.LONG.getType()];
        equals[ValueType.ZONED_DATE_TIME.getType()][ValueType.ZONED_DATE_TIME.getType()] = ZONED_EQ_F;
        equals[ValueType.ZONED_DATE_TIME.getType()][ValueType.PARTIAL_DATE_TIME.getType()] = ZONED_PARTIAL_EQ_F;
        equals[ValueType.PARTIAL_DATE_TIME.getType()][ValueType.ZONED_DATE_TIME.getType()] = PARTIAL_ZONED_EQ_F;
        equals[ValueType.ZONED_DATE_TIME.getType()][ValueType.STRING.getType()] = ZONED_STRING_EQ_F;
        equals[ValueType.STRING.getType()][ValueType.ZONED_DATE_TIME.getType()] = STRING_ZONED_EQ_F;
        
        //**************
        // STRING_EQUALS
        //**************
        final RelFunc[][] str_equals = functions[RelationOp.STRING_EQUALS.getType()];
        str_equals[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareTo(rs) == 0;
            }
        };
        //*******************************
        // STRING_EQUALS_CASE_INSENSITIVE
        //*******************************
        final RelFunc[][] str_equals_case_insensitive = functions[RelationOp.STRING_EQUALS_CASE_INSENSITIVE.getType()];
        str_equals_case_insensitive[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareToIgnoreCase(rs) == 0;
            }
        };
        
        //*****************
        // NOT_EQUALS
        //*****************
        final RelFunc[][] neq = functions[RelationOp.NOT_EQUALS.getType()];
        RelFunc[] neqNull = neq[ValueType.NULL.getType()];
        for (int i = 0; i < neqNull.length; i++) {
            if (i != ValueType.NULL.getType()) {
                neqNull[i] = TRUE_F;
            }
        }

        for (int lhsIdx = 0; lhsIdx < neq.length; lhsIdx++) {
            if (lhsIdx != ValueType.NULL.getType()) {
                RelFunc[] neqLHS = neq[lhsIdx];
                for (int rhsIdx = 0; rhsIdx < neqLHS.length; rhsIdx++) {
                    neqLHS[rhsIdx] = NOT_EQUALS_F;
                }
            }
        }

        neq[ValueType.MULTIVAL.getType()][ValueType.NULL.getType()] = MULTIVAL_NEQ_NULL;
        neq[ValueType.NULL.getType()][ValueType.MULTIVAL.getType()] = NULL_NEQ_MULTIVAL;
        neq[ValueType.MULTIVAL.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_NEQ_MULTIVAL;
        neq[ValueType.MULTIVAL.getType()][ValueType.STRING.getType()] = MULTIVAL_NOT_MATCH_L;
        neq[ValueType.STRING.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_NOT_MATCH_R;
        neq[ValueType.STRING.getType()][ValueType.STRING.getType()] = STR_NOT_MATCH_F;
        neq[ValueType.LONG.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                if ("".equals((String)rhs.getValue())) {
                    return neq[ValueType.LONG.getType()][ValueType.NULL.getType()].evaluate(lhs, rhs);
                }
                return (((Long)lhs.getValue()) != Long.parseLong((String)(rhs.getValue())));
            }
        };
        neq[ValueType.STRING.getType()][ValueType.LONG.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                if ("".equals((String)lhs.getValue())) {
                    return neq[ValueType.NULL.getType()][ValueType.LONG.getType()].evaluate(lhs, rhs);
                }
                return Long.parseLong((String)(lhs.getValue())) != (Long) rhs.getValue();
            }
        };
        neq[ValueType.STRING.getType()][ValueType.DATE.getType()] = neq[ValueType.STRING.getType()][ValueType.LONG.getType()];
        neq[ValueType.ZONED_DATE_TIME.getType()][ValueType.ZONED_DATE_TIME.getType()] = ZONED_NEQ_F;
        neq[ValueType.ZONED_DATE_TIME.getType()][ValueType.PARTIAL_DATE_TIME.getType()] = ZONED_PARTIAL_NEQ_F;
        neq[ValueType.PARTIAL_DATE_TIME.getType()][ValueType.ZONED_DATE_TIME.getType()] = PARTIAL_ZONED_NEQ_F;
        neq[ValueType.ZONED_DATE_TIME.getType()][ValueType.STRING.getType()] = ZONED_STRING_NEQ_F;
        neq[ValueType.STRING.getType()][ValueType.ZONED_DATE_TIME.getType()] = STRING_ZONED_NEQ_F;
        
        //******************
        // STRING_NOT_EQUALS
        //******************
        final RelFunc[][] str_not_equals = functions[RelationOp.STRING_NOT_EQUALS.getType()];
        str_not_equals[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareTo(rs) != 0;
            }
        };
        //***********************************
        // STRING_NOT_EQUALS_CASE_INSENSITIVE
        //***********************************
        final RelFunc[][] str_not_equals_case_insensitive = functions[RelationOp.STRING_NOT_EQUALS_CASE_INSENSITIVE.getType()];
        str_not_equals_case_insensitive[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareToIgnoreCase(rs) != 0;
            }
        };
        
        //**************
        // LT
        //**************
        final RelFunc[][] lt = functions[RelationOp.LESS_THAN.getType()];
        lt[ValueType.LONG.getType()][ValueType.LONG.getType()] = new RelFunc() {
          public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
              return (Long) lhs.getValue() < (Long) rhs.getValue();
          }
        };
        lt[ValueType.LONG.getType()][ValueType.STRING.getType()] = new RelFunc() {
          public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
              if ("".equals((String)rhs.getValue())) {
                  return lt[ValueType.LONG.getType()][ValueType.NULL.getType()].evaluate(lhs, rhs);
              }
              return (((Long) lhs.getValue()).longValue() < Long.parseLong((String)(rhs.getValue())));
          }
        };
        lt[ValueType.STRING.getType()][ValueType.LONG.getType()] = new RelFunc() {
          public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
              if ("".equals((String)lhs.getValue())) {
                  return lt[ValueType.NULL.getType()][ValueType.LONG.getType()].evaluate(lhs, rhs);
              }
              return (Long.parseLong((String)(lhs.getValue())) < ((Long) rhs.getValue()).longValue());
          }
        };
        lt[ValueType.STRING.getType()][ValueType.DATE.getType()] = lt[ValueType.STRING.getType()][ValueType.LONG.getType()];
        lt[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
          public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
              if ("".equals((String)lhs.getValue()) || "".equals((String)rhs.getValue())) {
                  return lt["".equals((String)lhs.getValue()) ? ValueType.NULL.getType() : ValueType.STRING.getType()]["".equals((String)rhs.getValue()) ? ValueType.NULL.getType() : ValueType.STRING.getType()].evaluate(lhs,rhs);
              }
              return (Long.parseLong((String)(lhs.getValue())) < Long.parseLong((String)(rhs.getValue())));
          }
        };
        lt[ValueType.DATE.getType()][ValueType.DATE.getType()] = lt[ValueType.LONG.getType()][ValueType.LONG.getType()];
        lt[ValueType.ZONED_DATE_TIME.getType()][ValueType.ZONED_DATE_TIME.getType()] = ZONED_LT_F;
        lt[ValueType.ZONED_DATE_TIME.getType()][ValueType.PARTIAL_DATE_TIME.getType()] = ZONED_PARTIAL_LT_F;
        lt[ValueType.ZONED_DATE_TIME.getType()][ValueType.STRING.getType()] = ZONED_STRING_LT_F;
        lt[ValueType.STRING.getType()][ValueType.ZONED_DATE_TIME.getType()] = STRING_ZONED_LT_F;


        //**********
        // STRING_LT
        //**********
        final RelFunc[][] string_lt = functions[RelationOp.STRING_LT.getType()];
        string_lt[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareTo(rs) < 0;
            }
        };
        
        //***************************
        // STRING_LT_CASE_INSENSITIVE
        //***************************
        final RelFunc[][] string_lt_case_insensitive = functions[RelationOp.STRING_LT_CASE_INSENSITIVE.getType()];
        string_lt_case_insensitive[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareToIgnoreCase(rs) < 0;
            }
        };
        
        //****
        // LTE
        //****
        final RelFunc[][] lte = functions[RelationOp.LESS_THAN_EQUALS.getType()];
        lte[ValueType.NULL.getType()][ValueType.NULL.getType()] = TRUE_F;
        lte[ValueType.LONG.getType()][ValueType.LONG.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                return (Long)lhs.getValue() <= (Long) rhs.getValue();
            }
        };
        lte[ValueType.LONG.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                if ("".equals((String)rhs.getValue())) {
                    return lte[ValueType.LONG.getType()][ValueType.NULL.getType()].evaluate(lhs, rhs);
                }
                return (Long)lhs.getValue() <= Long.parseLong((String)(rhs.getValue()));
            }
        };
        lte[ValueType.STRING.getType()][ValueType.LONG.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                if ("".equals((String)lhs.getValue())) {
                    return lte[ValueType.NULL.getType()][ValueType.LONG.getType()].evaluate(lhs, rhs);
                }
                return Long.parseLong((String)(lhs.getValue())) <= (Long) rhs.getValue();
            }
        };
        lte[ValueType.STRING.getType()][ValueType.DATE.getType()] = lte[ValueType.STRING.getType()][ValueType.LONG.getType()];
        lte[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
              if ("".equals((String)lhs.getValue()) || "".equals((String)rhs.getValue())) {
                  return lte["".equals((String)lhs.getValue()) ? ValueType.NULL.getType() : ValueType.STRING.getType()]["".equals((String)rhs.getValue()) ? ValueType.NULL.getType() : ValueType.STRING.getType()].evaluate(lhs,rhs);
              }
              return Long.parseLong((String)(lhs.getValue())) <= Long.parseLong((String)(rhs.getValue()));
            }
        };
        lte[ValueType.DATE.getType()][ValueType.DATE.getType()] = lte[ValueType.LONG.getType()][ValueType.LONG.getType()];
        lte[ValueType.ZONED_DATE_TIME.getType()][ValueType.ZONED_DATE_TIME.getType()] = ZONED_LTE_F;
        lte[ValueType.ZONED_DATE_TIME.getType()][ValueType.PARTIAL_DATE_TIME.getType()] = ZONED_PARTIAL_LTE_F;
        lte[ValueType.ZONED_DATE_TIME.getType()][ValueType.STRING.getType()] = ZONED_STRING_LTE_F;
        lte[ValueType.STRING.getType()][ValueType.ZONED_DATE_TIME.getType()] = STRING_ZONED_LTE_F;

        //***********
        // STRING_LTE
        //***********
        final RelFunc[][] string_lte = functions[RelationOp.STRING_LTE.getType()];
        string_lte[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareTo(rs) <= 0;
            }
        };
        
        //****************************
        // STRING_LTE_CASE_INSENSITIVE
        //*****************************
        final RelFunc[][] string_lte_case_insensitive = functions[RelationOp.STRING_LTE_CASE_INSENSITIVE.getType()];
        string_lte_case_insensitive[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareToIgnoreCase(rs) <= 0;
            }
        };
        
        //**************
        // GT
        //**************
        final RelFunc[][] gt = functions[RelationOp.GREATER_THAN.getType()];
        gt[ValueType.LONG.getType()][ValueType.LONG.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                return (Long)lhs.getValue() > (Long)rhs.getValue();
            }
        };
        gt[ValueType.LONG.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                if ("".equals((String)rhs.getValue())) {
                    return gt[ValueType.LONG.getType()][ValueType.NULL.getType()].evaluate(lhs, rhs);
                }
                return (Long)lhs.getValue() > Long.parseLong((String)(rhs.getValue()));
            }
        };
        gt[ValueType.STRING.getType()][ValueType.LONG.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                if ("".equals((String)lhs.getValue())) {
                    return gt[ValueType.NULL.getType()][ValueType.LONG.getType()].evaluate(lhs, rhs);
                }
                return Long.parseLong((String)(lhs.getValue())) > (Long) rhs.getValue();
            }
        };
        gt[ValueType.STRING.getType()][ValueType.DATE.getType()] = gt[ValueType.STRING.getType()][ValueType.LONG.getType()];
        gt[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
              if ("".equals((String)lhs.getValue()) || "".equals((String)rhs.getValue())) {
                  return gt["".equals((String)lhs.getValue()) ? ValueType.NULL.getType() : ValueType.STRING.getType()]["".equals((String)rhs.getValue()) ? ValueType.NULL.getType() : ValueType.STRING.getType()].evaluate(lhs,rhs);
              }
                return Long.parseLong((String)(lhs.getValue())) > Long.parseLong((String)(rhs.getValue()));
            }
        };
        gt[ValueType.DATE.getType()][ValueType.DATE.getType()] = gt[ValueType.LONG.getType()][ValueType.LONG.getType()];
        gt[ValueType.ZONED_DATE_TIME.getType()][ValueType.ZONED_DATE_TIME.getType()] = ZONED_GT_F;
        gt[ValueType.ZONED_DATE_TIME.getType()][ValueType.PARTIAL_DATE_TIME.getType()] = ZONED_PARTIAL_GT_F;
        gt[ValueType.ZONED_DATE_TIME.getType()][ValueType.STRING.getType()] = ZONED_STRING_GT_F;
        gt[ValueType.STRING.getType()][ValueType.ZONED_DATE_TIME.getType()] = STRING_ZONED_GT_F;

        //**********
        // STRING_GT
        //**********
        final RelFunc[][] string_gt = functions[RelationOp.STRING_GT.getType()];
        string_gt[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareTo(rs) > 0;
            }
        };
        
        //***************************
        // STRING_GT_CASE_INSENSITIVE
        //***************************
        final RelFunc[][] string_gt_case_insensitive = functions[RelationOp.STRING_GT_CASE_INSENSITIVE.getType()];
        string_gt_case_insensitive[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareToIgnoreCase(rs) > 0;
            }
        };
        
        //**************
        // GTE
        //**************
        final RelFunc[][] gte = functions[RelationOp.GREATER_THAN_EQUALS.getType()];
        gte[ValueType.NULL.getType()][ValueType.NULL.getType()] = TRUE_F;
        gte[ValueType.LONG.getType()][ValueType.LONG.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                return (Long) lhs.getValue() >= (Long) rhs.getValue();
            }
        };
        gte[ValueType.LONG.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                if ("".equals((String)rhs.getValue())) {
                    return gte[ValueType.LONG.getType()][ValueType.NULL.getType()].evaluate(lhs, rhs);
                }
                return (Long)lhs.getValue() >= Long.parseLong((String)(rhs.getValue()));
            }
        };
        gte[ValueType.STRING.getType()][ValueType.LONG.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                if ("".equals((String)lhs.getValue())) {
                    return gte[ValueType.NULL.getType()][ValueType.STRING.getType()].evaluate(lhs, rhs);
                }
                return Long.parseLong((String)(lhs.getValue())) >= (Long)rhs.getValue();
            }
        };
        gte[ValueType.STRING.getType()][ValueType.DATE.getType()] = gte[ValueType.STRING.getType()][ValueType.LONG.getType()];
        gte[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                if ("".equals((String)lhs.getValue()) || "".equals((String)rhs.getValue())) {
                    return gte["".equals((String)lhs.getValue()) ? ValueType.NULL.getType() : ValueType.STRING.getType()]["".equals((String)rhs.getValue()) ? ValueType.NULL.getType() : ValueType.STRING.getType()].evaluate(lhs,rhs);
                }
                return (Long.parseLong((String)(lhs.getValue())) >= Long.parseLong((String)(rhs.getValue())));
            }
        };
        gte[ValueType.DATE.getType()][ValueType.DATE.getType()] = gte[ValueType.LONG.getType()][ValueType.LONG.getType()];
        gte[ValueType.ZONED_DATE_TIME.getType()][ValueType.ZONED_DATE_TIME.getType()] = ZONED_GTE_F;
        gte[ValueType.ZONED_DATE_TIME.getType()][ValueType.PARTIAL_DATE_TIME.getType()] = ZONED_PARTIAL_GTE_F;
        gte[ValueType.ZONED_DATE_TIME.getType()][ValueType.STRING.getType()] = ZONED_STRING_GTE_F;
        gte[ValueType.STRING.getType()][ValueType.ZONED_DATE_TIME.getType()] = STRING_ZONED_GTE_F;

        //***********
        // STRING_GTE
        //***********
        final RelFunc[][] string_gte = functions[RelationOp.STRING_GTE.getType()];
        string_gte[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareTo(rs) >= 0;
            }
        };
        
        //****************************
        // STRING_GTE_CASE_INSENSITIVE
        //****************************
        final RelFunc[][] string_gte_case_insensitive = functions[RelationOp.STRING_GTE_CASE_INSENSITIVE.getType()];
        string_gte_case_insensitive[ValueType.STRING.getType()][ValueType.STRING.getType()] = new RelFunc() {
            public boolean evaluate(IEvalValue lhs, IEvalValue rhs) {
                String ls = (String)lhs.getValue();
                String rs = (String)rhs.getValue();

                return ls.compareToIgnoreCase(rs) >= 0;
            }
        };
        
        //**************
        // HAS
        //**************
        final RelFunc[][] has = functions[RelationOp.HAS.getType()];
        has[ValueType.MULTIVAL.getType()][ValueType.LONG.getType()] = MULTIVAL_HAS_L;
        has[ValueType.MULTIVAL.getType()][ValueType.STRING.getType()] = MULTIVAL_HAS_L;
        has[ValueType.MULTIVAL.getType()][ValueType.DATE.getType()] = MULTIVAL_HAS_L;
        has[ValueType.LONG.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_HAS_R;
        has[ValueType.STRING.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_HAS_R;
        has[ValueType.DATE.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_HAS_R;

        //**************
        // DOES_NOT_HAVE
        //**************
        final RelFunc[][] dnh = functions[RelationOp.DOES_NOT_HAVE.getType()];
        dnh[ValueType.MULTIVAL.getType()][ValueType.LONG.getType()] = MULTIVAL_HAS_NOT_L;
        dnh[ValueType.MULTIVAL.getType()][ValueType.STRING.getType()] = MULTIVAL_HAS_NOT_L;
        dnh[ValueType.MULTIVAL.getType()][ValueType.DATE.getType()] = MULTIVAL_HAS_NOT_L;
        dnh[ValueType.LONG.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_HAS_NOT_R;
        dnh[ValueType.STRING.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_HAS_NOT_R;
        dnh[ValueType.DATE.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_HAS_NOT_R;

        //*********
        // INCLUDES
        //*********
        final RelFunc[][] includes_all = functions[RelationOp.INCLUDES.getType()];

        // We'll define everything as A=B first, and then override the cases where one
        // argument is a multivalue. This lets "A includes B" work when A and B are both
        // strings, which is friendly
        for (int lhsIdx = 0; lhsIdx < includes_all.length; lhsIdx++) {
            if (lhsIdx != ValueType.NULL.getType()) {
                RelFunc[] includesAllLHS = includes_all[lhsIdx];
                for (int rhsIdx = 0; rhsIdx < includesAllLHS.length; rhsIdx++) {
                    includesAllLHS[rhsIdx] = equals[lhsIdx][rhsIdx];
                }
            }
        }
        
        includes_all[ValueType.MULTIVAL.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_INCLUDES_MULTIVAL_F;
        includes_all[ValueType.MULTIVAL.getType()][ValueType.NULL.getType()] = TRUE_F;
        includes_all[ValueType.MULTIVAL.getType()][ValueType.STRING.getType()] = MULTIVAL_INCLUSION;
        includes_all[ValueType.MULTIVAL.getType()][ValueType.LONG.getType()] = MULTIVAL_INCLUSION;
        includes_all[ValueType.MULTIVAL.getType()][ValueType.DATE.getType()] = MULTIVAL_INCLUSION;
        includes_all[ValueType.MULTIVAL.getType()][ValueType.ZONED_DATE_TIME.getType()] = MULTIVAL_INCLUSION;
        // It may seem odd to have "SV includes MV", but the person writing the policy 
        // side really will contain multiple values or could be just a single value, so we should do our best to make
        // it work
        includes_all[ValueType.STRING.getType()][ValueType.MULTIVAL.getType()] = VAL_INCLUSION;
        includes_all[ValueType.LONG.getType()][ValueType.MULTIVAL.getType()] = VAL_INCLUSION;
        includes_all[ValueType.DATE.getType()][ValueType.MULTIVAL.getType()] = VAL_INCLUSION;
        includes_all[ValueType.ZONED_DATE_TIME.getType()][ValueType.MULTIVAL.getType()] = VAL_INCLUSION;
        includes_all[ValueType.NULL.getType()][ValueType.NULL.getType()] = TRUE_F;

        //*********************
        // MULTIVAL EQUIVALENCE
        //*********************
        final RelFunc[][] equiv = functions[RelationOp.EQUALS_UNORDERED.getType()];

        // Default to = and override special cases. Same reasoning as includes_all
        for (int lhsIdx = 0; lhsIdx < equiv.length; lhsIdx++) {
            if (lhsIdx != ValueType.NULL.getType()) {
                RelFunc[] equivLHS = equiv[lhsIdx];
                for (int rhsIdx = 0; rhsIdx < equivLHS.length; rhsIdx++) {
                    equivLHS[rhsIdx] = equals[lhsIdx][rhsIdx];
                }
            }
        }
        
        equiv[ValueType.MULTIVAL.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_EQUIV_MULTIVAL;
        equiv[ValueType.MULTIVAL.getType()][ValueType.STRING.getType()] = MULTIVAL_EQUIV_SINGLEVAL_L;
        equiv[ValueType.MULTIVAL.getType()][ValueType.LONG.getType()] = MULTIVAL_EQUIV_SINGLEVAL_L;
        equiv[ValueType.MULTIVAL.getType()][ValueType.DATE.getType()] = MULTIVAL_EQUIV_SINGLEVAL_L;
        equiv[ValueType.MULTIVAL.getType()][ValueType.ZONED_DATE_TIME.getType()] = MULTIVAL_EQUIV_SINGLEVAL_L;
        equiv[ValueType.LONG.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_EQUIV_SINGLEVAL_R;
        equiv[ValueType.STRING.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_EQUIV_SINGLEVAL_R;
        equiv[ValueType.DATE.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_EQUIV_SINGLEVAL_R;
        equiv[ValueType.ZONED_DATE_TIME.getType()][ValueType.MULTIVAL.getType()] = MULTIVAL_EQUIV_SINGLEVAL_R;

        equiv[ValueType.NULL.getType()][ValueType.NULL.getType()] = TRUE_F;

        //**************
        // WITHIN LAST
        //**************
        final RelFunc[][] within_last = functions[RelationOp.WITHIN_LAST.getType()];
        within_last[ValueType.STRING.getType()][ValueType.STRING.getType()] = STRING_WITHIN_LAST_COMPARISON;
        within_last[ValueType.LONG.getType()][ValueType.STRING.getType()] = LONG_WITHIN_LAST_COMPARISON;
        within_last[ValueType.ZONED_DATE_TIME.getType()][ValueType.STRING.getType()] = ZDT_WITHIN_LAST_COMPARISON;
        within_last[ValueType.DATE.getType()][ValueType.STRING.getType()] = LONG_WITHIN_LAST_COMPARISON;
    }

    public Relation(RelationOp op, IExpression lhs, IExpression rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public final boolean match(IArguments arg) {

        IEvalValue lhsVal = lhs.evaluate(arg);
        IEvalValue rhsVal = rhs.evaluate(arg);

        RelFunc f = functions[op.getType()][lhsVal.getType().getType()][rhsVal.getType().getType()];
        return f.evaluate(lhsVal, rhsVal);
    }

    public void accept( IPredicateVisitor visitor, IPredicateVisitor.Order order ) {
        visitor.visit( this );
    }

    public RelationOp getOp() {
        return op;
    }

    public IExpression getLHS() {
        return lhs;
    }

    public IExpression getRHS() {
        return rhs;
    }

    private interface RelFunc {

        boolean evaluate(IEvalValue lhs, IEvalValue rhs);
    }

    /**
     * @param op
     */
    public void setOp(RelationOp op) {
        this.op = op;
    }

    /**
     * @param lhs
     */
    public void setLHS(IExpression lhs) {
        this.lhs = lhs;
    }

    /**
     * Sets the rhs
     * @param rhs The rhs to set.
     */
    public void setRHS(IExpression rhs) {
        this.rhs = rhs;
    }

    public String toString() {
        return lhs + " " + op + " " + rhs;
    }

    private static class TimeIntervalWithUnit {
        public static class TimeIntervalUnit extends EnumBase {
            public static final TimeIntervalUnit SECOND = new TimeIntervalUnit("second");
            public static final TimeIntervalUnit MINUTE = new TimeIntervalUnit("minute");
            public static final TimeIntervalUnit HOUR = new TimeIntervalUnit("hour");
            public static final TimeIntervalUnit DAY = new TimeIntervalUnit("day");
            public static final TimeIntervalUnit WEEK = new TimeIntervalUnit("week");
            public static final TimeIntervalUnit MONTH = new TimeIntervalUnit("month");
            public static final TimeIntervalUnit YEAR = new TimeIntervalUnit("year");
            
            private TimeIntervalUnit(String name) {
                super(name);
            }
            
            public static TimeIntervalUnit getUnit(String unitName) {
                if (unitName == null) {
                    throw new NullPointerException("unitName cannot be null");
                }

                // Remove trailing 's' from e.g. "minutes"
                if (unitName.endsWith("s")) {
                    unitName = unitName.substring(0, unitName.length()-1);
                }

                return getElement(unitName, TimeIntervalUnit.class);
            }
        }
        
        private Long scalar;
        private TimeIntervalUnit unit;
        
        private TimeIntervalWithUnit(String str) {
            String[] strs = str.split(" ");

            scalar = Long.parseLong(strs[0]);
            unit = TimeIntervalUnit.getUnit(strs[1]);
        }

        public Long getScalar() {
            return scalar;
        }

        public TimeIntervalUnit getUnit() {
            return unit;
        }

        public static ZonedDateTime addToTime(ZonedDateTime start, String timeInterval) {
            return addToTime(start, new TimeIntervalWithUnit(timeInterval));
        }
        
        public static ZonedDateTime addToTime(ZonedDateTime start, TimeIntervalWithUnit tiwu) {
            if (tiwu.getUnit() == TimeIntervalUnit.SECOND) {
                return start.plusSeconds(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.MINUTE) {
                return start.plusMinutes(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.HOUR) {
                return start.plusHours(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.DAY) {
                return start.plusDays(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.WEEK) {
                return start.plusWeeks(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.MONTH) {
                return start.plusMonths(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.YEAR) {
                return start.plusYears(tiwu.getScalar());
            }

            return start;
        }

        public static ZonedDateTime subtractFromTime(ZonedDateTime start, String timeInterval) {
            return subtractFromTime(start, new TimeIntervalWithUnit(timeInterval));
        }
        
        public static ZonedDateTime subtractFromTime(ZonedDateTime start, TimeIntervalWithUnit tiwu) {
            if (tiwu.getUnit() == TimeIntervalUnit.SECOND) {
                return start.minusSeconds(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.MINUTE) {
                return start.minusMinutes(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.HOUR) {
                return start.minusHours(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.DAY) {
                return start.minusDays(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.WEEK) {
                return start.minusWeeks(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.MONTH) {
                return start.minusMonths(tiwu.getScalar());
            } else if (tiwu.getUnit() == TimeIntervalUnit.YEAR) {
                return start.minusYears(tiwu.getScalar());
            }
            
            return start;
        }
    }
}


