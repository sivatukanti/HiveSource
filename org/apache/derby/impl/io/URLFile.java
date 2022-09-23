// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.io.FileNotFoundException;
import java.net.URL;
import org.apache.derby.io.StorageFile;
import java.io.InputStream;
import java.io.IOException;

class URLFile extends InputStreamFile
{
    private final URLStorageFactory storageFactory;
    
    URLFile(final URLStorageFactory storageFactory, final String s) {
        super(storageFactory, s);
        this.storageFactory = storageFactory;
    }
    
    URLFile(final URLStorageFactory storageFactory, final String s, final String s2) {
        super(storageFactory, s, s2);
        this.storageFactory = storageFactory;
    }
    
    URLFile(final URLFile urlFile, final String s) {
        super(urlFile, s);
        this.storageFactory = urlFile.storageFactory;
    }
    
    private URLFile(final URLStorageFactory storageFactory, final String s, final int n) {
        super(storageFactory, s, n);
        this.storageFactory = storageFactory;
    }
    
    public boolean exists() {
        try {
            final InputStream inputStream = this.getInputStream();
            if (inputStream == null) {
                return false;
            }
            inputStream.close();
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    StorageFile getParentDir(final int n) {
        return new URLFile(this.storageFactory, this.path, n);
    }
    
    public InputStream getInputStream() throws FileNotFoundException {
        try {
            return new URL(this.path).openStream();
        }
        catch (IOException ex) {
            throw new FileNotFoundException(this.path);
        }
    }
}
