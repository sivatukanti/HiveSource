// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.IOException;
import java.io.EOFException;
import java.io.OutputStream;

public class CounterOutputStream extends OutputStream implements Limit
{
    protected OutputStream out;
    private int count;
    private int limit;
    
    public CounterOutputStream() {
        this.limit = -1;
    }
    
    public void setOutputStream(final OutputStream out) {
        this.out = out;
        this.setLimit(-1);
    }
    
    public int getCount() {
        return this.count;
    }
    
    public void setLimit(final int limit) {
        this.count = 0;
        this.limit = limit;
    }
    
    public int clearLimit() {
        final int n = this.limit - this.count;
        this.limit = 0;
        return n;
    }
    
    public void write(final int n) throws IOException {
        if (this.limit >= 0 && this.count + 1 > this.limit) {
            throw new EOFException();
        }
        if (this.out != null) {
            this.out.write(n);
        }
        ++this.count;
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.limit >= 0 && this.count + len > this.limit) {
            throw new EOFException();
        }
        if (this.out != null) {
            this.out.write(b, off, len);
        }
        this.count += len;
    }
}
