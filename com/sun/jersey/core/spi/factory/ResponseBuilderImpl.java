// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.factory;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.CacheControl;
import java.util.Date;
import javax.ws.rs.core.EntityTag;
import java.net.URI;
import java.util.Iterator;
import java.util.Locale;
import java.util.List;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import com.sun.jersey.core.header.OutBoundHeaders;
import javax.ws.rs.core.Response;

public final class ResponseBuilderImpl extends Response.ResponseBuilder
{
    private Response.StatusType statusType;
    private OutBoundHeaders headers;
    private Object entity;
    private Type entityType;
    
    public ResponseBuilderImpl() {
        this.statusType = Response.Status.NO_CONTENT;
    }
    
    private ResponseBuilderImpl(final ResponseBuilderImpl that) {
        this.statusType = Response.Status.NO_CONTENT;
        this.statusType = that.statusType;
        this.entity = that.entity;
        if (that.headers != null) {
            this.headers = new OutBoundHeaders(that.headers);
        }
        else {
            this.headers = null;
        }
        this.entityType = that.entityType;
    }
    
    public Response.ResponseBuilder entityWithType(final Object entity, final Type entityType) {
        this.entity = entity;
        this.entityType = entityType;
        return this;
    }
    
    private OutBoundHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new OutBoundHeaders();
        }
        return this.headers;
    }
    
    @Override
    public Response build() {
        final Response r = new ResponseImpl(this.statusType, this.getHeaders(), this.entity, this.entityType);
        this.reset();
        return r;
    }
    
    private void reset() {
        this.statusType = Response.Status.NO_CONTENT;
        this.headers = null;
        this.entity = null;
        this.entityType = null;
    }
    
    @Override
    public Response.ResponseBuilder clone() {
        return new ResponseBuilderImpl(this);
    }
    
    @Override
    public Response.ResponseBuilder status(final Response.StatusType status) {
        if (status == null) {
            throw new IllegalArgumentException();
        }
        this.statusType = status;
        return this;
    }
    
    @Override
    public Response.ResponseBuilder status(final int status) {
        return this.status(ResponseImpl.toStatusType(status));
    }
    
    @Override
    public Response.ResponseBuilder entity(final Object entity) {
        this.entity = entity;
        this.entityType = ((entity != null) ? entity.getClass() : null);
        return this;
    }
    
    @Override
    public Response.ResponseBuilder type(final MediaType type) {
        this.headerSingle("Content-Type", type);
        return this;
    }
    
    @Override
    public Response.ResponseBuilder type(final String type) {
        return this.type((type == null) ? null : MediaType.valueOf(type));
    }
    
    @Override
    public Response.ResponseBuilder variant(final Variant variant) {
        if (variant == null) {
            this.type((MediaType)null);
            this.language((String)null);
            this.encoding(null);
            return this;
        }
        this.type(variant.getMediaType());
        this.language(variant.getLanguage());
        this.encoding(variant.getEncoding());
        return this;
    }
    
    @Override
    public Response.ResponseBuilder variants(final List<Variant> variants) {
        if (variants == null) {
            this.header("Vary", null);
            return this;
        }
        if (variants.isEmpty()) {
            return this;
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
        return this;
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
    
    @Override
    public Response.ResponseBuilder language(final String language) {
        this.headerSingle("Content-Language", language);
        return this;
    }
    
    @Override
    public Response.ResponseBuilder language(final Locale language) {
        this.headerSingle("Content-Language", language);
        return this;
    }
    
    @Override
    public Response.ResponseBuilder location(final URI location) {
        this.headerSingle("Location", location);
        return this;
    }
    
    @Override
    public Response.ResponseBuilder contentLocation(final URI location) {
        this.headerSingle("Content-Location", location);
        return this;
    }
    
    public Response.ResponseBuilder encoding(final String encoding) {
        this.headerSingle("Content-Encoding", encoding);
        return this;
    }
    
    @Override
    public Response.ResponseBuilder tag(final EntityTag tag) {
        this.headerSingle("ETag", tag);
        return this;
    }
    
    @Override
    public Response.ResponseBuilder tag(final String tag) {
        return this.tag((tag == null) ? null : new EntityTag(tag));
    }
    
    @Override
    public Response.ResponseBuilder lastModified(final Date lastModified) {
        this.headerSingle("Last-Modified", lastModified);
        return this;
    }
    
    @Override
    public Response.ResponseBuilder cacheControl(final CacheControl cacheControl) {
        this.headerSingle("Cache-Control", cacheControl);
        return this;
    }
    
    @Override
    public Response.ResponseBuilder expires(final Date expires) {
        this.headerSingle("Expires", expires);
        return this;
    }
    
    @Override
    public Response.ResponseBuilder cookie(final NewCookie... cookies) {
        if (cookies != null) {
            for (final NewCookie cookie : cookies) {
                this.header("Set-Cookie", cookie);
            }
        }
        else {
            this.header("Set-Cookie", null);
        }
        return this;
    }
    
    @Override
    public Response.ResponseBuilder header(final String name, final Object value) {
        return this.header(name, value, false);
    }
    
    public Response.ResponseBuilder headerSingle(final String name, final Object value) {
        return this.header(name, value, true);
    }
    
    public Response.ResponseBuilder header(final String name, final Object value, final boolean single) {
        if (value != null) {
            if (single) {
                this.getHeaders().putSingle(name, value);
            }
            else {
                this.getHeaders().add(name, value);
            }
        }
        else {
            this.getHeaders().remove(name);
        }
        return this;
    }
}
