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
public class VIntWritable implements WritableComparable<VIntWritable>
{
    private int value;
    
    public VIntWritable() {
    }
    
    public VIntWritable(final int value) {
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
        this.value = WritableUtils.readVInt(in);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        WritableUtils.writeVInt(out, this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof VIntWritable)) {
            return false;
        }
        final VIntWritable other = (VIntWritable)o;
        return this.value == other.value;
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public int compareTo(final VIntWritable o) {
        final int thisValue = this.value;
        final int thatValue = o.value;
        return (thisValue < thatValue) ? -1 : ((thisValue == thatValue) ? 0 : 1);
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
