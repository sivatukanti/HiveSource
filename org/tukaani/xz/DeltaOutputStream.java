// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.IOException;
import org.tukaani.xz.delta.DeltaEncoder;

class DeltaOutputStream extends FinishableOutputStream
{
    private static final int TMPBUF_SIZE = 4096;
    private FinishableOutputStream out;
    private final DeltaEncoder delta;
    private final byte[] tmpbuf;
    private boolean finished;
    private IOException exception;
    
    static int getMemoryUsage() {
        return 5;
    }
    
    DeltaOutputStream(final FinishableOutputStream out, final DeltaOptions deltaOptions) {
        this.tmpbuf = new byte[4096];
        this.finished = false;
        this.exception = null;
        this.out = out;
        this.delta = new DeltaEncoder(deltaOptions.getDistance());
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
            throw new XZIOException("Stream finished");
        }
        try {
            while (i > 4096) {
                this.delta.encode(array, n, 4096, this.tmpbuf);
                this.out.write(this.tmpbuf);
                n += 4096;
                i -= 4096;
            }
            this.delta.encode(array, n, i, this.tmpbuf);
            this.out.write(this.tmpbuf, 0, i);
        }
        catch (IOException exception) {
            throw this.exception = exception;
        }
    }
    
    public void flush() throws IOException {
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.finished) {
            throw new XZIOException("Stream finished or closed");
        }
        try {
            this.out.flush();
        }
        catch (IOException exception) {
            throw this.exception = exception;
        }
    }
    
    public void finish() throws IOException {
        if (!this.finished) {
            if (this.exception != null) {
                throw this.exception;
            }
            try {
                this.out.finish();
            }
            catch (IOException exception) {
                throw this.exception = exception;
            }
            this.finished = true;
        }
    }
    
    public void close() throws IOException {
        if (this.out != null) {
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
