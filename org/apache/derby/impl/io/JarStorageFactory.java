// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.io.IOException;
import java.io.File;
import org.apache.derby.io.StorageFile;
import java.util.zip.ZipFile;

public class JarStorageFactory extends BaseStorageFactory
{
    ZipFile zipData;
    
    StorageFile newPersistentFile(final String s) {
        return new JarDBFile(this, s);
    }
    
    StorageFile newPersistentFile(final String s, final String s2) {
        if (s == null || s.length() == 0) {
            return this.newPersistentFile(s2);
        }
        return new JarDBFile(this, s, s2);
    }
    
    StorageFile newPersistentFile(final StorageFile storageFile, final String s) {
        if (storageFile == null) {
            return this.newPersistentFile(s);
        }
        return new JarDBFile((JarDBFile)storageFile, s);
    }
    
    void doInit() throws IOException {
        if (this.dataDirectory == null) {
            return;
        }
        int n;
        for (n = 0; n < this.dataDirectory.length() && Character.isSpaceChar(this.dataDirectory.charAt(n)); ++n) {}
        int index = -1;
        int index2 = -1;
        if (n < this.dataDirectory.length()) {
            index = this.dataDirectory.indexOf(40, n);
            if (index >= 0) {
                index2 = this.dataDirectory.indexOf(41, index + 1);
            }
        }
        File file;
        if (index2 > 0) {
            file = this.getJarFile(this.dataDirectory.substring(index + 1, index2));
            int n2;
            for (n2 = index2 + 1; n2 < this.dataDirectory.length() && Character.isSpaceChar(this.dataDirectory.charAt(n2)); ++n2) {}
            this.dataDirectory = this.dataDirectory.substring(n2, this.dataDirectory.length());
        }
        else {
            file = this.getJarFile(this.dataDirectory);
            this.dataDirectory = "";
        }
        this.zipData = new ZipFile(file);
        this.canonicalName = "(" + file.getCanonicalPath() + ")" + this.dataDirectory;
        this.separatedDataDirectory = this.dataDirectory + '/';
        this.createTempDir();
    }
    
    public void shutdown() {
        if (this.zipData != null) {
            try {
                this.zipData.close();
            }
            catch (IOException ex) {}
            this.zipData = null;
        }
    }
    
    private File getJarFile(final String s) {
        File file = new File(s);
        if (this.home != null && !file.isAbsolute()) {
            file = new File(this.home, s);
        }
        return file;
    }
}
