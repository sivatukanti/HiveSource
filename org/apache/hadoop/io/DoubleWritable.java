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
public class DoubleWritable implements WritableComparable<DoubleWritable>
{
    private double value;
    
    public DoubleWritable() {
        this.value = 0.0;
    }
    
    public DoubleWritable(final double value) {
        this.value = 0.0;
        this.set(value);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.value = in.readDouble();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeDouble(this.value);
    }
    
    public void set(final double value) {
        this.value = value;
    }
    
    public double get() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DoubleWritable)) {
            return false;
        }
        final DoubleWritable other = (DoubleWritable)o;
        return this.value == other.value;
    }
    
    @Override
    public int hashCode() {
        return (int)Double.doubleToLongBits(this.value);
    }
    
    @Override
    public int compareTo(final DoubleWritable o) {
        return Double.compare(this.value, o.value);
    }
    
    @Override
    public String toString() {
        return Double.toString(this.value);
    }
    
    static {
        WritableComparator.define(DoubleWritable.class, new Comparator());
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(DoubleWritable.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            final double thisValue = WritableComparator.readDouble(b1, s1);
            final double thatValue = WritableComparator.readDouble(b2, s2);
            return Double.compare(thisValue, thatValue);
        }
    }
}
