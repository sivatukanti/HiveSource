// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.InputStream;

class ImportFileInputStream extends InputStream
{
    private RandomAccessFile raf;
    private long currentPosition;
    private long fileLength;
    
    ImportFileInputStream(final RandomAccessFile raf) throws IOException {
        this.raf = null;
        this.currentPosition = 0L;
        this.fileLength = 0L;
        this.raf = raf;
        this.fileLength = raf.length();
    }
    
    void seek(final long n) throws IOException {
        this.raf.seek(n);
        this.currentPosition = n;
    }
    
    public int read() throws IOException {
        return this.raf.read();
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.raf.read(b, off, len);
    }
    
    public int available() throws IOException {
        return (int)(this.fileLength - this.currentPosition);
    }
    
    public void close() throws IOException {
        if (this.raf != null) {
            this.raf.close();
        }
    }
}
