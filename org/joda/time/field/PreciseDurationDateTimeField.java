// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;

public abstract class PreciseDurationDateTimeField extends BaseDateTimeField
{
    private static final long serialVersionUID = 5004523158306266035L;
    final long iUnitMillis;
    private final DurationField iUnitField;
    
    public PreciseDurationDateTimeField(final DateTimeFieldType dateTimeFieldType, final DurationField iUnitField) {
        super(dateTimeFieldType);
        if (!iUnitField.isPrecise()) {
            throw new IllegalArgumentException("Unit duration field must be precise");
        }
        this.iUnitMillis = iUnitField.getUnitMillis();
        if (this.iUnitMillis < 1L) {
            throw new IllegalArgumentException("The unit milliseconds must be at least 1");
        }
        this.iUnitField = iUnitField;
    }
    
    @Override
    public boolean isLenient() {
        return false;
    }
    
    @Override
    public long set(final long n, final int n2) {
        FieldUtils.verifyValueBounds(this, n2, this.getMinimumValue(), this.getMaximumValueForSet(n, n2));
        return n + (n2 - this.get(n)) * this.iUnitMillis;
    }
    
    @Override
    public long roundFloor(long n) {
        if (n >= 0L) {
            return n - n % this.iUnitMillis;
        }
        ++n;
        return n - n % this.iUnitMillis - this.iUnitMillis;
    }
    
    @Override
    public long roundCeiling(long n) {
        if (n > 0L) {
            --n;
            return n - n % this.iUnitMillis + this.iUnitMillis;
        }
        return n - n % this.iUnitMillis;
    }
    
    @Override
    public long remainder(final long n) {
        if (n >= 0L) {
            return n % this.iUnitMillis;
        }
        return (n + 1L) % this.iUnitMillis + this.iUnitMillis - 1L;
    }
    
    @Override
    public DurationField getDurationField() {
        return this.iUnitField;
    }
    
    @Override
    public int getMinimumValue() {
        return 0;
    }
    
    public final long getUnitMillis() {
        return this.iUnitMillis;
    }
    
    protected int getMaximumValueForSet(final long n, final int n2) {
        return this.getMaximumValue(n);
    }
}
