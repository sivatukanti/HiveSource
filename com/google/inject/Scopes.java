// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

import com.google.inject.internal.CircularDependencyProxy;
import com.google.inject.internal.InternalInjectorCreator;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.internal.LinkedBindingImpl;
import java.lang.annotation.Annotation;
import com.google.inject.spi.BindingScopingVisitor;

public class Scopes
{
    private static final Object NULL;
    public static final Scope SINGLETON;
    public static final Scope NO_SCOPE;
    
    private Scopes() {
    }
    
    public static boolean isSingleton(Binding<?> binding) {
        while (true) {
            final boolean singleton = binding.acceptScopingVisitor((BindingScopingVisitor<Boolean>)new BindingScopingVisitor<Boolean>() {
                public Boolean visitNoScoping() {
                    return false;
                }
                
                public Boolean visitScopeAnnotation(final Class<? extends Annotation> scopeAnnotation) {
                    return scopeAnnotation == Singleton.class || scopeAnnotation == javax.inject.Singleton.class;
                }
                
                public Boolean visitScope(final Scope scope) {
                    return scope == Scopes.SINGLETON;
                }
                
                public Boolean visitEagerSingleton() {
                    return true;
                }
            });
            if (singleton) {
                return true;
            }
            if (binding instanceof LinkedBindingImpl) {
                final LinkedBindingImpl<?> linkedBinding = (LinkedBindingImpl<?>)(LinkedBindingImpl)binding;
                final Injector injector = linkedBinding.getInjector();
                if (injector == null) {
                    break;
                }
                binding = injector.getBinding(linkedBinding.getLinkedKey());
            }
            else {
                if (!(binding instanceof ExposedBinding)) {
                    break;
                }
                final ExposedBinding<?> exposedBinding = (ExposedBinding<?>)(ExposedBinding)binding;
                final Injector injector = exposedBinding.getPrivateElements().getInjector();
                if (injector == null) {
                    break;
                }
                binding = injector.getBinding(exposedBinding.getKey());
            }
        }
        return false;
    }
    
    static {
        NULL = new Object();
        SINGLETON = new Scope() {
            public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
                return new Provider<T>() {
                    private volatile Object instance;
                    
                    public T get() {
                        if (this.instance == null) {
                            synchronized (InternalInjectorCreator.class) {
                                if (this.instance == null) {
                                    final T provided = creator.get();
                                    if (provided instanceof CircularDependencyProxy) {
                                        return provided;
                                    }
                                    final Object providedOrSentinel = (provided == null) ? Scopes.NULL : provided;
                                    if (this.instance != null && this.instance != providedOrSentinel) {
                                        throw new ProvisionException("Provider was reentrant while creating a singleton");
                                    }
                                    this.instance = providedOrSentinel;
                                }
                            }
                        }
                        final Object localInstance = this.instance;
                        final T returnedInstance = (T)((localInstance != Scopes.NULL) ? localInstance : null);
                        return returnedInstance;
                    }
                    
                    @Override
                    public String toString() {
                        return String.format("%s[%s]", creator, Scopes.SINGLETON);
                    }
                };
            }
            
            @Override
            public String toString() {
                return "Scopes.SINGLETON";
            }
        };
        NO_SCOPE = new Scope() {
            public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
                return unscoped;
            }
            
            @Override
            public String toString() {
                return "Scopes.NO_SCOPE";
            }
        };
    }
}
