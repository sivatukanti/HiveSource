// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil;

public class HashCommon
{
    public static final Object REMOVED;
    
    protected HashCommon() {
    }
    
    public static final int murmurHash3(int x) {
        x ^= x >>> 16;
        x *= -2048144789;
        x ^= x >>> 13;
        x *= -1028477387;
        x ^= x >>> 16;
        return x;
    }
    
    public static final long murmurHash3(long x) {
        x ^= x >>> 33;
        x *= -49064778989728563L;
        x ^= x >>> 33;
        x *= -4265267296055464877L;
        x ^= x >>> 33;
        return x;
    }
    
    public static final int float2int(final float f) {
        return Float.floatToRawIntBits(f);
    }
    
    public static final int double2int(final double d) {
        final long l = Double.doubleToRawLongBits(d);
        return (int)(l ^ l >>> 32);
    }
    
    public static final int long2int(final long l) {
        return (int)(l ^ l >>> 32);
    }
    
    public static int nextPowerOfTwo(int x) {
        if (x == 0) {
            return 1;
        }
        x = (--x | x >> 1);
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        return (x | x >> 16) + 1;
    }
    
    public static long nextPowerOfTwo(long x) {
        if (x == 0L) {
            return 1L;
        }
        --x;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return (x | x >> 32) + 1L;
    }
    
    public static int maxFill(final int n, final float f) {
        return (int)Math.ceil(n * f);
    }
    
    public static long maxFill(final long n, final float f) {
        return (long)Math.ceil(n * f);
    }
    
    public static int arraySize(final int expected, final float f) {
        final long s = nextPowerOfTwo((long)Math.ceil(expected / f));
        if (s > 1073741824L) {
            throw new IllegalArgumentException("Too large (" + expected + " expected elements with load factor " + f + ")");
        }
        return (int)s;
    }
    
    public static long bigArraySize(final long expected, final float f) {
        return nextPowerOfTwo((long)Math.ceil(expected / f));
    }
    
    static {
        REMOVED = new Object();
    }
}
