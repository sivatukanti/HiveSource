// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.apache.tools.ant.Task;
import java.io.IOException;
import org.apache.tools.ant.ProjectComponent;
import java.io.File;
import java.io.InputStream;

public class ConcatFileInputStream extends InputStream
{
    private static final int EOF = -1;
    private int currentIndex;
    private boolean eof;
    private File[] file;
    private InputStream currentStream;
    private ProjectComponent managingPc;
    
    public ConcatFileInputStream(final File[] file) throws IOException {
        this.currentIndex = -1;
        this.eof = false;
        this.file = file;
    }
    
    @Override
    public void close() throws IOException {
        this.closeCurrent();
        this.eof = true;
    }
    
    @Override
    public int read() throws IOException {
        int result = this.readCurrent();
        if (result == -1 && !this.eof) {
            this.openFile(++this.currentIndex);
            result = this.readCurrent();
        }
        return result;
    }
    
    public void setManagingTask(final Task task) {
        this.setManagingComponent(task);
    }
    
    public void setManagingComponent(final ProjectComponent pc) {
        this.managingPc = pc;
    }
    
    public void log(final String message, final int loglevel) {
        if (this.managingPc != null) {
            this.managingPc.log(message, loglevel);
        }
        else if (loglevel > 1) {
            System.out.println(message);
        }
        else {
            System.err.println(message);
        }
    }
    
    private int readCurrent() throws IOException {
        return (this.eof || this.currentStream == null) ? -1 : this.currentStream.read();
    }
    
    private void openFile(final int index) throws IOException {
        this.closeCurrent();
        if (this.file != null && index < this.file.length) {
            this.log("Opening " + this.file[index], 3);
            try {
                this.currentStream = new BufferedInputStream(new FileInputStream(this.file[index]));
                return;
            }
            catch (IOException eyeOhEx) {
                this.log("Failed to open " + this.file[index], 0);
                throw eyeOhEx;
            }
        }
        this.eof = true;
    }
    
    private void closeCurrent() {
        FileUtils.close(this.currentStream);
        this.currentStream = null;
    }
}
