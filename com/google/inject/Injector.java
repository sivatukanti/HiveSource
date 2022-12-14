// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

import com.google.inject.spi.TypeConverterBinding;
import java.util.Set;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public interface Injector
{
    void injectMembers(final Object p0);
    
     <T> MembersInjector<T> getMembersInjector(final TypeLiteral<T> p0);
    
     <T> MembersInjector<T> getMembersInjector(final Class<T> p0);
    
    Map<Key<?>, Binding<?>> getBindings();
    
    Map<Key<?>, Binding<?>> getAllBindings();
    
     <T> Binding<T> getBinding(final Key<T> p0);
    
     <T> Binding<T> getBinding(final Class<T> p0);
    
     <T> Binding<T> getExistingBinding(final Key<T> p0);
    
     <T> List<Binding<T>> findBindingsByType(final TypeLiteral<T> p0);
    
     <T> Provider<T> getProvider(final Key<T> p0);
    
     <T> Provider<T> getProvider(final Class<T> p0);
    
     <T> T getInstance(final Key<T> p0);
    
     <T> T getInstance(final Class<T> p0);
    
    Injector getParent();
    
    Injector createChildInjector(final Iterable<? extends Module> p0);
    
    Injector createChildInjector(final Module... p0);
    
    Map<Class<? extends Annotation>, Scope> getScopeBindings();
    
    Set<TypeConverterBinding> getTypeConverterBindings();
}
