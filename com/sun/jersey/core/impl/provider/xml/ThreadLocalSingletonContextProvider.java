// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.xml;

import java.lang.annotation.Annotation;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import com.sun.jersey.spi.inject.InjectableProvider;

public abstract class ThreadLocalSingletonContextProvider<T> implements InjectableProvider<Context, Type>
{
    private final Class<T> t;
    private final ThreadLocal<T> rf;
    
    protected ThreadLocalSingletonContextProvider(final Class<T> t) {
        this.t = t;
        this.rf = new ThreadLocal<T>() {
            @Override
            protected synchronized T initialValue() {
                return ThreadLocalSingletonContextProvider.this.getInstance();
            }
        };
    }
    
    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }
    
    @Override
    public Injectable<T> getInjectable(final ComponentContext ic, final Context a, final Type c) {
        if (c == this.t) {
            return new Injectable<T>() {
                @Override
                public T getValue() {
                    return ThreadLocalSingletonContextProvider.this.rf.get();
                }
            };
        }
        return null;
    }
    
    protected abstract T getInstance();
}
