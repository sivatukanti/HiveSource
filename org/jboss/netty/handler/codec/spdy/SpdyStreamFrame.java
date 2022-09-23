// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

public interface SpdyStreamFrame extends SpdyFrame
{
    int getStreamId();
    
    void setStreamId(final int p0);
    
    boolean isLast();
    
    void setLast(final boolean p0);
}
