// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.ByteOrder;

public class BigEndianHeapChannelBuffer extends HeapChannelBuffer
{
    public BigEndianHeapChannelBuffer(final int length) {
        super(length);
    }
    
    public BigEndianHeapChannelBuffer(final byte[] array) {
        super(array);
    }
    
    private BigEndianHeapChannelBuffer(final byte[] array, final int readerIndex, final int writerIndex) {
        super(array, readerIndex, writerIndex);
    }
    
    public ChannelBufferFactory factory() {
        return HeapChannelBufferFactory.getInstance(ByteOrder.BIG_ENDIAN);
    }
    
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }
    
    public short getShort(final int index) {
        return (short)(this.array[index] << 8 | (this.array[index + 1] & 0xFF));
    }
    
    public int getUnsignedMedium(final int index) {
        return (this.array[index] & 0xFF) << 16 | (this.array[index + 1] & 0xFF) << 8 | (this.array[index + 2] & 0xFF);
    }
    
    public int getInt(final int index) {
        return (this.array[index] & 0xFF) << 24 | (this.array[index + 1] & 0xFF) << 16 | (this.array[index + 2] & 0xFF) << 8 | (this.array[index + 3] & 0xFF);
    }
    
    public long getLong(final int index) {
        return ((long)this.array[index] & 0xFFL) << 56 | ((long)this.array[index + 1] & 0xFFL) << 48 | ((long)this.array[index + 2] & 0xFFL) << 40 | ((long)this.array[index + 3] & 0xFFL) << 32 | ((long)this.array[index + 4] & 0xFFL) << 24 | ((long)this.array[index + 5] & 0xFFL) << 16 | ((long)this.array[index + 6] & 0xFFL) << 8 | ((long)this.array[index + 7] & 0xFFL);
    }
    
    public void setShort(final int index, final int value) {
        this.array[index] = (byte)(value >>> 8);
        this.array[index + 1] = (byte)value;
    }
    
    public void setMedium(final int index, final int value) {
        this.array[index] = (byte)(value >>> 16);
        this.array[index + 1] = (byte)(value >>> 8);
        this.array[index + 2] = (byte)value;
    }
    
    public void setInt(final int index, final int value) {
        this.array[index] = (byte)(value >>> 24);
        this.array[index + 1] = (byte)(value >>> 16);
        this.array[index + 2] = (byte)(value >>> 8);
        this.array[index + 3] = (byte)value;
    }
    
    public void setLong(final int index, final long value) {
        this.array[index] = (byte)(value >>> 56);
        this.array[index + 1] = (byte)(value >>> 48);
        this.array[index + 2] = (byte)(value >>> 40);
        this.array[index + 3] = (byte)(value >>> 32);
        this.array[index + 4] = (byte)(value >>> 24);
        this.array[index + 5] = (byte)(value >>> 16);
        this.array[index + 6] = (byte)(value >>> 8);
        this.array[index + 7] = (byte)value;
    }
    
    public ChannelBuffer duplicate() {
        return new BigEndianHeapChannelBuffer(this.array, this.readerIndex(), this.writerIndex());
    }
    
    public ChannelBuffer copy(final int index, final int length) {
        if (index < 0 || length < 0 || index + length > this.array.length) {
            throw new IndexOutOfBoundsException("Too many bytes to copy - Need " + (index + length) + ", maximum is " + this.array.length);
        }
        final byte[] copiedArray = new byte[length];
        System.arraycopy(this.array, index, copiedArray, 0, length);
        return new BigEndianHeapChannelBuffer(copiedArray);
    }
}
