// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.RandomAccessFile;

public class SeekableFileInputStream extends SeekableInputStream
{
    protected RandomAccessFile randomAccessFile;
    
    public SeekableFileInputStream(final File file) throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(file, "r");
    }
    
    public SeekableFileInputStream(final String name) throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(name, "r");
    }
    
    public SeekableFileInputStream(final RandomAccessFile randomAccessFile) {
        this.randomAccessFile = randomAccessFile;
    }
    
    public int read() throws IOException {
        return this.randomAccessFile.read();
    }
    
    public int read(final byte[] b) throws IOException {
        return this.randomAccessFile.read(b);
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.randomAccessFile.read(b, off, len);
    }
    
    public void close() throws IOException {
        this.randomAccessFile.close();
    }
    
    public long length() throws IOException {
        return this.randomAccessFile.length();
    }
    
    public long position() throws IOException {
        return this.randomAccessFile.getFilePointer();
    }
    
    public void seek(final long pos) throws IOException {
        this.randomAccessFile.seek(pos);
    }
}
