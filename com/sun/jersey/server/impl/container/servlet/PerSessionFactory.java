// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.servlet;

import com.sun.jersey.server.spi.component.ResourceComponentInjector;
import com.sun.jersey.core.spi.component.ioc.IoCDestroyable;
import javax.ws.rs.WebApplicationException;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.server.spi.component.ResourceComponentConstructor;
import java.lang.reflect.InvocationTargetException;
import com.sun.jersey.api.container.ContainerException;
import javax.servlet.http.HttpSession;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.server.spi.component.ResourceComponentDestructor;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.HashMap;
import com.sun.jersey.core.spi.component.ComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCProxiedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.server.spi.component.ResourceComponentProvider;
import com.sun.jersey.core.spi.component.ComponentScope;
import javax.ws.rs.core.Context;
import java.util.concurrent.ConcurrentHashMap;
import com.sun.jersey.api.core.HttpContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactory;

public final class PerSessionFactory implements ResourceComponentProviderFactory
{
    private final ServerInjectableProviderContext sipc;
    private final ServletContext sc;
    private final HttpServletRequest hsr;
    private final HttpContext threadLocalHc;
    private final String abstractPerSessionMapPropertyName;
    private final ConcurrentHashMap<Class, AbstractPerSession> abstractPerSessionMap;
    
    public PerSessionFactory(@Context final ServerInjectableProviderContext sipc, @Context final ServletContext sc, @Context final HttpServletRequest hsr, @Context final HttpContext threadLocalHc) {
        this.abstractPerSessionMap = new ConcurrentHashMap<Class, AbstractPerSession>();
        this.hsr = hsr;
        this.sc = sc;
        this.sipc = sipc;
        this.threadLocalHc = threadLocalHc;
        sc.setAttribute(this.abstractPerSessionMapPropertyName = this.toString(), this.abstractPerSessionMap);
    }
    
    @Override
    public ComponentScope getScope(final Class c) {
        return ComponentScope.Undefined;
    }
    
    @Override
    public ResourceComponentProvider getComponentProvider(final Class c) {
        return new PerSesson();
    }
    
    @Override
    public ResourceComponentProvider getComponentProvider(final IoCComponentProvider icp, final Class c) {
        if (icp instanceof IoCInstantiatedComponentProvider) {
            return new PerSessonInstantiated((IoCInstantiatedComponentProvider)icp);
        }
        if (icp instanceof IoCProxiedComponentProvider) {
            return new PerSessonProxied((IoCProxiedComponentProvider)icp);
        }
        throw new IllegalStateException();
    }
    
    private static class SessionMap extends HashMap<String, Object> implements HttpSessionBindingListener
    {
        private final String abstractPerSessionMapPropertyName;
        
        SessionMap(final String abstractPerSessionMapPropertyName) {
            this.abstractPerSessionMapPropertyName = abstractPerSessionMapPropertyName;
        }
        
        @Override
        public void valueBound(final HttpSessionBindingEvent hsbe) {
        }
        
        @Override
        public void valueUnbound(final HttpSessionBindingEvent hsbe) {
            final ServletContext sc = hsbe.getSession().getServletContext();
            final Map<Class, AbstractPerSession> abstractPerSessionMap = (Map<Class, AbstractPerSession>)sc.getAttribute(this.abstractPerSessionMapPropertyName);
            for (final Object o : ((HashMap<K, Object>)this).values()) {
                final AbstractPerSession aps = abstractPerSessionMap.get(o.getClass());
                if (aps != null) {
                    aps.destroy(o);
                }
            }
        }
    }
    
    private abstract class AbstractPerSession implements ResourceComponentProvider
    {
        private static final String SCOPE_PER_SESSION = "com.sun.jersey.scope.PerSession";
        private ResourceComponentDestructor rcd;
        private Class c;
        
        @Override
        public void init(final AbstractResource abstractResource) {
            this.rcd = new ResourceComponentDestructor(abstractResource);
            this.c = abstractResource.getResourceClass();
        }
        
        @Override
        public final Object getInstance() {
            return this.getInstance(PerSessionFactory.this.threadLocalHc);
        }
        
        @Override
        public final ComponentScope getScope() {
            return ComponentScope.Undefined;
        }
        
        @Override
        public final Object getInstance(final HttpContext hc) {
            final HttpSession hs = PerSessionFactory.this.hsr.getSession();
            synchronized (hs) {
                SessionMap sm = (SessionMap)hs.getAttribute("com.sun.jersey.scope.PerSession");
                if (sm == null) {
                    sm = new SessionMap(PerSessionFactory.this.abstractPerSessionMapPropertyName);
                    hs.setAttribute("com.sun.jersey.scope.PerSession", sm);
                }
                PerSessionFactory.this.abstractPerSessionMap.putIfAbsent(this.c, this);
                Object o = ((HashMap<K, Object>)sm).get(this.c.getName());
                if (o != null) {
                    return o;
                }
                o = this._getInstance(hc);
                sm.put(this.c.getName(), o);
                return o;
            }
        }
        
        protected abstract Object _getInstance(final HttpContext p0);
        
        @Override
        public final void destroy() {
        }
        
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
    
    private final class PerSesson extends AbstractPerSession
    {
        private ResourceComponentConstructor rcc;
        
        @Override
        public void init(final AbstractResource abstractResource) {
            super.init(abstractResource);
            this.rcc = new ResourceComponentConstructor(PerSessionFactory.this.sipc, ComponentScope.Undefined, abstractResource);
        }
        
        @Override
        protected Object _getInstance(final HttpContext hc) {
            try {
                return this.rcc.construct(hc);
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
    
    private final class PerSessonInstantiated extends AbstractPerSession
    {
        private final IoCInstantiatedComponentProvider iicp;
        private final IoCDestroyable destroyable;
        private ResourceComponentInjector rci;
        
        PerSessonInstantiated(final IoCInstantiatedComponentProvider iicp) {
            this.iicp = iicp;
            this.destroyable = ((iicp instanceof IoCDestroyable) ? iicp : null);
        }
        
        @Override
        public void init(final AbstractResource abstractResource) {
            super.init(abstractResource);
            if (this.destroyable == null) {
                this.rci = new ResourceComponentInjector(PerSessionFactory.this.sipc, ComponentScope.Undefined, abstractResource);
            }
        }
        
        @Override
        protected Object _getInstance(final HttpContext hc) {
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
    
    private final class PerSessonProxied extends AbstractPerSession
    {
        private final IoCProxiedComponentProvider ipcp;
        private ResourceComponentConstructor rcc;
        
        PerSessonProxied(final IoCProxiedComponentProvider ipcp) {
            this.ipcp = ipcp;
        }
        
        @Override
        public void init(final AbstractResource abstractResource) {
            super.init(abstractResource);
            this.rcc = new ResourceComponentConstructor(PerSessionFactory.this.sipc, ComponentScope.Undefined, abstractResource);
        }
        
        @Override
        protected Object _getInstance(final HttpContext hc) {
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
