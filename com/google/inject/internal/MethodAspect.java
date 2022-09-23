// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.util.Arrays;
import com.google.inject.internal.util.$Preconditions;
import org.aopalliance.intercept.MethodInterceptor;
import java.util.List;
import java.lang.reflect.Method;
import com.google.inject.matcher.Matcher;

final class MethodAspect
{
    private final Matcher<? super Class<?>> classMatcher;
    private final Matcher<? super Method> methodMatcher;
    private final List<MethodInterceptor> interceptors;
    
    MethodAspect(final Matcher<? super Class<?>> classMatcher, final Matcher<? super Method> methodMatcher, final List<MethodInterceptor> interceptors) {
        this.classMatcher = $Preconditions.checkNotNull(classMatcher, (Object)"class matcher");
        this.methodMatcher = $Preconditions.checkNotNull(methodMatcher, (Object)"method matcher");
        this.interceptors = $Preconditions.checkNotNull(interceptors, (Object)"interceptors");
    }
    
    MethodAspect(final Matcher<? super Class<?>> classMatcher, final Matcher<? super Method> methodMatcher, final MethodInterceptor... interceptors) {
        this(classMatcher, methodMatcher, Arrays.asList(interceptors));
    }
    
    boolean matches(final Class<?> clazz) {
        return this.classMatcher.matches(clazz);
    }
    
    boolean matches(final Method method) {
        return this.methodMatcher.matches(method);
    }
    
    List<MethodInterceptor> interceptors() {
        return this.interceptors;
    }
}
