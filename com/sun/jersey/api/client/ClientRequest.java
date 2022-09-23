// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import com.sun.jersey.client.impl.ClientRequestImpl;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.Map;
import javax.ws.rs.ext.RuntimeDelegate;

public abstract class ClientRequest
{
    private static final RuntimeDelegate rd;
    
    public abstract Map<String, Object> getProperties();
    
    public abstract void setProperties(final Map<String, Object> p0);
    
    public boolean getPropertyAsFeature(final String name) {
        return this.getPropertyAsFeature(name, false);
    }
    
    public boolean getPropertyAsFeature(final String name, final boolean defaultValue) {
        final Boolean v = this.getProperties().get(name);
        return (v != null) ? v : defaultValue;
    }
    
    public abstract URI getURI();
    
    public abstract void setURI(final URI p0);
    
    public abstract String getMethod();
    
    public abstract void setMethod(final String p0);
    
    public abstract Object getEntity();
    
    public abstract void setEntity(final Object p0);
    
    @Deprecated
    public abstract MultivaluedMap<String, Object> getMetadata();
    
    public abstract MultivaluedMap<String, Object> getHeaders();
    
    public abstract ClientRequestAdapter getAdapter();
    
    public abstract void setAdapter(final ClientRequestAdapter p0);
    
    public abstract ClientRequest clone();
    
    public static final Builder create() {
        return new Builder();
    }
    
    public static String getHeaderValue(final Object headerValue) {
        final RuntimeDelegate.HeaderDelegate hp = ClientRequest.rd.createHeaderDelegate(headerValue.getClass());
        return (hp != null) ? hp.toString(headerValue) : headerValue.toString();
    }
    
    static {
        rd = RuntimeDelegate.getInstance();
    }
    
    public static final class Builder extends PartialRequestBuilder<Builder>
    {
        public ClientRequest build(final URI uri, final String method) {
            final ClientRequest ro = new ClientRequestImpl(uri, method, this.entity, this.metadata);
            this.entity = null;
            this.metadata = null;
            return ro;
        }
    }
}
