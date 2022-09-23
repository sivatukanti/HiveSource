// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class ByteWritable implements WritableComparable<ByteWritable>
{
    private byte value;
    
    public ByteWritable() {
    }
    
    public ByteWritable(final byte value) {
        this.set(value);
    }
    
    public void set(final byte value) {
        this.value = value;
    }
    
    public byte get() {
        return this.value;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.value = in.readByte();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeByte(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ByteWritable)) {
            return false;
        }
        final ByteWritable other = (ByteWritable)o;
        return this.value == other.value;
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public int compareTo(final ByteWritable o) {
        final int thisValue = this.value;
        final int thatValue = o.value;
        return (thisValue < thatValue) ? -1 : ((thisValue == thatValue) ? 0 : 1);
    }
    
    @Override
    public String toString() {
        return Byte.toString(this.value);
    }
    
    static {
        WritableComparator.define(ByteWritable.class, new Comparator());
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(ByteWritable.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            final byte thisValue = b1[s1];
            final byte thatValue = b2[s2];
            return (thisValue < thatValue) ? -1 : ((thisValue == thatValue) ? 0 : 1);
        }
    }
}
