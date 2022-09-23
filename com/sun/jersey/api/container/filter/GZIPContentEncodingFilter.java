// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.container.filter;

import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import javax.ws.rs.core.EntityTag;
import com.sun.jersey.spi.container.ContainerResponse;
import java.io.IOException;
import com.sun.jersey.api.container.ContainerException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class GZIPContentEncodingFilter implements ContainerRequestFilter, ContainerResponseFilter
{
    private static final String ENTITY_TAG_GZIP_SUFFIX_VALUE = "-gzip";
    private static final String ENTITY_TAG_GZIP_SUFFIX_HEADER_VALUE = "-gzip\"";
    
    @Override
    public ContainerRequest filter(final ContainerRequest request) {
        final String contentEncoding = request.getRequestHeaders().getFirst("Content-Encoding");
        if (contentEncoding != null && contentEncoding.trim().equals("gzip")) {
            request.getRequestHeaders().remove("Content-Encoding");
            try {
                request.setEntityInputStream(new GZIPInputStream(request.getEntityInputStream()));
            }
            catch (IOException ex) {
                throw new ContainerException(ex);
            }
        }
        final String acceptEncoding = request.getRequestHeaders().getFirst("Accept-Encoding");
        final String entityTag = request.getRequestHeaders().getFirst("If-None-Match");
        if (acceptEncoding != null && acceptEncoding.contains("gzip") && entityTag != null) {
            if (entityTag.endsWith("-gzip\"")) {
                final int gzipsuffixbeginIndex = entityTag.lastIndexOf("-gzip\"");
                final StringBuilder sb = new StringBuilder();
                sb.append(entityTag.substring(0, gzipsuffixbeginIndex));
                sb.append('\"');
                request.getRequestHeaders().putSingle("If-None-Match", sb.toString());
            }
            else {
                request.getRequestHeaders().remove("If-None-Match");
            }
        }
        return request;
    }
    
    @Override
    public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response) {
        response.getHttpHeaders().add("Vary", "Accept-Encoding");
        final String acceptEncoding = request.getRequestHeaders().getFirst("Accept-Encoding");
        final String contentEncoding = response.getHttpHeaders().getFirst("Content-Encoding");
        if (acceptEncoding != null && contentEncoding == null && acceptEncoding.contains("gzip")) {
            if (response.getHttpHeaders().containsKey("ETag")) {
                final EntityTag entityTag = response.getHttpHeaders().getFirst("ETag");
                if (entityTag != null) {
                    response.getHttpHeaders().putSingle("ETag", new EntityTag(entityTag.getValue() + "-gzip", entityTag.isWeak()));
                }
            }
            if (response.getEntity() != null) {
                response.getHttpHeaders().add("Content-Encoding", "gzip");
                response.setContainerResponseWriter(new Adapter(response.getContainerResponseWriter()));
            }
        }
        return response;
    }
    
    private static final class Adapter implements ContainerResponseWriter
    {
        private final ContainerResponseWriter crw;
        private GZIPOutputStream gos;
        
        Adapter(final ContainerResponseWriter crw) {
            this.crw = crw;
        }
        
        @Override
        public OutputStream writeStatusAndHeaders(final long contentLength, final ContainerResponse response) throws IOException {
            return this.gos = new GZIPOutputStream(this.crw.writeStatusAndHeaders(-1L, response));
        }
        
        @Override
        public void finish() throws IOException {
            this.gos.finish();
            this.crw.finish();
        }
    }
}
