// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.stream;

public interface ChunkedInput
{
    boolean hasNextChunk() throws Exception;
    
    Object nextChunk() throws Exception;
    
    boolean isEndOfInput() throws Exception;
    
    void close() throws Exception;
}
