// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.typed;

public final class NumberUtil
{
    public static final int MAX_INT_CLEN = 11;
    public static final int MAX_LONG_CLEN = 21;
    public static final int MAX_DOUBLE_CLEN = 32;
    public static final int MAX_FLOAT_CLEN = 32;
    private static final char NULL_CHAR = '\0';
    private static final int MILLION = 1000000;
    private static final int BILLION = 1000000000;
    private static final long TEN_BILLION_L = 10000000000L;
    private static final long THOUSAND_L = 1000L;
    private static final byte BYTE_HYPHEN = 45;
    private static final byte BYTE_1 = 49;
    private static final byte BYTE_2 = 50;
    private static long MIN_INT_AS_LONG;
    private static long MAX_INT_AS_LONG;
    static final char[] LEADING_TRIPLETS;
    static final char[] FULL_TRIPLETS;
    
    public static int writeInt(int n, final char[] array, int n2) {
        if (n < 0) {
            if (n == Integer.MIN_VALUE) {
                return writeLong(n, array, n2);
            }
            array[n2++] = '-';
            n = -n;
        }
        if (n < 1000000) {
            if (n < 1000) {
                if (n < 10) {
                    array[n2++] = (char)(48 + n);
                }
                else {
                    n2 = writeLeadingTriplet(n, array, n2);
                }
            }
            else {
                final int n3 = n / 1000;
                n -= n3 * 1000;
                n2 = writeLeadingTriplet(n3, array, n2);
                n2 = writeFullTriplet(n, array, n2);
            }
            return n2;
        }
        final boolean b = n >= 1000000000;
        if (b) {
            n -= 1000000000;
            if (n >= 1000000000) {
                n -= 1000000000;
                array[n2++] = '2';
            }
            else {
                array[n2++] = '1';
            }
        }
        final int n4 = n / 1000;
        final int n5 = n - n4 * 1000;
        n = n4;
        final int n6 = n4 / 1000;
        final int n7 = n - n6 * 1000;
        if (b) {
            n2 = writeFullTriplet(n6, array, n2);
        }
        else {
            n2 = writeLeadingTriplet(n6, array, n2);
        }
        n2 = writeFullTriplet(n7, array, n2);
        n2 = writeFullTriplet(n5, array, n2);
        return n2;
    }
    
    public static int writeInt(int n, final byte[] array, int n2) {
        if (n < 0) {
            if (n == Integer.MIN_VALUE) {
                return writeLong(n, array, n2);
            }
            array[n2++] = 45;
            n = -n;
        }
        if (n < 1000000) {
            if (n < 1000) {
                if (n < 10) {
                    array[n2++] = (byte)(48 + n);
                }
                else {
                    n2 = writeLeadingTriplet(n, array, n2);
                }
            }
            else {
                final int n3 = n / 1000;
                n -= n3 * 1000;
                n2 = writeLeadingTriplet(n3, array, n2);
                n2 = writeFullTriplet(n, array, n2);
            }
            return n2;
        }
        final boolean b = n >= 1000000000;
        if (b) {
            n -= 1000000000;
            if (n >= 1000000000) {
                n -= 1000000000;
                array[n2++] = 50;
            }
            else {
                array[n2++] = 49;
            }
        }
        final int n4 = n / 1000;
        final int n5 = n - n4 * 1000;
        n = n4;
        final int n6 = n4 / 1000;
        final int n7 = n - n6 * 1000;
        if (b) {
            n2 = writeFullTriplet(n6, array, n2);
        }
        else {
            n2 = writeLeadingTriplet(n6, array, n2);
        }
        n2 = writeFullTriplet(n7, array, n2);
        n2 = writeFullTriplet(n5, array, n2);
        return n2;
    }
    
    public static int writeLong(long l, final char[] array, int n) {
        if (l < 0L) {
            if (l >= NumberUtil.MIN_INT_AS_LONG) {
                return writeInt((int)l, array, n);
            }
            if (l == Long.MIN_VALUE) {
                return getChars(String.valueOf(l), array, n);
            }
            array[n++] = '-';
            l = -l;
        }
        else if (l <= NumberUtil.MAX_INT_AS_LONG) {
            return writeInt((int)l, array, n);
        }
        final int n2 = n;
        int n3;
        n = (n3 = n + calcLongStrLength(l));
        while (l > NumberUtil.MAX_INT_AS_LONG) {
            n3 -= 3;
            final long n4 = l / 1000L;
            writeFullTriplet((int)(l - n4 * 1000L), array, n3);
            l = n4;
        }
        int i;
        int n5;
        for (i = (int)l; i >= 1000; i = n5) {
            n3 -= 3;
            n5 = i / 1000;
            writeFullTriplet(i - n5 * 1000, array, n3);
        }
        writeLeadingTriplet(i, array, n2);
        return n;
    }
    
    public static int writeLong(long l, final byte[] array, int n) {
        if (l < 0L) {
            if (l >= NumberUtil.MIN_INT_AS_LONG) {
                return writeInt((int)l, array, n);
            }
            if (l == Long.MIN_VALUE) {
                return getAsciiBytes(String.valueOf(l), array, n);
            }
            array[n++] = 45;
            l = -l;
        }
        else if (l <= NumberUtil.MAX_INT_AS_LONG) {
            return writeInt((int)l, array, n);
        }
        final int n2 = n;
        int n3;
        n = (n3 = n + calcLongStrLength(l));
        while (l > NumberUtil.MAX_INT_AS_LONG) {
            n3 -= 3;
            final long n4 = l / 1000L;
            writeFullTriplet((int)(l - n4 * 1000L), array, n3);
            l = n4;
        }
        int i;
        int n5;
        for (i = (int)l; i >= 1000; i = n5) {
            n3 -= 3;
            n5 = i / 1000;
            writeFullTriplet(i - n5 * 1000, array, n3);
        }
        writeLeadingTriplet(i, array, n2);
        return n;
    }
    
    public static int writeFloat(final float f, final char[] array, final int n) {
        return getChars(String.valueOf(f), array, n);
    }
    
    public static int writeFloat(final float f, final byte[] array, final int n) {
        return getAsciiBytes(String.valueOf(f), array, n);
    }
    
    public static int writeDouble(final double d, final char[] array, final int n) {
        return getChars(String.valueOf(d), array, n);
    }
    
    public static int writeDouble(final double d, final byte[] array, final int n) {
        return getAsciiBytes(String.valueOf(d), array, n);
    }
    
    private static int writeLeadingTriplet(final int n, final char[] array, int n2) {
        int n3 = n << 2;
        final char c = NumberUtil.LEADING_TRIPLETS[n3++];
        if (c != '\0') {
            array[n2++] = c;
        }
        final char c2 = NumberUtil.LEADING_TRIPLETS[n3++];
        if (c2 != '\0') {
            array[n2++] = c2;
        }
        array[n2++] = NumberUtil.LEADING_TRIPLETS[n3];
        return n2;
    }
    
    private static int writeLeadingTriplet(final int n, final byte[] array, int n2) {
        int n3 = n << 2;
        final char c = NumberUtil.LEADING_TRIPLETS[n3++];
        if (c != '\0') {
            array[n2++] = (byte)c;
        }
        final char c2 = NumberUtil.LEADING_TRIPLETS[n3++];
        if (c2 != '\0') {
            array[n2++] = (byte)c2;
        }
        array[n2++] = (byte)NumberUtil.LEADING_TRIPLETS[n3];
        return n2;
    }
    
    private static int writeFullTriplet(final int n, final char[] array, int n2) {
        int n3 = n << 2;
        array[n2++] = NumberUtil.FULL_TRIPLETS[n3++];
        array[n2++] = NumberUtil.FULL_TRIPLETS[n3++];
        array[n2++] = NumberUtil.FULL_TRIPLETS[n3];
        return n2;
    }
    
    private static int writeFullTriplet(final int n, final byte[] array, int n2) {
        int n3 = n << 2;
        array[n2++] = (byte)NumberUtil.FULL_TRIPLETS[n3++];
        array[n2++] = (byte)NumberUtil.FULL_TRIPLETS[n3++];
        array[n2++] = (byte)NumberUtil.FULL_TRIPLETS[n3];
        return n2;
    }
    
    private static int calcLongStrLength(final long n) {
        int n2 = 10;
        for (long n3 = 10000000000L; n >= n3 && n2 != 19; ++n2, n3 = (n3 << 3) + (n3 << 1)) {}
        return n2;
    }
    
    private static int getChars(final String s, final char[] dst, final int dstBegin) {
        final int length = s.length();
        s.getChars(0, length, dst, dstBegin);
        return dstBegin + length;
    }
    
    private static int getAsciiBytes(final String s, final byte[] array, int n) {
        for (int i = 0; i < s.length(); ++i) {
            array[n++] = (byte)s.charAt(i);
        }
        return n;
    }
    
    static {
        NumberUtil.MIN_INT_AS_LONG = -2147483647L;
        NumberUtil.MAX_INT_AS_LONG = 2147483647L;
        LEADING_TRIPLETS = new char[4000];
        FULL_TRIPLETS = new char[4000];
        int n = 0;
        for (int i = 0; i < 10; ++i) {
            final char c = (char)(48 + i);
            final char c2 = (i == 0) ? '\0' : c;
            for (int j = 0; j < 10; ++j) {
                final char c3 = (char)(48 + j);
                final char c4 = (i == 0 && j == 0) ? '\0' : c3;
                for (int k = 0; k < 10; ++k) {
                    final char c5 = (char)(48 + k);
                    NumberUtil.LEADING_TRIPLETS[n] = c2;
                    NumberUtil.LEADING_TRIPLETS[n + 1] = c4;
                    NumberUtil.LEADING_TRIPLETS[n + 2] = c5;
                    NumberUtil.FULL_TRIPLETS[n] = c;
                    NumberUtil.FULL_TRIPLETS[n + 1] = c3;
                    NumberUtil.FULL_TRIPLETS[n + 2] = c5;
                    n += 4;
                }
            }
        }
    }
}
