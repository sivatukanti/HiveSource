// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.apache.tools.ant.Task;
import java.io.IOException;
import java.io.PipedOutputStream;
import org.apache.tools.ant.ProjectComponent;
import java.io.PipedInputStream;

public class LeadPipeInputStream extends PipedInputStream
{
    private static final int BYTE_MASK = 255;
    private ProjectComponent managingPc;
    
    public LeadPipeInputStream() {
    }
    
    public LeadPipeInputStream(final int size) {
        this.setBufferSize(size);
    }
    
    public LeadPipeInputStream(final PipedOutputStream src) throws IOException {
        super(src);
    }
    
    public LeadPipeInputStream(final PipedOutputStream src, final int size) throws IOException {
        super(src);
        this.setBufferSize(size);
    }
    
    @Override
    public synchronized int read() throws IOException {
        int result = -1;
        try {
            result = super.read();
        }
        catch (IOException eyeOhEx) {
            final String msg = eyeOhEx.getMessage();
            if ("write end dead".equalsIgnoreCase(msg) || "pipe broken".equalsIgnoreCase(msg)) {
                if (super.in > 0 && super.out < super.buffer.length && super.out > super.in) {
                    result = (super.buffer[super.out++] & 0xFF);
                }
            }
            else {
                this.log("error at LeadPipeInputStream.read():  " + msg, 2);
            }
        }
        return result;
    }
    
    public synchronized void setBufferSize(final int size) {
        if (size > this.buffer.length) {
            final byte[] newBuffer = new byte[size];
            if (this.in >= 0) {
                if (this.in > this.out) {
                    System.arraycopy(this.buffer, this.out, newBuffer, this.out, this.in - this.out);
                }
                else {
                    final int outlen = this.buffer.length - this.out;
                    System.arraycopy(this.buffer, this.out, newBuffer, 0, outlen);
                    System.arraycopy(this.buffer, 0, newBuffer, outlen, this.in);
                    this.in += outlen;
                    this.out = 0;
                }
            }
            this.buffer = newBuffer;
        }
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
}
