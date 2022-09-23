// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.FilterConfig;
import com.google.inject.OutOfScopeException;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import java.lang.ref.WeakReference;
import com.google.inject.Inject;
import javax.servlet.Filter;

public class GuiceFilter implements Filter
{
    static final ThreadLocal<Context> localContext;
    static volatile FilterPipeline pipeline;
    @Inject
    private final FilterPipeline injectedPipeline;
    static volatile WeakReference<ServletContext> servletContext;
    private static final String MULTIPLE_INJECTORS_WARNING;
    
    public GuiceFilter() {
        this.injectedPipeline = null;
    }
    
    @Inject
    static void setPipeline(final FilterPipeline pipeline) {
        if (GuiceFilter.pipeline instanceof ManagedFilterPipeline) {
            Logger.getLogger(GuiceFilter.class.getName()).warning(GuiceFilter.MULTIPLE_INJECTORS_WARNING);
        }
        GuiceFilter.pipeline = pipeline;
    }
    
    static void reset() {
        GuiceFilter.pipeline = new DefaultFilterPipeline();
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final Context previous = GuiceFilter.localContext.get();
        final FilterPipeline filterPipeline = (null != this.injectedPipeline) ? this.injectedPipeline : GuiceFilter.pipeline;
        try {
            GuiceFilter.localContext.set(new Context((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse));
            filterPipeline.dispatch(servletRequest, servletResponse, filterChain);
        }
        finally {
            GuiceFilter.localContext.set(previous);
        }
    }
    
    static HttpServletRequest getRequest() {
        return getContext().getRequest();
    }
    
    static HttpServletResponse getResponse() {
        return getContext().getResponse();
    }
    
    static ServletContext getServletContext() {
        return GuiceFilter.servletContext.get();
    }
    
    static Context getContext() {
        final Context context = GuiceFilter.localContext.get();
        if (context == null) {
            throw new OutOfScopeException("Cannot access scoped object. Either we are not currently inside an HTTP Servlet request, or you may have forgotten to apply " + GuiceFilter.class.getName() + " as a servlet filter for this request.");
        }
        return context;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        final ServletContext servletContext = filterConfig.getServletContext();
        GuiceFilter.servletContext = new WeakReference<ServletContext>(servletContext);
        final FilterPipeline filterPipeline = (null != this.injectedPipeline) ? this.injectedPipeline : GuiceFilter.pipeline;
        filterPipeline.initPipeline(servletContext);
    }
    
    public void destroy() {
        try {
            final FilterPipeline filterPipeline = (null != this.injectedPipeline) ? this.injectedPipeline : GuiceFilter.pipeline;
            filterPipeline.destroyPipeline();
        }
        finally {
            reset();
            GuiceFilter.servletContext.clear();
        }
    }
    
    static {
        localContext = new ThreadLocal<Context>();
        GuiceFilter.pipeline = new DefaultFilterPipeline();
        GuiceFilter.servletContext = new WeakReference<ServletContext>(null);
        MULTIPLE_INJECTORS_WARNING = "Multiple Servlet injectors detected. This is a warning indicating that you have more than one " + GuiceFilter.class.getSimpleName() + " running " + "in your web application. If this is deliberate, you may safely " + "ignore this message. If this is NOT deliberate however, " + "your application may not work as expected.";
    }
    
    static class Context
    {
        final HttpServletRequest request;
        final HttpServletResponse response;
        
        Context(final HttpServletRequest request, final HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }
        
        HttpServletRequest getRequest() {
            return this.request;
        }
        
        HttpServletResponse getResponse() {
            return this.response;
        }
    }
}
