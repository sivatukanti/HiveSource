// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.util;

import java.util.BitSet;

public final class JBitSet
{
    private final BitSet bitSet;
    private int size;
    
    public JBitSet(final int n) {
        this.bitSet = new BitSet(n);
        this.size = n;
    }
    
    private JBitSet(final BitSet bitSet, final int size) {
        this.bitSet = bitSet;
        this.size = size;
    }
    
    public void setTo(final JBitSet set) {
        this.and(set);
        this.or(set);
    }
    
    public boolean contains(final JBitSet set) {
        for (int i = 0; i < this.size; ++i) {
            if (set.bitSet.get(i) && !this.bitSet.get(i)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean hasSingleBitSet() {
        boolean b = false;
        for (int i = 0; i < this.size; ++i) {
            if (this.bitSet.get(i)) {
                if (b) {
                    return false;
                }
                b = true;
            }
        }
        return b;
    }
    
    public int getFirstSetBit() {
        for (int i = 0; i < this.size; ++i) {
            if (this.bitSet.get(i)) {
                return i;
            }
        }
        return -1;
    }
    
    public void grow(final int size) {
        this.size = size;
    }
    
    public void clearAll() {
        for (int i = 0; i < this.size; ++i) {
            if (this.bitSet.get(i)) {
                this.bitSet.clear(i);
            }
        }
    }
    
    public String toString() {
        return this.bitSet.toString();
    }
    
    public boolean equals(final Object o) {
        return this.bitSet.equals(((JBitSet)o).bitSet);
    }
    
    public int hashCode() {
        return this.bitSet.hashCode();
    }
    
    public Object clone() {
        return new JBitSet((BitSet)this.bitSet.clone(), this.size);
    }
    
    public boolean get(final int bitIndex) {
        return this.bitSet.get(bitIndex);
    }
    
    public void set(final int bitIndex) {
        this.bitSet.set(bitIndex);
    }
    
    public void clear(final int bitIndex) {
        this.bitSet.clear(bitIndex);
    }
    
    public void and(final JBitSet set) {
        this.bitSet.and(set.bitSet);
    }
    
    public void or(final JBitSet set) {
        this.bitSet.or(set.bitSet);
    }
    
    public void xor(final JBitSet set) {
        this.bitSet.xor(set.bitSet);
    }
    
    public int size() {
        return this.size;
    }
}
