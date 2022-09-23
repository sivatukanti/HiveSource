// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

import com.google.inject.spi.TypeListener;
import com.google.inject.spi.TypeConverter;
import org.aopalliance.intercept.MethodInterceptor;
import java.lang.reflect.Method;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Message;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import java.lang.annotation.Annotation;
import com.google.inject.internal.util.$Preconditions;

public abstract class AbstractModule implements Module
{
    Binder binder;
    
    public final synchronized void configure(final Binder builder) {
        $Preconditions.checkState(this.binder == null, (Object)"Re-entry is not allowed.");
        this.binder = $Preconditions.checkNotNull(builder, (Object)"builder");
        try {
            this.configure();
        }
        finally {
            this.binder = null;
        }
    }
    
    protected abstract void configure();
    
    protected Binder binder() {
        return this.binder;
    }
    
    protected void bindScope(final Class<? extends Annotation> scopeAnnotation, final Scope scope) {
        this.binder.bindScope(scopeAnnotation, scope);
    }
    
    protected <T> LinkedBindingBuilder<T> bind(final Key<T> key) {
        return this.binder.bind(key);
    }
    
    protected <T> AnnotatedBindingBuilder<T> bind(final TypeLiteral<T> typeLiteral) {
        return this.binder.bind(typeLiteral);
    }
    
    protected <T> AnnotatedBindingBuilder<T> bind(final Class<T> clazz) {
        return this.binder.bind(clazz);
    }
    
    protected AnnotatedConstantBindingBuilder bindConstant() {
        return this.binder.bindConstant();
    }
    
    protected void install(final Module module) {
        this.binder.install(module);
    }
    
    protected void addError(final String message, final Object... arguments) {
        this.binder.addError(message, arguments);
    }
    
    protected void addError(final Throwable t) {
        this.binder.addError(t);
    }
    
    protected void addError(final Message message) {
        this.binder.addError(message);
    }
    
    protected void requestInjection(final Object instance) {
        this.binder.requestInjection(instance);
    }
    
    protected void requestStaticInjection(final Class<?>... types) {
        this.binder.requestStaticInjection(types);
    }
    
    protected void bindInterceptor(final Matcher<? super Class<?>> classMatcher, final Matcher<? super Method> methodMatcher, final MethodInterceptor... interceptors) {
        this.binder.bindInterceptor(classMatcher, methodMatcher, interceptors);
    }
    
    protected void requireBinding(final Key<?> key) {
        this.binder.getProvider(key);
    }
    
    protected void requireBinding(final Class<?> type) {
        this.binder.getProvider(type);
    }
    
    protected <T> Provider<T> getProvider(final Key<T> key) {
        return this.binder.getProvider(key);
    }
    
    protected <T> Provider<T> getProvider(final Class<T> type) {
        return this.binder.getProvider(type);
    }
    
    protected void convertToTypes(final Matcher<? super TypeLiteral<?>> typeMatcher, final TypeConverter converter) {
        this.binder.convertToTypes(typeMatcher, converter);
    }
    
    protected Stage currentStage() {
        return this.binder.currentStage();
    }
    
    protected <T> MembersInjector<T> getMembersInjector(final Class<T> type) {
        return this.binder.getMembersInjector(type);
    }
    
    protected <T> MembersInjector<T> getMembersInjector(final TypeLiteral<T> type) {
        return this.binder.getMembersInjector(type);
    }
    
    protected void bindListener(final Matcher<? super TypeLiteral<?>> typeMatcher, final TypeListener listener) {
        this.binder.bindListener(typeMatcher, listener);
    }
}
