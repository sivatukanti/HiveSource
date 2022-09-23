// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs;

import javax.ws.rs.core.Response;

public class WebApplicationException extends RuntimeException
{
    private static final long serialVersionUID = 11660101L;
    private Response response;
    
    public WebApplicationException() {
        this(null, Response.Status.INTERNAL_SERVER_ERROR);
    }
    
    public WebApplicationException(final Response response) {
        this(null, response);
    }
    
    public WebApplicationException(final int status) {
        this(null, status);
    }
    
    public WebApplicationException(final Response.Status status) {
        this(null, status);
    }
    
    public WebApplicationException(final Throwable cause) {
        this(cause, Response.Status.INTERNAL_SERVER_ERROR);
    }
    
    public WebApplicationException(final Throwable cause, final Response response) {
        super(cause);
        if (response == null) {
            this.response = Response.serverError().build();
        }
        else {
            this.response = response;
        }
    }
    
    public WebApplicationException(final Throwable cause, final int status) {
        this(cause, Response.status(status).build());
    }
    
    public WebApplicationException(final Throwable cause, final Response.Status status) {
        this(cause, Response.status(status).build());
    }
    
    public Response getResponse() {
        return this.response;
    }
}
