// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.io;

import java.io.OutputStream;
import java.io.IOException;
import java.io.DataInput;
import java.io.ByteArrayOutputStream;

public class NonSyncByteArrayOutputStream extends ByteArrayOutputStream
{
    public NonSyncByteArrayOutputStream(final int size) {
        super(size);
    }
    
    public NonSyncByteArrayOutputStream() {
    }
    
    public byte[] getData() {
        return this.buf;
    }
    
    public int getLength() {
        return this.count;
    }
    
    @Override
    public void reset() {
        this.count = 0;
    }
    
    public void write(final DataInput in, final int length) throws IOException {
        this.enLargeBuffer(length);
        in.readFully(this.buf, this.count, length);
        this.count += length;
    }
    
    @Override
    public void write(final int b) {
        this.enLargeBuffer(1);
        this.buf[this.count] = (byte)b;
        ++this.count;
    }
    
    private int enLargeBuffer(final int increment) {
        int newLen;
        final int temp = newLen = this.count + increment;
        if (temp > this.buf.length) {
            if (this.buf.length << 1 > temp) {
                newLen = this.buf.length << 1;
            }
            final byte[] newbuf = new byte[newLen];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
        return newLen;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        this.enLargeBuffer(len);
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count += len;
    }
    
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        out.write(this.buf, 0, this.count);
    }
}
