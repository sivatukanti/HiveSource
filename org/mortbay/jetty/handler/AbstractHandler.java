// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import org.mortbay.log.Log;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Handler;
import org.mortbay.component.AbstractLifeCycle;

public abstract class AbstractHandler extends AbstractLifeCycle implements Handler
{
    protected String _string;
    private Server _server;
    
    protected void doStart() throws Exception {
        Log.debug("starting {}", this);
    }
    
    protected void doStop() throws Exception {
        Log.debug("stopping {}", this);
    }
    
    public String toString() {
        if (this._string == null) {
            this._string = super.toString();
            this._string = this._string.substring(this._string.lastIndexOf(46) + 1);
        }
        return this._string;
    }
    
    public void setServer(final Server server) {
        final Server old_server = this._server;
        if (old_server != null && old_server != server) {
            old_server.getContainer().removeBean(this);
        }
        this._server = server;
        if (this._server != null && this._server != old_server) {
            this._server.getContainer().addBean(this);
        }
    }
    
    public Server getServer() {
        return this._server;
    }
    
    public void destroy() {
        if (!this.isStopped()) {
            throw new IllegalStateException("!STOPPED");
        }
        if (this._server != null) {
            this._server.getContainer().removeBean(this);
        }
    }
}
