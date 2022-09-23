// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.io.File;
import java.io.SyncFailedException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.derby.io.StorageFile;
import org.apache.derby.io.WritableStorageFactory;

public class DirStorageFactory extends BaseStorageFactory implements WritableStorageFactory
{
    public final StorageFile newStorageFile(final String s) {
        return this.newPersistentFile(s);
    }
    
    public final StorageFile newStorageFile(final String s, final String s2) {
        return this.newPersistentFile(s, s2);
    }
    
    public final StorageFile newStorageFile(final StorageFile storageFile, final String s) {
        return this.newPersistentFile(storageFile, s);
    }
    
    StorageFile newPersistentFile(final String s) {
        if (s == null) {
            return new DirFile(this.dataDirectory);
        }
        return new DirFile(this.dataDirectory, s);
    }
    
    StorageFile newPersistentFile(final String str, final String s) {
        return new DirFile(this.separatedDataDirectory + str, s);
    }
    
    StorageFile newPersistentFile(final StorageFile storageFile, final String s) {
        return new DirFile((DirFile)storageFile, s);
    }
    
    public void sync(final OutputStream outputStream, final boolean b) throws IOException, SyncFailedException {
        ((FileOutputStream)outputStream).getFD().sync();
    }
    
    public boolean supportsWriteSync() {
        return false;
    }
    
    public boolean isReadOnlyDatabase() {
        return false;
    }
    
    public boolean supportsRandomAccess() {
        return true;
    }
    
    void doInit() throws IOException {
        if (this.dataDirectory != null) {
            final File file = new File(this.dataDirectory);
            File file2;
            if (file.isAbsolute()) {
                file2 = file;
            }
            else if (this.home != null && this.dataDirectory.startsWith(this.home)) {
                file2 = file;
            }
            else {
                file2 = new File(this.home, this.dataDirectory);
                if (this.home != null) {
                    this.dataDirectory = this.home + this.getSeparator() + this.dataDirectory;
                }
            }
            this.canonicalName = file2.getCanonicalPath();
            this.createTempDir();
            this.separatedDataDirectory = this.dataDirectory + this.getSeparator();
        }
        else if (this.home != null) {
            this.dataDirectory = new File(this.home).getCanonicalPath();
            this.separatedDataDirectory = this.dataDirectory + this.getSeparator();
        }
    }
}
