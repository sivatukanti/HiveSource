// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class LimitInputStream extends FilterInputStream implements Limit
{
    protected int remainingBytes;
    protected boolean limitInPlace;
    
    public LimitInputStream(final InputStream in) {
        super(in);
        this.clearLimit();
    }
    
    public int read() throws IOException {
        if (!this.limitInPlace) {
            return super.read();
        }
        if (this.remainingBytes == 0) {
            return -1;
        }
        final int read = super.read();
        if (read >= 0) {
            --this.remainingBytes;
        }
        return read;
    }
    
    public int read(final byte[] array, final int n, int n2) throws IOException {
        if (!this.limitInPlace) {
            return super.read(array, n, n2);
        }
        if (this.remainingBytes == 0) {
            return -1;
        }
        if (this.remainingBytes < n2) {
            n2 = this.remainingBytes;
        }
        n2 = super.read(array, n, n2);
        if (n2 > 0) {
            this.remainingBytes -= n2;
        }
        return n2;
    }
    
    public long skip(long skip) throws IOException {
        if (!this.limitInPlace) {
            return super.skip(skip);
        }
        if (this.remainingBytes == 0) {
            return 0L;
        }
        if (this.remainingBytes < skip) {
            skip = this.remainingBytes;
        }
        skip = super.skip(skip);
        this.remainingBytes -= (int)skip;
        return skip;
    }
    
    public int available() throws IOException {
        if (!this.limitInPlace) {
            return super.available();
        }
        if (this.remainingBytes == 0) {
            return 0;
        }
        final int available = super.available();
        if (this.remainingBytes < available) {
            return this.remainingBytes;
        }
        return available;
    }
    
    public void setLimit(final int remainingBytes) {
        this.remainingBytes = remainingBytes;
        this.limitInPlace = true;
    }
    
    public int clearLimit() {
        final int remainingBytes = this.remainingBytes;
        this.limitInPlace = false;
        this.remainingBytes = -1;
        return remainingBytes;
    }
    
    public void setInput(final InputStream in) {
        this.in = in;
    }
    
    public boolean markSupported() {
        return false;
    }
}
