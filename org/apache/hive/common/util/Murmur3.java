// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common.util;

public class Murmur3
{
    public static final long NULL_HASHCODE = 2862933555777941757L;
    private static final int C1_32 = -862048943;
    private static final int C2_32 = 461845907;
    private static final int R1_32 = 15;
    private static final int R2_32 = 13;
    private static final int M_32 = 5;
    private static final int N_32 = -430675100;
    private static final long C1 = -8663945395140668459L;
    private static final long C2 = 5545529020109919103L;
    private static final int R1 = 31;
    private static final int R2 = 27;
    private static final int R3 = 33;
    private static final int M = 5;
    private static final int N1 = 1390208809;
    private static final int N2 = 944331445;
    private static final int DEFAULT_SEED = 104729;
    
    public static int hash32(final byte[] data) {
        return hash32(data, data.length, 104729);
    }
    
    public static int hash32(final byte[] data, final int length, final int seed) {
        int hash = seed;
        final int nblocks = length >> 2;
        for (int i = 0; i < nblocks; ++i) {
            final int i_4 = i << 2;
            int k = (data[i_4] & 0xFF) | (data[i_4 + 1] & 0xFF) << 8 | (data[i_4 + 2] & 0xFF) << 16 | (data[i_4 + 3] & 0xFF) << 24;
            k *= -862048943;
            k = Integer.rotateLeft(k, 15);
            k *= 461845907;
            hash ^= k;
            hash = Integer.rotateLeft(hash, 13) * 5 - 430675100;
        }
        final int idx = nblocks << 2;
        int k2 = 0;
        switch (length - idx) {
            case 3: {
                k2 ^= data[idx + 2] << 16;
            }
            case 2: {
                k2 ^= data[idx + 1] << 8;
            }
            case 1: {
                k2 ^= data[idx];
                k2 *= -862048943;
                k2 = Integer.rotateLeft(k2, 15);
                k2 *= 461845907;
                hash ^= k2;
                break;
            }
        }
        hash ^= length;
        hash ^= hash >>> 16;
        hash *= -2048144789;
        hash ^= hash >>> 13;
        hash *= -1028477387;
        hash ^= hash >>> 16;
        return hash;
    }
    
    public static long hash64(final byte[] data) {
        return hash64(data, data.length, 104729);
    }
    
    public static long hash64(final byte[] data, final int length) {
        return hash64(data, length, 104729);
    }
    
    public static long hash64(final byte[] data, final int length, final int seed) {
        long hash = seed;
        final int nblocks = length >> 3;
        for (int i = 0; i < nblocks; ++i) {
            final int i2 = i << 3;
            long k = ((long)data[i2] & 0xFFL) | ((long)data[i2 + 1] & 0xFFL) << 8 | ((long)data[i2 + 2] & 0xFFL) << 16 | ((long)data[i2 + 3] & 0xFFL) << 24 | ((long)data[i2 + 4] & 0xFFL) << 32 | ((long)data[i2 + 5] & 0xFFL) << 40 | ((long)data[i2 + 6] & 0xFFL) << 48 | ((long)data[i2 + 7] & 0xFFL) << 56;
            k *= -8663945395140668459L;
            k = Long.rotateLeft(k, 31);
            k *= 5545529020109919103L;
            hash ^= k;
            hash = Long.rotateLeft(hash, 27) * 5L + 1390208809L;
        }
        long k2 = 0L;
        final int tailStart = nblocks << 3;
        switch (length - tailStart) {
            case 7: {
                k2 ^= ((long)data[tailStart + 6] & 0xFFL) << 48;
            }
            case 6: {
                k2 ^= ((long)data[tailStart + 5] & 0xFFL) << 40;
            }
            case 5: {
                k2 ^= ((long)data[tailStart + 4] & 0xFFL) << 32;
            }
            case 4: {
                k2 ^= ((long)data[tailStart + 3] & 0xFFL) << 24;
            }
            case 3: {
                k2 ^= ((long)data[tailStart + 2] & 0xFFL) << 16;
            }
            case 2: {
                k2 ^= ((long)data[tailStart + 1] & 0xFFL) << 8;
            }
            case 1: {
                k2 ^= ((long)data[tailStart] & 0xFFL);
                k2 *= -8663945395140668459L;
                k2 = Long.rotateLeft(k2, 31);
                k2 *= 5545529020109919103L;
                hash ^= k2;
                break;
            }
        }
        hash ^= length;
        hash = fmix64(hash);
        return hash;
    }
    
    public static long[] hash128(final byte[] data) {
        return hash128(data, data.length, 104729);
    }
    
    public static long[] hash128(final byte[] data, final int length, final int seed) {
        long h1 = seed;
        long h2 = seed;
        final int nblocks = length >> 4;
        for (int i = 0; i < nblocks; ++i) {
            final int i2 = i << 4;
            long k1 = ((long)data[i2] & 0xFFL) | ((long)data[i2 + 1] & 0xFFL) << 8 | ((long)data[i2 + 2] & 0xFFL) << 16 | ((long)data[i2 + 3] & 0xFFL) << 24 | ((long)data[i2 + 4] & 0xFFL) << 32 | ((long)data[i2 + 5] & 0xFFL) << 40 | ((long)data[i2 + 6] & 0xFFL) << 48 | ((long)data[i2 + 7] & 0xFFL) << 56;
            long k2 = ((long)data[i2 + 8] & 0xFFL) | ((long)data[i2 + 9] & 0xFFL) << 8 | ((long)data[i2 + 10] & 0xFFL) << 16 | ((long)data[i2 + 11] & 0xFFL) << 24 | ((long)data[i2 + 12] & 0xFFL) << 32 | ((long)data[i2 + 13] & 0xFFL) << 40 | ((long)data[i2 + 14] & 0xFFL) << 48 | ((long)data[i2 + 15] & 0xFFL) << 56;
            k1 *= -8663945395140668459L;
            k1 = Long.rotateLeft(k1, 31);
            k1 *= 5545529020109919103L;
            h1 ^= k1;
            h1 = Long.rotateLeft(h1, 27);
            h1 += h2;
            h1 = h1 * 5L + 1390208809L;
            k2 *= 5545529020109919103L;
            k2 = Long.rotateLeft(k2, 33);
            k2 *= -8663945395140668459L;
            h2 ^= k2;
            h2 = Long.rotateLeft(h2, 31);
            h2 += h1;
            h2 = h2 * 5L + 944331445L;
        }
        long k3 = 0L;
        long k4 = 0L;
        final int tailStart = nblocks << 4;
        switch (length - tailStart) {
            case 15: {
                k4 ^= (long)(data[tailStart + 14] & 0xFF) << 48;
            }
            case 14: {
                k4 ^= (long)(data[tailStart + 13] & 0xFF) << 40;
            }
            case 13: {
                k4 ^= (long)(data[tailStart + 12] & 0xFF) << 32;
            }
            case 12: {
                k4 ^= (long)(data[tailStart + 11] & 0xFF) << 24;
            }
            case 11: {
                k4 ^= (long)(data[tailStart + 10] & 0xFF) << 16;
            }
            case 10: {
                k4 ^= (long)(data[tailStart + 9] & 0xFF) << 8;
            }
            case 9: {
                k4 ^= (data[tailStart + 8] & 0xFF);
                k4 *= 5545529020109919103L;
                k4 = Long.rotateLeft(k4, 33);
                k4 *= -8663945395140668459L;
                h2 ^= k4;
            }
            case 8: {
                k3 ^= (long)(data[tailStart + 7] & 0xFF) << 56;
            }
            case 7: {
                k3 ^= (long)(data[tailStart + 6] & 0xFF) << 48;
            }
            case 6: {
                k3 ^= (long)(data[tailStart + 5] & 0xFF) << 40;
            }
            case 5: {
                k3 ^= (long)(data[tailStart + 4] & 0xFF) << 32;
            }
            case 4: {
                k3 ^= (long)(data[tailStart + 3] & 0xFF) << 24;
            }
            case 3: {
                k3 ^= (long)(data[tailStart + 2] & 0xFF) << 16;
            }
            case 2: {
                k3 ^= (long)(data[tailStart + 1] & 0xFF) << 8;
            }
            case 1: {
                k3 ^= (data[tailStart] & 0xFF);
                k3 *= -8663945395140668459L;
                k3 = Long.rotateLeft(k3, 31);
                k3 *= 5545529020109919103L;
                h1 ^= k3;
                break;
            }
        }
        h1 ^= length;
        h2 ^= length;
        h1 += h2;
        h2 += h1;
        h1 = fmix64(h1);
        h2 = fmix64(h2);
        h1 += h2;
        h2 += h1;
        return new long[] { h1, h2 };
    }
    
    private static long fmix64(long h) {
        h ^= h >>> 33;
        h *= -49064778989728563L;
        h ^= h >>> 33;
        h *= -4265267296055464877L;
        h ^= h >>> 33;
        return h;
    }
}
