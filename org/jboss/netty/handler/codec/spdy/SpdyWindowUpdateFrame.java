// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

public interface SpdyWindowUpdateFrame extends SpdyFrame
{
    int getStreamId();
    
    void setStreamId(final int p0);
    
    int getDeltaWindowSize();
    
    void setDeltaWindowSize(final int p0);
}
