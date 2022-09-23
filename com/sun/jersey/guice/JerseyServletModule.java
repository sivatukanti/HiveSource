// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.guice;

import com.sun.jersey.api.core.HttpResponseContext;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.HttpHeaders;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.ExtendedUriInfo;
import javax.ws.rs.core.UriInfo;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.container.ExceptionMapperContext;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.ws.rs.ext.Providers;
import com.google.inject.Provides;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.google.inject.servlet.ServletModule;

public class JerseyServletModule extends ServletModule
{
    @Provides
    public WebApplication webApp(final GuiceContainer guiceContainer) {
        return guiceContainer.getWebApplication();
    }
    
    @Provides
    public Providers providers(final WebApplication webApplication) {
        return webApplication.getProviders();
    }
    
    @Provides
    public FeaturesAndProperties fearturesAndProperties(final WebApplication webApplication) {
        return webApplication.getFeaturesAndProperties();
    }
    
    @Provides
    public MessageBodyWorkers messageBodyWorkers(final WebApplication webApplication) {
        return webApplication.getMessageBodyWorkers();
    }
    
    @Provides
    public ExceptionMapperContext exceptionMapperContext(final WebApplication webApplication) {
        return webApplication.getExceptionMapperContext();
    }
    
    @RequestScoped
    @Provides
    public HttpContext httpContext(final WebApplication webApplication) {
        return webApplication.getThreadLocalHttpContext();
    }
    
    @Provides
    @RequestScoped
    public UriInfo uriInfo(final WebApplication wa) {
        return wa.getThreadLocalHttpContext().getUriInfo();
    }
    
    @Provides
    @RequestScoped
    public ExtendedUriInfo extendedUriInfo(final WebApplication wa) {
        return wa.getThreadLocalHttpContext().getUriInfo();
    }
    
    @RequestScoped
    @Provides
    public HttpRequestContext requestContext(final WebApplication wa) {
        return wa.getThreadLocalHttpContext().getRequest();
    }
    
    @RequestScoped
    @Provides
    public HttpHeaders httpHeaders(final WebApplication wa) {
        return wa.getThreadLocalHttpContext().getRequest();
    }
    
    @RequestScoped
    @Provides
    public Request request(final WebApplication wa) {
        return wa.getThreadLocalHttpContext().getRequest();
    }
    
    @RequestScoped
    @Provides
    public SecurityContext securityContext(final WebApplication wa) {
        return wa.getThreadLocalHttpContext().getRequest();
    }
    
    @RequestScoped
    @Provides
    public HttpResponseContext responseContext(final WebApplication wa) {
        return wa.getThreadLocalHttpContext().getResponse();
    }
}
