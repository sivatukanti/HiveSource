// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.nio;

import org.mortbay.jetty.HttpException;
import org.mortbay.jetty.EofException;
import org.mortbay.log.Log;
import org.mortbay.jetty.Connector;
import org.mortbay.io.nio.ChannelEndPoint;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.io.EndPoint;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.channels.ByteChannel;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class BlockingChannelConnector extends AbstractNIOConnector
{
    private transient ServerSocketChannel _acceptChannel;
    
    public Object getConnection() {
        return this._acceptChannel;
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
        final Connection connection = new Connection(channel);
        connection.dispatch();
    }
    
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        final Connection connection = (Connection)endpoint;
        if (connection._sotimeout != this._maxIdleTime) {
            connection._sotimeout = this._maxIdleTime;
            ((SocketChannel)endpoint.getTransport()).socket().setSoTimeout(this._maxIdleTime);
        }
        super.customize(endpoint, request);
        this.configure(((SocketChannel)endpoint.getTransport()).socket());
    }
    
    public int getLocalPort() {
        if (this._acceptChannel == null || !this._acceptChannel.isOpen()) {
            return -1;
        }
        return this._acceptChannel.socket().getLocalPort();
    }
    
    private class Connection extends ChannelEndPoint implements Runnable
    {
        boolean _dispatched;
        HttpConnection _connection;
        int _sotimeout;
        
        Connection(final ByteChannel channel) {
            super(channel);
            this._dispatched = false;
            this._connection = new HttpConnection(BlockingChannelConnector.this, this, BlockingChannelConnector.this.getServer());
        }
        
        void dispatch() throws IOException {
            if (!BlockingChannelConnector.this.getThreadPool().dispatch(this)) {
                Log.warn("dispatch failed for  {}", this._connection);
                this.close();
            }
        }
        
        public void run() {
            try {
                AbstractConnector.this.connectionOpened(this._connection);
                while (this.isOpen()) {
                    if (this._connection.isIdle() && BlockingChannelConnector.this.getServer().getThreadPool().isLowOnThreads()) {
                        final int lrmit = BlockingChannelConnector.this.getLowResourceMaxIdleTime();
                        if (lrmit >= 0 && this._sotimeout != lrmit) {
                            this._sotimeout = lrmit;
                            ((SocketChannel)this.getTransport()).socket().setSoTimeout(this._sotimeout);
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
            }
        }
    }
}
