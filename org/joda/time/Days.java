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

public final class Days extends BaseSingleFieldPeriod
{
    public static final Days ZERO;
    public static final Days ONE;
    public static final Days TWO;
    public static final Days THREE;
    public static final Days FOUR;
    public static final Days FIVE;
    public static final Days SIX;
    public static final Days SEVEN;
    public static final Days MAX_VALUE;
    public static final Days MIN_VALUE;
    private static final PeriodFormatter PARSER;
    private static final long serialVersionUID = 87525275727380865L;
    
    public static Days days(final int n) {
        switch (n) {
            case 0: {
                return Days.ZERO;
            }
            case 1: {
                return Days.ONE;
            }
            case 2: {
                return Days.TWO;
            }
            case 3: {
                return Days.THREE;
            }
            case 4: {
                return Days.FOUR;
            }
            case 5: {
                return Days.FIVE;
            }
            case 6: {
                return Days.SIX;
            }
            case 7: {
                return Days.SEVEN;
            }
            case Integer.MAX_VALUE: {
                return Days.MAX_VALUE;
            }
            case Integer.MIN_VALUE: {
                return Days.MIN_VALUE;
            }
            default: {
                return new Days(n);
            }
        }
    }
    
    public static Days daysBetween(final ReadableInstant readableInstant, final ReadableInstant readableInstant2) {
        return days(BaseSingleFieldPeriod.between(readableInstant, readableInstant2, DurationFieldType.days()));
    }
    
    public static Days daysBetween(final ReadablePartial readablePartial, final ReadablePartial readablePartial2) {
        if (readablePartial instanceof LocalDate && readablePartial2 instanceof LocalDate) {
            return days(DateTimeUtils.getChronology(readablePartial.getChronology()).days().getDifference(((LocalDate)readablePartial2).getLocalMillis(), ((LocalDate)readablePartial).getLocalMillis()));
        }
        return days(BaseSingleFieldPeriod.between(readablePartial, readablePartial2, Days.ZERO));
    }
    
    public static Days daysIn(final ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return Days.ZERO;
        }
        return days(BaseSingleFieldPeriod.between(readableInterval.getStart(), readableInterval.getEnd(), DurationFieldType.days()));
    }
    
    public static Days standardDaysIn(final ReadablePeriod readablePeriod) {
        return days(BaseSingleFieldPeriod.standardPeriodIn(readablePeriod, 86400000L));
    }
    
    @FromString
    public static Days parseDays(final String s) {
        if (s == null) {
            return Days.ZERO;
        }
        return days(Days.PARSER.parsePeriod(s).getDays());
    }
    
    private Days(final int n) {
        super(n);
    }
    
    private Object readResolve() {
        return days(this.getValue());
    }
    
    @Override
    public DurationFieldType getFieldType() {
        return DurationFieldType.days();
    }
    
    @Override
    public PeriodType getPeriodType() {
        return PeriodType.days();
    }
    
    public Weeks toStandardWeeks() {
        return Weeks.weeks(this.getValue() / 7);
    }
    
    public Hours toStandardHours() {
        return Hours.hours(FieldUtils.safeMultiply(this.getValue(), 24));
    }
    
    public Minutes toStandardMinutes() {
        return Minutes.minutes(FieldUtils.safeMultiply(this.getValue(), 1440));
    }
    
    public Seconds toStandardSeconds() {
        return Seconds.seconds(FieldUtils.safeMultiply(this.getValue(), 86400));
    }
    
    public Duration toStandardDuration() {
        return new Duration(this.getValue() * 86400000L);
    }
    
    public int getDays() {
        return this.getValue();
    }
    
    public Days plus(final int n) {
        if (n == 0) {
            return this;
        }
        return days(FieldUtils.safeAdd(this.getValue(), n));
    }
    
    public Days plus(final Days days) {
        if (days == null) {
            return this;
        }
        return this.plus(days.getValue());
    }
    
    public Days minus(final int n) {
        return this.plus(FieldUtils.safeNegate(n));
    }
    
    public Days minus(final Days days) {
        if (days == null) {
            return this;
        }
        return this.minus(days.getValue());
    }
    
    public Days multipliedBy(final int n) {
        return days(FieldUtils.safeMultiply(this.getValue(), n));
    }
    
    public Days dividedBy(final int n) {
        if (n == 1) {
            return this;
        }
        return days(this.getValue() / n);
    }
    
    public Days negated() {
        return days(FieldUtils.safeNegate(this.getValue()));
    }
    
    public boolean isGreaterThan(final Days days) {
        if (days == null) {
            return this.getValue() > 0;
        }
        return this.getValue() > days.getValue();
    }
    
    public boolean isLessThan(final Days days) {
        if (days == null) {
            return this.getValue() < 0;
        }
        return this.getValue() < days.getValue();
    }
    
    @ToString
    @Override
    public String toString() {
        return "P" + String.valueOf(this.getValue()) + "D";
    }
    
    static {
        ZERO = new Days(0);
        ONE = new Days(1);
        TWO = new Days(2);
        THREE = new Days(3);
        FOUR = new Days(4);
        FIVE = new Days(5);
        SIX = new Days(6);
        SEVEN = new Days(7);
        MAX_VALUE = new Days(Integer.MAX_VALUE);
        MIN_VALUE = new Days(Integer.MIN_VALUE);
        PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.days());
    }
}
