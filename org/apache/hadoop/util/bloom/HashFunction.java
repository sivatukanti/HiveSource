// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.bloom;

import org.apache.hadoop.util.hash.Hash;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public final class HashFunction
{
    private int nbHash;
    private int maxValue;
    private Hash hashFunction;
    
    public HashFunction(final int maxValue, final int nbHash, final int hashType) {
        if (maxValue <= 0) {
            throw new IllegalArgumentException("maxValue must be > 0");
        }
        if (nbHash <= 0) {
            throw new IllegalArgumentException("nbHash must be > 0");
        }
        this.maxValue = maxValue;
        this.nbHash = nbHash;
        this.hashFunction = Hash.getInstance(hashType);
        if (this.hashFunction == null) {
            throw new IllegalArgumentException("hashType must be known");
        }
    }
    
    public void clear() {
    }
    
    public int[] hash(final Key k) {
        final byte[] b = k.getBytes();
        if (b == null) {
            throw new NullPointerException("buffer reference is null");
        }
        if (b.length == 0) {
            throw new IllegalArgumentException("key length must be > 0");
        }
        final int[] result = new int[this.nbHash];
        int i = 0;
        int initval = 0;
        while (i < this.nbHash) {
            initval = this.hashFunction.hash(b, initval);
            result[i] = Math.abs(initval % this.maxValue);
            ++i;
        }
        return result;
    }
}
