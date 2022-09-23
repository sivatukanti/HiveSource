// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.nio.SslConnection;
import java.net.SocketTimeoutException;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import javax.net.ssl.SSLEngine;
import java.nio.channels.SelectionKey;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.nio.AsyncConnection;
import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.ConnectedEndPoint;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import org.eclipse.jetty.io.nio.SelectorManager;
import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import java.nio.channels.UnresolvedAddressException;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.util.thread.Timeout;
import java.nio.channels.SocketChannel;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.AggregateLifeCycle;

class SelectConnector extends AggregateLifeCycle implements HttpClient.Connector, Dumpable
{
    private static final Logger LOG;
    private final HttpClient _httpClient;
    private final Manager _selectorManager;
    private final Map<SocketChannel, Timeout.Task> _connectingChannels;
    
    SelectConnector(final HttpClient httpClient) {
        this._selectorManager = new Manager();
        this._connectingChannels = new ConcurrentHashMap<SocketChannel, Timeout.Task>();
        this.addBean(this._httpClient = httpClient, false);
        this.addBean(this._selectorManager, true);
    }
    
    public void startConnection(final HttpDestination destination) throws IOException {
        SocketChannel channel = null;
        try {
            channel = SocketChannel.open();
            final Address address = destination.isProxied() ? destination.getProxy() : destination.getAddress();
            channel.socket().setTcpNoDelay(true);
            if (this._httpClient.isConnectBlocking()) {
                channel.socket().connect(address.toSocketAddress(), this._httpClient.getConnectTimeout());
                channel.configureBlocking(false);
                this._selectorManager.register(channel, destination);
            }
            else {
                channel.configureBlocking(false);
                channel.connect(address.toSocketAddress());
                this._selectorManager.register(channel, destination);
                final ConnectTimeout connectTimeout = new ConnectTimeout(channel, destination);
                this._httpClient.schedule(connectTimeout, this._httpClient.getConnectTimeout());
                this._connectingChannels.put(channel, connectTimeout);
            }
        }
        catch (UnresolvedAddressException ex) {
            if (channel != null) {
                channel.close();
            }
            destination.onConnectionFailed(ex);
        }
        catch (IOException ex2) {
            if (channel != null) {
                channel.close();
            }
            destination.onConnectionFailed(ex2);
        }
    }
    
    static {
        LOG = Log.getLogger(SelectConnector.class);
    }
    
    class Manager extends SelectorManager
    {
        Logger LOG;
        
        Manager() {
            this.LOG = SelectConnector.LOG;
        }
        
        @Override
        public boolean dispatch(final Runnable task) {
            return SelectConnector.this._httpClient._threadPool.dispatch(task);
        }
        
        @Override
        protected void endPointOpened(final SelectChannelEndPoint endpoint) {
        }
        
        @Override
        protected void endPointClosed(final SelectChannelEndPoint endpoint) {
        }
        
        @Override
        protected void endPointUpgraded(final ConnectedEndPoint endpoint, final Connection oldConnection) {
        }
        
        @Override
        public AsyncConnection newConnection(final SocketChannel channel, final AsyncEndPoint endpoint, final Object attachment) {
            return new AsyncHttpConnection(SelectConnector.this._httpClient.getRequestBuffers(), SelectConnector.this._httpClient.getResponseBuffers(), endpoint);
        }
        
        @Override
        protected SelectChannelEndPoint newEndPoint(final SocketChannel channel, final SelectSet selectSet, final SelectionKey key) throws IOException {
            final Timeout.Task connectTimeout = SelectConnector.this._connectingChannels.remove(channel);
            if (connectTimeout != null) {
                connectTimeout.cancel();
            }
            if (this.LOG.isDebugEnabled()) {
                this.LOG.debug("Channels with connection pending: {}", new Object[] { SelectConnector.this._connectingChannels.size() });
            }
            final HttpDestination dest = (HttpDestination)key.attachment();
            AsyncEndPoint ep;
            final SelectChannelEndPoint scep = (SelectChannelEndPoint)(ep = new SelectChannelEndPoint(channel, selectSet, key, (int)SelectConnector.this._httpClient.getIdleTimeout()));
            if (dest.isSecure()) {
                this.LOG.debug("secure to {}, proxied={}", channel, dest.isProxied());
                ep = new UpgradableEndPoint(ep, this.newSslEngine(channel));
            }
            final AsyncConnection connection = selectSet.getManager().newConnection(channel, ep, key.attachment());
            ep.setConnection(connection);
            final AbstractHttpConnection httpConnection = (AbstractHttpConnection)connection;
            httpConnection.setDestination(dest);
            if (dest.isSecure() && !dest.isProxied()) {
                ((UpgradableEndPoint)ep).upgrade();
            }
            dest.onNewConnection(httpConnection);
            return scep;
        }
        
        private synchronized SSLEngine newSslEngine(final SocketChannel channel) throws IOException {
            final SslContextFactory sslContextFactory = SelectConnector.this._httpClient.getSslContextFactory();
            SSLEngine sslEngine;
            if (channel != null) {
                final String peerHost = channel.socket().getInetAddress().getHostAddress();
                final int peerPort = channel.socket().getPort();
                sslEngine = sslContextFactory.newSslEngine(peerHost, peerPort);
            }
            else {
                sslEngine = sslContextFactory.newSslEngine();
            }
            sslEngine.setUseClientMode(true);
            sslEngine.beginHandshake();
            return sslEngine;
        }
        
        @Override
        protected void connectionFailed(final SocketChannel channel, final Throwable ex, final Object attachment) {
            final Timeout.Task connectTimeout = SelectConnector.this._connectingChannels.remove(channel);
            if (connectTimeout != null) {
                connectTimeout.cancel();
            }
            if (attachment instanceof HttpDestination) {
                ((HttpDestination)attachment).onConnectionFailed(ex);
            }
            else {
                super.connectionFailed(channel, ex, attachment);
            }
        }
    }
    
    private class ConnectTimeout extends Timeout.Task
    {
        private final SocketChannel channel;
        private final HttpDestination destination;
        
        public ConnectTimeout(final SocketChannel channel, final HttpDestination destination) {
            this.channel = channel;
            this.destination = destination;
        }
        
        @Override
        public void expired() {
            if (this.channel.isConnectionPending()) {
                SelectConnector.LOG.debug("Channel {} timed out while connecting, closing it", this.channel);
                try {
                    this.channel.close();
                }
                catch (IOException x) {
                    SelectConnector.LOG.ignore(x);
                }
                this.destination.onConnectionFailed(new SocketTimeoutException());
            }
        }
    }
    
    public static class UpgradableEndPoint implements AsyncEndPoint
    {
        AsyncEndPoint _endp;
        SSLEngine _engine;
        
        public UpgradableEndPoint(final AsyncEndPoint endp, final SSLEngine engine) throws IOException {
            this._engine = engine;
            this._endp = endp;
        }
        
        public void upgrade() {
            final AsyncHttpConnection connection = (AsyncHttpConnection)this._endp.getConnection();
            final SslConnection sslConnection = new SslConnection(this._engine, this._endp);
            this._endp.setConnection(sslConnection);
            this._endp = sslConnection.getSslEndPoint();
            sslConnection.getSslEndPoint().setConnection(connection);
            SelectConnector.LOG.debug("upgrade {} to {} for {}", this, sslConnection, connection);
        }
        
        public Connection getConnection() {
            return this._endp.getConnection();
        }
        
        public void setConnection(final Connection connection) {
            this._endp.setConnection(connection);
        }
        
        public void shutdownOutput() throws IOException {
            this._endp.shutdownOutput();
        }
        
        public void asyncDispatch() {
            this._endp.asyncDispatch();
        }
        
        public boolean isOutputShutdown() {
            return this._endp.isOutputShutdown();
        }
        
        public void shutdownInput() throws IOException {
            this._endp.shutdownInput();
        }
        
        public void scheduleWrite() {
            this._endp.scheduleWrite();
        }
        
        public boolean isInputShutdown() {
            return this._endp.isInputShutdown();
        }
        
        public void close() throws IOException {
            this._endp.close();
        }
        
        public int fill(final Buffer buffer) throws IOException {
            return this._endp.fill(buffer);
        }
        
        public boolean isWritable() {
            return this._endp.isWritable();
        }
        
        public boolean hasProgressed() {
            return this._endp.hasProgressed();
        }
        
        public int flush(final Buffer buffer) throws IOException {
            return this._endp.flush(buffer);
        }
        
        public void scheduleTimeout(final Timeout.Task task, final long timeoutMs) {
            this._endp.scheduleTimeout(task, timeoutMs);
        }
        
        public void cancelTimeout(final Timeout.Task task) {
            this._endp.cancelTimeout(task);
        }
        
        public int flush(final Buffer header, final Buffer buffer, final Buffer trailer) throws IOException {
            return this._endp.flush(header, buffer, trailer);
        }
        
        public String getLocalAddr() {
            return this._endp.getLocalAddr();
        }
        
        public String getLocalHost() {
            return this._endp.getLocalHost();
        }
        
        public int getLocalPort() {
            return this._endp.getLocalPort();
        }
        
        public String getRemoteAddr() {
            return this._endp.getRemoteAddr();
        }
        
        public String getRemoteHost() {
            return this._endp.getRemoteHost();
        }
        
        public int getRemotePort() {
            return this._endp.getRemotePort();
        }
        
        public boolean isBlocking() {
            return this._endp.isBlocking();
        }
        
        public boolean blockReadable(final long millisecs) throws IOException {
            return this._endp.blockReadable(millisecs);
        }
        
        public boolean blockWritable(final long millisecs) throws IOException {
            return this._endp.blockWritable(millisecs);
        }
        
        public boolean isOpen() {
            return this._endp.isOpen();
        }
        
        public Object getTransport() {
            return this._endp.getTransport();
        }
        
        public void flush() throws IOException {
            this._endp.flush();
        }
        
        public int getMaxIdleTime() {
            return this._endp.getMaxIdleTime();
        }
        
        public void setMaxIdleTime(final int timeMs) throws IOException {
            this._endp.setMaxIdleTime(timeMs);
        }
        
        public void onIdleExpired(final long idleForMs) {
            this._endp.onIdleExpired(idleForMs);
        }
        
        public void setCheckForIdle(final boolean check) {
            this._endp.setCheckForIdle(check);
        }
        
        public boolean isCheckForIdle() {
            return this._endp.isCheckForIdle();
        }
        
        @Override
        public String toString() {
            return "Upgradable:" + this._endp.toString();
        }
    }
}
