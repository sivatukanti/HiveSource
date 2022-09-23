// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.buffer.ChannelBuffer;

public interface HttpMessage
{
    HttpVersion getProtocolVersion();
    
    void setProtocolVersion(final HttpVersion p0);
    
    HttpHeaders headers();
    
    ChannelBuffer getContent();
    
    void setContent(final ChannelBuffer p0);
    
    boolean isChunked();
    
    void setChunked(final boolean p0);
}
