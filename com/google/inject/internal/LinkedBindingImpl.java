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
import com.google.inject.Key;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.LinkedKeyBinding;

public final class LinkedBindingImpl<T> extends BindingImpl<T> implements LinkedKeyBinding<T>, HasDependencies
{
    final Key<? extends T> targetKey;
    
    public LinkedBindingImpl(final InjectorImpl injector, final Key<T> key, final Object source, final InternalFactory<? extends T> internalFactory, final Scoping scoping, final Key<? extends T> targetKey) {
        super(injector, key, source, internalFactory, scoping);
        this.targetKey = targetKey;
    }
    
    public LinkedBindingImpl(final Object source, final Key<T> key, final Scoping scoping, final Key<? extends T> targetKey) {
        super(source, key, scoping);
        this.targetKey = targetKey;
    }
    
    public <V> V acceptTargetVisitor(final BindingTargetVisitor<? super T, V> visitor) {
        return visitor.visit((LinkedKeyBinding<? extends T>)this);
    }
    
    public Key<? extends T> getLinkedKey() {
        return this.targetKey;
    }
    
    public Set<Dependency<?>> getDependencies() {
        return (Set<Dependency<?>>)$ImmutableSet.of(Dependency.get(this.targetKey));
    }
    
    public BindingImpl<T> withScoping(final Scoping scoping) {
        return new LinkedBindingImpl(this.getSource(), this.getKey(), scoping, this.targetKey);
    }
    
    public BindingImpl<T> withKey(final Key<T> key) {
        return new LinkedBindingImpl(this.getSource(), (Key<Object>)key, this.getScoping(), this.targetKey);
    }
    
    public void applyTo(final Binder binder) {
        this.getScoping().applyTo(binder.withSource(this.getSource()).bind(this.getKey()).to(this.getLinkedKey()));
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(LinkedKeyBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("scope", this.getScoping()).add("target", this.targetKey).toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof LinkedBindingImpl) {
            final LinkedBindingImpl<?> o = (LinkedBindingImpl<?>)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && $Objects.equal(this.targetKey, o.targetKey);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $Objects.hashCode(this.getKey(), this.getScoping(), this.targetKey);
    }
}
