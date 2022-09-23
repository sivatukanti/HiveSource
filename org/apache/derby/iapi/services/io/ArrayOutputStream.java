// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.IOException;
import java.io.EOFException;
import java.io.OutputStream;

public class ArrayOutputStream extends OutputStream implements Limit
{
    private byte[] pageData;
    private int start;
    private int end;
    private int position;
    
    public ArrayOutputStream() {
    }
    
    public ArrayOutputStream(final byte[] data) {
        this.setData(data);
    }
    
    public void setData(final byte[] pageData) {
        this.pageData = pageData;
        this.start = 0;
        if (pageData != null) {
            this.end = pageData.length;
        }
        else {
            this.end = 0;
        }
        this.position = 0;
    }
    
    public void write(final int n) throws IOException {
        if (this.position >= this.end) {
            throw new EOFException();
        }
        this.pageData[this.position++] = (byte)n;
    }
    
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        if (this.position + n2 > this.end) {
            throw new EOFException();
        }
        System.arraycopy(array, n, this.pageData, this.position, n2);
        this.position += n2;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public void setPosition(final int position) throws IOException {
        if (position < this.start || position > this.end) {
            throw new EOFException();
        }
        this.position = position;
    }
    
    public void setLimit(final int n) throws IOException {
        if (n < 0) {
            throw new EOFException();
        }
        if (this.position + n > this.end) {
            throw new EOFException();
        }
        this.start = this.position;
        this.end = this.position + n;
    }
    
    public int clearLimit() {
        final int n = this.end - this.position;
        this.end = this.pageData.length;
        return n;
    }
}
