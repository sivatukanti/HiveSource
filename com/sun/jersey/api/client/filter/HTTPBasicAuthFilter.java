// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientRequest;
import java.io.UnsupportedEncodingException;
import com.sun.jersey.core.util.Base64;

public final class HTTPBasicAuthFilter extends ClientFilter
{
    private final String authentication;
    
    public HTTPBasicAuthFilter(final String username, final String password) {
        try {
            this.authentication = "Basic " + new String(Base64.encode(username + ":" + password), "ASCII");
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public ClientResponse handle(final ClientRequest cr) throws ClientHandlerException {
        if (!cr.getMetadata().containsKey("Authorization")) {
            cr.getMetadata().add("Authorization", this.authentication);
        }
        return this.getNext().handle(cr);
    }
}
