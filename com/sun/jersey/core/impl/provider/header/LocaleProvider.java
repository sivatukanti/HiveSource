// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.header;

import java.text.ParseException;
import com.sun.jersey.core.header.LanguageTag;
import java.util.Locale;
import com.sun.jersey.spi.HeaderDelegateProvider;

public class LocaleProvider implements HeaderDelegateProvider<Locale>
{
    @Override
    public boolean supports(final Class<?> type) {
        return Locale.class.isAssignableFrom(type);
    }
    
    @Override
    public String toString(final Locale header) {
        if (header.getCountry().length() == 0) {
            return header.getLanguage();
        }
        final StringBuilder sb = new StringBuilder(header.getLanguage());
        return sb.append('-').append(header.getCountry()).toString();
    }
    
    @Override
    public Locale fromString(final String header) {
        try {
            final LanguageTag lt = new LanguageTag(header);
            return lt.getAsLocale();
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Error parsing date '" + header + "'", ex);
        }
    }
}
