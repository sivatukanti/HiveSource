// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.Arrays;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "Common", "HDFS", "MapReduce", "Yarn" })
@InterfaceStability.Unstable
public final class CrcUtil
{
    public static final int MULTIPLICATIVE_IDENTITY = Integer.MIN_VALUE;
    public static final int GZIP_POLYNOMIAL = -306674912;
    public static final int CASTAGNOLI_POLYNOMIAL = -2097792136;
    
    private CrcUtil() {
    }
    
    public static int getMonomial(final long lengthBytes, final int mod) {
        if (lengthBytes == 0L) {
            return Integer.MIN_VALUE;
        }
        if (lengthBytes < 0L) {
            throw new IllegalArgumentException("lengthBytes must be positive, got " + lengthBytes);
        }
        int multiplier = 8388608;
        int product = Integer.MIN_VALUE;
        for (long degree = lengthBytes; degree > 0L; degree >>= 1) {
            if ((degree & 0x1L) != 0x0L) {
                product = ((product == Integer.MIN_VALUE) ? multiplier : galoisFieldMultiply(product, multiplier, mod));
            }
            multiplier = galoisFieldMultiply(multiplier, multiplier, mod);
        }
        return product;
    }
    
    public static int composeWithMonomial(final int crcA, final int crcB, final int monomial, final int mod) {
        return galoisFieldMultiply(crcA, monomial, mod) ^ crcB;
    }
    
    public static int compose(final int crcA, final int crcB, final long lengthB, final int mod) {
        final int monomial = getMonomial(lengthB, mod);
        return composeWithMonomial(crcA, crcB, monomial, mod);
    }
    
    public static byte[] intToBytes(final int value) {
        final byte[] buf = new byte[4];
        try {
            writeInt(buf, 0, value);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return buf;
    }
    
    public static void writeInt(final byte[] buf, final int offset, final int value) throws IOException {
        if (offset + 4 > buf.length) {
            throw new IOException(String.format("writeInt out of bounds: buf.length=%d, offset=%d", buf.length, offset));
        }
        buf[offset + 0] = (byte)(value >>> 24 & 0xFF);
        buf[offset + 1] = (byte)(value >>> 16 & 0xFF);
        buf[offset + 2] = (byte)(value >>> 8 & 0xFF);
        buf[offset + 3] = (byte)(value & 0xFF);
    }
    
    public static int readInt(final byte[] buf, final int offset) throws IOException {
        if (offset + 4 > buf.length) {
            throw new IOException(String.format("readInt out of bounds: buf.length=%d, offset=%d", buf.length, offset));
        }
        final int value = (buf[offset + 0] & 0xFF) << 24 | (buf[offset + 1] & 0xFF) << 16 | (buf[offset + 2] & 0xFF) << 8 | (buf[offset + 3] & 0xFF);
        return value;
    }
    
    public static String toSingleCrcString(final byte[] bytes) throws IOException {
        if (bytes.length != 4) {
            throw new IOException(String.format("Unexpected byte[] length '%d' for single CRC. Contents: %s", bytes.length, Arrays.toString(bytes)));
        }
        return String.format("0x%08x", readInt(bytes, 0));
    }
    
    public static String toMultiCrcString(final byte[] bytes) throws IOException {
        if (bytes.length % 4 != 0) {
            throw new IOException(String.format("Unexpected byte[] length '%d' not divisible by 4. Contents: %s", bytes.length, Arrays.toString(bytes)));
        }
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < bytes.length; i += 4) {
            sb.append(String.format("0x%08x", readInt(bytes, i)));
            if (i != bytes.length - 4) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    private static int galoisFieldMultiply(final int p, final int q, final int m) {
        int summation = 0;
        int curTerm = Integer.MIN_VALUE;
        int px = p;
        while (curTerm != 0) {
            if ((q & curTerm) != 0x0) {
                summation ^= px;
            }
            final boolean hasMaxDegree = (px & 0x1) != 0x0;
            px >>>= 1;
            if (hasMaxDegree) {
                px ^= m;
            }
            curTerm >>>= 1;
        }
        return summation;
    }
}
