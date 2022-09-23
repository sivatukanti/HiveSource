// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.managedbeans;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.spi.component.ioc.IoCDestroyable;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.core.spi.component.ComponentProvider;
import java.lang.annotation.Annotation;
import javax.annotation.ManagedBean;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

final class ManagedBeanComponentProviderFactory implements IoCComponentProviderFactory
{
    private static final Logger LOGGER;
    private final Object injectionMgr;
    private final Method createManagedObjectMethod;
    private final Method destroyManagedObjectMethod;
    
    public ManagedBeanComponentProviderFactory(final Object injectionMgr, final Method createManagedObjectMethod, final Method destroyManagedObjectMethod) {
        this.injectionMgr = injectionMgr;
        this.createManagedObjectMethod = createManagedObjectMethod;
        this.destroyManagedObjectMethod = destroyManagedObjectMethod;
    }
    
    @Override
    public IoCComponentProvider getComponentProvider(final Class<?> c) {
        return this.getComponentProvider(null, c);
    }
    
    @Override
    public IoCComponentProvider getComponentProvider(final ComponentContext cc, final Class<?> c) {
        if (!this.isManagedBean(c)) {
            return null;
        }
        ManagedBeanComponentProviderFactory.LOGGER.info("Binding the Managed bean class " + c.getName() + " to ManagedBeanComponentProvider");
        return new ManagedBeanComponentProvider(c);
    }
    
    private boolean isManagedBean(final Class<?> c) {
        return c.isAnnotationPresent((Class<? extends Annotation>)ManagedBean.class);
    }
    
    static {
        LOGGER = Logger.getLogger(ManagedBeanComponentProviderFactory.class.getName());
    }
    
    private class ManagedBeanComponentProvider implements IoCInstantiatedComponentProvider, IoCDestroyable
    {
        private final Class<?> c;
        
        ManagedBeanComponentProvider(final Class<?> c) {
            this.c = c;
        }
        
        @Override
        public Object getInstance() {
            try {
                return ManagedBeanComponentProviderFactory.this.createManagedObjectMethod.invoke(ManagedBeanComponentProviderFactory.this.injectionMgr, this.c);
            }
            catch (Exception ex) {
                throw new ContainerException(ex);
            }
        }
        
        @Override
        public Object getInjectableInstance(final Object o) {
            return o;
        }
        
        @Override
        public void destroy(final Object o) {
            try {
                ManagedBeanComponentProviderFactory.this.destroyManagedObjectMethod.invoke(ManagedBeanComponentProviderFactory.this.injectionMgr, o);
            }
            catch (Exception ex) {
                throw new ContainerException(ex);
            }
        }
    }
}
