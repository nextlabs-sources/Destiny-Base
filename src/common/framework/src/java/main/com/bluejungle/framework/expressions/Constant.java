/*
 * Created on Feb 17, 2005
 *
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc., Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved worldwide.
 */
package com.bluejungle.framework.expressions;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import com.nextlabs.framework.expressions.PartialDateTime;

/**
 * @author sasha
 */

public class Constant implements IExpression {

    public static final Constant NULL = new Constant(IEvalValue.NULL);

    public static final Constant EMPTY = new Constant(IEvalValue.EMPTY);

    public static Constant build(IMultivalue mv, String rep) {
        return new Constant(EvalValue.build(mv), rep);
    }

    public static Constant build(String s) {
        return new Constant(EvalValue.build(s));
    }

    public static Constant build(String s, String rep) {
        return new Constant(EvalValue.build(s), rep);
    }

    public static Constant build(long l) {
        return new Constant(EvalValue.build(l));
    }

    public static Constant build(long l, String rep) {
        return new Constant(EvalValue.build(l), rep);
    }

    public static Constant build(Date d) {
        return new Constant(EvalValue.build(d));
    }

    public static Constant build(Date d, String rep) {
        return new Constant(EvalValue.build(d), rep);
    }

    public static Constant build(ZonedDateTime zdt, String rep) {
        return new Constant(EvalValue.build(zdt), rep);
    }
    
    public static Constant build(PartialDateTime dv) {
        return new Constant(EvalValue.build(dv));
    }
    
    public static Constant build(PartialDateTime dv, String rep) {
        return new Constant(EvalValue.build(dv), rep);
    }
    
    public static Constant build(LocalDateTime dv, String rep) {
        return new Constant(EvalValue.build(dv), rep);
    }

    /**
     * This may not be the best place for this function, but there are
     * two places where we need to take a string that represents a
     * date (in, potentially, a few different formats) and convert it
     * to a "date thing".
     *
     * We have a legacy fomat (which did not have a time-zone and had no
     * leading 0s) and the new format (which may have a time-zone and has
     * leading 0s).
     *
     * We'll also add ISO-8601 to the mix.
     */
    private static final DateTimeFormatter DATE_TIME_OPT_ZONE_FORMATTER =
        DateTimeFormatter.ofPattern("MMM d, yyyy hh:mm:ss a[ ][ ][v]").withLocale(Locale.US);
    
    private static final DateTimeFormatter LEGACY_DATE_TIME_FORMATTER =
        new DateTimeFormatterBuilder().appendPattern("MMM d, yyyy h:mm:ss a").toFormatter().withZone(ZoneId.systemDefault()).withLocale(Locale.US);

    public static Constant buildDate(String value) {
        TemporalAccessor ta = null;
        
        try {
            // NextLabs standard
            ta = DATE_TIME_OPT_ZONE_FORMATTER.parseBest(value, ZonedDateTime::from, LocalDateTime::from);
        } catch (DateTimeParseException e) {
            try {
                // No? ISO standard?
                ta = ISO_DATE_TIME.parseBest(value, ZonedDateTime::from, LocalDateTime::from);
            } catch (DateTimeParseException e1) {
                // Try legacy. This has no time zone
                try {
                    ZonedDateTime zonedDateTime = LEGACY_DATE_TIME_FORMATTER.parse(value, ZonedDateTime::from);
                    return Constant.build(zonedDateTime, '"' + value + '"');
                } catch (DateTimeParseException e2) {
                    return Constant.build(ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()), "error parsing: " + value);
                }
            }
        }
        
        if (ta instanceof ZonedDateTime) {
            return Constant.build((ZonedDateTime)ta, '"' + value + '"');
        } else {
            return Constant.build(PartialDateTime.fromDateTime((LocalDateTime)ta), '"' + value + '"');
        }
    }
    
    protected IEvalValue val;
    protected String rep;

    protected Constant(IEvalValue val) {
        this(val, toString(val));
    }

    protected Constant(IEvalValue val, String rep) {
        if ( val == null ) {
            throw new NullPointerException("value");
        }
        if ( rep == null ) {
            throw new NullPointerException("representation");
        }
        this.rep = rep;
        this.val = val;
    }

    /**
     * @see com.bluejungle.framework.expressions.IExpression#evaluate(com.bluejungle.framework.expressions.IArguments)
     */
    public IEvalValue evaluate(IArguments arg) {
        return val;
    }

    public IEvalValue getValue() {
        return val;
    }

    public String getRepresentation() {
	return rep;
    }

    public String toString() {
        return val.getType().formatRepresentation(rep);
    }

    private static String toString(IEvalValue val) {
        return ( val != null ) ? String.valueOf(val.getValue()) : "<NULL>";
    }

    /**
     * @see com.bluejungle.framework.expressions.IExpression#buildRelation(com.bluejungle.framework.expressions.RelationOp, com.bluejungle.framework.expressions.IExpression)
     */
    public IRelation buildRelation (RelationOp op, IExpression rhs) {
        return new Relation (op, this, rhs);
    }

    /**
     * @see com.bluejungle.framework.expressions.IExpression#acceptVisitor(com.bluejungle.framework.expressions.IExpressionVisitor)
     */
    public void acceptVisitor(IExpressionVisitor visitor, IExpressionVisitor.Order order) {
        visitor.visit((Constant) this);
    }

}
