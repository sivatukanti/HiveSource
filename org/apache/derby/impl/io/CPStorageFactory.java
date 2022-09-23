// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.io.IOException;
import org.apache.derby.io.StorageFile;

public class CPStorageFactory extends BaseStorageFactory
{
    StorageFile newPersistentFile(final String s) {
        return new CPFile(this, s);
    }
    
    StorageFile newPersistentFile(final String s, final String s2) {
        if (s == null || s.length() == 0) {
            return this.newPersistentFile(s2);
        }
        return new CPFile(this, s, s2);
    }
    
    StorageFile newPersistentFile(final StorageFile storageFile, final String s) {
        if (storageFile == null) {
            return this.newPersistentFile(s);
        }
        return new CPFile((CPFile)storageFile, s);
    }
    
    void doInit() throws IOException {
        if (this.dataDirectory != null) {
            this.separatedDataDirectory = this.dataDirectory + '/';
            this.canonicalName = this.dataDirectory;
            this.createTempDir();
        }
    }
}
