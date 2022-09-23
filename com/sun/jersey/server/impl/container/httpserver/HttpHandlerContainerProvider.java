// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.httpserver;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpHandler;
import com.sun.jersey.spi.container.ContainerProvider;

public final class HttpHandlerContainerProvider implements ContainerProvider<HttpHandler>
{
    @Override
    public HttpHandler createContainer(final Class<HttpHandler> type, final ResourceConfig resourceConfig, final WebApplication application) throws ContainerException {
        if (type != HttpHandler.class) {
            return null;
        }
        return new HttpHandlerContainer(application);
    }
}
