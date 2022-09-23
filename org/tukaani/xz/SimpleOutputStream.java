// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.simple.SimpleFilter;

class SimpleOutputStream extends FinishableOutputStream
{
    private static final int TMPBUF_SIZE = 4096;
    private FinishableOutputStream out;
    private final SimpleFilter simpleFilter;
    private final byte[] tmpbuf;
    private int pos;
    private int unfiltered;
    private IOException exception;
    private boolean finished;
    
    static int getMemoryUsage() {
        return 5;
    }
    
    SimpleOutputStream(final FinishableOutputStream out, final SimpleFilter simpleFilter) {
        this.tmpbuf = new byte[4096];
        this.pos = 0;
        this.unfiltered = 0;
        this.exception = null;
        this.finished = false;
        if (out == null) {
            throw new NullPointerException();
        }
        this.out = out;
        this.simpleFilter = simpleFilter;
    }
    
    public void write(final int n) throws IOException {
        this.write(new byte[] { (byte)n }, 0, 1);
    }
    
    public void write(final byte[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i < 0 || n + i > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        while (i > 0) {
            final int min = Math.min(i, 4096 - (this.pos + this.unfiltered));
            System.arraycopy(array, n, this.tmpbuf, this.pos + this.unfiltered, min);
            n += min;
            i -= min;
            this.unfiltered += min;
            final int code = this.simpleFilter.code(this.tmpbuf, this.pos, this.unfiltered);
            assert code <= this.unfiltered;
            this.unfiltered -= code;
            try {
                this.out.write(this.tmpbuf, this.pos, code);
            }
            catch (IOException exception) {
                throw this.exception = exception;
            }
            this.pos += code;
            if (this.pos + this.unfiltered != 4096) {
                continue;
            }
            System.arraycopy(this.tmpbuf, this.pos, this.tmpbuf, 0, this.unfiltered);
            this.pos = 0;
        }
    }
    
    private void writePending() throws IOException {
        assert !this.finished;
        if (this.exception != null) {
            throw this.exception;
        }
        try {
            this.out.write(this.tmpbuf, this.pos, this.unfiltered);
        }
        catch (IOException exception) {
            throw this.exception = exception;
        }
        this.finished = true;
    }
    
    public void flush() throws IOException {
        throw new UnsupportedOptionsException("Flushing is not supported");
    }
    
    public void finish() throws IOException {
        if (!this.finished) {
            this.writePending();
            try {
                this.out.finish();
            }
            catch (IOException exception) {
                throw this.exception = exception;
            }
        }
    }
    
    public void close() throws IOException {
        if (this.out != null) {
            if (!this.finished) {
                try {
                    this.writePending();
                }
                catch (IOException ex) {}
            }
            try {
                this.out.close();
            }
            catch (IOException exception) {
                if (this.exception == null) {
                    this.exception = exception;
                }
            }
            this.out = null;
        }
        if (this.exception != null) {
            throw this.exception;
        }
    }
}
