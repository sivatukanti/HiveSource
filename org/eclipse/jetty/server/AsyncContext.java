// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.continuation.ContinuationListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;

public interface AsyncContext
{
    public static final String ASYNC_REQUEST_URI = "javax.servlet.async.request_uri";
    public static final String ASYNC_CONTEXT_PATH = "javax.servlet.async.context_path";
    public static final String ASYNC_PATH_INFO = "javax.servlet.async.path_info";
    public static final String ASYNC_SERVLET_PATH = "javax.servlet.async.servlet_path";
    public static final String ASYNC_QUERY_STRING = "javax.servlet.async.query_string";
    
    ServletRequest getRequest();
    
    ServletResponse getResponse();
    
    boolean hasOriginalRequestAndResponse();
    
    void dispatch();
    
    void dispatch(final String p0);
    
    void dispatch(final ServletContext p0, final String p1);
    
    void complete();
    
    void start(final Runnable p0);
    
    void setTimeout(final long p0);
    
    void addContinuationListener(final ContinuationListener p0);
}
