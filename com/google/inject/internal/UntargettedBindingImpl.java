// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Objects;
import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.Binder;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.Key;
import com.google.inject.spi.UntargettedBinding;

final class UntargettedBindingImpl<T> extends BindingImpl<T> implements UntargettedBinding<T>
{
    UntargettedBindingImpl(final InjectorImpl injector, final Key<T> key, final Object source) {
        super(injector, key, source, (InternalFactory<? extends T>)new InternalFactory<T>() {
            public T get(final Errors errors, final InternalContext context, final Dependency<?> dependency, final boolean linked) {
                throw new AssertionError();
            }
        }, Scoping.UNSCOPED);
    }
    
    public UntargettedBindingImpl(final Object source, final Key<T> key, final Scoping scoping) {
        super(source, key, scoping);
    }
    
    public <V> V acceptTargetVisitor(final BindingTargetVisitor<? super T, V> visitor) {
        return visitor.visit((UntargettedBinding<? extends T>)this);
    }
    
    public BindingImpl<T> withScoping(final Scoping scoping) {
        return new UntargettedBindingImpl(this.getSource(), this.getKey(), scoping);
    }
    
    public BindingImpl<T> withKey(final Key<T> key) {
        return new UntargettedBindingImpl(this.getSource(), (Key<Object>)key, this.getScoping());
    }
    
    public void applyTo(final Binder binder) {
        this.getScoping().applyTo(binder.withSource(this.getSource()).bind(this.getKey()));
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(UntargettedBinding.class).add("key", this.getKey()).add("source", this.getSource()).toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof UntargettedBindingImpl) {
            final UntargettedBindingImpl<?> o = (UntargettedBindingImpl<?>)obj;
            return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return $Objects.hashCode(this.getKey(), this.getScoping());
    }
}
