// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.guice.spi.container.servlet;

import com.google.inject.servlet.ServletScopes;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.google.inject.Scope;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import javax.servlet.ServletException;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.WebConfig;
import java.util.Map;
import javax.inject.Inject;
import com.sun.jersey.spi.container.WebApplication;
import com.google.inject.Injector;
import javax.inject.Singleton;
import com.sun.jersey.spi.container.servlet.ServletContainer;

@Singleton
public class GuiceContainer extends ServletContainer
{
    private static final long serialVersionUID = 1931878850157940335L;
    private final Injector injector;
    private WebApplication webapp;
    
    @Inject
    public GuiceContainer(final Injector injector) {
        this.injector = injector;
    }
    
    @Override
    protected ResourceConfig getDefaultResourceConfig(final Map<String, Object> props, final WebConfig webConfig) throws ServletException {
        return new DefaultResourceConfig();
    }
    
    @Override
    protected void initiate(final ResourceConfig config, final WebApplication webapp) {
        (this.webapp = webapp).initiate(config, new ServletGuiceComponentProviderFactory(config, this.injector));
    }
    
    public WebApplication getWebApplication() {
        return this.webapp;
    }
    
    public class ServletGuiceComponentProviderFactory extends GuiceComponentProviderFactory
    {
        public ServletGuiceComponentProviderFactory(final ResourceConfig config, final Injector injector) {
            super(config, injector);
        }
        
        @Override
        public Map<Scope, ComponentScope> createScopeMap() {
            final Map<Scope, ComponentScope> m = super.createScopeMap();
            m.put(ServletScopes.REQUEST, ComponentScope.PerRequest);
            return m;
        }
    }
}
