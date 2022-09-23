// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.DataOutputStream;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class FSDataOutputStream extends DataOutputStream implements Syncable, CanSetDropBehind, StreamCapabilities
{
    private final OutputStream wrappedStream;
    
    public FSDataOutputStream(final OutputStream out, final FileSystem.Statistics stats) {
        this(out, stats, 0L);
    }
    
    public FSDataOutputStream(final OutputStream out, final FileSystem.Statistics stats, final long startPosition) {
        super(new PositionCache(out, stats, startPosition));
        this.wrappedStream = out;
    }
    
    public long getPos() {
        return ((PositionCache)this.out).getPos();
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FSDataOutputStream{");
        sb.append("wrappedStream=").append(this.wrappedStream);
        sb.append('}');
        return sb.toString();
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS" })
    public OutputStream getWrappedStream() {
        return this.wrappedStream;
    }
    
    @Override
    public boolean hasCapability(final String capability) {
        return this.wrappedStream instanceof StreamCapabilities && ((StreamCapabilities)this.wrappedStream).hasCapability(capability);
    }
    
    @Override
    public void hflush() throws IOException {
        if (this.wrappedStream instanceof Syncable) {
            ((Syncable)this.wrappedStream).hflush();
        }
        else {
            this.wrappedStream.flush();
        }
    }
    
    @Override
    public void hsync() throws IOException {
        if (this.wrappedStream instanceof Syncable) {
            ((Syncable)this.wrappedStream).hsync();
        }
        else {
            this.wrappedStream.flush();
        }
    }
    
    @Override
    public void setDropBehind(final Boolean dropBehind) throws IOException {
        try {
            ((CanSetDropBehind)this.wrappedStream).setDropBehind(dropBehind);
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("the wrapped stream does not support setting the drop-behind caching setting.");
        }
    }
    
    private static class PositionCache extends FilterOutputStream
    {
        private final FileSystem.Statistics statistics;
        private long position;
        
        PositionCache(final OutputStream out, final FileSystem.Statistics stats, final long pos) {
            super(out);
            this.statistics = stats;
            this.position = pos;
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.out.write(b);
            ++this.position;
            if (this.statistics != null) {
                this.statistics.incrementBytesWritten(1L);
            }
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.out.write(b, off, len);
            this.position += len;
            if (this.statistics != null) {
                this.statistics.incrementBytesWritten(len);
            }
        }
        
        long getPos() {
            return this.position;
        }
        
        @Override
        public void close() throws IOException {
            if (this.out != null) {
                this.out.close();
            }
        }
    }
}
