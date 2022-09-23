// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

public interface SpdyRstStreamFrame extends SpdyStreamFrame
{
    SpdyStreamStatus getStatus();
    
    void setStatus(final SpdyStreamStatus p0);
}
