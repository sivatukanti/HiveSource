// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.bio;

import org.eclipse.jetty.http.HttpException;
import java.net.SocketException;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ConnectedEndPoint;
import org.eclipse.jetty.io.bio.SocketEndPoint;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.component.AggregateLifeCycle;
import java.util.Iterator;
import java.util.Collection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.BlockingHttpConnection;
import org.eclipse.jetty.io.Connection;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.HashSet;
import org.eclipse.jetty.io.EndPoint;
import java.util.Set;
import java.net.ServerSocket;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.AbstractConnector;

public class SocketConnector extends AbstractConnector
{
    private static final Logger LOG;
    protected ServerSocket _serverSocket;
    protected final Set<EndPoint> _connections;
    protected volatile int _localPort;
    
    public SocketConnector() {
        this._localPort = -1;
        this._connections = new HashSet<EndPoint>();
    }
    
    public Object getConnection() {
        return this._serverSocket;
    }
    
    public void open() throws IOException {
        if (this._serverSocket == null || this._serverSocket.isClosed()) {
            this._serverSocket = this.newServerSocket(this.getHost(), this.getPort(), this.getAcceptQueueSize());
        }
        this._serverSocket.setReuseAddress(this.getReuseAddress());
        this._localPort = this._serverSocket.getLocalPort();
        if (this._localPort <= 0) {
            throw new IllegalStateException("port not allocated for " + this);
        }
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
        this._localPort = -2;
    }
    
    public void accept(final int acceptorID) throws IOException, InterruptedException {
        final Socket socket = this._serverSocket.accept();
        this.configure(socket);
        final ConnectorEndPoint connection = new ConnectorEndPoint(socket);
        connection.dispatch();
    }
    
    protected Connection newConnection(final EndPoint endpoint) {
        return new BlockingHttpConnection(this, endpoint, this.getServer());
    }
    
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        final ConnectorEndPoint connection = (ConnectorEndPoint)endpoint;
        final int lrmit = this.isLowResources() ? this._lowResourceMaxIdleTime : this._maxIdleTime;
        connection.setMaxIdleTime(lrmit);
        super.customize(endpoint, request);
    }
    
    public int getLocalPort() {
        return this._localPort;
    }
    
    @Override
    protected void doStart() throws Exception {
        this._connections.clear();
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        final Set<EndPoint> set = new HashSet<EndPoint>();
        synchronized (this._connections) {
            set.addAll(this._connections);
        }
        for (final EndPoint endPoint : set) {
            final ConnectorEndPoint connection = (ConnectorEndPoint)endPoint;
            connection.close();
        }
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        super.dump(out, indent);
        final Set<EndPoint> connections = new HashSet<EndPoint>();
        synchronized (this._connections) {
            connections.addAll(this._connections);
        }
        AggregateLifeCycle.dump(out, indent, connections);
    }
    
    static /* synthetic */ void access$100(final SocketConnector x0, final Connection x1, final Connection x2) {
        x0.connectionUpgraded(x1, x2);
    }
    
    static /* synthetic */ void access$300(final SocketConnector x0, final Connection x1) {
        x0.connectionOpened(x1);
    }
    
    static /* synthetic */ void access$400(final SocketConnector x0, final Connection x1) {
        x0.connectionClosed(x1);
    }
    
    static {
        LOG = Log.getLogger(SocketConnector.class);
    }
    
    protected class ConnectorEndPoint extends SocketEndPoint implements Runnable, ConnectedEndPoint
    {
        volatile Connection _connection;
        protected final Socket _socket;
        
        public ConnectorEndPoint(final Socket socket) throws IOException {
            super(socket, SocketConnector.this._maxIdleTime);
            this._connection = SocketConnector.this.newConnection(this);
            this._socket = socket;
        }
        
        public Connection getConnection() {
            return this._connection;
        }
        
        public void setConnection(final Connection connection) {
            if (this._connection != connection && this._connection != null) {
                SocketConnector.access$100(SocketConnector.this, this._connection, connection);
            }
            this._connection = connection;
        }
        
        public void dispatch() throws IOException {
            if (SocketConnector.this.getThreadPool() == null || !SocketConnector.this.getThreadPool().dispatch((Runnable)this)) {
                SocketConnector.LOG.warn("dispatch failed for {}", this._connection);
                this.close();
            }
        }
        
        @Override
        public int fill(final Buffer buffer) throws IOException {
            final int l = super.fill(buffer);
            if (l < 0) {
                this.close();
            }
            return l;
        }
        
        @Override
        public void close() throws IOException {
            if (this._connection instanceof AbstractHttpConnection) {
                ((AbstractHttpConnection)this._connection).getRequest().getAsyncContinuation().cancel();
            }
            super.close();
        }
        
        public void run() {
            try {
                SocketConnector.access$300(SocketConnector.this, this._connection);
                synchronized (SocketConnector.this._connections) {
                    SocketConnector.this._connections.add(this);
                }
                while (SocketConnector.this.isStarted() && !this.isClosed()) {
                    if (this._connection.isIdle() && SocketConnector.this.isLowResources()) {
                        this.setMaxIdleTime(SocketConnector.this.getLowResourcesMaxIdleTime());
                    }
                    this._connection = this._connection.handle();
                }
            }
            catch (EofException e) {
                SocketConnector.LOG.debug("EOF", e);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    SocketConnector.LOG.ignore(e2);
                }
            }
            catch (SocketException e3) {
                SocketConnector.LOG.debug("EOF", e3);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    SocketConnector.LOG.ignore(e2);
                }
            }
            catch (HttpException e4) {
                SocketConnector.LOG.debug("BAD", e4);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    SocketConnector.LOG.ignore(e2);
                }
            }
            catch (Exception e5) {
                SocketConnector.LOG.warn("handle failed?", e5);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    SocketConnector.LOG.ignore(e2);
                }
            }
            finally {
                SocketConnector.access$400(SocketConnector.this, this._connection);
                synchronized (SocketConnector.this._connections) {
                    SocketConnector.this._connections.remove(this);
                }
                try {
                    if (!this._socket.isClosed()) {
                        final long timestamp = System.currentTimeMillis();
                        final int max_idle = this.getMaxIdleTime();
                        this._socket.setSoTimeout(this.getMaxIdleTime());
                        int c = 0;
                        do {
                            c = this._socket.getInputStream().read();
                        } while (c >= 0 && System.currentTimeMillis() - timestamp < max_idle);
                        if (!this._socket.isClosed()) {
                            this._socket.close();
                        }
                    }
                }
                catch (IOException e6) {
                    SocketConnector.LOG.ignore(e6);
                }
            }
        }
    }
}
