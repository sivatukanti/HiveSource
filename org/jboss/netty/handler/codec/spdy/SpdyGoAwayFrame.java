// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

public interface SpdyGoAwayFrame extends SpdyFrame
{
    int getLastGoodStreamId();
    
    void setLastGoodStreamId(final int p0);
    
    SpdySessionStatus getStatus();
    
    void setStatus(final SpdySessionStatus p0);
}
