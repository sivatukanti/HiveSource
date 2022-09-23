// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.channels.ScatteringByteChannel;
import java.io.InputStream;
import java.nio.channels.GatheringByteChannel;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SlicedChannelBuffer extends AbstractChannelBuffer implements WrappedChannelBuffer
{
    private final ChannelBuffer buffer;
    private final int adjustment;
    private final int length;
    
    public SlicedChannelBuffer(final ChannelBuffer buffer, final int index, final int length) {
        if (index < 0 || index > buffer.capacity()) {
            throw new IndexOutOfBoundsException("Invalid index of " + index + ", maximum is " + buffer.capacity());
        }
        if (index + length > buffer.capacity()) {
            throw new IndexOutOfBoundsException("Invalid combined index of " + (index + length) + ", maximum is " + buffer.capacity());
        }
        this.buffer = buffer;
        this.adjustment = index;
        this.writerIndex(this.length = length);
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
    
    public int capacity() {
        return this.length;
    }
    
    public boolean hasArray() {
        return this.buffer.hasArray();
    }
    
    public byte[] array() {
        return this.buffer.array();
    }
    
    public int arrayOffset() {
        return this.buffer.arrayOffset() + this.adjustment;
    }
    
    public byte getByte(final int index) {
        this.checkIndex(index);
        return this.buffer.getByte(index + this.adjustment);
    }
    
    public short getShort(final int index) {
        this.checkIndex(index, 2);
        return this.buffer.getShort(index + this.adjustment);
    }
    
    public int getUnsignedMedium(final int index) {
        this.checkIndex(index, 3);
        return this.buffer.getUnsignedMedium(index + this.adjustment);
    }
    
    public int getInt(final int index) {
        this.checkIndex(index, 4);
        return this.buffer.getInt(index + this.adjustment);
    }
    
    public long getLong(final int index) {
        this.checkIndex(index, 8);
        return this.buffer.getLong(index + this.adjustment);
    }
    
    public ChannelBuffer duplicate() {
        final ChannelBuffer duplicate = new SlicedChannelBuffer(this.buffer, this.adjustment, this.length);
        duplicate.setIndex(this.readerIndex(), this.writerIndex());
        return duplicate;
    }
    
    public ChannelBuffer copy(final int index, final int length) {
        this.checkIndex(index, length);
        return this.buffer.copy(index + this.adjustment, length);
    }
    
    public ChannelBuffer slice(final int index, final int length) {
        this.checkIndex(index, length);
        if (length == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        return new SlicedChannelBuffer(this.buffer, index + this.adjustment, length);
    }
    
    public void getBytes(final int index, final ChannelBuffer dst, final int dstIndex, final int length) {
        this.checkIndex(index, length);
        this.buffer.getBytes(index + this.adjustment, dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        this.checkIndex(index, length);
        this.buffer.getBytes(index + this.adjustment, dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final ByteBuffer dst) {
        this.checkIndex(index, dst.remaining());
        this.buffer.getBytes(index + this.adjustment, dst);
    }
    
    public void setByte(final int index, final int value) {
        this.checkIndex(index);
        this.buffer.setByte(index + this.adjustment, value);
    }
    
    public void setShort(final int index, final int value) {
        this.checkIndex(index, 2);
        this.buffer.setShort(index + this.adjustment, value);
    }
    
    public void setMedium(final int index, final int value) {
        this.checkIndex(index, 3);
        this.buffer.setMedium(index + this.adjustment, value);
    }
    
    public void setInt(final int index, final int value) {
        this.checkIndex(index, 4);
        this.buffer.setInt(index + this.adjustment, value);
    }
    
    public void setLong(final int index, final long value) {
        this.checkIndex(index, 8);
        this.buffer.setLong(index + this.adjustment, value);
    }
    
    public void setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        this.checkIndex(index, length);
        this.buffer.setBytes(index + this.adjustment, src, srcIndex, length);
    }
    
    public void setBytes(final int index, final ChannelBuffer src, final int srcIndex, final int length) {
        this.checkIndex(index, length);
        this.buffer.setBytes(index + this.adjustment, src, srcIndex, length);
    }
    
    public void setBytes(final int index, final ByteBuffer src) {
        this.checkIndex(index, src.remaining());
        this.buffer.setBytes(index + this.adjustment, src);
    }
    
    public void getBytes(final int index, final OutputStream out, final int length) throws IOException {
        this.checkIndex(index, length);
        this.buffer.getBytes(index + this.adjustment, out, length);
    }
    
    public int getBytes(final int index, final GatheringByteChannel out, final int length) throws IOException {
        this.checkIndex(index, length);
        return this.buffer.getBytes(index + this.adjustment, out, length);
    }
    
    public int setBytes(final int index, final InputStream in, final int length) throws IOException {
        this.checkIndex(index, length);
        return this.buffer.setBytes(index + this.adjustment, in, length);
    }
    
    public int setBytes(final int index, final ScatteringByteChannel in, final int length) throws IOException {
        this.checkIndex(index, length);
        return this.buffer.setBytes(index + this.adjustment, in, length);
    }
    
    public ByteBuffer toByteBuffer(final int index, final int length) {
        this.checkIndex(index, length);
        return this.buffer.toByteBuffer(index + this.adjustment, length);
    }
    
    private void checkIndex(final int index) {
        if (index < 0 || index >= this.capacity()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index + ", maximum is " + this.capacity());
        }
    }
    
    private void checkIndex(final int startIndex, final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length is negative: " + length);
        }
        if (startIndex < 0) {
            throw new IndexOutOfBoundsException("startIndex cannot be negative");
        }
        if (startIndex + length > this.capacity()) {
            throw new IndexOutOfBoundsException("Index too big - Bytes needed: " + (startIndex + length) + ", maximum is " + this.capacity());
        }
    }
}
