// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Cookie;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.client.impl.ClientRequestImpl;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import com.sun.jersey.api.client.filter.Filterable;

public class ViewResource extends Filterable implements RequestBuilder<Builder>, ViewUniformInterface
{
    private final Client client;
    private final URI u;
    
    ViewResource(final Client c, final URI u) {
        super((ClientHandler)c);
        this.client = c;
        this.u = u;
    }
    
    private ViewResource(final ViewResource that, final UriBuilder ub) {
        super(that);
        this.client = that.client;
        this.u = ub.build(new Object[0]);
    }
    
    public URI getURI() {
        return this.u;
    }
    
    public UriBuilder getUriBuilder() {
        return UriBuilder.fromUri(this.u);
    }
    
    public Builder getRequestBuilder() {
        return new Builder();
    }
    
    @Override
    public String toString() {
        return this.u.toString();
    }
    
    @Override
    public int hashCode() {
        return this.u.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ViewResource) {
            final ViewResource that = (ViewResource)obj;
            return that.u.equals(this.u);
        }
        return false;
    }
    
    @Override
    public <T> T head(final Class<T> c) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "HEAD"));
    }
    
    @Override
    public <T> T head(final T t) {
        return this.handle(t, new ClientRequestImpl(this.getURI(), "HEAD"));
    }
    
    @Override
    public <T> T options(final Class<T> c) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }
    
    @Override
    public <T> T options(final T t) {
        return this.handle(t, new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }
    
    @Override
    public <T> T get(final Class<T> c) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "GET"));
    }
    
    @Override
    public <T> T get(final T t) {
        return this.handle(t, new ClientRequestImpl(this.getURI(), "GET"));
    }
    
    @Override
    public <T> T put(final Class<T> c) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT"));
    }
    
    @Override
    public <T> T put(final T t) {
        return this.handle(t, new ClientRequestImpl(this.getURI(), "PUT"));
    }
    
    @Override
    public <T> T put(final Class<T> c, final Object requestEntity) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }
    
    @Override
    public <T> T put(final T t, final Object requestEntity) {
        return this.handle(t, new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }
    
    @Override
    public <T> T post(final Class<T> c) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST"));
    }
    
    @Override
    public <T> T post(final T t) {
        return this.handle(t, new ClientRequestImpl(this.getURI(), "POST"));
    }
    
    @Override
    public <T> T post(final Class<T> c, final Object requestEntity) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }
    
    @Override
    public <T> T post(final T t, final Object requestEntity) {
        return this.handle(t, new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }
    
    @Override
    public <T> T delete(final Class<T> c) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE"));
    }
    
    @Override
    public <T> T delete(final T t) {
        return this.handle(t, new ClientRequestImpl(this.getURI(), "DELETE"));
    }
    
    @Override
    public <T> T delete(final Class<T> c, final Object requestEntity) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }
    
    @Override
    public <T> T delete(final T t, final Object requestEntity) {
        return this.handle(t, new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }
    
    @Override
    public <T> T method(final String method, final Class<T> c) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), method));
    }
    
    @Override
    public <T> T method(final String method, final T t) {
        return this.handle(t, new ClientRequestImpl(this.getURI(), method));
    }
    
    @Override
    public <T> T method(final String method, final Class<T> c, final Object requestEntity) {
        return this.handle(c, (ClientRequest)new ClientRequestImpl(this.getURI(), method, requestEntity));
    }
    
    @Override
    public <T> T method(final String method, final T t, final Object requestEntity) {
        return this.handle(t, new ClientRequestImpl(this.getURI(), method, requestEntity));
    }
    
    @Override
    public Builder entity(final Object entity) {
        return this.getRequestBuilder().entity(entity);
    }
    
    @Override
    public Builder entity(final Object entity, final MediaType type) {
        return this.getRequestBuilder().entity(entity, type);
    }
    
    @Override
    public Builder entity(final Object entity, final String type) {
        return this.getRequestBuilder().entity(entity, type);
    }
    
    @Override
    public Builder type(final MediaType type) {
        return this.getRequestBuilder().type(type);
    }
    
    @Override
    public Builder type(final String type) {
        return this.getRequestBuilder().type(type);
    }
    
    @Override
    public Builder accept(final MediaType... types) {
        return this.getRequestBuilder().accept(types);
    }
    
    @Override
    public Builder accept(final String... types) {
        return this.getRequestBuilder().accept(types);
    }
    
    @Override
    public Builder acceptLanguage(final Locale... locales) {
        return this.getRequestBuilder().acceptLanguage(locales);
    }
    
    @Override
    public Builder acceptLanguage(final String... locales) {
        return this.getRequestBuilder().acceptLanguage(locales);
    }
    
    @Override
    public Builder cookie(final Cookie cookie) {
        return this.getRequestBuilder().cookie(cookie);
    }
    
    @Override
    public Builder header(final String name, final Object value) {
        return this.getRequestBuilder().header(name, value);
    }
    
    public ViewResource path(final String path) {
        return new ViewResource(this, this.getUriBuilder().path(path));
    }
    
    public ViewResource uri(final URI uri) {
        final UriBuilder b = this.getUriBuilder();
        final String path = uri.getRawPath();
        if (path != null && path.length() > 0) {
            if (path.startsWith("/")) {
                b.replacePath(path);
            }
            else {
                b.path(path);
            }
        }
        final String query = uri.getRawQuery();
        if (query != null && query.length() > 0) {
            b.replaceQuery(query);
        }
        return new ViewResource(this, b);
    }
    
    public ViewResource queryParam(final String key, final String value) {
        final UriBuilder b = this.getUriBuilder();
        b.queryParam(key, value);
        return new ViewResource(this, b);
    }
    
    public ViewResource queryParams(final MultivaluedMap<String, String> params) {
        final UriBuilder b = this.getUriBuilder();
        for (final Map.Entry<String, List<String>> e : params.entrySet()) {
            for (final String value : e.getValue()) {
                b.queryParam(e.getKey(), value);
            }
        }
        return new ViewResource(this, b);
    }
    
    private <T> T handle(final Class<T> c, final ClientRequest ro) {
        return this.client.getViewProxy(c).view(c, ro, this.getHeadHandler());
    }
    
    private <T> T handle(final T t, final ClientRequest ro) {
        return (T)this.client.getViewProxy(t.getClass()).view(t, ro, this.getHeadHandler());
    }
    
    public final class Builder extends PartialRequestBuilder<Builder> implements ViewUniformInterface
    {
        private Builder() {
        }
        
        private ClientRequest build(final String method) {
            final ClientRequest ro = new ClientRequestImpl(ViewResource.this.u, method, this.entity, this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }
        
        private ClientRequest build(final String method, final Object e) {
            final ClientRequest ro = new ClientRequestImpl(ViewResource.this.u, method, e, this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }
        
        @Override
        public <T> T head(final Class<T> c) {
            return (T)ViewResource.this.handle((Class<Object>)c, this.build("HEAD"));
        }
        
        @Override
        public <T> T head(final T t) {
            return (T)ViewResource.this.handle(t, this.build("HEAD"));
        }
        
        @Override
        public <T> T options(final Class<T> c) {
            return (T)ViewResource.this.handle((Class<Object>)c, this.build("OPTIONS"));
        }
        
        @Override
        public <T> T options(final T t) {
            return (T)ViewResource.this.handle(t, this.build("OPTIONS"));
        }
        
        @Override
        public <T> T get(final Class<T> c) {
            return (T)ViewResource.this.handle((Class<Object>)c, this.build("GET"));
        }
        
        @Override
        public <T> T get(final T t) {
            return (T)ViewResource.this.handle(t, this.build("GET"));
        }
        
        @Override
        public <T> T put(final Class<T> c) {
            return (T)ViewResource.this.handle((Class<Object>)c, this.build("PUT"));
        }
        
        @Override
        public <T> T put(final T t) {
            return (T)ViewResource.this.handle(t, this.build("PUT"));
        }
        
        @Override
        public <T> T put(final Class<T> c, final Object requestEntity) {
            return (T)ViewResource.this.handle((Class<Object>)c, this.build("PUT", requestEntity));
        }
        
        @Override
        public <T> T put(final T t, final Object requestEntity) {
            return (T)ViewResource.this.handle(t, this.build("PUT", requestEntity));
        }
        
        @Override
        public <T> T post(final Class<T> c) {
            return (T)ViewResource.this.handle((Class<Object>)c, this.build("POST"));
        }
        
        @Override
        public <T> T post(final T t) {
            return (T)ViewResource.this.handle(t, this.build("POST"));
        }
        
        @Override
        public <T> T post(final Class<T> c, final Object requestEntity) {
            return (T)ViewResource.this.handle((Class<Object>)c, this.build("POST", requestEntity));
        }
        
        @Override
        public <T> T post(final T t, final Object requestEntity) {
            return (T)ViewResource.this.handle(t, this.build("POST", requestEntity));
        }
        
        @Override
        public <T> T delete(final Class<T> c) {
            return (T)ViewResource.this.handle((Class<Object>)c, this.build("DELETE"));
        }
        
        @Override
        public <T> T delete(final T t) {
            return (T)ViewResource.this.handle(t, this.build("DELETE"));
        }
        
        @Override
        public <T> T delete(final Class<T> c, final Object requestEntity) {
            return (T)ViewResource.this.handle((Class<Object>)c, this.build("DELETE", requestEntity));
        }
        
        @Override
        public <T> T delete(final T t, final Object requestEntity) {
            return (T)ViewResource.this.handle(t, this.build("DELETE", requestEntity));
        }
        
        @Override
        public <T> T method(final String method, final Class<T> c) {
            return (T)ViewResource.this.handle((Class<Object>)c, this.build(method));
        }
        
        @Override
        public <T> T method(final String method, final T t) {
            return (T)ViewResource.this.handle(t, this.build(method));
        }
        
        @Override
        public <T> T method(final String method, final Class<T> c, final Object requestEntity) {
            return (T)ViewResource.this.handle((Class<Object>)c, this.build(method, requestEntity));
        }
        
        @Override
        public <T> T method(final String method, final T t, final Object requestEntity) {
            return (T)ViewResource.this.handle(t, this.build(method, requestEntity));
        }
    }
}
