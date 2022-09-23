// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.util.Map;

public interface Extension extends WebSocketParser.FrameHandler, WebSocketGenerator
{
    String getName();
    
    String getParameterizedName();
    
    boolean init(final Map<String, String> p0);
    
    void bind(final WebSocket.FrameConnection p0, final WebSocketParser.FrameHandler p1, final WebSocketGenerator p2);
}
