// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.JsonMappingException;
import javax.ws.rs.ext.ExceptionMapper;

@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException>
{
    public Response toResponse(final JsonMappingException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).type("text/plain").build();
    }
}
