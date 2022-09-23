// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class RememberBytesInputStream extends FilterInputStream
{
    ByteHolder bh;
    boolean recording;
    boolean streamClosed;
    
    public RememberBytesInputStream(final InputStream in, final ByteHolder bh) {
        super(in);
        this.recording = true;
        this.streamClosed = false;
        this.bh = bh;
    }
    
    public int read() throws IOException {
        int read = -1;
        if (!this.streamClosed) {
            read = super.read();
            if (read != -1) {
                this.bh.write(read);
            }
            else {
                this.streamClosed = true;
            }
        }
        return read;
    }
    
    public int read(final byte[] b, final int off, int read) throws IOException {
        if (!this.streamClosed) {
            if (read + off > b.length) {
                read = b.length - off;
            }
            read = super.read(b, off, read);
            if (read > 0) {
                this.bh.write(b, off, read);
            }
            else {
                this.streamClosed = true;
            }
            return read;
        }
        return -1;
    }
    
    public long fillBuf(final int n) throws IOException {
        long write = 0L;
        if (!this.streamClosed) {
            write = this.bh.write(this.in, n);
            if (write < n) {
                this.streamClosed = true;
            }
        }
        return write;
    }
    
    public int putBuf(final OutputStream outputStream, final int n) throws IOException {
        this.bh.startReading();
        return this.bh.read(outputStream, n);
    }
    
    public long skip(final long n) throws IOException {
        return this.bh.write(this.in, n);
    }
    
    public InputStream getReplayStream() throws IOException {
        this.bh.startReading();
        this.recording = false;
        return new ByteHolderInputStream(this.bh);
    }
    
    public ByteHolder getByteHolder() throws IOException {
        return this.bh;
    }
    
    public void clear() throws IOException {
        this.bh.clear();
        this.recording = true;
    }
    
    public void setInput(final InputStream in) {
        this.in = in;
        this.streamClosed = false;
    }
    
    public boolean recording() {
        return this.recording;
    }
    
    public int available() throws IOException {
        final int available = this.bh.available();
        return (available > 0) ? available : (-1 * available);
    }
    
    public int numBytesSaved() throws IOException {
        return this.bh.numBytesSaved();
    }
    
    public int shiftToFront() throws IOException {
        return this.bh.shiftToFront();
    }
    
    public String toString() {
        return "RememberBytesInputStream:  recording: " + this.recording + " " + this.bh;
    }
}
