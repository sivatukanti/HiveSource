// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binder;
import java.util.List;
import com.google.inject.internal.util.$Preconditions;
import org.aopalliance.intercept.MethodInterceptor;
import com.google.inject.internal.util.$ImmutableList;
import java.lang.reflect.Method;
import com.google.inject.matcher.Matcher;

public final class InterceptorBinding implements Element
{
    private final Object source;
    private final Matcher<? super Class<?>> classMatcher;
    private final Matcher<? super Method> methodMatcher;
    private final $ImmutableList<MethodInterceptor> interceptors;
    
    InterceptorBinding(final Object source, final Matcher<? super Class<?>> classMatcher, final Matcher<? super Method> methodMatcher, final MethodInterceptor[] interceptors) {
        this.source = $Preconditions.checkNotNull(source, (Object)"source");
        this.classMatcher = $Preconditions.checkNotNull(classMatcher, (Object)"classMatcher");
        this.methodMatcher = $Preconditions.checkNotNull(methodMatcher, (Object)"methodMatcher");
        this.interceptors = $ImmutableList.of(interceptors);
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public Matcher<? super Class<?>> getClassMatcher() {
        return this.classMatcher;
    }
    
    public Matcher<? super Method> getMethodMatcher() {
        return this.methodMatcher;
    }
    
    public List<MethodInterceptor> getInterceptors() {
        return this.interceptors;
    }
    
    public <T> T acceptVisitor(final ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    public void applyTo(final Binder binder) {
        binder.withSource(this.getSource()).bindInterceptor(this.classMatcher, this.methodMatcher, (MethodInterceptor[])this.interceptors.toArray(new MethodInterceptor[this.interceptors.size()]));
    }
}
