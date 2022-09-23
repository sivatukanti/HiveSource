// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.xml;

import java.lang.annotation.Annotation;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import java.util.concurrent.atomic.AtomicReference;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import com.sun.jersey.spi.inject.InjectableProvider;

public abstract class LazySingletonContextProvider<T> implements InjectableProvider<Context, Type>
{
    private final Class<T> t;
    private final AtomicReference<T> rf;
    
    protected LazySingletonContextProvider(final Class<T> t) {
        this.rf = new AtomicReference<T>();
        this.t = t;
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
                    return (T)LazySingletonContextProvider.this.get();
                }
            };
        }
        return null;
    }
    
    private T get() {
        T f = this.rf.get();
        if (f == null) {
            final T nf = this.getInstance();
            this.rf.compareAndSet(null, nf);
            f = this.rf.get();
        }
        return f;
    }
    
    protected abstract T getInstance();
}
