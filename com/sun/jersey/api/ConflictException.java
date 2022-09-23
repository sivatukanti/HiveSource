// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;

public class ConflictException extends WebApplicationException
{
    public ConflictException() {
        super(Responses.conflict().build());
    }
    
    public ConflictException(final String message) {
        super(Response.status(409).entity(message).type("text/plain").build());
    }
}
