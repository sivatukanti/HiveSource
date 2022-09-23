// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.Iterator;
import com.google.inject.internal.util.$Iterators;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import com.google.inject.Binding;
import com.google.inject.Scopes;
import java.util.Set;
import com.google.inject.Injector;
import javax.servlet.ServletContext;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.BindingTargetVisitor;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import com.google.inject.Key;
import com.google.inject.spi.ProviderWithExtensionVisitor;

class ServletDefinition implements ProviderWithExtensionVisitor<ServletDefinition>
{
    private final String pattern;
    private final Key<? extends HttpServlet> servletKey;
    private final UriPatternMatcher patternMatcher;
    private final Map<String, String> initParams;
    private final HttpServlet servletInstance;
    private final AtomicReference<HttpServlet> httpServlet;
    
    public ServletDefinition(final String pattern, final Key<? extends HttpServlet> servletKey, final UriPatternMatcher patternMatcher, final Map<String, String> initParams, final HttpServlet servletInstance) {
        this.httpServlet = new AtomicReference<HttpServlet>();
        this.pattern = pattern;
        this.servletKey = servletKey;
        this.patternMatcher = patternMatcher;
        this.initParams = Collections.unmodifiableMap((Map<? extends String, ? extends String>)new HashMap<String, String>(initParams));
        this.servletInstance = servletInstance;
    }
    
    public ServletDefinition get() {
        return this;
    }
    
    public <B, V> V acceptExtensionVisitor(final BindingTargetVisitor<B, V> visitor, final ProviderInstanceBinding<? extends B> binding) {
        if (!(visitor instanceof ServletModuleTargetVisitor)) {
            return visitor.visit(binding);
        }
        if (this.servletInstance != null) {
            return ((ServletModuleTargetVisitor)visitor).visit(new InstanceServletBindingImpl(this.initParams, this.pattern, this.servletInstance, this.patternMatcher));
        }
        return ((ServletModuleTargetVisitor)visitor).visit(new LinkedServletBindingImpl(this.initParams, this.pattern, this.servletKey, this.patternMatcher));
    }
    
    boolean shouldServe(final String uri) {
        return this.patternMatcher.matches(uri);
    }
    
    public void init(final ServletContext servletContext, final Injector injector, final Set<HttpServlet> initializedSoFar) throws ServletException {
        if (!Scopes.isSingleton(injector.getBinding(this.servletKey))) {
            throw new ServletException("Servlets must be bound as singletons. " + this.servletKey + " was not bound in singleton scope.");
        }
        final HttpServlet httpServlet = injector.getInstance(this.servletKey);
        this.httpServlet.set(httpServlet);
        if (initializedSoFar.contains(httpServlet)) {
            return;
        }
        httpServlet.init(new ServletConfig() {
            public String getServletName() {
                return ServletDefinition.this.servletKey.toString();
            }
            
            public ServletContext getServletContext() {
                return servletContext;
            }
            
            public String getInitParameter(final String s) {
                return ServletDefinition.this.initParams.get(s);
            }
            
            public Enumeration getInitParameterNames() {
                return $Iterators.asEnumeration((Iterator<Object>)ServletDefinition.this.initParams.keySet().iterator());
            }
        });
        initializedSoFar.add(httpServlet);
    }
    
    public void destroy(final Set<HttpServlet> destroyedSoFar) {
        final HttpServlet reference = this.httpServlet.get();
        if (null == reference || destroyedSoFar.contains(reference)) {
            return;
        }
        try {
            reference.destroy();
        }
        finally {
            destroyedSoFar.add(reference);
        }
    }
    
    public boolean service(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest)servletRequest;
        final String path = request.getRequestURI().substring(request.getContextPath().length());
        final boolean serve = this.shouldServe(path);
        if (serve) {
            this.doService(servletRequest, servletResponse);
        }
        return serve;
    }
    
    void doService(final ServletRequest servletRequest, final ServletResponse servletResponse) throws ServletException, IOException {
        final HttpServletRequest request = new HttpServletRequestWrapper((HttpServletRequest)servletRequest) {
            private String path;
            private boolean pathComputed = false;
            private boolean pathInfoComputed = false;
            private String pathInfo;
            
            @Override
            public String getPathInfo() {
                if (!this.isPathInfoComputed()) {
                    final int servletPathLength = this.getServletPath().length();
                    this.pathInfo = this.getRequestURI().substring(this.getContextPath().length()).replaceAll("[/]{2,}", "/");
                    this.pathInfo = ((this.pathInfo.length() > servletPathLength) ? this.pathInfo.substring(servletPathLength) : null);
                    if ("".equals(this.pathInfo) && servletPathLength != 0) {
                        this.pathInfo = null;
                    }
                    this.pathInfoComputed = true;
                }
                return this.pathInfo;
            }
            
            private boolean isPathInfoComputed() {
                return this.pathInfoComputed && null == servletRequest.getAttribute("javax.servlet.forward.servlet_path");
            }
            
            private boolean isPathComputed() {
                return this.pathComputed && null == servletRequest.getAttribute("javax.servlet.forward.servlet_path");
            }
            
            @Override
            public String getServletPath() {
                return this.computePath();
            }
            
            @Override
            public String getPathTranslated() {
                final String info = this.getPathInfo();
                return (null == info) ? null : this.getRealPath(info);
            }
            
            private String computePath() {
                if (!this.isPathComputed()) {
                    final String servletPath = super.getServletPath();
                    this.path = ServletDefinition.this.patternMatcher.extractPath(servletPath);
                    this.pathComputed = true;
                    if (null == this.path) {
                        this.path = servletPath;
                    }
                }
                return this.path;
            }
        };
        this.httpServlet.get().service(request, servletResponse);
    }
    
    String getKey() {
        return this.servletKey.toString();
    }
    
    String getPattern() {
        return this.pattern;
    }
}
