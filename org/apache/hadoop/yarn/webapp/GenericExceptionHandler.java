// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import org.apache.commons.logging.LogFactory;
import javax.xml.bind.UnmarshalException;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.ipc.RemoteException;
import com.sun.jersey.api.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import javax.ws.rs.ext.Provider;
import com.google.inject.Singleton;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.ws.rs.ext.ExceptionMapper;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
@Singleton
@Provider
public class GenericExceptionHandler implements ExceptionMapper<Exception>
{
    public static final Log LOG;
    @Context
    private HttpServletResponse response;
    
    @Override
    public Response toResponse(Exception e) {
        if (GenericExceptionHandler.LOG.isTraceEnabled()) {
            GenericExceptionHandler.LOG.trace("GOT EXCEPITION", e);
        }
        if (e instanceof NotFoundException) {
            return ((NotFoundException)e).getResponse();
        }
        this.response.setContentType(null);
        if (e instanceof RemoteException) {
            e = ((RemoteException)e).unwrapRemoteException();
        }
        Response.Status s;
        if (e instanceof SecurityException) {
            s = Response.Status.UNAUTHORIZED;
        }
        else if (e instanceof AuthorizationException) {
            s = Response.Status.UNAUTHORIZED;
        }
        else if (e instanceof FileNotFoundException) {
            s = Response.Status.NOT_FOUND;
        }
        else if (e instanceof org.apache.hadoop.yarn.webapp.NotFoundException) {
            s = Response.Status.NOT_FOUND;
        }
        else if (e instanceof IOException) {
            s = Response.Status.NOT_FOUND;
        }
        else if (e instanceof ForbiddenException) {
            s = Response.Status.FORBIDDEN;
        }
        else if (e instanceof UnsupportedOperationException) {
            s = Response.Status.BAD_REQUEST;
        }
        else if (e instanceof IllegalArgumentException) {
            s = Response.Status.BAD_REQUEST;
        }
        else if (e instanceof NumberFormatException) {
            s = Response.Status.BAD_REQUEST;
        }
        else if (e instanceof BadRequestException) {
            s = Response.Status.BAD_REQUEST;
        }
        else if (e instanceof WebApplicationException && e.getCause() instanceof UnmarshalException) {
            s = Response.Status.BAD_REQUEST;
        }
        else {
            GenericExceptionHandler.LOG.warn("INTERNAL_SERVER_ERROR", e);
            s = Response.Status.INTERNAL_SERVER_ERROR;
        }
        final RemoteExceptionData exception = new RemoteExceptionData(e.getClass().getSimpleName(), e.getMessage(), e.getClass().getName());
        return Response.status(s).entity(exception).build();
    }
    
    static {
        LOG = LogFactory.getLog(GenericExceptionHandler.class);
    }
}
