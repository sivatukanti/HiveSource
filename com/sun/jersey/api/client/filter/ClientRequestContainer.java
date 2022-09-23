// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.core.util.UnmodifiableMultivaluedMap;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import com.sun.jersey.api.client.ClientRequest;

class ClientRequestContainer extends ClientRequest
{
    private ClientRequest request;
    
    ClientRequestContainer(final ClientRequest request) {
        this.request = request;
    }
    
    @Override
    public Map<String, Object> getProperties() {
        if (this.request.getProperties() != null) {
            return Collections.unmodifiableMap((Map<? extends String, ?>)this.request.getProperties());
        }
        return null;
    }
    
    @Override
    public void setProperties(final Map<String, Object> properties) {
        throw new UnsupportedOperationException("Read only instance.");
    }
    
    @Override
    public URI getURI() {
        return this.request.getURI();
    }
    
    @Override
    public void setURI(final URI uri) {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    @Override
    public String getMethod() {
        return this.request.getMethod();
    }
    
    @Override
    public void setMethod(final String method) {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    @Override
    public Object getEntity() {
        return this.request.getEntity();
    }
    
    @Override
    public void setEntity(final Object entity) {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return this.getHeaders();
    }
    
    @Override
    public MultivaluedMap<String, Object> getHeaders() {
        if (this.request.getHeaders() != null) {
            return new UnmodifiableMultivaluedMap<String, Object>(this.request.getHeaders());
        }
        return null;
    }
    
    @Override
    public ClientRequestAdapter getAdapter() {
        return this.request.getAdapter();
    }
    
    @Override
    public void setAdapter(final ClientRequestAdapter adapter) {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    @Override
    public ClientRequest clone() {
        throw new UnsupportedOperationException("Not supported.");
    }
}
