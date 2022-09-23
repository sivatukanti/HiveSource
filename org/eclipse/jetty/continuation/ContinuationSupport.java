// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.continuation;

import javax.servlet.ServletResponse;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletRequest;
import java.lang.reflect.Constructor;

public class ContinuationSupport
{
    static final boolean __jetty6;
    static final boolean __servlet3;
    static final Class<?> __waitingContinuation;
    static final Constructor<? extends Continuation> __newServlet3Continuation;
    static final Constructor<? extends Continuation> __newJetty6Continuation;
    
    public static Continuation getContinuation(ServletRequest request) {
        Continuation continuation = (Continuation)request.getAttribute("org.eclipse.jetty.continuation");
        if (continuation != null) {
            return continuation;
        }
        while (request instanceof ServletRequestWrapper) {
            request = ((ServletRequestWrapper)request).getRequest();
        }
        if (ContinuationSupport.__servlet3) {
            try {
                continuation = (Continuation)ContinuationSupport.__newServlet3Continuation.newInstance(request);
                request.setAttribute("org.eclipse.jetty.continuation", continuation);
                return continuation;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (ContinuationSupport.__jetty6) {
            final Object c = request.getAttribute("org.mortbay.jetty.ajax.Continuation");
            try {
                if (c == null || ContinuationSupport.__waitingContinuation == null || ContinuationSupport.__waitingContinuation.isInstance(c)) {
                    continuation = new FauxContinuation(request);
                }
                else {
                    continuation = (Continuation)ContinuationSupport.__newJetty6Continuation.newInstance(request, c);
                }
                request.setAttribute("org.eclipse.jetty.continuation", continuation);
                return continuation;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        throw new IllegalStateException("!(Jetty || Servlet 3.0 || ContinuationFilter)");
    }
    
    @Deprecated
    public static Continuation getContinuation(final ServletRequest request, final ServletResponse response) {
        return getContinuation(request);
    }
    
    static {
        boolean servlet3Support = false;
        Constructor<? extends Continuation> s3cc = null;
        try {
            final boolean servlet3 = ServletRequest.class.getMethod("startAsync", (Class<?>[])new Class[0]) != null;
            if (servlet3) {
                final Class<? extends Continuation> s3c = ContinuationSupport.class.getClassLoader().loadClass("org.eclipse.jetty.continuation.Servlet3Continuation").asSubclass(Continuation.class);
                s3cc = s3c.getConstructor(ServletRequest.class);
                servlet3Support = true;
            }
        }
        catch (Exception e) {}
        finally {
            __servlet3 = servlet3Support;
            __newServlet3Continuation = s3cc;
        }
        boolean jetty6Support = false;
        Constructor<? extends Continuation> j6cc = null;
        try {
            final Class<?> jetty6ContinuationClass = ContinuationSupport.class.getClassLoader().loadClass("org.mortbay.util.ajax.Continuation");
            final boolean jetty6 = jetty6ContinuationClass != null;
            if (jetty6) {
                final Class<? extends Continuation> j6c = ContinuationSupport.class.getClassLoader().loadClass("org.eclipse.jetty.continuation.Jetty6Continuation").asSubclass(Continuation.class);
                j6cc = j6c.getConstructor(ServletRequest.class, jetty6ContinuationClass);
                jetty6Support = true;
            }
        }
        catch (Exception e2) {}
        finally {
            __jetty6 = jetty6Support;
            __newJetty6Continuation = j6cc;
        }
        Class<?> waiting = null;
        try {
            waiting = ContinuationSupport.class.getClassLoader().loadClass("org.mortbay.util.ajax.WaitingContinuation");
        }
        catch (Exception e3) {}
        finally {
            __waitingContinuation = waiting;
        }
    }
}
