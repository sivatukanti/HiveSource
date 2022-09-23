// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.util;

import java.util.Iterator;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.Set;
import com.google.inject.spi.ProviderWithDependencies;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.spi.Dependency;
import java.util.Collection;
import com.google.inject.internal.util.$Sets;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.Provider;

public final class Providers
{
    private Providers() {
    }
    
    public static <T> Provider<T> of(final T instance) {
        return new Provider<T>() {
            public T get() {
                return instance;
            }
            
            @Override
            public String toString() {
                return "of(" + instance + ")";
            }
        };
    }
    
    public static <T> Provider<T> guicify(final javax.inject.Provider<T> provider) {
        if (provider instanceof Provider) {
            return (Provider<T>)(Provider)provider;
        }
        final javax.inject.Provider<T> delegate = $Preconditions.checkNotNull(provider, (Object)"provider");
        final Set<InjectionPoint> injectionPoints = InjectionPoint.forInstanceMethodsAndFields(provider.getClass());
        if (injectionPoints.isEmpty()) {
            return new Provider<T>() {
                public T get() {
                    return delegate.get();
                }
                
                @Override
                public String toString() {
                    return "guicified(" + delegate + ")";
                }
            };
        }
        final Set<Dependency<?>> mutableDeps = (Set<Dependency<?>>)$Sets.newHashSet();
        for (final InjectionPoint ip : injectionPoints) {
            mutableDeps.addAll(ip.getDependencies());
        }
        final Set<Dependency<?>> dependencies = (Set<Dependency<?>>)$ImmutableSet.copyOf((Iterable<?>)mutableDeps);
        return new ProviderWithDependencies<T>() {
            @Inject
            void initialize(final Injector injector) {
                injector.injectMembers(delegate);
            }
            
            public Set<Dependency<?>> getDependencies() {
                return dependencies;
            }
            
            public T get() {
                return delegate.get();
            }
            
            @Override
            public String toString() {
                return "guicified(" + delegate + ")";
            }
        };
    }
}
