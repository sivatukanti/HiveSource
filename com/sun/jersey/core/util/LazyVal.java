// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

public abstract class LazyVal<T>
{
    private volatile T val;
    
    public T get() {
        T result = this.val;
        if (result == null) {
            synchronized (this) {
                result = this.val;
                if (result == null) {
                    result = (this.val = this.instance());
                }
            }
        }
        return result;
    }
    
    public void set(final T t) {
        this.val = t;
    }
    
    protected abstract T instance();
}
