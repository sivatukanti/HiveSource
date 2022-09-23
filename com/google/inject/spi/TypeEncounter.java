// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import org.aopalliance.intercept.MethodInterceptor;
import java.lang.reflect.Method;
import com.google.inject.matcher.Matcher;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.Provider;
import com.google.inject.Key;

public interface TypeEncounter<I>
{
    void addError(final String p0, final Object... p1);
    
    void addError(final Throwable p0);
    
    void addError(final Message p0);
    
     <T> Provider<T> getProvider(final Key<T> p0);
    
     <T> Provider<T> getProvider(final Class<T> p0);
    
     <T> MembersInjector<T> getMembersInjector(final TypeLiteral<T> p0);
    
     <T> MembersInjector<T> getMembersInjector(final Class<T> p0);
    
    void register(final MembersInjector<? super I> p0);
    
    void register(final InjectionListener<? super I> p0);
    
    void bindInterceptor(final Matcher<? super Method> p0, final MethodInterceptor... p1);
}
