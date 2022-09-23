// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface WebSocketServletConnection extends WebSocketConnection
{
    void handshake(final HttpServletRequest p0, final HttpServletResponse p1, final String p2) throws IOException;
}
