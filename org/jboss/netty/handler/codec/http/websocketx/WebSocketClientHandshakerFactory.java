// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import java.util.Map;
import java.net.URI;

public class WebSocketClientHandshakerFactory
{
    public WebSocketClientHandshaker newHandshaker(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final Map<String, String> customHeaders) {
        return this.newHandshaker(webSocketURL, version, subprotocol, allowExtensions, customHeaders, Long.MAX_VALUE);
    }
    
    public WebSocketClientHandshaker newHandshaker(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final Map<String, String> customHeaders, final long maxFramePayloadLength) {
        if (version == WebSocketVersion.V13) {
            return new WebSocketClientHandshaker13(webSocketURL, WebSocketVersion.V13, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength);
        }
        if (version == WebSocketVersion.V08) {
            return new WebSocketClientHandshaker08(webSocketURL, WebSocketVersion.V08, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength);
        }
        if (version == WebSocketVersion.V07) {
            return new WebSocketClientHandshaker07(webSocketURL, WebSocketVersion.V07, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength);
        }
        if (version == WebSocketVersion.V00) {
            return new WebSocketClientHandshaker00(webSocketURL, WebSocketVersion.V00, subprotocol, customHeaders, maxFramePayloadLength);
        }
        throw new WebSocketHandshakeException("Protocol version " + version.toString() + " not supported.");
    }
}
