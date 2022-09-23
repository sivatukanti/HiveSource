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
public class BooleanWritable implements WritableComparable<BooleanWritable>
{
    private boolean value;
    
    public BooleanWritable() {
    }
    
    public BooleanWritable(final boolean value) {
        this.set(value);
    }
    
    public void set(final boolean value) {
        this.value = value;
    }
    
    public boolean get() {
        return this.value;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.value = in.readBoolean();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeBoolean(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BooleanWritable)) {
            return false;
        }
        final BooleanWritable other = (BooleanWritable)o;
        return this.value == other.value;
    }
    
    @Override
    public int hashCode() {
        return this.value ? 0 : 1;
    }
    
    @Override
    public int compareTo(final BooleanWritable o) {
        final boolean a = this.value;
        final boolean b = o.value;
        return (a == b) ? 0 : (a ? 1 : -1);
    }
    
    @Override
    public String toString() {
        return Boolean.toString(this.get());
    }
    
    static {
        WritableComparator.define(BooleanWritable.class, new Comparator());
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(BooleanWritable.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            return WritableComparator.compareBytes(b1, s1, l1, b2, s2, l2);
        }
    }
}
