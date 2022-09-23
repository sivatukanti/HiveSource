// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import java.io.IOException;
import java.io.InputStream;

class ReportingInputStream extends InputStream
{
    private final InputStream inputStream;
    private final ContainerListener listener;
    private int markPosition;
    private long totalBytes;
    private boolean finished;
    
    public ReportingInputStream(final InputStream inputStream, final ContainerListener listener) {
        this.markPosition = 0;
        this.totalBytes = 0L;
        this.finished = false;
        this.inputStream = inputStream;
        this.listener = listener;
    }
    
    private void report(final long bytes) {
        if (bytes == -1L) {
            this.finished = true;
            this.listener.onFinish();
        }
        else {
            this.totalBytes += bytes;
            this.listener.onReceived(bytes, this.totalBytes);
        }
    }
    
    @Override
    public int read() throws IOException {
        final int readBytes = this.inputStream.read();
        if (readBytes == -1) {
            this.report(-1L);
        }
        else {
            this.report(1L);
        }
        return readBytes;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        final int readBytes = this.inputStream.read(b);
        this.report(readBytes);
        return readBytes;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int readBytes = this.inputStream.read(b, off, len);
        this.report(readBytes);
        return readBytes;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        this.report(n);
        return this.inputStream.skip(n);
    }
    
    @Override
    public void close() throws IOException {
        if (!this.finished) {
            this.listener.onFinish();
        }
        this.inputStream.close();
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        this.markPosition = readlimit;
        this.inputStream.mark(readlimit);
    }
    
    @Override
    public synchronized void reset() throws IOException {
        this.totalBytes = this.markPosition;
        this.inputStream.reset();
    }
    
    @Override
    public boolean markSupported() {
        return this.inputStream.markSupported();
    }
    
    @Override
    public int available() throws IOException {
        return this.inputStream.available();
    }
}
