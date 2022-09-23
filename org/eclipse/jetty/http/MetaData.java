// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.util.Collections;
import java.util.Iterator;

public class MetaData implements Iterable<HttpField>
{
    private HttpVersion _httpVersion;
    private HttpFields _fields;
    private long _contentLength;
    
    public MetaData(final HttpVersion version, final HttpFields fields) {
        this(version, fields, Long.MIN_VALUE);
    }
    
    public MetaData(final HttpVersion version, final HttpFields fields, final long contentLength) {
        this._httpVersion = version;
        this._fields = fields;
        this._contentLength = contentLength;
    }
    
    protected void recycle() {
        this._httpVersion = null;
        if (this._fields != null) {
            this._fields.clear();
        }
        this._contentLength = Long.MIN_VALUE;
    }
    
    public boolean isRequest() {
        return false;
    }
    
    public boolean isResponse() {
        return false;
    }
    
    @Deprecated
    public HttpVersion getVersion() {
        return this.getHttpVersion();
    }
    
    public HttpVersion getHttpVersion() {
        return this._httpVersion;
    }
    
    public void setHttpVersion(final HttpVersion httpVersion) {
        this._httpVersion = httpVersion;
    }
    
    public HttpFields getFields() {
        return this._fields;
    }
    
    public long getContentLength() {
        if (this._contentLength == Long.MIN_VALUE && this._fields != null) {
            final HttpField field = this._fields.getField(HttpHeader.CONTENT_LENGTH);
            this._contentLength = ((field == null) ? -1L : field.getLongValue());
        }
        return this._contentLength;
    }
    
    @Override
    public Iterator<HttpField> iterator() {
        final HttpFields fields = this.getFields();
        return (fields == null) ? Collections.emptyIterator() : fields.iterator();
    }
    
    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder();
        for (final HttpField field : this) {
            out.append(field).append(System.lineSeparator());
        }
        return out.toString();
    }
    
    public static class Request extends MetaData
    {
        private String _method;
        private HttpURI _uri;
        
        public Request(final HttpFields fields) {
            this(null, null, null, fields);
        }
        
        public Request(final String method, final HttpURI uri, final HttpVersion version, final HttpFields fields) {
            this(method, uri, version, fields, Long.MIN_VALUE);
        }
        
        public Request(final String method, final HttpURI uri, final HttpVersion version, final HttpFields fields, final long contentLength) {
            super(version, fields, contentLength);
            this._method = method;
            this._uri = uri;
        }
        
        public Request(final String method, final HttpScheme scheme, final HostPortHttpField hostPort, final String uri, final HttpVersion version, final HttpFields fields) {
            this(method, new HttpURI((scheme == null) ? null : scheme.asString(), hostPort.getHost(), hostPort.getPort(), uri), version, fields);
        }
        
        public Request(final String method, final HttpScheme scheme, final HostPortHttpField hostPort, final String uri, final HttpVersion version, final HttpFields fields, final long contentLength) {
            this(method, new HttpURI((scheme == null) ? null : scheme.asString(), hostPort.getHost(), hostPort.getPort(), uri), version, fields, contentLength);
        }
        
        public Request(final String method, final String scheme, final HostPortHttpField hostPort, final String uri, final HttpVersion version, final HttpFields fields, final long contentLength) {
            this(method, new HttpURI(scheme, hostPort.getHost(), hostPort.getPort(), uri), version, fields, contentLength);
        }
        
        public Request(final Request request) {
            this(request.getMethod(), new HttpURI(request.getURI()), request.getHttpVersion(), new HttpFields(request.getFields()), request.getContentLength());
        }
        
        public void recycle() {
            super.recycle();
            this._method = null;
            if (this._uri != null) {
                this._uri.clear();
            }
        }
        
        @Override
        public boolean isRequest() {
            return true;
        }
        
        public String getMethod() {
            return this._method;
        }
        
        public void setMethod(final String method) {
            this._method = method;
        }
        
        public HttpURI getURI() {
            return this._uri;
        }
        
        public String getURIString() {
            return (this._uri == null) ? null : this._uri.toString();
        }
        
        public void setURI(final HttpURI uri) {
            this._uri = uri;
        }
        
        @Override
        public String toString() {
            final HttpFields fields = this.getFields();
            return String.format("%s{u=%s,%s,h=%d}", this.getMethod(), this.getURI(), this.getHttpVersion(), (fields == null) ? -1 : fields.size());
        }
    }
    
    public static class Response extends MetaData
    {
        private int _status;
        private String _reason;
        
        public Response() {
            this(null, 0, null);
        }
        
        public Response(final HttpVersion version, final int status, final HttpFields fields) {
            this(version, status, fields, Long.MIN_VALUE);
        }
        
        public Response(final HttpVersion version, final int status, final HttpFields fields, final long contentLength) {
            super(version, fields, contentLength);
            this._status = status;
        }
        
        public Response(final HttpVersion version, final int status, final String reason, final HttpFields fields, final long contentLength) {
            super(version, fields, contentLength);
            this._reason = reason;
            this._status = status;
        }
        
        @Override
        public boolean isResponse() {
            return true;
        }
        
        public int getStatus() {
            return this._status;
        }
        
        public String getReason() {
            return this._reason;
        }
        
        public void setStatus(final int status) {
            this._status = status;
        }
        
        public void setReason(final String reason) {
            this._reason = reason;
        }
        
        @Override
        public String toString() {
            final HttpFields fields = this.getFields();
            return String.format("%s{s=%d,h=%d}", this.getHttpVersion(), this.getStatus(), (fields == null) ? -1 : fields.size());
        }
    }
}
