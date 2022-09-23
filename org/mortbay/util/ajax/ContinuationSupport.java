// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util.ajax;

import javax.servlet.http.HttpServletRequest;

public class ContinuationSupport
{
    public static Continuation getContinuation(final HttpServletRequest request, final Object lock) {
        Continuation continuation = (Continuation)request.getAttribute("org.mortbay.jetty.ajax.Continuation");
        if (continuation == null) {
            continuation = new WaitingContinuation(lock);
        }
        else if (continuation instanceof WaitingContinuation && lock != null) {
            ((WaitingContinuation)continuation).setMutex(lock);
        }
        return continuation;
    }
}
