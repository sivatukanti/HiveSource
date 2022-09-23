// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import java.io.IOException;
import com.sun.jersey.spi.monitoring.ResponseListener;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import com.sun.jersey.spi.monitoring.RequestListener;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderFactory;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.api.core.ResourceContext;
import javax.ws.rs.ext.Providers;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.core.Traceable;

public interface WebApplication extends Traceable
{
    boolean isInitiated();
    
    void initiate(final ResourceConfig p0) throws IllegalArgumentException, ContainerException;
    
    void initiate(final ResourceConfig p0, final IoCComponentProviderFactory p1) throws IllegalArgumentException, ContainerException;
    
    WebApplication clone();
    
    FeaturesAndProperties getFeaturesAndProperties();
    
    Providers getProviders();
    
    ResourceContext getResourceContext();
    
    MessageBodyWorkers getMessageBodyWorkers();
    
    ExceptionMapperContext getExceptionMapperContext();
    
    HttpContext getThreadLocalHttpContext();
    
    ServerInjectableProviderFactory getServerInjectableProviderFactory();
    
    RequestListener getRequestListener();
    
    DispatchingListener getDispatchingListener();
    
    ResponseListener getResponseListener();
    
    void handleRequest(final ContainerRequest p0, final ContainerResponseWriter p1) throws IOException;
    
    void handleRequest(final ContainerRequest p0, final ContainerResponse p1) throws IOException;
    
    void destroy();
}
