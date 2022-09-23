// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

public class WebSocket13FrameDecoder extends WebSocket08FrameDecoder
{
    public WebSocket13FrameDecoder(final boolean maskedPayload, final boolean allowExtensions) {
        this(maskedPayload, allowExtensions, Long.MAX_VALUE);
    }
    
    public WebSocket13FrameDecoder(final boolean maskedPayload, final boolean allowExtensions, final long maxFramePayloadLength) {
        super(maskedPayload, allowExtensions, maxFramePayloadLength);
    }
}
