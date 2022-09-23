// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.File;

class DefaultMailFile extends File implements MailFile
{
    protected transient RandomAccessFile file;
    
    DefaultMailFile(final String name) {
        super(name);
    }
    
    public boolean lock(final String mode) {
        try {
            this.file = new RandomAccessFile(this, mode);
            return true;
        }
        catch (FileNotFoundException fe) {
            return false;
        }
        catch (IOException ie) {
            this.file = null;
            return false;
        }
    }
    
    public void unlock() {
        if (this.file != null) {
            try {
                this.file.close();
            }
            catch (IOException ex) {}
            this.file = null;
        }
    }
    
    public void touchlock() {
    }
    
    public FileDescriptor getFD() {
        if (this.file == null) {
            return null;
        }
        try {
            return this.file.getFD();
        }
        catch (IOException e) {
            return null;
        }
    }
}
