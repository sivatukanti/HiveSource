// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common.util;

import java.util.Arrays;
import com.google.common.base.Preconditions;

public class BloomFilter
{
    public static final double DEFAULT_FPP = 0.05;
    protected BitSet bitSet;
    protected int numBits;
    protected int numHashFunctions;
    
    public BloomFilter() {
    }
    
    public BloomFilter(final long expectedEntries) {
        this(expectedEntries, 0.05);
    }
    
    public BloomFilter(final long expectedEntries, final double fpp) {
        Preconditions.checkArgument(expectedEntries > 0L, (Object)"expectedEntries should be > 0");
        Preconditions.checkArgument(fpp > 0.0 && fpp < 1.0, (Object)"False positive probability should be > 0.0 & < 1.0");
        final int nb = optimalNumOfBits(expectedEntries, fpp);
        this.numBits = nb + (64 - nb % 64);
        this.numHashFunctions = optimalNumOfHashFunctions(expectedEntries, this.numBits);
        this.bitSet = new BitSet(this.numBits);
    }
    
    static int optimalNumOfHashFunctions(final long n, final long m) {
        return Math.max(1, (int)Math.round(m / (double)n * Math.log(2.0)));
    }
    
    static int optimalNumOfBits(final long n, final double p) {
        return (int)(-n * Math.log(p) / (Math.log(2.0) * Math.log(2.0)));
    }
    
    public void add(final byte[] val) {
        if (val == null) {
            this.addBytes(val, -1);
        }
        else {
            this.addBytes(val, val.length);
        }
    }
    
    public void addBytes(final byte[] val, final int length) {
        final long hash64 = (val == null) ? 2862933555777941757L : Murmur3.hash64(val, length);
        this.addHash(hash64);
    }
    
    private void addHash(final long hash64) {
        final int hash65 = (int)hash64;
        final int hash66 = (int)(hash64 >>> 32);
        for (int i = 1; i <= this.numHashFunctions; ++i) {
            int combinedHash = hash65 + i * hash66;
            if (combinedHash < 0) {
                combinedHash ^= -1;
            }
            final int pos = combinedHash % this.numBits;
            this.bitSet.set(pos);
        }
    }
    
    public void addString(final String val) {
        if (val == null) {
            this.add(null);
        }
        else {
            this.add(val.getBytes());
        }
    }
    
    public void addLong(final long val) {
        this.addHash(this.getLongHash(val));
    }
    
    public void addDouble(final double val) {
        this.addLong(Double.doubleToLongBits(val));
    }
    
    public boolean test(final byte[] val) {
        if (val == null) {
            return this.testBytes(val, -1);
        }
        return this.testBytes(val, val.length);
    }
    
    public boolean testBytes(final byte[] val, final int length) {
        final long hash64 = (val == null) ? 2862933555777941757L : Murmur3.hash64(val, length);
        return this.testHash(hash64);
    }
    
    private boolean testHash(final long hash64) {
        final int hash65 = (int)hash64;
        final int hash66 = (int)(hash64 >>> 32);
        for (int i = 1; i <= this.numHashFunctions; ++i) {
            int combinedHash = hash65 + i * hash66;
            if (combinedHash < 0) {
                combinedHash ^= -1;
            }
            final int pos = combinedHash % this.numBits;
            if (!this.bitSet.get(pos)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean testString(final String val) {
        if (val == null) {
            return this.test(null);
        }
        return this.test(val.getBytes());
    }
    
    public boolean testLong(final long val) {
        return this.testHash(this.getLongHash(val));
    }
    
    private long getLongHash(long key) {
        key = ~key + (key << 21);
        key ^= key >> 24;
        key = key + (key << 3) + (key << 8);
        key ^= key >> 14;
        key = key + (key << 2) + (key << 4);
        key ^= key >> 28;
        key += key << 31;
        return key;
    }
    
    public boolean testDouble(final double val) {
        return this.testLong(Double.doubleToLongBits(val));
    }
    
    public long sizeInBytes() {
        return this.getBitSize() / 8;
    }
    
    public int getBitSize() {
        return this.bitSet.getData().length * 64;
    }
    
    public int getNumHashFunctions() {
        return this.numHashFunctions;
    }
    
    public long[] getBitSet() {
        return this.bitSet.getData();
    }
    
    @Override
    public String toString() {
        return "m: " + this.numBits + " k: " + this.numHashFunctions;
    }
    
    public void merge(final BloomFilter that) {
        if (this != that && this.numBits == that.numBits && this.numHashFunctions == that.numHashFunctions) {
            this.bitSet.putAll(that.bitSet);
            return;
        }
        throw new IllegalArgumentException("BloomFilters are not compatible for merging. this - " + this.toString() + " that - " + that.toString());
    }
    
    public void reset() {
        this.bitSet.clear();
    }
    
    public class BitSet
    {
        private final long[] data;
        
        public BitSet(final BloomFilter this$0, final long bits) {
            this(this$0, new long[(int)Math.ceil(bits / 64.0)]);
        }
        
        public BitSet(final long[] data) {
            assert data.length > 0 : "data length is zero!";
            this.data = data;
        }
        
        public void set(final int index) {
            final long[] data = this.data;
            final int n = index >>> 6;
            data[n] |= 1L << index;
        }
        
        public boolean get(final int index) {
            return (this.data[index >>> 6] & 1L << index) != 0x0L;
        }
        
        public long bitSize() {
            return this.data.length * 64L;
        }
        
        public long[] getData() {
            return this.data;
        }
        
        public void putAll(final BitSet array) {
            assert this.data.length == array.data.length : "BitArrays must be of equal length (" + this.data.length + "!= " + array.data.length + ")";
            for (int i = 0; i < this.data.length; ++i) {
                final long[] data = this.data;
                final int n = i;
                data[n] |= array.data[i];
            }
        }
        
        public void clear() {
            Arrays.fill(this.data, 0L);
        }
    }
}
