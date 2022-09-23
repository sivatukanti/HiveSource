// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.io;

import java.io.ByteArrayInputStream;

public class NonSyncByteArrayInputStream extends ByteArrayInputStream
{
    public NonSyncByteArrayInputStream() {
        super(new byte[0]);
    }
    
    public NonSyncByteArrayInputStream(final byte[] bs) {
        super(bs);
    }
    
    public NonSyncByteArrayInputStream(final byte[] buf, final int offset, final int length) {
        super(buf, offset, length);
    }
    
    public void reset(final byte[] input, final int start, final int length) {
        this.buf = input;
        this.count = start + length;
        this.mark = start;
        this.pos = start;
    }
    
    public int getPosition() {
        return this.pos;
    }
    
    public int getLength() {
        return this.count;
    }
    
    @Override
    public int read() {
        return (this.pos < this.count) ? (this.buf[this.pos++] & 0xFF) : -1;
    }
    
    @Override
    public int read(final byte[] b, final int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (this.pos >= this.count) {
            return -1;
        }
        if (this.pos + len > this.count) {
            len = this.count - this.pos;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(this.buf, this.pos, b, off, len);
        this.pos += len;
        return len;
    }
    
    @Override
    public long skip(long n) {
        if (this.pos + n > this.count) {
            n = this.count - this.pos;
        }
        if (n < 0L) {
            return 0L;
        }
        this.pos += (int)n;
        return n;
    }
    
    @Override
    public int available() {
        return this.count - this.pos;
    }
}
