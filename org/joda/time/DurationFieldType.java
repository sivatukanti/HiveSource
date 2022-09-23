// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.io.Serializable;

public abstract class DurationFieldType implements Serializable
{
    private static final long serialVersionUID = 8765135187319L;
    static final byte ERAS = 1;
    static final byte CENTURIES = 2;
    static final byte WEEKYEARS = 3;
    static final byte YEARS = 4;
    static final byte MONTHS = 5;
    static final byte WEEKS = 6;
    static final byte DAYS = 7;
    static final byte HALFDAYS = 8;
    static final byte HOURS = 9;
    static final byte MINUTES = 10;
    static final byte SECONDS = 11;
    static final byte MILLIS = 12;
    static final DurationFieldType ERAS_TYPE;
    static final DurationFieldType CENTURIES_TYPE;
    static final DurationFieldType WEEKYEARS_TYPE;
    static final DurationFieldType YEARS_TYPE;
    static final DurationFieldType MONTHS_TYPE;
    static final DurationFieldType WEEKS_TYPE;
    static final DurationFieldType DAYS_TYPE;
    static final DurationFieldType HALFDAYS_TYPE;
    static final DurationFieldType HOURS_TYPE;
    static final DurationFieldType MINUTES_TYPE;
    static final DurationFieldType SECONDS_TYPE;
    static final DurationFieldType MILLIS_TYPE;
    private final String iName;
    
    protected DurationFieldType(final String iName) {
        this.iName = iName;
    }
    
    public static DurationFieldType millis() {
        return DurationFieldType.MILLIS_TYPE;
    }
    
    public static DurationFieldType seconds() {
        return DurationFieldType.SECONDS_TYPE;
    }
    
    public static DurationFieldType minutes() {
        return DurationFieldType.MINUTES_TYPE;
    }
    
    public static DurationFieldType hours() {
        return DurationFieldType.HOURS_TYPE;
    }
    
    public static DurationFieldType halfdays() {
        return DurationFieldType.HALFDAYS_TYPE;
    }
    
    public static DurationFieldType days() {
        return DurationFieldType.DAYS_TYPE;
    }
    
    public static DurationFieldType weeks() {
        return DurationFieldType.WEEKS_TYPE;
    }
    
    public static DurationFieldType weekyears() {
        return DurationFieldType.WEEKYEARS_TYPE;
    }
    
    public static DurationFieldType months() {
        return DurationFieldType.MONTHS_TYPE;
    }
    
    public static DurationFieldType years() {
        return DurationFieldType.YEARS_TYPE;
    }
    
    public static DurationFieldType centuries() {
        return DurationFieldType.CENTURIES_TYPE;
    }
    
    public static DurationFieldType eras() {
        return DurationFieldType.ERAS_TYPE;
    }
    
    public String getName() {
        return this.iName;
    }
    
    public abstract DurationField getField(final Chronology p0);
    
    public boolean isSupported(final Chronology chronology) {
        return this.getField(chronology).isSupported();
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    static {
        ERAS_TYPE = new StandardDurationFieldType("eras", (byte)1);
        CENTURIES_TYPE = new StandardDurationFieldType("centuries", (byte)2);
        WEEKYEARS_TYPE = new StandardDurationFieldType("weekyears", (byte)3);
        YEARS_TYPE = new StandardDurationFieldType("years", (byte)4);
        MONTHS_TYPE = new StandardDurationFieldType("months", (byte)5);
        WEEKS_TYPE = new StandardDurationFieldType("weeks", (byte)6);
        DAYS_TYPE = new StandardDurationFieldType("days", (byte)7);
        HALFDAYS_TYPE = new StandardDurationFieldType("halfdays", (byte)8);
        HOURS_TYPE = new StandardDurationFieldType("hours", (byte)9);
        MINUTES_TYPE = new StandardDurationFieldType("minutes", (byte)10);
        SECONDS_TYPE = new StandardDurationFieldType("seconds", (byte)11);
        MILLIS_TYPE = new StandardDurationFieldType("millis", (byte)12);
    }
    
    private static class StandardDurationFieldType extends DurationFieldType
    {
        private static final long serialVersionUID = 31156755687123L;
        private final byte iOrdinal;
        
        StandardDurationFieldType(final String s, final byte iOrdinal) {
            super(s);
            this.iOrdinal = iOrdinal;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o instanceof StandardDurationFieldType && this.iOrdinal == ((StandardDurationFieldType)o).iOrdinal);
        }
        
        @Override
        public int hashCode() {
            return 1 << this.iOrdinal;
        }
        
        @Override
        public DurationField getField(Chronology chronology) {
            chronology = DateTimeUtils.getChronology(chronology);
            switch (this.iOrdinal) {
                case 1: {
                    return chronology.eras();
                }
                case 2: {
                    return chronology.centuries();
                }
                case 3: {
                    return chronology.weekyears();
                }
                case 4: {
                    return chronology.years();
                }
                case 5: {
                    return chronology.months();
                }
                case 6: {
                    return chronology.weeks();
                }
                case 7: {
                    return chronology.days();
                }
                case 8: {
                    return chronology.halfdays();
                }
                case 9: {
                    return chronology.hours();
                }
                case 10: {
                    return chronology.minutes();
                }
                case 11: {
                    return chronology.seconds();
                }
                case 12: {
                    return chronology.millis();
                }
                default: {
                    throw new InternalError();
                }
            }
        }
        
        private Object readResolve() {
            switch (this.iOrdinal) {
                case 1: {
                    return StandardDurationFieldType.ERAS_TYPE;
                }
                case 2: {
                    return StandardDurationFieldType.CENTURIES_TYPE;
                }
                case 3: {
                    return StandardDurationFieldType.WEEKYEARS_TYPE;
                }
                case 4: {
                    return StandardDurationFieldType.YEARS_TYPE;
                }
                case 5: {
                    return StandardDurationFieldType.MONTHS_TYPE;
                }
                case 6: {
                    return StandardDurationFieldType.WEEKS_TYPE;
                }
                case 7: {
                    return StandardDurationFieldType.DAYS_TYPE;
                }
                case 8: {
                    return StandardDurationFieldType.HALFDAYS_TYPE;
                }
                case 9: {
                    return StandardDurationFieldType.HOURS_TYPE;
                }
                case 10: {
                    return StandardDurationFieldType.MINUTES_TYPE;
                }
                case 11: {
                    return StandardDurationFieldType.SECONDS_TYPE;
                }
                case 12: {
                    return StandardDurationFieldType.MILLIS_TYPE;
                }
                default: {
                    return this;
                }
            }
        }
    }
}
