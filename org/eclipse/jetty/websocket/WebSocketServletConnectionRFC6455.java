// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.util.Iterator;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import org.eclipse.jetty.io.EndPoint;

public class WebSocketServletConnectionRFC6455 extends WebSocketConnectionRFC6455 implements WebSocketServletConnection
{
    private final WebSocketFactory factory;
    
    public WebSocketServletConnectionRFC6455(final WebSocketFactory factory, final WebSocket websocket, final EndPoint endpoint, final WebSocketBuffers buffers, final long timestamp, final int maxIdleTime, final String protocol, final List<Extension> extensions, final int draft) throws IOException {
        super(websocket, endpoint, buffers, timestamp, maxIdleTime, protocol, extensions, draft);
        this.factory = factory;
    }
    
    public void handshake(final HttpServletRequest request, final HttpServletResponse response, final String subprotocol) throws IOException {
        final String key = request.getHeader("Sec-WebSocket-Key");
        response.setHeader("Upgrade", "WebSocket");
        response.addHeader("Connection", "Upgrade");
        response.addHeader("Sec-WebSocket-Accept", WebSocketConnectionRFC6455.hashKey(key));
        if (subprotocol != null) {
            response.addHeader("Sec-WebSocket-Protocol", subprotocol);
        }
        for (final Extension ext : this.getExtensions()) {
            response.addHeader("Sec-WebSocket-Extensions", ext.getParameterizedName());
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
