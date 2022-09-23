// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.io.File;
import java.io.IOException;
import org.apache.derby.io.StorageFile;
import org.apache.derby.io.StorageFactory;

abstract class BaseStorageFactory implements StorageFactory
{
    String home;
    protected StorageFile tempDir;
    protected String tempDirPath;
    protected String dataDirectory;
    protected String separatedDataDirectory;
    protected String uniqueName;
    protected String canonicalName;
    private static final String TEMP_DIR_PREFIX = "derbytmp_";
    
    public void init(final String home, final String s, final String tempDirPath, final String uniqueName) throws IOException {
        if (s != null) {
            this.dataDirectory = s;
            this.separatedDataDirectory = s + this.getSeparator();
        }
        this.home = home;
        this.uniqueName = uniqueName;
        this.tempDirPath = tempDirPath;
        this.doInit();
    }
    
    abstract void doInit() throws IOException;
    
    public void shutdown() {
    }
    
    public String getCanonicalName() throws IOException {
        return this.canonicalName;
    }
    
    public void setCanonicalName(final String canonicalName) {
        this.canonicalName = canonicalName;
    }
    
    public StorageFile newStorageFile(final String s) {
        if (s != null && this.tempDirPath != null && s.startsWith(this.tempDirPath)) {
            return new DirFile(s);
        }
        return this.newPersistentFile(s);
    }
    
    public StorageFile newStorageFile(final String s, final String s2) {
        if (s == null) {
            return this.newStorageFile(s2);
        }
        if (this.tempDirPath != null && s.startsWith(this.tempDirPath)) {
            return new DirFile(s, s2);
        }
        return this.newPersistentFile(s, s2);
    }
    
    public StorageFile newStorageFile(final StorageFile storageFile, final String s) {
        if (storageFile == null) {
            return this.newStorageFile(s);
        }
        if (s == null) {
            return storageFile;
        }
        if (this.tempDirPath != null && storageFile.getPath().startsWith(this.tempDirPath)) {
            return new DirFile((DirFile)storageFile, s);
        }
        return this.newPersistentFile(storageFile, s);
    }
    
    abstract StorageFile newPersistentFile(final String p0);
    
    abstract StorageFile newPersistentFile(final String p0, final String p1);
    
    abstract StorageFile newPersistentFile(final StorageFile p0, final String p1);
    
    public char getSeparator() {
        return File.separatorChar;
    }
    
    public StorageFile getTempDir() {
        return this.tempDir;
    }
    
    public boolean isFast() {
        return false;
    }
    
    public boolean isReadOnlyDatabase() {
        return true;
    }
    
    public boolean supportsRandomAccess() {
        return false;
    }
    
    void createTempDir() throws IOException {
        if (this.uniqueName == null) {
            return;
        }
        if (this.tempDirPath != null) {
            this.tempDir = new DirFile(this.tempDirPath, "derbytmp_".concat(this.uniqueName));
        }
        else if (this.isReadOnlyDatabase()) {
            this.tempDir = new DirFile(this.readOnlyTempRoot(), "derbytmp_".concat(this.uniqueName));
        }
        else {
            this.tempDir = new DirFile(this.canonicalName, "tmp");
        }
        this.tempDir.deleteAll();
        this.tempDir.mkdirs();
        this.tempDir.limitAccessToOwner();
        this.tempDirPath = this.tempDir.getPath();
    }
    
    private String readOnlyTempRoot() throws IOException {
        final File tempFile = File.createTempFile("derby", "tmp");
        final String parent = tempFile.getParent();
        tempFile.delete();
        return parent;
    }
    
    public int getStorageFactoryVersion() {
        return 1;
    }
    
    public StorageFile createTemporaryFile(final String prefix, final String suffix) throws IOException {
        return this.newStorageFile(this.getTempDir(), File.createTempFile(prefix, suffix, new File(this.getTempDir().getPath())).getName());
    }
}
