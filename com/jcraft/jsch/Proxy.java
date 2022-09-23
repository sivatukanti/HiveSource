// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;

public interface Proxy
{
    void connect(final SocketFactory p0, final String p1, final int p2, final int p3) throws Exception;
    
    InputStream getInputStream();
    
    OutputStream getOutputStream();
    
    Socket getSocket();
    
    void close();
}
