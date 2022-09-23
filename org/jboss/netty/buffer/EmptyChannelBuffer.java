// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.channels.ScatteringByteChannel;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.ByteBuffer;

public class EmptyChannelBuffer extends BigEndianHeapChannelBuffer
{
    private static final byte[] BUFFER;
    
    EmptyChannelBuffer() {
        super(EmptyChannelBuffer.BUFFER);
    }
    
    @Override
    public void clear() {
    }
    
    @Override
    public void readerIndex(final int readerIndex) {
        if (readerIndex != 0) {
            throw new IndexOutOfBoundsException("Invalid readerIndex: " + readerIndex + " - Maximum is 0");
        }
    }
    
    @Override
    public void writerIndex(final int writerIndex) {
        if (writerIndex != 0) {
            throw new IndexOutOfBoundsException("Invalid writerIndex: " + writerIndex + " - Maximum is 0");
        }
    }
    
    @Override
    public void setIndex(final int readerIndex, final int writerIndex) {
        if (writerIndex != 0 || readerIndex != 0) {
            throw new IndexOutOfBoundsException("Invalid writerIndex: " + writerIndex + " - Maximum is " + readerIndex + " or " + this.capacity());
        }
    }
    
    @Override
    public void markReaderIndex() {
    }
    
    @Override
    public void resetReaderIndex() {
    }
    
    @Override
    public void markWriterIndex() {
    }
    
    @Override
    public void resetWriterIndex() {
    }
    
    @Override
    public void discardReadBytes() {
    }
    
    @Override
    public ChannelBuffer readBytes(final int length) {
        this.checkReadableBytes(length);
        return this;
    }
    
    @Override
    public ChannelBuffer readSlice(final int length) {
        this.checkReadableBytes(length);
        return this;
    }
    
    @Override
    public void readBytes(final byte[] dst, final int dstIndex, final int length) {
        this.checkReadableBytes(length);
    }
    
    @Override
    public void readBytes(final byte[] dst) {
        this.checkReadableBytes(dst.length);
    }
    
    @Override
    public void readBytes(final ChannelBuffer dst) {
        this.checkReadableBytes(dst.writableBytes());
    }
    
    @Override
    public void readBytes(final ChannelBuffer dst, final int length) {
        this.checkReadableBytes(length);
    }
    
    @Override
    public void readBytes(final ChannelBuffer dst, final int dstIndex, final int length) {
        this.checkReadableBytes(length);
    }
    
    @Override
    public void readBytes(final ByteBuffer dst) {
        this.checkReadableBytes(dst.remaining());
    }
    
    @Override
    public int readBytes(final GatheringByteChannel out, final int length) throws IOException {
        this.checkReadableBytes(length);
        return 0;
    }
    
    @Override
    public void readBytes(final OutputStream out, final int length) throws IOException {
        this.checkReadableBytes(length);
    }
    
    @Override
    public void skipBytes(final int length) {
        this.checkReadableBytes(length);
    }
    
    @Override
    public void writeBytes(final byte[] src, final int srcIndex, final int length) {
        this.checkWritableBytes(length);
    }
    
    @Override
    public void writeBytes(final ChannelBuffer src, final int length) {
        this.checkWritableBytes(length);
    }
    
    @Override
    public void writeBytes(final ChannelBuffer src, final int srcIndex, final int length) {
        this.checkWritableBytes(length);
    }
    
    @Override
    public void writeBytes(final ByteBuffer src) {
        this.checkWritableBytes(src.remaining());
    }
    
    @Override
    public int writeBytes(final InputStream in, final int length) throws IOException {
        this.checkWritableBytes(length);
        return 0;
    }
    
    @Override
    public int writeBytes(final ScatteringByteChannel in, final int length) throws IOException {
        this.checkWritableBytes(length);
        return 0;
    }
    
    @Override
    public void writeZero(final int length) {
        this.checkWritableBytes(length);
    }
    
    private void checkWritableBytes(final int length) {
        if (length == 0) {
            return;
        }
        if (length > 0) {
            throw new IndexOutOfBoundsException("Writable bytes exceeded - Need " + length + ", maximum is " + 0);
        }
        throw new IndexOutOfBoundsException("length < 0");
    }
    
    @Override
    protected void checkReadableBytes(final int length) {
        if (length == 0) {
            return;
        }
        if (length > 0) {
            throw new IndexOutOfBoundsException("Not enough readable bytes - Need " + length + ", maximum is " + this.readableBytes());
        }
        throw new IndexOutOfBoundsException("length < 0");
    }
    
    static {
        BUFFER = new byte[0];
    }
}
