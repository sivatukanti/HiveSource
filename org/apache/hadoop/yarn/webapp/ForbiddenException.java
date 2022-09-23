// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import javax.ws.rs.core.Response;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.ws.rs.WebApplicationException;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class ForbiddenException extends WebApplicationException
{
    private static final long serialVersionUID = 1L;
    
    public ForbiddenException() {
        super(Response.Status.FORBIDDEN);
    }
    
    public ForbiddenException(final Throwable cause) {
        super(cause, Response.Status.FORBIDDEN);
    }
    
    public ForbiddenException(final String msg) {
        super(new Exception(msg), Response.Status.FORBIDDEN);
    }
}
