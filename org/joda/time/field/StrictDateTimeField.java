// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DateTimeField;

public class StrictDateTimeField extends DelegatedDateTimeField
{
    private static final long serialVersionUID = 3154803964207950910L;
    
    public static DateTimeField getInstance(DateTimeField wrappedField) {
        if (wrappedField == null) {
            return null;
        }
        if (wrappedField instanceof LenientDateTimeField) {
            wrappedField = ((LenientDateTimeField)wrappedField).getWrappedField();
        }
        if (!wrappedField.isLenient()) {
            return wrappedField;
        }
        return new StrictDateTimeField(wrappedField);
    }
    
    protected StrictDateTimeField(final DateTimeField dateTimeField) {
        super(dateTimeField);
    }
    
    @Override
    public final boolean isLenient() {
        return false;
    }
    
    @Override
    public long set(final long n, final int n2) {
        FieldUtils.verifyValueBounds(this, n2, this.getMinimumValue(n), this.getMaximumValue(n));
        return super.set(n, n2);
    }
}
