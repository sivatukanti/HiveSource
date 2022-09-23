// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.lz;

import java.io.DataInputStream;
import java.io.IOException;
import org.tukaani.xz.CorruptedInputException;

public final class LZDecoder
{
    private byte[] buf;
    private int start;
    private int pos;
    private int full;
    private int limit;
    private int pendingLen;
    private int pendingDist;
    
    public LZDecoder(final int b, final byte[] array) {
        this.start = 0;
        this.pos = 0;
        this.full = 0;
        this.limit = 0;
        this.pendingLen = 0;
        this.pendingDist = 0;
        this.buf = new byte[b];
        if (array != null) {
            this.pos = Math.min(array.length, b);
            this.full = this.pos;
            this.start = this.pos;
            System.arraycopy(array, array.length - this.pos, this.buf, 0, this.pos);
        }
    }
    
    public void reset() {
        this.start = 0;
        this.pos = 0;
        this.full = 0;
        this.limit = 0;
        this.buf[this.buf.length - 1] = 0;
    }
    
    public void setLimit(final int n) {
        if (this.buf.length - this.pos <= n) {
            this.limit = this.buf.length;
        }
        else {
            this.limit = this.pos + n;
        }
    }
    
    public boolean hasSpace() {
        return this.pos < this.limit;
    }
    
    public boolean hasPending() {
        return this.pendingLen > 0;
    }
    
    public int getPos() {
        return this.pos;
    }
    
    public int getByte(final int n) {
        int n2 = this.pos - n - 1;
        if (n >= this.pos) {
            n2 += this.buf.length;
        }
        return this.buf[n2] & 0xFF;
    }
    
    public void putByte(final byte b) {
        this.buf[this.pos++] = b;
        if (this.full < this.pos) {
            this.full = this.pos;
        }
    }
    
    public void repeat(final int pendingDist, final int b) throws IOException {
        if (pendingDist < 0 || pendingDist >= this.full) {
            throw new CorruptedInputException();
        }
        int min = Math.min(this.limit - this.pos, b);
        this.pendingLen = b - min;
        this.pendingDist = pendingDist;
        int n = this.pos - pendingDist - 1;
        if (pendingDist >= this.pos) {
            n += this.buf.length;
        }
        do {
            this.buf[this.pos++] = this.buf[n++];
            if (n == this.buf.length) {
                n = 0;
            }
        } while (--min > 0);
        if (this.full < this.pos) {
            this.full = this.pos;
        }
    }
    
    public void repeatPending() throws IOException {
        if (this.pendingLen > 0) {
            this.repeat(this.pendingDist, this.pendingLen);
        }
    }
    
    public void copyUncompressed(final DataInputStream dataInputStream, final int b) throws IOException {
        final int min = Math.min(this.buf.length - this.pos, b);
        dataInputStream.readFully(this.buf, this.pos, min);
        this.pos += min;
        if (this.full < this.pos) {
            this.full = this.pos;
        }
    }
    
    public int flush(final byte[] array, final int n) {
        final int n2 = this.pos - this.start;
        if (this.pos == this.buf.length) {
            this.pos = 0;
        }
        System.arraycopy(this.buf, this.start, array, n, n2);
        this.start = this.pos;
        return n2;
    }
}
