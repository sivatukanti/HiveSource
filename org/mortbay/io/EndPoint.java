// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io;

import java.io.IOException;

public interface EndPoint
{
    void shutdownOutput() throws IOException;
    
    void close() throws IOException;
    
    int fill(final Buffer p0) throws IOException;
    
    int flush(final Buffer p0) throws IOException;
    
    int flush(final Buffer p0, final Buffer p1, final Buffer p2) throws IOException;
    
    String getLocalAddr();
    
    String getLocalHost();
    
    int getLocalPort();
    
    String getRemoteAddr();
    
    String getRemoteHost();
    
    int getRemotePort();
    
    boolean isBlocking();
    
    boolean isBufferred();
    
    boolean blockReadable(final long p0) throws IOException;
    
    boolean blockWritable(final long p0) throws IOException;
    
    boolean isOpen();
    
    Object getTransport();
    
    boolean isBufferingInput();
    
    boolean isBufferingOutput();
    
    void flush() throws IOException;
}
