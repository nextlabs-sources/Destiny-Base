/*
 * Created on Oct 26, 2020
 *
 * All sources, binaries and HTML pages (C) copyright 2020 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.framework.expressions;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

/**
 * This class is used to express PQL dates and times with or without
 * time-zones.
 *
 * The classes in java.time let us represent some of what we want, but not everything.
 * Date and time can be handled with ZonedDateTime or LocalDateTime, for example, but
 * there is no easy way to express the concept of "any Monday, Pacific Standard Time".
 *
 * Each date/time thing is stored as a long, which has a different interpretation dependin
 * on whether it represents a time of day, year, etc
 */
public class PartialDateTime {
    public enum DateType { TIME_OF_DAY, DAY_OF_WEEK, DAY_IN_MONTH, DAY_OF_WEEK_IN_MONTH, YEAR, DATE_AND_TIME };
    
    private DateType dateType;
    private long value;
    private ZoneId timeZone;

    private PartialDateTime(long value, ZoneId timeZone, DateType dateType) {
        this.value = value;
        this.timeZone = timeZone;
        this.dateType = dateType;
    }

    /**
     * These methods take strings and parse them, converting them to
     * an integer (which represents different things depending on
     * whether we are talking about day of week, year, etc) and,
     * possibly, a time zone.
     *
     * java.time.format provides some very flexible tools for parsing
     * strings, but there is a problem The end result of parsing must
     * be a LocalDate, LocalTime, LocalDateTime, or ZonedDateTime.
     * "Wednesday PDT" doesn't let you generate that.
     *
     * There is a way around this, however. If you give the parser
     * enough additional information to determine an exact date/time
     * then you can use it to parse this. For example:
     *
     * new DateTimeFormatterBuilder().
     *   appendPattern("EEEE").   // day of week
     *   parseDefaulting("ChronoField.ALIGNED_WEEK_OF_YEAR", 1).
     *   parseDefaulting("ChronoField.YEAR, 2020).
     *   toFormatter();
     *
     * This will successfully parse "Wednesday" to a LocalDate,
     * because we've told the formatter "Assume this is the first week
     * in the year 2020" and that's enough to produce a date.
     *
     * This seems like a lot of work unless you actually want a
     * LocalDate at the end of it.
     */
    
    /**
     * Converts a string representing a day of the week (possibly with
     * time-zone) The day of week can be represented in PQL as a
     * number or a String. Check for both.
     *
     */
    public static PartialDateTime fromWeekday(String weekday) {
        long dayOfWeek = 0L;
        ZoneId timeZone = null;
        
        String[] dayAndZone = weekday.split(" ");

        try {
            dayOfWeek = Long.parseLong(dayAndZone[0]);
        } catch (NumberFormatException nfe) {
            // Maybe it's a string
            try {
                // DayOfWeek class wants MONDAY, TUESDAY, etc.
                DayOfWeek dow = DayOfWeek.valueOf(dayAndZone[0].toUpperCase());
                dayOfWeek = dow.getValue();
            } catch (IllegalArgumentException iae) {
                throw new RuntimeException("Invalid weekday specified: " + dayAndZone[0].toUpperCase());
            }
        }

        if (dayAndZone.length == 2) {
            try {
                timeZone = ZoneId.of(dayAndZone[1]);
            } catch (DateTimeException e) {
                throw new RuntimeException("Invalid time-zone specified: " + dayAndZone[1]);
            }
        }

        return new PartialDateTime(dayOfWeek, timeZone, DateType.DAY_OF_WEEK);
    }

    public static PartialDateTime fromWeekday(int dayOfWeek, ZoneId timeZone) {
        return new PartialDateTime((long)dayOfWeek, timeZone, DateType.DAY_OF_WEEK);
    }

    private static final LocalTime NOON = LocalTime.parse("12:00");

    private static final DateTimeFormatter LEGACY_TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm:ss");
    
    /**
     * Converts a string representing a time of day (which may be 12
     * hour with am/pm or 24 hour), but not date, possibly with
     * time-zone.
     */
    public static PartialDateTime fromTimeOfDay(String timeOfDay) {
        String[] timeAndZone = timeOfDay.split(" ");
        ZoneId timeZone = null;
        int timeZoneIndex = 1;

        LocalTime localTime;
        
        try {
            localTime = LocalTime.parse(timeAndZone[0]);
        } catch (DateTimeParseException dtpe) {
            localTime = LocalTime.parse(timeAndZone[0], LEGACY_TIME_FORMATTER);
        }
        
        if (timeAndZone.length > 1 && (timeAndZone[1].toLowerCase().equals("am") ||
                                       timeAndZone[1].toLowerCase().equals("pm"))) {
            // LocalTime assumes a 24 hour clock. Handle am/pm here

            // If the time is between 12:00 am and 12:59:59 am then subtract 12 hours
            // If it's between 1:00 pm and 11:59 pm then add 12 hours
            // Leave everything else alone
            if (timeAndZone[1].toLowerCase().equals("am") && localTime.getHour() == 12) {
                localTime = localTime.minusHours(12);
            } else if (timeAndZone[1].toLowerCase().equals("pm") && localTime.getHour() != 12) {
                localTime = localTime.plusHours(12);
            }
            
            timeZoneIndex = 2;
        }
        
        long msSinceMidnight = localTime.toSecondOfDay() * 1000;
        
        if (timeAndZone.length > timeZoneIndex) {
            try {
                timeZone = ZoneId.of(timeAndZone[timeZoneIndex]);
            } catch (DateTimeException e) {
                throw new RuntimeException("Invalid time-zone specified: " + timeAndZone[timeZoneIndex]);
            }
        }
        
        return new PartialDateTime(msSinceMidnight, timeZone, DateType.TIME_OF_DAY);
    }

    public static PartialDateTime fromTimeOfDay(Long msSinceMidnight, ZoneId timeZone) {
        return new PartialDateTime(msSinceMidnight, timeZone, DateType.TIME_OF_DAY);
    }
    
    /**
     * Converts a string representing a day in a month (i.e. the "15" for
     * the 15th), possibly with time-zone.
     */
    public static PartialDateTime fromDate(String dateWithinMonth) {
        String[] dateAndZone = dateWithinMonth.split(" ");
        ZoneId timeZone = null;

        long date = 0;
        
        try {
            date = Long.parseLong(dateAndZone[0]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid date specified: " + dateAndZone[0]);
        }

        if (dateAndZone.length == 2) {
            try {
                timeZone = ZoneId.of(dateAndZone[1]);
            } catch (DateTimeException e) {
                throw new RuntimeException("Invalid time-zone specified: " + dateAndZone[1]);
            }
        }

        return new PartialDateTime(date, timeZone, DateType.DAY_IN_MONTH);
    }

    public static PartialDateTime fromDate(int dayWithinMonth, ZoneId timeZone) {
        return new PartialDateTime((long)dayWithinMonth, timeZone, DateType.DAY_IN_MONTH);
    }

    /**
     * Converts a string representing the day-of-week-in-month (i.e. this day is in the
     * second week of the month), possibly with time-zone.
     */
    public static PartialDateTime fromDOWIM(String dowim) {
        String[] dowimAndZone = dowim.split(" ");
        ZoneId timeZone = null;
        
        long dowimValue;

        try {
            dowimValue = Long.parseLong(dowimAndZone[0]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid dowim specified: " + dowimAndZone[0]);
        }

        if (dowimAndZone.length == 2) {
            try {
                timeZone = ZoneId.of(dowimAndZone[1]);
            } catch (DateTimeException e) {
                throw new RuntimeException("Invalid time-zone specified: " + dowimAndZone[1]);
            }
        }

        return new PartialDateTime(dowimValue, timeZone, DateType.DAY_OF_WEEK_IN_MONTH);
    }
    
    public static PartialDateTime fromDOWIM(int dowim, ZoneId timeZone) {
        return new PartialDateTime((long)dowim, timeZone, DateType.DAY_OF_WEEK_IN_MONTH);
    }
    
    /**
     * Takes a string representing a year, possibly with time-zone
     */
    public static PartialDateTime fromYear(String year) {
        String[] yearAndZone = year.split(" ");
        ZoneId timeZone = null;

        long yearValue = 0;

        try {
            yearValue = Long.parseLong(yearAndZone[0]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid year specified: " + yearAndZone[0]);
        }

        if (yearAndZone.length == 2) {
            try {
                timeZone = ZoneId.of(yearAndZone[1]);
            } catch (DateTimeException e) {
                throw new RuntimeException("Invalid time-zone specified: " + yearAndZone[1]);
            }
        }

        return new PartialDateTime(yearValue, timeZone, DateType.YEAR);
    }

    public static PartialDateTime fromYear(int year, ZoneId timeZone) {
        return new PartialDateTime((long)year, timeZone, DateType.YEAR);
    }

    public static PartialDateTime fromDateTime(LocalDateTime ldt) {
        return new PartialDateTime(ldt.toEpochSecond(ZoneOffset.UTC), null, DateType.DATE_AND_TIME);
    }
    
    public static int compareTo(ZonedDateTime zdt, PartialDateTime dv) {
        // Move the zoned time to the timezone of the partially qualified time,
        // then extract and compare the relevant fields
        if (dv.timeZone != null) {
            zdt = zdt.withZoneSameInstant(dv.timeZone);
        }

        switch(dv.dateType) {
            case TIME_OF_DAY:
                return zdt.toLocalTime().toSecondOfDay() * 1000 - (int)dv.value;
            case DAY_OF_WEEK:
                return zdt.getDayOfWeek().getValue() - (int)dv.value;
            case DAY_IN_MONTH:
                return zdt.getDayOfMonth() - (int)dv.value;
            case DAY_OF_WEEK_IN_MONTH:
                return zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH) - (int)dv.value;
            case YEAR:
                return zdt.getYear() - (int)dv.value;
            case DATE_AND_TIME:
                return (int)Long.signum(zdt.toLocalDateTime().toEpochSecond(ZoneOffset.UTC) - dv.value);
            default:
                throw new RuntimeException("Invalid date type: " + dv.dateType);
        }
    }

    public DateType getDateType() {
        return dateType;
    }
    
    public long getValue() {
        return value;
    }

    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("MMM d, yyyy hh:mm:ss a");
    
    public String getValueAsString() {
        switch (dateType) {
            case DATE_AND_TIME:
                LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC);
                return localDateTime.format(LOCAL_DATE_TIME_FORMATTER);
            case TIME_OF_DAY:
                LocalTime localTime = LocalTime.ofSecondOfDay(value / 1000);
                return localTime.toString();
            case DAY_OF_WEEK:
                return DayOfWeek.of((int)value).toString();
            case DAY_IN_MONTH:
            case DAY_OF_WEEK_IN_MONTH:
            case YEAR:
                return ((Long)value).toString();
            default:
                throw new RuntimeException("Invalid date type: " + dateType);
        }
    }

    public ZoneId getZoneId() {
        return timeZone;
    }
    
    @Override
    public String toString() {
        String ret = getValueAsString();

        if (timeZone != null) {
            ret += " " + timeZone.toString();
        }

        return ret;
    }
}






