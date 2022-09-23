// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DynamicByteArrayOutputStream extends OutputStream
{
    private static int INITIAL_SIZE;
    private byte[] buf;
    private int position;
    private int used;
    private int beginPosition;
    
    public DynamicByteArrayOutputStream() {
        this(DynamicByteArrayOutputStream.INITIAL_SIZE);
    }
    
    public DynamicByteArrayOutputStream(final int n) {
        this.buf = new byte[n];
    }
    
    public DynamicByteArrayOutputStream(final byte[] buf) {
        this.buf = buf;
    }
    
    public DynamicByteArrayOutputStream(final DynamicByteArrayOutputStream dynamicByteArrayOutputStream) {
        final byte[] byteArray = dynamicByteArrayOutputStream.getByteArray();
        this.buf = new byte[byteArray.length];
        this.write(byteArray, 0, byteArray.length);
        this.position = dynamicByteArrayOutputStream.getPosition();
        this.used = dynamicByteArrayOutputStream.getUsed();
        this.beginPosition = dynamicByteArrayOutputStream.getBeginPosition();
    }
    
    public void write(final int n) {
        if (this.position >= this.buf.length) {
            this.expandBuffer(DynamicByteArrayOutputStream.INITIAL_SIZE);
        }
        this.buf[this.position++] = (byte)n;
        if (this.position > this.used) {
            this.used = this.position;
        }
    }
    
    public void write(final byte[] array, final int n, final int n2) {
        if (this.position + n2 > this.buf.length) {
            this.expandBuffer(n2);
        }
        System.arraycopy(array, n, this.buf, this.position, n2);
        this.position += n2;
        if (this.position > this.used) {
            this.used = this.position;
        }
    }
    
    void writeCompleteStream(final InputStream inputStream, final int n) throws IOException {
        if (this.position + n > this.buf.length) {
            this.expandBuffer(n);
        }
        InputStreamUtil.readFully(inputStream, this.buf, this.position, n);
        this.position += n;
        if (this.position > this.used) {
            this.used = this.position;
        }
    }
    
    public void close() {
        this.buf = null;
        this.reset();
    }
    
    public void reset() {
        this.position = 0;
        this.beginPosition = 0;
        this.used = 0;
    }
    
    public byte[] getByteArray() {
        return this.buf;
    }
    
    public int getUsed() {
        return this.used;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public int getBeginPosition() {
        return this.beginPosition;
    }
    
    public void setPosition(final int position) {
        if (position > this.position && position > this.buf.length) {
            this.expandBuffer(position - this.buf.length);
        }
        this.position = position;
        if (this.position > this.used) {
            this.used = this.position;
        }
    }
    
    public void setBeginPosition(final int beginPosition) {
        if (beginPosition > this.buf.length) {
            return;
        }
        this.beginPosition = beginPosition;
    }
    
    public void discardLeft(final int n) {
        System.arraycopy(this.buf, n, this.buf, 0, this.used - n);
        this.position -= n;
        this.used -= n;
    }
    
    private void expandBuffer(int initial_SIZE) {
        if (this.buf.length < 131072) {
            if (initial_SIZE < DynamicByteArrayOutputStream.INITIAL_SIZE) {
                initial_SIZE = DynamicByteArrayOutputStream.INITIAL_SIZE;
            }
        }
        else if (this.buf.length < 1048576) {
            if (initial_SIZE < 131072) {
                initial_SIZE = 131072;
            }
        }
        else if (initial_SIZE < 1048576) {
            initial_SIZE = 1048576;
        }
        final byte[] buf = new byte[this.buf.length + initial_SIZE];
        System.arraycopy(this.buf, 0, buf, 0, this.buf.length);
        this.buf = buf;
    }
    
    static {
        DynamicByteArrayOutputStream.INITIAL_SIZE = 4096;
    }
}
