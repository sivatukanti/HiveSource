// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.IOException;
import java.io.FileDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SyncableFileOutputStream extends FileOutputStream implements Syncable
{
    public SyncableFileOutputStream(final String name) throws FileNotFoundException {
        super(name);
    }
    
    public SyncableFileOutputStream(final File file) throws FileNotFoundException {
        super(file);
    }
    
    public SyncableFileOutputStream(final String name, final boolean append) throws FileNotFoundException {
        super(name, append);
    }
    
    public SyncableFileOutputStream(final File file, final boolean append) throws FileNotFoundException {
        super(file, append);
    }
    
    public SyncableFileOutputStream(final FileDescriptor fdObj) {
        super(fdObj);
    }
    
    @Override
    public void sync() throws IOException {
        this.getFD().sync();
    }
}
