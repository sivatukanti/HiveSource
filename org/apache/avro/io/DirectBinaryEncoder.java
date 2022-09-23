// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.io.IOException;
import java.io.OutputStream;

public class DirectBinaryEncoder extends BinaryEncoder
{
    private OutputStream out;
    private final byte[] buf;
    
    DirectBinaryEncoder(final OutputStream out) {
        this.buf = new byte[12];
        this.configure(out);
    }
    
    DirectBinaryEncoder configure(final OutputStream out) {
        if (null == out) {
            throw new NullPointerException("OutputStream cannot be null!");
        }
        this.out = out;
        return this;
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void writeBoolean(final boolean b) throws IOException {
        this.out.write(b ? 1 : 0);
    }
    
    @Override
    public void writeInt(final int n) throws IOException {
        final int val = n << 1 ^ n >> 31;
        if ((val & 0xFFFFFF80) == 0x0) {
            this.out.write(val);
            return;
        }
        if ((val & 0xFFFFC000) == 0x0) {
            this.out.write(0x80 | val);
            this.out.write(val >>> 7);
            return;
        }
        final int len = BinaryData.encodeInt(n, this.buf, 0);
        this.out.write(this.buf, 0, len);
    }
    
    @Override
    public void writeLong(final long n) throws IOException {
        final long val = n << 1 ^ n >> 63;
        if ((val & 0xFFFFFFFF80000000L) == 0x0L) {
            int i;
            for (i = (int)val; (i & 0xFFFFFF80) != 0x0; i >>>= 7) {
                this.out.write((byte)((0x80 | i) & 0xFF));
            }
            this.out.write((byte)i);
            return;
        }
        final int len = BinaryData.encodeLong(n, this.buf, 0);
        this.out.write(this.buf, 0, len);
    }
    
    @Override
    public void writeFloat(final float f) throws IOException {
        final int len = BinaryData.encodeFloat(f, this.buf, 0);
        this.out.write(this.buf, 0, len);
    }
    
    @Override
    public void writeDouble(final double d) throws IOException {
        final byte[] buf = new byte[8];
        final int len = BinaryData.encodeDouble(d, buf, 0);
        this.out.write(buf, 0, len);
    }
    
    @Override
    public void writeFixed(final byte[] bytes, final int start, final int len) throws IOException {
        this.out.write(bytes, start, len);
    }
    
    @Override
    protected void writeZero() throws IOException {
        this.out.write(0);
    }
    
    @Override
    public int bytesBuffered() {
        return 0;
    }
}
