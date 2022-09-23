// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.inject;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;

public abstract class SingletonTypeInjectableProvider<A extends Annotation, T> implements InjectableProvider<A, Type>, Injectable<T>
{
    private final Type t;
    private final T instance;
    
    public SingletonTypeInjectableProvider(final Type t, final T instance) {
        this.t = t;
        this.instance = instance;
    }
    
    @Override
    public final ComponentScope getScope() {
        return ComponentScope.Singleton;
    }
    
    @Override
    public final Injectable<T> getInjectable(final ComponentContext ic, final A a, final Type c) {
        if (c.equals(this.t)) {
            return this;
        }
        return null;
    }
    
    @Override
    public final T getValue() {
        return this.instance;
    }
}
