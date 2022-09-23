// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.Set;

public interface PushBuilder
{
    PushBuilder method(final String p0);
    
    PushBuilder queryString(final String p0);
    
    PushBuilder sessionId(final String p0);
    
    PushBuilder conditional(final boolean p0);
    
    PushBuilder setHeader(final String p0, final String p1);
    
    PushBuilder addHeader(final String p0, final String p1);
    
    PushBuilder path(final String p0);
    
    PushBuilder etag(final String p0);
    
    PushBuilder lastModified(final String p0);
    
    void push();
    
    String getMethod();
    
    String getQueryString();
    
    String getSessionId();
    
    boolean isConditional();
    
    Set<String> getHeaderNames();
    
    String getHeader(final String p0);
    
    String getPath();
    
    String getEtag();
    
    String getLastModified();
}
