// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.continuation;

import javax.servlet.ServletResponse;

public interface Continuation
{
    public static final String ATTRIBUTE = "org.eclipse.jetty.continuation";
    
    void setTimeout(final long p0);
    
    void suspend();
    
    void suspend(final ServletResponse p0);
    
    void resume();
    
    void complete();
    
    boolean isSuspended();
    
    boolean isResumed();
    
    boolean isExpired();
    
    boolean isInitial();
    
    boolean isResponseWrapped();
    
    ServletResponse getServletResponse();
    
    void addContinuationListener(final ContinuationListener p0);
    
    void setAttribute(final String p0, final Object p1);
    
    Object getAttribute(final String p0);
    
    void removeAttribute(final String p0);
    
    void undispatch() throws ContinuationThrowable;
}
