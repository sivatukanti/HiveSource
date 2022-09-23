// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Objects;
import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.Binder;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.spi.Dependency;
import java.util.Set;
import com.google.inject.spi.BindingTargetVisitor;
import javax.inject.Provider;
import com.google.inject.Key;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.ProviderKeyBinding;

final class LinkedProviderBindingImpl<T> extends BindingImpl<T> implements ProviderKeyBinding<T>, HasDependencies
{
    final Key<? extends Provider<? extends T>> providerKey;
    
    public LinkedProviderBindingImpl(final InjectorImpl injector, final Key<T> key, final Object source, final InternalFactory<? extends T> internalFactory, final Scoping scoping, final Key<? extends Provider<? extends T>> providerKey) {
        super(injector, key, source, internalFactory, scoping);
        this.providerKey = providerKey;
    }
    
    LinkedProviderBindingImpl(final Object source, final Key<T> key, final Scoping scoping, final Key<? extends Provider<? extends T>> providerKey) {
        super(source, key, scoping);
        this.providerKey = providerKey;
    }
    
    public <V> V acceptTargetVisitor(final BindingTargetVisitor<? super T, V> visitor) {
        return visitor.visit((ProviderKeyBinding<? extends T>)this);
    }
    
    public Key<? extends Provider<? extends T>> getProviderKey() {
        return this.providerKey;
    }
    
    public Set<Dependency<?>> getDependencies() {
        return (Set<Dependency<?>>)$ImmutableSet.of(Dependency.get(this.providerKey));
    }
    
    public BindingImpl<T> withScoping(final Scoping scoping) {
        return new LinkedProviderBindingImpl(this.getSource(), this.getKey(), scoping, this.providerKey);
    }
    
    public BindingImpl<T> withKey(final Key<T> key) {
        return new LinkedProviderBindingImpl(this.getSource(), (Key<Object>)key, this.getScoping(), this.providerKey);
    }
    
    public void applyTo(final Binder binder) {
        this.getScoping().applyTo(binder.withSource(this.getSource()).bind(this.getKey()).toProvider(this.getProviderKey()));
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(ProviderKeyBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("scope", this.getScoping()).add("provider", this.providerKey).toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof LinkedProviderBindingImpl) {
            final LinkedProviderBindingImpl<?> o = (LinkedProviderBindingImpl<?>)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && $Objects.equal(this.providerKey, o.providerKey);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $Objects.hashCode(this.getKey(), this.getScoping(), this.providerKey);
    }
}
