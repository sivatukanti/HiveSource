// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class FsStatus implements Writable
{
    private long capacity;
    private long used;
    private long remaining;
    
    public FsStatus(final long capacity, final long used, final long remaining) {
        this.capacity = capacity;
        this.used = used;
        this.remaining = remaining;
    }
    
    public long getCapacity() {
        return this.capacity;
    }
    
    public long getUsed() {
        return this.used;
    }
    
    public long getRemaining() {
        return this.remaining;
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeLong(this.capacity);
        out.writeLong(this.used);
        out.writeLong(this.remaining);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.capacity = in.readLong();
        this.used = in.readLong();
        this.remaining = in.readLong();
    }
}
