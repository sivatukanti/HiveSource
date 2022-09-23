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
public class FloatWritable implements WritableComparable<FloatWritable>
{
    private float value;
    
    public FloatWritable() {
    }
    
    public FloatWritable(final float value) {
        this.set(value);
    }
    
    public void set(final float value) {
        this.value = value;
    }
    
    public float get() {
        return this.value;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.value = in.readFloat();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeFloat(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof FloatWritable)) {
            return false;
        }
        final FloatWritable other = (FloatWritable)o;
        return this.value == other.value;
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }
    
    @Override
    public int compareTo(final FloatWritable o) {
        return Float.compare(this.value, o.value);
    }
    
    @Override
    public String toString() {
        return Float.toString(this.value);
    }
    
    static {
        WritableComparator.define(FloatWritable.class, new Comparator());
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(FloatWritable.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            final float thisValue = WritableComparator.readFloat(b1, s1);
            final float thatValue = WritableComparator.readFloat(b2, s2);
            return Float.compare(thisValue, thatValue);
        }
    }
}
