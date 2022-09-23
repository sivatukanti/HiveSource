// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeField;
import org.joda.time.DurationField;

public class DividedDateTimeField extends DecoratedDateTimeField
{
    private static final long serialVersionUID = 8318475124230605365L;
    final int iDivisor;
    final DurationField iDurationField;
    final DurationField iRangeDurationField;
    private final int iMin;
    private final int iMax;
    
    public DividedDateTimeField(final DateTimeField dateTimeField, final DateTimeFieldType dateTimeFieldType, final int n) {
        this(dateTimeField, dateTimeField.getRangeDurationField(), dateTimeFieldType, n);
    }
    
    public DividedDateTimeField(final DateTimeField dateTimeField, final DurationField iRangeDurationField, final DateTimeFieldType dateTimeFieldType, final int iDivisor) {
        super(dateTimeField, dateTimeFieldType);
        if (iDivisor < 2) {
            throw new IllegalArgumentException("The divisor must be at least 2");
        }
        final DurationField durationField = dateTimeField.getDurationField();
        if (durationField == null) {
            this.iDurationField = null;
        }
        else {
            this.iDurationField = new ScaledDurationField(durationField, dateTimeFieldType.getDurationType(), iDivisor);
        }
        this.iRangeDurationField = iRangeDurationField;
        this.iDivisor = iDivisor;
        final int minimumValue = dateTimeField.getMinimumValue();
        final int iMin = (minimumValue >= 0) ? (minimumValue / iDivisor) : ((minimumValue + 1) / iDivisor - 1);
        final int maximumValue = dateTimeField.getMaximumValue();
        final int iMax = (maximumValue >= 0) ? (maximumValue / iDivisor) : ((maximumValue + 1) / iDivisor - 1);
        this.iMin = iMin;
        this.iMax = iMax;
    }
    
    public DividedDateTimeField(final RemainderDateTimeField remainderDateTimeField, final DateTimeFieldType dateTimeFieldType) {
        this(remainderDateTimeField, null, dateTimeFieldType);
    }
    
    public DividedDateTimeField(final RemainderDateTimeField remainderDateTimeField, final DurationField iRangeDurationField, final DateTimeFieldType dateTimeFieldType) {
        super(remainderDateTimeField.getWrappedField(), dateTimeFieldType);
        final int iDivisor = remainderDateTimeField.iDivisor;
        this.iDivisor = iDivisor;
        final int n = iDivisor;
        this.iDurationField = remainderDateTimeField.iRangeField;
        this.iRangeDurationField = iRangeDurationField;
        final DateTimeField wrappedField = this.getWrappedField();
        final int minimumValue = wrappedField.getMinimumValue();
        final int iMin = (minimumValue >= 0) ? (minimumValue / n) : ((minimumValue + 1) / n - 1);
        final int maximumValue = wrappedField.getMaximumValue();
        final int iMax = (maximumValue >= 0) ? (maximumValue / n) : ((maximumValue + 1) / n - 1);
        this.iMin = iMin;
        this.iMax = iMax;
    }
    
    @Override
    public DurationField getRangeDurationField() {
        if (this.iRangeDurationField != null) {
            return this.iRangeDurationField;
        }
        return super.getRangeDurationField();
    }
    
    @Override
    public int get(final long n) {
        final int value = this.getWrappedField().get(n);
        if (value >= 0) {
            return value / this.iDivisor;
        }
        return (value + 1) / this.iDivisor - 1;
    }
    
    @Override
    public long add(final long n, final int n2) {
        return this.getWrappedField().add(n, n2 * this.iDivisor);
    }
    
    @Override
    public long add(final long n, final long n2) {
        return this.getWrappedField().add(n, n2 * this.iDivisor);
    }
    
    @Override
    public long addWrapField(final long n, final int n2) {
        return this.set(n, FieldUtils.getWrappedValue(this.get(n), n2, this.iMin, this.iMax));
    }
    
    @Override
    public int getDifference(final long n, final long n2) {
        return this.getWrappedField().getDifference(n, n2) / this.iDivisor;
    }
    
    @Override
    public long getDifferenceAsLong(final long n, final long n2) {
        return this.getWrappedField().getDifferenceAsLong(n, n2) / this.iDivisor;
    }
    
    @Override
    public long set(final long n, final int n2) {
        FieldUtils.verifyValueBounds(this, n2, this.iMin, this.iMax);
        return this.getWrappedField().set(n, n2 * this.iDivisor + this.getRemainder(this.getWrappedField().get(n)));
    }
    
    @Override
    public DurationField getDurationField() {
        return this.iDurationField;
    }
    
    @Override
    public int getMinimumValue() {
        return this.iMin;
    }
    
    @Override
    public int getMaximumValue() {
        return this.iMax;
    }
    
    @Override
    public long roundFloor(final long n) {
        final DateTimeField wrappedField = this.getWrappedField();
        return wrappedField.roundFloor(wrappedField.set(n, this.get(n) * this.iDivisor));
    }
    
    @Override
    public long remainder(final long n) {
        return this.set(n, this.get(this.getWrappedField().remainder(n)));
    }
    
    public int getDivisor() {
        return this.iDivisor;
    }
    
    private int getRemainder(final int n) {
        if (n >= 0) {
            return n % this.iDivisor;
        }
        return this.iDivisor - 1 + (n + 1) % this.iDivisor;
    }
}
