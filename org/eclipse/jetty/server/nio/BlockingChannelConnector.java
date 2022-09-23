// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.nio;

import org.eclipse.jetty.http.HttpException;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.BlockingHttpConnection;
import org.eclipse.jetty.io.ConnectedEndPoint;
import org.eclipse.jetty.io.nio.ChannelEndPoint;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.io.EndPoint;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.channels.ByteChannel;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import org.eclipse.jetty.util.ConcurrentHashSet;
import java.util.Set;
import java.nio.channels.ServerSocketChannel;
import org.eclipse.jetty.util.log.Logger;

public class BlockingChannelConnector extends AbstractNIOConnector
{
    private static final Logger LOG;
    private transient ServerSocketChannel _acceptChannel;
    private final Set<BlockingChannelEndPoint> _endpoints;
    
    public BlockingChannelConnector() {
        this._endpoints = new ConcurrentHashSet<BlockingChannelEndPoint>();
    }
    
    public Object getConnection() {
        return this._acceptChannel;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        this.getThreadPool().dispatch((Runnable)new Runnable() {
            public void run() {
                while (BlockingChannelConnector.this.isRunning()) {
                    try {
                        Thread.sleep(400L);
                        final long now = System.currentTimeMillis();
                        for (final BlockingChannelEndPoint endp : BlockingChannelConnector.this._endpoints) {
                            endp.checkIdleTimestamp(now);
                        }
                    }
                    catch (InterruptedException e) {
                        BlockingChannelConnector.LOG.ignore(e);
                    }
                    catch (Exception e2) {
                        BlockingChannelConnector.LOG.warn(e2);
                    }
                }
            }
        });
    }
    
    public void open() throws IOException {
        (this._acceptChannel = ServerSocketChannel.open()).configureBlocking(true);
        final InetSocketAddress addr = (this.getHost() == null) ? new InetSocketAddress(this.getPort()) : new InetSocketAddress(this.getHost(), this.getPort());
        this._acceptChannel.socket().bind(addr, this.getAcceptQueueSize());
    }
    
    public void close() throws IOException {
        if (this._acceptChannel != null) {
            this._acceptChannel.close();
        }
        this._acceptChannel = null;
    }
    
    public void accept(final int acceptorID) throws IOException, InterruptedException {
        final SocketChannel channel = this._acceptChannel.accept();
        channel.configureBlocking(true);
        final Socket socket = channel.socket();
        this.configure(socket);
        final BlockingChannelEndPoint connection = new BlockingChannelEndPoint(channel);
        connection.dispatch();
    }
    
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        super.customize(endpoint, request);
        endpoint.setMaxIdleTime(this._maxIdleTime);
        this.configure(((SocketChannel)endpoint.getTransport()).socket());
    }
    
    public int getLocalPort() {
        if (this._acceptChannel == null || !this._acceptChannel.isOpen()) {
            return -1;
        }
        return this._acceptChannel.socket().getLocalPort();
    }
    
    static /* synthetic */ void access$300(final BlockingChannelConnector x0, final Connection x1) {
        x0.connectionOpened(x1);
    }
    
    static /* synthetic */ void access$400(final BlockingChannelConnector x0, final Connection x1) {
        x0.connectionClosed(x1);
    }
    
    static {
        LOG = Log.getLogger(BlockingChannelConnector.class);
    }
    
    private class BlockingChannelEndPoint extends ChannelEndPoint implements Runnable, ConnectedEndPoint
    {
        private Connection _connection;
        private int _timeout;
        private volatile long _idleTimestamp;
        
        BlockingChannelEndPoint(final ByteChannel channel) throws IOException {
            super(channel, BlockingChannelConnector.this._maxIdleTime);
            this._connection = new BlockingHttpConnection(BlockingChannelConnector.this, this, BlockingChannelConnector.this.getServer());
        }
        
        public Connection getConnection() {
            return this._connection;
        }
        
        public void setConnection(final Connection connection) {
            this._connection = connection;
        }
        
        public void checkIdleTimestamp(final long now) {
            if (this._idleTimestamp != 0L && this._timeout > 0 && now > this._idleTimestamp + this._timeout) {
                this.idleExpired();
            }
        }
        
        protected void idleExpired() {
            try {
                super.close();
            }
            catch (IOException e) {
                BlockingChannelConnector.LOG.ignore(e);
            }
        }
        
        void dispatch() throws IOException {
            if (!BlockingChannelConnector.this.getThreadPool().dispatch((Runnable)this)) {
                BlockingChannelConnector.LOG.warn("dispatch failed for  {}", this._connection);
                super.close();
            }
        }
        
        @Override
        public int fill(final Buffer buffer) throws IOException {
            this._idleTimestamp = System.currentTimeMillis();
            return super.fill(buffer);
        }
        
        @Override
        public int flush(final Buffer buffer) throws IOException {
            this._idleTimestamp = System.currentTimeMillis();
            return super.flush(buffer);
        }
        
        @Override
        public int flush(final Buffer header, final Buffer buffer, final Buffer trailer) throws IOException {
            this._idleTimestamp = System.currentTimeMillis();
            return super.flush(header, buffer, trailer);
        }
        
        public void run() {
            try {
                this._timeout = this.getMaxIdleTime();
                BlockingChannelConnector.access$300(BlockingChannelConnector.this, this._connection);
                BlockingChannelConnector.this._endpoints.add(this);
                while (this.isOpen()) {
                    this._idleTimestamp = System.currentTimeMillis();
                    if (this._connection.isIdle()) {
                        if (BlockingChannelConnector.this.getServer().getThreadPool().isLowOnThreads()) {
                            final int lrmit = BlockingChannelConnector.this.getLowResourcesMaxIdleTime();
                            if (lrmit >= 0 && this._timeout != lrmit) {
                                this._timeout = lrmit;
                            }
                        }
                    }
                    else if (this._timeout != this.getMaxIdleTime()) {
                        this._timeout = this.getMaxIdleTime();
                    }
                    this._connection = this._connection.handle();
                }
            }
            catch (EofException e) {
                BlockingChannelConnector.LOG.debug("EOF", e);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    BlockingChannelConnector.LOG.ignore(e2);
                }
            }
            catch (HttpException e3) {
                BlockingChannelConnector.LOG.debug("BAD", e3);
                try {
                    super.close();
                }
                catch (IOException e2) {
                    BlockingChannelConnector.LOG.ignore(e2);
                }
            }
            catch (Throwable e4) {
                BlockingChannelConnector.LOG.warn("handle failed", e4);
                try {
                    super.close();
                }
                catch (IOException e2) {
                    BlockingChannelConnector.LOG.ignore(e2);
                }
            }
            finally {
                BlockingChannelConnector.access$400(BlockingChannelConnector.this, this._connection);
                BlockingChannelConnector.this._endpoints.remove(this);
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
                catch (IOException e5) {
                    BlockingChannelConnector.LOG.ignore(e5);
                }
            }
        }
        
        @Override
        public String toString() {
            return String.format("BCEP@%x{l(%s)<->r(%s),open=%b,ishut=%b,oshut=%b}-{%s}", this.hashCode(), this._socket.getRemoteSocketAddress(), this._socket.getLocalSocketAddress(), this.isOpen(), this.isInputShutdown(), this.isOutputShutdown(), this._connection);
        }
    }
}
