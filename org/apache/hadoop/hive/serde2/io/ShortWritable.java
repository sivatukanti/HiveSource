// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.io;

import org.apache.hadoop.io.WritableComparator;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.io.WritableComparable;

public class ShortWritable implements WritableComparable
{
    private short value;
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeShort(this.value);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.value = in.readShort();
    }
    
    public ShortWritable(final short s) {
        this.value = s;
    }
    
    public ShortWritable() {
        this.value = 0;
    }
    
    public void set(final short value) {
        this.value = value;
    }
    
    public short get() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o.getClass() == ShortWritable.class && this.get() == ((ShortWritable)o).get();
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.get());
    }
    
    @Override
    public int compareTo(final Object o) {
        final int thisValue = this.value;
        final int thatValue = ((ShortWritable)o).value;
        return thisValue - thatValue;
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
            final int a1 = (short)WritableComparator.readUnsignedShort(b1, s1);
            final int a2 = (short)WritableComparator.readUnsignedShort(b2, s2);
            return a1 - a2;
        }
    }
}
