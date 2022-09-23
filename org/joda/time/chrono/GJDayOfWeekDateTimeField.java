// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import java.util.Locale;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import org.joda.time.field.PreciseDurationDateTimeField;

final class GJDayOfWeekDateTimeField extends PreciseDurationDateTimeField
{
    private static final long serialVersionUID = -3857947176719041436L;
    private final BasicChronology iChronology;
    
    GJDayOfWeekDateTimeField(final BasicChronology iChronology, final DurationField durationField) {
        super(DateTimeFieldType.dayOfWeek(), durationField);
        this.iChronology = iChronology;
    }
    
    @Override
    public int get(final long n) {
        return this.iChronology.getDayOfWeek(n);
    }
    
    @Override
    public String getAsText(final int n, final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).dayOfWeekValueToText(n);
    }
    
    @Override
    public String getAsShortText(final int n, final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).dayOfWeekValueToShortText(n);
    }
    
    @Override
    protected int convertText(final String s, final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).dayOfWeekTextToValue(s);
    }
    
    @Override
    public DurationField getRangeDurationField() {
        return this.iChronology.weeks();
    }
    
    @Override
    public int getMinimumValue() {
        return 1;
    }
    
    @Override
    public int getMaximumValue() {
        return 7;
    }
    
    @Override
    public int getMaximumTextLength(final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getDayOfWeekMaxTextLength();
    }
    
    @Override
    public int getMaximumShortTextLength(final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getDayOfWeekMaxShortTextLength();
    }
    
    private Object readResolve() {
        return this.iChronology.dayOfWeek();
    }
}
