// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.core;

import java.util.Date;
import java.util.Locale;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

public abstract class Response
{
    protected Response() {
    }
    
    public abstract Object getEntity();
    
    public abstract int getStatus();
    
    public abstract MultivaluedMap<String, Object> getMetadata();
    
    public static ResponseBuilder fromResponse(final Response response) {
        final ResponseBuilder b = status(response.getStatus());
        b.entity(response.getEntity());
        for (final String headerName : response.getMetadata().keySet()) {
            final List<Object> headerValues = response.getMetadata().get(headerName);
            for (final Object headerValue : headerValues) {
                b.header(headerName, headerValue);
            }
        }
        return b;
    }
    
    public static ResponseBuilder status(final StatusType status) {
        final ResponseBuilder b = ResponseBuilder.newInstance();
        b.status(status);
        return b;
    }
    
    public static ResponseBuilder status(final Status status) {
        return status((StatusType)status);
    }
    
    public static ResponseBuilder status(final int status) {
        final ResponseBuilder b = ResponseBuilder.newInstance();
        b.status(status);
        return b;
    }
    
    public static ResponseBuilder ok() {
        final ResponseBuilder b = status(Status.OK);
        return b;
    }
    
    public static ResponseBuilder ok(final Object entity) {
        final ResponseBuilder b = ok();
        b.entity(entity);
        return b;
    }
    
    public static ResponseBuilder ok(final Object entity, final MediaType type) {
        final ResponseBuilder b = ok();
        b.entity(entity);
        b.type(type);
        return b;
    }
    
    public static ResponseBuilder ok(final Object entity, final String type) {
        final ResponseBuilder b = ok();
        b.entity(entity);
        b.type(type);
        return b;
    }
    
    public static ResponseBuilder ok(final Object entity, final Variant variant) {
        final ResponseBuilder b = ok();
        b.entity(entity);
        b.variant(variant);
        return b;
    }
    
    public static ResponseBuilder serverError() {
        final ResponseBuilder b = status(Status.INTERNAL_SERVER_ERROR);
        return b;
    }
    
    public static ResponseBuilder created(final URI location) {
        final ResponseBuilder b = status(Status.CREATED).location(location);
        return b;
    }
    
    public static ResponseBuilder noContent() {
        final ResponseBuilder b = status(Status.NO_CONTENT);
        return b;
    }
    
    public static ResponseBuilder notModified() {
        final ResponseBuilder b = status(Status.NOT_MODIFIED);
        return b;
    }
    
    public static ResponseBuilder notModified(final EntityTag tag) {
        final ResponseBuilder b = notModified();
        b.tag(tag);
        return b;
    }
    
    public static ResponseBuilder notModified(final String tag) {
        final ResponseBuilder b = notModified();
        b.tag(tag);
        return b;
    }
    
    public static ResponseBuilder seeOther(final URI location) {
        final ResponseBuilder b = status(Status.SEE_OTHER).location(location);
        return b;
    }
    
    public static ResponseBuilder temporaryRedirect(final URI location) {
        final ResponseBuilder b = status(Status.TEMPORARY_REDIRECT).location(location);
        return b;
    }
    
    public static ResponseBuilder notAcceptable(final List<Variant> variants) {
        final ResponseBuilder b = status(Status.NOT_ACCEPTABLE).variants(variants);
        return b;
    }
    
    public abstract static class ResponseBuilder
    {
        protected ResponseBuilder() {
        }
        
        protected static ResponseBuilder newInstance() {
            final ResponseBuilder b = RuntimeDelegate.getInstance().createResponseBuilder();
            return b;
        }
        
        public abstract Response build();
        
        public abstract ResponseBuilder clone();
        
        public abstract ResponseBuilder status(final int p0);
        
        public ResponseBuilder status(final StatusType status) {
            if (status == null) {
                throw new IllegalArgumentException();
            }
            return this.status(status.getStatusCode());
        }
        
        public ResponseBuilder status(final Status status) {
            return this.status((StatusType)status);
        }
        
        public abstract ResponseBuilder entity(final Object p0);
        
        public abstract ResponseBuilder type(final MediaType p0);
        
        public abstract ResponseBuilder type(final String p0);
        
        public abstract ResponseBuilder variant(final Variant p0);
        
        public abstract ResponseBuilder variants(final List<Variant> p0);
        
        public abstract ResponseBuilder language(final String p0);
        
        public abstract ResponseBuilder language(final Locale p0);
        
        public abstract ResponseBuilder location(final URI p0);
        
        public abstract ResponseBuilder contentLocation(final URI p0);
        
        public abstract ResponseBuilder tag(final EntityTag p0);
        
        public abstract ResponseBuilder tag(final String p0);
        
        public abstract ResponseBuilder lastModified(final Date p0);
        
        public abstract ResponseBuilder cacheControl(final CacheControl p0);
        
        public abstract ResponseBuilder expires(final Date p0);
        
        public abstract ResponseBuilder header(final String p0, final Object p1);
        
        public abstract ResponseBuilder cookie(final NewCookie... p0);
    }
    
    public enum Status implements StatusType
    {
        OK(200, "OK"), 
        CREATED(201, "Created"), 
        ACCEPTED(202, "Accepted"), 
        NO_CONTENT(204, "No Content"), 
        MOVED_PERMANENTLY(301, "Moved Permanently"), 
        SEE_OTHER(303, "See Other"), 
        NOT_MODIFIED(304, "Not Modified"), 
        TEMPORARY_REDIRECT(307, "Temporary Redirect"), 
        BAD_REQUEST(400, "Bad Request"), 
        UNAUTHORIZED(401, "Unauthorized"), 
        FORBIDDEN(403, "Forbidden"), 
        NOT_FOUND(404, "Not Found"), 
        NOT_ACCEPTABLE(406, "Not Acceptable"), 
        CONFLICT(409, "Conflict"), 
        GONE(410, "Gone"), 
        PRECONDITION_FAILED(412, "Precondition Failed"), 
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"), 
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"), 
        SERVICE_UNAVAILABLE(503, "Service Unavailable");
        
        private final int code;
        private final String reason;
        private Family family;
        
        private Status(final int statusCode, final String reasonPhrase) {
            this.code = statusCode;
            this.reason = reasonPhrase;
            switch (this.code / 100) {
                case 1: {
                    this.family = Family.INFORMATIONAL;
                    break;
                }
                case 2: {
                    this.family = Family.SUCCESSFUL;
                    break;
                }
                case 3: {
                    this.family = Family.REDIRECTION;
                    break;
                }
                case 4: {
                    this.family = Family.CLIENT_ERROR;
                    break;
                }
                case 5: {
                    this.family = Family.SERVER_ERROR;
                    break;
                }
                default: {
                    this.family = Family.OTHER;
                    break;
                }
            }
        }
        
        public Family getFamily() {
            return this.family;
        }
        
        public int getStatusCode() {
            return this.code;
        }
        
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
        
        public enum Family
        {
            INFORMATIONAL, 
            SUCCESSFUL, 
            REDIRECTION, 
            CLIENT_ERROR, 
            SERVER_ERROR, 
            OTHER;
        }
    }
    
    public interface StatusType
    {
        int getStatusCode();
        
        Status.Family getFamily();
        
        String getReasonPhrase();
    }
}
