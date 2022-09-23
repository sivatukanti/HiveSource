// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.IOException;
import java.io.File;
import java.io.RandomAccessFile;

public class UNIXInbox extends UNIXFolder implements InboxFile
{
    String user;
    private transient RandomAccessFile lockfile;
    private String lockfileName;
    
    public UNIXInbox(final String user, final String name) {
        super(name);
        this.user = user;
    }
    
    public boolean lock(final String mode) {
        if (!UNIXInbox.loaded) {
            return false;
        }
        if (!this.maillock(this.user, 5)) {
            return false;
        }
        if (!super.lock(mode)) {
            this.mailunlock();
            return false;
        }
        return true;
    }
    
    public void unlock() {
        super.unlock();
        if (UNIXInbox.loaded) {
            this.mailunlock();
        }
    }
    
    public void touchlock() {
        if (UNIXInbox.loaded) {
            this.touchlock0();
        }
    }
    
    public boolean openLock(final String mode) {
        if (mode.equals("r")) {
            return true;
        }
        if (this.lockfileName == null) {
            final String home = System.getProperty("user.home");
            this.lockfileName = home + File.separator + ".Maillock";
        }
        try {
            this.lockfile = new RandomAccessFile(this.lockfileName, mode);
            final boolean ret = UNIXFile.lock(this.lockfile.getFD(), mode);
            if (!ret) {
                this.closeLock();
            }
            return ret;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    public void closeLock() {
        if (this.lockfile == null) {
            return;
        }
        try {
            this.lockfile.close();
        }
        catch (IOException ex) {}
        finally {
            this.lockfile = null;
        }
    }
    
    private native boolean maillock(final String p0, final int p1);
    
    private native void mailunlock();
    
    private native void touchlock0();
}
