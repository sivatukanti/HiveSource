// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.cglib.core.$CodeGenerationException;
import org.aopalliance.intercept.MethodInterceptor;
import java.util.List;
import java.lang.reflect.Method;
import com.google.inject.internal.util.$ImmutableMap;
import java.lang.reflect.InvocationTargetException;
import com.google.inject.internal.cglib.reflect.$FastConstructor;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import com.google.inject.spi.InjectionPoint;

final class DefaultConstructionProxyFactory<T> implements ConstructionProxyFactory<T>
{
    private final InjectionPoint injectionPoint;
    
    DefaultConstructionProxyFactory(final InjectionPoint injectionPoint) {
        this.injectionPoint = injectionPoint;
    }
    
    public ConstructionProxy<T> create() {
        final Constructor<T> constructor = (Constructor<T>)this.injectionPoint.getMember();
        if (Modifier.isPublic(constructor.getModifiers())) {
            final Class<T> classToConstruct = constructor.getDeclaringClass();
            try {
                final $FastConstructor fastConstructor = BytecodeGen.newFastClass(classToConstruct, BytecodeGen.Visibility.forMember(constructor)).getConstructor(constructor);
                return new ConstructionProxy<T>() {
                    public T newInstance(final Object... arguments) throws InvocationTargetException {
                        return (T)fastConstructor.newInstance(arguments);
                    }
                    
                    public InjectionPoint getInjectionPoint() {
                        return DefaultConstructionProxyFactory.this.injectionPoint;
                    }
                    
                    public Constructor<T> getConstructor() {
                        return constructor;
                    }
                    
                    public $ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors() {
                        return $ImmutableMap.of();
                    }
                };
            }
            catch ($CodeGenerationException e) {
                if (!Modifier.isPublic(classToConstruct.getModifiers())) {
                    constructor.setAccessible(true);
                }
                return new ConstructionProxy<T>() {
                    final /* synthetic */ Constructor val$constructor;
                    
                    public T newInstance(final Object... arguments) throws InvocationTargetException {
                        try {
                            return this.val$constructor.newInstance(arguments);
                        }
                        catch (InstantiationException e) {
                            throw new AssertionError((Object)e);
                        }
                        catch (IllegalAccessException e2) {
                            throw new AssertionError((Object)e2);
                        }
                    }
                    
                    public InjectionPoint getInjectionPoint() {
                        return DefaultConstructionProxyFactory.this.injectionPoint;
                    }
                    
                    public Constructor<T> getConstructor() {
                        return (Constructor<T>)this.val$constructor;
                    }
                    
                    public $ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors() {
                        return $ImmutableMap.of();
                    }
                };
            }
        }
        constructor.setAccessible(true);
        return new ConstructionProxy<T>() {
            final /* synthetic */ Constructor val$constructor;
            
            public T newInstance(final Object... arguments) throws InvocationTargetException {
                try {
                    return constructor.newInstance(arguments);
                }
                catch (InstantiationException e) {
                    throw new AssertionError((Object)e);
                }
                catch (IllegalAccessException e2) {
                    throw new AssertionError((Object)e2);
                }
            }
            
            public InjectionPoint getInjectionPoint() {
                return DefaultConstructionProxyFactory.this.injectionPoint;
            }
            
            public Constructor<T> getConstructor() {
                return constructor;
            }
            
            public $ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors() {
                return $ImmutableMap.of();
            }
        };
    }
}
