// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import java.util.Iterator;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.spi.service.ServiceFinder;

public final class WebApplicationFactory
{
    private WebApplicationFactory() {
    }
    
    public static WebApplication createWebApplication() throws ContainerException {
        final Iterator i$ = ServiceFinder.find(WebApplicationProvider.class).iterator();
        if (i$.hasNext()) {
            final WebApplicationProvider wap = i$.next();
            return wap.createWebApplication();
        }
        throw new ContainerException("No WebApplication provider is present");
    }
}
