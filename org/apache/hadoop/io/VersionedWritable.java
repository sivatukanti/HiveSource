// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class VersionedWritable implements Writable
{
    public abstract byte getVersion();
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeByte(this.getVersion());
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        final byte version = in.readByte();
        if (version != this.getVersion()) {
            throw new VersionMismatchException(this.getVersion(), version);
        }
    }
}
