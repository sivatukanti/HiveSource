// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

public interface SpdyHeadersFrame extends SpdyStreamFrame
{
    boolean isInvalid();
    
    void setInvalid();
    
    boolean isTruncated();
    
    void setTruncated();
    
    SpdyHeaders headers();
}
