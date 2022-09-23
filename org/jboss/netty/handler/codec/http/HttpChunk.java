// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.ChannelBuffer;

public interface HttpChunk
{
    public static final HttpChunkTrailer LAST_CHUNK = new HttpChunkTrailer() {
        public ChannelBuffer getContent() {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        
        public void setContent(final ChannelBuffer content) {
            throw new IllegalStateException("read-only");
        }
        
        public boolean isLast() {
            return true;
        }
        
        public HttpHeaders trailingHeaders() {
            return HttpHeaders.EMPTY_HEADERS;
        }
    };
    
    boolean isLast();
    
    ChannelBuffer getContent();
    
    void setContent(final ChannelBuffer p0);
}
