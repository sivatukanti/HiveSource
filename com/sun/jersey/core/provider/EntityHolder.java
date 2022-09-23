// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.provider;

public class EntityHolder<T>
{
    private final T t;
    
    public EntityHolder() {
        this.t = null;
    }
    
    public EntityHolder(final T t) {
        this.t = t;
    }
    
    public boolean hasEntity() {
        return this.t != null;
    }
    
    public T getEntity() {
        return this.t;
    }
}
