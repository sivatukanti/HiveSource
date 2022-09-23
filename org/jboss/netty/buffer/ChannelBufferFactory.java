// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface ChannelBufferFactory
{
    ChannelBuffer getBuffer(final int p0);
    
    ChannelBuffer getBuffer(final ByteOrder p0, final int p1);
    
    ChannelBuffer getBuffer(final byte[] p0, final int p1, final int p2);
    
    ChannelBuffer getBuffer(final ByteOrder p0, final byte[] p1, final int p2, final int p3);
    
    ChannelBuffer getBuffer(final ByteBuffer p0);
    
    ByteOrder getDefaultOrder();
}
