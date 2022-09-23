// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.async;

import com.sun.jersey.api.client.GenericType;

public abstract class TypeListener<T> implements ITypeListener<T>
{
    private final Class<T> type;
    private final GenericType<T> genericType;
    
    public TypeListener(final Class<T> type) {
        this.type = type;
        this.genericType = null;
    }
    
    public TypeListener(final GenericType<T> genericType) {
        this.type = genericType.getRawClass();
        this.genericType = genericType;
    }
    
    @Override
    public Class<T> getType() {
        return this.type;
    }
    
    @Override
    public GenericType<T> getGenericType() {
        return this.genericType;
    }
}
