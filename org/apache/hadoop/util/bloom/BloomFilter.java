// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.bloom;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.util.BitSet;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class BloomFilter extends Filter
{
    private static final byte[] bitvalues;
    BitSet bits;
    
    public BloomFilter() {
    }
    
    public BloomFilter(final int vectorSize, final int nbHash, final int hashType) {
        super(vectorSize, nbHash, hashType);
        this.bits = new BitSet(this.vectorSize);
    }
    
    @Override
    public void add(final Key key) {
        if (key == null) {
            throw new NullPointerException("key cannot be null");
        }
        final int[] h = this.hash.hash(key);
        this.hash.clear();
        for (int i = 0; i < this.nbHash; ++i) {
            this.bits.set(h[i]);
        }
    }
    
    @Override
    public void and(final Filter filter) {
        if (filter == null || !(filter instanceof BloomFilter) || filter.vectorSize != this.vectorSize || filter.nbHash != this.nbHash) {
            throw new IllegalArgumentException("filters cannot be and-ed");
        }
        this.bits.and(((BloomFilter)filter).bits);
    }
    
    @Override
    public boolean membershipTest(final Key key) {
        if (key == null) {
            throw new NullPointerException("key cannot be null");
        }
        final int[] h = this.hash.hash(key);
        this.hash.clear();
        for (int i = 0; i < this.nbHash; ++i) {
            if (!this.bits.get(h[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void not() {
        this.bits.flip(0, this.vectorSize);
    }
    
    @Override
    public void or(final Filter filter) {
        if (filter == null || !(filter instanceof BloomFilter) || filter.vectorSize != this.vectorSize || filter.nbHash != this.nbHash) {
            throw new IllegalArgumentException("filters cannot be or-ed");
        }
        this.bits.or(((BloomFilter)filter).bits);
    }
    
    @Override
    public void xor(final Filter filter) {
        if (filter == null || !(filter instanceof BloomFilter) || filter.vectorSize != this.vectorSize || filter.nbHash != this.nbHash) {
            throw new IllegalArgumentException("filters cannot be xor-ed");
        }
        this.bits.xor(((BloomFilter)filter).bits);
    }
    
    @Override
    public String toString() {
        return this.bits.toString();
    }
    
    public int getVectorSize() {
        return this.vectorSize;
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        super.write(out);
        final byte[] bytes = new byte[this.getNBytes()];
        int i = 0;
        int byteIndex = 0;
        for (int bitIndex = 0; i < this.vectorSize; ++i, ++bitIndex) {
            if (bitIndex == 8) {
                bitIndex = 0;
                ++byteIndex;
            }
            if (bitIndex == 0) {
                bytes[byteIndex] = 0;
            }
            if (this.bits.get(i)) {
                final byte[] array = bytes;
                final int n = byteIndex;
                array[n] |= BloomFilter.bitvalues[bitIndex];
            }
        }
        out.write(bytes);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        super.readFields(in);
        this.bits = new BitSet(this.vectorSize);
        final byte[] bytes = new byte[this.getNBytes()];
        in.readFully(bytes);
        int i = 0;
        int byteIndex = 0;
        for (int bitIndex = 0; i < this.vectorSize; ++i, ++bitIndex) {
            if (bitIndex == 8) {
                bitIndex = 0;
                ++byteIndex;
            }
            if ((bytes[byteIndex] & BloomFilter.bitvalues[bitIndex]) != 0x0) {
                this.bits.set(i);
            }
        }
    }
    
    private int getNBytes() {
        return (int)((this.vectorSize + 7L) / 8L);
    }
    
    static {
        bitvalues = new byte[] { 1, 2, 4, 8, 16, 32, 64, -128 };
    }
}
