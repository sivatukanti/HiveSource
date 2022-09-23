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
public class ShortWritable implements WritableComparable<ShortWritable>
{
    private short value;
    
    public ShortWritable() {
    }
    
    public ShortWritable(final short value) {
        this.set(value);
    }
    
    public void set(final short value) {
        this.value = value;
    }
    
    public short get() {
        return this.value;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.value = in.readShort();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeShort(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ShortWritable)) {
            return false;
        }
        final ShortWritable other = (ShortWritable)o;
        return this.value == other.value;
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public int compareTo(final ShortWritable o) {
        final short thisValue = this.value;
        final short thatValue = o.value;
        return (thisValue < thatValue) ? -1 : ((thisValue == thatValue) ? 0 : 1);
    }
    
    @Override
    public String toString() {
        return Short.toString(this.value);
    }
    
    static {
        WritableComparator.define(ShortWritable.class, new Comparator());
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(ShortWritable.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            final short thisValue = (short)WritableComparator.readUnsignedShort(b1, s1);
            final short thatValue = (short)WritableComparator.readUnsignedShort(b2, s2);
            return (thisValue < thatValue) ? -1 : ((thisValue == thatValue) ? 0 : 1);
        }
    }
}
