// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core.io;

public final class NumberOutput
{
    private static final char NC = '\0';
    private static int MILLION;
    private static int BILLION;
    private static long TEN_BILLION_L;
    private static long THOUSAND_L;
    private static long MIN_INT_AS_LONG;
    private static long MAX_INT_AS_LONG;
    static final String SMALLEST_LONG;
    static final char[] LEAD_3;
    static final char[] FULL_3;
    static final byte[] FULL_TRIPLETS_B;
    static final String[] sSmallIntStrs;
    static final String[] sSmallIntStrs2;
    
    public static int outputInt(int v, final char[] b, int off) {
        if (v < 0) {
            if (v == Integer.MIN_VALUE) {
                return outputLong(v, b, off);
            }
            b[off++] = '-';
            v = -v;
        }
        if (v < NumberOutput.MILLION) {
            if (v < 1000) {
                if (v < 10) {
                    b[off++] = (char)(48 + v);
                }
                else {
                    off = leading3(v, b, off);
                }
            }
            else {
                final int thousands = v / 1000;
                v -= thousands * 1000;
                off = leading3(thousands, b, off);
                off = full3(v, b, off);
            }
            return off;
        }
        final boolean hasBillions = v >= NumberOutput.BILLION;
        if (hasBillions) {
            v -= NumberOutput.BILLION;
            if (v >= NumberOutput.BILLION) {
                v -= NumberOutput.BILLION;
                b[off++] = '2';
            }
            else {
                b[off++] = '1';
            }
        }
        int newValue = v / 1000;
        final int ones = v - newValue * 1000;
        v = newValue;
        newValue /= 1000;
        final int thousands2 = v - newValue * 1000;
        if (hasBillions) {
            off = full3(newValue, b, off);
        }
        else {
            off = leading3(newValue, b, off);
        }
        off = full3(thousands2, b, off);
        off = full3(ones, b, off);
        return off;
    }
    
    public static int outputInt(int v, final byte[] b, int off) {
        if (v < 0) {
            if (v == Integer.MIN_VALUE) {
                return outputLong(v, b, off);
            }
            b[off++] = 45;
            v = -v;
        }
        if (v < NumberOutput.MILLION) {
            if (v < 1000) {
                if (v < 10) {
                    b[off++] = (byte)(48 + v);
                }
                else {
                    off = leading3(v, b, off);
                }
            }
            else {
                final int thousands = v / 1000;
                v -= thousands * 1000;
                off = leading3(thousands, b, off);
                off = full3(v, b, off);
            }
            return off;
        }
        final boolean hasB = v >= NumberOutput.BILLION;
        if (hasB) {
            v -= NumberOutput.BILLION;
            if (v >= NumberOutput.BILLION) {
                v -= NumberOutput.BILLION;
                b[off++] = 50;
            }
            else {
                b[off++] = 49;
            }
        }
        int newValue = v / 1000;
        final int ones = v - newValue * 1000;
        v = newValue;
        newValue /= 1000;
        final int thousands2 = v - newValue * 1000;
        if (hasB) {
            off = full3(newValue, b, off);
        }
        else {
            off = leading3(newValue, b, off);
        }
        off = full3(thousands2, b, off);
        off = full3(ones, b, off);
        return off;
    }
    
    public static int outputLong(long v, final char[] b, int off) {
        if (v < 0L) {
            if (v > NumberOutput.MIN_INT_AS_LONG) {
                return outputInt((int)v, b, off);
            }
            if (v == Long.MIN_VALUE) {
                final int len = NumberOutput.SMALLEST_LONG.length();
                NumberOutput.SMALLEST_LONG.getChars(0, len, b, off);
                return off + len;
            }
            b[off++] = '-';
            v = -v;
        }
        else if (v <= NumberOutput.MAX_INT_AS_LONG) {
            return outputInt((int)v, b, off);
        }
        final int origOffset = off;
        int ptr;
        off = (ptr = off + calcLongStrLength(v));
        while (v > NumberOutput.MAX_INT_AS_LONG) {
            ptr -= 3;
            final long newValue = v / NumberOutput.THOUSAND_L;
            final int triplet = (int)(v - newValue * NumberOutput.THOUSAND_L);
            full3(triplet, b, ptr);
            v = newValue;
        }
        int ivalue;
        int newValue2;
        for (ivalue = (int)v; ivalue >= 1000; ivalue = newValue2) {
            ptr -= 3;
            newValue2 = ivalue / 1000;
            final int triplet = ivalue - newValue2 * 1000;
            full3(triplet, b, ptr);
        }
        leading3(ivalue, b, origOffset);
        return off;
    }
    
    public static int outputLong(long v, final byte[] b, int off) {
        if (v < 0L) {
            if (v > NumberOutput.MIN_INT_AS_LONG) {
                return outputInt((int)v, b, off);
            }
            if (v == Long.MIN_VALUE) {
                for (int len = NumberOutput.SMALLEST_LONG.length(), i = 0; i < len; ++i) {
                    b[off++] = (byte)NumberOutput.SMALLEST_LONG.charAt(i);
                }
                return off;
            }
            b[off++] = 45;
            v = -v;
        }
        else if (v <= NumberOutput.MAX_INT_AS_LONG) {
            return outputInt((int)v, b, off);
        }
        final int origOff = off;
        int ptr;
        off = (ptr = off + calcLongStrLength(v));
        while (v > NumberOutput.MAX_INT_AS_LONG) {
            ptr -= 3;
            final long newV = v / NumberOutput.THOUSAND_L;
            final int t = (int)(v - newV * NumberOutput.THOUSAND_L);
            full3(t, b, ptr);
            v = newV;
        }
        int ivalue;
        int newV2;
        for (ivalue = (int)v; ivalue >= 1000; ivalue = newV2) {
            ptr -= 3;
            newV2 = ivalue / 1000;
            final int t = ivalue - newV2 * 1000;
            full3(t, b, ptr);
        }
        leading3(ivalue, b, origOff);
        return off;
    }
    
    public static String toString(final int v) {
        if (v < NumberOutput.sSmallIntStrs.length) {
            if (v >= 0) {
                return NumberOutput.sSmallIntStrs[v];
            }
            final int v2 = -v - 1;
            if (v2 < NumberOutput.sSmallIntStrs2.length) {
                return NumberOutput.sSmallIntStrs2[v2];
            }
        }
        return Integer.toString(v);
    }
    
    public static String toString(final long v) {
        if (v <= 2147483647L && v >= -2147483648L) {
            return toString((int)v);
        }
        return Long.toString(v);
    }
    
    public static String toString(final double v) {
        return Double.toString(v);
    }
    
    private static int leading3(final int t, final char[] b, int off) {
        int digitOffset = t << 2;
        char c = NumberOutput.LEAD_3[digitOffset++];
        if (c != '\0') {
            b[off++] = c;
        }
        c = NumberOutput.LEAD_3[digitOffset++];
        if (c != '\0') {
            b[off++] = c;
        }
        b[off++] = NumberOutput.LEAD_3[digitOffset];
        return off;
    }
    
    private static int leading3(final int t, final byte[] b, int off) {
        int digitOffset = t << 2;
        char c = NumberOutput.LEAD_3[digitOffset++];
        if (c != '\0') {
            b[off++] = (byte)c;
        }
        c = NumberOutput.LEAD_3[digitOffset++];
        if (c != '\0') {
            b[off++] = (byte)c;
        }
        b[off++] = (byte)NumberOutput.LEAD_3[digitOffset];
        return off;
    }
    
    private static int full3(final int t, final char[] b, int off) {
        int digitOffset = t << 2;
        b[off++] = NumberOutput.FULL_3[digitOffset++];
        b[off++] = NumberOutput.FULL_3[digitOffset++];
        b[off++] = NumberOutput.FULL_3[digitOffset];
        return off;
    }
    
    private static int full3(final int t, final byte[] b, int off) {
        int digitOffset = t << 2;
        b[off++] = NumberOutput.FULL_TRIPLETS_B[digitOffset++];
        b[off++] = NumberOutput.FULL_TRIPLETS_B[digitOffset++];
        b[off++] = NumberOutput.FULL_TRIPLETS_B[digitOffset];
        return off;
    }
    
    private static int calcLongStrLength(final long v) {
        int len = 10;
        for (long cmp = NumberOutput.TEN_BILLION_L; v >= cmp && len != 19; ++len, cmp = (cmp << 3) + (cmp << 1)) {}
        return len;
    }
    
    static {
        NumberOutput.MILLION = 1000000;
        NumberOutput.BILLION = 1000000000;
        NumberOutput.TEN_BILLION_L = 10000000000L;
        NumberOutput.THOUSAND_L = 1000L;
        NumberOutput.MIN_INT_AS_LONG = -2147483648L;
        NumberOutput.MAX_INT_AS_LONG = 2147483647L;
        SMALLEST_LONG = String.valueOf(Long.MIN_VALUE);
        LEAD_3 = new char[4000];
        FULL_3 = new char[4000];
        int ix = 0;
        for (int i1 = 0; i1 < 10; ++i1) {
            final char f1 = (char)(48 + i1);
            final char l1 = (i1 == 0) ? '\0' : f1;
            for (int i2 = 0; i2 < 10; ++i2) {
                final char f2 = (char)(48 + i2);
                final char l2 = (i1 == 0 && i2 == 0) ? '\0' : f2;
                for (int i3 = 0; i3 < 10; ++i3) {
                    final char f3 = (char)(48 + i3);
                    NumberOutput.LEAD_3[ix] = l1;
                    NumberOutput.LEAD_3[ix + 1] = l2;
                    NumberOutput.LEAD_3[ix + 2] = f3;
                    NumberOutput.FULL_3[ix] = f1;
                    NumberOutput.FULL_3[ix + 1] = f2;
                    NumberOutput.FULL_3[ix + 2] = f3;
                    ix += 4;
                }
            }
        }
        FULL_TRIPLETS_B = new byte[4000];
        for (int j = 0; j < 4000; ++j) {
            NumberOutput.FULL_TRIPLETS_B[j] = (byte)NumberOutput.FULL_3[j];
        }
        sSmallIntStrs = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        sSmallIntStrs2 = new String[] { "-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9", "-10" };
    }
}
