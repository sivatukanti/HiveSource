// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import com.sun.jersey.api.client.async.FutureListener;
import com.sun.jersey.client.impl.async.FutureClientResponseListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Cookie;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.async.ITypeListener;
import com.sun.jersey.client.impl.ClientRequestImpl;
import java.util.concurrent.Future;
import javax.ws.rs.core.UriBuilder;
import com.sun.jersey.client.impl.CopyOnWriteHashMap;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import com.sun.jersey.api.client.async.AsyncClientHandler;
import com.sun.jersey.api.client.filter.Filterable;

public class AsyncWebResource extends Filterable implements AsyncClientHandler, RequestBuilder<Builder>, AsyncUniformInterface
{
    private static final Logger LOGGER;
    private final ExecutorService executorService;
    private final URI u;
    private CopyOnWriteHashMap<String, Object> properties;
    
    protected AsyncWebResource(final Client c, final CopyOnWriteHashMap<String, Object> properties, final URI u) {
        super((ClientHandler)c);
        this.executorService = c.getExecutorService();
        this.u = u;
        this.properties = properties.clone();
    }
    
    protected AsyncWebResource(final AsyncWebResource that, final UriBuilder ub) {
        super(that);
        this.executorService = that.executorService;
        this.u = ub.build(new Object[0]);
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
        if (obj instanceof AsyncWebResource) {
            final AsyncWebResource that = (AsyncWebResource)obj;
            return that.u.equals(this.u);
        }
        return false;
    }
    
    @Override
    public Future<ClientResponse> head() {
        return this.handle(ClientResponse.class, new ClientRequestImpl(this.getURI(), "HEAD"));
    }
    
    @Override
    public Future<ClientResponse> head(final ITypeListener<ClientResponse> l) {
        return this.handle(l, new ClientRequestImpl(this.getURI(), "HEAD"));
    }
    
    @Override
    public <T> Future<T> options(final Class<T> c) {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }
    
    @Override
    public <T> Future<T> options(final GenericType<T> gt) {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }
    
    @Override
    public <T> Future<T> options(final ITypeListener<T> l) {
        return this.handle(l, new ClientRequestImpl(this.getURI(), "OPTIONS"));
    }
    
    @Override
    public <T> Future<T> get(final Class<T> c) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "GET"));
    }
    
    @Override
    public <T> Future<T> get(final GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "GET"));
    }
    
    @Override
    public <T> Future<T> get(final ITypeListener<T> l) {
        return this.handle(l, new ClientRequestImpl(this.getURI(), "GET"));
    }
    
    @Override
    public Future<?> put() throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "PUT", null));
    }
    
    @Override
    public Future<?> put(final Object requestEntity) throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }
    
    @Override
    public <T> Future<T> put(final Class<T> c) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "PUT"));
    }
    
    @Override
    public <T> Future<T> put(final GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "PUT"));
    }
    
    @Override
    public <T> Future<T> put(final ITypeListener<T> l) {
        return this.handle(l, new ClientRequestImpl(this.getURI(), "PUT"));
    }
    
    @Override
    public <T> Future<T> put(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }
    
    @Override
    public <T> Future<T> put(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }
    
    @Override
    public <T> Future<T> put(final ITypeListener<T> l, final Object requestEntity) {
        return this.handle(l, new ClientRequestImpl(this.getURI(), "PUT", requestEntity));
    }
    
    @Override
    public Future<?> post() throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "POST"));
    }
    
    @Override
    public Future<?> post(final Object requestEntity) throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }
    
    @Override
    public <T> Future<T> post(final Class<T> c) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "POST"));
    }
    
    @Override
    public <T> Future<T> post(final GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "POST"));
    }
    
    @Override
    public <T> Future<T> post(final ITypeListener<T> l) {
        return this.handle(l, new ClientRequestImpl(this.getURI(), "POST"));
    }
    
    @Override
    public <T> Future<T> post(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }
    
    @Override
    public <T> Future<T> post(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }
    
    @Override
    public <T> Future<T> post(final ITypeListener<T> l, final Object requestEntity) {
        return this.handle(l, new ClientRequestImpl(this.getURI(), "POST", requestEntity));
    }
    
    @Override
    public Future<?> delete() throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "DELETE"));
    }
    
    @Override
    public Future<?> delete(final Object requestEntity) throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }
    
    @Override
    public <T> Future<T> delete(final Class<T> c) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "DELETE"));
    }
    
    @Override
    public <T> Future<T> delete(final GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "DELETE"));
    }
    
    @Override
    public <T> Future<T> delete(final ITypeListener<T> l) {
        return this.handle(l, new ClientRequestImpl(this.getURI(), "DELETE"));
    }
    
    @Override
    public <T> Future<T> delete(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }
    
    @Override
    public <T> Future<T> delete(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }
    
    @Override
    public <T> Future<T> delete(final ITypeListener<T> l, final Object requestEntity) {
        return this.handle(l, new ClientRequestImpl(this.getURI(), "DELETE", requestEntity));
    }
    
    @Override
    public Future<?> method(final String method) throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), method));
    }
    
    @Override
    public Future<?> method(final String method, final Object requestEntity) throws UniformInterfaceException {
        return this.voidHandle(new ClientRequestImpl(this.getURI(), method, requestEntity));
    }
    
    @Override
    public <T> Future<T> method(final String method, final Class<T> c) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), method));
    }
    
    @Override
    public <T> Future<T> method(final String method, final GenericType<T> gt) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), method));
    }
    
    @Override
    public <T> Future<T> method(final String method, final ITypeListener<T> l) {
        return this.handle(l, new ClientRequestImpl(this.getURI(), method));
    }
    
    @Override
    public <T> Future<T> method(final String method, final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(c, new ClientRequestImpl(this.getURI(), method, requestEntity));
    }
    
    @Override
    public <T> Future<T> method(final String method, final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
        return this.handle(gt, new ClientRequestImpl(this.getURI(), method, requestEntity));
    }
    
    @Override
    public <T> Future<T> method(final String method, final ITypeListener<T> l, final Object requestEntity) {
        return this.handle(l, new ClientRequestImpl(this.getURI(), method, requestEntity));
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
    
    public AsyncWebResource path(final String path) {
        return new AsyncWebResource(this, this.getUriBuilder().path(path));
    }
    
    public AsyncWebResource uri(final URI uri) {
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
        return new AsyncWebResource(this, b);
    }
    
    public AsyncWebResource queryParam(final String key, final String value) {
        final UriBuilder b = this.getUriBuilder();
        b.queryParam(key, value);
        return new AsyncWebResource(this, b);
    }
    
    public AsyncWebResource queryParams(final MultivaluedMap<String, String> params) {
        final UriBuilder b = this.getUriBuilder();
        for (final Map.Entry<String, List<String>> e : params.entrySet()) {
            for (final String value : e.getValue()) {
                b.queryParam(e.getKey(), value);
            }
        }
        return new AsyncWebResource(this, b);
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
    
    private <T> Future<T> handle(final Class<T> c, final ClientRequest request) {
        this.setProperties(request);
        final FutureClientResponseListener<T> ftw = new FutureClientResponseListener<T>() {
            @Override
            protected T get(final ClientResponse response) {
                if (c == ClientResponse.class) {
                    return c.cast(response);
                }
                if (response.getStatus() < 300) {
                    return response.getEntity(c);
                }
                throw new UniformInterfaceException(response, request.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
            }
        };
        ftw.setCancelableFuture(this.handle(request, ftw));
        return ftw;
    }
    
    private <T> Future<T> handle(final GenericType<T> gt, final ClientRequest request) {
        this.setProperties(request);
        final FutureClientResponseListener<T> ftw = new FutureClientResponseListener<T>() {
            @Override
            protected T get(final ClientResponse response) {
                if (gt.getRawClass() == ClientResponse.class) {
                    return gt.getRawClass().cast(response);
                }
                if (response.getStatus() < 300) {
                    return response.getEntity(gt);
                }
                throw new UniformInterfaceException(response, request.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
            }
        };
        ftw.setCancelableFuture(this.handle(request, ftw));
        return ftw;
    }
    
    private <T> Future<T> handle(final ITypeListener<T> l, final ClientRequest request) {
        this.setProperties(request);
        final FutureClientResponseListener<T> ftw = new FutureClientResponseListener<T>() {
            @Override
            protected void done() {
                try {
                    l.onComplete(this);
                }
                catch (Throwable t) {
                    AsyncWebResource.LOGGER.log(Level.SEVERE, "Throwable caught on call to ITypeListener.onComplete", t);
                }
            }
            
            @Override
            protected T get(final ClientResponse response) {
                if (l.getType() == ClientResponse.class) {
                    return (T)response;
                }
                if (response.getStatus() >= 300) {
                    throw new UniformInterfaceException(response, request.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
                }
                if (l.getGenericType() == null) {
                    return response.getEntity(l.getType());
                }
                return response.getEntity(l.getGenericType());
            }
        };
        ftw.setCancelableFuture(this.handle(request, ftw));
        return ftw;
    }
    
    private Future<?> voidHandle(final ClientRequest request) {
        this.setProperties(request);
        final FutureClientResponseListener<?> ftw = (FutureClientResponseListener<?>)new FutureClientResponseListener() {
            @Override
            protected Object get(final ClientResponse response) {
                if (response.getStatus() >= 300) {
                    throw new UniformInterfaceException(response, request.getPropertyAsFeature("com.sun.jersey.client.property.bufferResponseEntityOnException", true));
                }
                response.close();
                return null;
            }
        };
        ftw.setCancelableFuture(this.handle(request, ftw));
        return ftw;
    }
    
    @Override
    public Future<ClientResponse> handle(final ClientRequest request, final FutureListener<ClientResponse> l) {
        this.setProperties(request);
        final Callable<ClientResponse> c = new Callable<ClientResponse>() {
            @Override
            public ClientResponse call() throws Exception {
                return AsyncWebResource.this.getHeadHandler().handle(request);
            }
        };
        final FutureTask<ClientResponse> ft = new FutureTask<ClientResponse>(c) {
            @Override
            protected void done() {
                try {
                    l.onComplete(this);
                }
                catch (Throwable t) {
                    AsyncWebResource.LOGGER.log(Level.SEVERE, "Throwable caught on call to ClientResponseListener.onComplete", t);
                }
            }
        };
        this.executorService.submit(ft);
        return ft;
    }
    
    static {
        LOGGER = Logger.getLogger(AsyncWebResource.class.getName());
    }
    
    public final class Builder extends PartialRequestBuilder<Builder> implements AsyncUniformInterface
    {
        private Builder() {
        }
        
        private ClientRequest build(final String method) {
            final ClientRequest ro = new ClientRequestImpl(AsyncWebResource.this.u, method, this.entity, this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }
        
        private ClientRequest build(final String method, final Object e) {
            final ClientRequest ro = new ClientRequestImpl(AsyncWebResource.this.u, method, e, this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }
        
        @Override
        public Future<ClientResponse> head() {
            return (Future<ClientResponse>)AsyncWebResource.this.handle((Class<Object>)ClientResponse.class, this.build("HEAD"));
        }
        
        @Override
        public Future<ClientResponse> head(final ITypeListener<ClientResponse> l) {
            return (Future<ClientResponse>)AsyncWebResource.this.handle((ITypeListener<Object>)l, this.build("HEAD"));
        }
        
        @Override
        public <T> Future<T> options(final Class<T> c) {
            return (Future<T>)AsyncWebResource.this.handle((Class<Object>)c, this.build("OPTIONS"));
        }
        
        @Override
        public <T> Future<T> options(final GenericType<T> gt) {
            return (Future<T>)AsyncWebResource.this.handle((GenericType<Object>)gt, this.build("OPTIONS"));
        }
        
        @Override
        public <T> Future<T> options(final ITypeListener<T> l) {
            return (Future<T>)AsyncWebResource.this.handle((ITypeListener<Object>)l, this.build("OPTIONS"));
        }
        
        @Override
        public <T> Future<T> get(final Class<T> c) {
            return (Future<T>)AsyncWebResource.this.handle((Class<Object>)c, this.build("GET"));
        }
        
        @Override
        public <T> Future<T> get(final GenericType<T> gt) {
            return (Future<T>)AsyncWebResource.this.handle((GenericType<Object>)gt, this.build("GET"));
        }
        
        @Override
        public <T> Future<T> get(final ITypeListener<T> l) {
            return (Future<T>)AsyncWebResource.this.handle((ITypeListener<Object>)l, this.build("GET"));
        }
        
        @Override
        public Future<?> put() throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("PUT"));
        }
        
        @Override
        public Future<?> put(final Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("PUT", requestEntity));
        }
        
        @Override
        public <T> Future<T> put(final Class<T> c) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((Class<Object>)c, this.build("PUT"));
        }
        
        @Override
        public <T> Future<T> put(final GenericType<T> gt) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((GenericType<Object>)gt, this.build("PUT"));
        }
        
        @Override
        public <T> Future<T> put(final ITypeListener<T> l) {
            return (Future<T>)AsyncWebResource.this.handle((ITypeListener<Object>)l, this.build("PUT"));
        }
        
        @Override
        public <T> Future<T> put(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((Class<Object>)c, this.build("PUT", requestEntity));
        }
        
        @Override
        public <T> Future<T> put(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((GenericType<Object>)gt, this.build("PUT", requestEntity));
        }
        
        @Override
        public <T> Future<T> put(final ITypeListener<T> l, final Object requestEntity) {
            return (Future<T>)AsyncWebResource.this.handle((ITypeListener<Object>)l, this.build("PUT", requestEntity));
        }
        
        @Override
        public Future<?> post() throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("POST"));
        }
        
        @Override
        public Future<?> post(final Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("POST", requestEntity));
        }
        
        @Override
        public <T> Future<T> post(final Class<T> c) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((Class<Object>)c, this.build("POST"));
        }
        
        @Override
        public <T> Future<T> post(final GenericType<T> gt) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((GenericType<Object>)gt, this.build("POST"));
        }
        
        @Override
        public <T> Future<T> post(final ITypeListener<T> l) {
            return (Future<T>)AsyncWebResource.this.handle((ITypeListener<Object>)l, this.build("POST"));
        }
        
        @Override
        public <T> Future<T> post(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((Class<Object>)c, this.build("POST", requestEntity));
        }
        
        @Override
        public <T> Future<T> post(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((GenericType<Object>)gt, this.build("POST", requestEntity));
        }
        
        @Override
        public <T> Future<T> post(final ITypeListener<T> l, final Object requestEntity) {
            return (Future<T>)AsyncWebResource.this.handle((ITypeListener<Object>)l, this.build("POST", requestEntity));
        }
        
        @Override
        public Future<?> delete() throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("DELETE"));
        }
        
        @Override
        public Future<?> delete(final Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build("DELETE", requestEntity));
        }
        
        @Override
        public <T> Future<T> delete(final Class<T> c) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((Class<Object>)c, this.build("DELETE"));
        }
        
        @Override
        public <T> Future<T> delete(final GenericType<T> gt) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((GenericType<Object>)gt, this.build("DELETE"));
        }
        
        @Override
        public <T> Future<T> delete(final ITypeListener<T> l) {
            return (Future<T>)AsyncWebResource.this.handle((ITypeListener<Object>)l, this.build("DELETE"));
        }
        
        @Override
        public <T> Future<T> delete(final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((Class<Object>)c, this.build("DELETE", requestEntity));
        }
        
        @Override
        public <T> Future<T> delete(final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((GenericType<Object>)gt, this.build("DELETE", requestEntity));
        }
        
        @Override
        public <T> Future<T> delete(final ITypeListener<T> l, final Object requestEntity) {
            return (Future<T>)AsyncWebResource.this.handle((ITypeListener<Object>)l, this.build("DELETE", requestEntity));
        }
        
        @Override
        public Future<?> method(final String method) throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build(method));
        }
        
        @Override
        public Future<?> method(final String method, final Object requestEntity) throws UniformInterfaceException {
            return AsyncWebResource.this.voidHandle(this.build(method, requestEntity));
        }
        
        @Override
        public <T> Future<T> method(final String method, final Class<T> c) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((Class<Object>)c, this.build(method));
        }
        
        @Override
        public <T> Future<T> method(final String method, final GenericType<T> gt) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((GenericType<Object>)gt, this.build(method));
        }
        
        @Override
        public <T> Future<T> method(final String method, final ITypeListener<T> l) {
            return (Future<T>)AsyncWebResource.this.handle((ITypeListener<Object>)l, this.build(method));
        }
        
        @Override
        public <T> Future<T> method(final String method, final Class<T> c, final Object requestEntity) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((Class<Object>)c, this.build(method, requestEntity));
        }
        
        @Override
        public <T> Future<T> method(final String method, final GenericType<T> gt, final Object requestEntity) throws UniformInterfaceException {
            return (Future<T>)AsyncWebResource.this.handle((GenericType<Object>)gt, this.build(method, requestEntity));
        }
        
        @Override
        public <T> Future<T> method(final String method, final ITypeListener<T> l, final Object requestEntity) {
            return (Future<T>)AsyncWebResource.this.handle((ITypeListener<Object>)l, this.build(method, requestEntity));
        }
    }
}
