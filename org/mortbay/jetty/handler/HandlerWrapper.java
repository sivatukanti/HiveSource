// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import org.mortbay.jetty.Server;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.jetty.HandlerContainer;
import org.mortbay.jetty.Handler;

public class HandlerWrapper extends AbstractHandlerContainer
{
    private Handler _handler;
    
    public Handler getHandler() {
        return this._handler;
    }
    
    public void setHandler(final Handler handler) {
        try {
            final Handler old_handler = this._handler;
            if (this.getServer() != null) {
                this.getServer().getContainer().update(this, old_handler, handler, "handler");
            }
            if (handler != null) {
                handler.setServer(this.getServer());
            }
            this._handler = handler;
            if (old_handler != null && old_handler.isStarted()) {
                old_handler.stop();
            }
        }
        catch (Exception e) {
            final IllegalStateException ise = new IllegalStateException();
            ise.initCause(e);
            throw ise;
        }
    }
    
    public void addHandler(final Handler handler) {
        final Handler old = this.getHandler();
        if (old != null && !(handler instanceof HandlerContainer)) {
            throw new IllegalArgumentException("Cannot add");
        }
        this.setHandler(handler);
        if (old != null) {
            ((HandlerContainer)handler).addHandler(old);
        }
    }
    
    public void removeHandler(final Handler handler) {
        final Handler old = this.getHandler();
        if (old != null && old instanceof HandlerContainer) {
            ((HandlerContainer)old).removeHandler(handler);
        }
        else {
            if (old == null || !handler.equals(old)) {
                throw new IllegalStateException("Cannot remove");
            }
            this.setHandler(null);
        }
    }
    
    protected void doStart() throws Exception {
        if (this._handler != null) {
            this._handler.start();
        }
        super.doStart();
    }
    
    protected void doStop() throws Exception {
        super.doStop();
        if (this._handler != null) {
            this._handler.stop();
        }
    }
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        if (this._handler != null && this.isStarted()) {
            this._handler.handle(target, request, response, dispatch);
        }
    }
    
    public void setServer(final Server server) {
        final Server old_server = this.getServer();
        super.setServer(server);
        final Handler h = this.getHandler();
        if (h != null) {
            h.setServer(server);
        }
        if (server != null && server != old_server) {
            server.getContainer().update(this, null, this._handler, "handler");
        }
    }
    
    protected Object expandChildren(final Object list, final Class byClass) {
        return this.expandHandler(this._handler, list, byClass);
    }
}
