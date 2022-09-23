// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.asm;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormatSymbols;
import java.util.Comparator;
import java.util.TimeZone;
import java.util.HashSet;
import java.util.TreeMap;

public class ConvertDate
{
    static TreeMap<String, Integer> monthsTable;
    static TreeMap<String, Integer> daysTable;
    private static HashSet<String> voidData;
    static TreeMap<String, TimeZone> timeZoneMapping;
    
    static {
        ConvertDate.monthsTable = new TreeMap<String, Integer>(new StringCmpNS());
        ConvertDate.daysTable = new TreeMap<String, Integer>(new StringCmpNS());
        ConvertDate.voidData = new HashSet<String>();
        ConvertDate.timeZoneMapping = new TreeMap<String, TimeZone>();
        ConvertDate.voidData.add("MEZ");
        ConvertDate.voidData.add("Uhr");
        ConvertDate.voidData.add("h");
        ConvertDate.voidData.add("pm");
        ConvertDate.voidData.add("PM");
        ConvertDate.voidData.add("AM");
        ConvertDate.voidData.add("o'clock");
        String[] availableIDs;
        for (int length = (availableIDs = TimeZone.getAvailableIDs()).length, j = 0; j < length; ++j) {
            final String tz = availableIDs[j];
            ConvertDate.timeZoneMapping.put(tz, TimeZone.getTimeZone(tz));
        }
        Locale[] availableLocales;
        for (int length2 = (availableLocales = DateFormatSymbols.getAvailableLocales()).length, k = 0; k < length2; ++k) {
            final Locale locale = availableLocales[k];
            if (!"ja".equals(locale.getLanguage())) {
                if (!"ko".equals(locale.getLanguage())) {
                    if (!"zh".equals(locale.getLanguage())) {
                        final DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
                        String[] keys = dfs.getMonths();
                        for (int i = 0; i < keys.length; ++i) {
                            if (keys[i].length() != 0) {
                                fillMap(ConvertDate.monthsTable, keys[i], i);
                            }
                        }
                        keys = dfs.getShortMonths();
                        for (int i = 0; i < keys.length; ++i) {
                            final String s = keys[i];
                            if (s.length() != 0) {
                                if (!Character.isDigit(s.charAt(s.length() - 1))) {
                                    fillMap(ConvertDate.monthsTable, keys[i], i);
                                    fillMap(ConvertDate.monthsTable, keys[i].replace(".", ""), i);
                                }
                            }
                        }
                        keys = dfs.getWeekdays();
                        for (int i = 0; i < keys.length; ++i) {
                            final String s = keys[i];
                            if (s.length() != 0) {
                                fillMap(ConvertDate.daysTable, s, i);
                                fillMap(ConvertDate.daysTable, s.replace(".", ""), i);
                            }
                        }
                        keys = dfs.getShortWeekdays();
                        for (int i = 0; i < keys.length; ++i) {
                            final String s = keys[i];
                            if (s.length() != 0) {
                                fillMap(ConvertDate.daysTable, s, i);
                                fillMap(ConvertDate.daysTable, s.replace(".", ""), i);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static Integer getMonth(final String month) {
        return ConvertDate.monthsTable.get(month);
    }
    
    private static Integer parseMonth(final String s1) {
        if (Character.isDigit(s1.charAt(0))) {
            return Integer.parseInt(s1) - 1;
        }
        final Integer month = ConvertDate.monthsTable.get(s1);
        if (month == null) {
            throw new NullPointerException("can not parse " + s1 + " as month");
        }
        return month;
    }
    
    private static void fillMap(final TreeMap<String, Integer> map, String key, final Integer value) {
        map.put(key, value);
        key = key.replace("\u00e9", "e");
        key = key.replace("\u00fb", "u");
        map.put(key, value);
    }
    
    public static Date convertToDate(final Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date)obj;
        }
        if (obj instanceof Number) {
            return new Date(((Number)obj).longValue());
        }
        if (!(obj instanceof String)) {
            throw new RuntimeException("Primitive: Can not convert " + obj.getClass().getName() + " to int");
        }
        final StringTokenizer st = new StringTokenizer((String)obj, " -/:,.+");
        String s1 = "";
        if (!st.hasMoreTokens()) {
            return null;
        }
        s1 = st.nextToken();
        if (s1.length() == 4 && Character.isDigit(s1.charAt(0))) {
            return getYYYYMMDD(st, s1);
        }
        if (ConvertDate.daysTable.containsKey(s1)) {
            if (!st.hasMoreTokens()) {
                return null;
            }
            s1 = st.nextToken();
        }
        if (ConvertDate.monthsTable.containsKey(s1)) {
            return getMMDDYYYY(st, s1);
        }
        if (Character.isDigit(s1.charAt(0))) {
            return getDDMMYYYY(st, s1);
        }
        return null;
    }
    
    private static Date getYYYYMMDD(final StringTokenizer st, String s1) {
        final GregorianCalendar cal = new GregorianCalendar(2000, 0, 0, 0, 0, 0);
        cal.setTimeInMillis(0L);
        final int year = Integer.parseInt(s1);
        cal.set(1, year);
        if (!st.hasMoreTokens()) {
            return cal.getTime();
        }
        s1 = st.nextToken();
        cal.set(2, parseMonth(s1));
        if (!st.hasMoreTokens()) {
            return cal.getTime();
        }
        s1 = st.nextToken();
        if (!Character.isDigit(s1.charAt(0))) {
            return cal.getTime();
        }
        if (s1.length() == 5 && s1.charAt(2) == 'T') {
            final int day = Integer.parseInt(s1.substring(0, 2));
            cal.set(5, day);
            return addHour(st, cal, s1.substring(3));
        }
        final int day = Integer.parseInt(s1);
        cal.set(5, day);
        return addHour(st, cal, null);
    }
    
    private static int getYear(final String s1) {
        int year = Integer.parseInt(s1);
        if (year < 100) {
            if (year > 23) {
                year += 2000;
            }
            else {
                year += 1900;
            }
        }
        return year;
    }
    
    private static Date getMMDDYYYY(final StringTokenizer st, String s1) {
        final GregorianCalendar cal = new GregorianCalendar(2000, 0, 0, 0, 0, 0);
        final Integer month = ConvertDate.monthsTable.get(s1);
        if (month == null) {
            throw new NullPointerException("can not parse " + s1 + " as month");
        }
        cal.set(2, month);
        if (!st.hasMoreTokens()) {
            return null;
        }
        s1 = st.nextToken();
        final int day = Integer.parseInt(s1);
        cal.set(5, day);
        if (!st.hasMoreTokens()) {
            return null;
        }
        s1 = st.nextToken();
        if (Character.isLetter(s1.charAt(0))) {
            if (!st.hasMoreTokens()) {
                return null;
            }
            s1 = st.nextToken();
        }
        if (s1.length() == 4) {
            cal.set(1, getYear(s1));
        }
        else if (s1.length() == 2) {
            return addHour2(st, cal, s1);
        }
        return addHour(st, cal, null);
    }
    
    private static Date getDDMMYYYY(final StringTokenizer st, String s1) {
        final GregorianCalendar cal = new GregorianCalendar(2000, 0, 0, 0, 0, 0);
        final int day = Integer.parseInt(s1);
        cal.set(5, day);
        if (!st.hasMoreTokens()) {
            return null;
        }
        s1 = st.nextToken();
        cal.set(2, parseMonth(s1));
        if (!st.hasMoreTokens()) {
            return null;
        }
        s1 = st.nextToken();
        cal.set(1, getYear(s1));
        return addHour(st, cal, null);
    }
    
    private static Date addHour(final StringTokenizer st, final Calendar cal, String s1) {
        if (s1 == null) {
            if (!st.hasMoreTokens()) {
                return cal.getTime();
            }
            s1 = st.nextToken();
        }
        return addHour2(st, cal, s1);
    }
    
    private static Date addHour2(final StringTokenizer st, final Calendar cal, String s1) {
        cal.set(11, Integer.parseInt(s1));
        if (!st.hasMoreTokens()) {
            return cal.getTime();
        }
        s1 = st.nextToken();
        s1 = trySkip(st, s1, cal);
        if (s1 == null) {
            return cal.getTime();
        }
        cal.set(12, Integer.parseInt(s1));
        if (!st.hasMoreTokens()) {
            return cal.getTime();
        }
        s1 = st.nextToken();
        s1 = trySkip(st, s1, cal);
        if (s1 == null) {
            return cal.getTime();
        }
        cal.set(13, Integer.parseInt(s1));
        if (!st.hasMoreTokens()) {
            return cal.getTime();
        }
        s1 = st.nextToken();
        s1 = trySkip(st, s1, cal);
        if (s1 == null) {
            return cal.getTime();
        }
        s1 = trySkip(st, s1, cal);
        if (s1.length() == 4 && Character.isDigit(s1.charAt(0))) {
            cal.set(1, getYear(s1));
        }
        return cal.getTime();
    }
    
    private static String trySkip(final StringTokenizer st, String s1, final Calendar cal) {
        while (true) {
            final TimeZone tz = ConvertDate.timeZoneMapping.get(s1);
            if (tz != null) {
                cal.setTimeZone(tz);
                if (!st.hasMoreTokens()) {
                    return null;
                }
                s1 = st.nextToken();
            }
            else {
                if (!ConvertDate.voidData.contains(s1)) {
                    return s1;
                }
                if (s1.equalsIgnoreCase("pm")) {
                    cal.add(9, 1);
                }
                if (s1.equalsIgnoreCase("am")) {
                    cal.add(9, 0);
                }
                if (!st.hasMoreTokens()) {
                    return null;
                }
                s1 = st.nextToken();
            }
        }
    }
    
    public static class StringCmpNS implements Comparator<String>
    {
        @Override
        public int compare(final String o1, final String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }
}
