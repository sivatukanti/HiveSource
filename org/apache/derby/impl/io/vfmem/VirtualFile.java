// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io.vfmem;

import org.apache.derby.io.StorageRandomAccessFile;
import org.apache.derby.iapi.error.StandardException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.derby.io.StorageFile;

public class VirtualFile implements StorageFile
{
    private final String path;
    private final DataStore dStore;
    
    public VirtualFile(final String path, final DataStore dStore) {
        this.path = path;
        this.dStore = dStore;
    }
    
    public String[] list() {
        final DataStoreEntry entry = this.getEntry();
        if (entry == null || !entry.isDirectory()) {
            return null;
        }
        return this.dStore.listChildren(this.path);
    }
    
    public boolean canWrite() {
        return this.getEntry() != null && !this.getEntry().isReadOnly();
    }
    
    public boolean exists() {
        return this.getEntry() != null;
    }
    
    public boolean isDirectory() {
        final DataStoreEntry entry = this.getEntry();
        return entry != null && entry.isDirectory();
    }
    
    public boolean delete() {
        return this.dStore.deleteEntry(this.path);
    }
    
    public boolean deleteAll() {
        final DataStoreEntry entry = this.getEntry();
        if (entry == null) {
            return false;
        }
        if (entry.isDirectory()) {
            return this.dStore.deleteAll(this.path);
        }
        return this.delete();
    }
    
    public String getPath() {
        return this.path;
    }
    
    public String getCanonicalPath() {
        return this.getPath();
    }
    
    public String getName() {
        return PathUtil.getBaseName(this.path);
    }
    
    public URL getURL() throws MalformedURLException {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    public boolean createNewFile() {
        return this.dStore.createEntry(this.path, false) != null;
    }
    
    public boolean renameTo(final StorageFile storageFile) {
        return this.dStore.move(this, storageFile);
    }
    
    public boolean mkdir() {
        return this.getEntry() == null && this.dStore.createEntry(this.path, true) != null;
    }
    
    public boolean mkdirs() {
        return this.getEntry() == null && this.dStore.createAllParents(this.path) && this.dStore.createEntry(this.path, true) != null;
    }
    
    public long length() {
        final DataStoreEntry entry = this.getEntry();
        if (entry != null && !entry.isDirectory()) {
            return entry.length();
        }
        return 0L;
    }
    
    public StorageFile getParentDir() {
        final String parent = PathUtil.getParent(this.path);
        if (parent == null) {
            return null;
        }
        return new VirtualFile(parent, this.dStore);
    }
    
    public boolean setReadOnly() {
        final DataStoreEntry entry = this.getEntry();
        if (entry == null) {
            return false;
        }
        entry.setReadOnly();
        return true;
    }
    
    public OutputStream getOutputStream() throws FileNotFoundException {
        return this.getOutputStream(false);
    }
    
    public OutputStream getOutputStream(final boolean b) throws FileNotFoundException {
        DataStoreEntry dataStoreEntry = this.getEntry();
        if (dataStoreEntry == null) {
            dataStoreEntry = this.dStore.createEntry(this.path, false);
            if (dataStoreEntry == null) {
                throw new FileNotFoundException("Unable to create file: " + this.path);
            }
        }
        return dataStoreEntry.getOutputStream(b);
    }
    
    public InputStream getInputStream() throws FileNotFoundException {
        final DataStoreEntry entry = this.getEntry();
        if (entry == null) {
            throw new FileNotFoundException(this.path);
        }
        return entry.getInputStream();
    }
    
    public int getExclusiveFileLock() throws StandardException {
        return 1;
    }
    
    public void releaseExclusiveFileLock() {
    }
    
    public StorageRandomAccessFile getRandomAccessFile(final String str) throws FileNotFoundException {
        if (!str.equals("r") && !str.equals("rw") && !str.equals("rws") && !str.equals("rwd")) {
            throw new IllegalArgumentException("Invalid mode: " + str);
        }
        DataStoreEntry dataStoreEntry = this.getEntry();
        if (dataStoreEntry == null) {
            if (str.equals("r")) {
                throw new FileNotFoundException("Cannot read from non-existing file: " + this.path + " (mode=" + str + ")");
            }
            dataStoreEntry = this.dStore.createEntry(this.path, false);
            if (dataStoreEntry == null) {
                throw new FileNotFoundException("Unable to create file: " + this.path + " (mode=" + str + ")");
            }
        }
        return new VirtualRandomAccessFile(dataStoreEntry, str.equals("r"));
    }
    
    public String toString() {
        return "(db=" + this.dStore.getDatabaseName() + ")" + this.path + "#exists=" + this.exists() + ", isDirectory=" + this.isDirectory() + ", length=" + this.length() + ", canWrite=" + this.canWrite();
    }
    
    private DataStoreEntry getEntry() {
        return this.dStore.getEntry(this.path);
    }
    
    public void limitAccessToOwner() {
    }
}
