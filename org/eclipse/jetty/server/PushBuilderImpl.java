// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.http.HttpMethod;
import java.util.Set;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.util.log.Logger;

public class PushBuilderImpl implements PushBuilder
{
    private static final Logger LOG;
    private static final HttpField JettyPush;
    private final Request _request;
    private final HttpFields _fields;
    private String _method;
    private String _queryString;
    private String _sessionId;
    private boolean _conditional;
    private String _path;
    private String _etag;
    private String _lastModified;
    
    public PushBuilderImpl(final Request request, final HttpFields fields, final String method, final String queryString, final String sessionId, final boolean conditional) {
        this._request = request;
        this._fields = fields;
        this._method = method;
        this._queryString = queryString;
        this._sessionId = sessionId;
        this._conditional = conditional;
        this._fields.add(PushBuilderImpl.JettyPush);
        if (PushBuilderImpl.LOG.isDebugEnabled()) {
            PushBuilderImpl.LOG.debug("PushBuilder({} {}?{} s={} c={})", this._method, this._request.getRequestURI(), this._queryString, this._sessionId, this._conditional);
        }
    }
    
    @Override
    public String getMethod() {
        return this._method;
    }
    
    @Override
    public PushBuilder method(final String method) {
        this._method = method;
        return this;
    }
    
    @Override
    public String getQueryString() {
        return this._queryString;
    }
    
    @Override
    public PushBuilder queryString(final String queryString) {
        this._queryString = queryString;
        return this;
    }
    
    @Override
    public String getSessionId() {
        return this._sessionId;
    }
    
    @Override
    public PushBuilder sessionId(final String sessionId) {
        this._sessionId = sessionId;
        return this;
    }
    
    @Override
    public boolean isConditional() {
        return this._conditional;
    }
    
    @Override
    public PushBuilder conditional(final boolean conditional) {
        this._conditional = conditional;
        return this;
    }
    
    @Override
    public Set<String> getHeaderNames() {
        return this._fields.getFieldNamesCollection();
    }
    
    @Override
    public String getHeader(final String name) {
        return this._fields.get(name);
    }
    
    @Override
    public PushBuilder setHeader(final String name, final String value) {
        this._fields.put(name, value);
        return this;
    }
    
    @Override
    public PushBuilder addHeader(final String name, final String value) {
        this._fields.add(name, value);
        return this;
    }
    
    @Override
    public String getPath() {
        return this._path;
    }
    
    @Override
    public PushBuilder path(final String path) {
        this._path = path;
        return this;
    }
    
    @Override
    public String getEtag() {
        return this._etag;
    }
    
    @Override
    public PushBuilder etag(final String etag) {
        this._etag = etag;
        return this;
    }
    
    @Override
    public String getLastModified() {
        return this._lastModified;
    }
    
    @Override
    public PushBuilder lastModified(final String lastModified) {
        this._lastModified = lastModified;
        return this;
    }
    
    @Override
    public void push() {
        if (HttpMethod.POST.is(this._method) || HttpMethod.PUT.is(this._method)) {
            throw new IllegalStateException("Bad Method " + this._method);
        }
        if (this._path == null || this._path.length() == 0) {
            throw new IllegalStateException("Bad Path " + this._path);
        }
        String path = this._path;
        String query = this._queryString;
        final int q = path.indexOf(63);
        if (q >= 0) {
            query = ((query != null && query.length() > 0) ? (this._path.substring(q + 1) + '&' + query) : this._path.substring(q + 1));
            path = this._path.substring(0, q);
        }
        if (!path.startsWith("/")) {
            path = URIUtil.addPaths(this._request.getContextPath(), path);
        }
        String param = null;
        if (this._sessionId != null && this._request.isRequestedSessionIdFromURL()) {
            param = "jsessionid=" + this._sessionId;
        }
        if (this._conditional) {
            if (this._etag != null) {
                this._fields.add(HttpHeader.IF_NONE_MATCH, this._etag);
            }
            else if (this._lastModified != null) {
                this._fields.add(HttpHeader.IF_MODIFIED_SINCE, this._lastModified);
            }
        }
        final HttpURI uri = HttpURI.createHttpURI(this._request.getScheme(), this._request.getServerName(), this._request.getServerPort(), path, param, query, null);
        final MetaData.Request push = new MetaData.Request(this._method, uri, this._request.getHttpVersion(), this._fields);
        if (PushBuilderImpl.LOG.isDebugEnabled()) {
            PushBuilderImpl.LOG.debug("Push {} {} inm={} ims={}", this._method, uri, this._fields.get(HttpHeader.IF_NONE_MATCH), this._fields.get(HttpHeader.IF_MODIFIED_SINCE));
        }
        this._request.getHttpChannel().getHttpTransport().push(push);
        this._path = null;
        this._etag = null;
        this._lastModified = null;
    }
    
    static {
        LOG = Log.getLogger(PushBuilderImpl.class);
        JettyPush = new HttpField("x-http2-push", "PushBuilder");
    }
}
