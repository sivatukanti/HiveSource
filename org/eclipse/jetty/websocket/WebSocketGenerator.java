// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.io.IOException;

public interface WebSocketGenerator
{
    int flush() throws IOException;
    
    boolean isBufferEmpty();
    
    void addFrame(final byte p0, final byte p1, final byte[] p2, final int p3, final int p4) throws IOException;
}
