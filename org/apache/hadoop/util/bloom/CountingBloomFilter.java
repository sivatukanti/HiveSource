// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.bloom;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public final class CountingBloomFilter extends Filter
{
    private long[] buckets;
    private static final long BUCKET_MAX_VALUE = 15L;
    
    public CountingBloomFilter() {
    }
    
    public CountingBloomFilter(final int vectorSize, final int nbHash, final int hashType) {
        super(vectorSize, nbHash, hashType);
        this.buckets = new long[buckets2words(vectorSize)];
    }
    
    private static int buckets2words(final int vectorSize) {
        return (vectorSize - 1 >>> 4) + 1;
    }
    
    @Override
    public void add(final Key key) {
        if (key == null) {
            throw new NullPointerException("key can not be null");
        }
        final int[] h = this.hash.hash(key);
        this.hash.clear();
        for (int i = 0; i < this.nbHash; ++i) {
            final int wordNum = h[i] >> 4;
            final int bucketShift = (h[i] & 0xF) << 2;
            final long bucketMask = 15L << bucketShift;
            final long bucketValue = (this.buckets[wordNum] & bucketMask) >>> bucketShift;
            if (bucketValue < 15L) {
                this.buckets[wordNum] = ((this.buckets[wordNum] & ~bucketMask) | bucketValue + 1L << bucketShift);
            }
        }
    }
    
    public void delete(final Key key) {
        if (key == null) {
            throw new NullPointerException("Key may not be null");
        }
        if (!this.membershipTest(key)) {
            throw new IllegalArgumentException("Key is not a member");
        }
        final int[] h = this.hash.hash(key);
        this.hash.clear();
        for (int i = 0; i < this.nbHash; ++i) {
            final int wordNum = h[i] >> 4;
            final int bucketShift = (h[i] & 0xF) << 2;
            final long bucketMask = 15L << bucketShift;
            final long bucketValue = (this.buckets[wordNum] & bucketMask) >>> bucketShift;
            if (bucketValue >= 1L && bucketValue < 15L) {
                this.buckets[wordNum] = ((this.buckets[wordNum] & ~bucketMask) | bucketValue - 1L << bucketShift);
            }
        }
    }
    
    @Override
    public void and(final Filter filter) {
        if (filter == null || !(filter instanceof CountingBloomFilter) || filter.vectorSize != this.vectorSize || filter.nbHash != this.nbHash) {
            throw new IllegalArgumentException("filters cannot be and-ed");
        }
        final CountingBloomFilter cbf = (CountingBloomFilter)filter;
        for (int sizeInWords = buckets2words(this.vectorSize), i = 0; i < sizeInWords; ++i) {
            final long[] buckets = this.buckets;
            final int n = i;
            buckets[n] &= cbf.buckets[i];
        }
    }
    
    @Override
    public boolean membershipTest(final Key key) {
        if (key == null) {
            throw new NullPointerException("Key may not be null");
        }
        final int[] h = this.hash.hash(key);
        this.hash.clear();
        for (int i = 0; i < this.nbHash; ++i) {
            final int wordNum = h[i] >> 4;
            final int bucketShift = (h[i] & 0xF) << 2;
            final long bucketMask = 15L << bucketShift;
            if ((this.buckets[wordNum] & bucketMask) == 0x0L) {
                return false;
            }
        }
        return true;
    }
    
    public int approximateCount(final Key key) {
        int res = Integer.MAX_VALUE;
        final int[] h = this.hash.hash(key);
        this.hash.clear();
        for (int i = 0; i < this.nbHash; ++i) {
            final int wordNum = h[i] >> 4;
            final int bucketShift = (h[i] & 0xF) << 2;
            final long bucketMask = 15L << bucketShift;
            final long bucketValue = (this.buckets[wordNum] & bucketMask) >>> bucketShift;
            if (bucketValue < res) {
                res = (int)bucketValue;
            }
        }
        if (res != Integer.MAX_VALUE) {
            return res;
        }
        return 0;
    }
    
    @Override
    public void not() {
        throw new UnsupportedOperationException("not() is undefined for " + this.getClass().getName());
    }
    
    @Override
    public void or(final Filter filter) {
        if (filter == null || !(filter instanceof CountingBloomFilter) || filter.vectorSize != this.vectorSize || filter.nbHash != this.nbHash) {
            throw new IllegalArgumentException("filters cannot be or-ed");
        }
        final CountingBloomFilter cbf = (CountingBloomFilter)filter;
        for (int sizeInWords = buckets2words(this.vectorSize), i = 0; i < sizeInWords; ++i) {
            final long[] buckets = this.buckets;
            final int n = i;
            buckets[n] |= cbf.buckets[i];
        }
    }
    
    @Override
    public void xor(final Filter filter) {
        throw new UnsupportedOperationException("xor() is undefined for " + this.getClass().getName());
    }
    
    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        for (int i = 0; i < this.vectorSize; ++i) {
            if (i > 0) {
                res.append(" ");
            }
            final int wordNum = i >> 4;
            final int bucketShift = (i & 0xF) << 2;
            final long bucketMask = 15L << bucketShift;
            final long bucketValue = (this.buckets[wordNum] & bucketMask) >>> bucketShift;
            res.append(bucketValue);
        }
        return res.toString();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        super.write(out);
        for (int sizeInWords = buckets2words(this.vectorSize), i = 0; i < sizeInWords; ++i) {
            out.writeLong(this.buckets[i]);
        }
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        super.readFields(in);
        final int sizeInWords = buckets2words(this.vectorSize);
        this.buckets = new long[sizeInWords];
        for (int i = 0; i < sizeInWords; ++i) {
            this.buckets[i] = in.readLong();
        }
    }
}
