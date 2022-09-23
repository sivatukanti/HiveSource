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

public final class Minutes extends BaseSingleFieldPeriod
{
    public static final Minutes ZERO;
    public static final Minutes ONE;
    public static final Minutes TWO;
    public static final Minutes THREE;
    public static final Minutes MAX_VALUE;
    public static final Minutes MIN_VALUE;
    private static final PeriodFormatter PARSER;
    private static final long serialVersionUID = 87525275727380863L;
    
    public static Minutes minutes(final int n) {
        switch (n) {
            case 0: {
                return Minutes.ZERO;
            }
            case 1: {
                return Minutes.ONE;
            }
            case 2: {
                return Minutes.TWO;
            }
            case 3: {
                return Minutes.THREE;
            }
            case Integer.MAX_VALUE: {
                return Minutes.MAX_VALUE;
            }
            case Integer.MIN_VALUE: {
                return Minutes.MIN_VALUE;
            }
            default: {
                return new Minutes(n);
            }
        }
    }
    
    public static Minutes minutesBetween(final ReadableInstant readableInstant, final ReadableInstant readableInstant2) {
        return minutes(BaseSingleFieldPeriod.between(readableInstant, readableInstant2, DurationFieldType.minutes()));
    }
    
    public static Minutes minutesBetween(final ReadablePartial readablePartial, final ReadablePartial readablePartial2) {
        if (readablePartial instanceof LocalTime && readablePartial2 instanceof LocalTime) {
            return minutes(DateTimeUtils.getChronology(readablePartial.getChronology()).minutes().getDifference(((LocalTime)readablePartial2).getLocalMillis(), ((LocalTime)readablePartial).getLocalMillis()));
        }
        return minutes(BaseSingleFieldPeriod.between(readablePartial, readablePartial2, Minutes.ZERO));
    }
    
    public static Minutes minutesIn(final ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return Minutes.ZERO;
        }
        return minutes(BaseSingleFieldPeriod.between(readableInterval.getStart(), readableInterval.getEnd(), DurationFieldType.minutes()));
    }
    
    public static Minutes standardMinutesIn(final ReadablePeriod readablePeriod) {
        return minutes(BaseSingleFieldPeriod.standardPeriodIn(readablePeriod, 60000L));
    }
    
    @FromString
    public static Minutes parseMinutes(final String s) {
        if (s == null) {
            return Minutes.ZERO;
        }
        return minutes(Minutes.PARSER.parsePeriod(s).getMinutes());
    }
    
    private Minutes(final int n) {
        super(n);
    }
    
    private Object readResolve() {
        return minutes(this.getValue());
    }
    
    @Override
    public DurationFieldType getFieldType() {
        return DurationFieldType.minutes();
    }
    
    @Override
    public PeriodType getPeriodType() {
        return PeriodType.minutes();
    }
    
    public Weeks toStandardWeeks() {
        return Weeks.weeks(this.getValue() / 10080);
    }
    
    public Days toStandardDays() {
        return Days.days(this.getValue() / 1440);
    }
    
    public Hours toStandardHours() {
        return Hours.hours(this.getValue() / 60);
    }
    
    public Seconds toStandardSeconds() {
        return Seconds.seconds(FieldUtils.safeMultiply(this.getValue(), 60));
    }
    
    public Duration toStandardDuration() {
        return new Duration(this.getValue() * 60000L);
    }
    
    public int getMinutes() {
        return this.getValue();
    }
    
    public Minutes plus(final int n) {
        if (n == 0) {
            return this;
        }
        return minutes(FieldUtils.safeAdd(this.getValue(), n));
    }
    
    public Minutes plus(final Minutes minutes) {
        if (minutes == null) {
            return this;
        }
        return this.plus(minutes.getValue());
    }
    
    public Minutes minus(final int n) {
        return this.plus(FieldUtils.safeNegate(n));
    }
    
    public Minutes minus(final Minutes minutes) {
        if (minutes == null) {
            return this;
        }
        return this.minus(minutes.getValue());
    }
    
    public Minutes multipliedBy(final int n) {
        return minutes(FieldUtils.safeMultiply(this.getValue(), n));
    }
    
    public Minutes dividedBy(final int n) {
        if (n == 1) {
            return this;
        }
        return minutes(this.getValue() / n);
    }
    
    public Minutes negated() {
        return minutes(FieldUtils.safeNegate(this.getValue()));
    }
    
    public boolean isGreaterThan(final Minutes minutes) {
        if (minutes == null) {
            return this.getValue() > 0;
        }
        return this.getValue() > minutes.getValue();
    }
    
    public boolean isLessThan(final Minutes minutes) {
        if (minutes == null) {
            return this.getValue() < 0;
        }
        return this.getValue() < minutes.getValue();
    }
    
    @ToString
    @Override
    public String toString() {
        return "PT" + String.valueOf(this.getValue()) + "M";
    }
    
    static {
        ZERO = new Minutes(0);
        ONE = new Minutes(1);
        TWO = new Minutes(2);
        THREE = new Minutes(3);
        MAX_VALUE = new Minutes(Integer.MAX_VALUE);
        MIN_VALUE = new Minutes(Integer.MIN_VALUE);
        PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.minutes());
    }
}
