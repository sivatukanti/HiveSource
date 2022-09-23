// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import java.net.InetSocketAddress;

public class Address
{
    private final String host;
    private final int port;
    
    public static Address from(final String hostAndPort) {
        final int colon = hostAndPort.indexOf(58);
        String host;
        int port;
        if (colon >= 0) {
            host = hostAndPort.substring(0, colon);
            port = Integer.parseInt(hostAndPort.substring(colon + 1));
        }
        else {
            host = hostAndPort;
            port = 0;
        }
        return new Address(host, port);
    }
    
    public Address(final String host, final int port) {
        if (host == null) {
            throw new IllegalArgumentException("Host is null");
        }
        this.host = host.trim();
        this.port = port;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final Address that = (Address)obj;
        return this.host.equals(that.host) && this.port == that.port;
    }
    
    @Override
    public int hashCode() {
        int result = this.host.hashCode();
        result = 31 * result + this.port;
        return result;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public InetSocketAddress toSocketAddress() {
        return new InetSocketAddress(this.getHost(), this.getPort());
    }
    
    @Override
    public String toString() {
        return this.host + ":" + this.port;
    }
}
