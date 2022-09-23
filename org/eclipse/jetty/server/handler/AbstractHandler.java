// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

@ManagedObject("Jetty Handler")
public abstract class AbstractHandler extends ContainerLifeCycle implements Handler
{
    private static final Logger LOG;
    private Server _server;
    
    @Override
    protected void doStart() throws Exception {
        if (AbstractHandler.LOG.isDebugEnabled()) {
            AbstractHandler.LOG.debug("starting {}", this);
        }
        if (this._server == null) {
            AbstractHandler.LOG.warn("No Server set for {}", this);
        }
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        if (AbstractHandler.LOG.isDebugEnabled()) {
            AbstractHandler.LOG.debug("stopping {}", this);
        }
        super.doStop();
    }
    
    @Override
    public void setServer(final Server server) {
        if (this._server == server) {
            return;
        }
        if (this.isStarted()) {
            throw new IllegalStateException("STARTED");
        }
        this._server = server;
    }
    
    @Override
    public Server getServer() {
        return this._server;
    }
    
    @Override
    public void destroy() {
        if (!this.isStopped()) {
            throw new IllegalStateException("!STOPPED");
        }
        super.destroy();
    }
    
    public void dumpThis(final Appendable out) throws IOException {
        out.append(this.toString()).append(" - ").append(this.getState()).append('\n');
    }
    
    static {
        LOG = Log.getLogger(AbstractHandler.class);
    }
}
