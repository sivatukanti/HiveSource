// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.EOFException;
import org.apache.avro.util.ByteBufferInputStream;
import java.io.InputStream;

class DirectBinaryDecoder extends BinaryDecoder
{
    private InputStream in;
    private ByteReader byteReader;
    private final byte[] buf;
    
    DirectBinaryDecoder(final InputStream in) {
        this.buf = new byte[8];
        this.configure(in);
    }
    
    DirectBinaryDecoder configure(final InputStream in) {
        this.in = in;
        this.byteReader = ((in instanceof ByteBufferInputStream) ? new ReuseByteReader((ByteBufferInputStream)in) : new ByteReader());
        return this;
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        final int n = this.in.read();
        if (n < 0) {
            throw new EOFException();
        }
        return n == 1;
    }
    
    @Override
    public int readInt() throws IOException {
        int n = 0;
        int shift = 0;
        do {
            final int b = this.in.read();
            if (b < 0) {
                throw new EOFException();
            }
            n |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0x0) {
                return n >>> 1 ^ -(n & 0x1);
            }
            shift += 7;
        } while (shift < 32);
        throw new IOException("Invalid int encoding");
    }
    
    @Override
    public long readLong() throws IOException {
        long n = 0L;
        int shift = 0;
        do {
            final int b = this.in.read();
            if (b < 0) {
                throw new EOFException();
            }
            n |= ((long)b & 0x7FL) << shift;
            if ((b & 0x80) == 0x0) {
                return n >>> 1 ^ -(n & 0x1L);
            }
            shift += 7;
        } while (shift < 64);
        throw new IOException("Invalid long encoding");
    }
    
    @Override
    public float readFloat() throws IOException {
        this.doReadBytes(this.buf, 0, 4);
        final int n = (this.buf[0] & 0xFF) | (this.buf[1] & 0xFF) << 8 | (this.buf[2] & 0xFF) << 16 | (this.buf[3] & 0xFF) << 24;
        return Float.intBitsToFloat(n);
    }
    
    @Override
    public double readDouble() throws IOException {
        this.doReadBytes(this.buf, 0, 8);
        final long n = ((long)this.buf[0] & 0xFFL) | ((long)this.buf[1] & 0xFFL) << 8 | ((long)this.buf[2] & 0xFFL) << 16 | ((long)this.buf[3] & 0xFFL) << 24 | ((long)this.buf[4] & 0xFFL) << 32 | ((long)this.buf[5] & 0xFFL) << 40 | ((long)this.buf[6] & 0xFFL) << 48 | ((long)this.buf[7] & 0xFFL) << 56;
        return Double.longBitsToDouble(n);
    }
    
    @Override
    public ByteBuffer readBytes(final ByteBuffer old) throws IOException {
        final int length = this.readInt();
        return this.byteReader.read(old, length);
    }
    
    @Override
    protected void doSkipBytes(long length) throws IOException {
        while (length > 0L) {
            final long n = this.in.skip(length);
            if (n <= 0L) {
                throw new EOFException();
            }
            length -= n;
        }
    }
    
    @Override
    protected void doReadBytes(final byte[] bytes, int start, int length) throws IOException {
        while (true) {
            final int n = this.in.read(bytes, start, length);
            if (n == length || length == 0) {
                return;
            }
            if (n < 0) {
                throw new EOFException();
            }
            start += n;
            length -= n;
        }
    }
    
    @Override
    public InputStream inputStream() {
        return this.in;
    }
    
    @Override
    public boolean isEnd() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    private class ByteReader
    {
        public ByteBuffer read(final ByteBuffer old, final int length) throws IOException {
            ByteBuffer result;
            if (old != null && length <= old.capacity()) {
                result = old;
                result.clear();
            }
            else {
                result = ByteBuffer.allocate(length);
            }
            DirectBinaryDecoder.this.doReadBytes(result.array(), result.position(), length);
            result.limit(length);
            return result;
        }
    }
    
    private class ReuseByteReader extends ByteReader
    {
        private final ByteBufferInputStream bbi;
        
        public ReuseByteReader(final ByteBufferInputStream bbi) {
            this.bbi = bbi;
        }
        
        @Override
        public ByteBuffer read(final ByteBuffer old, final int length) throws IOException {
            if (old != null) {
                return super.read(old, length);
            }
            return this.bbi.readBuffer(length);
        }
    }
}
