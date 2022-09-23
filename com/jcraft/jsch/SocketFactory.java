// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.io.OutputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.Socket;

public interface SocketFactory
{
    Socket createSocket(final String p0, final int p1) throws IOException, UnknownHostException;
    
    InputStream getInputStream(final Socket p0) throws IOException;
    
    OutputStream getOutputStream(final Socket p0) throws IOException;
}
