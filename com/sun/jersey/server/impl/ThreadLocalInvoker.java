// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

public class ThreadLocalInvoker<T> implements InvocationHandler
{
    private ThreadLocal<T> threadLocalInstance;
    private ThreadLocal<T> immutableThreadLocalInstance;
    
    public ThreadLocalInvoker() {
        this.threadLocalInstance = new ThreadLocal<T>();
    }
    
    public void set(final T threadLocalInstance) {
        this.threadLocalInstance.set(threadLocalInstance);
    }
    
    public T get() {
        return this.threadLocalInstance.get();
    }
    
    public ThreadLocal<T> getThreadLocal() {
        return this.threadLocalInstance;
    }
    
    public ThreadLocal<T> getImmutableThreadLocal() {
        if (this.immutableThreadLocalInstance == null) {
            this.immutableThreadLocalInstance = new ThreadLocal<T>() {
                @Override
                public T get() {
                    return ThreadLocalInvoker.this.get();
                }
                
                @Override
                public void remove() {
                    throw new IllegalStateException();
                }
                
                @Override
                public void set(final T t) {
                    throw new IllegalStateException();
                }
            };
        }
        return this.immutableThreadLocalInstance;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (this.threadLocalInstance.get() == null) {
            throw new IllegalStateException("No thread local value in scope for proxy of " + proxy.getClass());
        }
        try {
            return method.invoke(this.threadLocalInstance.get(), args);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
        catch (InvocationTargetException ex2) {
            throw ex2.getTargetException();
        }
    }
}
