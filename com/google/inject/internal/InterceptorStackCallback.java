// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.lang.reflect.AccessibleObject;
import org.aopalliance.intercept.MethodInvocation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.cglib.proxy.$MethodProxy;
import java.util.List;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import java.util.Set;
import com.google.inject.internal.cglib.proxy.$MethodInterceptor;

final class InterceptorStackCallback implements $MethodInterceptor
{
    private static final Set<String> AOP_INTERNAL_CLASSES;
    final MethodInterceptor[] interceptors;
    final Method method;
    
    public InterceptorStackCallback(final Method method, final List<MethodInterceptor> interceptors) {
        this.method = method;
        this.interceptors = interceptors.toArray(new MethodInterceptor[interceptors.size()]);
    }
    
    public Object intercept(final Object proxy, final Method method, final Object[] arguments, final $MethodProxy methodProxy) throws Throwable {
        return new InterceptedMethodInvocation(proxy, methodProxy, arguments).proceed();
    }
    
    private void pruneStacktrace(final Throwable throwable) {
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            final StackTraceElement[] stackTrace = t.getStackTrace();
            final List<StackTraceElement> pruned = (List<StackTraceElement>)$Lists.newArrayList();
            for (final StackTraceElement element : stackTrace) {
                final String className = element.getClassName();
                if (!InterceptorStackCallback.AOP_INTERNAL_CLASSES.contains(className) && !className.contains("$EnhancerByGuice$")) {
                    pruned.add(element);
                }
            }
            t.setStackTrace(pruned.toArray(new StackTraceElement[pruned.size()]));
        }
    }
    
    static {
        AOP_INTERNAL_CLASSES = new HashSet<String>(Arrays.asList(InterceptorStackCallback.class.getName(), InterceptedMethodInvocation.class.getName(), $MethodProxy.class.getName()));
    }
    
    private class InterceptedMethodInvocation implements MethodInvocation
    {
        final Object proxy;
        final Object[] arguments;
        final $MethodProxy methodProxy;
        int index;
        
        public InterceptedMethodInvocation(final Object proxy, final $MethodProxy methodProxy, final Object[] arguments) {
            this.index = -1;
            this.proxy = proxy;
            this.methodProxy = methodProxy;
            this.arguments = arguments;
        }
        
        public Object proceed() throws Throwable {
            try {
                ++this.index;
                return (this.index == InterceptorStackCallback.this.interceptors.length) ? this.methodProxy.invokeSuper(this.proxy, this.arguments) : InterceptorStackCallback.this.interceptors[this.index].invoke(this);
            }
            catch (Throwable t) {
                InterceptorStackCallback.this.pruneStacktrace(t);
                throw t;
            }
            finally {
                --this.index;
            }
        }
        
        public Method getMethod() {
            return InterceptorStackCallback.this.method;
        }
        
        public Object[] getArguments() {
            return this.arguments;
        }
        
        public Object getThis() {
            return this.proxy;
        }
        
        public AccessibleObject getStaticPart() {
            return this.getMethod();
        }
    }
}
