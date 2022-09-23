// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

public class ReadLineInputStream extends BufferedInputStream
{
    boolean _seenCRLF;
    boolean _skipLF;
    
    public ReadLineInputStream(final InputStream in) {
        super(in);
    }
    
    public ReadLineInputStream(final InputStream in, final int size) {
        super(in, size);
    }
    
    public String readLine() throws IOException {
        this.mark(this.buf.length);
        while (true) {
            final int b = super.read();
            if (this.markpos < 0) {
                throw new IOException("Buffer size exceeded: no line terminator");
            }
            if (b == -1) {
                final int m = this.markpos;
                this.markpos = -1;
                if (this.pos > m) {
                    return new String(this.buf, m, this.pos - m, StandardCharsets.UTF_8);
                }
                return null;
            }
            else {
                if (b == 13) {
                    final int p = this.pos;
                    if (this._seenCRLF && this.pos < this.count) {
                        if (this.buf[this.pos] == 10) {
                            ++this.pos;
                        }
                    }
                    else {
                        this._skipLF = true;
                    }
                    final int i = this.markpos;
                    this.markpos = -1;
                    return new String(this.buf, i, p - i - 1, StandardCharsets.UTF_8);
                }
                if (b != 10) {
                    continue;
                }
                if (!this._skipLF) {
                    final int m = this.markpos;
                    this.markpos = -1;
                    return new String(this.buf, m, this.pos - m - 1, StandardCharsets.UTF_8);
                }
                this._skipLF = false;
                this._seenCRLF = true;
                ++this.markpos;
            }
        }
    }
    
    @Override
    public synchronized int read() throws IOException {
        int b = super.read();
        if (this._skipLF) {
            this._skipLF = false;
            if (this._seenCRLF && b == 10) {
                b = super.read();
            }
        }
        return b;
    }
    
    @Override
    public synchronized int read(final byte[] buf, final int off, final int len) throws IOException {
        if (this._skipLF && len > 0) {
            this._skipLF = false;
            if (this._seenCRLF) {
                final int b = super.read();
                if (b == -1) {
                    return -1;
                }
                if (b != 10) {
                    buf[off] = (byte)(0xFF & b);
                    return 1 + super.read(buf, off + 1, len - 1);
                }
            }
        }
        return super.read(buf, off, len);
    }
}
