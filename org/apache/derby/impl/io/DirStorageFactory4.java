// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import org.apache.derby.io.StorageFile;

public class DirStorageFactory4 extends DirStorageFactory
{
    StorageFile newPersistentFile(final String s) {
        if (s == null) {
            return new DirFile4(this.dataDirectory);
        }
        return new DirFile4(this.dataDirectory, s);
    }
    
    StorageFile newPersistentFile(final String str, final String s) {
        return new DirFile4(this.separatedDataDirectory + str, s);
    }
    
    StorageFile newPersistentFile(final StorageFile storageFile, final String s) {
        return new DirFile4((DirFile)storageFile, s);
    }
    
    public boolean supportsWriteSync() {
        return true;
    }
}
