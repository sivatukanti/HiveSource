// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import java.net.ProtocolException;
import org.eclipse.jetty.util.log.Log;
import java.util.Collection;
import org.eclipse.jetty.util.component.AggregateLifeCycle;
import java.util.Iterator;
import java.util.concurrent.RejectedExecutionException;
import java.lang.reflect.Constructor;
import org.eclipse.jetty.client.security.SecurityListener;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Connection;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.LinkedList;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.PathMap;
import org.eclipse.jetty.client.security.Authentication;
import org.eclipse.jetty.io.ByteArrayBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.Dumpable;

public class HttpDestination implements Dumpable
{
    private static final Logger LOG;
    private final List<HttpExchange> _queue;
    private final List<AbstractHttpConnection> _connections;
    private final BlockingQueue<Object> _newQueue;
    private final List<AbstractHttpConnection> _idle;
    private final HttpClient _client;
    private final Address _address;
    private final boolean _ssl;
    private final ByteArrayBuffer _hostHeader;
    private volatile int _maxConnections;
    private volatile int _maxQueueSize;
    private int _pendingConnections;
    private int _newConnection;
    private volatile Address _proxy;
    private Authentication _proxyAuthentication;
    private PathMap _authorizations;
    private List<HttpCookie> _cookies;
    
    HttpDestination(final HttpClient client, final Address address, final boolean ssl) {
        this._queue = new LinkedList<HttpExchange>();
        this._connections = new LinkedList<AbstractHttpConnection>();
        this._newQueue = new ArrayBlockingQueue<Object>(10, true);
        this._idle = new ArrayList<AbstractHttpConnection>();
        this._pendingConnections = 0;
        this._newConnection = 0;
        this._client = client;
        this._address = address;
        this._ssl = ssl;
        this._maxConnections = this._client.getMaxConnectionsPerAddress();
        this._maxQueueSize = this._client.getMaxQueueSizePerAddress();
        String addressString = address.getHost();
        if (address.getPort() != (this._ssl ? 443 : 80)) {
            addressString = addressString + ":" + address.getPort();
        }
        this._hostHeader = new ByteArrayBuffer(addressString);
    }
    
    public HttpClient getHttpClient() {
        return this._client;
    }
    
    public Address getAddress() {
        return this._address;
    }
    
    public boolean isSecure() {
        return this._ssl;
    }
    
    public Buffer getHostHeader() {
        return this._hostHeader;
    }
    
    public int getMaxConnections() {
        return this._maxConnections;
    }
    
    public void setMaxConnections(final int maxConnections) {
        this._maxConnections = maxConnections;
    }
    
    public int getMaxQueueSize() {
        return this._maxQueueSize;
    }
    
    public void setMaxQueueSize(final int maxQueueSize) {
        this._maxQueueSize = maxQueueSize;
    }
    
    public int getConnections() {
        synchronized (this) {
            return this._connections.size();
        }
    }
    
    public int getIdleConnections() {
        synchronized (this) {
            return this._idle.size();
        }
    }
    
    public void addAuthorization(final String pathSpec, final Authentication authorization) {
        synchronized (this) {
            if (this._authorizations == null) {
                this._authorizations = new PathMap();
            }
            this._authorizations.put((Object)pathSpec, authorization);
        }
    }
    
    public void addCookie(final HttpCookie cookie) {
        synchronized (this) {
            if (this._cookies == null) {
                this._cookies = new ArrayList<HttpCookie>();
            }
            this._cookies.add(cookie);
        }
    }
    
    private AbstractHttpConnection getConnection(long timeout) throws IOException {
        AbstractHttpConnection connection = null;
        while (connection == null && (connection = this.getIdleConnection()) == null && timeout > 0L) {
            boolean startConnection = false;
            synchronized (this) {
                final int totalConnections = this._connections.size() + this._pendingConnections;
                if (totalConnections < this._maxConnections) {
                    ++this._newConnection;
                    startConnection = true;
                }
            }
            if (startConnection) {
                this.startNewConnection();
                try {
                    final Object o = this._newQueue.take();
                    if (!(o instanceof AbstractHttpConnection)) {
                        throw (IOException)o;
                    }
                    connection = (AbstractHttpConnection)o;
                }
                catch (InterruptedException e) {
                    HttpDestination.LOG.ignore(e);
                }
            }
            else {
                try {
                    Thread.currentThread();
                    Thread.sleep(200L);
                    timeout -= 200L;
                }
                catch (InterruptedException e) {
                    HttpDestination.LOG.ignore(e);
                }
            }
        }
        return connection;
    }
    
    public AbstractHttpConnection reserveConnection(final long timeout) throws IOException {
        final AbstractHttpConnection connection = this.getConnection(timeout);
        if (connection != null) {
            connection.setReserved(true);
        }
        return connection;
    }
    
    public AbstractHttpConnection getIdleConnection() throws IOException {
        AbstractHttpConnection connection = null;
        do {
            synchronized (this) {
                if (connection != null) {
                    this._connections.remove(connection);
                    connection.close();
                    connection = null;
                }
                if (this._idle.size() > 0) {
                    connection = this._idle.remove(this._idle.size() - 1);
                }
            }
            if (connection == null) {
                return null;
            }
        } while (!connection.cancelIdleTimeout());
        return connection;
    }
    
    protected void startNewConnection() {
        try {
            synchronized (this) {
                ++this._pendingConnections;
            }
            final HttpClient.Connector connector = this._client._connector;
            if (connector != null) {
                connector.startConnection(this);
            }
        }
        catch (Exception e) {
            HttpDestination.LOG.debug(e);
            this.onConnectionFailed(e);
        }
    }
    
    public void onConnectionFailed(final Throwable throwable) {
        Throwable connect_failure = null;
        boolean startConnection = false;
        synchronized (this) {
            --this._pendingConnections;
            if (this._newConnection > 0) {
                connect_failure = throwable;
                --this._newConnection;
            }
            else if (this._queue.size() > 0) {
                final HttpExchange ex = this._queue.remove(0);
                if (ex.setStatus(9)) {
                    ex.getEventListener().onConnectionFailed(throwable);
                }
                if (!this._queue.isEmpty() && this._client.isStarted()) {
                    startConnection = true;
                }
            }
        }
        if (startConnection) {
            this.startNewConnection();
        }
        if (connect_failure != null) {
            try {
                this._newQueue.put(connect_failure);
            }
            catch (InterruptedException e) {
                HttpDestination.LOG.ignore(e);
            }
        }
    }
    
    public void onException(final Throwable throwable) {
        synchronized (this) {
            --this._pendingConnections;
            if (this._queue.size() > 0) {
                final HttpExchange ex = this._queue.remove(0);
                if (ex.setStatus(9)) {
                    ex.getEventListener().onException(throwable);
                }
            }
        }
    }
    
    public void onNewConnection(final AbstractHttpConnection connection) throws IOException {
        Connection q_connection = null;
        synchronized (this) {
            --this._pendingConnections;
            this._connections.add(connection);
            if (this._newConnection > 0) {
                q_connection = connection;
                --this._newConnection;
            }
            else if (this._queue.size() == 0) {
                connection.setIdleTimeout();
                this._idle.add(connection);
            }
            else {
                final EndPoint endPoint = connection.getEndPoint();
                if (this.isProxied() && endPoint instanceof SelectConnector.UpgradableEndPoint) {
                    final SelectConnector.UpgradableEndPoint proxyEndPoint = (SelectConnector.UpgradableEndPoint)endPoint;
                    final HttpExchange exchange = this._queue.get(0);
                    final ConnectExchange connect = new ConnectExchange(this.getAddress(), proxyEndPoint, exchange);
                    connect.setAddress(this.getProxy());
                    this.send(connection, connect);
                }
                else {
                    final HttpExchange exchange2 = this._queue.remove(0);
                    this.send(connection, exchange2);
                }
            }
        }
        if (q_connection != null) {
            try {
                this._newQueue.put(q_connection);
            }
            catch (InterruptedException e) {
                HttpDestination.LOG.ignore(e);
            }
        }
    }
    
    public void returnConnection(final AbstractHttpConnection connection, final boolean close) throws IOException {
        if (connection.isReserved()) {
            connection.setReserved(false);
        }
        if (close) {
            try {
                connection.close();
            }
            catch (IOException e) {
                HttpDestination.LOG.ignore(e);
            }
        }
        if (!this._client.isStarted()) {
            return;
        }
        if (!close && connection.getEndPoint().isOpen()) {
            synchronized (this) {
                if (this._queue.size() == 0) {
                    connection.setIdleTimeout();
                    this._idle.add(connection);
                }
                else {
                    final HttpExchange ex = this._queue.remove(0);
                    this.send(connection, ex);
                }
                this.notifyAll();
            }
        }
        else {
            boolean startConnection = false;
            synchronized (this) {
                this._connections.remove(connection);
                if (!this._queue.isEmpty()) {
                    startConnection = true;
                }
            }
            if (startConnection) {
                this.startNewConnection();
            }
        }
    }
    
    public void returnIdleConnection(final AbstractHttpConnection connection) {
        final long idleForMs = (connection != null && connection.getEndPoint() != null) ? connection.getEndPoint().getMaxIdleTime() : -1L;
        connection.onIdleExpired(idleForMs);
        boolean startConnection = false;
        synchronized (this) {
            this._idle.remove(connection);
            this._connections.remove(connection);
            if (!this._queue.isEmpty() && this._client.isStarted()) {
                startConnection = true;
            }
        }
        if (startConnection) {
            this.startNewConnection();
        }
    }
    
    public void send(final HttpExchange ex) throws IOException {
        final LinkedList<String> listeners = this._client.getRegisteredListeners();
        if (listeners != null) {
            for (int i = listeners.size(); i > 0; --i) {
                final String listenerClass = listeners.get(i - 1);
                try {
                    final Class listener = Class.forName(listenerClass);
                    final Constructor constructor = listener.getDeclaredConstructor(HttpDestination.class, HttpExchange.class);
                    final HttpEventListener elistener = constructor.newInstance(this, ex);
                    ex.setEventListener(elistener);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException("Unable to instantiate registered listener for destination: " + listenerClass);
                }
            }
        }
        if (this._client.hasRealms()) {
            ex.setEventListener(new SecurityListener(this, ex));
        }
        this.doSend(ex);
    }
    
    public void resend(final HttpExchange ex) throws IOException {
        ex.getEventListener().onRetry();
        ex.reset();
        this.doSend(ex);
    }
    
    protected void doSend(final HttpExchange ex) throws IOException {
        if (this._cookies != null) {
            StringBuilder buf = null;
            for (final HttpCookie cookie : this._cookies) {
                if (buf == null) {
                    buf = new StringBuilder();
                }
                else {
                    buf.append("; ");
                }
                buf.append(cookie.getName());
                buf.append("=");
                buf.append(cookie.getValue());
            }
            if (buf != null) {
                ex.addRequestHeader("Cookie", buf.toString());
            }
        }
        if (this._authorizations != null) {
            final Authentication auth = this._authorizations.match(ex.getRequestURI());
            if (auth != null) {
                auth.setCredentials(ex);
            }
        }
        ex.scheduleTimeout(this);
        final AbstractHttpConnection connection = this.getIdleConnection();
        if (connection != null) {
            this.send(connection, ex);
        }
        else {
            boolean startConnection = false;
            synchronized (this) {
                if (this._queue.size() == this._maxQueueSize) {
                    throw new RejectedExecutionException("Queue full for address " + this._address);
                }
                this._queue.add(ex);
                if (this._connections.size() + this._pendingConnections < this._maxConnections) {
                    startConnection = true;
                }
            }
            if (startConnection) {
                this.startNewConnection();
            }
        }
    }
    
    protected void exchangeExpired(final HttpExchange exchange) {
        synchronized (this) {
            this._queue.remove(exchange);
        }
    }
    
    protected void send(final AbstractHttpConnection connection, final HttpExchange exchange) throws IOException {
        synchronized (this) {
            if (!connection.send(exchange)) {
                if (exchange.getStatus() <= 1) {
                    this._queue.add(0, exchange);
                }
                this.returnIdleConnection(connection);
            }
        }
    }
    
    @Override
    public synchronized String toString() {
        return String.format("HttpDestination@%x//%s:%d(%d/%d,%d,%d/%d)%n", this.hashCode(), this._address.getHost(), this._address.getPort(), this._connections.size(), this._maxConnections, this._idle.size(), this._queue.size(), this._maxQueueSize);
    }
    
    public synchronized String toDetailString() {
        final StringBuilder b = new StringBuilder();
        b.append(this.toString());
        b.append('\n');
        synchronized (this) {
            for (final AbstractHttpConnection connection : this._connections) {
                b.append(connection.toDetailString());
                if (this._idle.contains(connection)) {
                    b.append(" IDLE");
                }
                b.append('\n');
            }
        }
        b.append("--");
        b.append('\n');
        return b.toString();
    }
    
    public void setProxy(final Address proxy) {
        this._proxy = proxy;
    }
    
    public Address getProxy() {
        return this._proxy;
    }
    
    public Authentication getProxyAuthentication() {
        return this._proxyAuthentication;
    }
    
    public void setProxyAuthentication(final Authentication authentication) {
        this._proxyAuthentication = authentication;
    }
    
    public boolean isProxied() {
        return this._proxy != null;
    }
    
    public void close() throws IOException {
        synchronized (this) {
            for (final AbstractHttpConnection connection : this._connections) {
                connection.close();
            }
        }
    }
    
    public String dump() {
        return AggregateLifeCycle.dump(this);
    }
    
    public void dump(final Appendable out, final String indent) throws IOException {
        synchronized (this) {
            out.append(String.valueOf(this) + "idle=" + this._idle.size() + " pending=" + this._pendingConnections).append("\n");
            AggregateLifeCycle.dump(out, indent, this._connections);
        }
    }
    
    static {
        LOG = Log.getLogger(HttpDestination.class);
    }
    
    private class ConnectExchange extends ContentExchange
    {
        private final SelectConnector.UpgradableEndPoint proxyEndPoint;
        private final HttpExchange exchange;
        
        public ConnectExchange(final Address serverAddress, final SelectConnector.UpgradableEndPoint proxyEndPoint, final HttpExchange exchange) {
            this.proxyEndPoint = proxyEndPoint;
            this.exchange = exchange;
            this.setMethod("CONNECT");
            this.setVersion(exchange.getVersion());
            final String serverHostAndPort = serverAddress.toString();
            this.setRequestURI(serverHostAndPort);
            this.addRequestHeader("Host", serverHostAndPort);
            this.addRequestHeader("Proxy-Connection", "keep-alive");
            this.addRequestHeader("User-Agent", "Jetty-Client");
        }
        
        @Override
        protected void onResponseComplete() throws IOException {
            final int responseStatus = this.getResponseStatus();
            if (responseStatus == 200) {
                this.proxyEndPoint.upgrade();
            }
            else if (responseStatus == 504) {
                this.onExpire();
            }
            else {
                this.onException(new ProtocolException("Proxy: " + this.proxyEndPoint.getRemoteAddr() + ":" + this.proxyEndPoint.getRemotePort() + " didn't return http return code 200, but " + responseStatus + " while trying to request: " + this.exchange.getAddress().toString()));
            }
        }
        
        @Override
        protected void onConnectionFailed(final Throwable x) {
            HttpDestination.this.onConnectionFailed(x);
        }
        
        @Override
        protected void onException(final Throwable x) {
            HttpDestination.this._queue.remove(this.exchange);
            if (this.exchange.setStatus(9)) {
                this.exchange.getEventListener().onException(x);
            }
        }
        
        @Override
        protected void onExpire() {
            HttpDestination.this._queue.remove(this.exchange);
            if (this.exchange.setStatus(8)) {
                this.exchange.getEventListener().onExpire();
            }
        }
    }
}
