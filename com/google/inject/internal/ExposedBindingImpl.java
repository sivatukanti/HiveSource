// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.Binder;
import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.spi.Dependency;
import java.util.Set;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.Key;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ExposedBinding;

public final class ExposedBindingImpl<T> extends BindingImpl<T> implements ExposedBinding<T>
{
    private final PrivateElements privateElements;
    
    public ExposedBindingImpl(final InjectorImpl injector, final Object source, final Key<T> key, final InternalFactory<T> factory, final PrivateElements privateElements) {
        super(injector, key, source, (InternalFactory<? extends T>)factory, Scoping.UNSCOPED);
        this.privateElements = privateElements;
    }
    
    public <V> V acceptTargetVisitor(final BindingTargetVisitor<? super T, V> visitor) {
        return visitor.visit((ExposedBinding<? extends T>)this);
    }
    
    public Set<Dependency<?>> getDependencies() {
        return (Set<Dependency<?>>)$ImmutableSet.of(Dependency.get((Key<Object>)Key.get((Class<T>)Injector.class)));
    }
    
    public PrivateElements getPrivateElements() {
        return this.privateElements;
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(ExposedBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("privateElements", this.privateElements).toString();
    }
    
    public void applyTo(final Binder binder) {
        throw new UnsupportedOperationException("This element represents a synthetic binding.");
    }
}
