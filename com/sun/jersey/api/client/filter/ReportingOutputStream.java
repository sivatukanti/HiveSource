// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import java.io.IOException;
import java.io.OutputStream;

class ReportingOutputStream extends OutputStream
{
    private final OutputStream outputStream;
    private final ContainerListener listener;
    private long totalBytes;
    
    public ReportingOutputStream(final OutputStream outputStream, final ContainerListener listener) {
        this.totalBytes = 0L;
        this.outputStream = outputStream;
        this.listener = listener;
    }
    
    private void report(final long bytes) {
        this.totalBytes += bytes;
        this.listener.onSent(bytes, this.totalBytes);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.outputStream.write(b);
        this.report(b.length);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.outputStream.write(b, off, len);
        this.report(len);
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.outputStream.write(b);
        this.report(1L);
    }
    
    @Override
    public void flush() throws IOException {
        this.outputStream.flush();
    }
}
