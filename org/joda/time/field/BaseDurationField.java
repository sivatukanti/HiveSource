// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DurationFieldType;
import java.io.Serializable;
import org.joda.time.DurationField;

public abstract class BaseDurationField extends DurationField implements Serializable
{
    private static final long serialVersionUID = -2554245107589433218L;
    private final DurationFieldType iType;
    
    protected BaseDurationField(final DurationFieldType iType) {
        if (iType == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        this.iType = iType;
    }
    
    @Override
    public final DurationFieldType getType() {
        return this.iType;
    }
    
    @Override
    public final String getName() {
        return this.iType.getName();
    }
    
    @Override
    public final boolean isSupported() {
        return true;
    }
    
    @Override
    public int getValue(final long n) {
        return FieldUtils.safeToInt(this.getValueAsLong(n));
    }
    
    @Override
    public long getValueAsLong(final long n) {
        return n / this.getUnitMillis();
    }
    
    @Override
    public int getValue(final long n, final long n2) {
        return FieldUtils.safeToInt(this.getValueAsLong(n, n2));
    }
    
    @Override
    public long getMillis(final int n) {
        return n * this.getUnitMillis();
    }
    
    @Override
    public long getMillis(final long n) {
        return FieldUtils.safeMultiply(n, this.getUnitMillis());
    }
    
    @Override
    public int getDifference(final long n, final long n2) {
        return FieldUtils.safeToInt(this.getDifferenceAsLong(n, n2));
    }
    
    public int compareTo(final DurationField durationField) {
        final long unitMillis = durationField.getUnitMillis();
        final long unitMillis2 = this.getUnitMillis();
        if (unitMillis2 == unitMillis) {
            return 0;
        }
        if (unitMillis2 < unitMillis) {
            return -1;
        }
        return 1;
    }
    
    @Override
    public String toString() {
        return "DurationField[" + this.getName() + ']';
    }
}
