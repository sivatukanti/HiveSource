// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import org.joda.time.format.ISOPeriodFormat;
import org.joda.convert.ToString;
import org.joda.time.field.FieldUtils;
import org.joda.convert.FromString;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.base.BaseSingleFieldPeriod;

public final class Seconds extends BaseSingleFieldPeriod
{
    public static final Seconds ZERO;
    public static final Seconds ONE;
    public static final Seconds TWO;
    public static final Seconds THREE;
    public static final Seconds MAX_VALUE;
    public static final Seconds MIN_VALUE;
    private static final PeriodFormatter PARSER;
    private static final long serialVersionUID = 87525275727380862L;
    
    public static Seconds seconds(final int n) {
        switch (n) {
            case 0: {
                return Seconds.ZERO;
            }
            case 1: {
                return Seconds.ONE;
            }
            case 2: {
                return Seconds.TWO;
            }
            case 3: {
                return Seconds.THREE;
            }
            case Integer.MAX_VALUE: {
                return Seconds.MAX_VALUE;
            }
            case Integer.MIN_VALUE: {
                return Seconds.MIN_VALUE;
            }
            default: {
                return new Seconds(n);
            }
        }
    }
    
    public static Seconds secondsBetween(final ReadableInstant readableInstant, final ReadableInstant readableInstant2) {
        return seconds(BaseSingleFieldPeriod.between(readableInstant, readableInstant2, DurationFieldType.seconds()));
    }
    
    public static Seconds secondsBetween(final ReadablePartial readablePartial, final ReadablePartial readablePartial2) {
        if (readablePartial instanceof LocalTime && readablePartial2 instanceof LocalTime) {
            return seconds(DateTimeUtils.getChronology(readablePartial.getChronology()).seconds().getDifference(((LocalTime)readablePartial2).getLocalMillis(), ((LocalTime)readablePartial).getLocalMillis()));
        }
        return seconds(BaseSingleFieldPeriod.between(readablePartial, readablePartial2, Seconds.ZERO));
    }
    
    public static Seconds secondsIn(final ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return Seconds.ZERO;
        }
        return seconds(BaseSingleFieldPeriod.between(readableInterval.getStart(), readableInterval.getEnd(), DurationFieldType.seconds()));
    }
    
    public static Seconds standardSecondsIn(final ReadablePeriod readablePeriod) {
        return seconds(BaseSingleFieldPeriod.standardPeriodIn(readablePeriod, 1000L));
    }
    
    @FromString
    public static Seconds parseSeconds(final String s) {
        if (s == null) {
            return Seconds.ZERO;
        }
        return seconds(Seconds.PARSER.parsePeriod(s).getSeconds());
    }
    
    private Seconds(final int n) {
        super(n);
    }
    
    private Object readResolve() {
        return seconds(this.getValue());
    }
    
    @Override
    public DurationFieldType getFieldType() {
        return DurationFieldType.seconds();
    }
    
    @Override
    public PeriodType getPeriodType() {
        return PeriodType.seconds();
    }
    
    public Weeks toStandardWeeks() {
        return Weeks.weeks(this.getValue() / 604800);
    }
    
    public Days toStandardDays() {
        return Days.days(this.getValue() / 86400);
    }
    
    public Hours toStandardHours() {
        return Hours.hours(this.getValue() / 3600);
    }
    
    public Minutes toStandardMinutes() {
        return Minutes.minutes(this.getValue() / 60);
    }
    
    public Duration toStandardDuration() {
        return new Duration(this.getValue() * 1000L);
    }
    
    public int getSeconds() {
        return this.getValue();
    }
    
    public Seconds plus(final int n) {
        if (n == 0) {
            return this;
        }
        return seconds(FieldUtils.safeAdd(this.getValue(), n));
    }
    
    public Seconds plus(final Seconds seconds) {
        if (seconds == null) {
            return this;
        }
        return this.plus(seconds.getValue());
    }
    
    public Seconds minus(final int n) {
        return this.plus(FieldUtils.safeNegate(n));
    }
    
    public Seconds minus(final Seconds seconds) {
        if (seconds == null) {
            return this;
        }
        return this.minus(seconds.getValue());
    }
    
    public Seconds multipliedBy(final int n) {
        return seconds(FieldUtils.safeMultiply(this.getValue(), n));
    }
    
    public Seconds dividedBy(final int n) {
        if (n == 1) {
            return this;
        }
        return seconds(this.getValue() / n);
    }
    
    public Seconds negated() {
        return seconds(FieldUtils.safeNegate(this.getValue()));
    }
    
    public boolean isGreaterThan(final Seconds seconds) {
        if (seconds == null) {
            return this.getValue() > 0;
        }
        return this.getValue() > seconds.getValue();
    }
    
    public boolean isLessThan(final Seconds seconds) {
        if (seconds == null) {
            return this.getValue() < 0;
        }
        return this.getValue() < seconds.getValue();
    }
    
    @ToString
    @Override
    public String toString() {
        return "PT" + String.valueOf(this.getValue()) + "S";
    }
    
    static {
        ZERO = new Seconds(0);
        ONE = new Seconds(1);
        TWO = new Seconds(2);
        THREE = new Seconds(3);
        MAX_VALUE = new Seconds(Integer.MAX_VALUE);
        MIN_VALUE = new Seconds(Integer.MIN_VALUE);
        PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.seconds());
    }
}
