// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import javax.ws.rs.core.Response;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.ws.rs.WebApplicationException;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class BadRequestException extends WebApplicationException
{
    private static final long serialVersionUID = 1L;
    
    public BadRequestException() {
        super(Response.Status.BAD_REQUEST);
    }
    
    public BadRequestException(final Throwable cause) {
        super(cause, Response.Status.BAD_REQUEST);
    }
    
    public BadRequestException(final String msg) {
        super(new Exception(msg), Response.Status.BAD_REQUEST);
    }
}
