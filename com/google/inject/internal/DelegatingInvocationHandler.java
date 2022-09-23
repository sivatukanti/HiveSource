// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

class DelegatingInvocationHandler<T> implements InvocationHandler
{
    private T delegate;
    
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (this.delegate == null) {
            throw new IllegalStateException("This is a proxy used to support circular references involving constructors. The object we're proxying is not constructed yet. Please wait until after injection has completed to use this object.");
        }
        try {
            return method.invoke(this.delegate, args);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e2) {
            throw new RuntimeException(e2);
        }
        catch (InvocationTargetException e3) {
            throw e3.getTargetException();
        }
    }
    
    public T getDelegate() {
        return this.delegate;
    }
    
    void setDelegate(final T delegate) {
        this.delegate = delegate;
    }
}
