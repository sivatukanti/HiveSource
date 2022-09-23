// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DurationFieldType;

public class PreciseDurationField extends BaseDurationField
{
    private static final long serialVersionUID = -8346152187724495365L;
    private final long iUnitMillis;
    
    public PreciseDurationField(final DurationFieldType durationFieldType, final long iUnitMillis) {
        super(durationFieldType);
        this.iUnitMillis = iUnitMillis;
    }
    
    @Override
    public final boolean isPrecise() {
        return true;
    }
    
    @Override
    public final long getUnitMillis() {
        return this.iUnitMillis;
    }
    
    @Override
    public long getValueAsLong(final long n, final long n2) {
        return n / this.iUnitMillis;
    }
    
    @Override
    public long getMillis(final int n, final long n2) {
        return n * this.iUnitMillis;
    }
    
    @Override
    public long getMillis(final long n, final long n2) {
        return FieldUtils.safeMultiply(n, this.iUnitMillis);
    }
    
    @Override
    public long add(final long n, final int n2) {
        return FieldUtils.safeAdd(n, n2 * this.iUnitMillis);
    }
    
    @Override
    public long add(final long n, final long n2) {
        return FieldUtils.safeAdd(n, FieldUtils.safeMultiply(n2, this.iUnitMillis));
    }
    
    @Override
    public long getDifferenceAsLong(final long n, final long n2) {
        return FieldUtils.safeSubtract(n, n2) / this.iUnitMillis;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PreciseDurationField) {
            final PreciseDurationField preciseDurationField = (PreciseDurationField)o;
            return this.getType() == preciseDurationField.getType() && this.iUnitMillis == preciseDurationField.iUnitMillis;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final long iUnitMillis = this.iUnitMillis;
        return (int)(iUnitMillis ^ iUnitMillis >>> 32) + this.getType().hashCode();
    }
}
