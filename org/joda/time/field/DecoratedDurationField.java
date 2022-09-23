// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DurationFieldType;
import org.joda.time.DurationField;

public class DecoratedDurationField extends BaseDurationField
{
    private static final long serialVersionUID = 8019982251647420015L;
    private final DurationField iField;
    
    public DecoratedDurationField(final DurationField iField, final DurationFieldType durationFieldType) {
        super(durationFieldType);
        if (iField == null) {
            throw new IllegalArgumentException("The field must not be null");
        }
        if (!iField.isSupported()) {
            throw new IllegalArgumentException("The field must be supported");
        }
        this.iField = iField;
    }
    
    public final DurationField getWrappedField() {
        return this.iField;
    }
    
    @Override
    public boolean isPrecise() {
        return this.iField.isPrecise();
    }
    
    @Override
    public long getValueAsLong(final long n, final long n2) {
        return this.iField.getValueAsLong(n, n2);
    }
    
    @Override
    public long getMillis(final int n, final long n2) {
        return this.iField.getMillis(n, n2);
    }
    
    @Override
    public long getMillis(final long n, final long n2) {
        return this.iField.getMillis(n, n2);
    }
    
    @Override
    public long add(final long n, final int n2) {
        return this.iField.add(n, n2);
    }
    
    @Override
    public long add(final long n, final long n2) {
        return this.iField.add(n, n2);
    }
    
    @Override
    public long getDifferenceAsLong(final long n, final long n2) {
        return this.iField.getDifferenceAsLong(n, n2);
    }
    
    @Override
    public long getUnitMillis() {
        return this.iField.getUnitMillis();
    }
}
