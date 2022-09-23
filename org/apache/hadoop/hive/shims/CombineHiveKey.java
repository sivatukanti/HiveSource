// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.io.WritableComparable;

public class CombineHiveKey implements WritableComparable
{
    Object key;
    
    public CombineHiveKey(final Object key) {
        this.key = key;
    }
    
    public Object getKey() {
        return this.key;
    }
    
    public void setKey(final Object key) {
        this.key = key;
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        throw new IOException("Method not supported");
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        throw new IOException("Method not supported");
    }
    
    @Override
    public int compareTo(final Object w) {
        assert false;
        return 0;
    }
}
