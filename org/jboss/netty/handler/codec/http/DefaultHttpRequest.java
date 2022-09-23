// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.util.internal.StringUtil;

public class DefaultHttpRequest extends DefaultHttpMessage implements HttpRequest
{
    private HttpMethod method;
    private String uri;
    
    public DefaultHttpRequest(final HttpVersion httpVersion, final HttpMethod method, final String uri) {
        super(httpVersion);
        this.setMethod(method);
        this.setUri(uri);
    }
    
    public HttpMethod getMethod() {
        return this.method;
    }
    
    public void setMethod(final HttpMethod method) {
        if (method == null) {
            throw new NullPointerException("method");
        }
        this.method = method;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public void setUri(final String uri) {
        if (uri == null) {
            throw new NullPointerException("uri");
        }
        this.uri = uri;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getSimpleName());
        buf.append("(chunked: ");
        buf.append(this.isChunked());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
        buf.append(this.getMethod().toString());
        buf.append(' ');
        buf.append(this.getUri());
        buf.append(' ');
        buf.append(this.getProtocolVersion().getText());
        buf.append(StringUtil.NEWLINE);
        this.appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }
}
