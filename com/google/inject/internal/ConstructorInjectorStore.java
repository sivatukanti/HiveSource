// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.util.Set;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$Iterables;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionPoint;

final class ConstructorInjectorStore
{
    private final InjectorImpl injector;
    private final FailableCache<InjectionPoint, ConstructorInjector<?>> cache;
    
    ConstructorInjectorStore(final InjectorImpl injector) {
        this.cache = new FailableCache<InjectionPoint, ConstructorInjector<?>>() {
            @Override
            protected ConstructorInjector<?> create(final InjectionPoint constructorInjector, final Errors errors) throws ErrorsException {
                return ConstructorInjectorStore.this.createConstructor(constructorInjector, errors);
            }
        };
        this.injector = injector;
    }
    
    public ConstructorInjector<?> get(final InjectionPoint constructorInjector, final Errors errors) throws ErrorsException {
        return this.cache.get(constructorInjector, errors);
    }
    
    boolean remove(final InjectionPoint ip) {
        return this.cache.remove(ip);
    }
    
    private <T> ConstructorInjector<T> createConstructor(final InjectionPoint injectionPoint, final Errors errors) throws ErrorsException {
        final int numErrorsBefore = errors.size();
        final SingleParameterInjector<?>[] constructorParameterInjectors = this.injector.getParametersInjectors(injectionPoint.getDependencies(), errors);
        final MembersInjectorImpl<T> membersInjector = this.injector.membersInjectorStore.get(injectionPoint.getDeclaringType(), errors);
        final $ImmutableList<MethodAspect> injectorAspects = this.injector.state.getMethodAspects();
        final $ImmutableList<MethodAspect> methodAspects = membersInjector.getAddedAspects().isEmpty() ? injectorAspects : $ImmutableList.copyOf($Iterables.concat((Iterable<? extends MethodAspect>)injectorAspects, (Iterable<? extends MethodAspect>)membersInjector.getAddedAspects()));
        final ConstructionProxyFactory<T> factory = new ProxyFactory<T>(injectionPoint, methodAspects);
        errors.throwIfNewErrors(numErrorsBefore);
        return new ConstructorInjector<T>(membersInjector.getInjectionPoints(), factory.create(), constructorParameterInjectors, membersInjector);
    }
}
