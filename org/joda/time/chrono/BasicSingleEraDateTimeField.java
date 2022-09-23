// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.field.UnsupportedDurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.DurationField;
import org.joda.time.IllegalFieldValueException;
import java.util.Locale;
import org.joda.time.DateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.DateTimeFieldType;
import org.joda.time.field.BaseDateTimeField;

final class BasicSingleEraDateTimeField extends BaseDateTimeField
{
    private static final int ERA_VALUE = 1;
    private final String iEraText;
    
    BasicSingleEraDateTimeField(final String iEraText) {
        super(DateTimeFieldType.era());
        this.iEraText = iEraText;
    }
    
    @Override
    public boolean isLenient() {
        return false;
    }
    
    @Override
    public int get(final long n) {
        return 1;
    }
    
    @Override
    public long set(final long n, final int n2) {
        FieldUtils.verifyValueBounds(this, n2, 1, 1);
        return n;
    }
    
    @Override
    public long set(final long n, final String s, final Locale locale) {
        if (!this.iEraText.equals(s) && !"1".equals(s)) {
            throw new IllegalFieldValueException(DateTimeFieldType.era(), s);
        }
        return n;
    }
    
    @Override
    public long roundFloor(final long n) {
        return Long.MIN_VALUE;
    }
    
    @Override
    public long roundCeiling(final long n) {
        return Long.MAX_VALUE;
    }
    
    @Override
    public long roundHalfFloor(final long n) {
        return Long.MIN_VALUE;
    }
    
    @Override
    public long roundHalfCeiling(final long n) {
        return Long.MIN_VALUE;
    }
    
    @Override
    public long roundHalfEven(final long n) {
        return Long.MIN_VALUE;
    }
    
    @Override
    public DurationField getDurationField() {
        return UnsupportedDurationField.getInstance(DurationFieldType.eras());
    }
    
    @Override
    public DurationField getRangeDurationField() {
        return null;
    }
    
    @Override
    public int getMinimumValue() {
        return 1;
    }
    
    @Override
    public int getMaximumValue() {
        return 1;
    }
    
    @Override
    public String getAsText(final int n, final Locale locale) {
        return this.iEraText;
    }
    
    @Override
    public int getMaximumTextLength(final Locale locale) {
        return this.iEraText.length();
    }
}
