// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

import java.net.UnknownHostException;
import java.net.InetAddress;

public class HostPort
{
    public final String host;
    public final int port;
    public final InetAddress addr;
    
    public HostPort(final String host, final int port) throws UnknownHostException {
        this.host = host;
        this.port = port;
        this.addr = Util.toInetAddress(host);
    }
    
    @Override
    public String toString() {
        return this.host + ":" + this.port;
    }
}
