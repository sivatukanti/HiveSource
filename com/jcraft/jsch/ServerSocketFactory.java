// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.InetAddress;

public interface ServerSocketFactory
{
    ServerSocket createServerSocket(final int p0, final int p1, final InetAddress p2) throws IOException;
}
