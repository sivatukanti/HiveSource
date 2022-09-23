// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.ajp;

import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Request;

public class Ajp13Request extends Request
{
    protected String _remoteAddr;
    protected String _remoteHost;
    protected String _remoteUser;
    protected boolean _sslSecure;
    
    public Ajp13Request(final AbstractHttpConnection connection) {
        super(connection);
    }
    
    public Ajp13Request() {
    }
    
    void setConnection(final Ajp13Connection connection) {
        super.setConnection((AbstractHttpConnection)connection);
    }
    
    public void setRemoteUser(final String remoteUser) {
        this._remoteUser = remoteUser;
    }
    
    @Override
    public String getRemoteUser() {
        if (this._remoteUser != null) {
            return this._remoteUser;
        }
        return super.getRemoteUser();
    }
    
    @Override
    public String getRemoteAddr() {
        if (this._remoteAddr != null) {
            return this._remoteAddr;
        }
        if (this._remoteHost != null) {
            return this._remoteHost;
        }
        return super.getRemoteAddr();
    }
    
    public void setRemoteAddr(final String remoteAddr) {
        this._remoteAddr = remoteAddr;
    }
    
    @Override
    public String getRemoteHost() {
        if (this._remoteHost != null) {
            return this._remoteHost;
        }
        if (this._remoteAddr != null) {
            return this._remoteAddr;
        }
        return super.getRemoteHost();
    }
    
    public void setRemoteHost(final String remoteHost) {
        this._remoteHost = remoteHost;
    }
    
    public boolean isSslSecure() {
        return this._sslSecure;
    }
    
    public void setSslSecure(final boolean sslSecure) {
        this._sslSecure = sslSecure;
    }
    
    @Override
    protected void recycle() {
        super.recycle();
        this._remoteAddr = null;
        this._remoteHost = null;
        this._remoteUser = null;
        this._sslSecure = false;
    }
}
