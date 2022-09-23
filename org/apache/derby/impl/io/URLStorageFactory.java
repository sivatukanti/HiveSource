// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.io.IOException;
import org.apache.derby.io.StorageFile;

public class URLStorageFactory extends BaseStorageFactory
{
    StorageFile newPersistentFile(final String s) {
        return new URLFile(this, s);
    }
    
    StorageFile newPersistentFile(final String s, final String s2) {
        if (s == null || s.length() == 0) {
            return this.newPersistentFile(s2);
        }
        return new URLFile(this, s, s2);
    }
    
    StorageFile newPersistentFile(final StorageFile storageFile, final String s) {
        if (storageFile == null) {
            return this.newPersistentFile(s);
        }
        return new URLFile((URLFile)storageFile, s);
    }
    
    void doInit() throws IOException {
        if (this.dataDirectory != null) {
            if (this.dataDirectory.endsWith("/")) {
                this.separatedDataDirectory = this.dataDirectory;
                this.dataDirectory = this.dataDirectory.substring(0, this.dataDirectory.length() - 1);
            }
            else {
                this.separatedDataDirectory = this.dataDirectory + '/';
            }
            this.canonicalName = this.dataDirectory;
            this.createTempDir();
        }
    }
}
