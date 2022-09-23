// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io.bio;

import java.net.InetAddress;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketEndPoint extends StreamEndPoint
{
    final Socket _socket;
    final InetSocketAddress _local;
    final InetSocketAddress _remote;
    
    public SocketEndPoint(final Socket socket) throws IOException {
        super(socket.getInputStream(), socket.getOutputStream());
        this._socket = socket;
        this._local = (InetSocketAddress)this._socket.getLocalSocketAddress();
        this._remote = (InetSocketAddress)this._socket.getRemoteSocketAddress();
    }
    
    public boolean isOpen() {
        return super.isOpen() && this._socket != null && !this._socket.isClosed() && !this._socket.isInputShutdown() && !this._socket.isOutputShutdown();
    }
    
    public void shutdownOutput() throws IOException {
        if (!this._socket.isClosed() && !this._socket.isOutputShutdown()) {
            this._socket.shutdownOutput();
        }
    }
    
    public void close() throws IOException {
        this._socket.close();
        this._in = null;
        this._out = null;
    }
    
    public String getLocalAddr() {
        if (this._local == null || this._local.getAddress() == null || this._local.getAddress().isAnyLocalAddress()) {
            return "0.0.0.0";
        }
        return this._local.getAddress().getHostAddress();
    }
    
    public String getLocalHost() {
        if (this._local == null || this._local.getAddress() == null || this._local.getAddress().isAnyLocalAddress()) {
            return "0.0.0.0";
        }
        return this._local.getAddress().getCanonicalHostName();
    }
    
    public int getLocalPort() {
        if (this._local == null) {
            return -1;
        }
        return this._local.getPort();
    }
    
    public String getRemoteAddr() {
        if (this._remote == null) {
            return null;
        }
        final InetAddress addr = this._remote.getAddress();
        return (addr == null) ? null : addr.getHostAddress();
    }
    
    public String getRemoteHost() {
        if (this._remote == null) {
            return null;
        }
        return this._remote.getAddress().getCanonicalHostName();
    }
    
    public int getRemotePort() {
        if (this._remote == null) {
            return -1;
        }
        return this._remote.getPort();
    }
    
    public Object getTransport() {
        return this._socket;
    }
}
