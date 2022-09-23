// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.JsonParseException;
import javax.ws.rs.ext.ExceptionMapper;

@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException>
{
    public Response toResponse(final JsonParseException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).type("text/plain").build();
    }
}
