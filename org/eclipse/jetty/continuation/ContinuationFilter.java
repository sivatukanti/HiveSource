// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.continuation;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.Filter;

public class ContinuationFilter implements Filter
{
    static boolean _initialized;
    static boolean __debug;
    private boolean _faux;
    private boolean _jetty6;
    private boolean _filtered;
    ServletContext _context;
    private boolean _debug;
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        final boolean jetty_7_or_greater = "org.eclipse.jetty.servlet".equals(filterConfig.getClass().getPackage().getName());
        this._context = filterConfig.getServletContext();
        String param = filterConfig.getInitParameter("debug");
        this._debug = (param != null && Boolean.parseBoolean(param));
        if (this._debug) {
            ContinuationFilter.__debug = true;
        }
        param = filterConfig.getInitParameter("jetty6");
        if (param == null) {
            param = filterConfig.getInitParameter("partial");
        }
        if (param != null) {
            this._jetty6 = Boolean.parseBoolean(param);
        }
        else {
            this._jetty6 = (ContinuationSupport.__jetty6 && !jetty_7_or_greater);
        }
        param = filterConfig.getInitParameter("faux");
        if (param != null) {
            this._faux = Boolean.parseBoolean(param);
        }
        else {
            this._faux = (!jetty_7_or_greater && !this._jetty6 && this._context.getMajorVersion() < 3);
        }
        this._filtered = (this._faux || this._jetty6);
        if (this._debug) {
            this._context.log("ContinuationFilter  jetty=" + jetty_7_or_greater + " jetty6=" + this._jetty6 + " faux=" + this._faux + " filtered=" + this._filtered + " servlet3=" + ContinuationSupport.__servlet3);
        }
        ContinuationFilter._initialized = true;
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (this._filtered) {
            final Continuation c = (Continuation)request.getAttribute("org.eclipse.jetty.continuation");
            FilteredContinuation fc;
            if (this._faux && (c == null || !(c instanceof FauxContinuation))) {
                fc = new FauxContinuation(request);
                request.setAttribute("org.eclipse.jetty.continuation", fc);
            }
            else {
                fc = (FilteredContinuation)c;
            }
            boolean complete = false;
            while (!complete) {
                try {
                    if (fc != null && !fc.enter(response)) {
                        continue;
                    }
                    chain.doFilter(request, response);
                }
                catch (ContinuationThrowable e) {
                    this.debug("faux", e);
                }
                finally {
                    if (fc == null) {
                        fc = (FilteredContinuation)request.getAttribute("org.eclipse.jetty.continuation");
                    }
                    complete = (fc == null || fc.exit());
                }
            }
        }
        else {
            try {
                chain.doFilter(request, response);
            }
            catch (ContinuationThrowable e2) {
                this.debug("caught", e2);
            }
        }
    }
    
    private void debug(final String string) {
        if (this._debug) {
            this._context.log(string);
        }
    }
    
    private void debug(final String string, final Throwable th) {
        if (this._debug) {
            if (th instanceof ContinuationThrowable) {
                this._context.log(string + ":" + th);
            }
            else {
                this._context.log(string, th);
            }
        }
    }
    
    public void destroy() {
    }
    
    public interface FilteredContinuation extends Continuation
    {
        boolean enter(final ServletResponse p0);
        
        boolean exit();
    }
}
