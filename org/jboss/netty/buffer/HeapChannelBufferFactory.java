// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HeapChannelBufferFactory extends AbstractChannelBufferFactory
{
    private static final HeapChannelBufferFactory INSTANCE_BE;
    private static final HeapChannelBufferFactory INSTANCE_LE;
    
    public static ChannelBufferFactory getInstance() {
        return HeapChannelBufferFactory.INSTANCE_BE;
    }
    
    public static ChannelBufferFactory getInstance(final ByteOrder endianness) {
        if (endianness == ByteOrder.BIG_ENDIAN) {
            return HeapChannelBufferFactory.INSTANCE_BE;
        }
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            return HeapChannelBufferFactory.INSTANCE_LE;
        }
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        throw new IllegalStateException("Should not reach here");
    }
    
    public HeapChannelBufferFactory() {
    }
    
    public HeapChannelBufferFactory(final ByteOrder defaultOrder) {
        super(defaultOrder);
    }
    
    public ChannelBuffer getBuffer(final ByteOrder order, final int capacity) {
        return ChannelBuffers.buffer(order, capacity);
    }
    
    public ChannelBuffer getBuffer(final ByteOrder order, final byte[] array, final int offset, final int length) {
        return ChannelBuffers.wrappedBuffer(order, array, offset, length);
    }
    
    public ChannelBuffer getBuffer(final ByteBuffer nioBuffer) {
        if (nioBuffer.hasArray()) {
            return ChannelBuffers.wrappedBuffer(nioBuffer);
        }
        final ChannelBuffer buf = this.getBuffer(nioBuffer.order(), nioBuffer.remaining());
        final int pos = nioBuffer.position();
        buf.writeBytes(nioBuffer);
        nioBuffer.position(pos);
        return buf;
    }
    
    static {
        INSTANCE_BE = new HeapChannelBufferFactory(ByteOrder.BIG_ENDIAN);
        INSTANCE_LE = new HeapChannelBufferFactory(ByteOrder.LITTLE_ENDIAN);
    }
}
