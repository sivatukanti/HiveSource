// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.delta.DeltaDecoder;
import java.io.InputStream;

public class DeltaInputStream extends InputStream
{
    public static final int DISTANCE_MIN = 1;
    public static final int DISTANCE_MAX = 256;
    private InputStream in;
    private final DeltaDecoder delta;
    private IOException exception;
    
    public DeltaInputStream(final InputStream in, final int n) {
        this.exception = null;
        if (in == null) {
            throw new NullPointerException();
        }
        this.in = in;
        this.delta = new DeltaDecoder(n);
    }
    
    public int read() throws IOException {
        final byte[] array = { 0 };
        return (this.read(array, 0, 1) == -1) ? -1 : (array[0] & 0xFF);
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        int read;
        try {
            read = this.in.read(b, off, len);
        }
        catch (IOException exception) {
            throw this.exception = exception;
        }
        if (read == -1) {
            return -1;
        }
        this.delta.decode(b, off, read);
        return read;
    }
    
    public int available() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        return this.in.available();
    }
    
    public void close() throws IOException {
        if (this.in != null) {
            try {
                this.in.close();
            }
            finally {
                this.in = null;
            }
        }
    }
}
