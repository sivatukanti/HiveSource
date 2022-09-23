// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.io.IOException;

public interface WebSocket
{
    void onOpen(final Connection p0);
    
    void onClose(final int p0, final String p1);
    
    public interface FrameConnection extends Connection
    {
        byte binaryOpcode();
        
        byte textOpcode();
        
        byte continuationOpcode();
        
        byte finMask();
        
        void setAllowFrameFragmentation(final boolean p0);
        
        boolean isMessageComplete(final byte p0);
        
        boolean isControl(final byte p0);
        
        boolean isText(final byte p0);
        
        boolean isBinary(final byte p0);
        
        boolean isContinuation(final byte p0);
        
        boolean isClose(final byte p0);
        
        boolean isPing(final byte p0);
        
        boolean isPong(final byte p0);
        
        boolean isAllowFrameFragmentation();
        
        void sendControl(final byte p0, final byte[] p1, final int p2, final int p3) throws IOException;
        
        void sendFrame(final byte p0, final byte p1, final byte[] p2, final int p3, final int p4) throws IOException;
    }
    
    public interface Connection
    {
        String getProtocol();
        
        void sendMessage(final String p0) throws IOException;
        
        void sendMessage(final byte[] p0, final int p1, final int p2) throws IOException;
        
        @Deprecated
        void disconnect();
        
        void close();
        
        void close(final int p0, final String p1);
        
        boolean isOpen();
        
        void setMaxIdleTime(final int p0);
        
        void setMaxTextMessageSize(final int p0);
        
        void setMaxBinaryMessageSize(final int p0);
        
        int getMaxIdleTime();
        
        int getMaxTextMessageSize();
        
        int getMaxBinaryMessageSize();
    }
    
    public interface OnFrame extends WebSocket
    {
        boolean onFrame(final byte p0, final byte p1, final byte[] p2, final int p3, final int p4);
        
        void onHandshake(final FrameConnection p0);
    }
    
    public interface OnControl extends WebSocket
    {
        boolean onControl(final byte p0, final byte[] p1, final int p2, final int p3);
    }
    
    public interface OnBinaryMessage extends WebSocket
    {
        void onMessage(final byte[] p0, final int p1, final int p2);
    }
    
    public interface OnTextMessage extends WebSocket
    {
        void onMessage(final String p0);
    }
}
