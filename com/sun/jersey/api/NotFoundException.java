// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api;

import java.net.URI;
import javax.ws.rs.WebApplicationException;

public class NotFoundException extends WebApplicationException
{
    private final URI notFoundUri;
    
    public NotFoundException() {
        this((URI)null);
    }
    
    public NotFoundException(final URI notFoundUri) {
        super(Responses.notFound().build());
        this.notFoundUri = notFoundUri;
    }
    
    public NotFoundException(final String message) {
        this(message, (URI)null);
    }
    
    public NotFoundException(final String message, final URI notFoundUri) {
        super(Responses.notFound().entity(message).type("text/plain").build());
        this.notFoundUri = notFoundUri;
    }
    
    public URI getNotFoundUri() {
        return this.notFoundUri;
    }
    
    @Override
    public String getMessage() {
        return super.getMessage() + " for uri: " + this.notFoundUri;
    }
}
