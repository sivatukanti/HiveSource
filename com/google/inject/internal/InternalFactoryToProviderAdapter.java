// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.Dependency;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.Provider;

final class InternalFactoryToProviderAdapter<T> implements InternalFactory<T>
{
    private final Initializable<Provider<? extends T>> initializable;
    private final Object source;
    
    public InternalFactoryToProviderAdapter(final Initializable<Provider<? extends T>> initializable, final Object source) {
        this.initializable = $Preconditions.checkNotNull(initializable, (Object)"provider");
        this.source = $Preconditions.checkNotNull(source, (Object)"source");
    }
    
    public T get(final Errors errors, final InternalContext context, final Dependency<?> dependency, final boolean linked) throws ErrorsException {
        try {
            return errors.checkForNull((T)this.initializable.get(errors).get(), this.source, dependency);
        }
        catch (RuntimeException userException) {
            throw errors.withSource(this.source).errorInProvider(userException).toException();
        }
    }
    
    @Override
    public String toString() {
        return this.initializable.toString();
    }
}
