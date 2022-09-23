// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.Set;
import javax.servlet.Filter;
import java.util.Map;
import com.google.inject.internal.util.$Sets;
import com.google.inject.internal.util.$Maps;
import java.util.Iterator;
import java.util.List;
import com.google.inject.Binding;
import com.google.inject.internal.util.$Lists;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.Injector;
import javax.servlet.ServletContext;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
class ManagedFilterPipeline implements FilterPipeline
{
    private final FilterDefinition[] filterDefinitions;
    private final ManagedServletPipeline servletPipeline;
    private final Provider<ServletContext> servletContext;
    private final Injector injector;
    private volatile boolean initialized;
    private static final TypeLiteral<FilterDefinition> FILTER_DEFS;
    
    @Inject
    public ManagedFilterPipeline(final Injector injector, final ManagedServletPipeline servletPipeline, final Provider<ServletContext> servletContext) {
        this.initialized = false;
        this.injector = injector;
        this.servletPipeline = servletPipeline;
        this.servletContext = servletContext;
        this.filterDefinitions = this.collectFilterDefinitions(injector);
    }
    
    private FilterDefinition[] collectFilterDefinitions(final Injector injector) {
        final List<FilterDefinition> filterDefinitions = (List<FilterDefinition>)$Lists.newArrayList();
        for (final Binding<FilterDefinition> entry : injector.findBindingsByType(ManagedFilterPipeline.FILTER_DEFS)) {
            filterDefinitions.add(entry.getProvider().get());
        }
        return filterDefinitions.toArray(new FilterDefinition[filterDefinitions.size()]);
    }
    
    public synchronized void initPipeline(final ServletContext servletContext) throws ServletException {
        if (this.initialized) {
            return;
        }
        final Set<Filter> initializedSoFar = $Sets.newSetFromMap((Map<Filter, Boolean>)$Maps.newIdentityHashMap());
        for (final FilterDefinition filterDefinition : this.filterDefinitions) {
            filterDefinition.init(servletContext, this.injector, initializedSoFar);
        }
        this.servletPipeline.init(servletContext, this.injector);
        this.initialized = true;
    }
    
    public void dispatch(final ServletRequest request, final ServletResponse response, final FilterChain proceedingFilterChain) throws IOException, ServletException {
        if (!this.initialized) {
            this.initPipeline(this.servletContext.get());
        }
        new FilterChainInvocation(this.filterDefinitions, this.servletPipeline, proceedingFilterChain).doFilter(this.withDispatcher(request, this.servletPipeline), response);
    }
    
    private ServletRequest withDispatcher(final ServletRequest servletRequest, final ManagedServletPipeline servletPipeline) {
        final HttpServletRequest request = (HttpServletRequest)servletRequest;
        if (!servletPipeline.hasServletsMapped()) {
            return servletRequest;
        }
        return new HttpServletRequestWrapper(request) {
            @Override
            public RequestDispatcher getRequestDispatcher(final String path) {
                final RequestDispatcher dispatcher = servletPipeline.getRequestDispatcher(path);
                return (null != dispatcher) ? dispatcher : super.getRequestDispatcher(path);
            }
        };
    }
    
    public void destroyPipeline() {
        this.servletPipeline.destroy();
        final Set<Filter> destroyedSoFar = $Sets.newSetFromMap((Map<Filter, Boolean>)$Maps.newIdentityHashMap());
        for (final FilterDefinition filterDefinition : this.filterDefinitions) {
            filterDefinition.destroy(destroyedSoFar);
        }
    }
    
    static {
        FILTER_DEFS = TypeLiteral.get(FilterDefinition.class);
    }
}
