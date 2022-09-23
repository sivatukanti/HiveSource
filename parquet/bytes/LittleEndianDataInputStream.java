// 
// Decompiled by Procyon v0.5.36
// 

package parquet.bytes;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class LittleEndianDataInputStream extends InputStream
{
    private final InputStream in;
    private byte[] readBuffer;
    
    public LittleEndianDataInputStream(final InputStream in) {
        this.readBuffer = new byte[8];
        this.in = in;
    }
    
    public final void readFully(final byte[] b) throws IOException {
        this.readFully(b, 0, b.length);
    }
    
    public final void readFully(final byte[] b, final int off, final int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int count;
        for (int n = 0; n < len; n += count) {
            count = this.in.read(b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
        }
    }
    
    public final int skipBytes(final int n) throws IOException {
        int total = 0;
        for (int cur = 0; total < n && (cur = (int)this.in.skip(n - total)) > 0; total += cur) {}
        return total;
    }
    
    @Override
    public int read() throws IOException {
        return this.in.read();
    }
    
    @Override
    public int hashCode() {
        return this.in.hashCode();
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.in.read(b);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.in.equals(obj);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.in.read(b, off, len);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.in.skip(n);
    }
    
    @Override
    public int available() throws IOException {
        return this.in.available();
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    @Override
    public void mark(final int readlimit) {
        this.in.mark(readlimit);
    }
    
    @Override
    public void reset() throws IOException {
        this.in.reset();
    }
    
    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }
    
    public final boolean readBoolean() throws IOException {
        final int ch = this.in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch != 0;
    }
    
    public final byte readByte() throws IOException {
        final int ch = this.in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte)ch;
    }
    
    public final int readUnsignedByte() throws IOException {
        final int ch = this.in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }
    
    public final short readShort() throws IOException {
        final int ch2 = this.in.read();
        final int ch3 = this.in.read();
        if ((ch3 | ch2) < 0) {
            throw new EOFException();
        }
        return (short)((ch3 << 8) + (ch2 << 0));
    }
    
    public final int readUnsignedShort() throws IOException {
        final int ch2 = this.in.read();
        final int ch3 = this.in.read();
        if ((ch3 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch3 << 8) + (ch2 << 0);
    }
    
    public final int readInt() throws IOException {
        final int ch4 = this.in.read();
        final int ch5 = this.in.read();
        final int ch6 = this.in.read();
        final int ch7 = this.in.read();
        if ((ch7 | ch6 | ch5 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch7 << 24) + (ch6 << 16) + (ch5 << 8) + (ch4 << 0);
    }
    
    public final long readLong() throws IOException {
        this.readFully(this.readBuffer, 0, 8);
        return ((long)this.readBuffer[7] << 56) + ((long)(this.readBuffer[6] & 0xFF) << 48) + ((long)(this.readBuffer[5] & 0xFF) << 40) + ((long)(this.readBuffer[4] & 0xFF) << 32) + ((long)(this.readBuffer[3] & 0xFF) << 24) + ((this.readBuffer[2] & 0xFF) << 16) + ((this.readBuffer[1] & 0xFF) << 8) + ((this.readBuffer[0] & 0xFF) << 0);
    }
    
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }
    
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }
}
