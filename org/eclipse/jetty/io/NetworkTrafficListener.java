// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.nio.ByteBuffer;
import java.net.Socket;

public interface NetworkTrafficListener
{
    void opened(final Socket p0);
    
    void incoming(final Socket p0, final ByteBuffer p1);
    
    void outgoing(final Socket p0, final ByteBuffer p1);
    
    void closed(final Socket p0);
    
    public static class Adapter implements NetworkTrafficListener
    {
        @Override
        public void opened(final Socket socket) {
        }
        
        @Override
        public void incoming(final Socket socket, final ByteBuffer bytes) {
        }
        
        @Override
        public void outgoing(final Socket socket, final ByteBuffer bytes) {
        }
        
        @Override
        public void closed(final Socket socket) {
        }
    }
}
