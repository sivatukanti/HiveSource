// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

public class BufferUtil
{
    static final byte SPACE = 32;
    static final byte MINUS = 45;
    static final byte[] DIGIT;
    private static final int[] decDivisors;
    private static final int[] hexDivisors;
    private static final long[] decDivisorsL;
    
    public static int toInt(final Buffer buffer) {
        int val = 0;
        boolean started = false;
        boolean minus = false;
        for (int i = buffer.getIndex(); i < buffer.putIndex(); ++i) {
            final byte b = buffer.peek(i);
            if (b <= 32) {
                if (started) {
                    break;
                }
            }
            else if (b >= 48 && b <= 57) {
                val = val * 10 + (b - 48);
                started = true;
            }
            else {
                if (b != 45 || started) {
                    break;
                }
                minus = true;
            }
        }
        if (started) {
            return minus ? (-val) : val;
        }
        throw new NumberFormatException(buffer.toString());
    }
    
    public static long toLong(final Buffer buffer) {
        long val = 0L;
        boolean started = false;
        boolean minus = false;
        for (int i = buffer.getIndex(); i < buffer.putIndex(); ++i) {
            final byte b = buffer.peek(i);
            if (b <= 32) {
                if (started) {
                    break;
                }
            }
            else if (b >= 48 && b <= 57) {
                val = val * 10L + (b - 48);
                started = true;
            }
            else {
                if (b != 45 || started) {
                    break;
                }
                minus = true;
            }
        }
        if (started) {
            return minus ? (-val) : val;
        }
        throw new NumberFormatException(buffer.toString());
    }
    
    public static void putHexInt(final Buffer buffer, int n) {
        if (n < 0) {
            buffer.put((byte)45);
            if (n == Integer.MIN_VALUE) {
                buffer.put((byte)56);
                buffer.put((byte)48);
                buffer.put((byte)48);
                buffer.put((byte)48);
                buffer.put((byte)48);
                buffer.put((byte)48);
                buffer.put((byte)48);
                buffer.put((byte)48);
                return;
            }
            n = -n;
        }
        if (n < 16) {
            buffer.put(BufferUtil.DIGIT[n]);
        }
        else {
            boolean started = false;
            for (int i = 0; i < BufferUtil.hexDivisors.length; ++i) {
                if (n < BufferUtil.hexDivisors[i]) {
                    if (started) {
                        buffer.put((byte)48);
                    }
                }
                else {
                    started = true;
                    final int d = n / BufferUtil.hexDivisors[i];
                    buffer.put(BufferUtil.DIGIT[d]);
                    n -= d * BufferUtil.hexDivisors[i];
                }
            }
        }
    }
    
    public static void prependHexInt(final Buffer buffer, int n) {
        if (n == 0) {
            int gi = buffer.getIndex();
            buffer.poke(--gi, (byte)48);
            buffer.setGetIndex(gi);
        }
        else {
            boolean minus = false;
            if (n < 0) {
                minus = true;
                n = -n;
            }
            int gi2 = buffer.getIndex();
            while (n > 0) {
                final int d = 0xF & n;
                n >>= 4;
                buffer.poke(--gi2, BufferUtil.DIGIT[d]);
            }
            if (minus) {
                buffer.poke(--gi2, (byte)45);
            }
            buffer.setGetIndex(gi2);
        }
    }
    
    public static void putDecInt(final Buffer buffer, int n) {
        if (n < 0) {
            buffer.put((byte)45);
            if (n == Integer.MIN_VALUE) {
                buffer.put((byte)50);
                n = 147483648;
            }
            else {
                n = -n;
            }
        }
        if (n < 10) {
            buffer.put(BufferUtil.DIGIT[n]);
        }
        else {
            boolean started = false;
            for (int i = 0; i < BufferUtil.decDivisors.length; ++i) {
                if (n < BufferUtil.decDivisors[i]) {
                    if (started) {
                        buffer.put((byte)48);
                    }
                }
                else {
                    started = true;
                    final int d = n / BufferUtil.decDivisors[i];
                    buffer.put(BufferUtil.DIGIT[d]);
                    n -= d * BufferUtil.decDivisors[i];
                }
            }
        }
    }
    
    public static void putDecLong(final Buffer buffer, long n) {
        if (n < 0L) {
            buffer.put((byte)45);
            if (n == Long.MIN_VALUE) {
                buffer.put((byte)57);
                n = 223372036854775808L;
            }
            else {
                n = -n;
            }
        }
        if (n < 10L) {
            buffer.put(BufferUtil.DIGIT[(int)n]);
        }
        else {
            boolean started = false;
            for (int i = 0; i < BufferUtil.decDivisorsL.length; ++i) {
                if (n < BufferUtil.decDivisorsL[i]) {
                    if (started) {
                        buffer.put((byte)48);
                    }
                }
                else {
                    started = true;
                    final long d = n / BufferUtil.decDivisorsL[i];
                    buffer.put(BufferUtil.DIGIT[(int)d]);
                    n -= d * BufferUtil.decDivisorsL[i];
                }
            }
        }
    }
    
    public static Buffer toBuffer(final long value) {
        final ByteArrayBuffer buf = new ByteArrayBuffer(32);
        putDecLong(buf, value);
        return buf;
    }
    
    public static void putCRLF(final Buffer buffer) {
        buffer.put((byte)13);
        buffer.put((byte)10);
    }
    
    public static boolean isPrefix(final Buffer prefix, final Buffer buffer) {
        if (prefix.length() > buffer.length()) {
            return false;
        }
        int bi = buffer.getIndex();
        for (int i = prefix.getIndex(); i < prefix.putIndex(); ++i) {
            if (prefix.peek(i) != buffer.peek(bi++)) {
                return false;
            }
        }
        return true;
    }
    
    public static String to8859_1_String(final Buffer buffer) {
        if (buffer instanceof BufferCache.CachedBuffer) {
            return buffer.toString();
        }
        return buffer.toString("ISO-8859-1");
    }
    
    static {
        DIGIT = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
        decDivisors = new int[] { 1000000000, 100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1 };
        hexDivisors = new int[] { 268435456, 16777216, 1048576, 65536, 4096, 256, 16, 1 };
        decDivisorsL = new long[] { 1000000000000000000L, 100000000000000000L, 10000000000000000L, 1000000000000000L, 100000000000000L, 10000000000000L, 1000000000000L, 100000000000L, 10000000000L, 1000000000L, 100000000L, 10000000L, 1000000L, 100000L, 10000L, 1000L, 100L, 10L, 1L };
    }
}
