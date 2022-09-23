// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class BinaryComparable implements Comparable<BinaryComparable>
{
    public abstract int getLength();
    
    public abstract byte[] getBytes();
    
    @Override
    public int compareTo(final BinaryComparable other) {
        if (this == other) {
            return 0;
        }
        return WritableComparator.compareBytes(this.getBytes(), 0, this.getLength(), other.getBytes(), 0, other.getLength());
    }
    
    public int compareTo(final byte[] other, final int off, final int len) {
        return WritableComparator.compareBytes(this.getBytes(), 0, this.getLength(), other, off, len);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof BinaryComparable)) {
            return false;
        }
        final BinaryComparable that = (BinaryComparable)other;
        return this.getLength() == that.getLength() && this.compareTo(that) == 0;
    }
    
    @Override
    public int hashCode() {
        return WritableComparator.hashBytes(this.getBytes(), this.getLength());
    }
}
