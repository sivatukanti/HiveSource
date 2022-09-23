// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import java.util.List;
import org.eclipse.jetty.util.ArrayUtil;
import javax.servlet.ServletException;
import org.eclipse.jetty.util.MultiException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import java.util.Arrays;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Handler of multiple handlers")
public class HandlerCollection extends AbstractHandlerContainer
{
    private final boolean _mutableWhenRunning;
    private volatile Handler[] _handlers;
    
    public HandlerCollection() {
        this._mutableWhenRunning = false;
    }
    
    public HandlerCollection(final boolean mutableWhenRunning) {
        this._mutableWhenRunning = mutableWhenRunning;
    }
    
    @ManagedAttribute(value = "Wrapped handlers", readonly = true)
    @Override
    public Handler[] getHandlers() {
        return this._handlers;
    }
    
    public void setHandlers(final Handler[] handlers) {
        if (!this._mutableWhenRunning && this.isStarted()) {
            throw new IllegalStateException("STARTED");
        }
        if (handlers != null) {
            for (final Handler handler : handlers) {
                if (handler == this || (handler instanceof HandlerContainer && Arrays.asList(((HandlerContainer)handler).getChildHandlers()).contains(this))) {
                    throw new IllegalStateException("setHandler loop");
                }
            }
            for (final Handler handler : handlers) {
                if (handler.getServer() != this.getServer()) {
                    handler.setServer(this.getServer());
                }
            }
        }
        final Handler[] old = this._handlers;
        this.updateBeans(old, this._handlers = handlers);
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (this._handlers != null && this.isStarted()) {
            MultiException mex = null;
            for (int i = 0; i < this._handlers.length; ++i) {
                try {
                    this._handlers[i].handle(target, baseRequest, request, response);
                }
                catch (IOException e) {
                    throw e;
                }
                catch (RuntimeException e2) {
                    throw e2;
                }
                catch (Exception e3) {
                    if (mex == null) {
                        mex = new MultiException();
                    }
                    mex.add(e3);
                }
            }
            if (mex != null) {
                if (mex.size() == 1) {
                    throw new ServletException(mex.getThrowable(0));
                }
                throw new ServletException(mex);
            }
        }
    }
    
    public void addHandler(final Handler handler) {
        this.setHandlers(ArrayUtil.addToArray(this.getHandlers(), handler, Handler.class));
    }
    
    public void removeHandler(final Handler handler) {
        final Handler[] handlers = this.getHandlers();
        if (handlers != null && handlers.length > 0) {
            this.setHandlers(ArrayUtil.removeFromArray(handlers, handler));
        }
    }
    
    @Override
    protected void expandChildren(final List<Handler> list, final Class<?> byClass) {
        if (this.getHandlers() != null) {
            for (final Handler h : this.getHandlers()) {
                this.expandHandler(h, list, byClass);
            }
        }
    }
    
    @Override
    public void destroy() {
        if (!this.isStopped()) {
            throw new IllegalStateException("!STOPPED");
        }
        final Handler[] children = this.getChildHandlers();
        this.setHandlers(null);
        for (final Handler child : children) {
            child.destroy();
        }
        super.destroy();
    }
    
    @Override
    public String toString() {
        final Handler[] handlers = this.getHandlers();
        return super.toString() + ((handlers == null) ? "[]" : Arrays.asList(this.getHandlers()).toString());
    }
}
