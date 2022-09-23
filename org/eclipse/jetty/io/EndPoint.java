// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.nio.channels.WritePendingException;
import java.nio.channels.ReadPendingException;
import org.eclipse.jetty.util.Callback;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import java.io.Closeable;

public interface EndPoint extends Closeable
{
    InetSocketAddress getLocalAddress();
    
    InetSocketAddress getRemoteAddress();
    
    boolean isOpen();
    
    long getCreatedTimeStamp();
    
    void shutdownOutput();
    
    boolean isOutputShutdown();
    
    boolean isInputShutdown();
    
    void close();
    
    int fill(final ByteBuffer p0) throws IOException;
    
    boolean flush(final ByteBuffer... p0) throws IOException;
    
    Object getTransport();
    
    long getIdleTimeout();
    
    void setIdleTimeout(final long p0);
    
    void fillInterested(final Callback p0) throws ReadPendingException;
    
    boolean tryFillInterested(final Callback p0);
    
    boolean isFillInterested();
    
    void write(final Callback p0, final ByteBuffer... p1) throws WritePendingException;
    
    Connection getConnection();
    
    void setConnection(final Connection p0);
    
    void onOpen();
    
    void onClose();
    
    boolean isOptimizedForDirectBuffers();
    
    void upgrade(final Connection p0);
}
