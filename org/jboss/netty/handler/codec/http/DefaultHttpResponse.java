// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.util.internal.StringUtil;

public class DefaultHttpResponse extends DefaultHttpMessage implements HttpResponse
{
    private HttpResponseStatus status;
    
    public DefaultHttpResponse(final HttpVersion version, final HttpResponseStatus status) {
        super(version);
        this.setStatus(status);
    }
    
    public HttpResponseStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(final HttpResponseStatus status) {
        if (status == null) {
            throw new NullPointerException("status");
        }
        this.status = status;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getSimpleName());
        buf.append("(chunked: ");
        buf.append(this.isChunked());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
        buf.append(this.getProtocolVersion().getText());
        buf.append(' ');
        buf.append(this.getStatus().toString());
        buf.append(StringUtil.NEWLINE);
        this.appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }
}
