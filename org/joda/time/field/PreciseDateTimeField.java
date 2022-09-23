// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;

public class PreciseDateTimeField extends PreciseDurationDateTimeField
{
    private static final long serialVersionUID = -5586801265774496376L;
    private final int iRange;
    private final DurationField iRangeField;
    
    public PreciseDateTimeField(final DateTimeFieldType dateTimeFieldType, final DurationField durationField, final DurationField iRangeField) {
        super(dateTimeFieldType, durationField);
        if (!iRangeField.isPrecise()) {
            throw new IllegalArgumentException("Range duration field must be precise");
        }
        this.iRange = (int)(iRangeField.getUnitMillis() / this.getUnitMillis());
        if (this.iRange < 2) {
            throw new IllegalArgumentException("The effective range must be at least 2");
        }
        this.iRangeField = iRangeField;
    }
    
    @Override
    public int get(final long n) {
        if (n >= 0L) {
            return (int)(n / this.getUnitMillis() % this.iRange);
        }
        return this.iRange - 1 + (int)((n + 1L) / this.getUnitMillis() % this.iRange);
    }
    
    @Override
    public long addWrapField(final long n, final int n2) {
        final int value = this.get(n);
        return n + (FieldUtils.getWrappedValue(value, n2, this.getMinimumValue(), this.getMaximumValue()) - value) * this.getUnitMillis();
    }
    
    @Override
    public long set(final long n, final int n2) {
        FieldUtils.verifyValueBounds(this, n2, this.getMinimumValue(), this.getMaximumValue());
        return n + (n2 - this.get(n)) * this.iUnitMillis;
    }
    
    @Override
    public DurationField getRangeDurationField() {
        return this.iRangeField;
    }
    
    @Override
    public int getMaximumValue() {
        return this.iRange - 1;
    }
    
    public int getRange() {
        return this.iRange;
    }
}
