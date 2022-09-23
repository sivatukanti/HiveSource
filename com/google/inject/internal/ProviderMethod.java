// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Objects;
import java.lang.reflect.Member;
import com.google.inject.internal.util.$StackTraceElements;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import com.google.inject.PrivateBinder;
import com.google.inject.Binder;
import com.google.inject.Exposed;
import com.google.inject.Provider;
import java.util.List;
import com.google.inject.spi.Dependency;
import com.google.inject.internal.util.$ImmutableSet;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import com.google.inject.Key;
import com.google.inject.spi.ProviderWithDependencies;

public class ProviderMethod<T> implements ProviderWithDependencies<T>
{
    private final Key<T> key;
    private final Class<? extends Annotation> scopeAnnotation;
    private final Object instance;
    private final Method method;
    private final $ImmutableSet<Dependency<?>> dependencies;
    private final List<Provider<?>> parameterProviders;
    private final boolean exposed;
    
    ProviderMethod(final Key<T> key, final Method method, final Object instance, final $ImmutableSet<Dependency<?>> dependencies, final List<Provider<?>> parameterProviders, final Class<? extends Annotation> scopeAnnotation) {
        this.key = key;
        this.scopeAnnotation = scopeAnnotation;
        this.instance = instance;
        this.dependencies = dependencies;
        this.method = method;
        this.parameterProviders = parameterProviders;
        this.exposed = method.isAnnotationPresent(Exposed.class);
        method.setAccessible(true);
    }
    
    public Key<T> getKey() {
        return this.key;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public Object getInstance() {
        return this.instance;
    }
    
    public void configure(Binder binder) {
        binder = binder.withSource(this.method);
        if (this.scopeAnnotation != null) {
            binder.bind(this.key).toProvider((Provider<? extends T>)this).in(this.scopeAnnotation);
        }
        else {
            binder.bind(this.key).toProvider((Provider<? extends T>)this);
        }
        if (this.exposed) {
            ((PrivateBinder)binder).expose(this.key);
        }
    }
    
    public T get() {
        final Object[] parameters = new Object[this.parameterProviders.size()];
        for (int i = 0; i < parameters.length; ++i) {
            parameters[i] = this.parameterProviders.get(i).get();
        }
        try {
            final T result = (T)this.method.invoke(this.instance, parameters);
            return result;
        }
        catch (IllegalAccessException e) {
            throw new AssertionError((Object)e);
        }
        catch (InvocationTargetException e2) {
            throw Exceptions.throwCleanly(e2);
        }
    }
    
    public Set<Dependency<?>> getDependencies() {
        return this.dependencies;
    }
    
    @Override
    public String toString() {
        return "@Provides " + $StackTraceElements.forMember(this.method).toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ProviderMethod) {
            final ProviderMethod o = (ProviderMethod)obj;
            return this.method.equals(o.method) && this.instance.equals(o.instance);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $Objects.hashCode(this.method);
    }
}
