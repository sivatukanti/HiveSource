// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.OutputStream;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class DataOutputOutputStream extends OutputStream
{
    private final DataOutput out;
    
    public static OutputStream constructOutputStream(final DataOutput out) {
        if (out instanceof OutputStream) {
            return (OutputStream)out;
        }
        return new DataOutputOutputStream(out);
    }
    
    private DataOutputOutputStream(final DataOutput out) {
        this.out = out;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.out.writeByte(b);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.out.write(b);
    }
}
