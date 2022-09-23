// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeField;
import org.joda.time.DurationField;

public class RemainderDateTimeField extends DecoratedDateTimeField
{
    private static final long serialVersionUID = 5708241235177666790L;
    final int iDivisor;
    final DurationField iDurationField;
    final DurationField iRangeField;
    
    public RemainderDateTimeField(final DateTimeField dateTimeField, final DateTimeFieldType dateTimeFieldType, final int iDivisor) {
        super(dateTimeField, dateTimeFieldType);
        if (iDivisor < 2) {
            throw new IllegalArgumentException("The divisor must be at least 2");
        }
        final DurationField durationField = dateTimeField.getDurationField();
        if (durationField == null) {
            this.iRangeField = null;
        }
        else {
            this.iRangeField = new ScaledDurationField(durationField, dateTimeFieldType.getRangeDurationType(), iDivisor);
        }
        this.iDurationField = dateTimeField.getDurationField();
        this.iDivisor = iDivisor;
    }
    
    public RemainderDateTimeField(final DateTimeField dateTimeField, final DurationField iRangeField, final DateTimeFieldType dateTimeFieldType, final int iDivisor) {
        super(dateTimeField, dateTimeFieldType);
        if (iDivisor < 2) {
            throw new IllegalArgumentException("The divisor must be at least 2");
        }
        this.iRangeField = iRangeField;
        this.iDurationField = dateTimeField.getDurationField();
        this.iDivisor = iDivisor;
    }
    
    public RemainderDateTimeField(final DividedDateTimeField dividedDateTimeField) {
        this(dividedDateTimeField, dividedDateTimeField.getType());
    }
    
    public RemainderDateTimeField(final DividedDateTimeField dividedDateTimeField, final DateTimeFieldType dateTimeFieldType) {
        this(dividedDateTimeField, dividedDateTimeField.getWrappedField().getDurationField(), dateTimeFieldType);
    }
    
    public RemainderDateTimeField(final DividedDateTimeField dividedDateTimeField, final DurationField iDurationField, final DateTimeFieldType dateTimeFieldType) {
        super(dividedDateTimeField.getWrappedField(), dateTimeFieldType);
        this.iDivisor = dividedDateTimeField.iDivisor;
        this.iDurationField = iDurationField;
        this.iRangeField = dividedDateTimeField.iDurationField;
    }
    
    @Override
    public int get(final long n) {
        final int value = this.getWrappedField().get(n);
        if (value >= 0) {
            return value % this.iDivisor;
        }
        return this.iDivisor - 1 + (value + 1) % this.iDivisor;
    }
    
    @Override
    public long addWrapField(final long n, final int n2) {
        return this.set(n, FieldUtils.getWrappedValue(this.get(n), n2, 0, this.iDivisor - 1));
    }
    
    @Override
    public long set(final long n, final int n2) {
        FieldUtils.verifyValueBounds(this, n2, 0, this.iDivisor - 1);
        return this.getWrappedField().set(n, this.getDivided(this.getWrappedField().get(n)) * this.iDivisor + n2);
    }
    
    @Override
    public DurationField getDurationField() {
        return this.iDurationField;
    }
    
    @Override
    public DurationField getRangeDurationField() {
        return this.iRangeField;
    }
    
    @Override
    public int getMinimumValue() {
        return 0;
    }
    
    @Override
    public int getMaximumValue() {
        return this.iDivisor - 1;
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
    public long roundHalfFloor(final long n) {
        return this.getWrappedField().roundHalfFloor(n);
    }
    
    @Override
    public long roundHalfCeiling(final long n) {
        return this.getWrappedField().roundHalfCeiling(n);
    }
    
    @Override
    public long roundHalfEven(final long n) {
        return this.getWrappedField().roundHalfEven(n);
    }
    
    @Override
    public long remainder(final long n) {
        return this.getWrappedField().remainder(n);
    }
    
    public int getDivisor() {
        return this.iDivisor;
    }
    
    private int getDivided(final int n) {
        if (n >= 0) {
            return n / this.iDivisor;
        }
        return (n + 1) / this.iDivisor - 1;
    }
}
