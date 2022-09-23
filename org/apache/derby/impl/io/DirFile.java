// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.io.StorageRandomAccessFile;
import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.apache.derby.iapi.services.io.FileUtil;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.derby.io.StorageFile;
import java.io.File;

class DirFile extends File implements StorageFile
{
    DirFile(final String pathname) {
        super(pathname);
    }
    
    DirFile(final String parent, final String child) {
        super(parent, child);
    }
    
    DirFile(final DirFile parent, final String child) {
        super(parent, child);
    }
    
    public StorageFile getParentDir() {
        final String parent = this.getParent();
        if (parent == null) {
            return null;
        }
        return new DirFile(parent);
    }
    
    public OutputStream getOutputStream() throws FileNotFoundException {
        final boolean exists = this.exists();
        final FileOutputStream fileOutputStream = new FileOutputStream(this);
        if (!exists) {
            FileUtil.limitAccessToOwner(this);
        }
        return fileOutputStream;
    }
    
    public OutputStream getOutputStream(final boolean append) throws FileNotFoundException {
        final boolean exists = this.exists();
        final FileOutputStream fileOutputStream = new FileOutputStream(this.getPath(), append);
        if (!exists) {
            FileUtil.limitAccessToOwner(this);
        }
        return fileOutputStream;
    }
    
    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(this);
    }
    
    public synchronized int getExclusiveFileLock() throws StandardException {
        if (this.exists()) {
            this.delete();
        }
        try {
            final RandomAccessFile randomAccessFile = new RandomAccessFile(this, "rw");
            this.limitAccessToOwner();
            randomAccessFile.getFD().sync();
            randomAccessFile.close();
        }
        catch (IOException ex) {}
        return 0;
    }
    
    public synchronized void releaseExclusiveFileLock() {
        if (this.exists()) {
            this.delete();
        }
    }
    
    public StorageRandomAccessFile getRandomAccessFile(String s) throws FileNotFoundException {
        if ("rws".equals(s) || "rwd".equals(s)) {
            s = "rw";
        }
        return new DirRandomAccessFile(this, s);
    }
    
    public boolean renameTo(final StorageFile storageFile) {
        boolean b = super.renameTo((File)storageFile);
        for (int n = 1; !b && n <= 5; b = super.renameTo((File)storageFile), ++n) {
            try {
                Thread.sleep(1000 * n);
            }
            catch (InterruptedException ex) {
                InterruptStatus.setInterrupted();
            }
        }
        return b;
    }
    
    public boolean deleteAll() {
        if (!this.exists()) {
            return false;
        }
        final String[] list = super.list();
        if (list != null) {
            final String path = this.getPath();
            for (int i = 0; i < list.length; ++i) {
                if (!list[i].equals(".")) {
                    if (!list[i].equals("..")) {
                        if (!new DirFile(path, list[i]).deleteAll()) {
                            return false;
                        }
                    }
                }
            }
        }
        return this.delete();
    }
    
    public URL getURL() throws MalformedURLException {
        return this.toURL();
    }
    
    public void limitAccessToOwner() {
        FileUtil.limitAccessToOwner(this);
    }
}
