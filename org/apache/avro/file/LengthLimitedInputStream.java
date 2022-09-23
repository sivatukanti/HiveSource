// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

class LengthLimitedInputStream extends FilterInputStream
{
    private long remaining;
    
    protected LengthLimitedInputStream(final InputStream in, final long maxLength) {
        super(in);
        this.remaining = maxLength;
    }
    
    @Override
    public int read() throws IOException {
        if (this.remaining > 0L) {
            final int v = super.read();
            if (v != -1) {
                --this.remaining;
            }
            return v;
        }
        return -1;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    private int remainingInt() {
        return (int)Math.min(this.remaining, 2147483647L);
    }
    
    @Override
    public int read(final byte[] b, final int off, int len) throws IOException {
        if (this.remaining == 0L) {
            return -1;
        }
        if (len > this.remaining) {
            len = this.remainingInt();
        }
        final int v = super.read(b, off, len);
        if (v != -1) {
            this.remaining -= v;
        }
        return v;
    }
    
    @Override
    public int available() throws IOException {
        return Math.min(super.available(), this.remainingInt());
    }
    
    @Override
    public long skip(final long n) throws IOException {
        final long v = super.skip(Math.min(this.remaining, n));
        this.remaining -= v;
        return v;
    }
}
