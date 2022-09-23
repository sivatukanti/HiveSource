// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.bio;

import org.eclipse.jetty.util.log.Log;
import java.net.InetAddress;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.eclipse.jetty.util.log.Logger;

public class SocketEndPoint extends StreamEndPoint
{
    private static final Logger LOG;
    final Socket _socket;
    final InetSocketAddress _local;
    final InetSocketAddress _remote;
    
    public SocketEndPoint(final Socket socket) throws IOException {
        super(socket.getInputStream(), socket.getOutputStream());
        this._socket = socket;
        this._local = (InetSocketAddress)this._socket.getLocalSocketAddress();
        this._remote = (InetSocketAddress)this._socket.getRemoteSocketAddress();
        super.setMaxIdleTime(this._socket.getSoTimeout());
    }
    
    protected SocketEndPoint(final Socket socket, final int maxIdleTime) throws IOException {
        super(socket.getInputStream(), socket.getOutputStream());
        this._socket = socket;
        this._local = (InetSocketAddress)this._socket.getLocalSocketAddress();
        this._remote = (InetSocketAddress)this._socket.getRemoteSocketAddress();
        this._socket.setSoTimeout((maxIdleTime > 0) ? maxIdleTime : 0);
        super.setMaxIdleTime(maxIdleTime);
    }
    
    @Override
    public boolean isOpen() {
        return super.isOpen() && this._socket != null && !this._socket.isClosed();
    }
    
    @Override
    public boolean isInputShutdown() {
        if (this._socket instanceof SSLSocket) {
            return super.isInputShutdown();
        }
        return this._socket.isClosed() || this._socket.isInputShutdown();
    }
    
    @Override
    public boolean isOutputShutdown() {
        if (this._socket instanceof SSLSocket) {
            return super.isOutputShutdown();
        }
        return this._socket.isClosed() || this._socket.isOutputShutdown();
    }
    
    protected final void shutdownSocketOutput() throws IOException {
        if (!this._socket.isClosed()) {
            if (!this._socket.isOutputShutdown()) {
                this._socket.shutdownOutput();
            }
            if (this._socket.isInputShutdown()) {
                this._socket.close();
            }
        }
    }
    
    @Override
    public void shutdownOutput() throws IOException {
        if (this._socket instanceof SSLSocket) {
            super.shutdownOutput();
        }
        else {
            this.shutdownSocketOutput();
        }
    }
    
    public void shutdownSocketInput() throws IOException {
        if (!this._socket.isClosed()) {
            if (!this._socket.isInputShutdown()) {
                this._socket.shutdownInput();
            }
            if (this._socket.isOutputShutdown()) {
                this._socket.close();
            }
        }
    }
    
    @Override
    public void shutdownInput() throws IOException {
        if (this._socket instanceof SSLSocket) {
            super.shutdownInput();
        }
        else {
            this.shutdownSocketInput();
        }
    }
    
    @Override
    public void close() throws IOException {
        this._socket.close();
        this._in = null;
        this._out = null;
    }
    
    @Override
    public String getLocalAddr() {
        if (this._local == null || this._local.getAddress() == null || this._local.getAddress().isAnyLocalAddress()) {
            return "0.0.0.0";
        }
        return this._local.getAddress().getHostAddress();
    }
    
    @Override
    public String getLocalHost() {
        if (this._local == null || this._local.getAddress() == null || this._local.getAddress().isAnyLocalAddress()) {
            return "0.0.0.0";
        }
        return this._local.getAddress().getCanonicalHostName();
    }
    
    @Override
    public int getLocalPort() {
        if (this._local == null) {
            return -1;
        }
        return this._local.getPort();
    }
    
    @Override
    public String getRemoteAddr() {
        if (this._remote == null) {
            return null;
        }
        final InetAddress addr = this._remote.getAddress();
        return (addr == null) ? null : addr.getHostAddress();
    }
    
    @Override
    public String getRemoteHost() {
        if (this._remote == null) {
            return null;
        }
        return this._remote.getAddress().getCanonicalHostName();
    }
    
    @Override
    public int getRemotePort() {
        if (this._remote == null) {
            return -1;
        }
        return this._remote.getPort();
    }
    
    @Override
    public Object getTransport() {
        return this._socket;
    }
    
    @Override
    public void setMaxIdleTime(final int timeMs) throws IOException {
        if (timeMs != this.getMaxIdleTime()) {
            this._socket.setSoTimeout((timeMs > 0) ? timeMs : 0);
        }
        super.setMaxIdleTime(timeMs);
    }
    
    @Override
    protected void idleExpired() throws IOException {
        try {
            if (!this.isInputShutdown()) {
                this.shutdownInput();
            }
        }
        catch (IOException e) {
            SocketEndPoint.LOG.ignore(e);
            this._socket.close();
        }
    }
    
    @Override
    public String toString() {
        return this._local + " <--> " + this._remote;
    }
    
    static {
        LOG = Log.getLogger(SocketEndPoint.class);
    }
}
