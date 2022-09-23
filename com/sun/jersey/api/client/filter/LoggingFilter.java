// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.AbstractClientRequestAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientRequest;
import java.io.PrintStream;
import java.util.logging.Logger;

public class LoggingFilter extends ClientFilter
{
    private static final Logger LOGGER;
    private static final String NOTIFICATION_PREFIX = "* ";
    private static final String REQUEST_PREFIX = "> ";
    private static final String RESPONSE_PREFIX = "< ";
    private final PrintStream loggingStream;
    private final Logger logger;
    private long _id;
    
    public LoggingFilter() {
        this(LoggingFilter.LOGGER);
    }
    
    public LoggingFilter(final Logger logger) {
        this._id = 0L;
        this.loggingStream = null;
        this.logger = logger;
    }
    
    public LoggingFilter(final PrintStream loggingStream) {
        this._id = 0L;
        this.loggingStream = loggingStream;
        this.logger = null;
    }
    
    private void log(final StringBuilder b) {
        if (this.logger != null) {
            this.logger.info(b.toString());
        }
        else {
            this.loggingStream.print(b);
        }
    }
    
    private StringBuilder prefixId(final StringBuilder b, final long id) {
        b.append(Long.toString(id)).append(" ");
        return b;
    }
    
    @Override
    public ClientResponse handle(final ClientRequest request) throws ClientHandlerException {
        final long id2 = this._id + 1L;
        this._id = id2;
        final long id = id2;
        this.logRequest(id, request);
        final ClientResponse response = this.getNext().handle(request);
        this.logResponse(id, response);
        return response;
    }
    
    private void logRequest(final long id, final ClientRequest request) {
        final StringBuilder b = new StringBuilder();
        this.printRequestLine(b, id, request);
        this.printRequestHeaders(b, id, request.getHeaders());
        if (request.getEntity() != null) {
            request.setAdapter(new Adapter(request.getAdapter(), b));
        }
        else {
            this.log(b);
        }
    }
    
    private void printRequestLine(final StringBuilder b, final long id, final ClientRequest request) {
        this.prefixId(b, id).append("* ").append("Client out-bound request").append("\n");
        this.prefixId(b, id).append("> ").append(request.getMethod()).append(" ").append(request.getURI().toASCIIString()).append("\n");
    }
    
    private void printRequestHeaders(final StringBuilder b, final long id, final MultivaluedMap<String, Object> headers) {
        for (final Map.Entry<String, List<Object>> e : headers.entrySet()) {
            final List<Object> val = e.getValue();
            final String header = e.getKey();
            if (val.size() == 1) {
                this.prefixId(b, id).append("> ").append(header).append(": ").append(ClientRequest.getHeaderValue(val.get(0))).append("\n");
            }
            else {
                final StringBuilder sb = new StringBuilder();
                boolean add = false;
                for (final Object o : val) {
                    if (add) {
                        sb.append(',');
                    }
                    add = true;
                    sb.append(ClientRequest.getHeaderValue(o));
                }
                this.prefixId(b, id).append("> ").append(header).append(": ").append(sb.toString()).append("\n");
            }
        }
    }
    
    private void logResponse(final long id, final ClientResponse response) {
        final StringBuilder b = new StringBuilder();
        this.printResponseLine(b, id, response);
        this.printResponseHeaders(b, id, response.getHeaders());
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final InputStream in = response.getEntityInputStream();
        try {
            ReaderWriter.writeTo(in, out);
            final byte[] requestEntity = out.toByteArray();
            this.printEntity(b, requestEntity);
            response.setEntityInputStream(new ByteArrayInputStream(requestEntity));
        }
        catch (IOException ex) {
            throw new ClientHandlerException(ex);
        }
        this.log(b);
    }
    
    private void printResponseLine(final StringBuilder b, final long id, final ClientResponse response) {
        this.prefixId(b, id).append("* ").append("Client in-bound response").append("\n");
        this.prefixId(b, id).append("< ").append(Integer.toString(response.getStatus())).append("\n");
    }
    
    private void printResponseHeaders(final StringBuilder b, final long id, final MultivaluedMap<String, String> headers) {
        for (final Map.Entry<String, List<String>> e : headers.entrySet()) {
            final String header = e.getKey();
            for (final String value : e.getValue()) {
                this.prefixId(b, id).append("< ").append(header).append(": ").append(value).append("\n");
            }
        }
        this.prefixId(b, id).append("< ").append("\n");
    }
    
    private void printEntity(final StringBuilder b, final byte[] entity) throws IOException {
        if (entity.length == 0) {
            return;
        }
        b.append(new String(entity)).append("\n");
    }
    
    static {
        LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    }
    
    private final class Adapter extends AbstractClientRequestAdapter
    {
        private final StringBuilder b;
        
        Adapter(final ClientRequestAdapter cra, final StringBuilder b) {
            super(cra);
            this.b = b;
        }
        
        @Override
        public OutputStream adapt(final ClientRequest request, final OutputStream out) throws IOException {
            return new LoggingOutputStream(this.getAdapter().adapt(request, out), this.b);
        }
    }
    
    private final class LoggingOutputStream extends OutputStream
    {
        private final OutputStream out;
        private final ByteArrayOutputStream baos;
        private final StringBuilder b;
        
        LoggingOutputStream(final OutputStream out, final StringBuilder b) {
            this.baos = new ByteArrayOutputStream();
            this.out = out;
            this.b = b;
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.baos.write(b);
            this.out.write(b);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.baos.write(b, off, len);
            this.out.write(b, off, len);
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.baos.write(b);
            this.out.write(b);
        }
        
        @Override
        public void close() throws IOException {
            LoggingFilter.this.printEntity(this.b, this.baos.toByteArray());
            LoggingFilter.this.log(this.b);
            this.out.close();
        }
    }
}
