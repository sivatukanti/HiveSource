// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.io.OutputStream;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.ByteOrder;

public class ReadOnlyChannelBuffer extends AbstractChannelBuffer implements WrappedChannelBuffer
{
    private final ChannelBuffer buffer;
    
    public ReadOnlyChannelBuffer(final ChannelBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.buffer = buffer;
        this.setIndex(buffer.readerIndex(), buffer.writerIndex());
    }
    
    private ReadOnlyChannelBuffer(final ReadOnlyChannelBuffer buffer) {
        this.buffer = buffer.buffer;
        this.setIndex(buffer.readerIndex(), buffer.writerIndex());
    }
    
    public ChannelBuffer unwrap() {
        return this.buffer;
    }
    
    public ChannelBufferFactory factory() {
        return this.buffer.factory();
    }
    
    public ByteOrder order() {
        return this.buffer.order();
    }
    
    public boolean isDirect() {
        return this.buffer.isDirect();
    }
    
    public boolean hasArray() {
        return false;
    }
    
    public byte[] array() {
        throw new ReadOnlyBufferException();
    }
    
    public int arrayOffset() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public void discardReadBytes() {
        throw new ReadOnlyBufferException();
    }
    
    public void setByte(final int index, final int value) {
        throw new ReadOnlyBufferException();
    }
    
    public void setBytes(final int index, final ChannelBuffer src, final int srcIndex, final int length) {
        throw new ReadOnlyBufferException();
    }
    
    public void setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        throw new ReadOnlyBufferException();
    }
    
    public void setBytes(final int index, final ByteBuffer src) {
        throw new ReadOnlyBufferException();
    }
    
    public void setShort(final int index, final int value) {
        throw new ReadOnlyBufferException();
    }
    
    public void setMedium(final int index, final int value) {
        throw new ReadOnlyBufferException();
    }
    
    public void setInt(final int index, final int value) {
        throw new ReadOnlyBufferException();
    }
    
    public void setLong(final int index, final long value) {
        throw new ReadOnlyBufferException();
    }
    
    public int setBytes(final int index, final InputStream in, final int length) throws IOException {
        throw new ReadOnlyBufferException();
    }
    
    public int setBytes(final int index, final ScatteringByteChannel in, final int length) throws IOException {
        throw new ReadOnlyBufferException();
    }
    
    public int getBytes(final int index, final GatheringByteChannel out, final int length) throws IOException {
        return this.buffer.getBytes(index, out, length);
    }
    
    public void getBytes(final int index, final OutputStream out, final int length) throws IOException {
        this.buffer.getBytes(index, out, length);
    }
    
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        this.buffer.getBytes(index, dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final ChannelBuffer dst, final int dstIndex, final int length) {
        this.buffer.getBytes(index, dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final ByteBuffer dst) {
        this.buffer.getBytes(index, dst);
    }
    
    public ChannelBuffer duplicate() {
        return new ReadOnlyChannelBuffer(this);
    }
    
    public ChannelBuffer copy(final int index, final int length) {
        return this.buffer.copy(index, length);
    }
    
    public ChannelBuffer slice(final int index, final int length) {
        return new ReadOnlyChannelBuffer(this.buffer.slice(index, length));
    }
    
    public byte getByte(final int index) {
        return this.buffer.getByte(index);
    }
    
    public short getShort(final int index) {
        return this.buffer.getShort(index);
    }
    
    public int getUnsignedMedium(final int index) {
        return this.buffer.getUnsignedMedium(index);
    }
    
    public int getInt(final int index) {
        return this.buffer.getInt(index);
    }
    
    public long getLong(final int index) {
        return this.buffer.getLong(index);
    }
    
    public ByteBuffer toByteBuffer(final int index, final int length) {
        return this.buffer.toByteBuffer(index, length).asReadOnlyBuffer();
    }
    
    @Override
    public ByteBuffer[] toByteBuffers(final int index, final int length) {
        final ByteBuffer[] bufs = this.buffer.toByteBuffers(index, length);
        for (int i = 0; i < bufs.length; ++i) {
            bufs[i] = bufs[i].asReadOnlyBuffer();
        }
        return bufs;
    }
    
    public int capacity() {
        return this.buffer.capacity();
    }
}
