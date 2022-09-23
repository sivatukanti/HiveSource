// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.Dependency;
import com.google.inject.spi.PrivateElements;
import com.google.inject.Key;

final class ExposedKeyFactory<T> implements InternalFactory<T>, CreationListener
{
    private final Key<T> key;
    private final PrivateElements privateElements;
    private BindingImpl<T> delegate;
    
    ExposedKeyFactory(final Key<T> key, final PrivateElements privateElements) {
        this.key = key;
        this.privateElements = privateElements;
    }
    
    public void notify(final Errors errors) {
        final InjectorImpl privateInjector = (InjectorImpl)this.privateElements.getInjector();
        final BindingImpl<T> explicitBinding = privateInjector.state.getExplicitBinding(this.key);
        if (explicitBinding.getInternalFactory() == this) {
            errors.withSource(explicitBinding.getSource()).exposedButNotBound(this.key);
            return;
        }
        this.delegate = explicitBinding;
    }
    
    public T get(final Errors errors, final InternalContext context, final Dependency<?> dependency, final boolean linked) throws ErrorsException {
        return (T)this.delegate.getInternalFactory().get(errors, context, dependency, linked);
    }
}
