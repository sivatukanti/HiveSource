// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.io.Serializable;

public abstract class DateTimeFieldType implements Serializable
{
    private static final long serialVersionUID = -42615285973990L;
    static final byte ERA = 1;
    static final byte YEAR_OF_ERA = 2;
    static final byte CENTURY_OF_ERA = 3;
    static final byte YEAR_OF_CENTURY = 4;
    static final byte YEAR = 5;
    static final byte DAY_OF_YEAR = 6;
    static final byte MONTH_OF_YEAR = 7;
    static final byte DAY_OF_MONTH = 8;
    static final byte WEEKYEAR_OF_CENTURY = 9;
    static final byte WEEKYEAR = 10;
    static final byte WEEK_OF_WEEKYEAR = 11;
    static final byte DAY_OF_WEEK = 12;
    static final byte HALFDAY_OF_DAY = 13;
    static final byte HOUR_OF_HALFDAY = 14;
    static final byte CLOCKHOUR_OF_HALFDAY = 15;
    static final byte CLOCKHOUR_OF_DAY = 16;
    static final byte HOUR_OF_DAY = 17;
    static final byte MINUTE_OF_DAY = 18;
    static final byte MINUTE_OF_HOUR = 19;
    static final byte SECOND_OF_DAY = 20;
    static final byte SECOND_OF_MINUTE = 21;
    static final byte MILLIS_OF_DAY = 22;
    static final byte MILLIS_OF_SECOND = 23;
    private static final DateTimeFieldType ERA_TYPE;
    private static final DateTimeFieldType YEAR_OF_ERA_TYPE;
    private static final DateTimeFieldType CENTURY_OF_ERA_TYPE;
    private static final DateTimeFieldType YEAR_OF_CENTURY_TYPE;
    private static final DateTimeFieldType YEAR_TYPE;
    private static final DateTimeFieldType DAY_OF_YEAR_TYPE;
    private static final DateTimeFieldType MONTH_OF_YEAR_TYPE;
    private static final DateTimeFieldType DAY_OF_MONTH_TYPE;
    private static final DateTimeFieldType WEEKYEAR_OF_CENTURY_TYPE;
    private static final DateTimeFieldType WEEKYEAR_TYPE;
    private static final DateTimeFieldType WEEK_OF_WEEKYEAR_TYPE;
    private static final DateTimeFieldType DAY_OF_WEEK_TYPE;
    private static final DateTimeFieldType HALFDAY_OF_DAY_TYPE;
    private static final DateTimeFieldType HOUR_OF_HALFDAY_TYPE;
    private static final DateTimeFieldType CLOCKHOUR_OF_HALFDAY_TYPE;
    private static final DateTimeFieldType CLOCKHOUR_OF_DAY_TYPE;
    private static final DateTimeFieldType HOUR_OF_DAY_TYPE;
    private static final DateTimeFieldType MINUTE_OF_DAY_TYPE;
    private static final DateTimeFieldType MINUTE_OF_HOUR_TYPE;
    private static final DateTimeFieldType SECOND_OF_DAY_TYPE;
    private static final DateTimeFieldType SECOND_OF_MINUTE_TYPE;
    private static final DateTimeFieldType MILLIS_OF_DAY_TYPE;
    private static final DateTimeFieldType MILLIS_OF_SECOND_TYPE;
    private final String iName;
    
    protected DateTimeFieldType(final String iName) {
        this.iName = iName;
    }
    
    public static DateTimeFieldType millisOfSecond() {
        return DateTimeFieldType.MILLIS_OF_SECOND_TYPE;
    }
    
    public static DateTimeFieldType millisOfDay() {
        return DateTimeFieldType.MILLIS_OF_DAY_TYPE;
    }
    
    public static DateTimeFieldType secondOfMinute() {
        return DateTimeFieldType.SECOND_OF_MINUTE_TYPE;
    }
    
    public static DateTimeFieldType secondOfDay() {
        return DateTimeFieldType.SECOND_OF_DAY_TYPE;
    }
    
    public static DateTimeFieldType minuteOfHour() {
        return DateTimeFieldType.MINUTE_OF_HOUR_TYPE;
    }
    
    public static DateTimeFieldType minuteOfDay() {
        return DateTimeFieldType.MINUTE_OF_DAY_TYPE;
    }
    
    public static DateTimeFieldType hourOfDay() {
        return DateTimeFieldType.HOUR_OF_DAY_TYPE;
    }
    
    public static DateTimeFieldType clockhourOfDay() {
        return DateTimeFieldType.CLOCKHOUR_OF_DAY_TYPE;
    }
    
    public static DateTimeFieldType hourOfHalfday() {
        return DateTimeFieldType.HOUR_OF_HALFDAY_TYPE;
    }
    
    public static DateTimeFieldType clockhourOfHalfday() {
        return DateTimeFieldType.CLOCKHOUR_OF_HALFDAY_TYPE;
    }
    
    public static DateTimeFieldType halfdayOfDay() {
        return DateTimeFieldType.HALFDAY_OF_DAY_TYPE;
    }
    
    public static DateTimeFieldType dayOfWeek() {
        return DateTimeFieldType.DAY_OF_WEEK_TYPE;
    }
    
    public static DateTimeFieldType dayOfMonth() {
        return DateTimeFieldType.DAY_OF_MONTH_TYPE;
    }
    
    public static DateTimeFieldType dayOfYear() {
        return DateTimeFieldType.DAY_OF_YEAR_TYPE;
    }
    
    public static DateTimeFieldType weekOfWeekyear() {
        return DateTimeFieldType.WEEK_OF_WEEKYEAR_TYPE;
    }
    
    public static DateTimeFieldType weekyear() {
        return DateTimeFieldType.WEEKYEAR_TYPE;
    }
    
    public static DateTimeFieldType weekyearOfCentury() {
        return DateTimeFieldType.WEEKYEAR_OF_CENTURY_TYPE;
    }
    
    public static DateTimeFieldType monthOfYear() {
        return DateTimeFieldType.MONTH_OF_YEAR_TYPE;
    }
    
    public static DateTimeFieldType year() {
        return DateTimeFieldType.YEAR_TYPE;
    }
    
    public static DateTimeFieldType yearOfEra() {
        return DateTimeFieldType.YEAR_OF_ERA_TYPE;
    }
    
    public static DateTimeFieldType yearOfCentury() {
        return DateTimeFieldType.YEAR_OF_CENTURY_TYPE;
    }
    
    public static DateTimeFieldType centuryOfEra() {
        return DateTimeFieldType.CENTURY_OF_ERA_TYPE;
    }
    
    public static DateTimeFieldType era() {
        return DateTimeFieldType.ERA_TYPE;
    }
    
    public String getName() {
        return this.iName;
    }
    
    public abstract DurationFieldType getDurationType();
    
    public abstract DurationFieldType getRangeDurationType();
    
    public abstract DateTimeField getField(final Chronology p0);
    
    public boolean isSupported(final Chronology chronology) {
        return this.getField(chronology).isSupported();
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    static {
        ERA_TYPE = new StandardDateTimeFieldType("era", (byte)1, DurationFieldType.eras(), null);
        YEAR_OF_ERA_TYPE = new StandardDateTimeFieldType("yearOfEra", (byte)2, DurationFieldType.years(), DurationFieldType.eras());
        CENTURY_OF_ERA_TYPE = new StandardDateTimeFieldType("centuryOfEra", (byte)3, DurationFieldType.centuries(), DurationFieldType.eras());
        YEAR_OF_CENTURY_TYPE = new StandardDateTimeFieldType("yearOfCentury", (byte)4, DurationFieldType.years(), DurationFieldType.centuries());
        YEAR_TYPE = new StandardDateTimeFieldType("year", (byte)5, DurationFieldType.years(), null);
        DAY_OF_YEAR_TYPE = new StandardDateTimeFieldType("dayOfYear", (byte)6, DurationFieldType.days(), DurationFieldType.years());
        MONTH_OF_YEAR_TYPE = new StandardDateTimeFieldType("monthOfYear", (byte)7, DurationFieldType.months(), DurationFieldType.years());
        DAY_OF_MONTH_TYPE = new StandardDateTimeFieldType("dayOfMonth", (byte)8, DurationFieldType.days(), DurationFieldType.months());
        WEEKYEAR_OF_CENTURY_TYPE = new StandardDateTimeFieldType("weekyearOfCentury", (byte)9, DurationFieldType.weekyears(), DurationFieldType.centuries());
        WEEKYEAR_TYPE = new StandardDateTimeFieldType("weekyear", (byte)10, DurationFieldType.weekyears(), null);
        WEEK_OF_WEEKYEAR_TYPE = new StandardDateTimeFieldType("weekOfWeekyear", (byte)11, DurationFieldType.weeks(), DurationFieldType.weekyears());
        DAY_OF_WEEK_TYPE = new StandardDateTimeFieldType("dayOfWeek", (byte)12, DurationFieldType.days(), DurationFieldType.weeks());
        HALFDAY_OF_DAY_TYPE = new StandardDateTimeFieldType("halfdayOfDay", (byte)13, DurationFieldType.halfdays(), DurationFieldType.days());
        HOUR_OF_HALFDAY_TYPE = new StandardDateTimeFieldType("hourOfHalfday", (byte)14, DurationFieldType.hours(), DurationFieldType.halfdays());
        CLOCKHOUR_OF_HALFDAY_TYPE = new StandardDateTimeFieldType("clockhourOfHalfday", (byte)15, DurationFieldType.hours(), DurationFieldType.halfdays());
        CLOCKHOUR_OF_DAY_TYPE = new StandardDateTimeFieldType("clockhourOfDay", (byte)16, DurationFieldType.hours(), DurationFieldType.days());
        HOUR_OF_DAY_TYPE = new StandardDateTimeFieldType("hourOfDay", (byte)17, DurationFieldType.hours(), DurationFieldType.days());
        MINUTE_OF_DAY_TYPE = new StandardDateTimeFieldType("minuteOfDay", (byte)18, DurationFieldType.minutes(), DurationFieldType.days());
        MINUTE_OF_HOUR_TYPE = new StandardDateTimeFieldType("minuteOfHour", (byte)19, DurationFieldType.minutes(), DurationFieldType.hours());
        SECOND_OF_DAY_TYPE = new StandardDateTimeFieldType("secondOfDay", (byte)20, DurationFieldType.seconds(), DurationFieldType.days());
        SECOND_OF_MINUTE_TYPE = new StandardDateTimeFieldType("secondOfMinute", (byte)21, DurationFieldType.seconds(), DurationFieldType.minutes());
        MILLIS_OF_DAY_TYPE = new StandardDateTimeFieldType("millisOfDay", (byte)22, DurationFieldType.millis(), DurationFieldType.days());
        MILLIS_OF_SECOND_TYPE = new StandardDateTimeFieldType("millisOfSecond", (byte)23, DurationFieldType.millis(), DurationFieldType.seconds());
    }
    
    private static class StandardDateTimeFieldType extends DateTimeFieldType
    {
        private static final long serialVersionUID = -9937958251642L;
        private final byte iOrdinal;
        private final transient DurationFieldType iUnitType;
        private final transient DurationFieldType iRangeType;
        
        StandardDateTimeFieldType(final String s, final byte iOrdinal, final DurationFieldType iUnitType, final DurationFieldType iRangeType) {
            super(s);
            this.iOrdinal = iOrdinal;
            this.iUnitType = iUnitType;
            this.iRangeType = iRangeType;
        }
        
        @Override
        public DurationFieldType getDurationType() {
            return this.iUnitType;
        }
        
        @Override
        public DurationFieldType getRangeDurationType() {
            return this.iRangeType;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o instanceof StandardDateTimeFieldType && this.iOrdinal == ((StandardDateTimeFieldType)o).iOrdinal);
        }
        
        @Override
        public int hashCode() {
            return 1 << this.iOrdinal;
        }
        
        @Override
        public DateTimeField getField(Chronology chronology) {
            chronology = DateTimeUtils.getChronology(chronology);
            switch (this.iOrdinal) {
                case 1: {
                    return chronology.era();
                }
                case 2: {
                    return chronology.yearOfEra();
                }
                case 3: {
                    return chronology.centuryOfEra();
                }
                case 4: {
                    return chronology.yearOfCentury();
                }
                case 5: {
                    return chronology.year();
                }
                case 6: {
                    return chronology.dayOfYear();
                }
                case 7: {
                    return chronology.monthOfYear();
                }
                case 8: {
                    return chronology.dayOfMonth();
                }
                case 9: {
                    return chronology.weekyearOfCentury();
                }
                case 10: {
                    return chronology.weekyear();
                }
                case 11: {
                    return chronology.weekOfWeekyear();
                }
                case 12: {
                    return chronology.dayOfWeek();
                }
                case 13: {
                    return chronology.halfdayOfDay();
                }
                case 14: {
                    return chronology.hourOfHalfday();
                }
                case 15: {
                    return chronology.clockhourOfHalfday();
                }
                case 16: {
                    return chronology.clockhourOfDay();
                }
                case 17: {
                    return chronology.hourOfDay();
                }
                case 18: {
                    return chronology.minuteOfDay();
                }
                case 19: {
                    return chronology.minuteOfHour();
                }
                case 20: {
                    return chronology.secondOfDay();
                }
                case 21: {
                    return chronology.secondOfMinute();
                }
                case 22: {
                    return chronology.millisOfDay();
                }
                case 23: {
                    return chronology.millisOfSecond();
                }
                default: {
                    throw new InternalError();
                }
            }
        }
        
        private Object readResolve() {
            switch (this.iOrdinal) {
                case 1: {
                    return DateTimeFieldType.ERA_TYPE;
                }
                case 2: {
                    return DateTimeFieldType.YEAR_OF_ERA_TYPE;
                }
                case 3: {
                    return DateTimeFieldType.CENTURY_OF_ERA_TYPE;
                }
                case 4: {
                    return DateTimeFieldType.YEAR_OF_CENTURY_TYPE;
                }
                case 5: {
                    return DateTimeFieldType.YEAR_TYPE;
                }
                case 6: {
                    return DateTimeFieldType.DAY_OF_YEAR_TYPE;
                }
                case 7: {
                    return DateTimeFieldType.MONTH_OF_YEAR_TYPE;
                }
                case 8: {
                    return DateTimeFieldType.DAY_OF_MONTH_TYPE;
                }
                case 9: {
                    return DateTimeFieldType.WEEKYEAR_OF_CENTURY_TYPE;
                }
                case 10: {
                    return DateTimeFieldType.WEEKYEAR_TYPE;
                }
                case 11: {
                    return DateTimeFieldType.WEEK_OF_WEEKYEAR_TYPE;
                }
                case 12: {
                    return DateTimeFieldType.DAY_OF_WEEK_TYPE;
                }
                case 13: {
                    return DateTimeFieldType.HALFDAY_OF_DAY_TYPE;
                }
                case 14: {
                    return DateTimeFieldType.HOUR_OF_HALFDAY_TYPE;
                }
                case 15: {
                    return DateTimeFieldType.CLOCKHOUR_OF_HALFDAY_TYPE;
                }
                case 16: {
                    return DateTimeFieldType.CLOCKHOUR_OF_DAY_TYPE;
                }
                case 17: {
                    return DateTimeFieldType.HOUR_OF_DAY_TYPE;
                }
                case 18: {
                    return DateTimeFieldType.MINUTE_OF_DAY_TYPE;
                }
                case 19: {
                    return DateTimeFieldType.MINUTE_OF_HOUR_TYPE;
                }
                case 20: {
                    return DateTimeFieldType.SECOND_OF_DAY_TYPE;
                }
                case 21: {
                    return DateTimeFieldType.SECOND_OF_MINUTE_TYPE;
                }
                case 22: {
                    return DateTimeFieldType.MILLIS_OF_DAY_TYPE;
                }
                case 23: {
                    return DateTimeFieldType.MILLIS_OF_SECOND_TYPE;
                }
                default: {
                    return this;
                }
            }
        }
    }
}
