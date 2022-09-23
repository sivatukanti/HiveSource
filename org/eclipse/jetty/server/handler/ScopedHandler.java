// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;

public abstract class ScopedHandler extends HandlerWrapper
{
    private static final ThreadLocal<ScopedHandler> __outerScope;
    protected ScopedHandler _outerScope;
    protected ScopedHandler _nextScope;
    
    @Override
    protected void doStart() throws Exception {
        try {
            this._outerScope = ScopedHandler.__outerScope.get();
            if (this._outerScope == null) {
                ScopedHandler.__outerScope.set(this);
            }
            super.doStart();
            this._nextScope = this.getChildHandlerByClass(ScopedHandler.class);
        }
        finally {
            if (this._outerScope == null) {
                ScopedHandler.__outerScope.set(null);
            }
        }
    }
    
    @Override
    public final void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (this.isStarted()) {
            if (this._outerScope == null) {
                this.doScope(target, baseRequest, request, response);
            }
            else {
                this.doHandle(target, baseRequest, request, response);
            }
        }
    }
    
    public abstract void doScope(final String p0, final Request p1, final HttpServletRequest p2, final HttpServletResponse p3) throws IOException, ServletException;
    
    public final void nextScope(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (this._nextScope != null) {
            this._nextScope.doScope(target, baseRequest, request, response);
        }
        else if (this._outerScope != null) {
            this._outerScope.doHandle(target, baseRequest, request, response);
        }
        else {
            this.doHandle(target, baseRequest, request, response);
        }
    }
    
    public abstract void doHandle(final String p0, final Request p1, final HttpServletRequest p2, final HttpServletResponse p3) throws IOException, ServletException;
    
    public final void nextHandle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (this._nextScope != null && this._nextScope == this._handler) {
            this._nextScope.doHandle(target, baseRequest, request, response);
        }
        else if (this._handler != null) {
            this._handler.handle(target, baseRequest, request, response);
        }
    }
    
    protected boolean never() {
        return false;
    }
    
    static {
        __outerScope = new ThreadLocal<ScopedHandler>();
    }
}
