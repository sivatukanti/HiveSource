// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

import com.google.inject.spi.TypeListener;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.Message;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import java.lang.annotation.Annotation;
import org.aopalliance.intercept.MethodInterceptor;
import java.lang.reflect.Method;
import com.google.inject.matcher.Matcher;

public interface Binder
{
    void bindInterceptor(final Matcher<? super Class<?>> p0, final Matcher<? super Method> p1, final MethodInterceptor... p2);
    
    void bindScope(final Class<? extends Annotation> p0, final Scope p1);
    
     <T> LinkedBindingBuilder<T> bind(final Key<T> p0);
    
     <T> AnnotatedBindingBuilder<T> bind(final TypeLiteral<T> p0);
    
     <T> AnnotatedBindingBuilder<T> bind(final Class<T> p0);
    
    AnnotatedConstantBindingBuilder bindConstant();
    
     <T> void requestInjection(final TypeLiteral<T> p0, final T p1);
    
    void requestInjection(final Object p0);
    
    void requestStaticInjection(final Class<?>... p0);
    
    void install(final Module p0);
    
    Stage currentStage();
    
    void addError(final String p0, final Object... p1);
    
    void addError(final Throwable p0);
    
    void addError(final Message p0);
    
     <T> Provider<T> getProvider(final Key<T> p0);
    
     <T> Provider<T> getProvider(final Class<T> p0);
    
     <T> MembersInjector<T> getMembersInjector(final TypeLiteral<T> p0);
    
     <T> MembersInjector<T> getMembersInjector(final Class<T> p0);
    
    void convertToTypes(final Matcher<? super TypeLiteral<?>> p0, final TypeConverter p1);
    
    void bindListener(final Matcher<? super TypeLiteral<?>> p0, final TypeListener p1);
    
    Binder withSource(final Object p0);
    
    Binder skipSources(final Class... p0);
    
    PrivateBinder newPrivateBinder();
    
    void requireExplicitBindings();
    
    void disableCircularProxies();
}
