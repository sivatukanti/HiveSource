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
public class VLongWritable implements WritableComparable<VLongWritable>
{
    private long value;
    
    public VLongWritable() {
    }
    
    public VLongWritable(final long value) {
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
        this.value = WritableUtils.readVLong(in);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        WritableUtils.writeVLong(out, this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof VLongWritable)) {
            return false;
        }
        final VLongWritable other = (VLongWritable)o;
        return this.value == other.value;
    }
    
    @Override
    public int hashCode() {
        return (int)this.value;
    }
    
    @Override
    public int compareTo(final VLongWritable o) {
        final long thisValue = this.value;
        final long thatValue = o.value;
        return (thisValue < thatValue) ? -1 : ((thisValue == thatValue) ? 0 : 1);
    }
    
    @Override
    public String toString() {
        return Long.toString(this.value);
    }
}
