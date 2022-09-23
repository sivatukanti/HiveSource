// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.binder;

import java.lang.reflect.Constructor;
import com.google.inject.Provider;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public interface LinkedBindingBuilder<T> extends ScopedBindingBuilder
{
    ScopedBindingBuilder to(final Class<? extends T> p0);
    
    ScopedBindingBuilder to(final TypeLiteral<? extends T> p0);
    
    ScopedBindingBuilder to(final Key<? extends T> p0);
    
    void toInstance(final T p0);
    
    ScopedBindingBuilder toProvider(final Provider<? extends T> p0);
    
    ScopedBindingBuilder toProvider(final Class<? extends javax.inject.Provider<? extends T>> p0);
    
    ScopedBindingBuilder toProvider(final TypeLiteral<? extends javax.inject.Provider<? extends T>> p0);
    
    ScopedBindingBuilder toProvider(final Key<? extends javax.inject.Provider<? extends T>> p0);
    
     <S extends T> ScopedBindingBuilder toConstructor(final Constructor<S> p0);
    
     <S extends T> ScopedBindingBuilder toConstructor(final Constructor<S> p0, final TypeLiteral<? extends S> p1);
}
