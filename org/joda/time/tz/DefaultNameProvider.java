// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.tz;

import org.joda.time.DateTimeUtils;
import java.util.Map;
import java.util.Locale;
import java.util.HashMap;

public class DefaultNameProvider implements NameProvider
{
    private HashMap<Locale, Map<String, Map<String, Object>>> iByLocaleCache;
    
    public DefaultNameProvider() {
        this.iByLocaleCache = (HashMap<Locale, Map<String, Map<String, Object>>>)this.createCache();
    }
    
    public String getShortName(final Locale locale, final String s, final String s2) {
        final String[] nameSet = this.getNameSet(locale, s, s2);
        return (nameSet == null) ? null : nameSet[0];
    }
    
    public String getName(final Locale locale, final String s, final String s2) {
        final String[] nameSet = this.getNameSet(locale, s, s2);
        return (nameSet == null) ? null : nameSet[1];
    }
    
    private synchronized String[] getNameSet(final Locale locale, final String s, final String s2) {
        if (locale == null || s == null || s2 == null) {
            return null;
        }
        Map<String, Map<String, Object>> cache = this.iByLocaleCache.get(locale);
        if (cache == null) {
            this.iByLocaleCache.put(locale, cache = (Map<String, Map<String, Object>>)this.createCache());
        }
        HashMap<String, String[]> cache2 = (HashMap<String, String[]>)cache.get(s);
        if (cache2 == null) {
            cache.put(s, (Map<String, Object>)(cache2 = (HashMap<String, String[]>)this.createCache()));
            final String[][] zoneStrings = DateTimeUtils.getDateFormatSymbols(Locale.ENGLISH).getZoneStrings();
            String[] array = null;
            for (final String[] array3 : zoneStrings) {
                if (array3 != null && array3.length == 5 && s.equals(array3[0])) {
                    array = array3;
                    break;
                }
            }
            final String[][] zoneStrings2 = DateTimeUtils.getDateFormatSymbols(locale).getZoneStrings();
            String[] array4 = null;
            for (final String[] array6 : zoneStrings2) {
                if (array6 != null && array6.length == 5 && s.equals(array6[0])) {
                    array4 = array6;
                    break;
                }
            }
            if (array != null && array4 != null) {
                cache2.put(array[2], new String[] { array4[2], array4[1] });
                if (array[2].equals(array[4])) {
                    cache2.put(array[4] + "-Summer", new String[] { array4[4], array4[3] });
                }
                else {
                    cache2.put(array[4], new String[] { array4[4], array4[3] });
                }
            }
        }
        return (String[])cache2.get(s2);
    }
    
    private HashMap createCache() {
        return new HashMap(7);
    }
}
