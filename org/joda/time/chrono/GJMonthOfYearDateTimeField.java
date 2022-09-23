// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import java.util.Locale;

final class GJMonthOfYearDateTimeField extends BasicMonthOfYearDateTimeField
{
    private static final long serialVersionUID = -4748157875845286249L;
    
    GJMonthOfYearDateTimeField(final BasicChronology basicChronology) {
        super(basicChronology, 2);
    }
    
    @Override
    public String getAsText(final int n, final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).monthOfYearValueToText(n);
    }
    
    @Override
    public String getAsShortText(final int n, final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).monthOfYearValueToShortText(n);
    }
    
    @Override
    protected int convertText(final String s, final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).monthOfYearTextToValue(s);
    }
    
    @Override
    public int getMaximumTextLength(final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getMonthMaxTextLength();
    }
    
    @Override
    public int getMaximumShortTextLength(final Locale locale) {
        return GJLocaleSymbols.forLocale(locale).getMonthMaxShortTextLength();
    }
}
