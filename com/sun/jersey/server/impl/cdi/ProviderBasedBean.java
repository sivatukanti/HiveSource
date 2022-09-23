// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import javax.enterprise.context.spi.CreationalContext;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import javax.inject.Provider;

public class ProviderBasedBean<T> extends AbstractBean<T>
{
    private Provider<T> provider;
    
    public ProviderBasedBean(final Class<?> klass, final Provider<T> provider, final Annotation qualifier) {
        super(klass, qualifier);
        this.provider = provider;
    }
    
    public ProviderBasedBean(final Class<?> klass, final Type type, final Provider<T> provider, final Annotation qualifier) {
        super(klass, type, qualifier);
        this.provider = provider;
    }
    
    @Override
    public T create(final CreationalContext<T> creationalContext) {
        return this.provider.get();
    }
}
