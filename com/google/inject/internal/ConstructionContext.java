// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.util.Iterator;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

final class ConstructionContext<T>
{
    T currentReference;
    boolean constructing;
    List<DelegatingInvocationHandler<T>> invocationHandlers;
    
    public T getCurrentReference() {
        return this.currentReference;
    }
    
    public void removeCurrentReference() {
        this.currentReference = null;
    }
    
    public void setCurrentReference(final T currentReference) {
        this.currentReference = currentReference;
    }
    
    public boolean isConstructing() {
        return this.constructing;
    }
    
    public void startConstruction() {
        this.constructing = true;
    }
    
    public void finishConstruction() {
        this.constructing = false;
        this.invocationHandlers = null;
    }
    
    public Object createProxy(final Errors errors, final Class<?> expectedType) throws ErrorsException {
        if (!expectedType.isInterface()) {
            throw errors.cannotSatisfyCircularDependency(expectedType).toException();
        }
        if (this.invocationHandlers == null) {
            this.invocationHandlers = new ArrayList<DelegatingInvocationHandler<T>>();
        }
        final DelegatingInvocationHandler<T> invocationHandler = new DelegatingInvocationHandler<T>();
        this.invocationHandlers.add(invocationHandler);
        final ClassLoader classLoader = BytecodeGen.getClassLoader(expectedType);
        return expectedType.cast(Proxy.newProxyInstance(classLoader, new Class[] { expectedType, CircularDependencyProxy.class }, invocationHandler));
    }
    
    public void setProxyDelegates(final T delegate) {
        if (this.invocationHandlers != null) {
            for (final DelegatingInvocationHandler<T> handler : this.invocationHandlers) {
                handler.setDelegate(delegate);
            }
        }
    }
}
