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
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;

public class HotSwapHandler extends AbstractHandlerContainer
{
    private volatile Handler _handler;
    
    public Handler getHandler() {
        return this._handler;
    }
    
    @Override
    public Handler[] getHandlers() {
        final Handler handler = this._handler;
        if (handler == null) {
            return new Handler[0];
        }
        return new Handler[] { handler };
    }
    
    public void setHandler(final Handler handler) {
        try {
            final Server server = this.getServer();
            if (handler != null) {
                handler.setServer(server);
            }
            this.updateBean(this._handler, handler, true);
            this._handler = handler;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final Handler handler = this._handler;
        if (handler != null && this.isStarted() && handler.isStarted()) {
            handler.handle(target, baseRequest, request, response);
        }
    }
    
    @Override
    protected void expandChildren(final List<Handler> list, final Class<?> byClass) {
        final Handler handler = this._handler;
        if (handler != null) {
            this.expandHandler(handler, list, byClass);
        }
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
