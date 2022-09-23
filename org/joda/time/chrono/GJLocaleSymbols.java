// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.DateTimeFieldType;
import java.text.DateFormatSymbols;
import java.util.Comparator;
import org.joda.time.DateTimeUtils;
import java.util.TreeMap;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

class GJLocaleSymbols
{
    private static ConcurrentMap<Locale, GJLocaleSymbols> cCache;
    private final String[] iEras;
    private final String[] iDaysOfWeek;
    private final String[] iShortDaysOfWeek;
    private final String[] iMonths;
    private final String[] iShortMonths;
    private final String[] iHalfday;
    private final TreeMap<String, Integer> iParseEras;
    private final TreeMap<String, Integer> iParseDaysOfWeek;
    private final TreeMap<String, Integer> iParseMonths;
    private final int iMaxEraLength;
    private final int iMaxDayOfWeekLength;
    private final int iMaxShortDayOfWeekLength;
    private final int iMaxMonthLength;
    private final int iMaxShortMonthLength;
    private final int iMaxHalfdayLength;
    
    static GJLocaleSymbols forLocale(Locale default1) {
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        GJLocaleSymbols gjLocaleSymbols = GJLocaleSymbols.cCache.get(default1);
        if (gjLocaleSymbols == null) {
            gjLocaleSymbols = new GJLocaleSymbols(default1);
            final GJLocaleSymbols gjLocaleSymbols2 = GJLocaleSymbols.cCache.putIfAbsent(default1, gjLocaleSymbols);
            if (gjLocaleSymbols2 != null) {
                gjLocaleSymbols = gjLocaleSymbols2;
            }
        }
        return gjLocaleSymbols;
    }
    
    private static String[] realignMonths(final String[] array) {
        final String[] array2 = new String[13];
        for (int i = 1; i < 13; ++i) {
            array2[i] = array[i - 1];
        }
        return array2;
    }
    
    private static String[] realignDaysOfWeek(final String[] array) {
        final String[] array2 = new String[8];
        for (int i = 1; i < 8; ++i) {
            array2[i] = array[(i < 7) ? (i + 1) : 1];
        }
        return array2;
    }
    
    private static void addSymbols(final TreeMap<String, Integer> treeMap, final String[] array, final Integer[] array2) {
        int length = array.length;
        while (--length >= 0) {
            final String key = array[length];
            if (key != null) {
                treeMap.put(key, array2[length]);
            }
        }
    }
    
    private static void addNumerals(final TreeMap<String, Integer> treeMap, final int n, final int n2, final Integer[] array) {
        for (int i = n; i <= n2; ++i) {
            treeMap.put(String.valueOf(i).intern(), array[i]);
        }
    }
    
    private static int maxLength(final String[] array) {
        int n = 0;
        int length = array.length;
        while (--length >= 0) {
            final String s = array[length];
            if (s != null) {
                final int length2 = s.length();
                if (length2 <= n) {
                    continue;
                }
                n = length2;
            }
        }
        return n;
    }
    
    private GJLocaleSymbols(final Locale locale) {
        final DateFormatSymbols dateFormatSymbols = DateTimeUtils.getDateFormatSymbols(locale);
        this.iEras = dateFormatSymbols.getEras();
        this.iDaysOfWeek = realignDaysOfWeek(dateFormatSymbols.getWeekdays());
        this.iShortDaysOfWeek = realignDaysOfWeek(dateFormatSymbols.getShortWeekdays());
        this.iMonths = realignMonths(dateFormatSymbols.getMonths());
        this.iShortMonths = realignMonths(dateFormatSymbols.getShortMonths());
        this.iHalfday = dateFormatSymbols.getAmPmStrings();
        final Integer[] array = new Integer[13];
        for (int i = 0; i < 13; ++i) {
            array[i] = i;
        }
        addSymbols(this.iParseEras = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER), this.iEras, array);
        if ("en".equals(locale.getLanguage())) {
            this.iParseEras.put("BCE", array[0]);
            this.iParseEras.put("CE", array[1]);
        }
        addSymbols(this.iParseDaysOfWeek = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER), this.iDaysOfWeek, array);
        addSymbols(this.iParseDaysOfWeek, this.iShortDaysOfWeek, array);
        addNumerals(this.iParseDaysOfWeek, 1, 7, array);
        addSymbols(this.iParseMonths = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER), this.iMonths, array);
        addSymbols(this.iParseMonths, this.iShortMonths, array);
        addNumerals(this.iParseMonths, 1, 12, array);
        this.iMaxEraLength = maxLength(this.iEras);
        this.iMaxDayOfWeekLength = maxLength(this.iDaysOfWeek);
        this.iMaxShortDayOfWeekLength = maxLength(this.iShortDaysOfWeek);
        this.iMaxMonthLength = maxLength(this.iMonths);
        this.iMaxShortMonthLength = maxLength(this.iShortMonths);
        this.iMaxHalfdayLength = maxLength(this.iHalfday);
    }
    
    public String eraValueToText(final int n) {
        return this.iEras[n];
    }
    
    public int eraTextToValue(final String key) {
        final Integer n = this.iParseEras.get(key);
        if (n != null) {
            return n;
        }
        throw new IllegalFieldValueException(DateTimeFieldType.era(), key);
    }
    
    public int getEraMaxTextLength() {
        return this.iMaxEraLength;
    }
    
    public String monthOfYearValueToText(final int n) {
        return this.iMonths[n];
    }
    
    public String monthOfYearValueToShortText(final int n) {
        return this.iShortMonths[n];
    }
    
    public int monthOfYearTextToValue(final String key) {
        final Integer n = this.iParseMonths.get(key);
        if (n != null) {
            return n;
        }
        throw new IllegalFieldValueException(DateTimeFieldType.monthOfYear(), key);
    }
    
    public int getMonthMaxTextLength() {
        return this.iMaxMonthLength;
    }
    
    public int getMonthMaxShortTextLength() {
        return this.iMaxShortMonthLength;
    }
    
    public String dayOfWeekValueToText(final int n) {
        return this.iDaysOfWeek[n];
    }
    
    public String dayOfWeekValueToShortText(final int n) {
        return this.iShortDaysOfWeek[n];
    }
    
    public int dayOfWeekTextToValue(final String key) {
        final Integer n = this.iParseDaysOfWeek.get(key);
        if (n != null) {
            return n;
        }
        throw new IllegalFieldValueException(DateTimeFieldType.dayOfWeek(), key);
    }
    
    public int getDayOfWeekMaxTextLength() {
        return this.iMaxDayOfWeekLength;
    }
    
    public int getDayOfWeekMaxShortTextLength() {
        return this.iMaxShortDayOfWeekLength;
    }
    
    public String halfdayValueToText(final int n) {
        return this.iHalfday[n];
    }
    
    public int halfdayTextToValue(final String anotherString) {
        final String[] iHalfday = this.iHalfday;
        int length = iHalfday.length;
        while (--length >= 0) {
            if (iHalfday[length].equalsIgnoreCase(anotherString)) {
                return length;
            }
        }
        throw new IllegalFieldValueException(DateTimeFieldType.halfdayOfDay(), anotherString);
    }
    
    public int getHalfdayMaxTextLength() {
        return this.iMaxHalfdayLength;
    }
    
    static {
        GJLocaleSymbols.cCache = new ConcurrentHashMap<Locale, GJLocaleSymbols>();
    }
}
