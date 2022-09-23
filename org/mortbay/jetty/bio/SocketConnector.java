// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.bio;

import org.mortbay.jetty.HttpException;
import org.mortbay.jetty.EofException;
import org.mortbay.log.Log;
import org.mortbay.io.bio.SocketEndPoint;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import org.mortbay.jetty.Request;
import org.mortbay.io.ByteArrayBuffer;
import org.mortbay.io.Buffer;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.io.EndPoint;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Set;
import java.net.ServerSocket;
import org.mortbay.jetty.AbstractConnector;

public class SocketConnector extends AbstractConnector
{
    protected ServerSocket _serverSocket;
    protected Set _connections;
    
    public Object getConnection() {
        return this._serverSocket;
    }
    
    public void open() throws IOException {
        if (this._serverSocket == null || this._serverSocket.isClosed()) {
            this._serverSocket = this.newServerSocket(this.getHost(), this.getPort(), this.getAcceptQueueSize());
        }
        this._serverSocket.setReuseAddress(this.getReuseAddress());
    }
    
    protected ServerSocket newServerSocket(final String host, final int port, final int backlog) throws IOException {
        final ServerSocket ss = (host == null) ? new ServerSocket(port, backlog) : new ServerSocket(port, backlog, InetAddress.getByName(host));
        return ss;
    }
    
    public void close() throws IOException {
        if (this._serverSocket != null) {
            this._serverSocket.close();
        }
        this._serverSocket = null;
    }
    
    public void accept(final int acceptorID) throws IOException, InterruptedException {
        final Socket socket = this._serverSocket.accept();
        this.configure(socket);
        final Connection connection = new Connection(socket);
        connection.dispatch();
    }
    
    protected HttpConnection newHttpConnection(final EndPoint endpoint) {
        return new HttpConnection(this, endpoint, this.getServer());
    }
    
    protected Buffer newBuffer(final int size) {
        return new ByteArrayBuffer(size);
    }
    
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        final Connection connection = (Connection)endpoint;
        if (connection._sotimeout != this._maxIdleTime) {
            connection._sotimeout = this._maxIdleTime;
            ((Socket)endpoint.getTransport()).setSoTimeout(this._maxIdleTime);
        }
        super.customize(endpoint, request);
    }
    
    public int getLocalPort() {
        if (this._serverSocket == null || this._serverSocket.isClosed()) {
            return -1;
        }
        return this._serverSocket.getLocalPort();
    }
    
    protected void doStart() throws Exception {
        this._connections = new HashSet();
        super.doStart();
    }
    
    protected void doStop() throws Exception {
        super.doStop();
        Set set = null;
        synchronized (this._connections) {
            set = new HashSet(this._connections);
        }
        for (final Connection connection : set) {
            connection.close();
        }
    }
    
    protected class Connection extends SocketEndPoint implements Runnable
    {
        boolean _dispatched;
        HttpConnection _connection;
        int _sotimeout;
        protected Socket _socket;
        
        public Connection(final Socket socket) throws IOException {
            super(socket);
            this._dispatched = false;
            this._connection = SocketConnector.this.newHttpConnection(this);
            this._sotimeout = socket.getSoTimeout();
            this._socket = socket;
        }
        
        public void dispatch() throws InterruptedException, IOException {
            if (SocketConnector.this.getThreadPool() == null || !SocketConnector.this.getThreadPool().dispatch(this)) {
                Log.warn("dispatch failed for {}", this._connection);
                this.close();
            }
        }
        
        public int fill(final Buffer buffer) throws IOException {
            final int l = super.fill(buffer);
            if (l < 0) {
                this.close();
            }
            return l;
        }
        
        public void run() {
            try {
                AbstractConnector.this.connectionOpened(this._connection);
                synchronized (SocketConnector.this._connections) {
                    SocketConnector.this._connections.add(this);
                }
                while (SocketConnector.this.isStarted() && !this.isClosed()) {
                    if (this._connection.isIdle() && SocketConnector.this.getServer().getThreadPool().isLowOnThreads()) {
                        final int lrmit = SocketConnector.this.getLowResourceMaxIdleTime();
                        if (lrmit >= 0 && this._sotimeout != lrmit) {
                            this._sotimeout = lrmit;
                            this._socket.setSoTimeout(this._sotimeout);
                        }
                    }
                    this._connection.handle();
                }
            }
            catch (EofException e) {
                Log.debug("EOF", e);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    Log.ignore(e2);
                }
            }
            catch (HttpException e3) {
                Log.debug("BAD", e3);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    Log.ignore(e2);
                }
            }
            catch (Throwable e4) {
                Log.warn("handle failed", e4);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    Log.ignore(e2);
                }
            }
            finally {
                AbstractConnector.this.connectionClosed(this._connection);
                synchronized (SocketConnector.this._connections) {
                    SocketConnector.this._connections.remove(this);
                }
            }
        }
    }
}
