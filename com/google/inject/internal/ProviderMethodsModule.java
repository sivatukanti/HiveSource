// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.spi.Message;
import com.google.inject.spi.Dependency;
import com.google.inject.Provider;
import com.google.inject.Key;
import java.util.logging.Logger;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import com.google.inject.Provides;
import com.google.inject.internal.util.$Lists;
import java.util.List;
import java.util.Iterator;
import com.google.inject.Binder;
import com.google.inject.util.Modules;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.TypeLiteral;
import com.google.inject.Module;

public final class ProviderMethodsModule implements Module
{
    private final Object delegate;
    private final TypeLiteral<?> typeLiteral;
    
    private ProviderMethodsModule(final Object delegate) {
        this.delegate = $Preconditions.checkNotNull(delegate, (Object)"delegate");
        this.typeLiteral = TypeLiteral.get(this.delegate.getClass());
    }
    
    public static Module forModule(final Module module) {
        return forObject(module);
    }
    
    public static Module forObject(final Object object) {
        if (object instanceof ProviderMethodsModule) {
            return Modules.EMPTY_MODULE;
        }
        return new ProviderMethodsModule(object);
    }
    
    public synchronized void configure(final Binder binder) {
        for (final ProviderMethod<?> providerMethod : this.getProviderMethods(binder)) {
            providerMethod.configure(binder);
        }
    }
    
    public List<ProviderMethod<?>> getProviderMethods(final Binder binder) {
        final List<ProviderMethod<?>> result = (List<ProviderMethod<?>>)$Lists.newArrayList();
        for (Class<?> c = this.delegate.getClass(); c != Object.class; c = c.getSuperclass()) {
            for (final Method method : c.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Provides.class)) {
                    result.add(this.createProviderMethod(binder, method));
                }
            }
        }
        return result;
    }
    
     <T> ProviderMethod<T> createProviderMethod(Binder binder, final Method method) {
        binder = binder.withSource(method);
        final Errors errors = new Errors(method);
        final List<Dependency<?>> dependencies = (List<Dependency<?>>)$Lists.newArrayList();
        final List<Provider<?>> parameterProviders = (List<Provider<?>>)$Lists.newArrayList();
        final List<TypeLiteral<?>> parameterTypes = this.typeLiteral.getParameterTypes(method);
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterTypes.size(); ++i) {
            Key<?> key = this.getKey(errors, parameterTypes.get(i), method, parameterAnnotations[i]);
            if (key.equals(Key.get(Logger.class))) {
                final Key<Logger> loggerKey = Key.get(Logger.class, UniqueAnnotations.create());
                binder.bind(loggerKey).toProvider(new LogProvider(method));
                key = loggerKey;
            }
            dependencies.add(Dependency.get(key));
            parameterProviders.add(binder.getProvider(key));
        }
        final TypeLiteral<T> returnType = (TypeLiteral<T>)this.typeLiteral.getReturnType(method);
        final Key<T> key2 = this.getKey(errors, returnType, method, method.getAnnotations());
        final Class<? extends Annotation> scopeAnnotation = Annotations.findScopeAnnotation(errors, method.getAnnotations());
        for (final Message message : errors.getMessages()) {
            binder.addError(message);
        }
        return new ProviderMethod<T>(key2, method, this.delegate, $ImmutableSet.copyOf((Iterable<? extends Dependency<?>>)dependencies), parameterProviders, scopeAnnotation);
    }
    
     <T> Key<T> getKey(final Errors errors, final TypeLiteral<T> type, final Member member, final Annotation[] annotations) {
        final Annotation bindingAnnotation = Annotations.findBindingAnnotation(errors, member, annotations);
        return (bindingAnnotation == null) ? Key.get(type) : Key.get(type, bindingAnnotation);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ProviderMethodsModule && ((ProviderMethodsModule)o).delegate == this.delegate;
    }
    
    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }
    
    private static final class LogProvider implements Provider<Logger>
    {
        private final String name;
        
        public LogProvider(final Method method) {
            this.name = method.getDeclaringClass().getName() + "." + method.getName();
        }
        
        public Logger get() {
            return Logger.getLogger(this.name);
        }
    }
}
