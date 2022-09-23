// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.net.UnknownHostException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.net.Socket;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.net.SocketFactory;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class StandardSocketFactory extends SocketFactory
{
    @Override
    public Socket createSocket() throws IOException {
        return SocketChannel.open().socket();
    }
    
    @Override
    public Socket createSocket(final InetAddress addr, final int port) throws IOException {
        final Socket socket = this.createSocket();
        socket.connect(new InetSocketAddress(addr, port));
        return socket;
    }
    
    @Override
    public Socket createSocket(final InetAddress addr, final int port, final InetAddress localHostAddr, final int localPort) throws IOException {
        final Socket socket = this.createSocket();
        socket.bind(new InetSocketAddress(localHostAddr, localPort));
        socket.connect(new InetSocketAddress(addr, port));
        return socket;
    }
    
    @Override
    public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
        final Socket socket = this.createSocket();
        socket.connect(new InetSocketAddress(host, port));
        return socket;
    }
    
    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localHostAddr, final int localPort) throws IOException, UnknownHostException {
        final Socket socket = this.createSocket();
        socket.bind(new InetSocketAddress(localHostAddr, localPort));
        socket.connect(new InetSocketAddress(host, port));
        return socket;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj != null && obj.getClass().equals(this.getClass()));
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
