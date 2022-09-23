// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

public interface HttpRequest extends HttpMessage
{
    HttpMethod getMethod();
    
    void setMethod(final HttpMethod p0);
    
    String getUri();
    
    void setUri(final String p0);
}
