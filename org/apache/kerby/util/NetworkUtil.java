// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

import java.io.IOException;
import java.net.ServerSocket;

public final class NetworkUtil
{
    private NetworkUtil() {
    }
    
    public static int getServerPort() {
        int serverPort = 0;
        try {
            final ServerSocket serverSocket = new ServerSocket(0);
            serverPort = serverSocket.getLocalPort();
            serverSocket.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to get a server socket point");
        }
        return serverPort;
    }
}
