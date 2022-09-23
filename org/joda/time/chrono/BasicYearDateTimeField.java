// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.DurationField;
import org.joda.time.DateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.DateTimeFieldType;
import org.joda.time.field.ImpreciseDateTimeField;

class BasicYearDateTimeField extends ImpreciseDateTimeField
{
    private static final long serialVersionUID = -98628754872287L;
    protected final BasicChronology iChronology;
    
    BasicYearDateTimeField(final BasicChronology iChronology) {
        super(DateTimeFieldType.year(), iChronology.getAverageMillisPerYear());
        this.iChronology = iChronology;
    }
    
    @Override
    public boolean isLenient() {
        return false;
    }
    
    @Override
    public int get(final long n) {
        return this.iChronology.getYear(n);
    }
    
    @Override
    public long add(final long n, final int n2) {
        if (n2 == 0) {
            return n;
        }
        return this.set(n, FieldUtils.safeAdd(this.get(n), n2));
    }
    
    @Override
    public long add(final long n, final long n2) {
        return this.add(n, FieldUtils.safeToInt(n2));
    }
    
    @Override
    public long addWrapField(final long n, final int n2) {
        if (n2 == 0) {
            return n;
        }
        return this.set(n, FieldUtils.getWrappedValue(this.iChronology.getYear(n), n2, this.iChronology.getMinYear(), this.iChronology.getMaxYear()));
    }
    
    @Override
    public long set(final long n, final int n2) {
        FieldUtils.verifyValueBounds(this, n2, this.iChronology.getMinYear(), this.iChronology.getMaxYear());
        return this.iChronology.setYear(n, n2);
    }
    
    @Override
    public long getDifferenceAsLong(final long n, final long n2) {
        if (n < n2) {
            return -this.iChronology.getYearDifference(n2, n);
        }
        return this.iChronology.getYearDifference(n, n2);
    }
    
    @Override
    public DurationField getRangeDurationField() {
        return null;
    }
    
    @Override
    public boolean isLeap(final long n) {
        return this.iChronology.isLeapYear(this.get(n));
    }
    
    @Override
    public int getLeapAmount(final long n) {
        if (this.iChronology.isLeapYear(this.get(n))) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public DurationField getLeapDurationField() {
        return this.iChronology.days();
    }
    
    @Override
    public int getMinimumValue() {
        return this.iChronology.getMinYear();
    }
    
    @Override
    public int getMaximumValue() {
        return this.iChronology.getMaxYear();
    }
    
    @Override
    public long roundFloor(final long n) {
        return this.iChronology.getYearMillis(this.get(n));
    }
    
    @Override
    public long roundCeiling(long yearMillis) {
        final int value = this.get(yearMillis);
        if (yearMillis != this.iChronology.getYearMillis(value)) {
            yearMillis = this.iChronology.getYearMillis(value + 1);
        }
        return yearMillis;
    }
    
    @Override
    public long remainder(final long n) {
        return n - this.roundFloor(n);
    }
    
    private Object readResolve() {
        return this.iChronology.year();
    }
}
