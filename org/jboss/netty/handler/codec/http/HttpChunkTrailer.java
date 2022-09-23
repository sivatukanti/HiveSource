// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

public interface HttpChunkTrailer extends HttpChunk
{
    boolean isLast();
    
    HttpHeaders trailingHeaders();
}
