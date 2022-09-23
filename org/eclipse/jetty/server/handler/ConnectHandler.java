// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import java.util.concurrent.TimeUnit;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.CountDownLatch;
import org.eclipse.jetty.io.ConnectedEndPoint;
import org.eclipse.jetty.io.nio.AsyncConnection;
import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import java.nio.channels.SelectionKey;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.TypeUtil;
import java.util.Arrays;
import java.util.Collection;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.eclipse.jetty.io.EndPoint;
import java.util.concurrent.ConcurrentMap;
import java.nio.channels.SocketChannel;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.Buffer;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.io.nio.IndirectNIOBuffer;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.server.AbstractHttpConnection;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.HostMap;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.io.nio.SelectorManager;
import org.eclipse.jetty.util.log.Logger;

public class ConnectHandler extends HandlerWrapper
{
    private static final Logger LOG;
    private final Logger _logger;
    private final SelectorManager _selectorManager;
    private volatile int _connectTimeout;
    private volatile int _writeTimeout;
    private volatile ThreadPool _threadPool;
    private volatile boolean _privateThreadPool;
    private HostMap<String> _white;
    private HostMap<String> _black;
    
    public ConnectHandler() {
        this(null);
    }
    
    public ConnectHandler(final String[] white, final String[] black) {
        this(null, white, black);
    }
    
    public ConnectHandler(final Handler handler) {
        this._logger = Log.getLogger(this.getClass().getName());
        this._selectorManager = new Manager();
        this._connectTimeout = 5000;
        this._writeTimeout = 30000;
        this._white = new HostMap<String>();
        this._black = new HostMap<String>();
        this.setHandler(handler);
    }
    
    public ConnectHandler(final Handler handler, final String[] white, final String[] black) {
        this._logger = Log.getLogger(this.getClass().getName());
        this._selectorManager = new Manager();
        this._connectTimeout = 5000;
        this._writeTimeout = 30000;
        this._white = new HostMap<String>();
        this._black = new HostMap<String>();
        this.setHandler(handler);
        this.set(white, this._white);
        this.set(black, this._black);
    }
    
    public int getConnectTimeout() {
        return this._connectTimeout;
    }
    
    public void setConnectTimeout(final int connectTimeout) {
        this._connectTimeout = connectTimeout;
    }
    
    public int getWriteTimeout() {
        return this._writeTimeout;
    }
    
    public void setWriteTimeout(final int writeTimeout) {
        this._writeTimeout = writeTimeout;
    }
    
    @Override
    public void setServer(final Server server) {
        super.setServer(server);
        server.getContainer().update((Object)this, (Object)null, (Object)this._selectorManager, "selectManager");
        if (this._privateThreadPool) {
            server.getContainer().update((Object)this, (Object)null, (Object)this._privateThreadPool, "threadpool", true);
        }
        else {
            this._threadPool = server.getThreadPool();
        }
    }
    
    public ThreadPool getThreadPool() {
        return this._threadPool;
    }
    
    public void setThreadPool(final ThreadPool threadPool) {
        if (this.getServer() != null) {
            this.getServer().getContainer().update((Object)this, (Object)(this._privateThreadPool ? this._threadPool : null), (Object)threadPool, "threadpool", true);
        }
        this._privateThreadPool = (threadPool != null);
        this._threadPool = threadPool;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if (this._threadPool == null) {
            this._threadPool = this.getServer().getThreadPool();
            this._privateThreadPool = false;
        }
        if (this._threadPool instanceof LifeCycle && !((LifeCycle)this._threadPool).isRunning()) {
            ((LifeCycle)this._threadPool).start();
        }
        this._selectorManager.start();
    }
    
    @Override
    protected void doStop() throws Exception {
        this._selectorManager.stop();
        final ThreadPool threadPool = this._threadPool;
        if (this._privateThreadPool && this._threadPool != null && threadPool instanceof LifeCycle) {
            ((LifeCycle)threadPool).stop();
        }
        super.doStop();
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if ("CONNECT".equalsIgnoreCase(request.getMethod())) {
            this._logger.debug("CONNECT request for {}", request.getRequestURI());
            try {
                this.handleConnect(baseRequest, request, response, request.getRequestURI());
            }
            catch (Exception e) {
                this._logger.warn("ConnectHandler " + baseRequest.getUri() + " " + e, new Object[0]);
                this._logger.debug(e);
            }
        }
        else {
            super.handle(target, baseRequest, request, response);
        }
    }
    
    protected void handleConnect(final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response, final String serverAddress) throws ServletException, IOException {
        final boolean proceed = this.handleAuthentication(request, response, serverAddress);
        if (!proceed) {
            return;
        }
        String host = serverAddress;
        int port = 80;
        final int colon = serverAddress.indexOf(58);
        if (colon > 0) {
            host = serverAddress.substring(0, colon);
            port = Integer.parseInt(serverAddress.substring(colon + 1));
        }
        if (!this.validateDestination(host)) {
            ConnectHandler.LOG.info("ProxyHandler: Forbidden destination " + host, new Object[0]);
            response.setStatus(403);
            baseRequest.setHandled(true);
            return;
        }
        final SocketChannel channel = this.connectToServer(request, host, port);
        final AbstractHttpConnection httpConnection = AbstractHttpConnection.getCurrentConnection();
        final Buffer headerBuffer = ((HttpParser)httpConnection.getParser()).getHeaderBuffer();
        final Buffer bodyBuffer = ((HttpParser)httpConnection.getParser()).getBodyBuffer();
        int length = (headerBuffer == null) ? 0 : headerBuffer.length();
        length += ((bodyBuffer == null) ? 0 : bodyBuffer.length());
        IndirectNIOBuffer buffer = null;
        if (length > 0) {
            buffer = new IndirectNIOBuffer(length);
            if (headerBuffer != null) {
                buffer.put(headerBuffer);
                headerBuffer.clear();
            }
            if (bodyBuffer != null) {
                buffer.put(bodyBuffer);
                bodyBuffer.clear();
            }
        }
        final ConcurrentMap<String, Object> context = new ConcurrentHashMap<String, Object>();
        this.prepareContext(request, context);
        final ClientToProxyConnection clientToProxy = this.prepareConnections(context, channel, buffer);
        response.setStatus(200);
        baseRequest.getConnection().getGenerator().setPersistent(true);
        response.getOutputStream().close();
        this.upgradeConnection(request, response, clientToProxy);
    }
    
    private ClientToProxyConnection prepareConnections(final ConcurrentMap<String, Object> context, final SocketChannel channel, final Buffer buffer) {
        final AbstractHttpConnection httpConnection = AbstractHttpConnection.getCurrentConnection();
        final ProxyToServerConnection proxyToServer = this.newProxyToServerConnection(context, buffer);
        final ClientToProxyConnection clientToProxy = this.newClientToProxyConnection(context, channel, httpConnection.getEndPoint(), httpConnection.getTimeStamp());
        clientToProxy.setConnection(proxyToServer);
        proxyToServer.setConnection(clientToProxy);
        return clientToProxy;
    }
    
    protected boolean handleAuthentication(final HttpServletRequest request, final HttpServletResponse response, final String address) throws ServletException, IOException {
        return true;
    }
    
    protected ClientToProxyConnection newClientToProxyConnection(final ConcurrentMap<String, Object> context, final SocketChannel channel, final EndPoint endPoint, final long timeStamp) {
        return new ClientToProxyConnection(context, channel, endPoint, timeStamp);
    }
    
    protected ProxyToServerConnection newProxyToServerConnection(final ConcurrentMap<String, Object> context, final Buffer buffer) {
        return new ProxyToServerConnection(context, buffer);
    }
    
    private SocketChannel connectToServer(final HttpServletRequest request, final String host, final int port) throws IOException {
        final SocketChannel channel = this.connect(request, host, port);
        channel.configureBlocking(false);
        return channel;
    }
    
    protected SocketChannel connect(final HttpServletRequest request, final String host, final int port) throws IOException {
        final SocketChannel channel = SocketChannel.open();
        try {
            this._logger.debug("Establishing connection to {}:{}", host, port);
            channel.socket().setTcpNoDelay(true);
            channel.socket().connect(new InetSocketAddress(host, port), this.getConnectTimeout());
            this._logger.debug("Established connection to {}:{}", host, port);
            return channel;
        }
        catch (IOException x) {
            this._logger.debug("Failed to establish connection to " + host + ":" + port, x);
            try {
                channel.close();
            }
            catch (IOException xx) {
                ConnectHandler.LOG.ignore(xx);
            }
            throw x;
        }
    }
    
    protected void prepareContext(final HttpServletRequest request, final ConcurrentMap<String, Object> context) {
    }
    
    private void upgradeConnection(final HttpServletRequest request, final HttpServletResponse response, final Connection connection) throws IOException {
        request.setAttribute("org.eclipse.jetty.io.Connection", connection);
        response.setStatus(101);
        this._logger.debug("Upgraded connection to {}", connection);
    }
    
    private void register(final SocketChannel channel, final ProxyToServerConnection proxyToServer) throws IOException {
        this._selectorManager.register(channel, proxyToServer);
        proxyToServer.waitReady(this._connectTimeout);
    }
    
    protected int read(final EndPoint endPoint, final Buffer buffer, final ConcurrentMap<String, Object> context) throws IOException {
        return endPoint.fill(buffer);
    }
    
    protected int write(final EndPoint endPoint, final Buffer buffer, final ConcurrentMap<String, Object> context) throws IOException {
        if (buffer == null) {
            return 0;
        }
        final int length = buffer.length();
        final StringBuilder builder = new StringBuilder();
        int written = endPoint.flush(buffer);
        builder.append(written);
        buffer.compact();
        if (!endPoint.isBlocking()) {
            while (buffer.space() == 0) {
                final boolean ready = endPoint.blockWritable((long)this.getWriteTimeout());
                if (!ready) {
                    throw new IOException("Write timeout");
                }
                written = endPoint.flush(buffer);
                builder.append("+").append(written);
                buffer.compact();
            }
        }
        this._logger.debug("Written {}/{} bytes {}", builder, length, endPoint);
        return length;
    }
    
    public void addWhite(final String entry) {
        this.add(entry, this._white);
    }
    
    public void addBlack(final String entry) {
        this.add(entry, this._black);
    }
    
    public void setWhite(final String[] entries) {
        this.set(entries, this._white);
    }
    
    public void setBlack(final String[] entries) {
        this.set(entries, this._black);
    }
    
    protected void set(final String[] entries, final HostMap<String> hostMap) {
        hostMap.clear();
        if (entries != null && entries.length > 0) {
            for (final String addrPath : entries) {
                this.add(addrPath, hostMap);
            }
        }
    }
    
    private void add(String entry, final HostMap<String> hostMap) {
        if (entry != null && entry.length() > 0) {
            entry = entry.trim();
            if (hostMap.get(entry) == null) {
                hostMap.put(entry, entry);
            }
        }
    }
    
    public boolean validateDestination(final String host) {
        if (this._white.size() > 0) {
            final Object whiteObj = this._white.getLazyMatches(host);
            if (whiteObj == null) {
                return false;
            }
        }
        if (this._black.size() > 0) {
            final Object blackObj = this._black.getLazyMatches(host);
            if (blackObj != null) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        this.dumpThis(out);
        if (this._privateThreadPool) {
            ContainerLifeCycle.dump(out, indent, Arrays.asList(this._threadPool, this._selectorManager), TypeUtil.asList(this.getHandlers()), this.getBeans());
        }
        else {
            ContainerLifeCycle.dump(out, indent, Arrays.asList(this._selectorManager), TypeUtil.asList(this.getHandlers()), this.getBeans());
        }
    }
    
    static {
        LOG = Log.getLogger(ConnectHandler.class);
    }
    
    private class Manager extends SelectorManager
    {
        @Override
        protected SelectChannelEndPoint newEndPoint(final SocketChannel channel, final SelectSet selectSet, final SelectionKey key) throws IOException {
            final SelectChannelEndPoint endp = new SelectChannelEndPoint(channel, selectSet, key, channel.socket().getSoTimeout());
            endp.setConnection(selectSet.getManager().newConnection(channel, endp, key.attachment()));
            endp.setMaxIdleTime(ConnectHandler.this._writeTimeout);
            return endp;
        }
        
        @Override
        public AsyncConnection newConnection(final SocketChannel channel, final AsyncEndPoint endpoint, final Object attachment) {
            final ProxyToServerConnection proxyToServer = (ProxyToServerConnection)attachment;
            proxyToServer.setTimeStamp(System.currentTimeMillis());
            proxyToServer.setEndPoint(endpoint);
            return proxyToServer;
        }
        
        @Override
        protected void endPointOpened(final SelectChannelEndPoint endpoint) {
            final ProxyToServerConnection proxyToServer = (ProxyToServerConnection)endpoint.getSelectionKey().attachment();
            proxyToServer.ready();
        }
        
        @Override
        public boolean dispatch(final Runnable task) {
            return ConnectHandler.this._threadPool.dispatch(task);
        }
        
        @Override
        protected void endPointClosed(final SelectChannelEndPoint endpoint) {
        }
        
        @Override
        protected void endPointUpgraded(final ConnectedEndPoint endpoint, final Connection oldConnection) {
        }
    }
    
    public class ProxyToServerConnection implements AsyncConnection
    {
        private final CountDownLatch _ready;
        private final Buffer _buffer;
        private final ConcurrentMap<String, Object> _context;
        private volatile Buffer _data;
        private volatile ClientToProxyConnection _toClient;
        private volatile long _timestamp;
        private volatile AsyncEndPoint _endPoint;
        
        public ProxyToServerConnection(final ConcurrentMap<String, Object> context, final Buffer data) {
            this._ready = new CountDownLatch(1);
            this._buffer = new IndirectNIOBuffer(1024);
            this._context = context;
            this._data = data;
        }
        
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("ProxyToServer");
            builder.append("(:").append(this._endPoint.getLocalPort());
            builder.append("<=>:").append(this._endPoint.getRemotePort());
            return builder.append(")").toString();
        }
        
        public Connection handle() throws IOException {
            ConnectHandler.this._logger.debug("{}: begin reading from server", this);
            try {
                this.writeData();
                while (true) {
                    final int read = ConnectHandler.this.read(this._endPoint, this._buffer, this._context);
                    if (read == -1) {
                        ConnectHandler.this._logger.debug("{}: server closed connection {}", this, this._endPoint);
                        if (this._endPoint.isOutputShutdown() || !this._endPoint.isOpen()) {
                            this.closeClient();
                            break;
                        }
                        this._toClient.shutdownOutput();
                        break;
                    }
                    else {
                        if (read == 0) {
                            break;
                        }
                        ConnectHandler.this._logger.debug("{}: read from server {} bytes {}", this, read, this._endPoint);
                        final int written = ConnectHandler.this.write(this._toClient._endPoint, this._buffer, this._context);
                        ConnectHandler.this._logger.debug("{}: written to {} {} bytes", this, this._toClient, written);
                    }
                }
                return this;
            }
            catch (ClosedChannelException x) {
                ConnectHandler.this._logger.debug(x);
                throw x;
            }
            catch (IOException x2) {
                ConnectHandler.this._logger.warn(this + ": unexpected exception", x2);
                this.close();
                throw x2;
            }
            catch (RuntimeException x3) {
                ConnectHandler.this._logger.warn(this + ": unexpected exception", x3);
                this.close();
                throw x3;
            }
            finally {
                ConnectHandler.this._logger.debug("{}: end reading from server", this);
            }
        }
        
        public void onInputShutdown() throws IOException {
        }
        
        private void writeData() throws IOException {
            synchronized (this) {
                if (this._data != null) {
                    try {
                        final int written = ConnectHandler.this.write(this._endPoint, this._data, this._context);
                        ConnectHandler.this._logger.debug("{}: written to server {} bytes", this, written);
                    }
                    finally {
                        this._data = null;
                    }
                }
            }
        }
        
        public void setConnection(final ClientToProxyConnection connection) {
            this._toClient = connection;
        }
        
        public long getTimeStamp() {
            return this._timestamp;
        }
        
        public void setTimeStamp(final long timestamp) {
            this._timestamp = timestamp;
        }
        
        public void setEndPoint(final AsyncEndPoint endpoint) {
            this._endPoint = endpoint;
        }
        
        public boolean isIdle() {
            return false;
        }
        
        public boolean isSuspended() {
            return false;
        }
        
        public void onClose() {
        }
        
        public void ready() {
            this._ready.countDown();
        }
        
        public void waitReady(final long timeout) throws IOException {
            try {
                this._ready.await(timeout, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException x) {
                throw new IOException() {
                    {
                        this.initCause(x);
                    }
                };
            }
        }
        
        public void closeClient() throws IOException {
            this._toClient.closeClient();
        }
        
        public void closeServer() throws IOException {
            this._endPoint.close();
        }
        
        public void close() {
            try {
                this.closeClient();
            }
            catch (IOException x) {
                ConnectHandler.this._logger.debug(this + ": unexpected exception closing the client", x);
            }
            try {
                this.closeServer();
            }
            catch (IOException x) {
                ConnectHandler.this._logger.debug(this + ": unexpected exception closing the server", x);
            }
        }
        
        public void shutdownOutput() throws IOException {
            this.writeData();
            this._endPoint.shutdownOutput();
        }
        
        public void onIdleExpired(final long idleForMs) {
            try {
                this.shutdownOutput();
            }
            catch (Exception e) {
                ConnectHandler.LOG.debug(e);
                this.close();
            }
        }
    }
    
    public class ClientToProxyConnection implements AsyncConnection
    {
        private final Buffer _buffer;
        private final ConcurrentMap<String, Object> _context;
        private final SocketChannel _channel;
        private final EndPoint _endPoint;
        private final long _timestamp;
        private volatile ProxyToServerConnection _toServer;
        private boolean _firstTime;
        
        public ClientToProxyConnection(final ConcurrentMap<String, Object> context, final SocketChannel channel, final EndPoint endPoint, final long timestamp) {
            this._buffer = new IndirectNIOBuffer(1024);
            this._firstTime = true;
            this._context = context;
            this._channel = channel;
            this._endPoint = endPoint;
            this._timestamp = timestamp;
        }
        
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("ClientToProxy");
            builder.append("(:").append(this._endPoint.getLocalPort());
            builder.append("<=>:").append(this._endPoint.getRemotePort());
            return builder.append(")").toString();
        }
        
        public Connection handle() throws IOException {
            ConnectHandler.this._logger.debug("{}: begin reading from client", this);
            try {
                if (this._firstTime) {
                    this._firstTime = false;
                    ConnectHandler.this.register(this._channel, this._toServer);
                    ConnectHandler.this._logger.debug("{}: registered channel {} with connection {}", this, this._channel, this._toServer);
                }
                while (true) {
                    final int read = ConnectHandler.this.read(this._endPoint, this._buffer, this._context);
                    if (read == -1) {
                        ConnectHandler.this._logger.debug("{}: client closed connection {}", this, this._endPoint);
                        if (this._endPoint.isOutputShutdown() || !this._endPoint.isOpen()) {
                            this.closeServer();
                            break;
                        }
                        this._toServer.shutdownOutput();
                        break;
                    }
                    else {
                        if (read == 0) {
                            break;
                        }
                        ConnectHandler.this._logger.debug("{}: read from client {} bytes {}", this, read, this._endPoint);
                        final int written = ConnectHandler.this.write(this._toServer._endPoint, this._buffer, this._context);
                        ConnectHandler.this._logger.debug("{}: written to {} {} bytes", this, this._toServer, written);
                    }
                }
                return this;
            }
            catch (ClosedChannelException x) {
                ConnectHandler.this._logger.debug(x);
                this.closeServer();
                throw x;
            }
            catch (IOException x2) {
                ConnectHandler.this._logger.warn(this + ": unexpected exception", x2);
                this.close();
                throw x2;
            }
            catch (RuntimeException x3) {
                ConnectHandler.this._logger.warn(this + ": unexpected exception", x3);
                this.close();
                throw x3;
            }
            finally {
                ConnectHandler.this._logger.debug("{}: end reading from client", this);
            }
        }
        
        public void onInputShutdown() throws IOException {
        }
        
        public long getTimeStamp() {
            return this._timestamp;
        }
        
        public boolean isIdle() {
            return false;
        }
        
        public boolean isSuspended() {
            return false;
        }
        
        public void onClose() {
        }
        
        public void setConnection(final ProxyToServerConnection connection) {
            this._toServer = connection;
        }
        
        public void closeClient() throws IOException {
            this._endPoint.close();
        }
        
        public void closeServer() throws IOException {
            this._toServer.closeServer();
        }
        
        public void close() {
            try {
                this.closeClient();
            }
            catch (IOException x) {
                ConnectHandler.this._logger.debug(this + ": unexpected exception closing the client", x);
            }
            try {
                this.closeServer();
            }
            catch (IOException x) {
                ConnectHandler.this._logger.debug(this + ": unexpected exception closing the server", x);
            }
        }
        
        public void shutdownOutput() throws IOException {
            this._endPoint.shutdownOutput();
        }
        
        public void onIdleExpired(final long idleForMs) {
            try {
                this.shutdownOutput();
            }
            catch (Exception e) {
                ConnectHandler.LOG.debug(e);
                this.close();
            }
        }
    }
}
