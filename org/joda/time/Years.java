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

public final class Years extends BaseSingleFieldPeriod
{
    public static final Years ZERO;
    public static final Years ONE;
    public static final Years TWO;
    public static final Years THREE;
    public static final Years MAX_VALUE;
    public static final Years MIN_VALUE;
    private static final PeriodFormatter PARSER;
    private static final long serialVersionUID = 87525275727380868L;
    
    public static Years years(final int n) {
        switch (n) {
            case 0: {
                return Years.ZERO;
            }
            case 1: {
                return Years.ONE;
            }
            case 2: {
                return Years.TWO;
            }
            case 3: {
                return Years.THREE;
            }
            case Integer.MAX_VALUE: {
                return Years.MAX_VALUE;
            }
            case Integer.MIN_VALUE: {
                return Years.MIN_VALUE;
            }
            default: {
                return new Years(n);
            }
        }
    }
    
    public static Years yearsBetween(final ReadableInstant readableInstant, final ReadableInstant readableInstant2) {
        return years(BaseSingleFieldPeriod.between(readableInstant, readableInstant2, DurationFieldType.years()));
    }
    
    public static Years yearsBetween(final ReadablePartial readablePartial, final ReadablePartial readablePartial2) {
        if (readablePartial instanceof LocalDate && readablePartial2 instanceof LocalDate) {
            return years(DateTimeUtils.getChronology(readablePartial.getChronology()).years().getDifference(((LocalDate)readablePartial2).getLocalMillis(), ((LocalDate)readablePartial).getLocalMillis()));
        }
        return years(BaseSingleFieldPeriod.between(readablePartial, readablePartial2, Years.ZERO));
    }
    
    public static Years yearsIn(final ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return Years.ZERO;
        }
        return years(BaseSingleFieldPeriod.between(readableInterval.getStart(), readableInterval.getEnd(), DurationFieldType.years()));
    }
    
    @FromString
    public static Years parseYears(final String s) {
        if (s == null) {
            return Years.ZERO;
        }
        return years(Years.PARSER.parsePeriod(s).getYears());
    }
    
    private Years(final int n) {
        super(n);
    }
    
    private Object readResolve() {
        return years(this.getValue());
    }
    
    @Override
    public DurationFieldType getFieldType() {
        return DurationFieldType.years();
    }
    
    @Override
    public PeriodType getPeriodType() {
        return PeriodType.years();
    }
    
    public int getYears() {
        return this.getValue();
    }
    
    public Years plus(final int n) {
        if (n == 0) {
            return this;
        }
        return years(FieldUtils.safeAdd(this.getValue(), n));
    }
    
    public Years plus(final Years years) {
        if (years == null) {
            return this;
        }
        return this.plus(years.getValue());
    }
    
    public Years minus(final int n) {
        return this.plus(FieldUtils.safeNegate(n));
    }
    
    public Years minus(final Years years) {
        if (years == null) {
            return this;
        }
        return this.minus(years.getValue());
    }
    
    public Years multipliedBy(final int n) {
        return years(FieldUtils.safeMultiply(this.getValue(), n));
    }
    
    public Years dividedBy(final int n) {
        if (n == 1) {
            return this;
        }
        return years(this.getValue() / n);
    }
    
    public Years negated() {
        return years(FieldUtils.safeNegate(this.getValue()));
    }
    
    public boolean isGreaterThan(final Years years) {
        if (years == null) {
            return this.getValue() > 0;
        }
        return this.getValue() > years.getValue();
    }
    
    public boolean isLessThan(final Years years) {
        if (years == null) {
            return this.getValue() < 0;
        }
        return this.getValue() < years.getValue();
    }
    
    @ToString
    @Override
    public String toString() {
        return "P" + String.valueOf(this.getValue()) + "Y";
    }
    
    static {
        ZERO = new Years(0);
        ONE = new Years(1);
        TWO = new Years(2);
        THREE = new Years(3);
        MAX_VALUE = new Years(Integer.MAX_VALUE);
        MIN_VALUE = new Years(Integer.MIN_VALUE);
        PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.years());
    }
}
