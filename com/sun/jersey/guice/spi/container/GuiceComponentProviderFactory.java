// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.guice.spi.container;

import com.sun.jersey.core.spi.component.ioc.IoCManagedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCProxiedComponentProvider;
import com.sun.jersey.core.spi.component.ComponentProvider;
import com.google.inject.Scopes;
import java.util.HashMap;
import java.lang.annotation.Annotation;
import com.google.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.AnnotatedElement;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.ConfigurationException;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.logging.Level;
import com.google.inject.Key;
import com.sun.jersey.api.core.ResourceConfig;
import com.google.inject.Injector;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.google.inject.Scope;
import java.util.Map;
import java.util.logging.Logger;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

public class GuiceComponentProviderFactory implements IoCComponentProviderFactory
{
    private static final Logger LOGGER;
    private final Map<Scope, ComponentScope> scopeMap;
    private final Injector injector;
    
    public GuiceComponentProviderFactory(final ResourceConfig config, final Injector injector) {
        this.scopeMap = this.createScopeMap();
        this.register(config, this.injector = injector);
    }
    
    private void register(final ResourceConfig config, Injector injector) {
        while (injector != null) {
            for (final Key<?> key : injector.getBindings().keySet()) {
                final Type type = key.getTypeLiteral().getType();
                if (type instanceof Class) {
                    final Class<?> c = (Class<?>)type;
                    if (ResourceConfig.isProviderClass(c)) {
                        GuiceComponentProviderFactory.LOGGER.log(Level.INFO, "Registering {0} as a provider class", c.getName());
                        config.getClasses().add(c);
                    }
                    else {
                        if (!ResourceConfig.isRootResourceClass(c)) {
                            continue;
                        }
                        GuiceComponentProviderFactory.LOGGER.log(Level.INFO, "Registering {0} as a root resource class", c.getName());
                        config.getClasses().add(c);
                    }
                }
            }
            injector = injector.getParent();
        }
    }
    
    @Override
    public IoCComponentProvider getComponentProvider(final Class<?> c) {
        return this.getComponentProvider(null, c);
    }
    
    @Override
    public IoCComponentProvider getComponentProvider(final ComponentContext cc, final Class<?> clazz) {
        if (GuiceComponentProviderFactory.LOGGER.isLoggable(Level.FINE)) {
            GuiceComponentProviderFactory.LOGGER.log(Level.FINE, "getComponentProvider({0})", clazz.getName());
        }
        final Key<?> key = Key.get(clazz);
        final Injector i = this.findInjector(key);
        Label_0171: {
            if (i == null) {
                if (this.isGuiceConstructorInjected(clazz)) {
                    try {
                        if (this.injector.getBinding(key) != null) {
                            GuiceComponentProviderFactory.LOGGER.log(Level.INFO, "Binding {0} to GuiceInstantiatedComponentProvider", clazz.getName());
                            return new GuiceInstantiatedComponentProvider(this.injector, clazz);
                        }
                        break Label_0171;
                    }
                    catch (ConfigurationException e) {
                        GuiceComponentProviderFactory.LOGGER.log(Level.SEVERE, "Cannot bind " + clazz.getName(), e);
                        throw e;
                    }
                }
                if (this.isGuiceFieldOrMethodInjected(clazz)) {
                    GuiceComponentProviderFactory.LOGGER.log(Level.INFO, "Binding {0} to GuiceInjectedComponentProvider", clazz.getName());
                    return new GuiceInjectedComponentProvider(this.injector);
                }
                return null;
            }
        }
        final ComponentScope componentScope = this.getComponentScope(key, i);
        GuiceComponentProviderFactory.LOGGER.log(Level.INFO, "Binding {0} to GuiceManagedComponentProvider with the scope \"{1}\"", new Object[] { clazz.getName(), componentScope });
        return new GuiceManagedComponentProvider(i, componentScope, clazz);
    }
    
    private ComponentScope getComponentScope(final Key<?> key, final Injector i) {
        return i.getBinding(key).acceptScopingVisitor((BindingScopingVisitor<ComponentScope>)new BindingScopingVisitor<ComponentScope>() {
            @Override
            public ComponentScope visitEagerSingleton() {
                return ComponentScope.Singleton;
            }
            
            @Override
            public ComponentScope visitScope(final Scope theScope) {
                final ComponentScope cs = GuiceComponentProviderFactory.this.scopeMap.get(theScope);
                return (cs != null) ? cs : ComponentScope.Undefined;
            }
            
            @Override
            public ComponentScope visitScopeAnnotation(final Class scopeAnnotation) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public ComponentScope visitNoScoping() {
                return ComponentScope.PerRequest;
            }
        });
    }
    
    private Injector findInjector(final Key<?> key) {
        for (Injector i = this.injector; i != null; i = i.getParent()) {
            if (i.getBindings().containsKey(key)) {
                return i;
            }
        }
        return null;
    }
    
    @Deprecated
    public boolean isImplicitGuiceComponent(final Class<?> c) {
        return this.isGuiceConstructorInjected(c);
    }
    
    public boolean isGuiceConstructorInjected(final Class<?> c) {
        for (final Constructor<?> con : c.getConstructors()) {
            if (isInjectable(con)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isGuiceFieldOrMethodInjected(final Class<?> c) {
        for (final Method m : c.getDeclaredMethods()) {
            if (isInjectable(m)) {
                return true;
            }
        }
        for (final Field f : c.getDeclaredFields()) {
            if (isInjectable(f)) {
                return true;
            }
        }
        return !c.equals(Object.class) && this.isGuiceFieldOrMethodInjected(c.getSuperclass());
    }
    
    private static boolean isInjectable(final AnnotatedElement element) {
        return element.isAnnotationPresent(Inject.class) || element.isAnnotationPresent(javax.inject.Inject.class);
    }
    
    public Map<Scope, ComponentScope> createScopeMap() {
        final Map<Scope, ComponentScope> result = new HashMap<Scope, ComponentScope>();
        result.put(Scopes.SINGLETON, ComponentScope.Singleton);
        result.put(Scopes.NO_SCOPE, ComponentScope.PerRequest);
        return result;
    }
    
    static {
        LOGGER = Logger.getLogger(GuiceComponentProviderFactory.class.getName());
    }
    
    private static class GuiceInjectedComponentProvider implements IoCProxiedComponentProvider
    {
        private final Injector injector;
        
        public GuiceInjectedComponentProvider(final Injector injector) {
            this.injector = injector;
        }
        
        @Override
        public Object getInstance() {
            throw new IllegalStateException();
        }
        
        @Override
        public Object proxy(final Object o) {
            this.injector.injectMembers(o);
            return o;
        }
    }
    
    private static class GuiceInstantiatedComponentProvider implements IoCInstantiatedComponentProvider
    {
        private final Injector injector;
        private final Class<?> clazz;
        
        public GuiceInstantiatedComponentProvider(final Injector injector, final Class<?> clazz) {
            this.injector = injector;
            this.clazz = clazz;
        }
        
        public Class<?> getInjectableClass(final Class<?> c) {
            return c.getSuperclass();
        }
        
        @Override
        public Object getInjectableInstance(final Object o) {
            return o;
        }
        
        @Override
        public Object getInstance() {
            return this.injector.getInstance(this.clazz);
        }
    }
    
    private static class GuiceManagedComponentProvider extends GuiceInstantiatedComponentProvider implements IoCManagedComponentProvider
    {
        private final ComponentScope scope;
        
        public GuiceManagedComponentProvider(final Injector injector, final ComponentScope scope, final Class<?> clazz) {
            super(injector, clazz);
            this.scope = scope;
        }
        
        @Override
        public ComponentScope getScope() {
            return this.scope;
        }
    }
}
