// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

public interface HttpResponse extends HttpMessage
{
    HttpResponseStatus getStatus();
    
    void setStatus(final HttpResponseStatus p0);
}
