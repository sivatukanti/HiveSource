// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.derby.io.StorageRandomAccessFile;
import org.apache.derby.io.StorageFile;

class LOBFile
{
    private final StorageFile storageFile;
    private final StorageRandomAccessFile randomAccessFile;
    
    LOBFile(final StorageFile storageFile) throws FileNotFoundException {
        this.storageFile = storageFile;
        this.randomAccessFile = storageFile.getRandomAccessFile("rw");
    }
    
    StorageFile getStorageFile() {
        return this.storageFile;
    }
    
    long length() throws IOException {
        return this.randomAccessFile.length();
    }
    
    void seek(final long n) throws IOException {
        this.randomAccessFile.seek(n);
    }
    
    void write(final int n) throws IOException, StandardException {
        this.randomAccessFile.write(n);
    }
    
    long getFilePointer() throws IOException {
        return this.randomAccessFile.getFilePointer();
    }
    
    void write(final byte[] array, final int n, final int n2) throws IOException, StandardException {
        this.randomAccessFile.write(array, n, n2);
    }
    
    int readByte() throws IOException, StandardException {
        return this.randomAccessFile.readByte();
    }
    
    int read(final byte[] array, final int n, final int n2) throws IOException, StandardException {
        return this.randomAccessFile.read(array, n, n2);
    }
    
    void close() throws IOException {
        this.randomAccessFile.close();
    }
    
    void setLength(final long length) throws IOException, StandardException {
        this.randomAccessFile.setLength(length);
    }
    
    void write(final byte[] array) throws IOException, StandardException {
        this.randomAccessFile.write(array);
    }
}
