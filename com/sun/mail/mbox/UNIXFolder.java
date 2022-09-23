// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.RandomAccessFile;

public class UNIXFolder extends UNIXFile implements MailFile
{
    protected transient RandomAccessFile file;
    
    public UNIXFolder(final String name) {
        super(name);
    }
    
    public boolean lock(final String mode) {
        try {
            this.file = new RandomAccessFile(this, mode);
            return UNIXFile.lock(this.file.getFD(), mode);
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
