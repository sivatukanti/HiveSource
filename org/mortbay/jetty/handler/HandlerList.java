// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import javax.servlet.ServletException;
import java.io.IOException;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.HttpConnection;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class HandlerList extends HandlerCollection
{
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        final Handler[] handlers = this.getHandlers();
        if (handlers != null && this.isStarted()) {
            final Request base_request = HttpConnection.getCurrentConnection().getRequest();
            for (int i = 0; i < handlers.length; ++i) {
                handlers[i].handle(target, request, response, dispatch);
                if (base_request.isHandled()) {
                    return;
                }
            }
        }
    }
}
