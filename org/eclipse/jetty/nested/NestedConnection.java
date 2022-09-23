// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.nested;

import org.eclipse.jetty.io.Connection;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Enumeration;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.http.Generator;
import org.eclipse.jetty.http.Parser;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.io.EndPoint;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.AbstractHttpConnection;

public class NestedConnection extends AbstractHttpConnection
{
    protected NestedConnection(final NestedConnector connector, final NestedEndPoint endp, final HttpServletRequest outerRequest, final HttpServletResponse outerResponse, final String nestedIn) throws IOException {
        super(connector, endp, connector.getServer(), new NestedParser(), new NestedGenerator(connector.getResponseBuffers(), endp, outerResponse, nestedIn), new NestedRequest(outerRequest));
        ((NestedRequest)this._request).setConnection(this);
        this._request.setDispatcherType(DispatcherType.REQUEST);
        this._request.setScheme(outerRequest.getScheme());
        this._request.setMethod(outerRequest.getMethod());
        final String uri = (outerRequest.getQueryString() == null) ? outerRequest.getRequestURI() : (outerRequest.getRequestURI() + "?" + outerRequest.getQueryString());
        this._request.setUri(new HttpURI(uri));
        this._request.setPathInfo(outerRequest.getRequestURI());
        this._request.setQueryString(outerRequest.getQueryString());
        this._request.setProtocol(outerRequest.getProtocol());
        final HttpFields fields = this.getRequestFields();
        Enumeration<String> e = outerRequest.getHeaderNames();
        while (e.hasMoreElements()) {
            final String header = e.nextElement();
            final String value = outerRequest.getHeader(header);
            fields.add(header, value);
        }
        this._request.setCookies(outerRequest.getCookies());
        e = outerRequest.getAttributeNames();
        while (e.hasMoreElements()) {
            final String attr = e.nextElement();
            this._request.setAttribute(attr, outerRequest.getAttribute(attr));
        }
        connector.customize((EndPoint)endp, this._request);
    }
    
    void service() throws IOException, ServletException {
        AbstractHttpConnection.setCurrentConnection(this);
        try {
            this.getServer().handle((AbstractHttpConnection)this);
            this.completeResponse();
            while (!this._generator.isComplete() && this._endp.isOpen()) {
                this._generator.flushBuffer();
            }
            this._endp.flush();
        }
        finally {
            AbstractHttpConnection.setCurrentConnection(null);
        }
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return ((NestedEndPoint)this._endp).getServletInputStream();
    }
    
    @Override
    public Connection handle() throws IOException {
        throw new IllegalStateException();
    }
}
