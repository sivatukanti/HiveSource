// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.bloom;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.WritableComparable;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class Key implements WritableComparable<Key>
{
    byte[] bytes;
    double weight;
    
    public Key() {
    }
    
    public Key(final byte[] value) {
        this(value, 1.0);
    }
    
    public Key(final byte[] value, final double weight) {
        this.set(value, weight);
    }
    
    public void set(final byte[] value, final double weight) {
        if (value == null) {
            throw new IllegalArgumentException("value can not be null");
        }
        this.bytes = value;
        this.weight = weight;
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    public double getWeight() {
        return this.weight;
    }
    
    public void incrementWeight(final double weight) {
        this.weight += weight;
    }
    
    public void incrementWeight() {
        ++this.weight;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Key && this.compareTo((Key)o) == 0;
    }
    
    @Override
    public int hashCode() {
        int result = 0;
        for (int i = 0; i < this.bytes.length; ++i) {
            result ^= Byte.valueOf(this.bytes[i]).hashCode();
        }
        result ^= Double.valueOf(this.weight).hashCode();
        return result;
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeInt(this.bytes.length);
        out.write(this.bytes);
        out.writeDouble(this.weight);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        in.readFully(this.bytes = new byte[in.readInt()]);
        this.weight = in.readDouble();
    }
    
    @Override
    public int compareTo(final Key other) {
        int result = this.bytes.length - other.getBytes().length;
        for (int i = 0; result == 0 && i < this.bytes.length; result = this.bytes[i] - other.bytes[i], ++i) {}
        if (result == 0) {
            result = (int)(this.weight - other.weight);
        }
        return result;
    }
}
