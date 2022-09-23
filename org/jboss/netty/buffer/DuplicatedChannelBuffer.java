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

public class DuplicatedChannelBuffer extends AbstractChannelBuffer implements WrappedChannelBuffer
{
    private final ChannelBuffer buffer;
    
    public DuplicatedChannelBuffer(final ChannelBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.buffer = buffer;
        this.setIndex(buffer.readerIndex(), buffer.writerIndex());
    }
    
    private DuplicatedChannelBuffer(final DuplicatedChannelBuffer buffer) {
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
    
    public ChannelBuffer duplicate() {
        return new DuplicatedChannelBuffer(this);
    }
    
    public ChannelBuffer copy(final int index, final int length) {
        return this.buffer.copy(index, length);
    }
    
    public ChannelBuffer slice(final int index, final int length) {
        return this.buffer.slice(index, length);
    }
    
    public void getBytes(final int index, final ChannelBuffer dst, final int dstIndex, final int length) {
        this.buffer.getBytes(index, dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        this.buffer.getBytes(index, dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final ByteBuffer dst) {
        this.buffer.getBytes(index, dst);
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
    
    public void getBytes(final int index, final OutputStream out, final int length) throws IOException {
        this.buffer.getBytes(index, out, length);
    }
    
    public int getBytes(final int index, final GatheringByteChannel out, final int length) throws IOException {
        return this.buffer.getBytes(index, out, length);
    }
    
    public int setBytes(final int index, final InputStream in, final int length) throws IOException {
        return this.buffer.setBytes(index, in, length);
    }
    
    public int setBytes(final int index, final ScatteringByteChannel in, final int length) throws IOException {
        return this.buffer.setBytes(index, in, length);
    }
    
    public ByteBuffer toByteBuffer(final int index, final int length) {
        return this.buffer.toByteBuffer(index, length);
    }
}
