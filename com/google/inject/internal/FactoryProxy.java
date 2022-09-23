// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.spi.Dependency;
import com.google.inject.Key;

final class FactoryProxy<T> implements InternalFactory<T>, CreationListener
{
    private final InjectorImpl injector;
    private final Key<T> key;
    private final Key<? extends T> targetKey;
    private final Object source;
    private InternalFactory<? extends T> targetFactory;
    
    FactoryProxy(final InjectorImpl injector, final Key<T> key, final Key<? extends T> targetKey, final Object source) {
        this.injector = injector;
        this.key = key;
        this.targetKey = targetKey;
        this.source = source;
    }
    
    public void notify(final Errors errors) {
        try {
            this.targetFactory = this.injector.getInternalFactory(this.targetKey, errors.withSource(this.source), InjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
        }
        catch (ErrorsException e) {
            errors.merge(e.getErrors());
        }
    }
    
    public T get(final Errors errors, final InternalContext context, final Dependency<?> dependency, final boolean linked) throws ErrorsException {
        return (T)this.targetFactory.get(errors.withSource(this.targetKey), context, dependency, true);
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(FactoryProxy.class).add("key", this.key).add("provider", this.targetFactory).toString();
    }
}
