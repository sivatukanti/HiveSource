// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffer;

public interface SpdyDataFrame extends SpdyStreamFrame
{
    ChannelBuffer getData();
    
    void setData(final ChannelBuffer p0);
}
