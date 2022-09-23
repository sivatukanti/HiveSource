// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.net.ProtocolException;
import java.util.concurrent.CountDownLatch;
import org.eclipse.jetty.util.log.Log;
import java.net.InetSocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;

public class WebSocketClient
{
    private static final Logger __log;
    private final WebSocketClientFactory _factory;
    private final Map<String, String> _cookies;
    private final List<String> _extensions;
    private String _origin;
    private String _protocol;
    private int _maxIdleTime;
    private int _maxTextMessageSize;
    private int _maxBinaryMessageSize;
    private MaskGen _maskGen;
    private SocketAddress _bindAddress;
    
    @Deprecated
    public WebSocketClient() throws Exception {
        this._cookies = new ConcurrentHashMap<String, String>();
        this._extensions = new CopyOnWriteArrayList<String>();
        this._maxIdleTime = -1;
        this._maxTextMessageSize = 16384;
        this._maxBinaryMessageSize = -1;
        (this._factory = new WebSocketClientFactory()).start();
        this._maskGen = this._factory.getMaskGen();
    }
    
    public WebSocketClient(final WebSocketClientFactory factory) {
        this._cookies = new ConcurrentHashMap<String, String>();
        this._extensions = new CopyOnWriteArrayList<String>();
        this._maxIdleTime = -1;
        this._maxTextMessageSize = 16384;
        this._maxBinaryMessageSize = -1;
        this._factory = factory;
        this._maskGen = this._factory.getMaskGen();
    }
    
    public WebSocketClientFactory getFactory() {
        return this._factory;
    }
    
    public SocketAddress getBindAddress() {
        return this._bindAddress;
    }
    
    public void setBindAddress(final SocketAddress bindAddress) {
        this._bindAddress = bindAddress;
    }
    
    public int getMaxIdleTime() {
        return this._maxIdleTime;
    }
    
    public void setMaxIdleTime(final int maxIdleTime) {
        this._maxIdleTime = maxIdleTime;
    }
    
    public String getProtocol() {
        return this._protocol;
    }
    
    public void setProtocol(final String protocol) {
        this._protocol = protocol;
    }
    
    public String getOrigin() {
        return this._origin;
    }
    
    public void setOrigin(final String origin) {
        this._origin = origin;
    }
    
    public Map<String, String> getCookies() {
        return this._cookies;
    }
    
    public List<String> getExtensions() {
        return this._extensions;
    }
    
    public MaskGen getMaskGen() {
        return this._maskGen;
    }
    
    public void setMaskGen(final MaskGen maskGen) {
        this._maskGen = maskGen;
    }
    
    public int getMaxTextMessageSize() {
        return this._maxTextMessageSize;
    }
    
    public void setMaxTextMessageSize(final int maxTextMessageSize) {
        this._maxTextMessageSize = maxTextMessageSize;
    }
    
    public int getMaxBinaryMessageSize() {
        return this._maxBinaryMessageSize;
    }
    
    public void setMaxBinaryMessageSize(final int maxBinaryMessageSize) {
        this._maxBinaryMessageSize = maxBinaryMessageSize;
    }
    
    public WebSocket.Connection open(final URI uri, final WebSocket websocket, final long maxConnectTime, final TimeUnit units) throws IOException, InterruptedException, TimeoutException {
        try {
            return this.open(uri, websocket).get(maxConnectTime, units);
        }
        catch (ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new RuntimeException(cause);
        }
    }
    
    public Future<WebSocket.Connection> open(final URI uri, final WebSocket websocket) throws IOException {
        if (!this._factory.isStarted()) {
            throw new IllegalStateException("Factory !started");
        }
        final InetSocketAddress address = toSocketAddress(uri);
        final SocketChannel channel = SocketChannel.open();
        if (this._bindAddress != null) {
            channel.socket().bind(this._bindAddress);
        }
        channel.socket().setTcpNoDelay(true);
        final WebSocketFuture holder = new WebSocketFuture(websocket, uri, this, (ByteChannel)channel);
        channel.configureBlocking(false);
        channel.connect(address);
        this._factory.getSelectorManager().register(channel, holder);
        return holder;
    }
    
    public static InetSocketAddress toSocketAddress(final URI uri) {
        final String scheme = uri.getScheme();
        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("Bad WebSocket scheme: " + scheme);
        }
        int port = uri.getPort();
        if (port == 0) {
            throw new IllegalArgumentException("Bad WebSocket port: " + port);
        }
        if (port < 0) {
            port = ("ws".equals(scheme) ? 80 : 443);
        }
        return new InetSocketAddress(uri.getHost(), port);
    }
    
    static {
        __log = Log.getLogger(WebSocketClient.class.getName());
    }
    
    static class WebSocketFuture implements Future<WebSocket.Connection>
    {
        final WebSocket _websocket;
        final URI _uri;
        final WebSocketClient _client;
        final CountDownLatch _done;
        ByteChannel _channel;
        WebSocketConnection _connection;
        Throwable _exception;
        
        private WebSocketFuture(final WebSocket websocket, final URI uri, final WebSocketClient client, final ByteChannel channel) {
            this._done = new CountDownLatch(1);
            this._websocket = websocket;
            this._uri = uri;
            this._client = client;
            this._channel = channel;
        }
        
        public void onConnection(final WebSocketConnection connection) {
            try {
                this._client.getFactory().addConnection(connection);
                connection.getConnection().setMaxTextMessageSize(this._client.getMaxTextMessageSize());
                connection.getConnection().setMaxBinaryMessageSize(this._client.getMaxBinaryMessageSize());
                final WebSocketConnection con;
                synchronized (this) {
                    if (this._channel != null) {
                        this._connection = connection;
                    }
                    con = this._connection;
                }
                if (con != null) {
                    if (this._websocket instanceof WebSocket.OnFrame) {
                        ((WebSocket.OnFrame)this._websocket).onHandshake((WebSocket.FrameConnection)con.getConnection());
                    }
                    this._websocket.onOpen(con.getConnection());
                }
            }
            finally {
                this._done.countDown();
            }
        }
        
        public void handshakeFailed(final Throwable ex) {
            try {
                ByteChannel channel = null;
                synchronized (this) {
                    if (this._channel != null) {
                        channel = this._channel;
                        this._channel = null;
                        this._exception = ex;
                    }
                }
                if (channel != null) {
                    if (ex instanceof ProtocolException) {
                        this.closeChannel(channel, 1002, ex.getMessage());
                    }
                    else {
                        this.closeChannel(channel, 1006, ex.getMessage());
                    }
                }
            }
            finally {
                this._done.countDown();
            }
        }
        
        public Map<String, String> getCookies() {
            return this._client.getCookies();
        }
        
        public String getProtocol() {
            return this._client.getProtocol();
        }
        
        public WebSocket getWebSocket() {
            return this._websocket;
        }
        
        public URI getURI() {
            return this._uri;
        }
        
        public int getMaxIdleTime() {
            return this._client.getMaxIdleTime();
        }
        
        public String getOrigin() {
            return this._client.getOrigin();
        }
        
        public MaskGen getMaskGen() {
            return this._client.getMaskGen();
        }
        
        @Override
        public String toString() {
            return "[" + this._uri + "," + this._websocket + "]@" + this.hashCode();
        }
        
        public boolean cancel(final boolean mayInterruptIfRunning) {
            try {
                ByteChannel channel = null;
                synchronized (this) {
                    if (this._connection == null && this._exception == null && this._channel != null) {
                        channel = this._channel;
                        this._channel = null;
                    }
                }
                if (channel != null) {
                    this.closeChannel(channel, 1006, "cancelled");
                    return true;
                }
                return false;
            }
            finally {
                this._done.countDown();
            }
        }
        
        public boolean isCancelled() {
            synchronized (this) {
                return this._channel == null && this._connection == null;
            }
        }
        
        public boolean isDone() {
            synchronized (this) {
                return this._connection != null && this._exception == null;
            }
        }
        
        public WebSocket.Connection get() throws InterruptedException, ExecutionException {
            try {
                return this.get(Long.MAX_VALUE, TimeUnit.SECONDS);
            }
            catch (TimeoutException e) {
                throw new IllegalStateException("The universe has ended", e);
            }
        }
        
        public WebSocket.Connection get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            this._done.await(timeout, unit);
            ByteChannel channel = null;
            WebSocket.Connection connection = null;
            Throwable exception;
            synchronized (this) {
                exception = this._exception;
                if (this._connection == null) {
                    exception = this._exception;
                    channel = this._channel;
                    this._channel = null;
                }
                else {
                    connection = this._connection.getConnection();
                }
            }
            if (channel != null) {
                this.closeChannel(channel, 1006, "timeout");
            }
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            if (connection != null) {
                return connection;
            }
            throw new TimeoutException();
        }
        
        private void closeChannel(final ByteChannel channel, final int code, final String message) {
            try {
                this._websocket.onClose(code, message);
            }
            catch (Exception e) {
                WebSocketClient.__log.warn(e);
            }
            try {
                channel.close();
            }
            catch (IOException e2) {
                WebSocketClient.__log.debug(e2);
            }
        }
    }
}
