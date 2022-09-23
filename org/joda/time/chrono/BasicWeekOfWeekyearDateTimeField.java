// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.ReadablePartial;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.field.PreciseDurationDateTimeField;

final class BasicWeekOfWeekyearDateTimeField extends PreciseDurationDateTimeField
{
    private static final long serialVersionUID = -1587436826395135328L;
    private final BasicChronology iChronology;
    
    BasicWeekOfWeekyearDateTimeField(final BasicChronology iChronology, final DurationField durationField) {
        super(DateTimeFieldType.weekOfWeekyear(), durationField);
        this.iChronology = iChronology;
    }
    
    @Override
    public int get(final long n) {
        return this.iChronology.getWeekOfWeekyear(n);
    }
    
    @Override
    public DurationField getRangeDurationField() {
        return this.iChronology.weekyears();
    }
    
    @Override
    public long roundFloor(final long n) {
        return super.roundFloor(n + 259200000L) - 259200000L;
    }
    
    @Override
    public long roundCeiling(final long n) {
        return super.roundCeiling(n + 259200000L) - 259200000L;
    }
    
    @Override
    public long remainder(final long n) {
        return super.remainder(n + 259200000L);
    }
    
    @Override
    public int getMinimumValue() {
        return 1;
    }
    
    @Override
    public int getMaximumValue() {
        return 53;
    }
    
    @Override
    public int getMaximumValue(final long n) {
        return this.iChronology.getWeeksInYear(this.iChronology.getWeekyear(n));
    }
    
    @Override
    public int getMaximumValue(final ReadablePartial readablePartial) {
        if (readablePartial.isSupported(DateTimeFieldType.weekyear())) {
            return this.iChronology.getWeeksInYear(readablePartial.get(DateTimeFieldType.weekyear()));
        }
        return 53;
    }
    
    @Override
    public int getMaximumValue(final ReadablePartial readablePartial, final int[] array) {
        for (int size = readablePartial.size(), i = 0; i < size; ++i) {
            if (readablePartial.getFieldType(i) == DateTimeFieldType.weekyear()) {
                return this.iChronology.getWeeksInYear(array[i]);
            }
        }
        return 53;
    }
    
    @Override
    protected int getMaximumValueForSet(final long n, final int n2) {
        return (n2 > 52) ? this.getMaximumValue(n) : 52;
    }
    
    private Object readResolve() {
        return this.iChronology.weekOfWeekyear();
    }
}
