// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.Iterator;
import com.google.inject.internal.util.$Iterators;
import java.util.Enumeration;
import javax.servlet.FilterConfig;
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
import javax.servlet.Filter;
import com.google.inject.Key;
import com.google.inject.spi.ProviderWithExtensionVisitor;

class FilterDefinition implements ProviderWithExtensionVisitor<FilterDefinition>
{
    private final String pattern;
    private final Key<? extends Filter> filterKey;
    private final UriPatternMatcher patternMatcher;
    private final Map<String, String> initParams;
    private final Filter filterInstance;
    private final AtomicReference<Filter> filter;
    
    public FilterDefinition(final String pattern, final Key<? extends Filter> filterKey, final UriPatternMatcher patternMatcher, final Map<String, String> initParams, final Filter filterInstance) {
        this.filter = new AtomicReference<Filter>();
        this.pattern = pattern;
        this.filterKey = filterKey;
        this.patternMatcher = patternMatcher;
        this.initParams = Collections.unmodifiableMap((Map<? extends String, ? extends String>)new HashMap<String, String>(initParams));
        this.filterInstance = filterInstance;
    }
    
    public FilterDefinition get() {
        return this;
    }
    
    public <B, V> V acceptExtensionVisitor(final BindingTargetVisitor<B, V> visitor, final ProviderInstanceBinding<? extends B> binding) {
        if (!(visitor instanceof ServletModuleTargetVisitor)) {
            return visitor.visit(binding);
        }
        if (this.filterInstance != null) {
            return ((ServletModuleTargetVisitor)visitor).visit(new InstanceFilterBindingImpl(this.initParams, this.pattern, this.filterInstance, this.patternMatcher));
        }
        return ((ServletModuleTargetVisitor)visitor).visit(new LinkedFilterBindingImpl(this.initParams, this.pattern, this.filterKey, this.patternMatcher));
    }
    
    private boolean shouldFilter(final String uri) {
        return this.patternMatcher.matches(uri);
    }
    
    public void init(final ServletContext servletContext, final Injector injector, final Set<Filter> initializedSoFar) throws ServletException {
        if (!Scopes.isSingleton(injector.getBinding(this.filterKey))) {
            throw new ServletException("Filters must be bound as singletons. " + this.filterKey + " was not bound in singleton scope.");
        }
        final Filter filter = injector.getInstance(this.filterKey);
        this.filter.set(filter);
        if (initializedSoFar.contains(filter)) {
            return;
        }
        filter.init(new FilterConfig() {
            public String getFilterName() {
                return FilterDefinition.this.filterKey.toString();
            }
            
            public ServletContext getServletContext() {
                return servletContext;
            }
            
            public String getInitParameter(final String s) {
                return FilterDefinition.this.initParams.get(s);
            }
            
            public Enumeration getInitParameterNames() {
                return $Iterators.asEnumeration((Iterator<Object>)FilterDefinition.this.initParams.keySet().iterator());
            }
        });
        initializedSoFar.add(filter);
    }
    
    public void destroy(final Set<Filter> destroyedSoFar) {
        final Filter reference = this.filter.get();
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
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChainInvocation filterChainInvocation) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest)servletRequest;
        final String path = request.getRequestURI().substring(request.getContextPath().length());
        if (this.shouldFilter(path)) {
            this.filter.get().doFilter(servletRequest, servletResponse, filterChainInvocation);
        }
        else {
            filterChainInvocation.doFilter(servletRequest, servletResponse);
        }
    }
    
    Filter getFilter() {
        return this.filter.get();
    }
}
