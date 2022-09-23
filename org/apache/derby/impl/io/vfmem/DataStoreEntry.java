// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io.vfmem;

import java.io.FileNotFoundException;

public class DataStoreEntry
{
    private final String path;
    private final boolean isDir;
    private boolean isReadOnly;
    private final BlockedByteArray src;
    private volatile boolean released;
    
    public DataStoreEntry(final String path, final boolean isDir) {
        this.isReadOnly = false;
        this.released = false;
        this.path = path;
        this.isDir = isDir;
        if (isDir) {
            this.src = null;
        }
        else {
            this.src = new BlockedByteArray();
        }
    }
    
    public boolean isDirectory() {
        this.checkIfReleased();
        return this.isDir;
    }
    
    BlockedByteArrayInputStream getInputStream() throws FileNotFoundException {
        this.checkIfReleased();
        if (this.isDir) {
            throw new FileNotFoundException("'" + this.path + "' is a directory");
        }
        return this.src.getInputStream();
    }
    
    BlockedByteArrayOutputStream getOutputStream(final boolean b) throws FileNotFoundException {
        this.checkIfReleased();
        if (this.isDir) {
            throw new FileNotFoundException("'" + this.path + "' is a directory");
        }
        if (this.isReadOnly) {
            throw new FileNotFoundException("'" + this.path + "' is read-only");
        }
        BlockedByteArrayOutputStream blockedByteArrayOutputStream;
        if (b) {
            blockedByteArrayOutputStream = this.src.getOutputStream(this.src.length());
        }
        else {
            this.src.setLength(0L);
            blockedByteArrayOutputStream = this.src.getOutputStream(0L);
        }
        return blockedByteArrayOutputStream;
    }
    
    public long length() {
        this.checkIfReleased();
        return this.src.length();
    }
    
    public void setReadOnly() {
        this.checkIfReleased();
        this.isReadOnly = true;
    }
    
    public boolean isReadOnly() {
        this.checkIfReleased();
        return this.isReadOnly;
    }
    
    void release() {
        this.released = true;
        if (this.src != null) {
            this.src.release();
        }
    }
    
    public void setLength(final long length) {
        this.checkIfReleased();
        this.src.setLength(length);
    }
    
    private void checkIfReleased() {
        if (this.released) {
            throw new IllegalStateException("Entry has been released.");
        }
    }
}
