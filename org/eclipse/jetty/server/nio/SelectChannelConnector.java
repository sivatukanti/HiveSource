// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.nio;

import org.eclipse.jetty.io.ConnectedEndPoint;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.AsyncHttpConnection;
import org.eclipse.jetty.io.nio.AsyncConnection;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import java.nio.channels.SelectionKey;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.io.EndPoint;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import org.eclipse.jetty.io.nio.SelectorManager;
import java.nio.channels.ServerSocketChannel;

public class SelectChannelConnector extends AbstractNIOConnector
{
    protected ServerSocketChannel _acceptChannel;
    private int _lowResourcesConnections;
    private int _lowResourcesMaxIdleTime;
    private int _localPort;
    private final SelectorManager _manager;
    
    public SelectChannelConnector() {
        this._localPort = -1;
        (this._manager = new ConnectorSelectorManager()).setMaxIdleTime(this.getMaxIdleTime());
        this.addBean(this._manager, true);
        this.setAcceptors(Math.max(1, (Runtime.getRuntime().availableProcessors() + 3) / 4));
    }
    
    public void accept(final int acceptorID) throws IOException {
        final ServerSocketChannel server;
        synchronized (this) {
            server = this._acceptChannel;
        }
        if (server != null && server.isOpen() && this._manager.isStarted()) {
            final SocketChannel channel = server.accept();
            channel.configureBlocking(false);
            final Socket socket = channel.socket();
            this.configure(socket);
            this._manager.register(channel);
        }
    }
    
    public void close() throws IOException {
        synchronized (this) {
            if (this._acceptChannel != null) {
                this.removeBean(this._acceptChannel);
                if (this._acceptChannel.isOpen()) {
                    this._acceptChannel.close();
                }
            }
            this._acceptChannel = null;
            this._localPort = -2;
        }
    }
    
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        request.setTimeStamp(System.currentTimeMillis());
        endpoint.setMaxIdleTime(this._maxIdleTime);
        super.customize(endpoint, request);
    }
    
    public void persist(final EndPoint endpoint) throws IOException {
        final AsyncEndPoint aEndp = (AsyncEndPoint)endpoint;
        aEndp.setCheckForIdle(true);
        super.persist(endpoint);
    }
    
    public SelectorManager getSelectorManager() {
        return this._manager;
    }
    
    public synchronized Object getConnection() {
        return this._acceptChannel;
    }
    
    public int getLocalPort() {
        synchronized (this) {
            return this._localPort;
        }
    }
    
    public void open() throws IOException {
        synchronized (this) {
            if (this._acceptChannel == null) {
                (this._acceptChannel = ServerSocketChannel.open()).configureBlocking(true);
                this._acceptChannel.socket().setReuseAddress(this.getReuseAddress());
                final InetSocketAddress addr = (this.getHost() == null) ? new InetSocketAddress(this.getPort()) : new InetSocketAddress(this.getHost(), this.getPort());
                this._acceptChannel.socket().bind(addr, this.getAcceptQueueSize());
                this._localPort = this._acceptChannel.socket().getLocalPort();
                if (this._localPort <= 0) {
                    throw new IOException("Server channel not bound");
                }
                this.addBean(this._acceptChannel);
            }
        }
    }
    
    public void setMaxIdleTime(final int maxIdleTime) {
        this._manager.setMaxIdleTime(maxIdleTime);
        super.setMaxIdleTime(maxIdleTime);
    }
    
    public int getLowResourcesConnections() {
        return this._lowResourcesConnections;
    }
    
    public void setLowResourcesConnections(final int lowResourcesConnections) {
        this._lowResourcesConnections = lowResourcesConnections;
    }
    
    public int getLowResourcesMaxIdleTime() {
        return this._lowResourcesMaxIdleTime;
    }
    
    public void setLowResourcesMaxIdleTime(final int lowResourcesMaxIdleTime) {
        super.setLowResourcesMaxIdleTime(this._lowResourcesMaxIdleTime = lowResourcesMaxIdleTime);
    }
    
    @Override
    protected void doStart() throws Exception {
        this._manager.setSelectSets(this.getAcceptors());
        this._manager.setMaxIdleTime(this.getMaxIdleTime());
        this._manager.setLowResourcesConnections(this.getLowResourcesConnections());
        this._manager.setLowResourcesMaxIdleTime(this.getLowResourcesMaxIdleTime());
        super.doStart();
    }
    
    protected SelectChannelEndPoint newEndPoint(final SocketChannel channel, final SelectorManager.SelectSet selectSet, final SelectionKey key) throws IOException {
        final SelectChannelEndPoint endp = new SelectChannelEndPoint(channel, selectSet, key, this._maxIdleTime);
        endp.setConnection(selectSet.getManager().newConnection(channel, endp, key.attachment()));
        return endp;
    }
    
    protected void endPointClosed(final SelectChannelEndPoint endpoint) {
        this.connectionClosed(endpoint.getConnection());
    }
    
    protected AsyncConnection newConnection(final SocketChannel channel, final AsyncEndPoint endpoint) {
        return new AsyncHttpConnection(this, endpoint, this.getServer());
    }
    
    static /* synthetic */ void access$100(final SelectChannelConnector x0, final Connection x1) {
        x0.connectionOpened(x1);
    }
    
    static /* synthetic */ void access$200(final SelectChannelConnector x0, final Connection x1, final Connection x2) {
        x0.connectionUpgraded(x1, x2);
    }
    
    private final class ConnectorSelectorManager extends SelectorManager
    {
        @Override
        public boolean dispatch(final Runnable task) {
            ThreadPool pool = SelectChannelConnector.this.getThreadPool();
            if (pool == null) {
                pool = SelectChannelConnector.this.getServer().getThreadPool();
            }
            return pool.dispatch(task);
        }
        
        @Override
        protected void endPointClosed(final SelectChannelEndPoint endpoint) {
            SelectChannelConnector.this.endPointClosed(endpoint);
        }
        
        @Override
        protected void endPointOpened(final SelectChannelEndPoint endpoint) {
            SelectChannelConnector.access$100(SelectChannelConnector.this, endpoint.getConnection());
        }
        
        @Override
        protected void endPointUpgraded(final ConnectedEndPoint endpoint, final Connection oldConnection) {
            SelectChannelConnector.access$200(SelectChannelConnector.this, oldConnection, endpoint.getConnection());
        }
        
        @Override
        public AsyncConnection newConnection(final SocketChannel channel, final AsyncEndPoint endpoint, final Object attachment) {
            return SelectChannelConnector.this.newConnection(channel, endpoint);
        }
        
        @Override
        protected SelectChannelEndPoint newEndPoint(final SocketChannel channel, final SelectSet selectSet, final SelectionKey sKey) throws IOException {
            return SelectChannelConnector.this.newEndPoint(channel, selectSet, sKey);
        }
    }
}
