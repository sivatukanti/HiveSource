// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.Provider;
import com.google.inject.Key;
import com.google.inject.Binding;

public abstract class BindingImpl<T> implements Binding<T>
{
    private final InjectorImpl injector;
    private final Key<T> key;
    private final Object source;
    private final Scoping scoping;
    private final InternalFactory<? extends T> internalFactory;
    private volatile Provider<T> provider;
    
    public BindingImpl(final InjectorImpl injector, final Key<T> key, final Object source, final InternalFactory<? extends T> internalFactory, final Scoping scoping) {
        this.injector = injector;
        this.key = key;
        this.source = source;
        this.internalFactory = internalFactory;
        this.scoping = scoping;
    }
    
    protected BindingImpl(final Object source, final Key<T> key, final Scoping scoping) {
        this.internalFactory = null;
        this.injector = null;
        this.source = source;
        this.key = key;
        this.scoping = scoping;
    }
    
    public Key<T> getKey() {
        return this.key;
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public Provider<T> getProvider() {
        if (this.provider == null) {
            if (this.injector == null) {
                throw new UnsupportedOperationException("getProvider() not supported for module bindings");
            }
            this.provider = this.injector.getProvider(this.key);
        }
        return this.provider;
    }
    
    public InternalFactory<? extends T> getInternalFactory() {
        return this.internalFactory;
    }
    
    public Scoping getScoping() {
        return this.scoping;
    }
    
    public boolean isConstant() {
        return this instanceof InstanceBinding;
    }
    
    public <V> V acceptVisitor(final ElementVisitor<V> visitor) {
        return visitor.visit((Binding<Object>)this);
    }
    
    public <V> V acceptScopingVisitor(final BindingScopingVisitor<V> visitor) {
        return this.scoping.acceptVisitor(visitor);
    }
    
    protected BindingImpl<T> withScoping(final Scoping scoping) {
        throw new AssertionError();
    }
    
    protected BindingImpl<T> withKey(final Key<T> key) {
        throw new AssertionError();
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(Binding.class).add("key", this.key).add("scope", this.scoping).add("source", this.source).toString();
    }
    
    public InjectorImpl getInjector() {
        return this.injector;
    }
}
