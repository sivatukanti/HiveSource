// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import org.apache.derby.io.StorageRandomAccessFile;
import java.io.RandomAccessFile;

class DirRandomAccessFile extends RandomAccessFile implements StorageRandomAccessFile
{
    DirRandomAccessFile(final File file, final String mode) throws FileNotFoundException {
        super(file, mode);
    }
    
    public void sync() throws IOException {
        this.getFD().sync();
    }
}
