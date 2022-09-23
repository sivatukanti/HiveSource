// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

public abstract class WebSocketHandler extends HandlerWrapper implements WebSocketFactory.Acceptor
{
    private final WebSocketFactory _webSocketFactory;
    
    public WebSocketHandler() {
        this._webSocketFactory = new WebSocketFactory(this, 32768);
    }
    
    public WebSocketFactory getWebSocketFactory() {
        return this._webSocketFactory;
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (this._webSocketFactory.acceptWebSocket(request, response) || response.isCommitted()) {
            return;
        }
        super.handle(target, baseRequest, request, response);
    }
    
    public boolean checkOrigin(final HttpServletRequest request, final String origin) {
        return true;
    }
}
