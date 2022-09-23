// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.io;

import org.apache.derby.io.StorageRandomAccessFile;
import org.apache.derby.iapi.error.StandardException;
import java.nio.channels.OverlappingFileLockException;
import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import org.apache.derby.iapi.util.InterruptStatus;
import java.io.FileNotFoundException;
import org.apache.derby.iapi.services.io.FileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.derby.io.StorageFile;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;
import java.io.RandomAccessFile;

class DirFile4 extends DirFile
{
    private RandomAccessFile lockFileOpen;
    private FileChannel lockFileChannel;
    private FileLock dbLock;
    
    DirFile4(final String s) {
        super(s);
    }
    
    DirFile4(final String s, final String s2) {
        super(s, s2);
    }
    
    DirFile4(final DirFile dirFile, final String s) {
        super(dirFile, s);
    }
    
    public StorageFile getParentDir() {
        final String parent = this.getParent();
        if (parent == null) {
            return null;
        }
        return new DirFile4(parent);
    }
    
    public OutputStream getOutputStream(final boolean append) throws FileNotFoundException {
        final boolean exists = this.exists();
        final FileOutputStream fileOutputStream = new FileOutputStream(this, append);
        if (!exists) {
            FileUtil.limitAccessToOwner(this);
        }
        return fileOutputStream;
    }
    
    public synchronized int getExclusiveFileLock() throws StandardException {
        boolean b = false;
        int n2;
        try {
            if (this.createNewFile()) {
                b = true;
            }
            else if (this.length() > 0L) {
                b = true;
            }
            if (b) {
                int n = 120;
                while (true) {
                    this.lockFileOpen = new RandomAccessFile(this, "rw");
                    this.limitAccessToOwner();
                    this.lockFileChannel = this.lockFileOpen.getChannel();
                    try {
                        this.dbLock = this.lockFileChannel.tryLock();
                        if (this.dbLock == null) {
                            this.lockFileChannel.close();
                            this.lockFileChannel = null;
                            this.lockFileOpen.close();
                            this.lockFileOpen = null;
                            n2 = 2;
                        }
                        else {
                            this.lockFileOpen.writeInt(1);
                            this.lockFileChannel.force(true);
                            n2 = 1;
                        }
                    }
                    catch (AsynchronousCloseException ex) {
                        InterruptStatus.setInterrupted();
                        this.lockFileOpen.close();
                        if (n-- > 0) {
                            continue;
                        }
                        throw ex;
                    }
                    break;
                }
            }
            else {
                n2 = 0;
            }
        }
        catch (IOException ex2) {
            this.releaseExclusiveFileLock();
            n2 = 0;
        }
        catch (OverlappingFileLockException ex3) {
            try {
                this.lockFileChannel.close();
                this.lockFileOpen.close();
            }
            catch (IOException ex4) {}
            this.lockFileChannel = null;
            this.lockFileOpen = null;
            n2 = 2;
        }
        return n2;
    }
    
    public synchronized void releaseExclusiveFileLock() {
        try {
            if (this.dbLock != null) {
                this.dbLock.release();
                this.dbLock = null;
            }
            if (this.lockFileChannel != null) {
                this.lockFileChannel.close();
                this.lockFileChannel = null;
            }
            if (this.lockFileOpen != null) {
                this.lockFileOpen.close();
                this.lockFileOpen = null;
            }
            super.releaseExclusiveFileLock();
        }
        catch (IOException ex) {}
    }
    
    public StorageRandomAccessFile getRandomAccessFile(final String s) throws FileNotFoundException {
        return new DirRandomAccessFile(this, s);
    }
}
