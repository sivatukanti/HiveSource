// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.file.tfile;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class SimpleBufferedOutputStream extends FilterOutputStream
{
    protected byte[] buf;
    protected int count;
    
    public SimpleBufferedOutputStream(final OutputStream out, final byte[] buf) {
        super(out);
        this.count = 0;
        this.buf = buf;
    }
    
    private void flushBuffer() throws IOException {
        if (this.count > 0) {
            this.out.write(this.buf, 0, this.count);
            this.count = 0;
        }
    }
    
    @Override
    public void write(final int b) throws IOException {
        if (this.count >= this.buf.length) {
            this.flushBuffer();
        }
        this.buf[this.count++] = (byte)b;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (len >= this.buf.length) {
            this.flushBuffer();
            this.out.write(b, off, len);
            return;
        }
        if (len > this.buf.length - this.count) {
            this.flushBuffer();
        }
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count += len;
    }
    
    @Override
    public synchronized void flush() throws IOException {
        this.flushBuffer();
        this.out.flush();
    }
    
    public int size() {
        return this.count;
    }
}
