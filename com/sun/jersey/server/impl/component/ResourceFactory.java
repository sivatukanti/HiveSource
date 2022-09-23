// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.component;

import com.sun.jersey.core.spi.component.ComponentConstructor;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import com.sun.jersey.core.spi.component.ComponentInjector;
import java.lang.annotation.Annotation;
import java.security.PrivilegedActionException;
import com.sun.jersey.api.container.ContainerException;
import java.security.AccessController;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.server.impl.resource.PerRequestFactory;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactoryClass;
import com.sun.jersey.server.spi.component.ResourceComponentProvider;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import java.util.HashMap;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactory;
import java.util.Map;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.api.core.ResourceConfig;

public class ResourceFactory
{
    private final ResourceConfig config;
    private final ServerInjectableProviderContext ipc;
    private final Map<Class, ResourceComponentProviderFactory> factories;
    
    public ResourceFactory(final ResourceConfig config, final ServerInjectableProviderContext ipc) {
        this.config = config;
        this.ipc = ipc;
        this.factories = new HashMap<Class, ResourceComponentProviderFactory>();
    }
    
    public ServerInjectableProviderContext getInjectableProviderContext() {
        return this.ipc;
    }
    
    public ComponentScope getScope(final Class c) {
        return this.getComponentProviderFactory(c).getScope(c);
    }
    
    public ResourceComponentProvider getComponentProvider(final ComponentContext cc, final Class c) {
        return this.getComponentProviderFactory(c).getComponentProvider(c);
    }
    
    protected ResourceComponentProviderFactory getComponentProviderFactory(final Class c) {
        Class<? extends ResourceComponentProviderFactory> providerFactoryClass = null;
        Class<? extends Annotation> scope = null;
        for (final Annotation a : c.getAnnotations()) {
            final Class<? extends Annotation> annotationType = a.annotationType();
            final ResourceComponentProviderFactoryClass rf = annotationType.getAnnotation(ResourceComponentProviderFactoryClass.class);
            if (rf != null && providerFactoryClass == null) {
                providerFactoryClass = rf.value();
                scope = annotationType;
            }
            else if (rf != null && providerFactoryClass != null) {
                Errors.error("Class " + c.getName() + " is annotated with multiple scopes: " + scope.getName() + " and " + annotationType.getName());
            }
        }
        Label_0262: {
            if (providerFactoryClass == null) {
                final Object v = this.config.getProperties().get("com.sun.jersey.config.property.DefaultResourceComponentProviderFactoryClass");
                if (v == null) {
                    providerFactoryClass = PerRequestFactory.class;
                }
                else {
                    if (v instanceof String) {
                        try {
                            providerFactoryClass = this.getSubclass(AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA((String)v)));
                            break Label_0262;
                        }
                        catch (ClassNotFoundException ex) {
                            throw new ContainerException(ex);
                        }
                        catch (PrivilegedActionException pae) {
                            throw new ContainerException(pae.getCause());
                        }
                    }
                    if (!(v instanceof Class)) {
                        throw new IllegalArgumentException("Property value for com.sun.jersey.config.property.DefaultResourceComponentProviderFactoryClass of type Class or String");
                    }
                    providerFactoryClass = this.getSubclass((Class<?>)v);
                }
            }
        }
        ResourceComponentProviderFactory rcpf = this.factories.get(providerFactoryClass);
        if (rcpf == null) {
            rcpf = this.getInstance(providerFactoryClass);
            this.factories.put(providerFactoryClass, rcpf);
        }
        return rcpf;
    }
    
    private Class<? extends ResourceComponentProviderFactory> getSubclass(final Class<?> c) {
        if (ResourceComponentProviderFactory.class.isAssignableFrom(c)) {
            return c.asSubclass(ResourceComponentProviderFactory.class);
        }
        throw new IllegalArgumentException("Property value for com.sun.jersey.config.property.DefaultResourceComponentProviderFactoryClass of type " + c + " not of a subclass of " + ResourceComponentProviderFactory.class);
    }
    
    private ResourceComponentProviderFactory getInstance(final Class<? extends ResourceComponentProviderFactory> providerFactoryClass) {
        try {
            final ComponentInjector<ResourceComponentProviderFactory> ci = new ComponentInjector<ResourceComponentProviderFactory>(this.ipc, (Class<ResourceComponentProviderFactory>)providerFactoryClass);
            final ComponentConstructor<ResourceComponentProviderFactory> cc = new ComponentConstructor<ResourceComponentProviderFactory>(this.ipc, (Class<ResourceComponentProviderFactory>)providerFactoryClass, ci);
            return cc.getInstance();
        }
        catch (Exception ex) {
            throw new ContainerException("Unable to create resource component provider", ex);
        }
    }
}
