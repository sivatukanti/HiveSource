// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.container.filter;

import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.ContainerResponse;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import com.sun.jersey.api.container.ContainerException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.ByteArrayOutputStream;
import com.sun.jersey.spi.container.ContainerRequest;
import java.util.Map;
import com.sun.jersey.api.core.ResourceConfig;
import javax.ws.rs.core.Context;
import com.sun.jersey.api.core.HttpContext;
import java.util.logging.Logger;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter
{
    public static final String FEATURE_LOGGING_DISABLE_ENTITY = "com.sun.jersey.config.feature.logging.DisableEntitylogging";
    private static final Logger LOGGER;
    private static final String NOTIFICATION_PREFIX = "* ";
    private static final String REQUEST_PREFIX = "> ";
    private static final String RESPONSE_PREFIX = "< ";
    private final Logger logger;
    @Context
    private HttpContext hc;
    @Context
    private ResourceConfig rc;
    private long id;
    
    public LoggingFilter() {
        this(LoggingFilter.LOGGER);
    }
    
    public LoggingFilter(final Logger logger) {
        this.id = 0L;
        this.logger = logger;
    }
    
    private synchronized void setId() {
        if (this.hc.getProperties().get("request-id") == null) {
            final Map<String, Object> properties = this.hc.getProperties();
            final String s = "request-id";
            final long n = this.id + 1L;
            this.id = n;
            properties.put(s, Long.toString(n));
        }
    }
    
    private StringBuilder prefixId(final StringBuilder b) {
        b.append(this.hc.getProperties().get("request-id").toString()).append(" ");
        return b;
    }
    
    @Override
    public ContainerRequest filter(final ContainerRequest request) {
        this.setId();
        final StringBuilder b = new StringBuilder();
        this.printRequestLine(b, request);
        this.printRequestHeaders(b, request.getRequestHeaders());
        if (this.rc.getFeature("com.sun.jersey.config.feature.logging.DisableEntitylogging")) {
            this.logger.info(b.toString());
            return request;
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final InputStream in = request.getEntityInputStream();
        try {
            if (in.available() > 0) {
                ReaderWriter.writeTo(in, out);
                final byte[] requestEntity = out.toByteArray();
                this.printEntity(b, requestEntity);
                request.setEntityInputStream(new ByteArrayInputStream(requestEntity));
            }
            return request;
        }
        catch (IOException ex) {
            throw new ContainerException(ex);
        }
        finally {
            this.logger.info(b.toString());
        }
    }
    
    private void printRequestLine(final StringBuilder b, final ContainerRequest request) {
        this.prefixId(b).append("* ").append("Server in-bound request").append('\n');
        this.prefixId(b).append("> ").append(request.getMethod()).append(" ").append(request.getRequestUri().toASCIIString()).append('\n');
    }
    
    private void printRequestHeaders(final StringBuilder b, final MultivaluedMap<String, String> headers) {
        for (final Map.Entry<String, List<String>> e : headers.entrySet()) {
            final String header = e.getKey();
            for (final String value : e.getValue()) {
                this.prefixId(b).append("> ").append(header).append(": ").append(value).append('\n');
            }
        }
        this.prefixId(b).append("> ").append('\n');
    }
    
    private void printEntity(final StringBuilder b, final byte[] entity) throws IOException {
        if (entity.length == 0) {
            return;
        }
        b.append(new String(entity)).append("\n");
    }
    
    @Override
    public ContainerResponse filter(final ContainerRequest request, final ContainerResponse response) {
        this.setId();
        response.setContainerResponseWriter(new Adapter(response.getContainerResponseWriter()));
        return response;
    }
    
    private void printResponseLine(final StringBuilder b, final ContainerResponse response) {
        this.prefixId(b).append("* ").append("Server out-bound response").append('\n');
        this.prefixId(b).append("< ").append(Integer.toString(response.getStatus())).append('\n');
    }
    
    private void printResponseHeaders(final StringBuilder b, final MultivaluedMap<String, Object> headers) {
        for (final Map.Entry<String, List<Object>> e : headers.entrySet()) {
            final String header = e.getKey();
            for (final Object value : e.getValue()) {
                this.prefixId(b).append("< ").append(header).append(": ").append(ContainerResponse.getHeaderValue(value)).append('\n');
            }
        }
        this.prefixId(b).append("< ").append('\n');
    }
    
    static {
        LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    }
    
    private final class Adapter implements ContainerResponseWriter
    {
        private final ContainerResponseWriter crw;
        private final boolean disableEntity;
        private long contentLength;
        private ContainerResponse response;
        private ByteArrayOutputStream baos;
        private StringBuilder b;
        
        Adapter(final ContainerResponseWriter crw) {
            this.b = new StringBuilder();
            this.crw = crw;
            this.disableEntity = LoggingFilter.this.rc.getFeature("com.sun.jersey.config.feature.logging.DisableEntitylogging");
        }
        
        @Override
        public OutputStream writeStatusAndHeaders(final long contentLength, final ContainerResponse response) throws IOException {
            LoggingFilter.this.printResponseLine(this.b, response);
            LoggingFilter.this.printResponseHeaders(this.b, response.getHttpHeaders());
            if (this.disableEntity) {
                LoggingFilter.this.logger.info(this.b.toString());
                return this.crw.writeStatusAndHeaders(contentLength, response);
            }
            this.contentLength = contentLength;
            this.response = response;
            return this.baos = new ByteArrayOutputStream();
        }
        
        @Override
        public void finish() throws IOException {
            if (!this.disableEntity) {
                final byte[] entity = this.baos.toByteArray();
                LoggingFilter.this.printEntity(this.b, entity);
                LoggingFilter.this.logger.info(this.b.toString());
                final OutputStream out = this.crw.writeStatusAndHeaders(this.contentLength, this.response);
                out.write(entity);
            }
            this.crw.finish();
        }
    }
}
