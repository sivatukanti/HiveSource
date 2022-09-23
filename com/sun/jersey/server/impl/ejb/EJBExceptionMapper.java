// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.ejb;

import com.sun.jersey.api.container.MappableContainerException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Providers;
import javax.ejb.EJBException;
import javax.ws.rs.ext.ExceptionMapper;

public class EJBExceptionMapper implements ExceptionMapper<EJBException>
{
    private final Providers providers;
    
    public EJBExceptionMapper(@Context final Providers providers) {
        this.providers = providers;
    }
    
    @Override
    public Response toResponse(final EJBException exception) {
        final Exception cause = exception.getCausedByException();
        if (cause != null) {
            final ExceptionMapper mapper = this.providers.getExceptionMapper(cause.getClass());
            if (mapper != null) {
                return mapper.toResponse(cause);
            }
            if (cause instanceof WebApplicationException) {
                return ((WebApplicationException)cause).getResponse();
            }
        }
        throw new MappableContainerException((Throwable)((cause == null) ? exception : cause));
    }
}
