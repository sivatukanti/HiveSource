// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.net.URL;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.derby.io.StorageFile;

class CPFile extends InputStreamFile
{
    private final CPStorageFactory storageFactory;
    
    CPFile(final CPStorageFactory storageFactory, final String s) {
        super(storageFactory, s);
        this.storageFactory = storageFactory;
    }
    
    CPFile(final CPStorageFactory storageFactory, final String s, final String s2) {
        super(storageFactory, s, s2);
        this.storageFactory = storageFactory;
    }
    
    CPFile(final CPFile cpFile, final String s) {
        super(cpFile, s);
        this.storageFactory = cpFile.storageFactory;
    }
    
    private CPFile(final CPStorageFactory storageFactory, final String s, final int n) {
        super(storageFactory, s, n);
        this.storageFactory = storageFactory;
    }
    
    public boolean exists() {
        return this.getURL() != null;
    }
    
    StorageFile getParentDir(final int n) {
        return new CPFile(this.storageFactory, this.path, n);
    }
    
    public InputStream getInputStream() throws FileNotFoundException {
        InputStream inputStream = null;
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            inputStream = contextClassLoader.getResourceAsStream(this.path);
        }
        if (inputStream == null) {
            final ClassLoader classLoader = this.getClass().getClassLoader();
            if (classLoader != null) {
                inputStream = classLoader.getResourceAsStream(this.path);
            }
            else {
                inputStream = ClassLoader.getSystemResourceAsStream(this.path);
            }
        }
        if (inputStream == null) {
            throw new FileNotFoundException(this.toString());
        }
        return inputStream;
    }
    
    public URL getURL() {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            final URL resource = contextClassLoader.getResource(this.path);
            if (resource != null) {
                return resource;
            }
        }
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader != null) {
            return classLoader.getResource(this.path);
        }
        return ClassLoader.getSystemResource(this.path);
    }
}
