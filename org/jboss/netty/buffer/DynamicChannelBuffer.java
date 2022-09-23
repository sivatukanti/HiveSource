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
import java.nio.ByteOrder;

public class DynamicChannelBuffer extends AbstractChannelBuffer
{
    private final ChannelBufferFactory factory;
    private final ByteOrder endianness;
    private ChannelBuffer buffer;
    
    public DynamicChannelBuffer(final int estimatedLength) {
        this(ByteOrder.BIG_ENDIAN, estimatedLength);
    }
    
    public DynamicChannelBuffer(final ByteOrder endianness, final int estimatedLength) {
        this(endianness, estimatedLength, HeapChannelBufferFactory.getInstance(endianness));
    }
    
    public DynamicChannelBuffer(final ByteOrder endianness, final int estimatedLength, final ChannelBufferFactory factory) {
        if (estimatedLength < 0) {
            throw new IllegalArgumentException("estimatedLength: " + estimatedLength);
        }
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        this.factory = factory;
        this.endianness = endianness;
        this.buffer = factory.getBuffer(this.order(), estimatedLength);
    }
    
    @Override
    public void ensureWritableBytes(final int minWritableBytes) {
        if (minWritableBytes <= this.writableBytes()) {
            return;
        }
        int newCapacity;
        if (this.capacity() == 0) {
            newCapacity = 1;
        }
        else {
            newCapacity = this.capacity();
        }
        final int minNewCapacity = this.writerIndex() + minWritableBytes;
        while (newCapacity < minNewCapacity) {
            newCapacity <<= 1;
            if (newCapacity == 0) {
                throw new IllegalStateException("Maximum size of 2gb exceeded");
            }
        }
        final ChannelBuffer newBuffer = this.factory().getBuffer(this.order(), newCapacity);
        newBuffer.writeBytes(this.buffer, 0, this.writerIndex());
        this.buffer = newBuffer;
    }
    
    public ChannelBufferFactory factory() {
        return this.factory;
    }
    
    public ByteOrder order() {
        return this.endianness;
    }
    
    public boolean isDirect() {
        return this.buffer.isDirect();
    }
    
    public int capacity() {
        return this.buffer.capacity();
    }
    
    public boolean hasArray() {
        return this.buffer.hasArray();
    }
    
    public byte[] array() {
        return this.buffer.array();
    }
    
    public int arrayOffset() {
        return this.buffer.arrayOffset();
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
    
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        this.buffer.getBytes(index, dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final ChannelBuffer dst, final int dstIndex, final int length) {
        this.buffer.getBytes(index, dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final ByteBuffer dst) {
        this.buffer.getBytes(index, dst);
    }
    
    public int getBytes(final int index, final GatheringByteChannel out, final int length) throws IOException {
        return this.buffer.getBytes(index, out, length);
    }
    
    public void getBytes(final int index, final OutputStream out, final int length) throws IOException {
        this.buffer.getBytes(index, out, length);
    }
    
    public void setByte(final int index, final int value) {
        this.buffer.setByte(index, value);
    }
    
    public void setShort(final int index, final int value) {
        this.buffer.setShort(index, value);
    }
    
    public void setMedium(final int index, final int value) {
        this.buffer.setMedium(index, value);
    }
    
    public void setInt(final int index, final int value) {
        this.buffer.setInt(index, value);
    }
    
    public void setLong(final int index, final long value) {
        this.buffer.setLong(index, value);
    }
    
    public void setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        this.buffer.setBytes(index, src, srcIndex, length);
    }
    
    public void setBytes(final int index, final ChannelBuffer src, final int srcIndex, final int length) {
        this.buffer.setBytes(index, src, srcIndex, length);
    }
    
    public void setBytes(final int index, final ByteBuffer src) {
        this.buffer.setBytes(index, src);
    }
    
    public int setBytes(final int index, final InputStream in, final int length) throws IOException {
        return this.buffer.setBytes(index, in, length);
    }
    
    public int setBytes(final int index, final ScatteringByteChannel in, final int length) throws IOException {
        return this.buffer.setBytes(index, in, length);
    }
    
    @Override
    public void writeByte(final int value) {
        this.ensureWritableBytes(1);
        super.writeByte(value);
    }
    
    @Override
    public void writeShort(final int value) {
        this.ensureWritableBytes(2);
        super.writeShort(value);
    }
    
    @Override
    public void writeMedium(final int value) {
        this.ensureWritableBytes(3);
        super.writeMedium(value);
    }
    
    @Override
    public void writeInt(final int value) {
        this.ensureWritableBytes(4);
        super.writeInt(value);
    }
    
    @Override
    public void writeLong(final long value) {
        this.ensureWritableBytes(8);
        super.writeLong(value);
    }
    
    @Override
    public void writeBytes(final byte[] src, final int srcIndex, final int length) {
        this.ensureWritableBytes(length);
        super.writeBytes(src, srcIndex, length);
    }
    
    @Override
    public void writeBytes(final ChannelBuffer src, final int srcIndex, final int length) {
        this.ensureWritableBytes(length);
        super.writeBytes(src, srcIndex, length);
    }
    
    @Override
    public void writeBytes(final ByteBuffer src) {
        this.ensureWritableBytes(src.remaining());
        super.writeBytes(src);
    }
    
    @Override
    public int writeBytes(final InputStream in, final int length) throws IOException {
        this.ensureWritableBytes(length);
        return super.writeBytes(in, length);
    }
    
    @Override
    public int writeBytes(final ScatteringByteChannel in, final int length) throws IOException {
        this.ensureWritableBytes(length);
        return super.writeBytes(in, length);
    }
    
    @Override
    public void writeZero(final int length) {
        this.ensureWritableBytes(length);
        super.writeZero(length);
    }
    
    public ChannelBuffer duplicate() {
        return new DuplicatedChannelBuffer(this);
    }
    
    public ChannelBuffer copy(final int index, final int length) {
        final DynamicChannelBuffer copiedBuffer = new DynamicChannelBuffer(this.order(), Math.max(length, 64), this.factory());
        copiedBuffer.buffer = this.buffer.copy(index, length);
        copiedBuffer.setIndex(0, length);
        return copiedBuffer;
    }
    
    public ChannelBuffer slice(final int index, final int length) {
        if (index == 0) {
            if (length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new TruncatedChannelBuffer(this, length);
        }
        else {
            if (length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new SlicedChannelBuffer(this, index, length);
        }
    }
    
    public ByteBuffer toByteBuffer(final int index, final int length) {
        return this.buffer.toByteBuffer(index, length);
    }
}
