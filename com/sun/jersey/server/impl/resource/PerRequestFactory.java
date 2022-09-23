// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.resource;

import com.sun.jersey.server.spi.component.ResourceComponentInjector;
import com.sun.jersey.core.spi.component.ioc.IoCDestroyable;
import javax.ws.rs.WebApplicationException;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.server.spi.component.ResourceComponentConstructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.server.spi.component.ResourceComponentDestructor;
import com.sun.jersey.core.spi.component.ComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCProxiedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.server.spi.component.ResourceComponentProvider;
import com.sun.jersey.core.spi.component.ComponentScope;
import javax.ws.rs.core.Context;
import java.util.Iterator;
import com.sun.jersey.api.container.ContainerException;
import java.util.logging.Level;
import java.util.Map;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import java.util.logging.Logger;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactory;

public final class PerRequestFactory implements ResourceComponentProviderFactory
{
    private static final Logger LOGGER;
    private final ServerInjectableProviderContext sipc;
    private final HttpContext threadLocalHc;
    private static final String SCOPE_PER_REQUEST = "com.sun.jersey.scope.PerRequest";
    
    public static void destroy(final HttpContext hc) {
        final Map<AbstractPerRequest, Object> m = hc.getProperties().get("com.sun.jersey.scope.PerRequest");
        if (m != null) {
            for (final Map.Entry<AbstractPerRequest, Object> e : m.entrySet()) {
                try {
                    e.getKey().destroy(e.getValue());
                }
                catch (ContainerException ex) {
                    PerRequestFactory.LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex);
                }
            }
        }
    }
    
    public PerRequestFactory(@Context final ServerInjectableProviderContext sipc, @Context final HttpContext threadLocalHc) {
        this.sipc = sipc;
        this.threadLocalHc = threadLocalHc;
    }
    
    @Override
    public ComponentScope getScope(final Class c) {
        return ComponentScope.PerRequest;
    }
    
    @Override
    public ResourceComponentProvider getComponentProvider(final Class c) {
        return new PerRequest();
    }
    
    @Override
    public ResourceComponentProvider getComponentProvider(final IoCComponentProvider icp, final Class c) {
        if (icp instanceof IoCInstantiatedComponentProvider) {
            return new PerRequestInstantiated((IoCInstantiatedComponentProvider)icp);
        }
        if (icp instanceof IoCProxiedComponentProvider) {
            return new PerRequestProxied((IoCProxiedComponentProvider)icp);
        }
        throw new IllegalStateException();
    }
    
    static {
        LOGGER = Logger.getLogger(PerRequestFactory.class.getName());
    }
    
    private abstract class AbstractPerRequest implements ResourceComponentProvider
    {
        private ResourceComponentDestructor rcd;
        
        @Override
        public final Object getInstance() {
            return this.getInstance(PerRequestFactory.this.threadLocalHc);
        }
        
        @Override
        public final ComponentScope getScope() {
            return ComponentScope.PerRequest;
        }
        
        @Override
        public void init(final AbstractResource abstractResource) {
            this.rcd = new ResourceComponentDestructor(abstractResource);
        }
        
        @Override
        public final Object getInstance(final HttpContext hc) {
            Map<AbstractPerRequest, Object> m = hc.getProperties().get("com.sun.jersey.scope.PerRequest");
            if (m == null) {
                m = new HashMap<AbstractPerRequest, Object>();
                hc.getProperties().put("com.sun.jersey.scope.PerRequest", m);
            }
            else {
                final Object o = m.get(this);
                if (o != null) {
                    return o;
                }
            }
            final Object o = this._getInstance(hc);
            m.put(this, o);
            return o;
        }
        
        @Override
        public final void destroy() {
        }
        
        protected abstract Object _getInstance(final HttpContext p0);
        
        public void destroy(final Object o) {
            try {
                this.rcd.destroy(o);
            }
            catch (IllegalAccessException ex) {
                throw new ContainerException("Unable to destroy resource", ex);
            }
            catch (InvocationTargetException ex2) {
                throw new ContainerException("Unable to destroy resource", ex2);
            }
            catch (RuntimeException ex3) {
                throw new ContainerException("Unable to destroy resource", ex3);
            }
        }
    }
    
    private final class PerRequest extends AbstractPerRequest
    {
        private ResourceComponentConstructor rcc;
        
        @Override
        public void init(final AbstractResource abstractResource) {
            super.init(abstractResource);
            this.rcc = new ResourceComponentConstructor(PerRequestFactory.this.sipc, ComponentScope.PerRequest, abstractResource);
        }
        
        @Override
        protected Object _getInstance(final HttpContext hc) {
            try {
                return this.rcc.construct(hc);
            }
            catch (InstantiationException ex) {
                throw new ContainerException("Unable to create resource " + this.rcc.getResourceClass(), ex);
            }
            catch (IllegalAccessException ex2) {
                throw new ContainerException("Unable to create resource " + this.rcc.getResourceClass(), ex2);
            }
            catch (InvocationTargetException ex3) {
                throw new MappableContainerException(ex3.getTargetException());
            }
            catch (WebApplicationException ex4) {
                throw ex4;
            }
            catch (RuntimeException ex5) {
                throw new ContainerException("Unable to create resource " + this.rcc.getResourceClass(), ex5);
            }
        }
    }
    
    private final class PerRequestInstantiated extends AbstractPerRequest
    {
        private final IoCInstantiatedComponentProvider iicp;
        private final IoCDestroyable destroyable;
        private ResourceComponentInjector rci;
        
        PerRequestInstantiated(final IoCInstantiatedComponentProvider iicp) {
            this.iicp = iicp;
            this.destroyable = ((iicp instanceof IoCDestroyable) ? iicp : null);
        }
        
        @Override
        public void init(final AbstractResource abstractResource) {
            super.init(abstractResource);
            if (this.destroyable == null) {
                this.rci = new ResourceComponentInjector(PerRequestFactory.this.sipc, ComponentScope.PerRequest, abstractResource);
            }
        }
        
        public Object _getInstance(final HttpContext hc) {
            final Object o = this.iicp.getInstance();
            if (this.destroyable == null) {
                this.rci.inject(hc, this.iicp.getInjectableInstance(o));
            }
            return o;
        }
        
        @Override
        public void destroy(final Object o) {
            if (this.destroyable != null) {
                this.destroyable.destroy(o);
            }
            else {
                super.destroy(o);
            }
        }
    }
    
    private final class PerRequestProxied extends AbstractPerRequest
    {
        private final IoCProxiedComponentProvider ipcp;
        private ResourceComponentConstructor rcc;
        
        PerRequestProxied(final IoCProxiedComponentProvider ipcp) {
            this.ipcp = ipcp;
        }
        
        @Override
        public void init(final AbstractResource abstractResource) {
            super.init(abstractResource);
            this.rcc = new ResourceComponentConstructor(PerRequestFactory.this.sipc, ComponentScope.PerRequest, abstractResource);
        }
        
        public Object _getInstance(final HttpContext hc) {
            try {
                return this.ipcp.proxy(this.rcc.construct(hc));
            }
            catch (InstantiationException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
            catch (IllegalAccessException ex2) {
                throw new ContainerException("Unable to create resource", ex2);
            }
            catch (InvocationTargetException ex3) {
                throw new MappableContainerException(ex3.getTargetException());
            }
            catch (WebApplicationException ex4) {
                throw ex4;
            }
            catch (RuntimeException ex5) {
                throw new ContainerException("Unable to create resource", ex5);
            }
        }
    }
}
