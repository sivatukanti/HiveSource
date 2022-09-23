// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import org.eclipse.jetty.util.component.LifeCycle;
import java.io.InputStream;
import javax.net.ssl.SSLContext;
import java.util.Iterator;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.io.IOException;
import org.eclipse.jetty.http.HttpSchemes;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.http.HttpBuffersImpl;
import org.eclipse.jetty.util.AttributesMap;
import org.eclipse.jetty.client.security.RealmResolver;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import java.util.LinkedList;
import java.util.Set;
import org.eclipse.jetty.client.security.Authentication;
import org.eclipse.jetty.util.thread.Timeout;
import org.eclipse.jetty.util.thread.ThreadPool;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.http.HttpBuffers;
import org.eclipse.jetty.util.component.AggregateLifeCycle;

public class HttpClient extends AggregateLifeCycle implements HttpBuffers, Attributes, Dumpable
{
    public static final int CONNECTOR_SOCKET = 0;
    public static final int CONNECTOR_SELECT_CHANNEL = 2;
    private int _connectorType;
    private boolean _useDirectBuffers;
    private boolean _connectBlocking;
    private int _maxConnectionsPerAddress;
    private int _maxQueueSizePerAddress;
    private ConcurrentMap<Address, HttpDestination> _destinations;
    ThreadPool _threadPool;
    Connector _connector;
    private long _idleTimeout;
    private long _timeout;
    private int _connectTimeout;
    private Timeout _timeoutQ;
    private Timeout _idleTimeoutQ;
    private Address _proxy;
    private Authentication _proxyAuthentication;
    private Set<String> _noProxy;
    private int _maxRetries;
    private int _maxRedirects;
    private LinkedList<String> _registeredListeners;
    private final SslContextFactory _sslContextFactory;
    private RealmResolver _realmResolver;
    private AttributesMap _attributes;
    private final HttpBuffersImpl _buffers;
    
    private void setBufferTypes() {
        if (this._connectorType == 0) {
            this._buffers.setRequestBufferType(Buffers.Type.BYTE_ARRAY);
            this._buffers.setRequestHeaderType(Buffers.Type.BYTE_ARRAY);
            this._buffers.setResponseBufferType(Buffers.Type.BYTE_ARRAY);
            this._buffers.setResponseHeaderType(Buffers.Type.BYTE_ARRAY);
        }
        else {
            this._buffers.setRequestBufferType(Buffers.Type.DIRECT);
            this._buffers.setRequestHeaderType(this._useDirectBuffers ? Buffers.Type.DIRECT : Buffers.Type.INDIRECT);
            this._buffers.setResponseBufferType(Buffers.Type.DIRECT);
            this._buffers.setResponseHeaderType(this._useDirectBuffers ? Buffers.Type.DIRECT : Buffers.Type.INDIRECT);
        }
    }
    
    public HttpClient() {
        this(new SslContextFactory());
    }
    
    public HttpClient(final SslContextFactory sslContextFactory) {
        this._connectorType = 2;
        this._useDirectBuffers = true;
        this._connectBlocking = true;
        this._maxConnectionsPerAddress = Integer.MAX_VALUE;
        this._maxQueueSizePerAddress = Integer.MAX_VALUE;
        this._destinations = new ConcurrentHashMap<Address, HttpDestination>();
        this._idleTimeout = 20000L;
        this._timeout = 320000L;
        this._connectTimeout = 75000;
        this._timeoutQ = new Timeout();
        this._idleTimeoutQ = new Timeout();
        this._maxRetries = 3;
        this._maxRedirects = 20;
        this._attributes = new AttributesMap();
        this._buffers = new HttpBuffersImpl();
        this.addBean(this._sslContextFactory = sslContextFactory);
        this.addBean(this._buffers);
    }
    
    public boolean isConnectBlocking() {
        return this._connectBlocking;
    }
    
    public void setConnectBlocking(final boolean connectBlocking) {
        this._connectBlocking = connectBlocking;
    }
    
    public void send(final HttpExchange exchange) throws IOException {
        final boolean ssl = HttpSchemes.HTTPS_BUFFER.equalsIgnoreCase(exchange.getScheme());
        exchange.setStatus(1);
        final HttpDestination destination = this.getDestination(exchange.getAddress(), ssl);
        destination.send(exchange);
    }
    
    public ThreadPool getThreadPool() {
        return this._threadPool;
    }
    
    public void setThreadPool(final ThreadPool threadPool) {
        this.removeBean(this._threadPool);
        this.addBean(this._threadPool = threadPool);
    }
    
    public Object getAttribute(final String name) {
        return this._attributes.getAttribute(name);
    }
    
    public Enumeration getAttributeNames() {
        return this._attributes.getAttributeNames();
    }
    
    public void removeAttribute(final String name) {
        this._attributes.removeAttribute(name);
    }
    
    public void setAttribute(final String name, final Object attribute) {
        this._attributes.setAttribute(name, attribute);
    }
    
    public void clearAttributes() {
        this._attributes.clearAttributes();
    }
    
    public HttpDestination getDestination(final Address remote, final boolean ssl) throws IOException {
        if (remote == null) {
            throw new UnknownHostException("Remote socket address cannot be null.");
        }
        HttpDestination destination = this._destinations.get(remote);
        if (destination == null) {
            destination = new HttpDestination(this, remote, ssl);
            if (this._proxy != null && (this._noProxy == null || !this._noProxy.contains(remote.getHost()))) {
                destination.setProxy(this._proxy);
                if (this._proxyAuthentication != null) {
                    destination.setProxyAuthentication(this._proxyAuthentication);
                }
            }
            final HttpDestination other = this._destinations.putIfAbsent(remote, destination);
            if (other != null) {
                destination = other;
            }
        }
        return destination;
    }
    
    public void schedule(final Timeout.Task task) {
        this._timeoutQ.schedule(task);
    }
    
    public void schedule(final Timeout.Task task, final long timeout) {
        this._timeoutQ.schedule(task, timeout - this._timeoutQ.getDuration());
    }
    
    public void scheduleIdle(final Timeout.Task task) {
        this._idleTimeoutQ.schedule(task);
    }
    
    public void cancel(final Timeout.Task task) {
        task.cancel();
    }
    
    public boolean getUseDirectBuffers() {
        return this._useDirectBuffers;
    }
    
    public void setRealmResolver(final RealmResolver resolver) {
        this._realmResolver = resolver;
    }
    
    public RealmResolver getRealmResolver() {
        return this._realmResolver;
    }
    
    public boolean hasRealms() {
        return this._realmResolver != null;
    }
    
    public void registerListener(final String listenerClass) {
        if (this._registeredListeners == null) {
            this._registeredListeners = new LinkedList<String>();
        }
        this._registeredListeners.add(listenerClass);
    }
    
    public LinkedList<String> getRegisteredListeners() {
        return this._registeredListeners;
    }
    
    public void setUseDirectBuffers(final boolean direct) {
        this._useDirectBuffers = direct;
        this.setBufferTypes();
    }
    
    public int getConnectorType() {
        return this._connectorType;
    }
    
    public void setConnectorType(final int connectorType) {
        this._connectorType = connectorType;
        this.setBufferTypes();
    }
    
    public int getMaxConnectionsPerAddress() {
        return this._maxConnectionsPerAddress;
    }
    
    public void setMaxConnectionsPerAddress(final int maxConnectionsPerAddress) {
        this._maxConnectionsPerAddress = maxConnectionsPerAddress;
    }
    
    public int getMaxQueueSizePerAddress() {
        return this._maxQueueSizePerAddress;
    }
    
    public void setMaxQueueSizePerAddress(final int maxQueueSizePerAddress) {
        this._maxQueueSizePerAddress = maxQueueSizePerAddress;
    }
    
    @Override
    protected void doStart() throws Exception {
        this.setBufferTypes();
        this._timeoutQ.setDuration(this._timeout);
        this._timeoutQ.setNow();
        this._idleTimeoutQ.setDuration(this._idleTimeout);
        this._idleTimeoutQ.setNow();
        if (this._threadPool == null) {
            final QueuedThreadPool pool = new LocalQueuedThreadPool();
            pool.setMaxThreads(16);
            pool.setDaemon(true);
            pool.setName("HttpClient");
            this.addBean(this._threadPool = pool, true);
        }
        this.addBean(this._connector = ((this._connectorType == 2) ? new SelectConnector(this) : new SocketConnector(this)), true);
        super.doStart();
        this._threadPool.dispatch((Runnable)new Runnable() {
            public void run() {
                while (HttpClient.this.isRunning()) {
                    HttpClient.this._timeoutQ.tick(System.currentTimeMillis());
                    HttpClient.this._idleTimeoutQ.tick(HttpClient.this._timeoutQ.getNow());
                    try {
                        Thread.sleep(200L);
                    }
                    catch (InterruptedException ignored) {}
                }
            }
        });
    }
    
    @Override
    protected void doStop() throws Exception {
        for (final HttpDestination destination : this._destinations.values()) {
            destination.close();
        }
        this._timeoutQ.cancelAll();
        this._idleTimeoutQ.cancelAll();
        super.doStop();
        if (this._threadPool instanceof LocalQueuedThreadPool) {
            this.removeBean(this._threadPool);
            this._threadPool = null;
        }
        this.removeBean(this._connector);
    }
    
    protected SSLContext getSSLContext() {
        return this._sslContextFactory.getSslContext();
    }
    
    public SslContextFactory getSslContextFactory() {
        return this._sslContextFactory;
    }
    
    public long getIdleTimeout() {
        return this._idleTimeout;
    }
    
    public void setIdleTimeout(final long ms) {
        this._idleTimeout = ms;
    }
    
    @Deprecated
    public int getSoTimeout() {
        return this.getTimeout().intValue();
    }
    
    @Deprecated
    public void setSoTimeout(final int timeout) {
        this.setTimeout(timeout);
    }
    
    public long getTimeout() {
        return this._timeout;
    }
    
    public void setTimeout(final long timeout) {
        this._timeout = timeout;
    }
    
    public int getConnectTimeout() {
        return this._connectTimeout;
    }
    
    public void setConnectTimeout(final int connectTimeout) {
        this._connectTimeout = connectTimeout;
    }
    
    public Address getProxy() {
        return this._proxy;
    }
    
    public void setProxy(final Address proxy) {
        this._proxy = proxy;
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
    
    public Set<String> getNoProxy() {
        return this._noProxy;
    }
    
    public void setNoProxy(final Set<String> noProxyAddresses) {
        this._noProxy = noProxyAddresses;
    }
    
    public int maxRetries() {
        return this._maxRetries;
    }
    
    public void setMaxRetries(final int retries) {
        this._maxRetries = retries;
    }
    
    public int maxRedirects() {
        return this._maxRedirects;
    }
    
    public void setMaxRedirects(final int redirects) {
        this._maxRedirects = redirects;
    }
    
    public int getRequestBufferSize() {
        return this._buffers.getRequestBufferSize();
    }
    
    public void setRequestBufferSize(final int requestBufferSize) {
        this._buffers.setRequestBufferSize(requestBufferSize);
    }
    
    public int getRequestHeaderSize() {
        return this._buffers.getRequestHeaderSize();
    }
    
    public void setRequestHeaderSize(final int requestHeaderSize) {
        this._buffers.setRequestHeaderSize(requestHeaderSize);
    }
    
    public int getResponseBufferSize() {
        return this._buffers.getResponseBufferSize();
    }
    
    public void setResponseBufferSize(final int responseBufferSize) {
        this._buffers.setResponseBufferSize(responseBufferSize);
    }
    
    public int getResponseHeaderSize() {
        return this._buffers.getResponseHeaderSize();
    }
    
    public void setResponseHeaderSize(final int responseHeaderSize) {
        this._buffers.setResponseHeaderSize(responseHeaderSize);
    }
    
    public Buffers.Type getRequestBufferType() {
        return this._buffers.getRequestBufferType();
    }
    
    public Buffers.Type getRequestHeaderType() {
        return this._buffers.getRequestHeaderType();
    }
    
    public Buffers.Type getResponseBufferType() {
        return this._buffers.getResponseBufferType();
    }
    
    public Buffers.Type getResponseHeaderType() {
        return this._buffers.getResponseHeaderType();
    }
    
    public void setRequestBuffers(final Buffers requestBuffers) {
        this._buffers.setRequestBuffers(requestBuffers);
    }
    
    public void setResponseBuffers(final Buffers responseBuffers) {
        this._buffers.setResponseBuffers(responseBuffers);
    }
    
    public Buffers getRequestBuffers() {
        return this._buffers.getRequestBuffers();
    }
    
    public Buffers getResponseBuffers() {
        return this._buffers.getResponseBuffers();
    }
    
    public void setMaxBuffers(final int maxBuffers) {
        this._buffers.setMaxBuffers(maxBuffers);
    }
    
    public int getMaxBuffers() {
        return this._buffers.getMaxBuffers();
    }
    
    @Deprecated
    public String getTrustStoreLocation() {
        return this._sslContextFactory.getTrustStore();
    }
    
    @Deprecated
    public void setTrustStoreLocation(final String trustStoreLocation) {
        this._sslContextFactory.setTrustStore(trustStoreLocation);
    }
    
    @Deprecated
    public InputStream getTrustStoreInputStream() {
        return this._sslContextFactory.getTrustStoreInputStream();
    }
    
    @Deprecated
    public void setTrustStoreInputStream(final InputStream trustStoreInputStream) {
        this._sslContextFactory.setTrustStoreInputStream(trustStoreInputStream);
    }
    
    @Deprecated
    public String getKeyStoreLocation() {
        return this._sslContextFactory.getKeyStorePath();
    }
    
    @Deprecated
    public void setKeyStoreLocation(final String keyStoreLocation) {
        this._sslContextFactory.setKeyStorePath(keyStoreLocation);
    }
    
    @Deprecated
    public InputStream getKeyStoreInputStream() {
        return this._sslContextFactory.getKeyStoreInputStream();
    }
    
    @Deprecated
    public void setKeyStoreInputStream(final InputStream keyStoreInputStream) {
        this._sslContextFactory.setKeyStoreInputStream(keyStoreInputStream);
    }
    
    @Deprecated
    public void setKeyStorePassword(final String keyStorePassword) {
        this._sslContextFactory.setKeyStorePassword(keyStorePassword);
    }
    
    @Deprecated
    public void setKeyManagerPassword(final String keyManagerPassword) {
        this._sslContextFactory.setKeyManagerPassword(keyManagerPassword);
    }
    
    @Deprecated
    public void setTrustStorePassword(final String trustStorePassword) {
        this._sslContextFactory.setTrustStorePassword(trustStorePassword);
    }
    
    @Deprecated
    public String getKeyStoreType() {
        return this._sslContextFactory.getKeyStoreType();
    }
    
    @Deprecated
    public void setKeyStoreType(final String keyStoreType) {
        this._sslContextFactory.setKeyStoreType(keyStoreType);
    }
    
    @Deprecated
    public String getTrustStoreType() {
        return this._sslContextFactory.getTrustStoreType();
    }
    
    @Deprecated
    public void setTrustStoreType(final String trustStoreType) {
        this._sslContextFactory.setTrustStoreType(trustStoreType);
    }
    
    @Deprecated
    public String getKeyManagerAlgorithm() {
        return this._sslContextFactory.getSslKeyManagerFactoryAlgorithm();
    }
    
    @Deprecated
    public void setKeyManagerAlgorithm(final String keyManagerAlgorithm) {
        this._sslContextFactory.setSslKeyManagerFactoryAlgorithm(keyManagerAlgorithm);
    }
    
    @Deprecated
    public String getTrustManagerAlgorithm() {
        return this._sslContextFactory.getTrustManagerFactoryAlgorithm();
    }
    
    @Deprecated
    public void setTrustManagerAlgorithm(final String trustManagerAlgorithm) {
        this._sslContextFactory.setTrustManagerFactoryAlgorithm(trustManagerAlgorithm);
    }
    
    @Deprecated
    public String getProtocol() {
        return this._sslContextFactory.getProtocol();
    }
    
    @Deprecated
    public void setProtocol(final String protocol) {
        this._sslContextFactory.setProtocol(protocol);
    }
    
    @Deprecated
    public String getProvider() {
        return this._sslContextFactory.getProvider();
    }
    
    @Deprecated
    public void setProvider(final String provider) {
        this.setProvider(provider);
    }
    
    @Deprecated
    public String getSecureRandomAlgorithm() {
        return this._sslContextFactory.getSecureRandomAlgorithm();
    }
    
    @Deprecated
    public void setSecureRandomAlgorithm(final String secureRandomAlgorithm) {
        this._sslContextFactory.setSecureRandomAlgorithm(secureRandomAlgorithm);
    }
    
    private static class LocalQueuedThreadPool extends QueuedThreadPool
    {
    }
    
    interface Connector extends LifeCycle
    {
        void startConnection(final HttpDestination p0) throws IOException;
    }
}
