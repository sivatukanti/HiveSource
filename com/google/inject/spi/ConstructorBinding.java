// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import org.aopalliance.intercept.MethodInterceptor;
import java.util.List;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import com.google.inject.Binding;

public interface ConstructorBinding<T> extends Binding<T>, HasDependencies
{
    InjectionPoint getConstructor();
    
    Set<InjectionPoint> getInjectableMembers();
    
    Map<Method, List<MethodInterceptor>> getMethodInterceptors();
}
