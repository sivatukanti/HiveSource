// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.component;

import com.sun.jersey.server.spi.component.ResourceComponentInjector;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCFullyManagedComponentProvider;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCManagedComponentProvider;
import java.util.Iterator;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.server.spi.component.ResourceComponentProvider;
import com.sun.jersey.core.spi.component.ComponentContext;
import java.util.Comparator;
import java.util.Collections;
import com.sun.jersey.core.util.PriorityUtil;
import java.util.Collection;
import java.util.ArrayList;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import java.util.List;

public class IoCResourceFactory extends ResourceFactory
{
    private final List<IoCComponentProviderFactory> factories;
    
    public IoCResourceFactory(final ResourceConfig config, final ServerInjectableProviderContext ipc, final List<IoCComponentProviderFactory> factories) {
        super(config, ipc);
        final List<IoCComponentProviderFactory> myFactories = new ArrayList<IoCComponentProviderFactory>(factories);
        Collections.sort(myFactories, PriorityUtil.INSTANCE_COMPARATOR);
        this.factories = Collections.unmodifiableList((List<? extends IoCComponentProviderFactory>)myFactories);
    }
    
    @Override
    public ResourceComponentProvider getComponentProvider(final ComponentContext cc, final Class c) {
        IoCComponentProvider icp = null;
        for (final IoCComponentProviderFactory f : this.factories) {
            icp = f.getComponentProvider(cc, c);
            if (icp != null) {
                break;
            }
        }
        return (icp == null) ? super.getComponentProvider(cc, c) : this.wrap(c, icp);
    }
    
    private ResourceComponentProvider wrap(final Class c, final IoCComponentProvider icp) {
        if (icp instanceof IoCManagedComponentProvider) {
            final IoCManagedComponentProvider imcp = (IoCManagedComponentProvider)icp;
            if (imcp.getScope() == ComponentScope.PerRequest) {
                return new PerRequestWrapper(this.getInjectableProviderContext(), imcp);
            }
            if (imcp.getScope() == ComponentScope.Singleton) {
                return new SingletonWrapper(this.getInjectableProviderContext(), imcp);
            }
            return new UndefinedWrapper(this.getInjectableProviderContext(), imcp);
        }
        else {
            if (icp instanceof IoCFullyManagedComponentProvider) {
                final IoCFullyManagedComponentProvider ifmcp = (IoCFullyManagedComponentProvider)icp;
                return new FullyManagedWrapper(ifmcp);
            }
            final ResourceComponentProviderFactory rcpf = this.getComponentProviderFactory(c);
            return rcpf.getComponentProvider(icp, c);
        }
    }
    
    private static class FullyManagedWrapper implements ResourceComponentProvider
    {
        private final IoCFullyManagedComponentProvider ifmcp;
        
        FullyManagedWrapper(final IoCFullyManagedComponentProvider ifmcp) {
            this.ifmcp = ifmcp;
        }
        
        @Override
        public void init(final AbstractResource abstractResource) {
        }
        
        @Override
        public ComponentScope getScope() {
            return this.ifmcp.getScope();
        }
        
        @Override
        public Object getInstance(final HttpContext hc) {
            return this.ifmcp.getInstance();
        }
        
        @Override
        public Object getInstance() {
            throw new IllegalStateException();
        }
        
        @Override
        public void destroy() {
        }
    }
    
    private static class PerRequestWrapper implements ResourceComponentProvider
    {
        private final ServerInjectableProviderContext ipc;
        private final IoCManagedComponentProvider imcp;
        private ResourceComponentInjector rci;
        
        PerRequestWrapper(final ServerInjectableProviderContext ipc, final IoCManagedComponentProvider imcp) {
            this.ipc = ipc;
            this.imcp = imcp;
        }
        
        @Override
        public void init(final AbstractResource abstractResource) {
            this.rci = new ResourceComponentInjector(this.ipc, ComponentScope.PerRequest, abstractResource);
        }
        
        @Override
        public ComponentScope getScope() {
            return ComponentScope.PerRequest;
        }
        
        @Override
        public Object getInstance(final HttpContext hc) {
            final Object o = this.imcp.getInstance();
            this.rci.inject(hc, this.imcp.getInjectableInstance(o));
            return o;
        }
        
        @Override
        public Object getInstance() {
            throw new IllegalStateException();
        }
        
        @Override
        public void destroy() {
        }
    }
    
    private static class SingletonWrapper implements ResourceComponentProvider
    {
        private final ServerInjectableProviderContext ipc;
        private final IoCManagedComponentProvider imcp;
        private Object o;
        
        SingletonWrapper(final ServerInjectableProviderContext ipc, final IoCManagedComponentProvider imcp) {
            this.ipc = ipc;
            this.imcp = imcp;
        }
        
        @Override
        public void init(final AbstractResource abstractResource) {
            final ResourceComponentInjector rci = new ResourceComponentInjector(this.ipc, ComponentScope.Singleton, abstractResource);
            this.o = this.imcp.getInstance();
            rci.inject(null, this.imcp.getInjectableInstance(this.o));
        }
        
        @Override
        public ComponentScope getScope() {
            return ComponentScope.Singleton;
        }
        
        @Override
        public Object getInstance(final HttpContext hc) {
            return this.o;
        }
        
        @Override
        public Object getInstance() {
            throw new IllegalStateException();
        }
        
        @Override
        public void destroy() {
        }
    }
    
    private static class UndefinedWrapper implements ResourceComponentProvider
    {
        private final ServerInjectableProviderContext ipc;
        private final IoCManagedComponentProvider imcp;
        private ResourceComponentInjector rci;
        
        UndefinedWrapper(final ServerInjectableProviderContext ipc, final IoCManagedComponentProvider imcp) {
            this.ipc = ipc;
            this.imcp = imcp;
        }
        
        @Override
        public void init(final AbstractResource abstractResource) {
            this.rci = new ResourceComponentInjector(this.ipc, ComponentScope.Undefined, abstractResource);
        }
        
        @Override
        public ComponentScope getScope() {
            return ComponentScope.Undefined;
        }
        
        @Override
        public Object getInstance(final HttpContext hc) {
            final Object o = this.imcp.getInstance();
            this.rci.inject(hc, this.imcp.getInjectableInstance(o));
            return o;
        }
        
        @Override
        public Object getInstance() {
            throw new IllegalStateException();
        }
        
        @Override
        public void destroy() {
        }
    }
}
