// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.eclipse.jetty.io.EndPoint;

public class WebSocketServletConnectionD06 extends WebSocketConnectionD06 implements WebSocketServletConnection
{
    private final WebSocketFactory factory;
    
    public WebSocketServletConnectionD06(final WebSocketFactory factory, final WebSocket websocket, final EndPoint endpoint, final WebSocketBuffers buffers, final long timestamp, final int maxIdleTime, final String protocol) throws IOException {
        super(websocket, endpoint, buffers, timestamp, maxIdleTime, protocol);
        this.factory = factory;
    }
    
    public void handshake(final HttpServletRequest request, final HttpServletResponse response, final String subprotocol) throws IOException {
        final String key = request.getHeader("Sec-WebSocket-Key");
        response.setHeader("Upgrade", "WebSocket");
        response.addHeader("Connection", "Upgrade");
        response.addHeader("Sec-WebSocket-Accept", WebSocketConnectionD06.hashKey(key));
        if (subprotocol != null) {
            response.addHeader("Sec-WebSocket-Protocol", subprotocol);
        }
        response.sendError(101);
        this.onFrameHandshake();
        this.onWebSocketOpen();
    }
    
    @Override
    public void onClose() {
        super.onClose();
        this.factory.removeConnection(this);
    }
}
