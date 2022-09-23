// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import com.sun.jersey.server.impl.application.ResourceMethodDispatcherFactory;
import java.util.Iterator;
import com.sun.jersey.impl.ImplMessages;
import java.util.logging.Level;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.core.spi.component.ProviderServices;
import java.util.Set;
import java.util.logging.Logger;

public class ResourceMethodCustomInvokerDispatchFactory
{
    private static final Logger LOGGER;
    final Set<ResourceMethodCustomInvokerDispatchProvider> customInvokerDispatchProviders;
    
    public ResourceMethodCustomInvokerDispatchFactory(final ProviderServices providerServices) {
        this.customInvokerDispatchProviders = providerServices.getProvidersAndServices(ResourceMethodCustomInvokerDispatchProvider.class);
    }
    
    public RequestDispatcher getDispatcher(final AbstractResourceMethod abstractResourceMethod, final JavaMethodInvoker invoker) {
        if (invoker == null) {
            return null;
        }
        Errors.mark();
        for (final ResourceMethodCustomInvokerDispatchProvider rmdp : this.customInvokerDispatchProviders) {
            try {
                final RequestDispatcher d = rmdp.create(abstractResourceMethod, invoker);
                if (d != null) {
                    Errors.reset();
                    return d;
                }
                continue;
            }
            catch (Exception e) {
                ResourceMethodCustomInvokerDispatchFactory.LOGGER.log(Level.SEVERE, ImplMessages.ERROR_PROCESSING_METHOD(abstractResourceMethod.getMethod(), rmdp.getClass().getName()), e);
            }
        }
        Errors.unmark();
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(ResourceMethodDispatcherFactory.class.getName());
    }
}
