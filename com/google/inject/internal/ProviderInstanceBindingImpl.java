// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Objects;
import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.Binder;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ProviderWithExtensionVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import java.util.Set;
import com.google.inject.Key;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.Provider;
import com.google.inject.spi.ProviderInstanceBinding;

final class ProviderInstanceBindingImpl<T> extends BindingImpl<T> implements ProviderInstanceBinding<T>
{
    final Provider<? extends T> providerInstance;
    final $ImmutableSet<InjectionPoint> injectionPoints;
    
    public ProviderInstanceBindingImpl(final InjectorImpl injector, final Key<T> key, final Object source, final InternalFactory<? extends T> internalFactory, final Scoping scoping, final Provider<? extends T> providerInstance, final Set<InjectionPoint> injectionPoints) {
        super(injector, key, source, internalFactory, scoping);
        this.providerInstance = providerInstance;
        this.injectionPoints = $ImmutableSet.copyOf((Iterable<? extends InjectionPoint>)injectionPoints);
    }
    
    public ProviderInstanceBindingImpl(final Object source, final Key<T> key, final Scoping scoping, final Set<InjectionPoint> injectionPoints, final Provider<? extends T> providerInstance) {
        super(source, key, scoping);
        this.injectionPoints = $ImmutableSet.copyOf((Iterable<? extends InjectionPoint>)injectionPoints);
        this.providerInstance = providerInstance;
    }
    
    public <V> V acceptTargetVisitor(final BindingTargetVisitor<? super T, V> visitor) {
        if (this.providerInstance instanceof ProviderWithExtensionVisitor) {
            return ((ProviderWithExtensionVisitor)this.providerInstance).acceptExtensionVisitor(visitor, this);
        }
        return visitor.visit((ProviderInstanceBinding<? extends T>)this);
    }
    
    public Provider<? extends T> getProviderInstance() {
        return this.providerInstance;
    }
    
    public Set<InjectionPoint> getInjectionPoints() {
        return this.injectionPoints;
    }
    
    public Set<Dependency<?>> getDependencies() {
        return (this.providerInstance instanceof HasDependencies) ? $ImmutableSet.copyOf((Iterable<?>)((HasDependencies)this.providerInstance).getDependencies()) : Dependency.forInjectionPoints(this.injectionPoints);
    }
    
    public BindingImpl<T> withScoping(final Scoping scoping) {
        return new ProviderInstanceBindingImpl(this.getSource(), this.getKey(), scoping, this.injectionPoints, this.providerInstance);
    }
    
    public BindingImpl<T> withKey(final Key<T> key) {
        return new ProviderInstanceBindingImpl(this.getSource(), (Key<Object>)key, this.getScoping(), this.injectionPoints, this.providerInstance);
    }
    
    public void applyTo(final Binder binder) {
        this.getScoping().applyTo(binder.withSource(this.getSource()).bind(this.getKey()).toProvider(this.getProviderInstance()));
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(ProviderInstanceBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("scope", this.getScoping()).add("provider", this.providerInstance).toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ProviderInstanceBindingImpl) {
            final ProviderInstanceBindingImpl<?> o = (ProviderInstanceBindingImpl<?>)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && $Objects.equal(this.providerInstance, o.providerInstance);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $Objects.hashCode(this.getKey(), this.getScoping());
    }
}
