// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

public interface SpdySynStreamFrame extends SpdyHeadersFrame
{
    int getAssociatedToStreamId();
    
    void setAssociatedToStreamId(final int p0);
    
    byte getPriority();
    
    void setPriority(final byte p0);
    
    boolean isUnidirectional();
    
    void setUnidirectional(final boolean p0);
}
