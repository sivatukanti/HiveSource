// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.Message;
import com.google.inject.ProvisionException;
import com.google.inject.spi.Dependency;
import com.google.inject.Provider;

final class ProviderToInternalFactoryAdapter<T> implements Provider<T>
{
    private final InjectorImpl injector;
    private final InternalFactory<? extends T> internalFactory;
    
    public ProviderToInternalFactoryAdapter(final InjectorImpl injector, final InternalFactory<? extends T> internalFactory) {
        this.injector = injector;
        this.internalFactory = internalFactory;
    }
    
    public T get() {
        final Errors errors = new Errors();
        try {
            final T t = this.injector.callInContext((ContextualCallable<T>)new ContextualCallable<T>() {
                public T call(final InternalContext context) throws ErrorsException {
                    final Dependency dependency = context.getDependency();
                    return ProviderToInternalFactoryAdapter.this.internalFactory.get(errors, context, dependency, true);
                }
            });
            errors.throwIfNewErrors(0);
            return t;
        }
        catch (ErrorsException e) {
            throw new ProvisionException(errors.merge(e.getErrors()).getMessages());
        }
    }
    
    @Override
    public String toString() {
        return this.internalFactory.toString();
    }
}
