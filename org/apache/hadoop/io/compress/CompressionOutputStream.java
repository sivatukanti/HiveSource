// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.OutputStream;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class CompressionOutputStream extends OutputStream
{
    protected final OutputStream out;
    private Compressor trackedCompressor;
    
    protected CompressionOutputStream(final OutputStream out) {
        this.out = out;
    }
    
    void setTrackedCompressor(final Compressor compressor) {
        this.trackedCompressor = compressor;
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.finish();
        }
        finally {
            try {
                this.out.close();
            }
            finally {
                if (this.trackedCompressor != null) {
                    CodecPool.returnCompressor(this.trackedCompressor);
                    this.trackedCompressor = null;
                }
            }
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public abstract void write(final byte[] p0, final int p1, final int p2) throws IOException;
    
    public abstract void finish() throws IOException;
    
    public abstract void resetState() throws IOException;
}
