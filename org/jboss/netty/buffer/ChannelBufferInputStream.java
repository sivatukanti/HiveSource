// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.DataInput;
import java.io.InputStream;

public class ChannelBufferInputStream extends InputStream implements DataInput
{
    private final ChannelBuffer buffer;
    private final int startIndex;
    private final int endIndex;
    private final StringBuilder lineBuf;
    
    public ChannelBufferInputStream(final ChannelBuffer buffer) {
        this(buffer, buffer.readableBytes());
    }
    
    public ChannelBufferInputStream(final ChannelBuffer buffer, final int length) {
        this.lineBuf = new StringBuilder();
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (length > buffer.readableBytes()) {
            throw new IndexOutOfBoundsException("Too many bytes to be read - Needs " + length + ", maximum is " + buffer.readableBytes());
        }
        this.buffer = buffer;
        this.startIndex = buffer.readerIndex();
        this.endIndex = this.startIndex + length;
        buffer.markReaderIndex();
    }
    
    public int readBytes() {
        return this.buffer.readerIndex() - this.startIndex;
    }
    
    @Override
    public int available() throws IOException {
        return this.endIndex - this.buffer.readerIndex();
    }
    
    @Override
    public void mark(final int readlimit) {
        this.buffer.markReaderIndex();
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public int read() throws IOException {
        if (!this.buffer.readable()) {
            return -1;
        }
        return this.buffer.readByte() & 0xFF;
    }
    
    @Override
    public int read(final byte[] b, final int off, int len) throws IOException {
        final int available = this.available();
        if (available == 0) {
            return -1;
        }
        len = Math.min(available, len);
        this.buffer.readBytes(b, off, len);
        return len;
    }
    
    @Override
    public void reset() throws IOException {
        this.buffer.resetReaderIndex();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n > 2147483647L) {
            return this.skipBytes(Integer.MAX_VALUE);
        }
        return this.skipBytes((int)n);
    }
    
    public boolean readBoolean() throws IOException {
        this.checkAvailable(1);
        return this.read() != 0;
    }
    
    public byte readByte() throws IOException {
        if (!this.buffer.readable()) {
            throw new EOFException();
        }
        return this.buffer.readByte();
    }
    
    public char readChar() throws IOException {
        return (char)this.readShort();
    }
    
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }
    
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }
    
    public void readFully(final byte[] b) throws IOException {
        this.readFully(b, 0, b.length);
    }
    
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        this.checkAvailable(len);
        this.buffer.readBytes(b, off, len);
    }
    
    public int readInt() throws IOException {
        this.checkAvailable(4);
        return this.buffer.readInt();
    }
    
    public String readLine() throws IOException {
        this.lineBuf.setLength(0);
        while (true) {
            final int b = this.read();
            if (b < 0 || b == 10) {
                break;
            }
            this.lineBuf.append((char)b);
        }
        if (this.lineBuf.length() > 0) {
            while (this.lineBuf.charAt(this.lineBuf.length() - 1) == '\r') {
                this.lineBuf.setLength(this.lineBuf.length() - 1);
            }
        }
        return this.lineBuf.toString();
    }
    
    public long readLong() throws IOException {
        this.checkAvailable(8);
        return this.buffer.readLong();
    }
    
    public short readShort() throws IOException {
        this.checkAvailable(2);
        return this.buffer.readShort();
    }
    
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }
    
    public int readUnsignedByte() throws IOException {
        return this.readByte() & 0xFF;
    }
    
    public int readUnsignedShort() throws IOException {
        return this.readShort() & 0xFFFF;
    }
    
    public int skipBytes(final int n) throws IOException {
        final int nBytes = Math.min(this.available(), n);
        this.buffer.skipBytes(nBytes);
        return nBytes;
    }
    
    private void checkAvailable(final int fieldSize) throws IOException {
        if (fieldSize < 0) {
            throw new IndexOutOfBoundsException("fieldSize cannot be a negative number");
        }
        if (fieldSize > this.available()) {
            throw new EOFException("fieldSize is too long! Length is " + fieldSize + ", but maximum is " + this.available());
        }
    }
}
