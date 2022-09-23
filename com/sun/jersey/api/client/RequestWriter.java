// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import java.util.Map;
import java.util.List;
import java.io.OutputStream;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Type;
import java.io.IOException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Context;
import com.sun.jersey.spi.MessageBodyWorkers;
import java.lang.annotation.Annotation;
import java.util.logging.Logger;

public class RequestWriter
{
    private static final Logger LOGGER;
    protected static final Annotation[] EMPTY_ANNOTATIONS;
    private MessageBodyWorkers workers;
    
    public RequestWriter() {
    }
    
    public RequestWriter(final MessageBodyWorkers workers) {
        this.workers = workers;
    }
    
    @Context
    public void setMessageBodyWorkers(final MessageBodyWorkers workers) {
        this.workers = workers;
    }
    
    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.workers;
    }
    
    protected RequestEntityWriter getRequestEntityWriter(final ClientRequest ro) {
        return new RequestEntityWriterImpl(ro);
    }
    
    protected void writeRequestEntity(final ClientRequest ro, final RequestEntityWriterListener listener) throws IOException {
        Object entity = ro.getEntity();
        if (entity == null) {
            return;
        }
        Type entityType = null;
        if (entity instanceof GenericEntity) {
            final GenericEntity ge = (GenericEntity)entity;
            entityType = ge.getType();
            entity = ge.getEntity();
        }
        else {
            entityType = entity.getClass();
        }
        final Class entityClass = entity.getClass();
        final MultivaluedMap<String, Object> headers = ro.getHeaders();
        final MediaType mediaType = this.getMediaType(entityClass, entityType, headers);
        final MessageBodyWriter bw = this.workers.getMessageBodyWriter((Class<Object>)entityClass, entityType, RequestWriter.EMPTY_ANNOTATIONS, mediaType);
        if (bw == null) {
            throw new ClientHandlerException("A message body writer for Java type, " + entity.getClass() + ", and MIME media type, " + mediaType + ", was not found");
        }
        final long size = headers.containsKey("Content-Encoding") ? -1L : bw.getSize(entity, entityClass, entityType, RequestWriter.EMPTY_ANNOTATIONS, mediaType);
        listener.onRequestEntitySize(size);
        final OutputStream out = ro.getAdapter().adapt(ro, listener.onGetOutputStream());
        try {
            bw.writeTo(entity, entityClass, entityType, RequestWriter.EMPTY_ANNOTATIONS, mediaType, headers, out);
            out.flush();
        }
        catch (IOException ex) {
            try {
                out.close();
            }
            catch (Exception ex3) {}
            throw ex;
        }
        catch (RuntimeException ex2) {
            try {
                out.close();
            }
            catch (Exception ex4) {}
            throw ex2;
        }
        out.close();
    }
    
    private MediaType getMediaType(final Class entityClass, final Type entityType, final MultivaluedMap<String, Object> headers) {
        final Object mediaTypeHeader = headers.getFirst("Content-Type");
        if (mediaTypeHeader instanceof MediaType) {
            return (MediaType)mediaTypeHeader;
        }
        if (mediaTypeHeader != null) {
            return MediaType.valueOf(mediaTypeHeader.toString());
        }
        final List<MediaType> mediaTypes = this.workers.getMessageBodyWriterMediaTypes((Class<Object>)entityClass, entityType, RequestWriter.EMPTY_ANNOTATIONS);
        final MediaType mediaType = this.getMediaType(mediaTypes);
        headers.putSingle("Content-Type", mediaType);
        return mediaType;
    }
    
    private MediaType getMediaType(final List<MediaType> mediaTypes) {
        if (mediaTypes.isEmpty()) {
            return MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
        MediaType mediaType = mediaTypes.get(0);
        if (mediaType.isWildcardType() || mediaType.isWildcardSubtype()) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
        return mediaType;
    }
    
    static {
        LOGGER = Logger.getLogger(RequestWriter.class.getName());
        EMPTY_ANNOTATIONS = new Annotation[0];
    }
    
    private final class RequestEntityWriterImpl implements RequestEntityWriter
    {
        private final ClientRequest cr;
        private final Object entity;
        private final Type entityType;
        private MediaType mediaType;
        private final long size;
        private final MessageBodyWriter bw;
        
        public RequestEntityWriterImpl(final ClientRequest cr) {
            this.cr = cr;
            final Object e = cr.getEntity();
            if (e == null) {
                throw new IllegalArgumentException("The entity of the client request is null");
            }
            if (e instanceof GenericEntity) {
                final GenericEntity ge = (GenericEntity)e;
                this.entity = ge.getEntity();
                this.entityType = ge.getType();
            }
            else {
                this.entity = e;
                this.entityType = this.entity.getClass();
            }
            final Class entityClass = this.entity.getClass();
            final MultivaluedMap<String, Object> headers = cr.getHeaders();
            this.mediaType = RequestWriter.this.getMediaType(entityClass, this.entityType, headers);
            this.bw = RequestWriter.this.workers.getMessageBodyWriter((Class<Object>)entityClass, this.entityType, RequestWriter.EMPTY_ANNOTATIONS, this.mediaType);
            if (this.bw == null) {
                final String message = "A message body writer for Java class " + this.entity.getClass().getName() + ", and Java type " + this.entityType + ", and MIME media type " + this.mediaType + " was not found";
                RequestWriter.LOGGER.severe(message);
                final Map<MediaType, List<MessageBodyWriter>> m = RequestWriter.this.workers.getWriters(this.mediaType);
                RequestWriter.LOGGER.severe("The registered message body writers compatible with the MIME media type are:\n" + RequestWriter.this.workers.writersToString(m));
                throw new ClientHandlerException(message);
            }
            this.size = (headers.containsKey("Content-Encoding") ? -1L : this.bw.getSize(this.entity, entityClass, this.entityType, RequestWriter.EMPTY_ANNOTATIONS, this.mediaType));
        }
        
        @Override
        public long getSize() {
            return this.size;
        }
        
        @Override
        public MediaType getMediaType() {
            return this.mediaType;
        }
        
        @Override
        public void writeRequestEntity(OutputStream out) throws IOException {
            out = this.cr.getAdapter().adapt(this.cr, out);
            try {
                this.bw.writeTo(this.entity, this.entity.getClass(), this.entityType, RequestWriter.EMPTY_ANNOTATIONS, this.mediaType, this.cr.getMetadata(), out);
                out.flush();
            }
            finally {
                out.close();
            }
        }
    }
    
    protected interface RequestEntityWriter
    {
        long getSize();
        
        MediaType getMediaType();
        
        void writeRequestEntity(final OutputStream p0) throws IOException;
    }
    
    protected interface RequestEntityWriterListener
    {
        void onRequestEntitySize(final long p0) throws IOException;
        
        OutputStream onGetOutputStream() throws IOException;
    }
}
