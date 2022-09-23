// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.util.Locale;

public final class I18nUtils
{
    private I18nUtils() {
    }
    
    public static Locale getLocaleFromString(String localeString) {
        if (localeString == null) {
            return null;
        }
        localeString = localeString.trim();
        if (localeString.toLowerCase().equals("default")) {
            return Locale.getDefault();
        }
        final int languageIndex = localeString.indexOf(95);
        String language = null;
        if (languageIndex == -1) {
            return new Locale(localeString, "");
        }
        language = localeString.substring(0, languageIndex);
        final int countryIndex = localeString.indexOf(95, languageIndex + 1);
        String country = null;
        if (countryIndex == -1) {
            country = localeString.substring(languageIndex + 1);
            return new Locale(language, country);
        }
        country = localeString.substring(languageIndex + 1, countryIndex);
        final String variant = localeString.substring(countryIndex + 1);
        return new Locale(language, country, variant);
    }
}
