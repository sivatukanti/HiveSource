// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.DataInput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class CompressedWritable implements Writable
{
    private byte[] compressed;
    
    @Override
    public final void readFields(final DataInput in) throws IOException {
        in.readFully(this.compressed = new byte[in.readInt()], 0, this.compressed.length);
    }
    
    protected void ensureInflated() {
        if (this.compressed != null) {
            try {
                final ByteArrayInputStream deflated = new ByteArrayInputStream(this.compressed);
                final DataInput inflater = new DataInputStream(new InflaterInputStream(deflated));
                this.readFieldsCompressed(inflater);
                this.compressed = null;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    protected abstract void readFieldsCompressed(final DataInput p0) throws IOException;
    
    @Override
    public final void write(final DataOutput out) throws IOException {
        if (this.compressed == null) {
            final ByteArrayOutputStream deflated = new ByteArrayOutputStream();
            final Deflater deflater = new Deflater(1);
            final DataOutputStream dout = new DataOutputStream(new DeflaterOutputStream(deflated, deflater));
            this.writeCompressed(dout);
            dout.close();
            deflater.end();
            this.compressed = deflated.toByteArray();
        }
        out.writeInt(this.compressed.length);
        out.write(this.compressed);
    }
    
    protected abstract void writeCompressed(final DataOutput p0) throws IOException;
}
