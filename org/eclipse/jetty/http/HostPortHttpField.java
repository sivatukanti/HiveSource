// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import org.eclipse.jetty.util.HostPort;

public class HostPortHttpField extends HttpField
{
    final HostPort _hostPort;
    
    public HostPortHttpField(final String authority) {
        this(HttpHeader.HOST, HttpHeader.HOST.asString(), authority);
    }
    
    protected HostPortHttpField(final HttpHeader header, final String name, final String authority) {
        super(header, name, authority);
        try {
            this._hostPort = new HostPort(authority);
        }
        catch (Exception e) {
            throw new BadMessageException(400, "Bad HostPort", e);
        }
    }
    
    public String getHost() {
        return this._hostPort.getHost();
    }
    
    public int getPort() {
        return this._hostPort.getPort();
    }
    
    public int getPort(final int defaultPort) {
        return this._hostPort.getPort(defaultPort);
    }
}
