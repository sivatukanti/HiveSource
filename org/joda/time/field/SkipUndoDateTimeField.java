// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DateTimeField;
import org.joda.time.Chronology;

public final class SkipUndoDateTimeField extends DelegatedDateTimeField
{
    private static final long serialVersionUID = -5875876968979L;
    private final Chronology iChronology;
    private final int iSkip;
    private transient int iMinValue;
    
    public SkipUndoDateTimeField(final Chronology chronology, final DateTimeField dateTimeField) {
        this(chronology, dateTimeField, 0);
    }
    
    public SkipUndoDateTimeField(final Chronology iChronology, final DateTimeField dateTimeField, final int n) {
        super(dateTimeField);
        this.iChronology = iChronology;
        final int minimumValue = super.getMinimumValue();
        if (minimumValue < n) {
            this.iMinValue = minimumValue + 1;
        }
        else if (minimumValue == n + 1) {
            this.iMinValue = n;
        }
        else {
            this.iMinValue = minimumValue;
        }
        this.iSkip = n;
    }
    
    @Override
    public int get(final long n) {
        int value = super.get(n);
        if (value < this.iSkip) {
            ++value;
        }
        return value;
    }
    
    @Override
    public long set(final long n, int n2) {
        FieldUtils.verifyValueBounds(this, n2, this.iMinValue, this.getMaximumValue());
        if (n2 <= this.iSkip) {
            --n2;
        }
        return super.set(n, n2);
    }
    
    @Override
    public int getMinimumValue() {
        return this.iMinValue;
    }
    
    private Object readResolve() {
        return this.getType().getField(this.iChronology);
    }
}
