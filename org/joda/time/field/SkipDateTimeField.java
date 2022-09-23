// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.IllegalFieldValueException;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeField;
import org.joda.time.Chronology;

public final class SkipDateTimeField extends DelegatedDateTimeField
{
    private static final long serialVersionUID = -8869148464118507846L;
    private final Chronology iChronology;
    private final int iSkip;
    private transient int iMinValue;
    
    public SkipDateTimeField(final Chronology chronology, final DateTimeField dateTimeField) {
        this(chronology, dateTimeField, 0);
    }
    
    public SkipDateTimeField(final Chronology iChronology, final DateTimeField dateTimeField, final int iSkip) {
        super(dateTimeField);
        this.iChronology = iChronology;
        final int minimumValue = super.getMinimumValue();
        if (minimumValue < iSkip) {
            this.iMinValue = minimumValue - 1;
        }
        else if (minimumValue == iSkip) {
            this.iMinValue = iSkip + 1;
        }
        else {
            this.iMinValue = minimumValue;
        }
        this.iSkip = iSkip;
    }
    
    @Override
    public int get(final long n) {
        int value = super.get(n);
        if (value <= this.iSkip) {
            --value;
        }
        return value;
    }
    
    @Override
    public long set(final long n, int i) {
        FieldUtils.verifyValueBounds(this, i, this.iMinValue, this.getMaximumValue());
        if (i <= this.iSkip) {
            if (i == this.iSkip) {
                throw new IllegalFieldValueException(DateTimeFieldType.year(), i, null, null);
            }
            ++i;
        }
        return super.set(n, i);
    }
    
    @Override
    public int getMinimumValue() {
        return this.iMinValue;
    }
    
    private Object readResolve() {
        return this.getType().getField(this.iChronology);
    }
}
