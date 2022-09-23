// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import org.aopalliance.intercept.MethodInterceptor;
import java.util.List;
import java.lang.reflect.Method;
import com.google.inject.internal.util.$ImmutableMap;
import java.lang.reflect.Constructor;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.InvocationTargetException;

interface ConstructionProxy<T>
{
    T newInstance(final Object... p0) throws InvocationTargetException;
    
    InjectionPoint getInjectionPoint();
    
    Constructor<T> getConstructor();
    
    $ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors();
}
