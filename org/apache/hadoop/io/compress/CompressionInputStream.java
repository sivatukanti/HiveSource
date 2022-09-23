// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import java.io.IOException;
import org.apache.hadoop.fs.PositionedReadable;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.Seekable;
import java.io.InputStream;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class CompressionInputStream extends InputStream implements Seekable
{
    protected final InputStream in;
    protected long maxAvailableData;
    private Decompressor trackedDecompressor;
    
    protected CompressionInputStream(final InputStream in) throws IOException {
        this.maxAvailableData = 0L;
        if (!(in instanceof Seekable) || !(in instanceof PositionedReadable)) {
            this.maxAvailableData = in.available();
        }
        this.in = in;
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.in.close();
        }
        finally {
            if (this.trackedDecompressor != null) {
                CodecPool.returnDecompressor(this.trackedDecompressor);
                this.trackedDecompressor = null;
            }
        }
    }
    
    @Override
    public abstract int read(final byte[] p0, final int p1, final int p2) throws IOException;
    
    public abstract void resetState() throws IOException;
    
    @Override
    public long getPos() throws IOException {
        if (!(this.in instanceof Seekable) || !(this.in instanceof PositionedReadable)) {
            return this.maxAvailableData - this.in.available();
        }
        return ((Seekable)this.in).getPos();
    }
    
    @Override
    public void seek(final long pos) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean seekToNewSource(final long targetPos) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    void setTrackedDecompressor(final Decompressor decompressor) {
        this.trackedDecompressor = decompressor;
    }
}
