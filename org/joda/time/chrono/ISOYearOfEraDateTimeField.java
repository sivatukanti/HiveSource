// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.field.FieldUtils;
import org.joda.time.ReadablePartial;
import org.joda.time.DurationField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeField;
import org.joda.time.field.DecoratedDateTimeField;

class ISOYearOfEraDateTimeField extends DecoratedDateTimeField
{
    private static final long serialVersionUID = 7037524068969447317L;
    static final DateTimeField INSTANCE;
    
    private ISOYearOfEraDateTimeField() {
        super(GregorianChronology.getInstanceUTC().year(), DateTimeFieldType.yearOfEra());
    }
    
    @Override
    public DurationField getRangeDurationField() {
        return GregorianChronology.getInstanceUTC().eras();
    }
    
    @Override
    public int get(final long n) {
        final int value = this.getWrappedField().get(n);
        return (value < 0) ? (-value) : value;
    }
    
    @Override
    public long add(final long n, final int n2) {
        return this.getWrappedField().add(n, n2);
    }
    
    @Override
    public long add(final long n, final long n2) {
        return this.getWrappedField().add(n, n2);
    }
    
    @Override
    public long addWrapField(final long n, final int n2) {
        return this.getWrappedField().addWrapField(n, n2);
    }
    
    @Override
    public int[] addWrapField(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        return this.getWrappedField().addWrapField(readablePartial, n, array, n2);
    }
    
    @Override
    public int getDifference(final long n, final long n2) {
        return this.getWrappedField().getDifference(n, n2);
    }
    
    @Override
    public long getDifferenceAsLong(final long n, final long n2) {
        return this.getWrappedField().getDifferenceAsLong(n, n2);
    }
    
    @Override
    public long set(final long n, int n2) {
        FieldUtils.verifyValueBounds(this, n2, 0, this.getMaximumValue());
        if (this.getWrappedField().get(n) < 0) {
            n2 = -n2;
        }
        return super.set(n, n2);
    }
    
    @Override
    public int getMinimumValue() {
        return 0;
    }
    
    @Override
    public int getMaximumValue() {
        return this.getWrappedField().getMaximumValue();
    }
    
    @Override
    public long roundFloor(final long n) {
        return this.getWrappedField().roundFloor(n);
    }
    
    @Override
    public long roundCeiling(final long n) {
        return this.getWrappedField().roundCeiling(n);
    }
    
    @Override
    public long remainder(final long n) {
        return this.getWrappedField().remainder(n);
    }
    
    private Object readResolve() {
        return ISOYearOfEraDateTimeField.INSTANCE;
    }
    
    static {
        INSTANCE = new ISOYearOfEraDateTimeField();
    }
}
