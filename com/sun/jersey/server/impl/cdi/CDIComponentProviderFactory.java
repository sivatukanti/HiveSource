// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.core.spi.component.ioc.IoCDestroyable;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.core.spi.component.ComponentProvider;
import java.util.Collections;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import javax.annotation.ManagedBean;
import javax.enterprise.context.spi.CreationalContext;
import java.lang.reflect.Type;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.Bean;
import com.sun.jersey.core.spi.component.ioc.IoCFullyManagedComponentProvider;
import javax.enterprise.context.Dependent;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentScope;
import java.lang.annotation.Annotation;
import java.util.Map;
import javax.enterprise.inject.spi.BeanManager;
import java.util.logging.Logger;
import com.sun.jersey.spi.container.WebApplicationListener;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

public class CDIComponentProviderFactory implements IoCComponentProviderFactory, WebApplicationListener
{
    private static final Logger LOGGER;
    private final BeanManager beanManager;
    private final CDIExtension extension;
    private final Map<Class<? extends Annotation>, ComponentScope> scopeMap;
    
    public CDIComponentProviderFactory(final Object bm, final ResourceConfig rc, final WebApplication wa) {
        this.scopeMap = this.createScopeMap();
        this.beanManager = (BeanManager)bm;
        if (CDIExtension.lookupExtensionInBeanManager) {
            this.extension = Utils.getInstance(this.beanManager, CDIExtension.class);
        }
        else {
            this.extension = CDIExtension.getInitializedExtension();
        }
        this.extension.setWebApplication(wa);
        this.extension.setResourceConfig(rc);
    }
    
    @Override
    public void onWebApplicationReady() {
        this.extension.lateInitialize();
    }
    
    @Override
    public IoCComponentProvider getComponentProvider(final Class<?> c) {
        return this.getComponentProvider(null, c);
    }
    
    @Override
    public IoCComponentProvider getComponentProvider(final ComponentContext cc, final Class<?> c) {
        final Bean<?> b = Utils.getBean(this.beanManager, c);
        if (b == null) {
            return null;
        }
        final Class<? extends Annotation> s = (Class<? extends Annotation>)b.getScope();
        final ComponentScope cs = this.getComponentScope(b);
        if (s != Dependent.class) {
            CDIComponentProviderFactory.LOGGER.fine("Binding the CDI managed bean " + c.getName() + " in scope " + s.getName() + " to CDIComponentProviderFactory in scope " + cs);
            return new IoCFullyManagedComponentProvider() {
                @Override
                public ComponentScope getScope() {
                    return cs;
                }
                
                @Override
                public Object getInstance() {
                    final CreationalContext<?> bcc = (CreationalContext<?>)CDIComponentProviderFactory.this.beanManager.createCreationalContext((Contextual)b);
                    return c.cast(CDIComponentProviderFactory.this.beanManager.getReference(b, (Type)c, (CreationalContext)bcc));
                }
            };
        }
        if (!this.extension.getResourceConfig().getFeature("com.sun.jersey.config.feature.AllowRawManagedBeans") && !c.isAnnotationPresent((Class<? extends Annotation>)ManagedBean.class)) {
            return null;
        }
        CDIComponentProviderFactory.LOGGER.fine("Binding the CDI managed bean " + c.getName() + " in scope " + s.getName() + " to CDIComponentProviderFactory");
        return new ComponentProviderDestroyable() {
            @Override
            public Object getInjectableInstance(final Object o) {
                return o;
            }
            
            @Override
            public Object getInstance() {
                final CreationalContext<?> bcc = (CreationalContext<?>)CDIComponentProviderFactory.this.beanManager.createCreationalContext((Contextual)b);
                return c.cast(CDIComponentProviderFactory.this.beanManager.getReference(b, (Type)c, (CreationalContext)bcc));
            }
            
            @Override
            public void destroy(final Object o) {
                final CreationalContext cc = CDIComponentProviderFactory.this.beanManager.createCreationalContext((Contextual)b);
                b.destroy(o, cc);
            }
        };
    }
    
    private ComponentScope getComponentScope(final Bean<?> b) {
        final ComponentScope cs = this.scopeMap.get(b.getScope());
        return (cs != null) ? cs : ComponentScope.Undefined;
    }
    
    private Map<Class<? extends Annotation>, ComponentScope> createScopeMap() {
        final Map<Class<? extends Annotation>, ComponentScope> m = new HashMap<Class<? extends Annotation>, ComponentScope>();
        m.put((Class<? extends Annotation>)ApplicationScoped.class, ComponentScope.Singleton);
        m.put((Class<? extends Annotation>)RequestScoped.class, ComponentScope.PerRequest);
        m.put((Class<? extends Annotation>)Dependent.class, ComponentScope.PerRequest);
        return Collections.unmodifiableMap((Map<? extends Class<? extends Annotation>, ? extends ComponentScope>)m);
    }
    
    static {
        LOGGER = Logger.getLogger(CDIComponentProviderFactory.class.getName());
    }
    
    private interface ComponentProviderDestroyable extends IoCInstantiatedComponentProvider, IoCDestroyable
    {
    }
}
