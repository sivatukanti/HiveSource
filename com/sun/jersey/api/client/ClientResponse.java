// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import javax.ws.rs.core.NewCookie;
import java.net.URI;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.ByteArrayOutputStream;
import java.util.List;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.Closeable;
import com.sun.jersey.core.provider.CompletableReader;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.io.IOException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import com.sun.jersey.spi.MessageBodyWorkers;
import java.io.InputStream;
import com.sun.jersey.core.header.InBoundHeaders;
import java.util.Map;
import java.util.Date;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.ext.RuntimeDelegate;
import java.lang.annotation.Annotation;
import java.util.logging.Logger;

public class ClientResponse
{
    private static final Logger LOGGER;
    private static final Annotation[] EMPTY_ANNOTATIONS;
    protected static final RuntimeDelegate.HeaderDelegate<EntityTag> entityTagDelegate;
    protected static final RuntimeDelegate.HeaderDelegate<Date> dateDelegate;
    private Map<String, Object> properties;
    private int status;
    private InBoundHeaders headers;
    private boolean isEntityBuffered;
    private InputStream entity;
    private MessageBodyWorkers workers;
    
    public ClientResponse(final int status, final InBoundHeaders headers, final InputStream entity, final MessageBodyWorkers workers) {
        this.status = status;
        this.headers = headers;
        this.entity = entity;
        this.workers = workers;
    }
    
    public Client getClient() {
        return this.getProperties().get(Client.class.getName());
    }
    
    public Map<String, Object> getProperties() {
        if (this.properties != null) {
            return this.properties;
        }
        return this.properties = new HashMap<String, Object>();
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public void setStatus(final int status) {
        this.status = status;
    }
    
    public void setStatus(final Response.StatusType status) {
        this.setStatus(status.getStatusCode());
    }
    
    public Status getClientResponseStatus() {
        return Status.fromStatusCode(this.status);
    }
    
    @Deprecated
    public Response.Status getResponseStatus() {
        return Response.Status.fromStatusCode(this.status);
    }
    
    @Deprecated
    public void setResponseStatus(final Response.StatusType status) {
        this.setStatus(status);
    }
    
    @Deprecated
    public MultivaluedMap<String, String> getMetadata() {
        return this.getHeaders();
    }
    
    public MultivaluedMap<String, String> getHeaders() {
        return this.headers;
    }
    
    public boolean hasEntity() {
        try {
            if (this.entity.available() > 0) {
                return true;
            }
            if (this.entity.markSupported()) {
                this.entity.mark(1);
                final int i = this.entity.read();
                this.entity.reset();
                return i != -1;
            }
            return false;
        }
        catch (IOException ex) {
            throw new ClientHandlerException(ex);
        }
    }
    
    public InputStream getEntityInputStream() {
        return this.entity;
    }
    
    public void setEntityInputStream(final InputStream entity) {
        this.isEntityBuffered = false;
        this.entity = entity;
    }
    
    public <T> T getEntity(final Class<T> c) throws ClientHandlerException, UniformInterfaceException {
        return this.getEntity(c, c);
    }
    
    public <T> T getEntity(final GenericType<T> gt) throws ClientHandlerException, UniformInterfaceException {
        return this.getEntity(gt.getRawClass(), gt.getType());
    }
    
    private <T> T getEntity(final Class<T> c, final Type type) {
        if (this.getStatus() == 204) {
            throw new UniformInterfaceException(this);
        }
        MediaType mediaType = this.getType();
        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
        final MessageBodyReader<T> br = this.workers.getMessageBodyReader(c, type, ClientResponse.EMPTY_ANNOTATIONS, mediaType);
        if (br == null) {
            this.close();
            final String message = "A message body reader for Java class " + c.getName() + ", and Java type " + type + ", and MIME media type " + mediaType + " was not found";
            ClientResponse.LOGGER.severe(message);
            final Map<MediaType, List<MessageBodyReader>> m = this.workers.getReaders(mediaType);
            ClientResponse.LOGGER.severe("The registered message body readers compatible with the MIME media type are:\n" + this.workers.readersToString(m));
            throw new ClientHandlerException(message);
        }
        try {
            T t = br.readFrom(c, type, ClientResponse.EMPTY_ANNOTATIONS, mediaType, this.headers, this.entity);
            if (br instanceof CompletableReader) {
                t = ((CompletableReader)br).complete(t);
            }
            if (!(t instanceof Closeable)) {
                this.close();
            }
            return t;
        }
        catch (IOException ex) {
            this.close();
            throw new ClientHandlerException(ex);
        }
    }
    
    public void bufferEntity() throws ClientHandlerException {
        if (this.isEntityBuffered) {
            return;
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ReaderWriter.writeTo(this.entity, baos);
        }
        catch (IOException ex) {
            throw new ClientHandlerException(ex);
        }
        finally {
            this.close();
        }
        this.entity = new ByteArrayInputStream(baos.toByteArray());
        this.isEntityBuffered = true;
    }
    
    public void close() throws ClientHandlerException {
        try {
            this.entity.close();
        }
        catch (IOException e) {
            throw new ClientHandlerException(e);
        }
    }
    
    public MediaType getType() {
        final String ct = this.getHeaders().getFirst("Content-Type");
        return (ct != null) ? MediaType.valueOf(ct) : null;
    }
    
    public URI getLocation() {
        final String l = this.getHeaders().getFirst("Location");
        return (l != null) ? URI.create(l) : null;
    }
    
    public EntityTag getEntityTag() {
        final String t = this.getHeaders().getFirst("ETag");
        return (t != null) ? ClientResponse.entityTagDelegate.fromString(t) : null;
    }
    
    public Date getLastModified() {
        final String d = this.getHeaders().getFirst("Last-Modified");
        return (d != null) ? ClientResponse.dateDelegate.fromString(d) : null;
    }
    
    public Date getResponseDate() {
        final String d = this.getHeaders().getFirst("Date");
        return (d != null) ? ClientResponse.dateDelegate.fromString(d) : null;
    }
    
    public String getLanguage() {
        return this.getHeaders().getFirst("Content-Language");
    }
    
    public int getLength() {
        int size = -1;
        final String sizeStr = this.getHeaders().getFirst("Content-Length");
        if (sizeStr == null) {
            return -1;
        }
        try {
            size = Integer.parseInt(sizeStr);
        }
        catch (NumberFormatException ex) {}
        return size;
    }
    
    public List<NewCookie> getCookies() {
        final List<String> hs = this.getHeaders().get("Set-Cookie");
        if (hs == null) {
            return Collections.emptyList();
        }
        final List<NewCookie> cs = new ArrayList<NewCookie>();
        for (final String h : hs) {
            cs.add(NewCookie.valueOf(h));
        }
        return cs;
    }
    
    public Set<String> getAllow() {
        final String allow = this.headers.getFirst("Allow");
        if (allow == null) {
            return Collections.emptySet();
        }
        final Set<String> allowedMethods = new HashSet<String>();
        final StringTokenizer tokenizer = new StringTokenizer(allow, ",");
        while (tokenizer.hasMoreTokens()) {
            final String m = tokenizer.nextToken().trim();
            if (m.length() > 0) {
                allowedMethods.add(m.toUpperCase());
            }
        }
        return allowedMethods;
    }
    
    public WebResourceLinkHeaders getLinks() {
        return new WebResourceLinkHeaders(this.getClient(), this.getHeaders());
    }
    
    @Override
    public String toString() {
        return "Client response status: " + this.status;
    }
    
    static {
        LOGGER = Logger.getLogger(ClientResponse.class.getName());
        EMPTY_ANNOTATIONS = new Annotation[0];
        entityTagDelegate = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class);
        dateDelegate = RuntimeDelegate.getInstance().createHeaderDelegate(Date.class);
    }
    
    public enum Status implements Response.StatusType
    {
        OK(200, "OK"), 
        CREATED(201, "Created"), 
        ACCEPTED(202, "Accepted"), 
        NON_AUTHORITIVE_INFORMATION(203, "Non-Authoritative Information"), 
        NO_CONTENT(204, "No Content"), 
        RESET_CONTENT(205, "Reset Content"), 
        PARTIAL_CONTENT(206, "Partial Content"), 
        MOVED_PERMANENTLY(301, "Moved Permanently"), 
        FOUND(302, "Found"), 
        SEE_OTHER(303, "See Other"), 
        NOT_MODIFIED(304, "Not Modified"), 
        USE_PROXY(305, "Use Proxy"), 
        TEMPORARY_REDIRECT(307, "Temporary Redirect"), 
        BAD_REQUEST(400, "Bad Request"), 
        UNAUTHORIZED(401, "Unauthorized"), 
        PAYMENT_REQUIRED(402, "Payment Required"), 
        FORBIDDEN(403, "Forbidden"), 
        NOT_FOUND(404, "Not Found"), 
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"), 
        NOT_ACCEPTABLE(406, "Not Acceptable"), 
        PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"), 
        REQUEST_TIMEOUT(408, "Request Timeout"), 
        CONFLICT(409, "Conflict"), 
        GONE(410, "Gone"), 
        LENGTH_REQUIRED(411, "Length Required"), 
        PRECONDITION_FAILED(412, "Precondition Failed"), 
        REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"), 
        REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"), 
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"), 
        REQUESTED_RANGE_NOT_SATIFIABLE(416, "Requested Range Not Satisfiable"), 
        EXPECTATION_FAILED(417, "Expectation Failed"), 
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"), 
        NOT_IMPLEMENTED(501, "Not Implemented"), 
        BAD_GATEWAY(502, "Bad Gateway"), 
        SERVICE_UNAVAILABLE(503, "Service Unavailable"), 
        GATEWAY_TIMEOUT(504, "Gateway Timeout"), 
        HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");
        
        private final int code;
        private final String reason;
        private Response.Status.Family family;
        
        private Status(final int statusCode, final String reasonPhrase) {
            this.code = statusCode;
            this.reason = reasonPhrase;
            switch (this.code / 100) {
                case 1: {
                    this.family = Response.Status.Family.INFORMATIONAL;
                    break;
                }
                case 2: {
                    this.family = Response.Status.Family.SUCCESSFUL;
                    break;
                }
                case 3: {
                    this.family = Response.Status.Family.REDIRECTION;
                    break;
                }
                case 4: {
                    this.family = Response.Status.Family.CLIENT_ERROR;
                    break;
                }
                case 5: {
                    this.family = Response.Status.Family.SERVER_ERROR;
                    break;
                }
                default: {
                    this.family = Response.Status.Family.OTHER;
                    break;
                }
            }
        }
        
        @Override
        public Response.Status.Family getFamily() {
            return this.family;
        }
        
        @Override
        public int getStatusCode() {
            return this.code;
        }
        
        @Override
        public String getReasonPhrase() {
            return this.toString();
        }
        
        @Override
        public String toString() {
            return this.reason;
        }
        
        public static Status fromStatusCode(final int statusCode) {
            for (final Status s : values()) {
                if (s.code == statusCode) {
                    return s;
                }
            }
            return null;
        }
    }
}
