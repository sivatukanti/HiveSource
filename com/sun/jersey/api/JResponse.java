// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api;

import java.util.AbstractMap;
import com.sun.jersey.core.util.KeyComparatorLinkedHashMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.CacheControl;
import java.util.Date;
import java.util.Locale;
import javax.ws.rs.core.EntityTag;
import java.net.URI;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import com.sun.jersey.core.spi.factory.ResponseImpl;
import com.sun.jersey.core.header.OutBoundHeaders;
import javax.ws.rs.core.Response;

public class JResponse<E>
{
    private final Response.StatusType statusType;
    private final E entity;
    private final OutBoundHeaders headers;
    
    public JResponse(final Response.StatusType statusType, final OutBoundHeaders headers, final E entity) {
        this.statusType = statusType;
        this.entity = entity;
        this.headers = headers;
    }
    
    public JResponse(final int status, final OutBoundHeaders headers, final E entity) {
        this(ResponseImpl.toStatusType(status), headers, entity);
    }
    
    public JResponse(final JResponse<E> that) {
        this(that.statusType, (that.headers != null) ? new OutBoundHeaders(that.headers) : null, that.entity);
    }
    
    protected JResponse(final AJResponseBuilder<E, ?> b) {
        this.statusType = b.getStatusType();
        this.entity = b.getEntity();
        this.headers = b.getMetadata();
    }
    
    public JResponseAsResponse toResponse() {
        return new JResponseAsResponse(this);
    }
    
    public JResponseAsResponse toResponse(final Type type) {
        return new JResponseAsResponse(this, type);
    }
    
    public Response.StatusType getStatusType() {
        return this.statusType;
    }
    
    public int getStatus() {
        return this.statusType.getStatusCode();
    }
    
    public OutBoundHeaders getMetadata() {
        return this.headers;
    }
    
    public E getEntity() {
        return this.entity;
    }
    
    public Type getType() {
        return getSuperclassTypeParameter(this.getClass());
    }
    
    private static Type getSuperclassTypeParameter(final Class<?> subclass) {
        final Type superclass = subclass.getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) {
            return Object.class;
        }
        final ParameterizedType parameterized = (ParameterizedType)superclass;
        return parameterized.getActualTypeArguments()[0];
    }
    
    public static <E> JResponseBuilder<E> fromResponse(final Response response) {
        final JResponseBuilder b = status(response.getStatus());
        b.entity(response.getEntity());
        for (final String headerName : response.getMetadata().keySet()) {
            final List<Object> headerValues = response.getMetadata().get(headerName);
            for (final Object headerValue : headerValues) {
                b.header(headerName, headerValue);
            }
        }
        return (JResponseBuilder<E>)b;
    }
    
    public static <E> JResponseBuilder<E> fromResponse(final JResponse<E> response) {
        final JResponseBuilder<E> b = status(response.getStatus());
        b.entity((E)response.getEntity());
        for (final String headerName : ((AbstractMap<String, V>)response.getMetadata()).keySet()) {
            final List<Object> headerValues = ((KeyComparatorLinkedHashMap<K, List<Object>>)response.getMetadata()).get(headerName);
            for (final Object headerValue : headerValues) {
                b.header(headerName, headerValue);
            }
        }
        return b;
    }
    
    public static <E> JResponseBuilder<E> status(final Response.StatusType status) {
        final JResponseBuilder<E> b = new JResponseBuilder<E>();
        b.status(status);
        return b;
    }
    
    public static <E> JResponseBuilder<E> status(final Response.Status status) {
        return status((Response.StatusType)status);
    }
    
    public static <E> JResponseBuilder<E> status(final int status) {
        final JResponseBuilder<E> b = new JResponseBuilder<E>();
        b.status(status);
        return b;
    }
    
    public static <E> JResponseBuilder<E> ok() {
        final JResponseBuilder b = status(Response.Status.OK);
        return (JResponseBuilder<E>)b;
    }
    
    public static <E> JResponseBuilder<E> ok(final E entity) {
        final JResponseBuilder<E> b = ok();
        b.entity((E)entity);
        return b;
    }
    
    public static <E> JResponseBuilder<E> ok(final E entity, final MediaType type) {
        final JResponseBuilder<E> b = ok();
        b.entity((E)entity);
        b.type(type);
        return b;
    }
    
    public static <E> JResponseBuilder<E> ok(final E entity, final String type) {
        final JResponseBuilder<E> b = ok();
        b.entity((E)entity);
        b.type(type);
        return b;
    }
    
    public static <E> JResponseBuilder<E> ok(final E entity, final Variant variant) {
        final JResponseBuilder<E> b = ok();
        b.entity((E)entity);
        b.variant(variant);
        return b;
    }
    
    public static <E> JResponseBuilder<E> serverError() {
        final JResponseBuilder<E> b = status(Response.Status.INTERNAL_SERVER_ERROR);
        return b;
    }
    
    public static <E> JResponseBuilder<E> created(final URI location) {
        final JResponseBuilder<E> b = (JResponseBuilder<E>)status(Response.Status.CREATED).location(location);
        return b;
    }
    
    public static <E> JResponseBuilder<E> noContent() {
        final JResponseBuilder<E> b = status(Response.Status.NO_CONTENT);
        return b;
    }
    
    public static <E> JResponseBuilder<E> notModified() {
        final JResponseBuilder<E> b = status(Response.Status.NOT_MODIFIED);
        return b;
    }
    
    public static <E> JResponseBuilder<E> notModified(final EntityTag tag) {
        final JResponseBuilder<E> b = notModified();
        b.tag(tag);
        return b;
    }
    
    public static <E> JResponseBuilder<E> notModified(final String tag) {
        final JResponseBuilder b = notModified();
        b.tag(tag);
        return (JResponseBuilder<E>)b;
    }
    
    public static <E> JResponseBuilder<E> seeOther(final URI location) {
        final JResponseBuilder<E> b = (JResponseBuilder<E>)status(Response.Status.SEE_OTHER).location(location);
        return b;
    }
    
    public static <E> JResponseBuilder<E> temporaryRedirect(final URI location) {
        final JResponseBuilder<E> b = (JResponseBuilder<E>)status(Response.Status.TEMPORARY_REDIRECT).location(location);
        return b;
    }
    
    public static <E> JResponseBuilder<E> notAcceptable(final List<Variant> variants) {
        final JResponseBuilder<E> b = (JResponseBuilder<E>)status(Response.Status.NOT_ACCEPTABLE).variants(variants);
        return b;
    }
    
    public static final class JResponseBuilder<E> extends AJResponseBuilder<E, JResponseBuilder<E>>
    {
        public JResponseBuilder() {
        }
        
        public JResponseBuilder(final JResponseBuilder<E> that) {
            super(that);
        }
        
        public JResponseBuilder<E> clone() {
            return new JResponseBuilder<E>(this);
        }
        
        public JResponse<E> build() {
            final JResponse<E> r = new JResponse<E>(this);
            this.reset();
            return r;
        }
    }
    
    public abstract static class AJResponseBuilder<E, B extends AJResponseBuilder>
    {
        protected Response.StatusType statusType;
        protected OutBoundHeaders headers;
        protected E entity;
        
        protected AJResponseBuilder() {
            this.statusType = Response.Status.NO_CONTENT;
        }
        
        protected AJResponseBuilder(final AJResponseBuilder<E, ?> that) {
            this.statusType = Response.Status.NO_CONTENT;
            this.statusType = that.statusType;
            this.entity = that.entity;
            if (that.headers != null) {
                this.headers = new OutBoundHeaders(that.headers);
            }
            else {
                this.headers = null;
            }
        }
        
        protected void reset() {
            this.statusType = Response.Status.NO_CONTENT;
            this.entity = null;
            this.headers = null;
        }
        
        protected Response.StatusType getStatusType() {
            return this.statusType;
        }
        
        protected int getStatus() {
            return this.statusType.getStatusCode();
        }
        
        protected OutBoundHeaders getMetadata() {
            if (this.headers == null) {
                this.headers = new OutBoundHeaders();
            }
            return this.headers;
        }
        
        protected E getEntity() {
            return this.entity;
        }
        
        public B status(final int status) {
            return this.status(ResponseImpl.toStatusType(status));
        }
        
        public B status(final Response.StatusType status) {
            if (status == null) {
                throw new IllegalArgumentException();
            }
            this.statusType = status;
            return (B)this;
        }
        
        public B status(final Response.Status status) {
            return this.status((Response.StatusType)status);
        }
        
        public B entity(final E entity) {
            this.entity = entity;
            return (B)this;
        }
        
        public B type(final MediaType type) {
            this.headerSingle("Content-Type", type);
            return (B)this;
        }
        
        public B type(final String type) {
            return this.type((type == null) ? null : MediaType.valueOf(type));
        }
        
        public B variant(final Variant variant) {
            if (variant == null) {
                this.type((MediaType)null);
                this.language((String)null);
                this.encoding(null);
                return (B)this;
            }
            this.type(variant.getMediaType());
            this.language(variant.getLanguage());
            this.encoding(variant.getEncoding());
            return (B)this;
        }
        
        public B variants(final List<Variant> variants) {
            if (variants == null) {
                this.header("Vary", null);
                return (B)this;
            }
            if (variants.isEmpty()) {
                return (B)this;
            }
            final MediaType accept = variants.get(0).getMediaType();
            boolean vAccept = false;
            final Locale acceptLanguage = variants.get(0).getLanguage();
            boolean vAcceptLanguage = false;
            final String acceptEncoding = variants.get(0).getEncoding();
            boolean vAcceptEncoding = false;
            for (final Variant v : variants) {
                vAccept |= (!vAccept && this.vary(v.getMediaType(), accept));
                vAcceptLanguage |= (!vAcceptLanguage && this.vary(v.getLanguage(), acceptLanguage));
                vAcceptEncoding |= (!vAcceptEncoding && this.vary(v.getEncoding(), acceptEncoding));
            }
            final StringBuilder vary = new StringBuilder();
            this.append(vary, vAccept, "Accept");
            this.append(vary, vAcceptLanguage, "Accept-Language");
            this.append(vary, vAcceptEncoding, "Accept-Encoding");
            if (vary.length() > 0) {
                this.header("Vary", vary.toString());
            }
            return (B)this;
        }
        
        private boolean vary(final MediaType v, final MediaType vary) {
            return v != null && !v.equals(vary);
        }
        
        private boolean vary(final Locale v, final Locale vary) {
            return v != null && !v.equals(vary);
        }
        
        private boolean vary(final String v, final String vary) {
            return v != null && !v.equalsIgnoreCase(vary);
        }
        
        private void append(final StringBuilder sb, final boolean v, final String s) {
            if (v) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(s);
            }
        }
        
        public B language(final String language) {
            this.headerSingle("Content-Language", language);
            return (B)this;
        }
        
        public B language(final Locale language) {
            this.headerSingle("Content-Language", language);
            return (B)this;
        }
        
        public B location(final URI location) {
            this.headerSingle("Location", location);
            return (B)this;
        }
        
        public B contentLocation(final URI location) {
            this.headerSingle("Content-Location", location);
            return (B)this;
        }
        
        public B encoding(final String encoding) {
            this.headerSingle("Content-Encoding", encoding);
            return (B)this;
        }
        
        public B tag(final EntityTag tag) {
            this.headerSingle("ETag", tag);
            return (B)this;
        }
        
        public B tag(final String tag) {
            return this.tag((tag == null) ? null : new EntityTag(tag));
        }
        
        public B lastModified(final Date lastModified) {
            this.headerSingle("Last-Modified", lastModified);
            return (B)this;
        }
        
        public B cacheControl(final CacheControl cacheControl) {
            this.headerSingle("Cache-Control", cacheControl);
            return (B)this;
        }
        
        public B expires(final Date expires) {
            this.headerSingle("Expires", expires);
            return (B)this;
        }
        
        public B cookie(final NewCookie... cookies) {
            if (cookies != null) {
                for (final NewCookie cookie : cookies) {
                    this.header("Set-Cookie", cookie);
                }
            }
            else {
                this.header("Set-Cookie", null);
            }
            return (B)this;
        }
        
        public B header(final String name, final Object value) {
            return this.header(name, value, false);
        }
        
        public B headerSingle(final String name, final Object value) {
            return this.header(name, value, true);
        }
        
        public B header(final String name, final Object value, final boolean single) {
            if (value != null) {
                if (single) {
                    this.getMetadata().putSingle(name, value);
                }
                else {
                    this.getMetadata().add(name, value);
                }
            }
            else {
                this.getMetadata().remove(name);
            }
            return (B)this;
        }
    }
}
