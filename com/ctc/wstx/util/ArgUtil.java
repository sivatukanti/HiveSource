// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

public final class ArgUtil
{
    private ArgUtil() {
    }
    
    public static boolean convertToBoolean(final String prop, final Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (boolean)value;
        }
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Invalid value type (" + value.getClass() + ") for property '" + prop + "': expected Boolean value.");
        }
        final String str = (String)value;
        if (str.equalsIgnoreCase("false")) {
            return false;
        }
        if (str.equalsIgnoreCase("true")) {
            return true;
        }
        throw new IllegalArgumentException("Invalid String value for property '" + prop + "': expected Boolean value.");
    }
    
    public static int convertToInt(final String prop, final Object value, final int minValue) {
        int i = 0;
        Label_0123: {
            if (value == null) {
                i = 0;
            }
            else {
                if (!(value instanceof Number)) {
                    if (value instanceof String) {
                        try {
                            i = Integer.parseInt((String)value);
                            break Label_0123;
                        }
                        catch (NumberFormatException nex) {
                            throw new IllegalArgumentException("Invalid String value for property '" + prop + "': expected a number (Integer).");
                        }
                    }
                    throw new IllegalArgumentException("Invalid value type (" + value.getClass() + ") for property '" + prop + "': expected Integer value.");
                }
                i = ((Number)value).intValue();
            }
        }
        if (i < minValue) {
            throw new IllegalArgumentException("Invalid numeric value (" + i + ") for property '" + prop + "': minimum is " + minValue + ".");
        }
        return i;
    }
    
    public static long convertToLong(final String prop, final Object value, final long minValue) {
        long i = 0L;
        Label_0126: {
            if (value == null) {
                i = 0L;
            }
            else {
                if (!(value instanceof Number)) {
                    if (value instanceof String) {
                        try {
                            i = Long.parseLong((String)value);
                            break Label_0126;
                        }
                        catch (NumberFormatException nex) {
                            throw new IllegalArgumentException("Invalid String value for property '" + prop + "': expected a number (Long).");
                        }
                    }
                    throw new IllegalArgumentException("Invalid value type (" + value.getClass() + ") for property '" + prop + "': expected Long value.");
                }
                i = ((Number)value).longValue();
            }
        }
        if (i < minValue) {
            throw new IllegalArgumentException("Invalid numeric value (" + i + ") for property '" + prop + "': minimum is " + minValue + ".");
        }
        return i;
    }
}
