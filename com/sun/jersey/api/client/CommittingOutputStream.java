// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import java.io.IOException;
import java.io.OutputStream;

public abstract class CommittingOutputStream extends OutputStream
{
    private OutputStream o;
    private boolean isCommitted;
    
    public CommittingOutputStream() {
    }
    
    public CommittingOutputStream(final OutputStream o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        this.o = o;
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.commitWrite();
        this.o.write(b);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.commitWrite();
        this.o.write(b, off, len);
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.commitWrite();
        this.o.write(b);
    }
    
    @Override
    public void flush() throws IOException {
        this.commitWrite();
        this.o.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.commitWrite();
        this.o.close();
    }
    
    private void commitWrite() throws IOException {
        if (!this.isCommitted) {
            this.isCommitted = true;
            this.commit();
            if (this.o == null) {
                this.o = this.getOutputStream();
            }
        }
    }
    
    protected OutputStream getOutputStream() throws IOException {
        throw new IllegalStateException();
    }
    
    protected abstract void commit() throws IOException;
}
