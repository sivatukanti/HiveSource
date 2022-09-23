// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.ByteOrder;

public abstract class AbstractChannelBufferFactory implements ChannelBufferFactory
{
    private final ByteOrder defaultOrder;
    
    protected AbstractChannelBufferFactory() {
        this(ByteOrder.BIG_ENDIAN);
    }
    
    protected AbstractChannelBufferFactory(final ByteOrder defaultOrder) {
        if (defaultOrder == null) {
            throw new NullPointerException("defaultOrder");
        }
        this.defaultOrder = defaultOrder;
    }
    
    public ChannelBuffer getBuffer(final int capacity) {
        return this.getBuffer(this.getDefaultOrder(), capacity);
    }
    
    public ChannelBuffer getBuffer(final byte[] array, final int offset, final int length) {
        return this.getBuffer(this.getDefaultOrder(), array, offset, length);
    }
    
    public ByteOrder getDefaultOrder() {
        return this.defaultOrder;
    }
}
