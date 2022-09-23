// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.factory;

import com.sun.jersey.api.uri.UriBuilderImpl;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.util.Iterator;
import java.util.Date;
import java.net.URI;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.EntityTag;
import com.sun.jersey.spi.service.ServiceFinder;
import java.util.WeakHashMap;
import java.util.HashSet;
import java.util.Map;
import com.sun.jersey.spi.HeaderDelegateProvider;
import java.util.Set;
import javax.ws.rs.ext.RuntimeDelegate;

public abstract class AbstractRuntimeDelegate extends RuntimeDelegate
{
    private final Set<HeaderDelegateProvider> hps;
    private final Map<Class<?>, HeaderDelegate> map;
    
    public AbstractRuntimeDelegate() {
        this.hps = new HashSet<HeaderDelegateProvider>();
        this.map = new WeakHashMap<Class<?>, HeaderDelegate>();
        for (final HeaderDelegateProvider p : ServiceFinder.find(HeaderDelegateProvider.class, true)) {
            this.hps.add(p);
        }
        this.map.put(EntityTag.class, this._createHeaderDelegate(EntityTag.class));
        this.map.put(MediaType.class, this._createHeaderDelegate(MediaType.class));
        this.map.put(CacheControl.class, this._createHeaderDelegate(CacheControl.class));
        this.map.put(NewCookie.class, this._createHeaderDelegate(NewCookie.class));
        this.map.put(Cookie.class, this._createHeaderDelegate(Cookie.class));
        this.map.put(URI.class, this._createHeaderDelegate(URI.class));
        this.map.put(Date.class, this._createHeaderDelegate(Date.class));
        this.map.put(String.class, this._createHeaderDelegate(String.class));
    }
    
    @Override
    public Variant.VariantListBuilder createVariantListBuilder() {
        return new VariantListBuilderImpl();
    }
    
    @Override
    public Response.ResponseBuilder createResponseBuilder() {
        return new ResponseBuilderImpl();
    }
    
    @Override
    public UriBuilder createUriBuilder() {
        return new UriBuilderImpl();
    }
    
    @Override
    public <T> HeaderDelegate<T> createHeaderDelegate(final Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type parameter cannot be null");
        }
        final HeaderDelegate h = this.map.get(type);
        if (h != null) {
            return (HeaderDelegate<T>)h;
        }
        return (HeaderDelegate<T>)this._createHeaderDelegate((Class<Object>)type);
    }
    
    private <T> HeaderDelegate<T> _createHeaderDelegate(final Class<T> type) {
        for (final HeaderDelegateProvider hp : this.hps) {
            if (hp.supports(type)) {
                return (HeaderDelegate<T>)hp;
            }
        }
        return null;
    }
}
