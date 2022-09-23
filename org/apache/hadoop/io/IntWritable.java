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
public class IntWritable implements WritableComparable<IntWritable>
{
    private int value;
    
    public IntWritable() {
    }
    
    public IntWritable(final int value) {
        this.set(value);
    }
    
    public void set(final int value) {
        this.value = value;
    }
    
    public int get() {
        return this.value;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.value = in.readInt();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeInt(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IntWritable)) {
            return false;
        }
        final IntWritable other = (IntWritable)o;
        return this.value == other.value;
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public int compareTo(final IntWritable o) {
        final int thisValue = this.value;
        final int thatValue = o.value;
        return (thisValue < thatValue) ? -1 : ((thisValue == thatValue) ? 0 : 1);
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
    
    static {
        WritableComparator.define(IntWritable.class, new Comparator());
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(IntWritable.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            final int thisValue = WritableComparator.readInt(b1, s1);
            final int thatValue = WritableComparator.readInt(b2, s2);
            return (thisValue < thatValue) ? -1 : ((thisValue == thatValue) ? 0 : 1);
        }
    }
}
