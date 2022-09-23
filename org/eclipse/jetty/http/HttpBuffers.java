// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import org.eclipse.jetty.io.Buffers;

public interface HttpBuffers
{
    int getRequestBufferSize();
    
    void setRequestBufferSize(final int p0);
    
    int getRequestHeaderSize();
    
    void setRequestHeaderSize(final int p0);
    
    int getResponseBufferSize();
    
    void setResponseBufferSize(final int p0);
    
    int getResponseHeaderSize();
    
    void setResponseHeaderSize(final int p0);
    
    Buffers.Type getRequestBufferType();
    
    Buffers.Type getRequestHeaderType();
    
    Buffers.Type getResponseBufferType();
    
    Buffers.Type getResponseHeaderType();
    
    void setRequestBuffers(final Buffers p0);
    
    void setResponseBuffers(final Buffers p0);
    
    Buffers getRequestBuffers();
    
    Buffers getResponseBuffers();
    
    void setMaxBuffers(final int p0);
    
    int getMaxBuffers();
}
