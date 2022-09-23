// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.ReadablePartial;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.field.PreciseDurationDateTimeField;

final class BasicDayOfYearDateTimeField extends PreciseDurationDateTimeField
{
    private static final long serialVersionUID = -6821236822336841037L;
    private final BasicChronology iChronology;
    
    BasicDayOfYearDateTimeField(final BasicChronology iChronology, final DurationField durationField) {
        super(DateTimeFieldType.dayOfYear(), durationField);
        this.iChronology = iChronology;
    }
    
    @Override
    public int get(final long n) {
        return this.iChronology.getDayOfYear(n);
    }
    
    @Override
    public DurationField getRangeDurationField() {
        return this.iChronology.years();
    }
    
    @Override
    public int getMinimumValue() {
        return 1;
    }
    
    @Override
    public int getMaximumValue() {
        return this.iChronology.getDaysInYearMax();
    }
    
    @Override
    public int getMaximumValue(final long n) {
        return this.iChronology.getDaysInYear(this.iChronology.getYear(n));
    }
    
    @Override
    public int getMaximumValue(final ReadablePartial readablePartial) {
        if (readablePartial.isSupported(DateTimeFieldType.year())) {
            return this.iChronology.getDaysInYear(readablePartial.get(DateTimeFieldType.year()));
        }
        return this.iChronology.getDaysInYearMax();
    }
    
    @Override
    public int getMaximumValue(final ReadablePartial readablePartial, final int[] array) {
        for (int size = readablePartial.size(), i = 0; i < size; ++i) {
            if (readablePartial.getFieldType(i) == DateTimeFieldType.year()) {
                return this.iChronology.getDaysInYear(array[i]);
            }
        }
        return this.iChronology.getDaysInYearMax();
    }
    
    @Override
    protected int getMaximumValueForSet(final long n, final int n2) {
        final int n3 = this.iChronology.getDaysInYearMax() - 1;
        return (n2 > n3 || n2 < 1) ? this.getMaximumValue(n) : n3;
    }
    
    @Override
    public boolean isLeap(final long n) {
        return this.iChronology.isLeapDay(n);
    }
    
    private Object readResolve() {
        return this.iChronology.dayOfYear();
    }
}
