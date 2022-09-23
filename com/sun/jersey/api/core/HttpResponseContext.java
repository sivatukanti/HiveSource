// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.Response;

public interface HttpResponseContext
{
    Response getResponse();
    
    void setResponse(final Response p0);
    
    boolean isResponseSet();
    
    Throwable getMappedThrowable();
    
    Response.StatusType getStatusType();
    
    void setStatusType(final Response.StatusType p0);
    
    int getStatus();
    
    void setStatus(final int p0);
    
    Object getEntity();
    
    Type getEntityType();
    
    Object getOriginalEntity();
    
    void setEntity(final Object p0);
    
    Annotation[] getAnnotations();
    
    void setAnnotations(final Annotation[] p0);
    
    MultivaluedMap<String, Object> getHttpHeaders();
    
    MediaType getMediaType();
    
    OutputStream getOutputStream() throws IOException;
    
    boolean isCommitted();
}
