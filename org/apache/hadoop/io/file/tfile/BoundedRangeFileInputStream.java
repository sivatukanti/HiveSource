// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.file.tfile;

import java.io.IOException;
import org.apache.hadoop.fs.FSDataInputStream;
import java.io.InputStream;

public class BoundedRangeFileInputStream extends InputStream
{
    private FSDataInputStream in;
    private long pos;
    private long end;
    private long mark;
    private final byte[] oneByte;
    
    public BoundedRangeFileInputStream(final FSDataInputStream in, final long offset, final long length) {
        this.oneByte = new byte[1];
        if (offset < 0L || length < 0L) {
            throw new IndexOutOfBoundsException("Invalid offset/length: " + offset + "/" + length);
        }
        this.in = in;
        this.pos = offset;
        this.end = offset + length;
        this.mark = -1L;
    }
    
    @Override
    public int available() throws IOException {
        int avail = this.in.available();
        if (this.pos + avail > this.end) {
            avail = (int)(this.end - this.pos);
        }
        return avail;
    }
    
    @Override
    public int read() throws IOException {
        final int ret = this.read(this.oneByte);
        if (ret == 1) {
            return this.oneByte[0] & 0xFF;
        }
        return -1;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if ((off | len | off + len | b.length - (off + len)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        final int n = (int)Math.min(2147483647L, Math.min(len, this.end - this.pos));
        if (n == 0) {
            return -1;
        }
        int ret = 0;
        synchronized (this.in) {
            this.in.seek(this.pos);
            ret = this.in.read(b, off, n);
        }
        if (ret < 0) {
            this.end = this.pos;
            return -1;
        }
        this.pos += ret;
        return ret;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        final long len = Math.min(n, this.end - this.pos);
        this.pos += len;
        return len;
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        this.mark = this.pos;
    }
    
    @Override
    public synchronized void reset() throws IOException {
        if (this.mark < 0L) {
            throw new IOException("Resetting to invalid mark");
        }
        this.pos = this.mark;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public void close() {
        this.in = null;
        this.pos = this.end;
        this.mark = -1L;
    }
}
