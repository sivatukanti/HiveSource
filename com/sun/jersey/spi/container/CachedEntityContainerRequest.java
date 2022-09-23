// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class CachedEntityContainerRequest extends AdaptingContainerRequest
{
    Object entity;
    
    public CachedEntityContainerRequest(final ContainerRequest acr) {
        super(acr);
    }
    
    @Override
    public <T> T getEntity(final Class<T> type) throws ClassCastException {
        if (this.entity == null) {
            final T t = this.acr.getEntity(type);
            return (T)(this.entity = t);
        }
        return type.cast(this.entity);
    }
    
    @Override
    public <T> T getEntity(final Class<T> type, final Type genericType, final Annotation[] as) throws ClassCastException {
        if (this.entity == null) {
            final T t = this.acr.getEntity(type, genericType, as);
            return (T)(this.entity = t);
        }
        return type.cast(this.entity);
    }
}
