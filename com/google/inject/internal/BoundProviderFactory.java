// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.Dependency;
import javax.inject.Provider;
import com.google.inject.Key;

final class BoundProviderFactory<T> implements InternalFactory<T>, CreationListener
{
    private final InjectorImpl injector;
    final Key<? extends Provider<? extends T>> providerKey;
    final Object source;
    private InternalFactory<? extends Provider<? extends T>> providerFactory;
    
    BoundProviderFactory(final InjectorImpl injector, final Key<? extends Provider<? extends T>> providerKey, final Object source) {
        this.injector = injector;
        this.providerKey = providerKey;
        this.source = source;
    }
    
    public void notify(final Errors errors) {
        try {
            this.providerFactory = this.injector.getInternalFactory(this.providerKey, errors.withSource(this.source), InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
        }
        catch (ErrorsException e) {
            errors.merge(e.getErrors());
        }
    }
    
    public T get(Errors errors, final InternalContext context, final Dependency<?> dependency, final boolean linked) throws ErrorsException {
        errors = errors.withSource(this.providerKey);
        final Provider<? extends T> provider = (Provider<? extends T>)this.providerFactory.get(errors, context, dependency, true);
        try {
            return errors.checkForNull((T)provider.get(), this.source, dependency);
        }
        catch (RuntimeException userException) {
            throw errors.errorInProvider(userException).toException();
        }
    }
    
    @Override
    public String toString() {
        return this.providerKey.toString();
    }
}
