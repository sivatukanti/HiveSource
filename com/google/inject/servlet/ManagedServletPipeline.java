// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import com.google.inject.internal.util.$Preconditions;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.Set;
import javax.servlet.http.HttpServlet;
import java.util.Map;
import com.google.inject.internal.util.$Sets;
import com.google.inject.internal.util.$Maps;
import javax.servlet.ServletContext;
import java.util.Iterator;
import java.util.List;
import com.google.inject.Binding;
import com.google.inject.internal.util.$Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.Singleton;

@Singleton
class ManagedServletPipeline
{
    private final ServletDefinition[] servletDefinitions;
    private static final TypeLiteral<ServletDefinition> SERVLET_DEFS;
    public static final String REQUEST_DISPATCHER_REQUEST = "javax.servlet.forward.servlet_path";
    
    @Inject
    public ManagedServletPipeline(final Injector injector) {
        this.servletDefinitions = this.collectServletDefinitions(injector);
    }
    
    boolean hasServletsMapped() {
        return this.servletDefinitions.length > 0;
    }
    
    private ServletDefinition[] collectServletDefinitions(final Injector injector) {
        final List<ServletDefinition> servletDefinitions = (List<ServletDefinition>)$Lists.newArrayList();
        for (final Binding<ServletDefinition> entry : injector.findBindingsByType(ManagedServletPipeline.SERVLET_DEFS)) {
            servletDefinitions.add(entry.getProvider().get());
        }
        return servletDefinitions.toArray(new ServletDefinition[servletDefinitions.size()]);
    }
    
    public void init(final ServletContext servletContext, final Injector injector) throws ServletException {
        final Set<HttpServlet> initializedSoFar = $Sets.newSetFromMap((Map<HttpServlet, Boolean>)$Maps.newIdentityHashMap());
        for (final ServletDefinition servletDefinition : this.servletDefinitions) {
            servletDefinition.init(servletContext, injector, initializedSoFar);
        }
    }
    
    public boolean service(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
        for (final ServletDefinition servletDefinition : this.servletDefinitions) {
            if (servletDefinition.service(request, response)) {
                return true;
            }
        }
        return false;
    }
    
    public void destroy() {
        final Set<HttpServlet> destroyedSoFar = $Sets.newSetFromMap((Map<HttpServlet, Boolean>)$Maps.newIdentityHashMap());
        for (final ServletDefinition servletDefinition : this.servletDefinitions) {
            servletDefinition.destroy(destroyedSoFar);
        }
    }
    
    RequestDispatcher getRequestDispatcher(final String path) {
        final String newRequestUri = path;
        for (final ServletDefinition servletDefinition : this.servletDefinitions) {
            if (servletDefinition.shouldServe(path)) {
                return new RequestDispatcher() {
                    public void forward(final ServletRequest servletRequest, final ServletResponse servletResponse) throws ServletException, IOException {
                        $Preconditions.checkState(!servletResponse.isCommitted(), (Object)"Response has been committed--you can only call forward before committing the response (hint: don't flush buffers)");
                        servletResponse.resetBuffer();
                        ServletRequest requestToProcess;
                        if (servletRequest instanceof HttpServletRequest) {
                            requestToProcess = new RequestDispatcherRequestWrapper(servletRequest, newRequestUri);
                        }
                        else {
                            requestToProcess = servletRequest;
                        }
                        servletRequest.setAttribute("javax.servlet.forward.servlet_path", Boolean.TRUE);
                        try {
                            servletDefinition.doService(requestToProcess, servletResponse);
                        }
                        finally {
                            servletRequest.removeAttribute("javax.servlet.forward.servlet_path");
                        }
                    }
                    
                    public void include(final ServletRequest servletRequest, final ServletResponse servletResponse) throws ServletException, IOException {
                        servletRequest.setAttribute("javax.servlet.forward.servlet_path", Boolean.TRUE);
                        try {
                            servletDefinition.doService(servletRequest, servletResponse);
                        }
                        finally {
                            servletRequest.removeAttribute("javax.servlet.forward.servlet_path");
                        }
                    }
                };
            }
        }
        return null;
    }
    
    static {
        SERVLET_DEFS = TypeLiteral.get(ServletDefinition.class);
    }
    
    private static class RequestDispatcherRequestWrapper extends HttpServletRequestWrapper
    {
        private final String newRequestUri;
        
        public RequestDispatcherRequestWrapper(final ServletRequest servletRequest, final String newRequestUri) {
            super((HttpServletRequest)servletRequest);
            this.newRequestUri = newRequestUri;
        }
        
        @Override
        public String getRequestURI() {
            return this.newRequestUri;
        }
    }
}
