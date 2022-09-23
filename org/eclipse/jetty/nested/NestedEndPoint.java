// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.nested;

import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.io.bio.StreamEndPoint;

public class NestedEndPoint extends StreamEndPoint
{
    private final HttpServletRequest _outerRequest;
    
    public NestedEndPoint(final HttpServletRequest outerRequest, final HttpServletResponse outerResponse) throws IOException {
        super(outerRequest.getInputStream(), outerResponse.getOutputStream());
        this._outerRequest = outerRequest;
    }
    
    public ServletInputStream getServletInputStream() {
        return (ServletInputStream)this.getInputStream();
    }
    
    @Override
    public String getLocalAddr() {
        return this._outerRequest.getLocalAddr();
    }
    
    @Override
    public String getLocalHost() {
        return this._outerRequest.getLocalName();
    }
    
    @Override
    public int getLocalPort() {
        return this._outerRequest.getLocalPort();
    }
    
    @Override
    public String getRemoteAddr() {
        return this._outerRequest.getRemoteAddr();
    }
    
    @Override
    public String getRemoteHost() {
        return this._outerRequest.getRemoteHost();
    }
    
    @Override
    public int getRemotePort() {
        return this._outerRequest.getRemotePort();
    }
}
