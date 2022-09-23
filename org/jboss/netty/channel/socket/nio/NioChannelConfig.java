// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.ChannelConfig;

public interface NioChannelConfig extends ChannelConfig
{
    int getWriteBufferHighWaterMark();
    
    void setWriteBufferHighWaterMark(final int p0);
    
    int getWriteBufferLowWaterMark();
    
    void setWriteBufferLowWaterMark(final int p0);
    
    int getWriteSpinCount();
    
    void setWriteSpinCount(final int p0);
}
