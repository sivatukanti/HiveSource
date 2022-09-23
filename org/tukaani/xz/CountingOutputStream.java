// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.IOException;
import java.io.OutputStream;

class CountingOutputStream extends FinishableOutputStream
{
    private OutputStream out;
    private long size;
    
    public CountingOutputStream(final OutputStream out) {
        this.size = 0L;
        this.out = out;
    }
    
    public void write(final int n) throws IOException {
        this.out.write(n);
        if (this.size >= 0L) {
            ++this.size;
        }
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
        if (this.size >= 0L) {
            this.size += len;
        }
    }
    
    public void flush() throws IOException {
        this.out.flush();
    }
    
    public void close() throws IOException {
        this.out.close();
    }
    
    public long getSize() {
        return this.size;
    }
}
