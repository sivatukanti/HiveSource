// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.resource;

import com.sun.jersey.server.spi.component.ResourceComponentInjector;
import com.sun.jersey.core.spi.component.ioc.IoCDestroyable;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.server.spi.component.ResourceComponentConstructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.server.spi.component.ResourceComponentDestructor;
import com.sun.jersey.core.spi.component.ComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCProxiedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.server.spi.component.ResourceComponentProvider;
import com.sun.jersey.core.spi.component.ComponentScope;
import javax.ws.rs.core.Context;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import java.util.logging.Logger;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactory;

public final class SingletonFactory implements ResourceComponentProviderFactory
{
    private static final Logger LOGGER;
    private final ServerInjectableProviderContext sipc;
    
    public SingletonFactory(@Context final ServerInjectableProviderContext sipc) {
        this.sipc = sipc;
    }
    
    @Override
    public ComponentScope getScope(final Class c) {
        return ComponentScope.Singleton;
    }
    
    @Override
    public ResourceComponentProvider getComponentProvider(final Class c) {
        return new Singleton();
    }
    
    @Override
    public ResourceComponentProvider getComponentProvider(final IoCComponentProvider icp, final Class c) {
        if (icp instanceof IoCInstantiatedComponentProvider) {
            return new SingletonInstantiated((IoCInstantiatedComponentProvider)icp);
        }
        if (icp instanceof IoCProxiedComponentProvider) {
            return new SingletonProxied((IoCProxiedComponentProvider)icp);
        }
        throw new IllegalStateException();
    }
    
    static {
        LOGGER = Logger.getLogger(SingletonFactory.class.getName());
    }
    
    private abstract class AbstractSingleton implements ResourceComponentProvider
    {
        private ResourceComponentDestructor rcd;
        protected Object resource;
        
        @Override
        public void init(final AbstractResource abstractResource) {
            this.rcd = new ResourceComponentDestructor(abstractResource);
        }
        
        @Override
        public final Object getInstance(final HttpContext hc) {
            return this.resource;
        }
        
        @Override
        public final Object getInstance() {
            return this.resource;
        }
        
        @Override
        public final ComponentScope getScope() {
            return ComponentScope.Singleton;
        }
        
        @Override
        public void destroy() {
            try {
                this.rcd.destroy(this.resource);
            }
            catch (IllegalAccessException ex) {
                SingletonFactory.LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex);
            }
            catch (IllegalArgumentException ex2) {
                SingletonFactory.LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex2);
            }
            catch (InvocationTargetException ex3) {
                SingletonFactory.LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex3);
            }
        }
    }
    
    private class Singleton extends AbstractSingleton
    {
        @Override
        public void init(final AbstractResource abstractResource) {
            super.init(abstractResource);
            final ResourceComponentConstructor rcc = new ResourceComponentConstructor(SingletonFactory.this.sipc, ComponentScope.Singleton, abstractResource);
            try {
                this.resource = rcc.construct(null);
            }
            catch (InvocationTargetException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
            catch (InstantiationException ex2) {
                throw new ContainerException("Unable to create resource", ex2);
            }
            catch (IllegalAccessException ex3) {
                throw new ContainerException("Unable to create resource", ex3);
            }
        }
    }
    
    private class SingletonInstantiated extends AbstractSingleton
    {
        private final IoCInstantiatedComponentProvider iicp;
        private final IoCDestroyable destroyable;
        
        SingletonInstantiated(final IoCInstantiatedComponentProvider iicp) {
            this.iicp = iicp;
            this.destroyable = ((iicp instanceof IoCDestroyable) ? iicp : null);
        }
        
        @Override
        public void init(final AbstractResource abstractResource) {
            super.init(abstractResource);
            this.resource = this.iicp.getInstance();
            if (this.destroyable == null) {
                final ResourceComponentInjector rci = new ResourceComponentInjector(SingletonFactory.this.sipc, ComponentScope.Singleton, abstractResource);
                rci.inject(null, this.iicp.getInjectableInstance(this.resource));
            }
        }
        
        @Override
        public void destroy() {
            if (this.destroyable != null) {
                this.destroyable.destroy(this.resource);
            }
            else {
                super.destroy();
            }
        }
    }
    
    private class SingletonProxied extends AbstractSingleton
    {
        private final IoCProxiedComponentProvider ipcp;
        
        SingletonProxied(final IoCProxiedComponentProvider ipcp) {
            this.ipcp = ipcp;
        }
        
        @Override
        public void init(final AbstractResource abstractResource) {
            super.init(abstractResource);
            final ResourceComponentConstructor rcc = new ResourceComponentConstructor(SingletonFactory.this.sipc, ComponentScope.Singleton, abstractResource);
            try {
                final Object o = rcc.construct(null);
                this.resource = this.ipcp.proxy(o);
            }
            catch (InvocationTargetException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
            catch (InstantiationException ex2) {
                throw new ContainerException("Unable to create resource", ex2);
            }
            catch (IllegalAccessException ex3) {
                throw new ContainerException("Unable to create resource", ex3);
            }
        }
    }
}
