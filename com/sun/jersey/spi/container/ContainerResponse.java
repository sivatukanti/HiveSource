// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import com.sun.jersey.core.header.OutBoundHeaders;
import javax.ws.rs.core.GenericEntity;
import com.sun.jersey.core.spi.factory.ResponseImpl;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.logging.Level;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.api.Responses;
import com.sun.jersey.api.core.TraceInformation;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.OutputStream;
import com.sun.jersey.core.reflection.ReflectionHelper;
import javax.ws.rs.WebApplicationException;
import com.sun.jersey.api.MessageException;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.logging.Logger;
import java.lang.annotation.Annotation;
import com.sun.jersey.api.core.HttpResponseContext;

public class ContainerResponse implements HttpResponseContext
{
    private static final Annotation[] EMPTY_ANNOTATIONS;
    private static final Logger LOGGER;
    private static final RuntimeDelegate rd;
    private final WebApplication wa;
    private ContainerRequest request;
    private ContainerResponseWriter responseWriter;
    private Response response;
    private Throwable mappedThrowable;
    private Response.StatusType statusType;
    private MultivaluedMap<String, Object> headers;
    private Object originalEntity;
    private Object entity;
    private Type entityType;
    private boolean isCommitted;
    private CommittingOutputStream out;
    private Annotation[] annotations;
    
    public ContainerResponse(final WebApplication wa, final ContainerRequest request, final ContainerResponseWriter responseWriter) {
        this.annotations = ContainerResponse.EMPTY_ANNOTATIONS;
        this.wa = wa;
        this.request = request;
        this.responseWriter = responseWriter;
        this.statusType = Response.Status.NO_CONTENT;
    }
    
    ContainerResponse(final ContainerResponse acr) {
        this.annotations = ContainerResponse.EMPTY_ANNOTATIONS;
        this.wa = acr.wa;
    }
    
    public static String getHeaderValue(final Object headerValue) {
        final RuntimeDelegate.HeaderDelegate hp = ContainerResponse.rd.createHeaderDelegate(headerValue.getClass());
        return (hp != null) ? hp.toString(headerValue) : headerValue.toString();
    }
    
    public void write() throws IOException {
        if (this.isCommitted) {
            return;
        }
        if (this.request.isTracingEnabled()) {
            this.configureTrace(this.responseWriter);
        }
        if (this.entity == null) {
            this.isCommitted = true;
            this.responseWriter.writeStatusAndHeaders(-1L, this);
            this.responseWriter.finish();
            return;
        }
        if (!this.getHttpHeaders().containsKey("Vary")) {
            final String varyHeader = this.request.getProperties().get("Vary");
            if (varyHeader != null) {
                this.getHttpHeaders().add("Vary", varyHeader);
            }
        }
        MediaType contentType = this.getMediaType();
        if (contentType == null) {
            contentType = this.getMessageBodyWorkers().getMessageBodyWriterMediaType(this.entity.getClass(), this.entityType, this.annotations, this.request.getAcceptableMediaTypes());
            if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
            }
            this.getHttpHeaders().putSingle("Content-Type", contentType);
        }
        final MessageBodyWriter writer = this.getMessageBodyWorkers().getMessageBodyWriter(this.entity.getClass(), this.entityType, this.annotations, contentType);
        if (writer == null) {
            final String message = "A message body writer for Java class " + this.entity.getClass().getName() + ", and Java type " + this.entityType + ", and MIME media type " + contentType + " was not found.\n";
            final Map<MediaType, List<MessageBodyWriter>> m = this.getMessageBodyWorkers().getWriters(contentType);
            ContainerResponse.LOGGER.severe(message + "The registered message body writers compatible with the MIME media type are:\n" + this.getMessageBodyWorkers().writersToString(m));
            if (!this.request.getMethod().equals("HEAD")) {
                throw new WebApplicationException(new MessageException(message), 500);
            }
            this.writeHttpHead(-1L);
        }
        else {
            final long size = writer.getSize(this.entity, this.entity.getClass(), this.entityType, this.annotations, contentType);
            if (this.request.getMethod().equals("HEAD")) {
                this.writeHttpHead(size);
            }
            else {
                if (this.request.isTracingEnabled()) {
                    this.request.trace(String.format("matched message body writer: %s, \"%s\" -> %s", ReflectionHelper.objectToString(this.entity), contentType, ReflectionHelper.objectToString(writer)));
                }
                if (this.out == null) {
                    this.out = new CommittingOutputStream(size);
                }
                writer.writeTo(this.entity, this.entity.getClass(), this.entityType, this.annotations, contentType, this.getHttpHeaders(), this.out);
                if (!this.isCommitted) {
                    this.isCommitted = true;
                    this.responseWriter.writeStatusAndHeaders(-1L, this);
                }
            }
        }
        this.responseWriter.finish();
    }
    
    private void writeHttpHead(final long size) throws IOException {
        if (size != -1L) {
            this.getHttpHeaders().putSingle("Content-Length", Long.toString(size));
        }
        this.isCommitted = true;
        this.responseWriter.writeStatusAndHeaders(size, this);
        if (this.entity instanceof InputStream) {
            ((InputStream)this.entity).close();
        }
    }
    
    private void configureTrace(final ContainerResponseWriter crw) {
        final TraceInformation ti = this.request.getProperties().get(TraceInformation.class.getName());
        this.setContainerResponseWriter(new ContainerResponseWriter() {
            @Override
            public OutputStream writeStatusAndHeaders(final long contentLength, final ContainerResponse response) throws IOException {
                ti.addTraceHeaders();
                return crw.writeStatusAndHeaders(contentLength, response);
            }
            
            @Override
            public void finish() throws IOException {
                crw.finish();
            }
        });
    }
    
    public void reset() {
        this.setResponse(Responses.noContent().build());
    }
    
    public ContainerRequest getContainerRequest() {
        return this.request;
    }
    
    public void setContainerRequest(final ContainerRequest request) {
        this.request = request;
    }
    
    public ContainerResponseWriter getContainerResponseWriter() {
        return this.responseWriter;
    }
    
    public void setContainerResponseWriter(final ContainerResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }
    
    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.wa.getMessageBodyWorkers();
    }
    
    public void mapMappableContainerException(final MappableContainerException e) {
        final Throwable cause = e.getCause();
        if (cause instanceof WebApplicationException) {
            this.mapWebApplicationException((WebApplicationException)cause);
        }
        else if (!this.mapException(cause)) {
            if (cause instanceof RuntimeException) {
                ContainerResponse.LOGGER.log(Level.SEVERE, "The RuntimeException could not be mapped to a response, re-throwing to the HTTP container", cause);
                throw (RuntimeException)cause;
            }
            ContainerResponse.LOGGER.log(Level.SEVERE, "The exception contained within MappableContainerException could not be mapped to a response, re-throwing to the HTTP container", cause);
            throw e;
        }
    }
    
    public void mapWebApplicationException(final WebApplicationException e) {
        if (e.getResponse().getEntity() != null) {
            this.wa.getResponseListener().onError(Thread.currentThread().getId(), e);
            this.onException(e, e.getResponse(), false);
        }
        else if (!this.mapException(e)) {
            this.onException(e, e.getResponse(), false);
        }
    }
    
    public boolean mapException(final Throwable e) {
        final ExceptionMapper em = this.wa.getExceptionMapperContext().find(e.getClass());
        if (em == null) {
            this.wa.getResponseListener().onError(Thread.currentThread().getId(), e);
            return false;
        }
        this.wa.getResponseListener().onMappedException(Thread.currentThread().getId(), e, em);
        if (this.request.isTracingEnabled()) {
            this.request.trace(String.format("matched exception mapper: %s -> %s", ReflectionHelper.objectToString(e), ReflectionHelper.objectToString(em)));
        }
        try {
            Response r = em.toResponse(e);
            if (r == null) {
                r = Response.noContent().build();
            }
            this.onException(e, r, true);
        }
        catch (MappableContainerException ex) {
            throw ex;
        }
        catch (RuntimeException ex2) {
            ContainerResponse.LOGGER.severe("Exception mapper " + em + " for Throwable " + e + " threw a RuntimeException when " + "attempting to obtain the response");
            final Response r2 = Response.serverError().build();
            this.onException(ex2, r2, false);
        }
        return true;
    }
    
    private void onException(final Throwable e, final Response r, final boolean mapped) {
        if (this.request.isTracingEnabled()) {
            final Response.Status s = Response.Status.fromStatusCode(r.getStatus());
            if (s != null) {
                this.request.trace(String.format("mapped exception to response: %s -> %d (%s)", ReflectionHelper.objectToString(e), r.getStatus(), s.getReasonPhrase()));
            }
            else {
                this.request.trace(String.format("mapped exception to response: %s -> %d", ReflectionHelper.objectToString(e), r.getStatus()));
            }
        }
        if (!mapped && r.getStatus() >= 500) {
            this.logException(e, r, Level.SEVERE);
        }
        else if (ContainerResponse.LOGGER.isLoggable(Level.FINE)) {
            this.logException(e, r, Level.FINE);
        }
        this.setResponse(r);
        this.mappedThrowable = e;
        if (this.getEntity() != null && this.getHttpHeaders().getFirst("Content-Type") == null) {
            final Object m = this.request.getProperties().get("com.sun.jersey.server.impl.uri.rules.HttpMethodRule.Content-Type");
            if (m != null) {
                this.request.getProperties().remove("com.sun.jersey.server.impl.uri.rules.HttpMethodRule.Content-Type");
                this.getHttpHeaders().putSingle("Content-Type", m);
            }
        }
    }
    
    private void logException(final Throwable e, final Response r, final Level l) {
        final Response.Status s = Response.Status.fromStatusCode(r.getStatus());
        if (s != null) {
            ContainerResponse.LOGGER.log(l, "Mapped exception to response: " + r.getStatus() + " (" + s.getReasonPhrase() + ")", e);
        }
        else {
            ContainerResponse.LOGGER.log(l, "Mapped exception to response: " + r.getStatus(), e);
        }
    }
    
    @Override
    public Response getResponse() {
        if (this.response == null) {
            this.setResponse(null);
        }
        return this.response;
    }
    
    @Override
    public void setResponse(Response response) {
        this.isCommitted = false;
        this.out = null;
        response = (this.response = ((response != null) ? response : Responses.noContent().build()));
        this.mappedThrowable = null;
        if (response instanceof ResponseImpl) {
            final ResponseImpl responseImpl = (ResponseImpl)response;
            this.setStatusType(responseImpl.getStatusType());
            this.setHeaders(response.getMetadata());
            this.setEntity(responseImpl.getEntity(), responseImpl.getEntityType());
        }
        else {
            this.setStatus(response.getStatus());
            this.setHeaders(response.getMetadata());
            this.setEntity(response.getEntity());
        }
    }
    
    @Override
    public boolean isResponseSet() {
        return this.response != null;
    }
    
    @Override
    public Throwable getMappedThrowable() {
        return this.mappedThrowable;
    }
    
    @Override
    public Response.StatusType getStatusType() {
        return this.statusType;
    }
    
    @Override
    public void setStatusType(final Response.StatusType statusType) {
        this.statusType = statusType;
    }
    
    @Override
    public int getStatus() {
        return this.statusType.getStatusCode();
    }
    
    @Override
    public void setStatus(final int status) {
        this.statusType = ResponseImpl.toStatusType(status);
    }
    
    @Override
    public Object getEntity() {
        return this.entity;
    }
    
    @Override
    public Type getEntityType() {
        return this.entityType;
    }
    
    @Override
    public Object getOriginalEntity() {
        return this.originalEntity;
    }
    
    @Override
    public void setEntity(final Object entity) {
        this.setEntity(entity, (entity == null) ? null : entity.getClass());
    }
    
    public void setEntity(final Object entity, final Type entityType) {
        this.entity = entity;
        this.originalEntity = entity;
        this.entityType = entityType;
        if (this.entity instanceof GenericEntity) {
            final GenericEntity ge = (GenericEntity)this.entity;
            this.entity = ge.getEntity();
            this.entityType = ge.getType();
        }
    }
    
    @Override
    public Annotation[] getAnnotations() {
        return this.annotations;
    }
    
    @Override
    public void setAnnotations(final Annotation[] annotations) {
        this.annotations = ((annotations != null) ? annotations : ContainerResponse.EMPTY_ANNOTATIONS);
    }
    
    @Override
    public MultivaluedMap<String, Object> getHttpHeaders() {
        if (this.headers == null) {
            this.headers = new OutBoundHeaders();
        }
        return this.headers;
    }
    
    @Override
    public MediaType getMediaType() {
        final Object mediaTypeHeader = this.getHttpHeaders().getFirst("Content-Type");
        if (mediaTypeHeader instanceof MediaType) {
            return (MediaType)mediaTypeHeader;
        }
        if (mediaTypeHeader != null) {
            return MediaType.valueOf(mediaTypeHeader.toString());
        }
        return null;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.out == null) {
            this.out = new CommittingOutputStream(-1L);
        }
        return this.out;
    }
    
    @Override
    public boolean isCommitted() {
        return this.isCommitted;
    }
    
    private void setHeaders(final MultivaluedMap<String, Object> headers) {
        this.headers = headers;
        Object location = headers.getFirst("Location");
        if (location != null && location instanceof URI) {
            final URI locationUri = (URI)location;
            if (!locationUri.isAbsolute()) {
                final URI base = (this.statusType.getStatusCode() == Response.Status.CREATED.getStatusCode()) ? this.request.getAbsolutePath() : this.request.getBaseUri();
                location = UriBuilder.fromUri(base).path(locationUri.getRawPath()).replaceQuery(locationUri.getRawQuery()).fragment(locationUri.getRawFragment()).build(new Object[0]);
            }
            headers.putSingle("Location", location);
        }
    }
    
    static {
        EMPTY_ANNOTATIONS = new Annotation[0];
        LOGGER = Logger.getLogger(ContainerResponse.class.getName());
        rd = RuntimeDelegate.getInstance();
    }
    
    private final class CommittingOutputStream extends OutputStream
    {
        private final long size;
        private OutputStream o;
        
        CommittingOutputStream(final long size) {
            this.size = size;
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.commitWrite();
            this.o.write(b);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.commitWrite();
            this.o.write(b, off, len);
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.commitWrite();
            this.o.write(b);
        }
        
        @Override
        public void flush() throws IOException {
            this.commitWrite();
            this.o.flush();
        }
        
        @Override
        public void close() throws IOException {
            this.commitClose();
            this.o.close();
        }
        
        private void commitWrite() throws IOException {
            if (!ContainerResponse.this.isCommitted) {
                if (ContainerResponse.this.getStatus() == 204) {
                    ContainerResponse.this.setStatus(200);
                }
                ContainerResponse.this.isCommitted = true;
                this.o = ContainerResponse.this.responseWriter.writeStatusAndHeaders(this.size, ContainerResponse.this);
            }
        }
        
        private void commitClose() throws IOException {
            if (!ContainerResponse.this.isCommitted) {
                ContainerResponse.this.isCommitted = true;
                this.o = ContainerResponse.this.responseWriter.writeStatusAndHeaders(-1L, ContainerResponse.this);
            }
        }
    }
}
