// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.net.MalformedURLException;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.derby.io.StorageFile;
import java.util.zip.ZipEntry;

class JarDBFile extends InputStreamFile
{
    private final JarStorageFactory storageFactory;
    
    JarDBFile(final JarStorageFactory storageFactory, final String s) {
        super(storageFactory, s);
        this.storageFactory = storageFactory;
    }
    
    JarDBFile(final JarStorageFactory storageFactory, final String s, final String s2) {
        super(storageFactory, s, s2);
        this.storageFactory = storageFactory;
    }
    
    JarDBFile(final JarDBFile jarDBFile, final String s) {
        super(jarDBFile, s);
        this.storageFactory = jarDBFile.storageFactory;
    }
    
    private JarDBFile(final JarStorageFactory storageFactory, final String s, final int n) {
        super(storageFactory, s, n);
        this.storageFactory = storageFactory;
    }
    
    public boolean exists() {
        return this.getEntry() != null;
    }
    
    private ZipEntry getEntry() {
        return this.storageFactory.zipData.getEntry(this.path);
    }
    
    public long length() {
        final ZipEntry entry = this.getEntry();
        if (entry == null) {
            return 0L;
        }
        return entry.getSize();
    }
    
    StorageFile getParentDir(final int n) {
        return new JarDBFile(this.storageFactory, this.path, n);
    }
    
    public InputStream getInputStream() throws FileNotFoundException {
        final ZipEntry entry = this.getEntry();
        if (entry == null) {
            throw new FileNotFoundException(this.path);
        }
        try {
            return this.storageFactory.zipData.getInputStream(entry);
        }
        catch (IOException ex) {
            throw new FileNotFoundException(this.path);
        }
    }
    
    public String toString() {
        return this.path;
    }
    
    public URL getURL() throws MalformedURLException {
        return new URL("jar:" + new File(this.storageFactory.zipData.getName()).toURL().toString() + "!/" + this.path);
    }
}
