// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.hash;

import java.util.Arrays;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.shaded.com.google.common.primitives.Ints;
import org.apache.curator.shaded.com.google.common.math.LongMath;
import java.math.RoundingMode;

enum BloomFilterStrategies implements BloomFilter.Strategy
{
    MURMUR128_MITZ_32 {
        @Override
        public <T> boolean put(final T object, final Funnel<? super T> funnel, final int numHashFunctions, final BitArray bits) {
            final long hash64 = Hashing.murmur3_128().hashObject(object, funnel).asLong();
            final int hash65 = (int)hash64;
            final int hash66 = (int)(hash64 >>> 32);
            boolean bitsChanged = false;
            for (int i = 1; i <= numHashFunctions; ++i) {
                int nextHash = hash65 + i * hash66;
                if (nextHash < 0) {
                    nextHash ^= -1;
                }
                bitsChanged |= bits.set(nextHash % bits.bitSize());
            }
            return bitsChanged;
        }
        
        @Override
        public <T> boolean mightContain(final T object, final Funnel<? super T> funnel, final int numHashFunctions, final BitArray bits) {
            final long hash64 = Hashing.murmur3_128().hashObject(object, funnel).asLong();
            final int hash65 = (int)hash64;
            final int hash66 = (int)(hash64 >>> 32);
            for (int i = 1; i <= numHashFunctions; ++i) {
                int nextHash = hash65 + i * hash66;
                if (nextHash < 0) {
                    nextHash ^= -1;
                }
                if (!bits.get(nextHash % bits.bitSize())) {
                    return false;
                }
            }
            return true;
        }
    };
    
    static class BitArray
    {
        final long[] data;
        int bitCount;
        
        BitArray(final long bits) {
            this(new long[Ints.checkedCast(LongMath.divide(bits, 64L, RoundingMode.CEILING))]);
        }
        
        BitArray(final long[] data) {
            Preconditions.checkArgument(data.length > 0, (Object)"data length is zero!");
            this.data = data;
            int bitCount = 0;
            for (final long value : data) {
                bitCount += Long.bitCount(value);
            }
            this.bitCount = bitCount;
        }
        
        boolean set(final int index) {
            if (!this.get(index)) {
                final long[] data = this.data;
                final int n = index >> 6;
                data[n] |= 1L << index;
                ++this.bitCount;
                return true;
            }
            return false;
        }
        
        boolean get(final int index) {
            return (this.data[index >> 6] & 1L << index) != 0x0L;
        }
        
        int bitSize() {
            return this.data.length * 64;
        }
        
        int bitCount() {
            return this.bitCount;
        }
        
        BitArray copy() {
            return new BitArray(this.data.clone());
        }
        
        void putAll(final BitArray array) {
            Preconditions.checkArgument(this.data.length == array.data.length, "BitArrays must be of equal length (%s != %s)", this.data.length, array.data.length);
            this.bitCount = 0;
            for (int i = 0; i < this.data.length; ++i) {
                final long[] data = this.data;
                final int n = i;
                data[n] |= array.data[i];
                this.bitCount += Long.bitCount(this.data[i]);
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof BitArray) {
                final BitArray bitArray = (BitArray)o;
                return Arrays.equals(this.data, bitArray.data);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(this.data);
        }
    }
}
