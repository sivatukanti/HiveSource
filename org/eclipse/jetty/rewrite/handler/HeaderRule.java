// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public abstract class HeaderRule extends Rule
{
    private String _header;
    private String _headerValue;
    
    public String getHeader() {
        return this._header;
    }
    
    public void setHeader(final String header) {
        this._header = header;
    }
    
    public String getHeaderValue() {
        return this._headerValue;
    }
    
    public void setHeaderValue(final String headerValue) {
        this._headerValue = headerValue;
    }
    
    @Override
    public String matchAndApply(final String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String requestHeaderValue = request.getHeader(this._header);
        if (requestHeaderValue != null && (this._headerValue == null || this._headerValue.equals(requestHeaderValue))) {
            this.apply(target, requestHeaderValue, request, response);
        }
        return null;
    }
    
    protected abstract String apply(final String p0, final String p1, final HttpServletRequest p2, final HttpServletResponse p3) throws IOException;
    
    @Override
    public String toString() {
        return super.toString() + "[" + this._header + ":" + this._headerValue + "]";
    }
}
