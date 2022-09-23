// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.eclipse.jetty.io.SelectChannelEndPoint;
import java.nio.channels.SelectionKey;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.net.SocketException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Future;
import java.nio.channels.Channel;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.annotation.Name;
import java.nio.channels.ServerSocketChannel;
import org.eclipse.jetty.io.SelectorManager;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("HTTP connector using NIO ByteChannels and Selectors")
public class ServerConnector extends AbstractNetworkConnector
{
    private final SelectorManager _manager;
    private volatile ServerSocketChannel _acceptChannel;
    private volatile boolean _inheritChannel;
    private volatile int _localPort;
    private volatile int _acceptQueueSize;
    private volatile boolean _reuseAddress;
    private volatile int _lingerTime;
    
    public ServerConnector(@Name("server") final Server server) {
        this(server, null, null, null, -1, -1, new ConnectionFactory[] { new HttpConnectionFactory() });
    }
    
    public ServerConnector(@Name("server") final Server server, @Name("acceptors") final int acceptors, @Name("selectors") final int selectors) {
        this(server, null, null, null, acceptors, selectors, new ConnectionFactory[] { new HttpConnectionFactory() });
    }
    
    public ServerConnector(@Name("server") final Server server, @Name("acceptors") final int acceptors, @Name("selectors") final int selectors, @Name("factories") final ConnectionFactory... factories) {
        this(server, null, null, null, acceptors, selectors, factories);
    }
    
    public ServerConnector(@Name("server") final Server server, @Name("factories") final ConnectionFactory... factories) {
        this(server, null, null, null, -1, -1, factories);
    }
    
    public ServerConnector(@Name("server") final Server server, @Name("sslContextFactory") final SslContextFactory sslContextFactory) {
        this(server, null, null, null, -1, -1, AbstractConnectionFactory.getFactories(sslContextFactory, new HttpConnectionFactory()));
    }
    
    public ServerConnector(@Name("server") final Server server, @Name("acceptors") final int acceptors, @Name("selectors") final int selectors, @Name("sslContextFactory") final SslContextFactory sslContextFactory) {
        this(server, null, null, null, acceptors, selectors, AbstractConnectionFactory.getFactories(sslContextFactory, new HttpConnectionFactory()));
    }
    
    public ServerConnector(@Name("server") final Server server, @Name("sslContextFactory") final SslContextFactory sslContextFactory, @Name("factories") final ConnectionFactory... factories) {
        this(server, null, null, null, -1, -1, AbstractConnectionFactory.getFactories(sslContextFactory, factories));
    }
    
    public ServerConnector(@Name("server") final Server server, @Name("executor") final Executor executor, @Name("scheduler") final Scheduler scheduler, @Name("bufferPool") final ByteBufferPool bufferPool, @Name("acceptors") final int acceptors, @Name("selectors") final int selectors, @Name("factories") final ConnectionFactory... factories) {
        super(server, executor, scheduler, bufferPool, acceptors, factories);
        this._inheritChannel = false;
        this._localPort = -1;
        this._acceptQueueSize = 0;
        this._reuseAddress = true;
        this._lingerTime = -1;
        this.addBean(this._manager = this.newSelectorManager(this.getExecutor(), this.getScheduler(), (selectors > 0) ? selectors : Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors() / 2))), true);
        this.setAcceptorPriorityDelta(-2);
    }
    
    protected SelectorManager newSelectorManager(final Executor executor, final Scheduler scheduler, final int selectors) {
        return new ServerConnectorManager(executor, scheduler, selectors);
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if (this.getAcceptors() == 0) {
            this._acceptChannel.configureBlocking(false);
            this._manager.acceptor(this._acceptChannel);
        }
    }
    
    @Override
    public boolean isOpen() {
        final ServerSocketChannel channel = this._acceptChannel;
        return channel != null && channel.isOpen();
    }
    
    @Deprecated
    public int getSelectorPriorityDelta() {
        return this._manager.getSelectorPriorityDelta();
    }
    
    @Deprecated
    public void setSelectorPriorityDelta(final int selectorPriorityDelta) {
        this._manager.setSelectorPriorityDelta(selectorPriorityDelta);
    }
    
    public boolean isInheritChannel() {
        return this._inheritChannel;
    }
    
    public void setInheritChannel(final boolean inheritChannel) {
        this._inheritChannel = inheritChannel;
    }
    
    public void open(final ServerSocketChannel acceptChannel) throws IOException {
        if (this.isStarted()) {
            throw new IllegalStateException(this.getState());
        }
        this.updateBean(this._acceptChannel, acceptChannel);
        this._acceptChannel = acceptChannel;
        this._localPort = this._acceptChannel.socket().getLocalPort();
        if (this._localPort <= 0) {
            throw new IOException("Server channel not bound");
        }
    }
    
    @Override
    public void open() throws IOException {
        if (this._acceptChannel == null) {
            (this._acceptChannel = this.openAcceptChannel()).configureBlocking(true);
            this._localPort = this._acceptChannel.socket().getLocalPort();
            if (this._localPort <= 0) {
                throw new IOException("Server channel not bound");
            }
            this.addBean(this._acceptChannel);
        }
    }
    
    protected ServerSocketChannel openAcceptChannel() throws IOException {
        ServerSocketChannel serverChannel = null;
        if (this.isInheritChannel()) {
            final Channel channel = System.inheritedChannel();
            if (channel instanceof ServerSocketChannel) {
                serverChannel = (ServerSocketChannel)channel;
            }
            else {
                this.LOG.warn("Unable to use System.inheritedChannel() [{}]. Trying a new ServerSocketChannel at {}:{}", channel, this.getHost(), this.getPort());
            }
        }
        if (serverChannel == null) {
            serverChannel = ServerSocketChannel.open();
            final InetSocketAddress bindAddress = (this.getHost() == null) ? new InetSocketAddress(this.getPort()) : new InetSocketAddress(this.getHost(), this.getPort());
            serverChannel.socket().setReuseAddress(this.getReuseAddress());
            serverChannel.socket().bind(bindAddress, this.getAcceptQueueSize());
        }
        return serverChannel;
    }
    
    @Override
    public Future<Void> shutdown() {
        return super.shutdown();
    }
    
    @Override
    public void close() {
        final ServerSocketChannel serverChannel = this._acceptChannel;
        this._acceptChannel = null;
        if (serverChannel != null) {
            this.removeBean(serverChannel);
            if (serverChannel.isOpen()) {
                try {
                    serverChannel.close();
                }
                catch (IOException e) {
                    this.LOG.warn(e);
                }
            }
        }
        this._localPort = -2;
    }
    
    public void accept(final int acceptorID) throws IOException {
        final ServerSocketChannel serverChannel = this._acceptChannel;
        if (serverChannel != null && serverChannel.isOpen()) {
            final SocketChannel channel = serverChannel.accept();
            this.accepted(channel);
        }
    }
    
    private void accepted(final SocketChannel channel) throws IOException {
        channel.configureBlocking(false);
        final Socket socket = channel.socket();
        this.configure(socket);
        this._manager.accept(channel);
    }
    
    protected void configure(final Socket socket) {
        try {
            socket.setTcpNoDelay(true);
            if (this._lingerTime >= 0) {
                socket.setSoLinger(true, this._lingerTime / 1000);
            }
            else {
                socket.setSoLinger(false, 0);
            }
        }
        catch (SocketException e) {
            this.LOG.ignore(e);
        }
    }
    
    public SelectorManager getSelectorManager() {
        return this._manager;
    }
    
    @Override
    public Object getTransport() {
        return this._acceptChannel;
    }
    
    @ManagedAttribute("local port")
    @Override
    public int getLocalPort() {
        return this._localPort;
    }
    
    protected SelectChannelEndPoint newEndPoint(final SocketChannel channel, final ManagedSelector selectSet, final SelectionKey key) throws IOException {
        return new SelectChannelEndPoint(channel, selectSet, key, this.getScheduler(), this.getIdleTimeout());
    }
    
    @ManagedAttribute("TCP/IP solinger time or -1 to disable")
    public int getSoLingerTime() {
        return this._lingerTime;
    }
    
    public void setSoLingerTime(final int lingerTime) {
        this._lingerTime = lingerTime;
    }
    
    @ManagedAttribute("Accept Queue size")
    public int getAcceptQueueSize() {
        return this._acceptQueueSize;
    }
    
    public void setAcceptQueueSize(final int acceptQueueSize) {
        this._acceptQueueSize = acceptQueueSize;
    }
    
    public boolean getReuseAddress() {
        return this._reuseAddress;
    }
    
    public void setReuseAddress(final boolean reuseAddress) {
        this._reuseAddress = reuseAddress;
    }
    
    public ExecutionStrategy.Factory getExecutionStrategyFactory() {
        return this._manager.getExecutionStrategyFactory();
    }
    
    public void setExecutionStrategyFactory(final ExecutionStrategy.Factory executionFactory) {
        this._manager.setExecutionStrategyFactory(executionFactory);
    }
    
    protected class ServerConnectorManager extends SelectorManager
    {
        public ServerConnectorManager(final Executor executor, final Scheduler scheduler, final int selectors) {
            super(executor, scheduler, selectors);
        }
        
        @Override
        protected void accepted(final SocketChannel channel) throws IOException {
            ServerConnector.this.accepted(channel);
        }
        
        @Override
        protected SelectChannelEndPoint newEndPoint(final SocketChannel channel, final ManagedSelector selectSet, final SelectionKey selectionKey) throws IOException {
            return ServerConnector.this.newEndPoint(channel, selectSet, selectionKey);
        }
        
        @Override
        public Connection newConnection(final SocketChannel channel, final EndPoint endpoint, final Object attachment) throws IOException {
            return ServerConnector.this.getDefaultConnectionFactory().newConnection(ServerConnector.this, endpoint);
        }
        
        @Override
        protected void endPointOpened(final EndPoint endpoint) {
            super.endPointOpened(endpoint);
            ServerConnector.this.onEndPointOpened(endpoint);
        }
        
        @Override
        protected void endPointClosed(final EndPoint endpoint) {
            ServerConnector.this.onEndPointClosed(endpoint);
            super.endPointClosed(endpoint);
        }
    }
}
