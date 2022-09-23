// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.internal.util.$Objects;
import java.util.Iterator;
import java.util.List;
import com.google.inject.internal.util.$ImmutableSet;
import java.util.Collection;
import com.google.inject.internal.util.$Lists;
import java.util.Set;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.Key;

public final class Dependency<T>
{
    private final InjectionPoint injectionPoint;
    private final Key<T> key;
    private final boolean nullable;
    private final int parameterIndex;
    
    Dependency(final InjectionPoint injectionPoint, final Key<T> key, final boolean nullable, final int parameterIndex) {
        this.injectionPoint = injectionPoint;
        this.key = $Preconditions.checkNotNull(key, (Object)"key");
        this.nullable = nullable;
        this.parameterIndex = parameterIndex;
    }
    
    public static <T> Dependency<T> get(final Key<T> key) {
        return new Dependency<T>(null, key, true, -1);
    }
    
    public static Set<Dependency<?>> forInjectionPoints(final Set<InjectionPoint> injectionPoints) {
        final List<Dependency<?>> dependencies = (List<Dependency<?>>)$Lists.newArrayList();
        for (final InjectionPoint injectionPoint : injectionPoints) {
            dependencies.addAll(injectionPoint.getDependencies());
        }
        return (Set<Dependency<?>>)$ImmutableSet.copyOf((Iterable<?>)dependencies);
    }
    
    public Key<T> getKey() {
        return this.key;
    }
    
    public boolean isNullable() {
        return this.nullable;
    }
    
    public InjectionPoint getInjectionPoint() {
        return this.injectionPoint;
    }
    
    public int getParameterIndex() {
        return this.parameterIndex;
    }
    
    @Override
    public int hashCode() {
        return $Objects.hashCode(this.injectionPoint, this.parameterIndex, this.key);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Dependency) {
            final Dependency dependency = (Dependency)o;
            return $Objects.equal(this.injectionPoint, dependency.injectionPoint) && $Objects.equal(this.parameterIndex, dependency.parameterIndex) && $Objects.equal(this.key, dependency.key);
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.key);
        if (this.injectionPoint != null) {
            builder.append("@").append(this.injectionPoint);
            if (this.parameterIndex != -1) {
                builder.append("[").append(this.parameterIndex).append("]");
            }
        }
        return builder.toString();
    }
}
