/*
 * Created on April 24, 2007
 *
 * All sources, binaries, and HTML pages (C) copyright 2007 by Next Labs Inc.,
 * San Mateo CA.  Ownership remains with Next Labs Inc.  All rights reserved
 * worldwide.
 */

package com.bluejungle.framework.expressions;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Date;

import junit.framework.TestCase;

import com.bluejungle.framework.expressions.Constant;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.IExpression;
import com.bluejungle.framework.expressions.IMultivalue;
import com.bluejungle.framework.expressions.Multivalue;
import com.bluejungle.framework.expressions.Relation;
import com.bluejungle.framework.expressions.RelationOp;
import com.nextlabs.framework.expressions.PartialDateTime;

/*
 * @author amorgan
 */

public class RelationTest extends TestCase {
    private Constant beatles = Constant.build(Multivalue.create(new String[] { "John", "Paul", "George", "Ringo" }), "FABFOUR");
    private Constant beatlesRegexp = Constant.build(Multivalue.create(new String[] { "Jo?C", "Pa?C", "Geo?C", "R?C" }), "FABFOUR");
    private Constant justPaul = Constant.build(Multivalue.create(new String[] { "Paul"}), "The good looking one");
    private Constant justPaulRegexp = Constant.build(Multivalue.create(new String[] { "Pau?C"}), "The good looking one");
    private Constant alphabeatles = Constant.build(Multivalue.create(new String[] { "George", "John", "Paul", "Ringo" }), "ABFFORU");
    private Constant livingBeatles = Constant.build(Multivalue.create(new String[] { "Paul", "Ringo" }), "LIVING");
    private Constant smallPrimes = Constant.build(Multivalue.create (new Long[] { 2L, 3L, 5L, 7L, 11L }), "PRIMES");
    private Constant emptyMultival = Constant.build(Multivalue.create (new String[0]), "EMPTY");
    private ZonedDateTime lateThursdayLosAngeles = ZonedDateTime.of(2020, 10, 15, 19, 0, 0, 0, ZoneId.of("America/Los_Angeles"));
    private String lateThursdayLosAngelesAsString = "Oct 15, 2020 07:00:00 PM America/Los_Angeles";
    private String lateThursdayLosAngelesAsISO = "2020-10-15T19:00:00-07:00";
    private ZonedDateTime earlyFridaySingapore = ZonedDateTime.of(2020, 10, 16, 10, 0, 0, 0, ZoneId.of("Asia/Singapore"));
    private String earlyFridaySingaporeAsString = "Oct 16, 2020 10:00:00 AM Asia/Singapore";
    private ZonedDateTime lateFridaySingapore = ZonedDateTime.of(2020, 10, 16, 20, 0, 0, 0, ZoneId.of("Asia/Singapore"));
    private String lateFridaySingaporeAsString = "Oct 16, 2020 08:00:00 PM Asia/Singapore";
    private PartialDateTime thursdayLocal = PartialDateTime.fromWeekday("Thursday");
    private PartialDateTime thursdayLA = PartialDateTime.fromWeekday("Thursday America/Los_Angeles");
    private PartialDateTime thursdaySingapore = PartialDateTime.fromWeekday("Thursday Asia/Singapore");
    private PartialDateTime fridayLocal = PartialDateTime.fromWeekday("Friday");
    private PartialDateTime fridayLA = PartialDateTime.fromWeekday("Friday America/Los_Angeles");
    private PartialDateTime fridaySingapore = PartialDateTime.fromWeekday("Friday Asia/Singapore");
    private PartialDateTime eveningLocal = PartialDateTime.fromTimeOfDay("19:00:00");
    private PartialDateTime eveningLA = PartialDateTime.fromTimeOfDay("19:00:00 America/Los_Angeles");
    private PartialDateTime eveningSingapore = PartialDateTime.fromTimeOfDay("19:00:00 Asia/Singapore");
    private PartialDateTime morningLocal = PartialDateTime.fromTimeOfDay("10:00:00");
    private PartialDateTime morningLA = PartialDateTime.fromTimeOfDay("10:00:00 America/Los_Angeles");
    private PartialDateTime morningSingapore = PartialDateTime.fromTimeOfDay("10:00:00 Asia/Singapore");
    private PartialDateTime fifteenthLocal = PartialDateTime.fromDate("15");
    private PartialDateTime fifteenthLA = PartialDateTime.fromDate("15 America/Los_Angeles");
    private PartialDateTime fifteenthSingapore = PartialDateTime.fromDate("15 Asia/Singapore");
    private PartialDateTime sixteenthLocal = PartialDateTime.fromDate("16");
    private PartialDateTime sixteenthLA = PartialDateTime.fromDate("16 America/Los_Angeles");
    private PartialDateTime sixteenthSingapore = PartialDateTime.fromDate("16 Asia/Singapore");
    private ZonedDateTime newYearsEveLA = ZonedDateTime.of(2020, 12, 31, 19, 0, 0, 0, ZoneId.of("America/Los_Angeles"));
    private String newYearsEveLAasString = "Dec 31, 2020 07:00:00 PM America/Los_Angeles";
    private ZonedDateTime newYearsEveSingapore = ZonedDateTime.of(2020, 12, 31, 19, 0, 0, 0, ZoneId.of("Asia/Singapore"));
    private String newYearsEveSingaporeAsString = "Dec 31, 2020 07:00:00 PM Asia/Singapore";
    private ZonedDateTime newYearsDaySingapore = ZonedDateTime.of(2021, 1, 1, 4, 0, 0, 0, ZoneId.of("Asia/Singapore"));
    private String newYearsDaySingaporeAsString = "Jan 1, 2021 04:00:00 AM Asia/Singapore";
    private PartialDateTime year2020Local = PartialDateTime.fromYear("2020");
    private PartialDateTime year2020LA = PartialDateTime.fromYear("2020 America/Los_Angeles");
    private PartialDateTime year2020Singapore = PartialDateTime.fromYear("2020 Asia/Singapore");
    private PartialDateTime year2021Local = PartialDateTime.fromYear("2021");
    private PartialDateTime year2021LA = PartialDateTime.fromYear("2021 America/Los_Angeles");
    private PartialDateTime year2021Singapore = PartialDateTime.fromYear("2021 Asia/Singapore");
    private PartialDateTime newYears2099 = PartialDateTime.fromDateTime(LocalDateTime.of(2099, 12, 31, 23, 59));

    // Test dates relative to current time
    
    public RelationTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    private void check(RelationOp op, IExpression lhs, IExpression rhs, boolean expected) {
        Relation r = new Relation(op, lhs, rhs);
        assertEquals(expected, r.match(null));
    }

    private void checkIncompatible(RelationOp op, IExpression lhs, IExpression rhs) {
        Relation r = new Relation(op, lhs, rhs);
        try {
            r.match(null);
        } catch (RuntimeException e) {
            assertTrue(true);
            return;
        }

        assertTrue(false);
    }
            
    public final void testEQ() {
        RelationOp op = RelationOp.EQUALS;

        check(op, Constant.build(3), Constant.build(3), true);
        check(op, Constant.build("George"), beatles, true);
        check(op, justPaulRegexp, beatles, false);
        check(op, justPaul, beatlesRegexp, true);
        check(op, Constant.build("Paul"), justPaulRegexp, true);
        check(op, beatles, livingBeatles, true);
        check(op, alphabeatles, beatles, true);
        check(op, alphabeatles, beatlesRegexp, true);
        check(op, Constant.build("Ringo"), Constant.build("Ringo"), true);
        check(op, Constant.build("Ringo"), Constant.build("George"), false);
        check(op, beatles, Constant.build("Yoko"), false);
        check(op, Constant.NULL, Constant.build(3), false);
        check(op, Constant.build("4"), Constant.NULL, false);
        check(op, Constant.build("4"), Constant.build(4), true);
        check(op, Constant.build(3), Constant.build("4"), false);
        check(op, Constant.build("3"), Constant.build(new Date(44444)), false);
        check(op, Constant.build("3"), Constant.build(new Date(3)), true);
        check(op, Constant.NULL, Constant.NULL, true);
        check(op, Constant.build(""), Constant.build(3), false);
        check(op, Constant.build(3), Constant.build(""), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateThursdayLosAngeles, "LA"), true);
        check(op, Constant.buildDate(lateThursdayLosAngelesAsString), Constant.build(lateThursdayLosAngelesAsString, "LA"), true);
        check(op, Constant.buildDate(lateThursdayLosAngelesAsString), Constant.buildDate(lateThursdayLosAngelesAsISO), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateThursdayLosAngelesAsString, "LA"), true);
        check(op, Constant.build(lateThursdayLosAngelesAsString, "LA"), Constant.build(lateThursdayLosAngeles, "LA"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(earlyFridaySingapore, "Early Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(earlyFridaySingaporeAsString, "Early Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingapore, "Late Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngelesAsString, "LA"), Constant.build(lateFridaySingapore, "Late Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngelesAsISO, "LA"), Constant.build(lateFridaySingapore, "Late Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingaporeAsString, "Late Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdayLocal), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdayLA), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fridaySingapore), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fridayLocal), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(eveningLocal), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(eveningLA), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(eveningSingapore), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(morningLocal), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(morningLA), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(morningSingapore), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fifteenthLocal), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fifteenthLA), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fifteenthSingapore), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(sixteenthLocal), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(sixteenthLA), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(sixteenthSingapore), true);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2020Local), true);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2020LA), true);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2020Singapore), false);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2021Local), false);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2021LA), false);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2021Singapore), true);
    }

    public final void testNEQ() {
        RelationOp op = RelationOp.NOT_EQUALS;

        check(op, Constant.build(3), Constant.build(3), false);
        check(op, Constant.build("George"), beatles, false);
        check(op, beatles, Constant.build("Ringo"), false);
        check(op, Constant.build("Ringo"), Constant.build("Ringo"), false);
        check(op, Constant.build("Ringo"), Constant.build("George"), true);
        check(op, Constant.build("Paul"), justPaulRegexp, false);
        check(op, beatles, Constant.build("Yoko"), true);
        check(op, Constant.NULL, Constant.build(3), true);
        check(op, Constant.build("4"), Constant.NULL, true);
        check(op, Constant.build("4"), Constant.build(4), false);
        check(op, Constant.build(3), Constant.build("4"), true);
        check(op, Constant.build("3"), Constant.build(new Date(44444)), true);
        check(op, Constant.build("3"), Constant.build(new Date(3)), false);
        check(op, Constant.build(""), Constant.build(3), true);
        check(op, Constant.build(3), Constant.build(""), true);
        check(op, Constant.NULL, Constant.NULL, false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateThursdayLosAngeles, "LA"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(earlyFridaySingapore, "Early Singapore"), false);
        check(op, Constant.buildDate(lateThursdayLosAngelesAsString), Constant.build(lateThursdayLosAngelesAsString, "LA"), false);
        check(op, Constant.build(lateThursdayLosAngelesAsString, "LA"), Constant.buildDate(lateThursdayLosAngelesAsString), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(earlyFridaySingaporeAsString, "Early Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingapore, "Late Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingaporeAsString, "Late Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdayLocal), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdayLA), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fridaySingapore), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fridayLocal), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(eveningLocal), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(eveningLA), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(eveningSingapore), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(morningLocal), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(morningLA), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(morningSingapore), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fifteenthLocal), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fifteenthLA), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fifteenthSingapore), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(sixteenthLocal), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(sixteenthLA), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(sixteenthSingapore), false);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2020Local), false);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2020LA), false);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2020Singapore), true);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2021Local), true);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2021LA), true);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(year2021Singapore), false);
    }

    public final void testLT() {
        RelationOp op = RelationOp.LESS_THAN;

        check(op, Constant.NULL, Constant.build(4), false);
        check(op, Constant.NULL, Constant.NULL, false);
        check(op, Constant.build(4), Constant.NULL, false);
        check(op, Constant.build(3), Constant.build(4), true);
        check(op, Constant.build("-3"), Constant.build("4"), true);
        check(op, Constant.build("4"), Constant.build(3), false);
        check(op, Constant.build(""), Constant.build(3), false);
        check(op, Constant.build(3), Constant.build(""), false);
        check(op, Constant.build(""), Constant.build(""), false);
        check(op, Constant.build(3), Constant.build("3"), false);
        check(op, Constant.build(new Date(44444)), Constant.build(new Date(55555)), true);
        check(op, Constant.build(new Date(44444)), Constant.build(new Date(44444)), false);
        check(op, Constant.build("3"), Constant.build(new Date(44444)), true);
        check(op, Constant.build(""), Constant.build(3), false);
        check(op, Constant.build(3), Constant.build(""), false);
        check(op, Constant.build(""), Constant.build(""), false);
        check(op, Constant.NULL, Constant.NULL, false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(earlyFridaySingapore, "Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(earlyFridaySingaporeAsString, "Singapore"), false);
        check(op, Constant.build(earlyFridaySingaporeAsString, "Singapore"), Constant.build(lateThursdayLosAngeles, "LA"), false);
        check(op, Constant.build(earlyFridaySingaporeAsString, "Singapore"), Constant.build(lateFridaySingapore, "Singapore"), true);
        check(op, Constant.build(lateFridaySingapore, "Singapore"), Constant.build(earlyFridaySingaporeAsString, "Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingapore, "Singapore"), true);
        check(op, Constant.build(lateFridaySingapore, "Singapore"), Constant.build(lateThursdayLosAngeles, "LA"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingaporeAsString), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingaporeAsString, "Singapore"), true);
        check(op, Constant.build(lateFridaySingaporeAsString, "Singapore"), Constant.build(lateThursdayLosAngeles, "LA"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fridayLocal, "Local"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fridayLA, "LA"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fridaySingapore, "Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdayLocal, "Local"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdaySingapore, "Singapore"), false);
        check(op, Constant.build(newYearsEveLA, "LA"), Constant.build(newYears2099), true);
                  
        checkIncompatible(op, Constant.build(new Date(44444)), Constant.build("3"));
        checkIncompatible(op, Constant.build(3), beatles);
        checkIncompatible(op, beatles, Constant.build("Paul"));
    }

    public final void testLTE() {
        RelationOp op = RelationOp.LESS_THAN_EQUALS;

        check(op, Constant.NULL, Constant.build(4), false);
        check(op, Constant.build(4), Constant.NULL, false);
        check(op, Constant.build(3), Constant.build(4), true);
        check(op, Constant.build("3"), Constant.build("4"), true);
        check(op, Constant.build("4"), Constant.build(3), false);
        check(op, Constant.build(""), Constant.build(3), false);
        check(op, Constant.build(3), Constant.build(""), false);
        check(op, Constant.build(""), Constant.build(""), true);
        check(op, Constant.build(3), Constant.build("3"), true);
        check(op, Constant.build(new Date(44444)), Constant.build(new Date(55555)), true);
        check(op, Constant.build(new Date(44444)), Constant.build(new Date(44444)), true);
        check(op, Constant.build("3"), Constant.build(new Date(44444)), true);
        check(op, Constant.build(""), Constant.build(3), false);
        check(op, Constant.build(3), Constant.build(""), false);
        check(op, Constant.build(""), Constant.build(""), true);
        check(op, Constant.NULL, Constant.NULL, true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(earlyFridaySingapore, "Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(earlyFridaySingaporeAsString, "Singapore"), true);
        check(op, Constant.build(earlyFridaySingaporeAsString, "Singapore"), Constant.build(lateThursdayLosAngeles, "LA"), true);
        check(op, Constant.buildDate(earlyFridaySingaporeAsString), Constant.build(earlyFridaySingaporeAsString, "Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingaporeAsString, "Singapore"), true);
        check(op, Constant.build(lateFridaySingaporeAsString, "Singapore"), Constant.build(lateThursdayLosAngeles, "LA"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingapore, "Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fridaySingapore, "Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdayLocal, "Local"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdaySingapore, "Singapore"), false);

        checkIncompatible(op, Constant.build(new Date(44444)), Constant.build("3"));
        checkIncompatible(op, Constant.build(3), beatles);
        checkIncompatible(op, beatles, Constant.build("John"));
    }

    public final void testGT() {
        RelationOp op = RelationOp.GREATER_THAN;

        check(op, Constant.NULL, Constant.build(4), false);
        check(op, Constant.build(4), Constant.NULL, false);
        check(op, Constant.build(3), Constant.build(4), false);
        check(op, Constant.build("3"), Constant.build("4"), false);
        check(op, Constant.build("4"), Constant.build(3), true);
        check(op, Constant.build(""), Constant.build(3), false);
        check(op, Constant.build(3), Constant.build(""), false);
        check(op, Constant.build(""), Constant.build(""), false);
        check(op, Constant.build(3), Constant.build("3"), false);
        check(op, Constant.build(new Date(44444)), Constant.build(new Date(55555)), false);
        check(op, Constant.build(new Date(44444)), Constant.build(new Date(44444)), false);
        check(op, Constant.build("3"), Constant.build(new Date(44444)), false);
        check(op, Constant.build(""), Constant.build(3), false);
        check(op, Constant.build(3), Constant.build(""), false);
        check(op, Constant.build(""), Constant.build(""), false);
        check(op, Constant.NULL, Constant.NULL, false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(earlyFridaySingapore, "Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(earlyFridaySingaporeAsString, "Singapore"), false);
        check(op, Constant.buildDate(earlyFridaySingaporeAsString), Constant.build(earlyFridaySingaporeAsString, "Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingapore, "Singapore"), false);
        check(op, Constant.build(lateFridaySingapore, "Singapore"), Constant.build(lateThursdayLosAngeles, "LA"), true);
        check(op, Constant.build(lateFridaySingapore, "Singapore"), Constant.build(lateThursdayLosAngelesAsString, "LA"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fridaySingapore, "Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdayLocal, "Local"), false);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdaySingapore, "Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingaporeAsString, "Singapore"), false);
        check(op, Constant.build(lateFridaySingaporeAsString, "Singapore"), Constant.build(lateThursdayLosAngeles, "LA"), true);

        checkIncompatible(op, Constant.build(new Date(44444)), Constant.build("3"));
        checkIncompatible(op, Constant.build(3), beatles);
        checkIncompatible(op, beatles, Constant.build("Ringo"));
    }

    public final void testGTE() {
        RelationOp op = RelationOp.GREATER_THAN_EQUALS;

        check(op, Constant.NULL, Constant.build(4), false);
        check(op, Constant.build(4), Constant.NULL, false);
        check(op, Constant.build(3), Constant.build(4), false);
        check(op, Constant.build("3"), Constant.build("4"), false);
        check(op, Constant.build("4"), Constant.build(3), true);
        check(op, Constant.build(""), Constant.build(3), false);
        check(op, Constant.build(3), Constant.build(""), false);
        check(op, Constant.build(""), Constant.build(""), true);
        check(op, Constant.build(3), Constant.build("3"), true);
        check(op, Constant.build(new Date(44444)), Constant.build(new Date(55555)), false);
        check(op, Constant.build(new Date(44444)), Constant.build(new Date(44444)), true);
        check(op, Constant.build("3"), Constant.build(new Date(44444)), false);
        check(op, Constant.build(""), Constant.build(3), false);
        check(op, Constant.build(3), Constant.build(""), false);
        check(op, Constant.build(""), Constant.build(""), true);
        check(op, Constant.NULL, Constant.NULL, true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(earlyFridaySingapore, "Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngelesAsString, "LA"), Constant.build(earlyFridaySingapore, "Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingapore, "Singapore"), false);
        check(op, Constant.build(lateThursdayLosAngelesAsString, "LA"), Constant.build(lateFridaySingapore, "Singapore"), false);
        check(op, Constant.build(lateFridaySingapore, "Singapore"), Constant.build(lateThursdayLosAngeles, "LA"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(fridaySingapore, "Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdayLocal, "Local"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(thursdaySingapore, "Singapore"), true);
        check(op, Constant.build(lateThursdayLosAngeles, "LA"), Constant.build(lateFridaySingaporeAsString, "Singapore"), false);
        check(op, Constant.build(lateFridaySingaporeAsString, "Singapore"), Constant.build(lateThursdayLosAngeles, "LA"), true);

        checkIncompatible(op, Constant.build(new Date(44444)), Constant.build("3"));
        checkIncompatible(op, Constant.build(3), beatles);
        checkIncompatible(op, beatles, Constant.build("George"));
    }

    public final void testIncludes() {
        RelationOp op = RelationOp.INCLUDES;

        check(op, livingBeatles, beatles, false);
        check(op, livingBeatles, Constant.build("Paul"), true);
        check(op, livingBeatles, Constant.build("paul"), true);
        check(op, beatles, livingBeatles, true);
        check(op, beatles, beatles, true);
        check(op, livingBeatles, Constant.build("Paul"), true);
        check(op, smallPrimes, Constant.build(3), true);
        check(op, smallPrimes, Constant.build(4), false);
        check(op, beatles, emptyMultival, true);
        check(op, smallPrimes, emptyMultival, true);

        check(op, beatles, beatlesRegexp, true);
        check(op, alphabeatles, beatlesRegexp, true);
        check(op, justPaul, beatlesRegexp, false);
        check(op, justPaul, justPaulRegexp, true);
        check(op, beatles, justPaulRegexp, true);

        check(op, Constant.build("paul"), Constant.build("Paul"), true);
        check(op, Constant.build("paul"), justPaul, true);

        check(op, Constant.build(3), Constant.build(4), false);
        check(op, Constant.build(3), Constant.build(3), true);
        check(op, Constant.build(3), Constant.build(Multivalue.create(new Long[] {3l}), "Three"), true);
        
        check(op, Constant.NULL, Constant.NULL, true);
        check(op, Constant.NULL, beatles, false);
        check(op, beatles, Constant.NULL, true);
    }

    public final void testUnorderedEquals() {
        RelationOp op = RelationOp.EQUALS_UNORDERED;

        check(op, beatles, beatles, true);
        check(op, beatles, beatlesRegexp, false);
        check(op, beatles, alphabeatles, true);
        check(op, alphabeatles, beatles, true);
        check(op, livingBeatles, beatles, false);
        check(op, Constant.build("Paul"), beatles, false);
        check(op, Constant.build("Paul"), justPaul, true);
        check(op, justPaul, Constant.build("Paul"), true);
        check(op, Constant.build("paul"), Constant.build("Paul"), true);
        
        check(op, Constant.NULL, Constant.NULL, true);
        check(op, Constant.NULL, beatles, false);
        check(op, beatles, Constant.NULL, false);

        check(op, Constant.build(3), Constant.build(4), false);
        check(op, Constant.build(3), Constant.build(3), true);
    }

    private Constant smallApple = Constant.build("apple");
    private Constant bigApple = Constant.build("APPLE");
    
    public final void testStringRelations() {
        check(RelationOp.STRING_EQUALS, smallApple, smallApple, true);
        check(RelationOp.STRING_EQUALS, smallApple, bigApple, false);
        check(RelationOp.STRING_NOT_EQUALS, smallApple, bigApple, true);
        check(RelationOp.STRING_NOT_EQUALS_CASE_INSENSITIVE, smallApple, bigApple, false);
        check(RelationOp.STRING_EQUALS_CASE_INSENSITIVE, smallApple, bigApple, true);
        check(RelationOp.STRING_LT, smallApple, bigApple, false);
        check(RelationOp.STRING_LT, bigApple, smallApple, true);
        check(RelationOp.STRING_LT, smallApple, smallApple, false);
        check(RelationOp.STRING_LT_CASE_INSENSITIVE, smallApple, bigApple, false);
        check(RelationOp.STRING_LTE, smallApple, smallApple, true);
        check(RelationOp.STRING_LTE_CASE_INSENSITIVE, smallApple, smallApple, true);
        check(RelationOp.STRING_GT, smallApple, bigApple, true);
        check(RelationOp.STRING_GT, bigApple, smallApple, false);
        check(RelationOp.STRING_GT, bigApple, bigApple, false);
        check(RelationOp.STRING_GT_CASE_INSENSITIVE, smallApple, bigApple, false);
        check(RelationOp.STRING_GTE, smallApple, smallApple, true);
        check(RelationOp.STRING_GTE_CASE_INSENSITIVE, smallApple, smallApple, true);
    }

    private ZonedDateTime fifteenSecondsAgo = ZonedDateTime.now().minusSeconds(15);
    private ZonedDateTime sixMinutesAgo = ZonedDateTime.now().minusMinutes(6);
    private ZonedDateTime threeHoursAgo = ZonedDateTime.now().minusHours(3);
    private ZonedDateTime fiveDaysAgo = ZonedDateTime.now().minusDays(5);
    private ZonedDateTime twoWeeksAgo = ZonedDateTime.now().minusWeeks(2);
    private ZonedDateTime threeMonthsAgo = ZonedDateTime.now().minusMonths(3);
    private ZonedDateTime twoYearsAgo = ZonedDateTime.now().minusYears(2);

    private Constant tenSeconds = Constant.build("10 seconds");
    private Constant twentySeconds = Constant.build("20 seconds");
    private Constant fiveMinutes = Constant.build("5 minutes");
    private Constant sevenMinutes = Constant.build("7 minutes");
    private Constant twoHours = Constant.build("2 hours");
    private Constant fourHours = Constant.build("4 hours");
    private Constant fourDays = Constant.build("4 days");
    private Constant sixDays = Constant.build("6 days");
    private Constant oneWeek = Constant.build("1 week");
    private Constant threeWeeks = Constant.build("3 weeks");
    private Constant twoMonths = Constant.build("2 months");
    private Constant fourMonths = Constant.build("4 months");
    private Constant oneYear = Constant.build("1 year");
    private Constant threeYears = Constant.build("3 years");

    private Constant asZDT(ZonedDateTime zdt) {
        return Constant.build(zdt, "recently");
    }
    
    private Constant asDate(ZonedDateTime zdt) {
        return Constant.build(Date.from(zdt.toInstant()));
    }

    private Constant asString(ZonedDateTime zdt) {
        return Constant.build(zdt.toString());
    }
    
    private Constant asTimeStampString(ZonedDateTime zdt) {
        return Constant.build(zdt.toInstant().toEpochMilli());
    }

    private final void withinLastHelper(ZonedDateTime inThePast, Constant inRange, Constant outOfRange) {
        check(RelationOp.WITHIN_LAST, asZDT(inThePast), inRange, true);
        check(RelationOp.WITHIN_LAST, asDate(inThePast), inRange, true);
        check(RelationOp.WITHIN_LAST, asString(inThePast), inRange, true);
        check(RelationOp.WITHIN_LAST, asTimeStampString(inThePast), inRange, true);
        
        check(RelationOp.WITHIN_LAST, asZDT(inThePast), outOfRange, false);
        check(RelationOp.WITHIN_LAST, asDate(inThePast), outOfRange, false);
        check(RelationOp.WITHIN_LAST, asString(inThePast), outOfRange, false);
        check(RelationOp.WITHIN_LAST, asTimeStampString(inThePast), outOfRange, false);
    }
    
    public final void testWithinLast() {
        withinLastHelper(fifteenSecondsAgo, twentySeconds, tenSeconds);
        withinLastHelper(sixMinutesAgo, sevenMinutes, fiveMinutes);
        withinLastHelper(threeHoursAgo, fourHours, twoHours);
        withinLastHelper(fiveDaysAgo, sixDays, fourDays);
        withinLastHelper(twoWeeksAgo, threeWeeks, oneWeek);
        withinLastHelper(threeMonthsAgo, fourMonths, twoMonths);
        withinLastHelper(twoYearsAgo, threeYears, oneYear);
    }
}




