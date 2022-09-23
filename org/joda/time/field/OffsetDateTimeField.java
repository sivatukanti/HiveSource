// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DurationField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeField;

public class OffsetDateTimeField extends DecoratedDateTimeField
{
    private static final long serialVersionUID = 3145790132623583142L;
    private final int iOffset;
    private final int iMin;
    private final int iMax;
    
    public OffsetDateTimeField(final DateTimeField dateTimeField, final int n) {
        this(dateTimeField, (dateTimeField == null) ? null : dateTimeField.getType(), n, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    public OffsetDateTimeField(final DateTimeField dateTimeField, final DateTimeFieldType dateTimeFieldType, final int n) {
        this(dateTimeField, dateTimeFieldType, n, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    public OffsetDateTimeField(final DateTimeField dateTimeField, final DateTimeFieldType dateTimeFieldType, final int iOffset, final int iMin, final int iMax) {
        super(dateTimeField, dateTimeFieldType);
        if (iOffset == 0) {
            throw new IllegalArgumentException("The offset cannot be zero");
        }
        this.iOffset = iOffset;
        if (iMin < dateTimeField.getMinimumValue() + iOffset) {
            this.iMin = dateTimeField.getMinimumValue() + iOffset;
        }
        else {
            this.iMin = iMin;
        }
        if (iMax > dateTimeField.getMaximumValue() + iOffset) {
            this.iMax = dateTimeField.getMaximumValue() + iOffset;
        }
        else {
            this.iMax = iMax;
        }
    }
    
    @Override
    public int get(final long n) {
        return super.get(n) + this.iOffset;
    }
    
    @Override
    public long add(long add, final int n) {
        add = super.add(add, n);
        FieldUtils.verifyValueBounds(this, this.get(add), this.iMin, this.iMax);
        return add;
    }
    
    @Override
    public long add(long add, final long n) {
        add = super.add(add, n);
        FieldUtils.verifyValueBounds(this, this.get(add), this.iMin, this.iMax);
        return add;
    }
    
    @Override
    public long addWrapField(final long n, final int n2) {
        return this.set(n, FieldUtils.getWrappedValue(this.get(n), n2, this.iMin, this.iMax));
    }
    
    @Override
    public long set(final long n, final int n2) {
        FieldUtils.verifyValueBounds(this, n2, this.iMin, this.iMax);
        return super.set(n, n2 - this.iOffset);
    }
    
    @Override
    public boolean isLeap(final long n) {
        return this.getWrappedField().isLeap(n);
    }
    
    @Override
    public int getLeapAmount(final long n) {
        return this.getWrappedField().getLeapAmount(n);
    }
    
    @Override
    public DurationField getLeapDurationField() {
        return this.getWrappedField().getLeapDurationField();
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
    
    public int getOffset() {
        return this.iOffset;
    }
}
