// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.field.UnsupportedDurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.DurationField;
import org.joda.time.DateTimeField;
import org.joda.time.field.FieldUtils;
import java.util.Locale;
import org.joda.time.DateTimeFieldType;
import org.joda.time.field.BaseDateTimeField;

final class GJEraDateTimeField extends BaseDateTimeField
{
    private static final long serialVersionUID = 4240986525305515528L;
    private final BasicChronology iChronology;
    
    GJEraDateTimeField(final BasicChronology iChronology) {
        super(DateTimeFieldType.era());
        this.iChronology = iChronology;
    }
    
    @Override
    public boolean isLenient() {
        return false;
    }
    
    @Override
    public int get(final long n) {
        if (this.iChronology.getYear(n) <= 0) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public String getAsText(final int n, final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).eraValueToText(n);
    }
    
    @Override
    public long set(final long n, final int n2) {
        FieldUtils.verifyValueBounds(this, n2, 0, 1);
        if (this.get(n) != n2) {
            return this.iChronology.setYear(n, -this.iChronology.getYear(n));
        }
        return n;
    }
    
    @Override
    public long set(final long n, final String s, final Locale locale) {
        return this.set(n, GJLocaleSymbols.forLocale(locale).eraTextToValue(s));
    }
    
    @Override
    public long roundFloor(final long n) {
        if (this.get(n) == 1) {
            return this.iChronology.setYear(0L, 1);
        }
        return Long.MIN_VALUE;
    }
    
    @Override
    public long roundCeiling(final long n) {
        if (this.get(n) == 0) {
            return this.iChronology.setYear(0L, 1);
        }
        return Long.MAX_VALUE;
    }
    
    @Override
    public long roundHalfFloor(final long n) {
        return this.roundFloor(n);
    }
    
    @Override
    public long roundHalfCeiling(final long n) {
        return this.roundFloor(n);
    }
    
    @Override
    public long roundHalfEven(final long n) {
        return this.roundFloor(n);
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
        return 0;
    }
    
    @Override
    public int getMaximumValue() {
        return 1;
    }
    
    @Override
    public int getMaximumTextLength(final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getEraMaxTextLength();
    }
    
    private Object readResolve() {
        return this.iChronology.era();
    }
}
