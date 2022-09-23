// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.nio.ByteOrder;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import com.google.common.primitives.UnsignedBytes;
import sun.misc.Unsafe;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

abstract class FastByteComparisons
{
    static final Logger LOG;
    
    public static int compareTo(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
        return LexicographicalComparerHolder.BEST_COMPARER.compareTo(b1, s1, l1, b2, s2, l2);
    }
    
    private static Comparer<byte[]> lexicographicalComparerJavaImpl() {
        return LexicographicalComparerHolder.PureJavaComparer.INSTANCE;
    }
    
    static {
        LOG = LoggerFactory.getLogger(FastByteComparisons.class);
    }
    
    private static class LexicographicalComparerHolder
    {
        static final String UNSAFE_COMPARER_NAME;
        static final Comparer<byte[]> BEST_COMPARER;
        
        static Comparer<byte[]> getBestComparer() {
            if (System.getProperty("os.arch").toLowerCase().startsWith("sparc")) {
                if (FastByteComparisons.LOG.isTraceEnabled()) {
                    FastByteComparisons.LOG.trace("Lexicographical comparer selected for byte aligned system architecture");
                }
                return lexicographicalComparerJavaImpl();
            }
            try {
                final Class<?> theClass = Class.forName(LexicographicalComparerHolder.UNSAFE_COMPARER_NAME);
                final Comparer<byte[]> comparer = (Comparer<byte[]>)theClass.getEnumConstants()[0];
                if (FastByteComparisons.LOG.isTraceEnabled()) {
                    FastByteComparisons.LOG.trace("Unsafe comparer selected for byte unaligned system architecture");
                }
                return comparer;
            }
            catch (Throwable t) {
                if (FastByteComparisons.LOG.isTraceEnabled()) {
                    FastByteComparisons.LOG.trace(t.getMessage());
                    FastByteComparisons.LOG.trace("Lexicographical comparer selected");
                }
                return lexicographicalComparerJavaImpl();
            }
        }
        
        static {
            UNSAFE_COMPARER_NAME = LexicographicalComparerHolder.class.getName() + "$UnsafeComparer";
            BEST_COMPARER = getBestComparer();
        }
        
        private enum PureJavaComparer implements Comparer<byte[]>
        {
            INSTANCE;
            
            @Override
            public int compareTo(final byte[] buffer1, final int offset1, final int length1, final byte[] buffer2, final int offset2, final int length2) {
                if (buffer1 == buffer2 && offset1 == offset2 && length1 == length2) {
                    return 0;
                }
                for (int end1 = offset1 + length1, end2 = offset2 + length2, i = offset1, j = offset2; i < end1 && j < end2; ++i, ++j) {
                    final int a = buffer1[i] & 0xFF;
                    final int b = buffer2[j] & 0xFF;
                    if (a != b) {
                        return a - b;
                    }
                }
                return length1 - length2;
            }
        }
        
        private enum UnsafeComparer implements Comparer<byte[]>
        {
            INSTANCE;
            
            static final Unsafe theUnsafe;
            static final int BYTE_ARRAY_BASE_OFFSET;
            static final boolean littleEndian;
            
            static boolean lessThanUnsigned(final long x1, final long x2) {
                return x1 + Long.MIN_VALUE < x2 + Long.MIN_VALUE;
            }
            
            @Override
            public int compareTo(final byte[] buffer1, final int offset1, final int length1, final byte[] buffer2, final int offset2, final int length2) {
                if (buffer1 == buffer2 && offset1 == offset2 && length1 == length2) {
                    return 0;
                }
                final int stride = 8;
                final int minLength = Math.min(length1, length2);
                final int strideLimit = minLength & 0xFFFFFFF8;
                final int offset1Adj = offset1 + UnsafeComparer.BYTE_ARRAY_BASE_OFFSET;
                final int offset2Adj = offset2 + UnsafeComparer.BYTE_ARRAY_BASE_OFFSET;
                int i = 0;
                while (i < strideLimit) {
                    final long lw = UnsafeComparer.theUnsafe.getLong(buffer1, offset1Adj + (long)i);
                    final long rw = UnsafeComparer.theUnsafe.getLong(buffer2, offset2Adj + (long)i);
                    if (lw != rw) {
                        if (!UnsafeComparer.littleEndian) {
                            return lessThanUnsigned(lw, rw) ? -1 : 1;
                        }
                        final int n = Long.numberOfTrailingZeros(lw ^ rw) & 0xFFFFFFF8;
                        return (int)(lw >>> n & 0xFFL) - (int)(rw >>> n & 0xFFL);
                    }
                    else {
                        i += 8;
                    }
                }
                while (i < minLength) {
                    final int result = UnsignedBytes.compare(buffer1[offset1 + i], buffer2[offset2 + i]);
                    if (result != 0) {
                        return result;
                    }
                    ++i;
                }
                return length1 - length2;
            }
            
            static {
                theUnsafe = AccessController.doPrivileged((PrivilegedAction<Unsafe>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            final Field f = Unsafe.class.getDeclaredField("theUnsafe");
                            f.setAccessible(true);
                            return f.get(null);
                        }
                        catch (NoSuchFieldException e) {
                            throw new Error();
                        }
                        catch (IllegalAccessException e2) {
                            throw new Error();
                        }
                    }
                });
                BYTE_ARRAY_BASE_OFFSET = UnsafeComparer.theUnsafe.arrayBaseOffset(byte[].class);
                if (UnsafeComparer.theUnsafe.arrayIndexScale(byte[].class) != 1) {
                    throw new AssertionError();
                }
                littleEndian = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);
            }
        }
    }
    
    private interface Comparer<T>
    {
        int compareTo(final T p0, final int p1, final int p2, final T p3, final int p4, final int p5);
    }
}
