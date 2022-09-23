// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

class CountingInputStream extends FilterInputStream
{
    private long size;
    
    public CountingInputStream(final InputStream in) {
        super(in);
        this.size = 0L;
    }
    
    public int read() throws IOException {
        final int read = this.in.read();
        if (read != -1 && this.size >= 0L) {
            ++this.size;
        }
        return read;
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int read = this.in.read(b, off, len);
        if (read > 0 && this.size >= 0L) {
            this.size += read;
        }
        return read;
    }
    
    public long getSize() {
        return this.size;
    }
}
