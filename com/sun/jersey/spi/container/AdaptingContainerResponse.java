// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import java.io.OutputStream;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.spi.MessageBodyWorkers;
import java.io.IOException;

public class AdaptingContainerResponse extends ContainerResponse
{
    protected final ContainerResponse acr;
    
    protected AdaptingContainerResponse(final ContainerResponse acr) {
        super(acr);
        this.acr = acr;
    }
    
    @Override
    public void write() throws IOException {
        this.acr.write();
    }
    
    @Override
    public void reset() {
        this.acr.reset();
    }
    
    @Override
    public ContainerRequest getContainerRequest() {
        return this.acr.getContainerRequest();
    }
    
    @Override
    public void setContainerRequest(final ContainerRequest request) {
        this.acr.setContainerRequest(request);
    }
    
    @Override
    public ContainerResponseWriter getContainerResponseWriter() {
        return this.acr.getContainerResponseWriter();
    }
    
    @Override
    public void setContainerResponseWriter(final ContainerResponseWriter responseWriter) {
        this.acr.setContainerResponseWriter(responseWriter);
    }
    
    @Override
    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.acr.getMessageBodyWorkers();
    }
    
    @Override
    public void mapMappableContainerException(final MappableContainerException e) {
        this.acr.mapMappableContainerException(e);
    }
    
    @Override
    public void mapWebApplicationException(final WebApplicationException e) {
        this.acr.mapWebApplicationException(e);
    }
    
    @Override
    public boolean mapException(final Throwable e) {
        return this.acr.mapException(e);
    }
    
    @Override
    public Response getResponse() {
        return this.acr.getResponse();
    }
    
    @Override
    public void setResponse(final Response response) {
        this.acr.setResponse(response);
    }
    
    @Override
    public boolean isResponseSet() {
        return this.acr.isResponseSet();
    }
    
    @Override
    public Throwable getMappedThrowable() {
        return this.acr.getMappedThrowable();
    }
    
    @Override
    public Response.StatusType getStatusType() {
        return this.acr.getStatusType();
    }
    
    @Override
    public void setStatusType(final Response.StatusType statusType) {
        this.acr.setStatusType(statusType);
    }
    
    @Override
    public int getStatus() {
        return this.acr.getStatus();
    }
    
    @Override
    public void setStatus(final int status) {
        this.acr.setStatus(status);
    }
    
    @Override
    public Object getEntity() {
        return this.acr.getEntity();
    }
    
    @Override
    public Type getEntityType() {
        return this.acr.getEntityType();
    }
    
    @Override
    public Object getOriginalEntity() {
        return this.acr.getOriginalEntity();
    }
    
    @Override
    public void setEntity(final Object entity) {
        this.acr.setEntity(entity);
    }
    
    @Override
    public void setEntity(final Object entity, final Type entityType) {
        this.acr.setEntity(entity, entityType);
    }
    
    @Override
    public Annotation[] getAnnotations() {
        return this.acr.getAnnotations();
    }
    
    @Override
    public void setAnnotations(final Annotation[] annotations) {
        this.acr.setAnnotations(annotations);
    }
    
    @Override
    public MultivaluedMap<String, Object> getHttpHeaders() {
        return this.acr.getHttpHeaders();
    }
    
    @Override
    public MediaType getMediaType() {
        return this.acr.getMediaType();
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.acr.getOutputStream();
    }
    
    @Override
    public boolean isCommitted() {
        return this.acr.isCommitted();
    }
}
