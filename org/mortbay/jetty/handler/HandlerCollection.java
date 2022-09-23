// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import org.mortbay.util.LazyList;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.jetty.Server;
import org.mortbay.util.MultiException;
import org.mortbay.jetty.Handler;

public class HandlerCollection extends AbstractHandlerContainer
{
    private Handler[] _handlers;
    
    public Handler[] getHandlers() {
        return this._handlers;
    }
    
    public void setHandlers(final Handler[] handlers) {
        final Handler[] old_handlers = (Handler[])((this._handlers == null) ? null : ((Handler[])this._handlers.clone()));
        if (this.getServer() != null) {
            this.getServer().getContainer().update(this, old_handlers, handlers, "handler");
        }
        final Server server = this.getServer();
        final MultiException mex = new MultiException();
        for (int i = 0; handlers != null && i < handlers.length; ++i) {
            if (handlers[i].getServer() != server) {
                handlers[i].setServer(server);
            }
        }
        this._handlers = handlers;
        for (int i = 0; old_handlers != null && i < old_handlers.length; ++i) {
            if (old_handlers[i] != null) {
                try {
                    if (old_handlers[i].isStarted()) {
                        old_handlers[i].stop();
                    }
                }
                catch (Throwable e) {
                    mex.add(e);
                }
            }
        }
        mex.ifExceptionThrowRuntime();
    }
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        if (this._handlers != null && this.isStarted()) {
            MultiException mex = null;
            for (int i = 0; i < this._handlers.length; ++i) {
                try {
                    this._handlers[i].handle(target, request, response, dispatch);
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
    
    protected void doStart() throws Exception {
        final MultiException mex = new MultiException();
        if (this._handlers != null) {
            for (int i = 0; i < this._handlers.length; ++i) {
                try {
                    this._handlers[i].start();
                }
                catch (Throwable e) {
                    mex.add(e);
                }
            }
        }
        super.doStart();
        mex.ifExceptionThrow();
    }
    
    protected void doStop() throws Exception {
        final MultiException mex = new MultiException();
        try {
            super.doStop();
        }
        catch (Throwable e) {
            mex.add(e);
        }
        if (this._handlers != null) {
            int i = this._handlers.length;
            while (i-- > 0) {
                try {
                    this._handlers[i].stop();
                }
                catch (Throwable e2) {
                    mex.add(e2);
                }
            }
        }
        mex.ifExceptionThrow();
    }
    
    public void setServer(final Server server) {
        final Server old_server = this.getServer();
        super.setServer(server);
        final Handler[] h = this.getHandlers();
        for (int i = 0; h != null && i < h.length; ++i) {
            h[i].setServer(server);
        }
        if (server != null && server != old_server) {
            server.getContainer().update(this, null, this._handlers, "handler");
        }
    }
    
    public void addHandler(final Handler handler) {
        this.setHandlers((Handler[])LazyList.addToArray(this.getHandlers(), handler, Handler.class));
    }
    
    public void removeHandler(final Handler handler) {
        final Handler[] handlers = this.getHandlers();
        if (handlers != null && handlers.length > 0) {
            this.setHandlers((Handler[])LazyList.removeFromArray(handlers, handler));
        }
    }
    
    protected Object expandChildren(Object list, final Class byClass) {
        final Handler[] handlers = this.getHandlers();
        for (int i = 0; handlers != null && i < handlers.length; ++i) {
            list = this.expandHandler(handlers[i], list, byClass);
        }
        return list;
    }
}
