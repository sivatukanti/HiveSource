// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.derby.io.StorageRandomAccessFile;
import org.apache.derby.iapi.error.StandardException;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import org.apache.derby.io.StorageFile;

abstract class InputStreamFile implements StorageFile
{
    final String path;
    final int nameStart;
    final BaseStorageFactory storageFactory;
    
    InputStreamFile(final BaseStorageFactory storageFactory, final String str) {
        this.storageFactory = storageFactory;
        if (str == null || str.length() == 0) {
            this.path = storageFactory.dataDirectory;
            this.nameStart = -1;
        }
        else {
            final StringBuffer sb = new StringBuffer(storageFactory.separatedDataDirectory);
            if (File.separatorChar != '/') {
                sb.append(str.replace(File.separatorChar, '/'));
            }
            else {
                sb.append(str);
            }
            this.path = sb.toString();
            this.nameStart = this.path.lastIndexOf(47) + 1;
        }
    }
    
    InputStreamFile(final BaseStorageFactory storageFactory, final String str, final String str2) {
        this.storageFactory = storageFactory;
        final StringBuffer sb = new StringBuffer(storageFactory.separatedDataDirectory);
        if (File.separatorChar != '/') {
            sb.append(str.replace(File.separatorChar, '/'));
            sb.append('/');
            sb.append(str2.replace(File.separatorChar, '/'));
        }
        else {
            sb.append(str);
            sb.append('/');
            sb.append(str2);
        }
        this.path = sb.toString();
        this.nameStart = this.path.lastIndexOf(47) + 1;
    }
    
    InputStreamFile(final InputStreamFile inputStreamFile, final String str) {
        this.storageFactory = inputStreamFile.storageFactory;
        final StringBuffer sb = new StringBuffer(inputStreamFile.path);
        sb.append('/');
        if (File.separatorChar != '/') {
            sb.append(str.replace(File.separatorChar, '/'));
        }
        else {
            sb.append(str);
        }
        this.path = sb.toString();
        this.nameStart = this.path.lastIndexOf(47) + 1;
    }
    
    InputStreamFile(final BaseStorageFactory storageFactory, final String s, final int endIndex) {
        this.storageFactory = storageFactory;
        this.path = s.substring(0, endIndex);
        this.nameStart = this.path.lastIndexOf(47) + 1;
    }
    
    public boolean equals(final Object o) {
        return o != null && this.getClass().equals(o.getClass()) && this.path.equals(((InputStreamFile)o).path);
    }
    
    public int hashCode() {
        return this.path.hashCode();
    }
    
    public String[] list() {
        return null;
    }
    
    public boolean canWrite() {
        return false;
    }
    
    public abstract boolean exists();
    
    public boolean isDirectory() {
        return false;
    }
    
    public boolean delete() {
        return false;
    }
    
    public boolean deleteAll() {
        return false;
    }
    
    public String getPath() {
        if (File.separatorChar != '/') {
            return this.path.replace('/', File.separatorChar);
        }
        return this.path;
    }
    
    public String getCanonicalPath() throws IOException {
        return this.storageFactory.getCanonicalName() + "/" + this.path;
    }
    
    public String getName() {
        return (this.nameStart < 0) ? "" : this.path.substring(this.nameStart);
    }
    
    public boolean createNewFile() throws IOException {
        throw new IOException("createNewFile called in a read-only file system.");
    }
    
    public boolean renameTo(final StorageFile storageFile) {
        return false;
    }
    
    public boolean mkdir() {
        return false;
    }
    
    public boolean mkdirs() {
        return false;
    }
    
    public long length() {
        try {
            final InputStream inputStream = this.getInputStream();
            if (inputStream == null) {
                return 0L;
            }
            final long n = inputStream.available();
            inputStream.close();
            return n;
        }
        catch (IOException ex) {
            return 0L;
        }
    }
    
    public StorageFile getParentDir() {
        if (this.path.length() <= this.storageFactory.separatedDataDirectory.length()) {
            return null;
        }
        return this.getParentDir(this.path.lastIndexOf(47));
    }
    
    abstract StorageFile getParentDir(final int p0);
    
    public boolean setReadOnly() {
        return true;
    }
    
    public OutputStream getOutputStream() throws FileNotFoundException {
        throw new FileNotFoundException("Attempt to write into a read only file system.");
    }
    
    public OutputStream getOutputStream(final boolean b) throws FileNotFoundException {
        throw new FileNotFoundException("Attempt to write into a read only file system.");
    }
    
    public abstract InputStream getInputStream() throws FileNotFoundException;
    
    public int getExclusiveFileLock() throws StandardException {
        return 0;
    }
    
    public void releaseExclusiveFileLock() {
    }
    
    public StorageRandomAccessFile getRandomAccessFile(final String s) throws FileNotFoundException {
        return null;
    }
    
    public String toString() {
        return this.path;
    }
    
    public URL getURL() throws MalformedURLException {
        throw new MalformedURLException(this.toString());
    }
    
    public void limitAccessToOwner() {
    }
}
