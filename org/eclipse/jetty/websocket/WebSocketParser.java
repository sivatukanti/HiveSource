// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.io.Buffer;

public interface WebSocketParser
{
    Buffer getBuffer();
    
    int parseNext();
    
    boolean isBufferEmpty();
    
    void fill(final Buffer p0);
    
    public interface FrameHandler
    {
        void onFrame(final byte p0, final byte p1, final Buffer p2);
        
        void close(final int p0, final String p1);
    }
}
