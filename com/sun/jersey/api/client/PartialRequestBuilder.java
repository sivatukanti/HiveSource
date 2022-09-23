// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import javax.ws.rs.core.Cookie;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.core.header.OutBoundHeaders;
import javax.ws.rs.core.MultivaluedMap;

public abstract class PartialRequestBuilder<T extends RequestBuilder> implements RequestBuilder<T>
{
    protected Object entity;
    protected MultivaluedMap<String, Object> metadata;
    
    protected PartialRequestBuilder() {
        this.metadata = new OutBoundHeaders();
    }
    
    @Override
    public T entity(final Object entity) {
        this.entity = entity;
        return (T)this;
    }
    
    @Override
    public T entity(final Object entity, final MediaType type) {
        this.entity(entity);
        this.type(type);
        return (T)this;
    }
    
    @Override
    public T entity(final Object entity, final String type) {
        this.entity(entity);
        this.type(type);
        return (T)this;
    }
    
    @Override
    public T type(final MediaType type) {
        this.getMetadata().putSingle("Content-Type", type);
        return (T)this;
    }
    
    @Override
    public T type(final String type) {
        this.getMetadata().putSingle("Content-Type", MediaType.valueOf(type));
        return (T)this;
    }
    
    @Override
    public T accept(final MediaType... types) {
        for (final MediaType type : types) {
            this.getMetadata().add("Accept", type);
        }
        return (T)this;
    }
    
    @Override
    public T accept(final String... types) {
        for (final String type : types) {
            this.getMetadata().add("Accept", type);
        }
        return (T)this;
    }
    
    @Override
    public T acceptLanguage(final Locale... locales) {
        for (final Locale locale : locales) {
            this.getMetadata().add("Accept-Language", locale);
        }
        return (T)this;
    }
    
    @Override
    public T acceptLanguage(final String... locales) {
        for (final String locale : locales) {
            this.getMetadata().add("Accept-Language", locale);
        }
        return (T)this;
    }
    
    @Override
    public T cookie(final Cookie cookie) {
        this.getMetadata().add("Cookie", cookie);
        return (T)this;
    }
    
    @Override
    public T header(final String name, final Object value) {
        this.getMetadata().add(name, value);
        return (T)this;
    }
    
    private MultivaluedMap<String, Object> getMetadata() {
        if (this.metadata != null) {
            return this.metadata;
        }
        return this.metadata = new OutBoundHeaders();
    }
}
