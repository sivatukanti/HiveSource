// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.io.IOException;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import java.io.FilterInputStream;

@InterfaceStability.Unstable
public final class LimitInputStream extends FilterInputStream
{
    private long left;
    private long mark;
    
    public LimitInputStream(final InputStream in, final long limit) {
        super(in);
        this.mark = -1L;
        Preconditions.checkNotNull(in);
        Preconditions.checkArgument(limit >= 0L, (Object)"limit must be non-negative");
        this.left = limit;
    }
    
    @Override
    public int available() throws IOException {
        return (int)Math.min(this.in.available(), this.left);
    }
    
    @Override
    public synchronized void mark(final int readLimit) {
        this.in.mark(readLimit);
        this.mark = this.left;
    }
    
    @Override
    public int read() throws IOException {
        if (this.left == 0L) {
            return -1;
        }
        final int result = this.in.read();
        if (result != -1) {
            --this.left;
        }
        return result;
    }
    
    @Override
    public int read(final byte[] b, final int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (this.left == 0L) {
            return -1;
        }
        len = (int)Math.min(len, this.left);
        final int result = this.in.read(b, off, len);
        if (result != -1) {
            this.left -= result;
        }
        return result;
    }
    
    @Override
    public synchronized void reset() throws IOException {
        if (!this.in.markSupported()) {
            throw new IOException("Mark not supported");
        }
        if (this.mark == -1L) {
            throw new IOException("Mark not set");
        }
        this.in.reset();
        this.left = this.mark;
    }
    
    @Override
    public long skip(long n) throws IOException {
        n = Math.min(n, this.left);
        final long skipped = this.in.skip(n);
        this.left -= skipped;
        return skipped;
    }
}
