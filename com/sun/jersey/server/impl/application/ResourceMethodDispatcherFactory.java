// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.application;

import com.sun.jersey.impl.ImplMessages;
import java.util.logging.Level;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.api.model.AbstractResourceMethod;
import java.util.Iterator;
import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.core.spi.component.ProviderServices;
import java.util.Set;
import java.util.logging.Logger;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;

public final class ResourceMethodDispatcherFactory implements ResourceMethodDispatchProvider
{
    private static final Logger LOGGER;
    private final Set<ResourceMethodDispatchProvider> dispatchers;
    
    private ResourceMethodDispatcherFactory(final ProviderServices providerServices) {
        this.dispatchers = providerServices.getProvidersAndServices(ResourceMethodDispatchProvider.class);
    }
    
    public static ResourceMethodDispatchProvider create(final ProviderServices providerServices) {
        ResourceMethodDispatchProvider p = new ResourceMethodDispatcherFactory(providerServices);
        for (final ResourceMethodDispatchAdapter a : providerServices.getProvidersAndServices(ResourceMethodDispatchAdapter.class)) {
            p = a.adapt(p);
        }
        return p;
    }
    
    @Override
    public RequestDispatcher create(final AbstractResourceMethod abstractResourceMethod) {
        Errors.mark();
        for (final ResourceMethodDispatchProvider rmdp : this.dispatchers) {
            try {
                final RequestDispatcher d = rmdp.create(abstractResourceMethod);
                if (d != null) {
                    Errors.reset();
                    return d;
                }
                continue;
            }
            catch (Exception e) {
                ResourceMethodDispatcherFactory.LOGGER.log(Level.SEVERE, ImplMessages.ERROR_PROCESSING_METHOD(abstractResourceMethod.getMethod(), rmdp.getClass().getName()), e);
            }
        }
        Errors.unmark();
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(ResourceMethodDispatcherFactory.class.getName());
    }
}
