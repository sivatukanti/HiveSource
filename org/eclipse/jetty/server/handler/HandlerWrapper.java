// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import java.util.List;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import java.util.Arrays;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Handler wrapping another Handler")
public class HandlerWrapper extends AbstractHandlerContainer
{
    protected Handler _handler;
    
    @ManagedAttribute(value = "Wrapped Handler", readonly = true)
    public Handler getHandler() {
        return this._handler;
    }
    
    @Override
    public Handler[] getHandlers() {
        if (this._handler == null) {
            return new Handler[0];
        }
        return new Handler[] { this._handler };
    }
    
    public void setHandler(final Handler handler) {
        if (this.isStarted()) {
            throw new IllegalStateException("STARTED");
        }
        if (handler == this || (handler instanceof HandlerContainer && Arrays.asList(((HandlerContainer)handler).getChildHandlers()).contains(this))) {
            throw new IllegalStateException("setHandler loop");
        }
        if (handler != null) {
            handler.setServer(this.getServer());
        }
        final Handler old = this._handler;
        this.updateBean(old, this._handler = handler, true);
    }
    
    public void insertHandler(final HandlerWrapper wrapper) {
        if (wrapper == null) {
            throw new IllegalArgumentException();
        }
        HandlerWrapper tail;
        for (tail = wrapper; tail.getHandler() instanceof HandlerWrapper; tail = (HandlerWrapper)tail.getHandler()) {}
        if (tail.getHandler() != null) {
            throw new IllegalArgumentException("bad tail of inserted wrapper chain");
        }
        final Handler next = this.getHandler();
        this.setHandler(wrapper);
        tail.setHandler(next);
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final Handler handler = this._handler;
        if (handler != null) {
            handler.handle(target, baseRequest, request, response);
        }
    }
    
    @Override
    protected void expandChildren(final List<Handler> list, final Class<?> byClass) {
        this.expandHandler(this._handler, list, byClass);
    }
    
    @Override
    public void destroy() {
        if (!this.isStopped()) {
            throw new IllegalStateException("!STOPPED");
        }
        final Handler child = this.getHandler();
        if (child != null) {
            this.setHandler(null);
            child.destroy();
        }
        super.destroy();
    }
}
