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
public class NullWritable implements WritableComparable<NullWritable>
{
    private static final NullWritable THIS;
    
    private NullWritable() {
    }
    
    public static NullWritable get() {
        return NullWritable.THIS;
    }
    
    @Override
    public String toString() {
        return "(null)";
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public int compareTo(final NullWritable other) {
        return 0;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof NullWritable;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
    }
    
    static {
        THIS = new NullWritable();
        WritableComparator.define(NullWritable.class, new Comparator());
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(NullWritable.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            assert 0 == l1;
            assert 0 == l2;
            return 0;
        }
    }
}
