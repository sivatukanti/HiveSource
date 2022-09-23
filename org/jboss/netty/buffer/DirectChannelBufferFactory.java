// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DirectChannelBufferFactory extends AbstractChannelBufferFactory
{
    private static final DirectChannelBufferFactory INSTANCE_BE;
    private static final DirectChannelBufferFactory INSTANCE_LE;
    private final Object bigEndianLock;
    private final Object littleEndianLock;
    private final int preallocatedBufCapacity;
    private ChannelBuffer preallocatedBEBuf;
    private int preallocatedBEBufPos;
    private ChannelBuffer preallocatedLEBuf;
    private int preallocatedLEBufPos;
    
    public static ChannelBufferFactory getInstance() {
        return DirectChannelBufferFactory.INSTANCE_BE;
    }
    
    public static ChannelBufferFactory getInstance(final ByteOrder defaultEndianness) {
        if (defaultEndianness == ByteOrder.BIG_ENDIAN) {
            return DirectChannelBufferFactory.INSTANCE_BE;
        }
        if (defaultEndianness == ByteOrder.LITTLE_ENDIAN) {
            return DirectChannelBufferFactory.INSTANCE_LE;
        }
        if (defaultEndianness == null) {
            throw new NullPointerException("defaultEndianness");
        }
        throw new IllegalStateException("Should not reach here");
    }
    
    public DirectChannelBufferFactory() {
        this(ByteOrder.BIG_ENDIAN);
    }
    
    public DirectChannelBufferFactory(final int preallocatedBufferCapacity) {
        this(ByteOrder.BIG_ENDIAN, preallocatedBufferCapacity);
    }
    
    public DirectChannelBufferFactory(final ByteOrder defaultOrder) {
        this(defaultOrder, 1048576);
    }
    
    public DirectChannelBufferFactory(final ByteOrder defaultOrder, final int preallocatedBufferCapacity) {
        super(defaultOrder);
        this.bigEndianLock = new Object();
        this.littleEndianLock = new Object();
        if (preallocatedBufferCapacity <= 0) {
            throw new IllegalArgumentException("preallocatedBufCapacity must be greater than 0: " + preallocatedBufferCapacity);
        }
        this.preallocatedBufCapacity = preallocatedBufferCapacity;
    }
    
    public ChannelBuffer getBuffer(final ByteOrder order, final int capacity) {
        if (order == null) {
            throw new NullPointerException("order");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity: " + capacity);
        }
        if (capacity == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        if (capacity >= this.preallocatedBufCapacity) {
            return ChannelBuffers.directBuffer(order, capacity);
        }
        ChannelBuffer slice;
        if (order == ByteOrder.BIG_ENDIAN) {
            slice = this.allocateBigEndianBuffer(capacity);
        }
        else {
            slice = this.allocateLittleEndianBuffer(capacity);
        }
        slice.clear();
        return slice;
    }
    
    public ChannelBuffer getBuffer(final ByteOrder order, final byte[] array, final int offset, final int length) {
        if (array == null) {
            throw new NullPointerException("array");
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException("offset: " + offset);
        }
        if (length == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        if (offset + length > array.length) {
            throw new IndexOutOfBoundsException("length: " + length);
        }
        final ChannelBuffer buf = this.getBuffer(order, length);
        buf.writeBytes(array, offset, length);
        return buf;
    }
    
    public ChannelBuffer getBuffer(final ByteBuffer nioBuffer) {
        if (!nioBuffer.isReadOnly() && nioBuffer.isDirect()) {
            return ChannelBuffers.wrappedBuffer(nioBuffer);
        }
        final ChannelBuffer buf = this.getBuffer(nioBuffer.order(), nioBuffer.remaining());
        final int pos = nioBuffer.position();
        buf.writeBytes(nioBuffer);
        nioBuffer.position(pos);
        return buf;
    }
    
    private ChannelBuffer allocateBigEndianBuffer(final int capacity) {
        ChannelBuffer slice;
        synchronized (this.bigEndianLock) {
            if (this.preallocatedBEBuf == null) {
                this.preallocatedBEBuf = ChannelBuffers.directBuffer(ByteOrder.BIG_ENDIAN, this.preallocatedBufCapacity);
                slice = this.preallocatedBEBuf.slice(0, capacity);
                this.preallocatedBEBufPos = capacity;
            }
            else if (this.preallocatedBEBuf.capacity() - this.preallocatedBEBufPos >= capacity) {
                slice = this.preallocatedBEBuf.slice(this.preallocatedBEBufPos, capacity);
                this.preallocatedBEBufPos += capacity;
            }
            else {
                this.preallocatedBEBuf = ChannelBuffers.directBuffer(ByteOrder.BIG_ENDIAN, this.preallocatedBufCapacity);
                slice = this.preallocatedBEBuf.slice(0, capacity);
                this.preallocatedBEBufPos = capacity;
            }
        }
        return slice;
    }
    
    private ChannelBuffer allocateLittleEndianBuffer(final int capacity) {
        ChannelBuffer slice;
        synchronized (this.littleEndianLock) {
            if (this.preallocatedLEBuf == null) {
                this.preallocatedLEBuf = ChannelBuffers.directBuffer(ByteOrder.LITTLE_ENDIAN, this.preallocatedBufCapacity);
                slice = this.preallocatedLEBuf.slice(0, capacity);
                this.preallocatedLEBufPos = capacity;
            }
            else if (this.preallocatedLEBuf.capacity() - this.preallocatedLEBufPos >= capacity) {
                slice = this.preallocatedLEBuf.slice(this.preallocatedLEBufPos, capacity);
                this.preallocatedLEBufPos += capacity;
            }
            else {
                this.preallocatedLEBuf = ChannelBuffers.directBuffer(ByteOrder.LITTLE_ENDIAN, this.preallocatedBufCapacity);
                slice = this.preallocatedLEBuf.slice(0, capacity);
                this.preallocatedLEBufPos = capacity;
            }
        }
        return slice;
    }
    
    static {
        INSTANCE_BE = new DirectChannelBufferFactory(ByteOrder.BIG_ENDIAN);
        INSTANCE_LE = new DirectChannelBufferFactory(ByteOrder.LITTLE_ENDIAN);
    }
}
