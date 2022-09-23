// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.util.HashMap;
import java.io.OutputStream;
import org.apache.derby.impl.io.vfmem.PathUtil;
import java.io.IOException;
import org.apache.derby.impl.io.vfmem.VirtualFile;
import java.io.File;
import org.apache.derby.io.StorageFile;
import org.apache.derby.impl.io.vfmem.DataStore;
import java.util.Map;
import org.apache.derby.io.WritableStorageFactory;
import org.apache.derby.io.StorageFactory;

public class VFMemoryStorageFactory implements StorageFactory, WritableStorageFactory
{
    private static final Map DATABASES;
    private static final DataStore DUMMY_STORE;
    private String canonicalName;
    private StorageFile dataDirectory;
    private StorageFile tempDir;
    private DataStore dbData;
    
    public void init(final String s, final String pathname, final String s2, final String s3) throws IOException {
        if (pathname != null) {
            if (s != null && !new File(pathname).isAbsolute()) {
                this.canonicalName = new File(s, pathname).getCanonicalPath();
            }
            else {
                this.canonicalName = new File(pathname).getCanonicalPath();
            }
            synchronized (VFMemoryStorageFactory.DATABASES) {
                this.dbData = VFMemoryStorageFactory.DATABASES.get(this.canonicalName);
                if (this.dbData != null && this.dbData.scheduledForDeletion()) {
                    VFMemoryStorageFactory.DATABASES.remove(this.canonicalName);
                    this.dbData.purge();
                    this.dbDropCleanupInDummy(this.canonicalName);
                    this.dbData = null;
                }
                if (this.dbData == null) {
                    if (s3 != null) {
                        this.dbData = new DataStore(this.canonicalName);
                        VFMemoryStorageFactory.DATABASES.put(this.canonicalName, this.dbData);
                    }
                    else {
                        this.dbData = VFMemoryStorageFactory.DUMMY_STORE;
                    }
                }
            }
            this.dataDirectory = new VirtualFile(this.canonicalName, this.dbData);
            this.tempDir = new VirtualFile(this.normalizePath(this.canonicalName, "tmp"), this.dbData);
        }
        else if (s != null) {
            final String canonicalPath = new File(s).getCanonicalPath();
            this.dbData = VFMemoryStorageFactory.DUMMY_STORE;
            this.dataDirectory = new VirtualFile(canonicalPath, this.dbData);
            this.tempDir = new VirtualFile(this.getSeparator() + "tmp", this.dbData);
        }
        if (s3 != null && this.tempDir != null && !this.tempDir.exists()) {
            this.tempDir.mkdirs();
            this.tempDir.limitAccessToOwner();
        }
    }
    
    public void shutdown() {
        if (this.dbData.scheduledForDeletion()) {
            final DataStore dataStore;
            synchronized (VFMemoryStorageFactory.DATABASES) {
                dataStore = VFMemoryStorageFactory.DATABASES.remove(this.canonicalName);
                if (dataStore != null && dataStore == this.dbData) {
                    this.dbDropCleanupInDummy(this.canonicalName);
                }
            }
            if (dataStore != null && dataStore == this.dbData) {
                this.dbData.purge();
                this.dbData = null;
            }
        }
    }
    
    public String getCanonicalName() {
        return this.canonicalName;
    }
    
    public void setCanonicalName(final String canonicalName) {
        this.canonicalName = canonicalName;
    }
    
    public StorageFile newStorageFile(final String s) {
        if (s == null) {
            return this.dataDirectory;
        }
        return new VirtualFile(this.normalizePath(s), this.dbData);
    }
    
    public StorageFile newStorageFile(final String s, final String s2) {
        return new VirtualFile(this.normalizePath(s, s2), this.dbData);
    }
    
    public StorageFile newStorageFile(final StorageFile storageFile, final String s) {
        return this.newStorageFile((storageFile == null) ? null : storageFile.getPath(), s);
    }
    
    public StorageFile getTempDir() {
        return this.tempDir;
    }
    
    public boolean isFast() {
        return true;
    }
    
    public boolean isReadOnlyDatabase() {
        return false;
    }
    
    public boolean supportsRandomAccess() {
        return true;
    }
    
    public int getStorageFactoryVersion() {
        return 1;
    }
    
    public StorageFile createTemporaryFile(final String str, String s) {
        if (s == null) {
            s = ".tmp";
        }
        String s2;
        if (str == null) {
            s2 = this.dbData.getTempFileCounter() + s;
        }
        else {
            s2 = str + this.dbData.getTempFileCounter() + s;
        }
        return this.newStorageFile(this.tempDir, s2);
    }
    
    public char getSeparator() {
        return PathUtil.SEP;
    }
    
    public void sync(final OutputStream outputStream, final boolean b) {
    }
    
    public boolean supportsWriteSync() {
        return true;
    }
    
    private String normalizePath(String parent, final String child) {
        if (parent == null || parent.length() == 0) {
            parent = this.dataDirectory.getPath();
        }
        else if (!new File(parent).isAbsolute()) {
            parent = new File(this.dataDirectory.getPath(), parent).getPath();
        }
        return new File(parent, child).getPath();
    }
    
    private String normalizePath(final String s) {
        if (s == null || s.length() == 0) {
            return this.dataDirectory.getPath();
        }
        if (new File(s).isAbsolute()) {
            return s;
        }
        return new File(this.dataDirectory.getPath(), s).getPath();
    }
    
    private void dbDropCleanupInDummy(String parent) {
        while (parent != null && VFMemoryStorageFactory.DUMMY_STORE.deleteEntry(parent)) {
            parent = new File(parent).getParent();
        }
    }
    
    static {
        DATABASES = new HashMap();
        DUMMY_STORE = new DataStore("::DUMMY::");
    }
}
