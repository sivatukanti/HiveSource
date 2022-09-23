// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import java.util.logging.Logger;
import com.google.inject.Singleton;
import java.util.Map;
import javax.servlet.http.HttpSession;
import com.google.inject.Provides;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletRequest;
import java.lang.annotation.Annotation;
import com.google.inject.AbstractModule;

final class InternalServletModule extends AbstractModule
{
    @Override
    protected void configure() {
        this.bindScope(RequestScoped.class, ServletScopes.REQUEST);
        this.bindScope(SessionScoped.class, ServletScopes.SESSION);
        this.bind(ServletRequest.class).to((Class<?>)HttpServletRequest.class);
        this.bind(ServletResponse.class).to((Class<?>)HttpServletResponse.class);
        this.requestStaticInjection(GuiceFilter.class);
        this.bind(ManagedFilterPipeline.class);
        this.bind(ManagedServletPipeline.class);
        this.bind(FilterPipeline.class).to((Class<?>)ManagedFilterPipeline.class).asEagerSingleton();
        this.bind(ServletContext.class).toProvider((Class<? extends Provider<?>>)BackwardsCompatibleServletContextProvider.class);
    }
    
    @Provides
    @RequestScoped
    HttpServletRequest provideHttpServletRequest() {
        return GuiceFilter.getRequest();
    }
    
    @Provides
    @RequestScoped
    HttpServletResponse provideHttpServletResponse() {
        return GuiceFilter.getResponse();
    }
    
    @Provides
    HttpSession provideHttpSession() {
        return GuiceFilter.getRequest().getSession();
    }
    
    @Provides
    @RequestScoped
    @RequestParameters
    Map<String, String[]> provideRequestParameters() {
        return GuiceFilter.getRequest().getParameterMap();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof InternalServletModule;
    }
    
    @Override
    public int hashCode() {
        return InternalServletModule.class.hashCode();
    }
    
    @Singleton
    static class BackwardsCompatibleServletContextProvider implements Provider<ServletContext>
    {
        private ServletContext injectedServletContext;
        
        void set(final ServletContext injectedServletContext) {
            this.injectedServletContext = injectedServletContext;
        }
        
        public ServletContext get() {
            if (null != this.injectedServletContext) {
                return this.injectedServletContext;
            }
            Logger.getLogger(InternalServletModule.class.getName()).warning("You are attempting to use a deprecated API (specifically, attempting to @Inject ServletContext inside an eagerly created singleton. While we allow this for backwards compatibility, be warned that this MAY have unexpected behavior if you have more than one injector (with ServletModule) running in the same JVM. Please consult the Guice documentation at http://code.google.com/p/google-guice/wiki/Servlets for more information.");
            return GuiceFilter.getServletContext();
        }
    }
}
