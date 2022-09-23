// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Objects;
import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.Binder;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.util.Providers;
import java.util.Set;
import com.google.inject.Key;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.Provider;
import com.google.inject.spi.InstanceBinding;

final class InstanceBindingImpl<T> extends BindingImpl<T> implements InstanceBinding<T>
{
    final T instance;
    final Provider<T> provider;
    final $ImmutableSet<InjectionPoint> injectionPoints;
    
    public InstanceBindingImpl(final InjectorImpl injector, final Key<T> key, final Object source, final InternalFactory<? extends T> internalFactory, final Set<InjectionPoint> injectionPoints, final T instance) {
        super(injector, key, source, internalFactory, Scoping.EAGER_SINGLETON);
        this.injectionPoints = $ImmutableSet.copyOf((Iterable<? extends InjectionPoint>)injectionPoints);
        this.instance = instance;
        this.provider = Providers.of(instance);
    }
    
    public InstanceBindingImpl(final Object source, final Key<T> key, final Scoping scoping, final Set<InjectionPoint> injectionPoints, final T instance) {
        super(source, key, scoping);
        this.injectionPoints = $ImmutableSet.copyOf((Iterable<? extends InjectionPoint>)injectionPoints);
        this.instance = instance;
        this.provider = Providers.of(instance);
    }
    
    @Override
    public Provider<T> getProvider() {
        return this.provider;
    }
    
    public <V> V acceptTargetVisitor(final BindingTargetVisitor<? super T, V> visitor) {
        return visitor.visit((InstanceBinding<? extends T>)this);
    }
    
    public T getInstance() {
        return this.instance;
    }
    
    public Set<InjectionPoint> getInjectionPoints() {
        return this.injectionPoints;
    }
    
    public Set<Dependency<?>> getDependencies() {
        return (this.instance instanceof HasDependencies) ? $ImmutableSet.copyOf((Iterable<?>)((HasDependencies)this.instance).getDependencies()) : Dependency.forInjectionPoints(this.injectionPoints);
    }
    
    public BindingImpl<T> withScoping(final Scoping scoping) {
        return new InstanceBindingImpl(this.getSource(), this.getKey(), scoping, this.injectionPoints, this.instance);
    }
    
    public BindingImpl<T> withKey(final Key<T> key) {
        return new InstanceBindingImpl(this.getSource(), (Key<Object>)key, this.getScoping(), this.injectionPoints, this.instance);
    }
    
    public void applyTo(final Binder binder) {
        binder.withSource(this.getSource()).bind(this.getKey()).toInstance(this.instance);
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(InstanceBinding.class).add("key", this.getKey()).add("source", this.getSource()).add("instance", this.instance).toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof InstanceBindingImpl) {
            final InstanceBindingImpl<?> o = (InstanceBindingImpl<?>)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && $Objects.equal(this.instance, o.instance);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $Objects.hashCode(this.getKey(), this.getScoping());
    }
}
