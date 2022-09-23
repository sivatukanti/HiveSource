// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.inject;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;

public abstract class PerRequestTypeInjectableProvider<A extends Annotation, T> implements InjectableProvider<A, Type>
{
    private final Type t;
    
    public PerRequestTypeInjectableProvider(final Type t) {
        this.t = t;
    }
    
    @Override
    public final ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }
    
    @Override
    public final Injectable getInjectable(final ComponentContext ic, final A a, final Type c) {
        if (c.equals(this.t)) {
            return this.getInjectable(ic, a);
        }
        return null;
    }
    
    public abstract Injectable<T> getInjectable(final ComponentContext p0, final A p1);
}
