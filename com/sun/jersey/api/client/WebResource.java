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
import com.sun.jersey.client.impl.CopyOnWriteHashMap;
import java.net.URI;
import com.sun.jersey.api.client.filter.Filterable;

public class WebResource extends Filterable implements RequestBuilder<Builder>, UniformInterface
{
    private final URI u;
    private CopyOnWriteHashMap<String, Object> properties;
    
    WebResource(final ClientHandler c, final CopyOnWriteHashMap<String, Object> properties, final URI u) {
        super(c);
        this.u = u;
        this.properties = properties.clone();
    }
    
    private WebResource(final WebResource that, final UriBuilder ub) {
        super(that);
        this.u = ub.build(new Object[0]);
        this.properties = ((that.properties == null) ? null : that.properties.clone());
    }
    
    public URI getURI() {
        return this.u;
    }
    
    @Deprecated
    public UriBuilder getBuilder() {
        return UriBuilder.fromUri(this.u);
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
        if (obj instanceof WebResource) {
            final WebResource that = (WebResource)obj;
            return that.u.equals(this.u);
        }
        return false;
    }
    
    @Override
    public ClientResponse head() {
        return this.getHeadHandler().handle(new ClientRequestImpl(this.getURI(), "HEAD"));
    }
    
    @Override
    public <T> T options(final Class<T> c) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }
    
    @Override
    public <T> T options(final GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }
    
    @Override
    public <T> T get(final Class<T> c) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "GET"));
    }
    
    @Override
    public <T> T get(final GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "GET"));
    }
    
    @Override
    public void put() throws UniformInterfaceException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "PUT", null));
    }
    
    @Override
    public void put(final Object requestEntity) throws UniformInterfaceException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }
    
    @Override
    public <T> T put(final Class<T> c) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "PUT"));
    }
    
    @Override
    public <T> T put(final GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "PUT"));
    }
    
    @Override
    public <T> T put(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }
    
    @Override
    public <T> T put(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }
    
    @Override
    public void post() throws UniformInterfaceException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "POST"));
    }
    
    @Override
    public void post(final Object requestEntity) throws UniformInterfaceException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }
    
    @Override
    public <T> T post(final Class<T> c) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "POST"));
    }
    
    @Override
    public <T> T post(final GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "POST"));
    }
    
    @Override
    public <T> T post(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }
    
    @Override
    public <T> T post(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }
    
    @Override
    public void delete() throws UniformInterfaceException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "DELETE"));
    }
    
    @Override
    public void delete(final Object requestEntity) throws UniformInterfaceException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }
    
    @Override
    public <T> T delete(final Class<T> c) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "DELETE"));
    }
    
    @Override
    public <T> T delete(final GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "DELETE"));
    }
    
    @Override
    public <T> T delete(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }
    
    @Override
    public <T> T delete(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }
    
    @Override
    public void method(final String method) throws UniformInterfaceException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), method));
    }
    
    @Override
    public void method(final String method, final Object requestEntity) throws UniformInterfaceException {
        this.voidHandle(new ClientRequestImpl(this.getURI(), method, requestEntity));
    }
    
    @Override
    public <T> T method(final String method, final Class<T> c) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), method));
    }
    
    @Override
    public <T> T method(final String method, final GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), method));
    }
    
    @Override
    public <T> T method(final String method, final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), method, requestEntity));
    }
    
    @Override
    public <T> T method(final String method, final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), method, requestEntity));
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
    
    public WebResource path(final String path) {
        return new WebResource(this, this.getUriBuilder().path(path));
    }
    
    public WebResource uri(final URI uri) {
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
        return new WebResource(this, b);
    }
    
    public WebResource queryParam(final String key, final String value) {
        final UriBuilder b = this.getUriBuilder();
        b.queryParam(key, value);
        return new WebResource(this, b);
    }
    
    public WebResource queryParams(final MultivaluedMap<String, String> params) {
        final UriBuilder b = this.getUriBuilder();
        for (final Map.Entry<String, List<String>> e : params.entrySet()) {
            for (final String value : e.getValue()) {
                b.queryParam(e.getKey(), value);
            }
        }
        return new WebResource(this, b);
    }
    
    public void setProperty(final String property, final Object value) {
        this.getProperties().put(property, value);
    }
    
    public Map<String, Object> getProperties() {
        if (this.properties == null) {
            this.properties = new CopyOnWriteHashMap<String, Object>();
        }
        return this.properties;
    }
    
    private void setProperties(final ClientRequest ro) {
        if (this.properties != null) {
            ro.setProperties(this.properties);
        }
    }
    
    private <T> T handle(final Class<T> c, final ClientRequest ro) throws UniformInterfaceException {
        this.setProperties(ro);
        final ClientResponse r = this.getHeadHandler().handle(ro);
        if (c == ClientResponse.class) {
            return c.cast(r);
        }
        if (r.getStatus() < 300) {
            return r.getEntity(c);
        }
        throw new UniformInterfaceException(r, ro.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
    }
    
    private <T> T handle(final GenericType<T> gt, final ClientRequest ro) throws UniformInterfaceException {
        this.setProperties(ro);
        final ClientResponse r = this.getHeadHandler().handle(ro);
        if (gt.getRawClass() == ClientResponse.class) {
            return gt.getRawClass().cast(r);
        }
        if (r.getStatus() < 300) {
            return r.getEntity(gt);
        }
        throw new UniformInterfaceException(r, ro.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
    }
    
    private void voidHandle(final ClientRequest ro) throws UniformInterfaceException {
        this.setProperties(ro);
        final ClientResponse r = this.getHeadHandler().handle(ro);
        if (r.getStatus() >= 300) {
            throw new UniformInterfaceException(r, ro.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
        }
        r.close();
    }
    
    public final class Builder extends PartialRequestBuilder<Builder> implements UniformInterface
    {
        private Builder() {
        }
        
        private ClientRequest build(final String method) {
            final ClientRequest ro = new ClientRequestImpl(WebResource.this.u, method, this.entity, this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }
        
        private ClientRequest build(final String method, final Object e) {
            final ClientRequest ro = new ClientRequestImpl(WebResource.this.u, method, e, this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }
        
        @Override
        public ClientResponse head() {
            return WebResource.this.getHeadHandler().handle(this.build("HEAD"));
        }
        
        @Override
        public <T> T options(final Class<T> c) throws UniformInterfaceException {
            return (T)WebResource.this.handle((Class<Object>)c, this.build("OPTIONS"));
        }
        
        @Override
        public <T> T options(final GenericType<T> gt) throws UniformInterfaceException {
            return (T)WebResource.this.handle((GenericType<Object>)gt, this.build("OPTIONS"));
        }
        
        @Override
        public <T> T get(final Class<T> c) throws UniformInterfaceException {
            return (T)WebResource.this.handle((Class<Object>)c, this.build("GET"));
        }
        
        @Override
        public <T> T get(final GenericType<T> gt) throws UniformInterfaceException {
            return (T)WebResource.this.handle((GenericType<Object>)gt, this.build("GET"));
        }
        
        @Override
        public void put() throws UniformInterfaceException {
            WebResource.this.voidHandle(this.build("PUT"));
        }
        
        @Override
        public void put(final Object requestEntity) throws UniformInterfaceException {
            WebResource.this.voidHandle(this.build("PUT", requestEntity));
        }
        
        @Override
        public <T> T put(final Class<T> c) throws UniformInterfaceException {
            return (T)WebResource.this.handle((Class<Object>)c, this.build("PUT"));
        }
        
        @Override
        public <T> T put(final GenericType<T> gt) throws UniformInterfaceException {
            return (T)WebResource.this.handle((GenericType<Object>)gt, this.build("PUT"));
        }
        
        @Override
        public <T> T put(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
            return (T)WebResource.this.handle((Class<Object>)c, this.build("PUT", requestEntity));
        }
        
        @Override
        public <T> T put(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
            return (T)WebResource.this.handle((GenericType<Object>)gt, this.build("PUT", requestEntity));
        }
        
        @Override
        public void post() throws UniformInterfaceException {
            WebResource.this.voidHandle(this.build("POST"));
        }
        
        @Override
        public void post(final Object requestEntity) throws UniformInterfaceException {
            WebResource.this.voidHandle(this.build("POST", requestEntity));
        }
        
        @Override
        public <T> T post(final Class<T> c) throws UniformInterfaceException {
            return (T)WebResource.this.handle((Class<Object>)c, this.build("POST"));
        }
        
        @Override
        public <T> T post(final GenericType<T> gt) throws UniformInterfaceException {
            return (T)WebResource.this.handle((GenericType<Object>)gt, this.build("POST"));
        }
        
        @Override
        public <T> T post(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
            return (T)WebResource.this.handle((Class<Object>)c, this.build("POST", requestEntity));
        }
        
        @Override
        public <T> T post(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
            return (T)WebResource.this.handle((GenericType<Object>)gt, this.build("POST", requestEntity));
        }
        
        @Override
        public void delete() throws UniformInterfaceException {
            WebResource.this.voidHandle(this.build("DELETE"));
        }
        
        @Override
        public void delete(final Object requestEntity) throws UniformInterfaceException {
            WebResource.this.voidHandle(this.build("DELETE", requestEntity));
        }
        
        @Override
        public <T> T delete(final Class<T> c) throws UniformInterfaceException {
            return (T)WebResource.this.handle((Class<Object>)c, this.build("DELETE"));
        }
        
        @Override
        public <T> T delete(final GenericType<T> gt) throws UniformInterfaceException {
            return (T)WebResource.this.handle((GenericType<Object>)gt, this.build("DELETE"));
        }
        
        @Override
        public <T> T delete(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
            return (T)WebResource.this.handle((Class<Object>)c, this.build("DELETE", requestEntity));
        }
        
        @Override
        public <T> T delete(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
            return (T)WebResource.this.handle((GenericType<Object>)gt, this.build("DELETE", requestEntity));
        }
        
        @Override
        public void method(final String method) throws UniformInterfaceException {
            WebResource.this.voidHandle(this.build(method));
        }
        
        @Override
        public void method(final String method, final Object requestEntity) throws UniformInterfaceException {
            WebResource.this.voidHandle(this.build(method, requestEntity));
        }
        
        @Override
        public <T> T method(final String method, final Class<T> c) throws UniformInterfaceException {
            return (T)WebResource.this.handle((Class<Object>)c, this.build(method));
        }
        
        @Override
        public <T> T method(final String method, final GenericType<T> gt) throws UniformInterfaceException {
            return (T)WebResource.this.handle((GenericType<Object>)gt, this.build(method));
        }
        
        @Override
        public <T> T method(final String method, final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
            return (T)WebResource.this.handle((Class<Object>)c, this.build(method, requestEntity));
        }
        
        @Override
        public <T> T method(final String method, final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
            return (T)WebResource.this.handle((GenericType<Object>)gt, this.build(method, requestEntity));
        }
    }
}
