// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.simple.SimpleFilter;
import java.io.InputStream;

class SimpleInputStream extends InputStream
{
    private static final int TMPBUF_SIZE = 4096;
    private InputStream in;
    private final SimpleFilter simpleFilter;
    private final byte[] tmpbuf;
    private int pos;
    private int filtered;
    private int unfiltered;
    private boolean endReached;
    private IOException exception;
    
    static int getMemoryUsage() {
        return 5;
    }
    
    SimpleInputStream(final InputStream in, final SimpleFilter simpleFilter) {
        this.tmpbuf = new byte[4096];
        this.pos = 0;
        this.filtered = 0;
        this.unfiltered = 0;
        this.endReached = false;
        this.exception = null;
        if (in == null) {
            throw new NullPointerException();
        }
        assert simpleFilter == null;
        this.in = in;
        this.simpleFilter = simpleFilter;
    }
    
    public int read() throws IOException {
        final byte[] array = { 0 };
        return (this.read(array, 0, 1) == -1) ? -1 : (array[0] & 0xFF);
    }
    
    public int read(final byte[] array, int n, int b) throws IOException {
        if (n < 0 || b < 0 || n + b < 0 || n + b > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (b == 0) {
            return 0;
        }
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        try {
            int n2 = 0;
            while (true) {
                final int min = Math.min(this.filtered, b);
                System.arraycopy(this.tmpbuf, this.pos, array, n, min);
                this.pos += min;
                this.filtered -= min;
                n += min;
                b -= min;
                n2 += min;
                if (this.pos + this.filtered + this.unfiltered == 4096) {
                    System.arraycopy(this.tmpbuf, this.pos, this.tmpbuf, 0, this.filtered + this.unfiltered);
                    this.pos = 0;
                }
                if (b == 0 || this.endReached) {
                    return (n2 > 0) ? n2 : -1;
                }
                assert this.filtered == 0;
                final int read = this.in.read(this.tmpbuf, this.pos + this.filtered + this.unfiltered, 4096 - (this.pos + this.filtered + this.unfiltered));
                if (read == -1) {
                    this.endReached = true;
                    this.filtered = this.unfiltered;
                    this.unfiltered = 0;
                }
                else {
                    this.unfiltered += read;
                    this.filtered = this.simpleFilter.code(this.tmpbuf, this.pos, this.unfiltered);
                    assert this.filtered <= this.unfiltered;
                    this.unfiltered -= this.filtered;
                }
            }
        }
        catch (IOException exception) {
            throw this.exception = exception;
        }
    }
    
    public int available() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        return this.filtered;
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
