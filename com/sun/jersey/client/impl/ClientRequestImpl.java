// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.client.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import com.sun.jersey.core.header.OutBoundHeaders;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.Map;
import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.api.client.ClientRequest;

public final class ClientRequestImpl extends ClientRequest implements ClientRequestAdapter
{
    private Map<String, Object> properties;
    private URI uri;
    private String method;
    private Object entity;
    private final MultivaluedMap<String, Object> metadata;
    private ClientRequestAdapter adapter;
    
    public ClientRequestImpl(final URI uri, final String method) {
        this(uri, method, null, null);
    }
    
    public ClientRequestImpl(final URI uri, final String method, final Object entity) {
        this(uri, method, entity, null);
    }
    
    public ClientRequestImpl(final URI uri, final String method, final Object entity, final MultivaluedMap<String, Object> metadata) {
        this.uri = uri;
        this.method = method;
        this.entity = entity;
        this.metadata = ((metadata != null) ? metadata : new OutBoundHeaders());
        this.adapter = this;
    }
    
    @Override
    public Map<String, Object> getProperties() {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        return this.properties;
    }
    
    @Override
    public void setProperties(final Map<String, Object> properties) {
        this.properties = properties;
    }
    
    @Override
    public URI getURI() {
        return this.uri;
    }
    
    @Override
    public void setURI(final URI uri) {
        this.uri = uri;
    }
    
    @Override
    public String getMethod() {
        return this.method;
    }
    
    @Override
    public void setMethod(final String method) {
        this.method = method;
    }
    
    @Override
    public Object getEntity() {
        return this.entity;
    }
    
    @Override
    public void setEntity(final Object entity) {
        this.entity = entity;
    }
    
    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return this.getHeaders();
    }
    
    @Override
    public MultivaluedMap<String, Object> getHeaders() {
        return this.metadata;
    }
    
    @Override
    public ClientRequestAdapter getAdapter() {
        return this.adapter;
    }
    
    @Override
    public void setAdapter(final ClientRequestAdapter adapter) {
        this.adapter = ((adapter != null) ? adapter : this);
    }
    
    @Override
    public ClientRequest clone() {
        return new ClientRequestImpl(this.uri, this.method, this.entity, clone(this.metadata));
    }
    
    private static MultivaluedMap<String, Object> clone(final MultivaluedMap<String, Object> md) {
        final MultivaluedMap<String, Object> clone = new OutBoundHeaders();
        for (final Map.Entry<String, List<Object>> e : md.entrySet()) {
            clone.put(e.getKey(), new ArrayList<Object>(e.getValue()));
        }
        return clone;
    }
    
    @Override
    public OutputStream adapt(final ClientRequest request, final OutputStream out) throws IOException {
        return out;
    }
}
