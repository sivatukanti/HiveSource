// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import javax.servlet.ServletException;
import java.io.IOException;
import org.eclipse.jetty.server.Handler;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;

public class HandlerList extends HandlerCollection
{
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final Handler[] handlers = this.getHandlers();
        if (handlers != null && this.isStarted()) {
            for (int i = 0; i < handlers.length; ++i) {
                handlers[i].handle(target, baseRequest, request, response);
                if (baseRequest.isHandled()) {
                    return;
                }
            }
        }
    }
}
