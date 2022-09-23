// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.Dependency;

final class SingleParameterInjector<T>
{
    private static final Object[] NO_ARGUMENTS;
    private final Dependency<T> dependency;
    private final InternalFactory<? extends T> factory;
    
    SingleParameterInjector(final Dependency<T> dependency, final InternalFactory<? extends T> factory) {
        this.dependency = dependency;
        this.factory = factory;
    }
    
    private T inject(final Errors errors, final InternalContext context) throws ErrorsException {
        final Dependency previous = context.setDependency(this.dependency);
        try {
            return (T)this.factory.get(errors.withSource(this.dependency), context, this.dependency, false);
        }
        finally {
            context.setDependency(previous);
        }
    }
    
    static Object[] getAll(final Errors errors, final InternalContext context, final SingleParameterInjector<?>[] parameterInjectors) throws ErrorsException {
        if (parameterInjectors == null) {
            return SingleParameterInjector.NO_ARGUMENTS;
        }
        final int numErrorsBefore = errors.size();
        final int size = parameterInjectors.length;
        final Object[] parameters = new Object[size];
        for (int i = 0; i < size; ++i) {
            final SingleParameterInjector<?> parameterInjector = parameterInjectors[i];
            try {
                parameters[i] = parameterInjector.inject(errors, context);
            }
            catch (ErrorsException e) {
                errors.merge(e.getErrors());
            }
        }
        errors.throwIfNewErrors(numErrorsBefore);
        return parameters;
    }
    
    static {
        NO_ARGUMENTS = new Object[0];
    }
}
