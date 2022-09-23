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
public class LongWritable implements WritableComparable<LongWritable>
{
    private long value;
    
    public LongWritable() {
    }
    
    public LongWritable(final long value) {
        this.set(value);
    }
    
    public void set(final long value) {
        this.value = value;
    }
    
    public long get() {
        return this.value;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.value = in.readLong();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeLong(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof LongWritable)) {
            return false;
        }
        final LongWritable other = (LongWritable)o;
        return this.value == other.value;
    }
    
    @Override
    public int hashCode() {
        return (int)this.value;
    }
    
    @Override
    public int compareTo(final LongWritable o) {
        final long thisValue = this.value;
        final long thatValue = o.value;
        return (thisValue < thatValue) ? -1 : ((thisValue == thatValue) ? 0 : 1);
    }
    
    @Override
    public String toString() {
        return Long.toString(this.value);
    }
    
    static {
        WritableComparator.define(LongWritable.class, new Comparator());
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(LongWritable.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            final long thisValue = WritableComparator.readLong(b1, s1);
            final long thatValue = WritableComparator.readLong(b2, s2);
            return (thisValue < thatValue) ? -1 : ((thisValue == thatValue) ? 0 : 1);
        }
    }
    
    public static class DecreasingComparator extends Comparator
    {
        @Override
        public int compare(final WritableComparable a, final WritableComparable b) {
            return super.compare(b, a);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            return super.compare(b2, s2, l2, b1, s1, l1);
        }
    }
}
