// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.ejb;

import com.sun.jersey.core.spi.component.ComponentScope;
import javax.ws.rs.ext.Provider;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.interceptor.InvocationContext;
import java.util.concurrent.ConcurrentHashMap;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessor;
import java.util.concurrent.ConcurrentMap;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactory;

final class EJBInjectionInterceptor
{
    private IoCComponentProcessorFactory cpf;
    private final ConcurrentMap<Class, IoCComponentProcessor> componentProcessorMap;
    private static final IoCComponentProcessor NULL_COMPONENT_PROCESSOR;
    
    EJBInjectionInterceptor() {
        this.componentProcessorMap = new ConcurrentHashMap<Class, IoCComponentProcessor>();
    }
    
    public void setFactory(final IoCComponentProcessorFactory cpf) {
        this.cpf = cpf;
    }
    
    @PostConstruct
    private void init(final InvocationContext context) throws Exception {
        if (this.cpf == null) {
            return;
        }
        final Object beanInstance = context.getTarget();
        final IoCComponentProcessor icp = this.get(beanInstance.getClass());
        if (icp != null) {
            icp.postConstruct(beanInstance);
        }
        context.proceed();
    }
    
    private IoCComponentProcessor get(final Class c) {
        IoCComponentProcessor cp = this.componentProcessorMap.get(c);
        if (cp != null) {
            return (cp == EJBInjectionInterceptor.NULL_COMPONENT_PROCESSOR) ? null : cp;
        }
        synchronized (this.componentProcessorMap) {
            cp = this.componentProcessorMap.get(c);
            if (cp != null) {
                return (cp == EJBInjectionInterceptor.NULL_COMPONENT_PROCESSOR) ? null : cp;
            }
            final ComponentScope cs = c.isAnnotationPresent(ManagedBean.class) ? (c.isAnnotationPresent(Provider.class) ? ComponentScope.Singleton : this.cpf.getScope(c)) : ComponentScope.Singleton;
            cp = this.cpf.get(c, cs);
            if (cp != null) {
                this.componentProcessorMap.put(c, cp);
            }
            else {
                this.componentProcessorMap.put(c, EJBInjectionInterceptor.NULL_COMPONENT_PROCESSOR);
            }
        }
        return cp;
    }
    
    static {
        NULL_COMPONENT_PROCESSOR = new IoCComponentProcessor() {
            @Override
            public void preConstruct() {
            }
            
            @Override
            public void postConstruct(final Object o) {
            }
        };
    }
}
