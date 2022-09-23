// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.http.HttpURI;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.eclipse.jetty.io.EndPoint;

public class WebSocketServletConnectionD00 extends WebSocketConnectionD00 implements WebSocketServletConnection
{
    private final WebSocketFactory factory;
    
    public WebSocketServletConnectionD00(final WebSocketFactory factory, final WebSocket websocket, final EndPoint endpoint, final WebSocketBuffers buffers, final long timestamp, final int maxIdleTime, final String protocol) throws IOException {
        super(websocket, endpoint, buffers, timestamp, maxIdleTime, protocol);
        this.factory = factory;
    }
    
    public void handshake(final HttpServletRequest request, final HttpServletResponse response, final String subprotocol) throws IOException {
        String uri = request.getRequestURI();
        final String query = request.getQueryString();
        if (query != null && query.length() > 0) {
            uri = uri + "?" + query;
        }
        uri = new HttpURI(uri).toString();
        final String host = request.getHeader("Host");
        String origin = request.getHeader("Sec-WebSocket-Origin");
        if (origin == null) {
            origin = request.getHeader("Origin");
        }
        if (origin != null) {
            origin = QuotedStringTokenizer.quoteIfNeeded(origin, "\r\n");
        }
        final String key1 = request.getHeader("Sec-WebSocket-Key1");
        if (key1 != null) {
            final String key2 = request.getHeader("Sec-WebSocket-Key2");
            this.setHixieKeys(key1, key2);
            response.setHeader("Upgrade", "WebSocket");
            response.addHeader("Connection", "Upgrade");
            if (origin != null) {
                response.addHeader("Sec-WebSocket-Origin", origin);
            }
            response.addHeader("Sec-WebSocket-Location", (request.isSecure() ? "wss://" : "ws://") + host + uri);
            if (subprotocol != null) {
                response.addHeader("Sec-WebSocket-Protocol", subprotocol);
            }
            response.sendError(101, "WebSocket Protocol Handshake");
        }
        else {
            response.setHeader("Upgrade", "WebSocket");
            response.addHeader("Connection", "Upgrade");
            response.addHeader("WebSocket-Origin", origin);
            response.addHeader("WebSocket-Location", (request.isSecure() ? "wss://" : "ws://") + host + uri);
            if (subprotocol != null) {
                response.addHeader("WebSocket-Protocol", subprotocol);
            }
            response.sendError(101, "Web Socket Protocol Handshake");
            response.flushBuffer();
            this.onFrameHandshake();
            this.onWebsocketOpen();
        }
    }
    
    @Override
    public void onClose() {
        super.onClose();
        this.factory.removeConnection(this);
    }
}
