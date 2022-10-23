/*
 * Created on Apr 20, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.pf.domain.destiny.environment;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.bluejungle.framework.expressions.Constant;
import com.bluejungle.framework.expressions.EvalValue;
import com.bluejungle.framework.expressions.IArguments;
import com.bluejungle.framework.expressions.IAttribute;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.IExpression;
import com.bluejungle.framework.expressions.IExpressionVisitor;
import com.bluejungle.framework.expressions.IRelation;
import com.bluejungle.framework.expressions.Relation;
import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.framework.expressions.IExpressionVisitor.Order;
import com.bluejungle.framework.patterns.EnumBase;
import com.bluejungle.pf.domain.epicenter.evaluation.IEvaluationRequest;
import com.nextlabs.framework.expressions.PartialDateTime;

/**
 * @author sasha
 *
 * This class contains implementations of time attributes, which allow
 * expressions to be based on the month, week, day of week, or time of
 * day.
 *
 * Traditionally this is evaluated relative to the PDP, but if
 * the EvaluationRequest specifies a different time then it will be
 * evaluated based on that (see calls to getPepTime()). 
 *
 * It is assumed that on-prem systems will largely ignore this
 * ability, as the PEP and PDP are almost always in the same time
 * zone.
 */

public abstract class TimeAttribute extends EnumBase implements IAttribute {
    public static final TimeAttribute IDENTITY = new TimeAttribute("identity") {
        public IExpression build(String value) {
            return Constant.buildDate(value);
        }
    };
    
    public static final TimeAttribute YEAR = new TimeAttribute("year") {
        public IExpression build(String value) {
            return Constant.build(PartialDateTime.fromYear(value), '"' + value + '"');
        }
    };
    
    public static final TimeAttribute WEEKDAY = new TimeAttribute("weekday") {
        // Weekday is stored as a long value 
        public IExpression build(String value) {
            return Constant.build(PartialDateTime.fromWeekday(value), '"' + value + '"');
        }
    };
    
    /**
     * Time of day, i.e. 05:00 PM
     */
    public static final TimeAttribute TIME = new TimeAttribute("time") {
        // time of day is stored as number of milliseconds, between 0 and MILLIS_PER_DAY - 1;
        public IExpression build(String value) {
            return Constant.build(PartialDateTime.fromTimeOfDay(value), '"' + value + '"');
        }
    };
    
    /**
     * date within a month, i.e. 16th
     */
    public static final TimeAttribute DATE = new TimeAttribute("date") {
        public IExpression build(String value) {
            return Constant.build(PartialDateTime.fromDate(value), '"' + value + '"');
        }
    };
    
    /**
     * day of week in month, i.e. the number 2 in "every 2nd monday of
     * the month" negative numbers count from the end of the month, so
     * "last monday of the month" is -1
     */
    public static final TimeAttribute DOWIM = new TimeAttribute("day_in_month") {
        public IExpression build(String value) {
            return Constant.build(PartialDateTime.fromDOWIM(value), '"' + value + '"');
        }
    };

    public IEvalValue evaluate(IArguments arg) {
        ZonedDateTime timeOfEvaluation = ZonedDateTime.now();
        
        if (arg instanceof IEvaluationRequest) {
            IEvaluationRequest req = (IEvaluationRequest)arg;
            
            timeOfEvaluation = req.getPepTime() != null ? req.getPepTime() : timeOfEvaluation;
        }
        
        return EvalValue.build(timeOfEvaluation);
    }
    
    public IRelation buildRelation(RelationOp op, String value) {
        IExpression rhs = build(value);
        return new Relation(op, this, rhs);
    }
    
    public IRelation buildRelation(RelationOp op, IExpression expr) {
        if (expr instanceof Constant) {
            return buildRelation(op, ((Constant)expr).getRepresentation());
        } else {
            return new Relation(op, this, expr);
        }
    }
    
    
    
    /**
     * @see com.bluejungle.framework.expressions.IExpression#acceptVisitor(com.bluejungle.framework.expressions.IExpressionVisitor, com.bluejungle.framework.expressions.IExpressionVisitor.Order)
     */
    public void acceptVisitor(IExpressionVisitor visitor, Order order) {
        visitor.visit((IAttribute) this);
    }
    
    
    /**
     * @see com.bluejungle.framework.expressions.IAttribute#getObjectTypeName()
     */
    public String getObjectTypeName() {
        return "CURRENT_TIME";
    }
    /**
     * @see com.bluejungle.framework.expressions.IAttribute#getObjectTypeName()
     */
    public String getObjectSubTypeName() {
        return null;
    }
    public static TimeAttribute getElement(String name) {
        return getElement(name, TimeAttribute.class);
    }
    
    public static boolean existsElement(String name) {
        return existsElement(name, TimeAttribute.class);
    }
    
    private TimeAttribute(String name) {
        super(name, TimeAttribute.class);
    }
    
    public abstract IExpression build(String value);

    protected static final HashMap<String, Long> WEEKDAY_MAP = new HashMap<String, Long>();
    protected static final long MILLIS_PER_SECOND = 1000;
    protected static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;    
    protected static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;        
    
    static {
        String[] weekdays = new DateFormatSymbols(Locale.US).getWeekdays();
        for (int i = 0; i < weekdays.length; i++) {
            WEEKDAY_MAP.put(weekdays[i].toLowerCase(), new Long(i));
        }
    }
}














