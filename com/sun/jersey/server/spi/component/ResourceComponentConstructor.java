// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.spi.component;

import java.util.Collections;
import java.util.SortedSet;
import java.lang.reflect.AccessibleObject;
import com.sun.jersey.api.model.AbstractResourceConstructor;
import java.util.Comparator;
import java.util.TreeSet;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import com.sun.jersey.api.core.HttpContext;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import javax.ws.rs.WebApplicationException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import com.sun.jersey.spi.inject.Injectable;
import java.util.Collection;
import com.sun.jersey.spi.inject.Errors;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import java.lang.reflect.Method;
import java.util.List;
import java.lang.reflect.Constructor;

public class ResourceComponentConstructor
{
    private final Class clazz;
    private final ResourceComponentInjector resourceComponentInjector;
    private final Constructor constructor;
    private final Constructor nonPublicConstructor;
    private final List<Method> postConstructs;
    private final List<AbstractHttpContextInjectable> injectables;
    
    public ResourceComponentConstructor(final ServerInjectableProviderContext serverInjectableProviderCtx, final ComponentScope scope, final AbstractResource abstractResource) {
        this.postConstructs = new ArrayList<Method>();
        this.clazz = abstractResource.getResourceClass();
        final int modifiers = this.clazz.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            Errors.nonPublicClass(this.clazz);
        }
        if (Modifier.isAbstract(modifiers)) {
            if (Modifier.isInterface(modifiers)) {
                Errors.interfaceClass(this.clazz);
            }
            else {
                Errors.abstractClass(this.clazz);
            }
        }
        if (this.clazz.getEnclosingClass() != null && !Modifier.isStatic(modifiers)) {
            Errors.innerClass(this.clazz);
        }
        if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && this.clazz.getConstructors().length == 0) {
            Errors.nonPublicConstructor(this.clazz);
        }
        this.resourceComponentInjector = new ResourceComponentInjector(serverInjectableProviderCtx, scope, abstractResource);
        this.postConstructs.addAll(abstractResource.getPostConstructMethods());
        final ConstructorInjectablePair ctorInjectablePair = this.getConstructor(serverInjectableProviderCtx, scope, abstractResource);
        if (ctorInjectablePair == null) {
            this.constructor = null;
            this.nonPublicConstructor = this.getNonPublicConstructor();
            this.injectables = null;
        }
        else if (ctorInjectablePair.injectables.isEmpty()) {
            this.constructor = ctorInjectablePair.constructor;
            this.nonPublicConstructor = null;
            this.injectables = null;
        }
        else {
            if (ctorInjectablePair.injectables.contains(null)) {
                for (int i = 0; i < ctorInjectablePair.injectables.size(); ++i) {
                    if (ctorInjectablePair.injectables.get(i) == null) {
                        Errors.missingDependency(ctorInjectablePair.constructor, i);
                    }
                }
            }
            this.constructor = ctorInjectablePair.constructor;
            this.injectables = (List<AbstractHttpContextInjectable>)AbstractHttpContextInjectable.transform(ctorInjectablePair.injectables);
            if (this.constructor != null) {
                this.setAccessible(this.constructor);
                this.nonPublicConstructor = null;
            }
            else {
                this.nonPublicConstructor = ((this.injectables == null) ? this.getNonPublicConstructor() : null);
            }
        }
    }
    
    private Constructor getNonPublicConstructor() {
        try {
            final Constructor result = AccessController.doPrivileged((PrivilegedExceptionAction<Constructor>)new PrivilegedExceptionAction<Constructor>() {
                @Override
                public Constructor run() throws NoSuchMethodException {
                    return ResourceComponentConstructor.this.clazz.getDeclaredConstructor((Class[])new Class[0]);
                }
            });
            this.setAccessible(result);
            return result;
        }
        catch (PrivilegedActionException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof NoSuchMethodException) {
                return null;
            }
            throw new WebApplicationException(cause);
        }
    }
    
    private void setAccessible(final Constructor constructor) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                constructor.setAccessible(true);
                return null;
            }
        });
    }
    
    public Class getResourceClass() {
        return this.clazz;
    }
    
    public Object construct(final HttpContext hc) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Object o = this._construct(hc);
        this.resourceComponentInjector.inject(hc, o);
        for (final Method postConstruct : this.postConstructs) {
            postConstruct.invoke(o, new Object[0]);
        }
        return o;
    }
    
    private Object _construct(final HttpContext httpContext) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (this.injectables == null) {
            return (this.constructor != null) ? this.constructor.newInstance(new Object[0]) : ((this.nonPublicConstructor != null) ? this.nonPublicConstructor.newInstance(new Object[0]) : this.clazz.newInstance());
        }
        final Object[] params = new Object[this.injectables.size()];
        int i = 0;
        for (final AbstractHttpContextInjectable injectable : this.injectables) {
            params[i++] = ((injectable != null) ? injectable.getValue(httpContext) : null);
        }
        return this.constructor.newInstance(params);
    }
    
    private <T> ConstructorInjectablePair getConstructor(final ServerInjectableProviderContext sipc, final ComponentScope scope, final AbstractResource ar) {
        if (ar.getConstructors().isEmpty()) {
            return null;
        }
        final SortedSet<ConstructorInjectablePair> cs = new TreeSet<ConstructorInjectablePair>(new ConstructorComparator<Object>());
        for (final AbstractResourceConstructor arc : ar.getConstructors()) {
            final List<Injectable> is = sipc.getInjectable(arc.getCtor(), arc.getParameters(), scope);
            cs.add(new ConstructorInjectablePair(arc.getCtor(), (List)is));
        }
        return cs.first();
    }
    
    private static class ConstructorInjectablePair
    {
        private final Constructor constructor;
        private final List<Injectable> injectables;
        
        private ConstructorInjectablePair(final Constructor constructor, final List<Injectable> injectables) {
            this.constructor = constructor;
            this.injectables = injectables;
        }
    }
    
    private static class ConstructorComparator<T> implements Comparator<ConstructorInjectablePair>
    {
        @Override
        public int compare(final ConstructorInjectablePair o1, final ConstructorInjectablePair o2) {
            final int p = Collections.frequency(o1.injectables, null) - Collections.frequency(o2.injectables, null);
            if (p != 0) {
                return p;
            }
            return o2.constructor.getParameterTypes().length - o1.constructor.getParameterTypes().length;
        }
    }
}
