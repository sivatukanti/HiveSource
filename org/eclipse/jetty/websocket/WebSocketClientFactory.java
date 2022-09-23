// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.io.EOFException;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.SimpleBuffers;
import org.eclipse.jetty.util.B64Code;
import java.util.Random;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.ConnectedEndPoint;
import org.eclipse.jetty.io.nio.AsyncConnection;
import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.nio.SslConnection;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import java.nio.channels.SelectionKey;
import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.io.IOException;
import javax.net.ssl.SSLEngine;
import java.nio.channels.SocketChannel;
import org.eclipse.jetty.io.nio.SelectorManager;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import java.util.Queue;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AggregateLifeCycle;

public class WebSocketClientFactory extends AggregateLifeCycle
{
    private static final Logger __log;
    private static final ByteArrayBuffer __ACCEPT;
    private final Queue<WebSocketConnection> connections;
    private final SslContextFactory _sslContextFactory;
    private final ThreadPool _threadPool;
    private final WebSocketClientSelector _selector;
    private MaskGen _maskGen;
    private WebSocketBuffers _buffers;
    
    public WebSocketClientFactory() {
        this(null);
    }
    
    public WebSocketClientFactory(final ThreadPool threadPool) {
        this(threadPool, new RandomMaskGen());
    }
    
    public WebSocketClientFactory(final ThreadPool threadPool, final MaskGen maskGen) {
        this(threadPool, maskGen, 8192);
    }
    
    public WebSocketClientFactory(ThreadPool threadPool, final MaskGen maskGen, final int bufferSize) {
        this.connections = new ConcurrentLinkedQueue<WebSocketConnection>();
        this._sslContextFactory = new SslContextFactory();
        if (threadPool == null) {
            threadPool = new QueuedThreadPool();
        }
        this.addBean(this._threadPool = threadPool);
        this.addBean(this._buffers = new WebSocketBuffers(bufferSize));
        this.addBean(this._maskGen = maskGen);
        this.addBean(this._selector = new WebSocketClientSelector());
        this.addBean(this._sslContextFactory);
    }
    
    public SslContextFactory getSslContextFactory() {
        return this._sslContextFactory;
    }
    
    public SelectorManager getSelectorManager() {
        return this._selector;
    }
    
    public ThreadPool getThreadPool() {
        return this._threadPool;
    }
    
    public MaskGen getMaskGen() {
        return this._maskGen;
    }
    
    public void setMaskGen(final MaskGen maskGen) {
        if (this.isRunning()) {
            throw new IllegalStateException(this.getState());
        }
        this.removeBean(this._maskGen);
        this.addBean(this._maskGen = maskGen);
    }
    
    public void setBufferSize(final int bufferSize) {
        if (this.isRunning()) {
            throw new IllegalStateException(this.getState());
        }
        this.removeBean(this._buffers);
        this.addBean(this._buffers = new WebSocketBuffers(bufferSize));
    }
    
    public int getBufferSize() {
        return this._buffers.getBufferSize();
    }
    
    @Override
    protected void doStop() throws Exception {
        this.closeConnections();
        super.doStop();
    }
    
    public WebSocketClient newWebSocketClient() {
        return new WebSocketClient(this);
    }
    
    protected SSLEngine newSslEngine(final SocketChannel channel) throws IOException {
        SSLEngine sslEngine;
        if (channel != null) {
            final String peerHost = channel.socket().getInetAddress().getHostAddress();
            final int peerPort = channel.socket().getPort();
            sslEngine = this._sslContextFactory.newSslEngine(peerHost, peerPort);
        }
        else {
            sslEngine = this._sslContextFactory.newSslEngine();
        }
        sslEngine.setUseClientMode(true);
        sslEngine.beginHandshake();
        return sslEngine;
    }
    
    protected boolean addConnection(final WebSocketConnection connection) {
        return this.isRunning() && this.connections.add(connection);
    }
    
    protected boolean removeConnection(final WebSocketConnection connection) {
        return this.connections.remove(connection);
    }
    
    protected void closeConnections() {
        for (final WebSocketConnection connection : this.connections) {
            connection.shutdown();
        }
    }
    
    static {
        __log = Log.getLogger(WebSocketClientFactory.class.getName());
        __ACCEPT = new ByteArrayBuffer.CaseInsensitive("Sec-WebSocket-Accept");
    }
    
    class WebSocketClientSelector extends SelectorManager
    {
        @Override
        public boolean dispatch(final Runnable task) {
            return WebSocketClientFactory.this._threadPool.dispatch(task);
        }
        
        @Override
        protected SelectChannelEndPoint newEndPoint(final SocketChannel channel, final SelectSet selectSet, final SelectionKey key) throws IOException {
            final WebSocketClient.WebSocketFuture holder = (WebSocketClient.WebSocketFuture)key.attachment();
            int maxIdleTime = holder.getMaxIdleTime();
            if (maxIdleTime < 0) {
                maxIdleTime = (int)this.getMaxIdleTime();
            }
            AsyncEndPoint endPoint;
            final SelectChannelEndPoint result = (SelectChannelEndPoint)(endPoint = new SelectChannelEndPoint(channel, selectSet, key, maxIdleTime));
            if ("wss".equals(holder.getURI().getScheme())) {
                final SSLEngine sslEngine = WebSocketClientFactory.this.newSslEngine(channel);
                final SslConnection sslConnection = new SslConnection(sslEngine, endPoint);
                endPoint.setConnection(sslConnection);
                endPoint = sslConnection.getSslEndPoint();
            }
            final AsyncConnection connection = selectSet.getManager().newConnection(channel, endPoint, holder);
            endPoint.setConnection(connection);
            return result;
        }
        
        @Override
        public AsyncConnection newConnection(final SocketChannel channel, final AsyncEndPoint endpoint, final Object attachment) {
            final WebSocketClient.WebSocketFuture holder = (WebSocketClient.WebSocketFuture)attachment;
            return new HandshakeConnection(endpoint, holder);
        }
        
        @Override
        protected void endPointOpened(final SelectChannelEndPoint endpoint) {
        }
        
        @Override
        protected void endPointUpgraded(final ConnectedEndPoint endpoint, final Connection oldConnection) {
            WebSocketClientSelector.LOG.debug("upgrade {} -> {}", oldConnection, endpoint.getConnection());
        }
        
        @Override
        protected void endPointClosed(final SelectChannelEndPoint endpoint) {
            endpoint.getConnection().onClose();
        }
        
        @Override
        protected void connectionFailed(final SocketChannel channel, final Throwable ex, final Object attachment) {
            if (!(attachment instanceof WebSocketClient.WebSocketFuture)) {
                super.connectionFailed(channel, ex, attachment);
            }
            else {
                WebSocketClientFactory.__log.debug(ex);
                final WebSocketClient.WebSocketFuture future = (WebSocketClient.WebSocketFuture)attachment;
                future.handshakeFailed(ex);
            }
        }
    }
    
    class HandshakeConnection extends AbstractConnection implements AsyncConnection
    {
        private final AsyncEndPoint _endp;
        private final WebSocketClient.WebSocketFuture _future;
        private final String _key;
        private final HttpParser _parser;
        private String _accept;
        private String _error;
        private ByteArrayBuffer _handshake;
        
        public HandshakeConnection(final AsyncEndPoint endpoint, final WebSocketClient.WebSocketFuture future) {
            super((EndPoint)endpoint, System.currentTimeMillis());
            this._endp = endpoint;
            this._future = future;
            final byte[] bytes = new byte[16];
            new Random().nextBytes(bytes);
            this._key = new String(B64Code.encode(bytes));
            final Buffers buffers = new SimpleBuffers(WebSocketClientFactory.this._buffers.getBuffer(), null);
            this._parser = new HttpParser(buffers, (EndPoint)this._endp, (HttpParser.EventHandler)new HttpParser.EventHandler() {
                @Override
                public void startResponse(final Buffer version, final int status, final Buffer reason) throws IOException {
                    if (status != 101) {
                        HandshakeConnection.this._error = "Bad response status " + status + " " + reason;
                        HandshakeConnection.this._endp.close();
                    }
                }
                
                @Override
                public void parsedHeader(final Buffer name, final Buffer value) throws IOException {
                    if (WebSocketClientFactory.__ACCEPT.equals(name)) {
                        HandshakeConnection.this._accept = value.toString();
                    }
                }
                
                @Override
                public void startRequest(final Buffer method, final Buffer url, final Buffer version) throws IOException {
                    if (HandshakeConnection.this._error == null) {
                        HandshakeConnection.this._error = "Bad response: " + method + " " + url + " " + version;
                    }
                    HandshakeConnection.this._endp.close();
                }
                
                @Override
                public void content(final Buffer ref) throws IOException {
                    if (HandshakeConnection.this._error == null) {
                        HandshakeConnection.this._error = "Bad response. " + ref.length() + "B of content?";
                    }
                    HandshakeConnection.this._endp.close();
                }
            });
        }
        
        private boolean handshake() {
            if (this._handshake == null) {
                String path = this._future.getURI().getPath();
                if (path == null || path.length() == 0) {
                    path = "/";
                }
                if (this._future.getURI().getRawQuery() != null) {
                    path = path + "?" + this._future.getURI().getRawQuery();
                }
                final String origin = this._future.getOrigin();
                final StringBuilder request = new StringBuilder(512);
                request.append("GET ").append(path).append(" HTTP/1.1\r\n").append("Host: ").append(this._future.getURI().getHost()).append(":").append(this._future.getURI().getPort()).append("\r\n").append("Upgrade: websocket\r\n").append("Connection: Upgrade\r\n").append("Sec-WebSocket-Key: ").append(this._key).append("\r\n");
                if (origin != null) {
                    request.append("Origin: ").append(origin).append("\r\n");
                }
                request.append("Sec-WebSocket-Version: ").append(13).append("\r\n");
                if (this._future.getProtocol() != null) {
                    request.append("Sec-WebSocket-Protocol: ").append(this._future.getProtocol()).append("\r\n");
                }
                final Map<String, String> cookies = this._future.getCookies();
                if (cookies != null && cookies.size() > 0) {
                    for (final String cookie : cookies.keySet()) {
                        request.append("Cookie: ").append(QuotedStringTokenizer.quoteIfNeeded(cookie, "\"\\\n\r\t\f\b%+ ;=")).append("=").append(QuotedStringTokenizer.quoteIfNeeded(cookies.get(cookie), "\"\\\n\r\t\f\b%+ ;=")).append("\r\n");
                    }
                }
                request.append("\r\n");
                this._handshake = new ByteArrayBuffer(request.toString(), false);
            }
            try {
                final int len = this._handshake.length();
                final int flushed = this._endp.flush((Buffer)this._handshake);
                if (flushed < 0) {
                    throw new IOException("incomplete handshake");
                }
            }
            catch (IOException e) {
                this._future.handshakeFailed(e);
            }
            return this._handshake.length() == 0;
        }
        
        public Connection handle() throws IOException {
            while (this._endp.isOpen() && !this._parser.isComplete()) {
                if ((this._handshake == null || this._handshake.length() > 0) && !this.handshake()) {
                    return this;
                }
                if (!this._parser.parseAvailable()) {
                    if (this._endp.isInputShutdown()) {
                        this._future.handshakeFailed(new IOException("Incomplete handshake response"));
                    }
                    return this;
                }
            }
            if (this._error == null) {
                if (this._accept == null) {
                    this._error = "No Sec-WebSocket-Accept";
                }
                else {
                    if (WebSocketConnectionRFC6455.hashKey(this._key).equals(this._accept)) {
                        final WebSocketConnection connection = this.newWebSocketConnection();
                        final Buffer header = this._parser.getHeaderBuffer();
                        if (header.hasContent()) {
                            connection.fillBuffersFrom(header);
                        }
                        WebSocketClientFactory.this._buffers.returnBuffer(header);
                        this._future.onConnection(connection);
                        return connection;
                    }
                    this._error = "Bad Sec-WebSocket-Accept";
                }
            }
            this._endp.close();
            return this;
        }
        
        private WebSocketConnection newWebSocketConnection() throws IOException {
            return new WebSocketClientConnection(this._future._client.getFactory(), this._future.getWebSocket(), this._endp, WebSocketClientFactory.this._buffers, System.currentTimeMillis(), this._future.getMaxIdleTime(), this._future.getProtocol(), null, 13, this._future.getMaskGen());
        }
        
        public void onInputShutdown() throws IOException {
            this._endp.close();
        }
        
        public boolean isIdle() {
            return false;
        }
        
        public boolean isSuspended() {
            return false;
        }
        
        @Override
        public void onClose() {
            if (this._error != null) {
                this._future.handshakeFailed(new ProtocolException(this._error));
            }
            else {
                this._future.handshakeFailed(new EOFException());
            }
        }
    }
    
    private static class WebSocketClientConnection extends WebSocketConnectionRFC6455
    {
        private final WebSocketClientFactory factory;
        
        public WebSocketClientConnection(final WebSocketClientFactory factory, final WebSocket webSocket, final EndPoint endPoint, final WebSocketBuffers buffers, final long timeStamp, final int maxIdleTime, final String protocol, final List<Extension> extensions, final int draftVersion, final MaskGen maskGen) throws IOException {
            super(webSocket, endPoint, buffers, timeStamp, maxIdleTime, protocol, extensions, draftVersion, maskGen);
            this.factory = factory;
        }
        
        @Override
        public void onClose() {
            super.onClose();
            this.factory.removeConnection(this);
        }
    }
}
