// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component.ioc;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.core.spi.component.ComponentDestructor;
import java.util.logging.Logger;
import com.sun.jersey.core.spi.component.ComponentScope;
import java.util.Iterator;
import com.sun.jersey.core.spi.component.ComponentProvider;
import java.util.Comparator;
import com.sun.jersey.core.util.PriorityUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.util.List;
import com.sun.jersey.core.spi.component.ProviderFactory;

public class IoCProviderFactory extends ProviderFactory
{
    private final List<IoCComponentProviderFactory> factories;
    
    public IoCProviderFactory(final InjectableProviderContext ipc, final IoCComponentProviderFactory icpf) {
        this(ipc, Collections.singletonList(icpf));
    }
    
    public IoCProviderFactory(final InjectableProviderContext ipc, final List<IoCComponentProviderFactory> factories) {
        super(ipc);
        final List<IoCComponentProviderFactory> myFactories = new ArrayList<IoCComponentProviderFactory>(factories);
        Collections.sort(myFactories, PriorityUtil.INSTANCE_COMPARATOR);
        this.factories = Collections.unmodifiableList((List<? extends IoCComponentProviderFactory>)myFactories);
    }
    
    public ComponentProvider _getComponentProvider(final Class c) {
        IoCComponentProvider icp = null;
        for (final IoCComponentProviderFactory f : this.factories) {
            icp = f.getComponentProvider((Class<?>)c);
            if (icp != null) {
                break;
            }
        }
        return (icp == null) ? super._getComponentProvider(c) : this.wrap(c, icp);
    }
    
    private ComponentProvider wrap(final Class c, final IoCComponentProvider icp) {
        if (icp instanceof IoCManagedComponentProvider) {
            final IoCManagedComponentProvider imcp = (IoCManagedComponentProvider)icp;
            if (imcp.getScope() == ComponentScope.Singleton) {
                return new ManagedSingleton(this.getInjectableProviderContext(), imcp, c);
            }
            throw new RuntimeException("The scope of the component " + c + " must be a singleton");
        }
        else {
            if (icp instanceof IoCFullyManagedComponentProvider) {
                final IoCFullyManagedComponentProvider ifmcp = (IoCFullyManagedComponentProvider)icp;
                return new FullyManagedSingleton(ifmcp.getInstance());
            }
            if (icp instanceof IoCInstantiatedComponentProvider) {
                final IoCInstantiatedComponentProvider iicp = (IoCInstantiatedComponentProvider)icp;
                return new InstantiatedSingleton(this.getInjectableProviderContext(), iicp, c);
            }
            if (!(icp instanceof IoCProxiedComponentProvider)) {
                throw new UnsupportedOperationException();
            }
            final IoCProxiedComponentProvider ipcp = (IoCProxiedComponentProvider)icp;
            final ComponentProvider cp = super._getComponentProvider(c);
            if (cp == null) {
                return null;
            }
            return new ProxiedSingletonWrapper(ipcp, cp, c);
        }
    }
    
    private static class InstantiatedSingleton implements ComponentProvider, Destroyable
    {
        private final Object o;
        private final IoCDestroyable destroyable;
        private final ComponentDestructor cd;
        
        InstantiatedSingleton(final InjectableProviderContext ipc, final IoCInstantiatedComponentProvider iicp, final Class c) {
            this.destroyable = ((iicp instanceof IoCDestroyable) ? iicp : null);
            this.o = iicp.getInstance();
            this.cd = ((this.destroyable == null) ? new ComponentDestructor(c) : null);
            if (this.destroyable == null) {
                final ComponentInjector ci = new ComponentInjector(ipc, c);
                ci.inject(iicp.getInjectableInstance(this.o));
            }
        }
        
        @Override
        public Object getInstance() {
            return this.o;
        }
        
        @Override
        public void destroy() {
            if (this.destroyable != null) {
                this.destroyable.destroy(this.o);
            }
            else {
                try {
                    this.cd.destroy(this.o);
                }
                catch (IllegalAccessException ex) {
                    IoCProviderFactory.LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex);
                }
                catch (IllegalArgumentException ex2) {
                    IoCProviderFactory.LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex2);
                }
                catch (InvocationTargetException ex3) {
                    IoCProviderFactory.LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex3);
                }
            }
        }
    }
    
    private static class ManagedSingleton implements ComponentProvider
    {
        private final Object o;
        
        ManagedSingleton(final InjectableProviderContext ipc, final IoCInstantiatedComponentProvider iicp, final Class c) {
            final ComponentInjector rci = new ComponentInjector(ipc, c);
            this.o = iicp.getInstance();
            rci.inject(iicp.getInjectableInstance(this.o));
        }
        
        @Override
        public Object getInstance() {
            return this.o;
        }
    }
    
    private static class FullyManagedSingleton implements ComponentProvider
    {
        private final Object o;
        
        FullyManagedSingleton(final Object o) {
            this.o = o;
        }
        
        @Override
        public Object getInstance() {
            return this.o;
        }
    }
    
    private static class ProxiedSingletonWrapper implements ComponentProvider, Destroyable
    {
        private final Destroyable destroyable;
        private final Object proxy;
        
        ProxiedSingletonWrapper(final IoCProxiedComponentProvider ipcp, final ComponentProvider cp, final Class c) {
            this.destroyable = ((cp instanceof Destroyable) ? cp : null);
            final Object o = cp.getInstance();
            this.proxy = ipcp.proxy(o);
            if (!this.proxy.getClass().isAssignableFrom(o.getClass())) {
                throw new IllegalStateException("Proxied object class " + this.proxy.getClass() + " is not assignable from object class " + o.getClass());
            }
        }
        
        @Override
        public Object getInstance() {
            return this.proxy;
        }
        
        @Override
        public void destroy() {
            if (this.destroyable != null) {
                this.destroyable.destroy();
            }
        }
    }
}
