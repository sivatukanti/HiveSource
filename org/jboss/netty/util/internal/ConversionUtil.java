// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

public final class ConversionUtil
{
    private static final Pattern ARRAY_DELIM;
    private static final String[] INTEGERS;
    
    public static int toInt(final Object value) {
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
    
    public static boolean toBoolean(final Object value) {
        if (value instanceof Boolean) {
            return (boolean)value;
        }
        if (value instanceof Number) {
            return ((Number)value).intValue() != 0;
        }
        final String s = String.valueOf(value);
        if (s.length() == 0) {
            return false;
        }
        try {
            return Integer.parseInt(s) != 0;
        }
        catch (NumberFormatException e) {
            switch (Character.toUpperCase(s.charAt(0))) {
                case 'T':
                case 'Y': {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
    }
    
    public static String[] toStringArray(final Object value) {
        if (value instanceof String[]) {
            return (String[])value;
        }
        if (value instanceof Iterable) {
            final List<String> answer = new ArrayList<String>();
            for (final Object v : (Iterable)value) {
                if (v == null) {
                    answer.add(null);
                }
                else {
                    answer.add(String.valueOf(v));
                }
            }
            return answer.toArray(new String[answer.size()]);
        }
        return ConversionUtil.ARRAY_DELIM.split(String.valueOf(value));
    }
    
    public static String toString(final int value) {
        if (value >= 0 && value < ConversionUtil.INTEGERS.length) {
            return ConversionUtil.INTEGERS[value];
        }
        return Integer.toString(value);
    }
    
    private ConversionUtil() {
    }
    
    static {
        ARRAY_DELIM = Pattern.compile("[, \\t\\n\\r\\f\\e\\a]");
        INTEGERS = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" };
    }
}
