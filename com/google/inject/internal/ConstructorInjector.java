// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.internal.util.$ImmutableSet;

final class ConstructorInjector<T>
{
    private final $ImmutableSet<InjectionPoint> injectableMembers;
    private final SingleParameterInjector<?>[] parameterInjectors;
    private final ConstructionProxy<T> constructionProxy;
    private final MembersInjectorImpl<T> membersInjector;
    
    ConstructorInjector(final Set<InjectionPoint> injectableMembers, final ConstructionProxy<T> constructionProxy, final SingleParameterInjector<?>[] parameterInjectors, final MembersInjectorImpl<T> membersInjector) {
        this.injectableMembers = $ImmutableSet.copyOf((Iterable<? extends InjectionPoint>)injectableMembers);
        this.constructionProxy = constructionProxy;
        this.parameterInjectors = parameterInjectors;
        this.membersInjector = membersInjector;
    }
    
    public $ImmutableSet<InjectionPoint> getInjectableMembers() {
        return this.injectableMembers;
    }
    
    ConstructionProxy<T> getConstructionProxy() {
        return this.constructionProxy;
    }
    
    Object construct(final Errors errors, final InternalContext context, final Class<?> expectedType, final boolean allowProxy) throws ErrorsException {
        final ConstructionContext<T> constructionContext = context.getConstructionContext(this);
        if (constructionContext.isConstructing()) {
            if (!allowProxy) {
                throw errors.circularProxiesDisabled(expectedType).toException();
            }
            return constructionContext.createProxy(errors, expectedType);
        }
        else {
            T t = constructionContext.getCurrentReference();
            if (t != null) {
                return t;
            }
            try {
                constructionContext.startConstruction();
                try {
                    final Object[] parameters = SingleParameterInjector.getAll(errors, context, this.parameterInjectors);
                    t = this.constructionProxy.newInstance(parameters);
                    constructionContext.setProxyDelegates(t);
                }
                finally {
                    constructionContext.finishConstruction();
                }
                constructionContext.setCurrentReference(t);
                this.membersInjector.injectMembers(t, errors, context, false);
                this.membersInjector.notifyListeners(t, errors);
                return t;
            }
            catch (InvocationTargetException userException) {
                final Throwable cause = (userException.getCause() != null) ? userException.getCause() : userException;
                throw errors.withSource(this.constructionProxy.getInjectionPoint()).errorInjectingConstructor(cause).toException();
            }
            finally {
                constructionContext.removeCurrentReference();
            }
        }
    }
}
