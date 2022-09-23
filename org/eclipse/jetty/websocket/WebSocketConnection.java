// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.util.List;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.nio.AsyncConnection;

public interface WebSocketConnection extends AsyncConnection
{
    void fillBuffersFrom(final Buffer p0);
    
    List<Extension> getExtensions();
    
    WebSocket.Connection getConnection();
    
    void shutdown();
}
