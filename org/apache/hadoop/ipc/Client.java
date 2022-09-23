// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.io.DataInput;
import org.apache.hadoop.io.WritableUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.Flushable;
import org.apache.hadoop.io.retry.RetryPolicies;
import java.net.SocketTimeoutException;
import java.io.FilterInputStream;
import java.util.Map;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.io.Closeable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.ipc.protobuf.IpcConnectionContextProtos;
import java.io.DataOutputStream;
import org.apache.htrace.core.Span;
import java.io.InputStream;
import org.apache.htrace.core.Tracer;
import java.security.PrivilegedExceptionAction;
import java.util.Random;
import java.net.InetAddress;
import org.apache.hadoop.net.ConnectTimeoutException;
import java.net.SocketAddress;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.KerberosInfo;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.OutputStream;
import org.apache.hadoop.util.ProtoUtil;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Hashtable;
import org.apache.hadoop.io.retry.RetryPolicy;
import java.net.Socket;
import org.apache.hadoop.security.SaslRpcClient;
import org.apache.hadoop.security.SaslRpcServer;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.net.InetSocketAddress;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionException;
import java.util.Iterator;
import org.apache.hadoop.net.NetUtils;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.util.StringUtils;
import java.util.Arrays;
import java.io.EOFException;
import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.concurrent.ExecutorService;
import javax.net.SocketFactory;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentMap;
import java.io.IOException;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.concurrent.AsyncGet;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class Client implements AutoCloseable
{
    public static final Logger LOG;
    private static final AtomicInteger callIdCounter;
    private static final ThreadLocal<Integer> callId;
    private static final ThreadLocal<Integer> retryCount;
    private static final ThreadLocal<Object> EXTERNAL_CALL_HANDLER;
    private static final ThreadLocal<AsyncGet<? extends Writable, IOException>> ASYNC_RPC_RESPONSE;
    private static final ThreadLocal<Boolean> asynchronousMode;
    private ConcurrentMap<ConnectionId, Connection> connections;
    private Class<? extends Writable> valueClass;
    private AtomicBoolean running;
    private final Configuration conf;
    private SocketFactory socketFactory;
    private int refCount;
    private final int connectionTimeout;
    private final boolean fallbackAllowed;
    private final boolean bindToWildCardAddress;
    private final byte[] clientId;
    private final int maxAsyncCalls;
    private final AtomicInteger asyncCallCounter;
    private final ExecutorService sendParamsExecutor;
    private static final ClientExecutorServiceFactory clientExcecutorFactory;
    
    @InterfaceStability.Unstable
    public static <T extends Writable> AsyncGet<T, IOException> getAsyncRpcResponse() {
        return (AsyncGet<T, IOException>)Client.ASYNC_RPC_RESPONSE.get();
    }
    
    public static void setCallIdAndRetryCount(final int cid, final int rc, final Object externalHandler) {
        Preconditions.checkArgument(cid != -2);
        Preconditions.checkState(Client.callId.get() == null);
        Preconditions.checkArgument(rc != -1);
        Client.callId.set(cid);
        Client.retryCount.set(rc);
        Client.EXTERNAL_CALL_HANDLER.set(externalHandler);
    }
    
    public static final void setPingInterval(final Configuration conf, final int pingInterval) {
        conf.setInt("ipc.ping.interval", pingInterval);
    }
    
    public static final int getPingInterval(final Configuration conf) {
        return conf.getInt("ipc.ping.interval", 60000);
    }
    
    @Deprecated
    public static final int getTimeout(final Configuration conf) {
        final int timeout = getRpcTimeout(conf);
        if (timeout > 0) {
            return timeout;
        }
        if (!conf.getBoolean("ipc.client.ping", true)) {
            return getPingInterval(conf);
        }
        return -1;
    }
    
    public static final int getRpcTimeout(final Configuration conf) {
        final int timeout = conf.getInt("ipc.client.rpc-timeout.ms", 0);
        return (timeout < 0) ? 0 : timeout;
    }
    
    public static final void setConnectTimeout(final Configuration conf, final int timeout) {
        conf.setInt("ipc.client.connect.timeout", timeout);
    }
    
    @VisibleForTesting
    public static final ExecutorService getClientExecutor() {
        return Client.clientExcecutorFactory.clientExecutor;
    }
    
    synchronized void incCount() {
        ++this.refCount;
    }
    
    synchronized void decCount() {
        --this.refCount;
    }
    
    synchronized boolean isZeroReference() {
        return this.refCount == 0;
    }
    
    void checkResponse(final RpcHeaderProtos.RpcResponseHeaderProto header) throws IOException {
        if (header == null) {
            throw new EOFException("Response is null.");
        }
        if (header.hasClientId()) {
            final byte[] id = header.getClientId().toByteArray();
            if (!Arrays.equals(id, RpcConstants.DUMMY_CLIENT_ID) && !Arrays.equals(id, this.clientId)) {
                throw new IOException("Client IDs not matched: local ID=" + StringUtils.byteToHexString(this.clientId) + ", ID in response=" + StringUtils.byteToHexString(header.getClientId().toByteArray()));
            }
        }
    }
    
    Call createCall(final RPC.RpcKind rpcKind, final Writable rpcRequest) {
        return new Call(rpcKind, rpcRequest);
    }
    
    public Client(final Class<? extends Writable> valueClass, final Configuration conf, final SocketFactory factory) {
        this.connections = new ConcurrentHashMap<ConnectionId, Connection>();
        this.running = new AtomicBoolean(true);
        this.refCount = 1;
        this.asyncCallCounter = new AtomicInteger(0);
        this.valueClass = valueClass;
        this.conf = conf;
        this.socketFactory = factory;
        this.connectionTimeout = conf.getInt("ipc.client.connect.timeout", 20000);
        this.fallbackAllowed = conf.getBoolean("ipc.client.fallback-to-simple-auth-allowed", false);
        this.bindToWildCardAddress = conf.getBoolean("ipc.client.bind.wildcard.addr", false);
        this.clientId = ClientId.getClientId();
        this.sendParamsExecutor = Client.clientExcecutorFactory.refAndGetInstance();
        this.maxAsyncCalls = conf.getInt("ipc.client.async.calls.max", 100);
    }
    
    public Client(final Class<? extends Writable> valueClass, final Configuration conf) {
        this(valueClass, conf, NetUtils.getDefaultSocketFactory(conf));
    }
    
    SocketFactory getSocketFactory() {
        return this.socketFactory;
    }
    
    public void stop() {
        if (Client.LOG.isDebugEnabled()) {
            Client.LOG.debug("Stopping client");
        }
        if (!this.running.compareAndSet(true, false)) {
            return;
        }
        for (final Connection conn : this.connections.values()) {
            conn.interrupt();
            conn.interruptConnectingThread();
        }
        while (!this.connections.isEmpty()) {
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException ex) {}
        }
        Client.clientExcecutorFactory.unrefAndCleanup();
    }
    
    public Writable call(final RPC.RpcKind rpcKind, final Writable rpcRequest, final ConnectionId remoteId, final AtomicBoolean fallbackToSimpleAuth) throws IOException {
        return this.call(rpcKind, rpcRequest, remoteId, 0, fallbackToSimpleAuth);
    }
    
    private void checkAsyncCall() throws IOException {
        if (isAsynchronousMode() && this.asyncCallCounter.incrementAndGet() > this.maxAsyncCalls) {
            this.asyncCallCounter.decrementAndGet();
            final String errMsg = String.format("Exceeded limit of max asynchronous calls: %d, please configure %s to adjust it.", this.maxAsyncCalls, "ipc.client.async.calls.max");
            throw new AsyncCallLimitExceededException(errMsg);
        }
    }
    
    Writable call(final RPC.RpcKind rpcKind, final Writable rpcRequest, final ConnectionId remoteId, final int serviceClass, final AtomicBoolean fallbackToSimpleAuth) throws IOException {
        final Call call = this.createCall(rpcKind, rpcRequest);
        final Connection connection = this.getConnection(remoteId, call, serviceClass, fallbackToSimpleAuth);
        try {
            this.checkAsyncCall();
            try {
                connection.sendRpcRequest(call);
            }
            catch (RejectedExecutionException e) {
                throw new IOException("connection has been closed", e);
            }
            catch (InterruptedException e2) {
                Thread.currentThread().interrupt();
                Client.LOG.warn("interrupted waiting to send rpc request to server", e2);
                throw new IOException(e2);
            }
        }
        catch (Exception e3) {
            if (isAsynchronousMode()) {
                this.releaseAsyncCall();
            }
            throw e3;
        }
        if (isAsynchronousMode()) {
            final AsyncGet<Writable, IOException> asyncGet = new AsyncGet<Writable, IOException>() {
                @Override
                public Writable get(final long timeout, final TimeUnit unit) throws IOException, TimeoutException {
                    boolean done = true;
                    try {
                        final Writable w = Client.this.getRpcResponse(call, connection, timeout, unit);
                        if (w == null) {
                            done = false;
                            throw new TimeoutException(call + " timed out " + timeout + " " + unit);
                        }
                        return w;
                    }
                    finally {
                        if (done) {
                            Client.this.releaseAsyncCall();
                        }
                    }
                }
                
                @Override
                public boolean isDone() {
                    synchronized (call) {
                        return call.done;
                    }
                }
            };
            Client.ASYNC_RPC_RESPONSE.set(asyncGet);
            return null;
        }
        return this.getRpcResponse(call, connection, -1L, null);
    }
    
    @InterfaceStability.Unstable
    public static boolean isAsynchronousMode() {
        return Client.asynchronousMode.get();
    }
    
    @InterfaceStability.Unstable
    public static void setAsynchronousMode(final boolean async) {
        Client.asynchronousMode.set(async);
    }
    
    private void releaseAsyncCall() {
        this.asyncCallCounter.decrementAndGet();
    }
    
    @VisibleForTesting
    int getAsyncCallCount() {
        return this.asyncCallCounter.get();
    }
    
    private Writable getRpcResponse(final Call call, final Connection connection, final long timeout, final TimeUnit unit) throws IOException {
        synchronized (call) {
            while (!call.done) {
                try {
                    AsyncGet.Util.wait(call, timeout, unit);
                    if (timeout >= 0L && !call.done) {
                        return null;
                    }
                    continue;
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException("Call interrupted");
                }
                break;
            }
            if (call.error == null) {
                return call.getRpcResponse();
            }
            if (call.error instanceof RemoteException) {
                call.error.fillInStackTrace();
                throw call.error;
            }
            final InetSocketAddress address = connection.getRemoteAddress();
            throw NetUtils.wrapException(address.getHostName(), address.getPort(), NetUtils.getHostname(), 0, call.error);
        }
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    Set<ConnectionId> getConnectionIds() {
        return this.connections.keySet();
    }
    
    private Connection getConnection(final ConnectionId remoteId, final Call call, final int serviceClass, final AtomicBoolean fallbackToSimpleAuth) throws IOException {
        if (!this.running.get()) {
            throw new IOException("The client is stopped");
        }
        Connection connection;
        while (true) {
            connection = this.connections.get(remoteId);
            if (connection == null) {
                connection = new Connection(remoteId, serviceClass);
                final Connection existing = this.connections.putIfAbsent(remoteId, connection);
                if (existing != null) {
                    connection = existing;
                }
            }
            if (connection.addCall(call)) {
                break;
            }
            this.connections.remove(remoteId, connection);
        }
        connection.setupIOstreams(fallbackToSimpleAuth);
        return connection;
    }
    
    public static int nextCallId() {
        return Client.callIdCounter.getAndIncrement() & Integer.MAX_VALUE;
    }
    
    @InterfaceStability.Unstable
    @Override
    public void close() throws Exception {
        this.stop();
    }
    
    static {
        LOG = LoggerFactory.getLogger(Client.class);
        callIdCounter = new AtomicInteger();
        callId = new ThreadLocal<Integer>();
        retryCount = new ThreadLocal<Integer>();
        EXTERNAL_CALL_HANDLER = new ThreadLocal<Object>();
        ASYNC_RPC_RESPONSE = new ThreadLocal<AsyncGet<? extends Writable, IOException>>();
        asynchronousMode = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return false;
            }
        };
        clientExcecutorFactory = new ClientExecutorServiceFactory();
    }
    
    private static class ClientExecutorServiceFactory
    {
        private int executorRefCount;
        private ExecutorService clientExecutor;
        
        private ClientExecutorServiceFactory() {
            this.executorRefCount = 0;
            this.clientExecutor = null;
        }
        
        synchronized ExecutorService refAndGetInstance() {
            if (this.executorRefCount == 0) {
                this.clientExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("IPC Parameter Sending Thread #%d").build());
            }
            ++this.executorRefCount;
            return this.clientExecutor;
        }
        
        synchronized ExecutorService unrefAndCleanup() {
            --this.executorRefCount;
            assert this.executorRefCount >= 0;
            if (this.executorRefCount == 0) {
                this.clientExecutor.shutdown();
                try {
                    if (!this.clientExecutor.awaitTermination(1L, TimeUnit.MINUTES)) {
                        this.clientExecutor.shutdownNow();
                    }
                }
                catch (InterruptedException e) {
                    Client.LOG.warn("Interrupted while waiting for clientExecutor to stop");
                    this.clientExecutor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
                this.clientExecutor = null;
            }
            return this.clientExecutor;
        }
    }
    
    static class Call
    {
        final int id;
        final int retry;
        final Writable rpcRequest;
        Writable rpcResponse;
        IOException error;
        final RPC.RpcKind rpcKind;
        boolean done;
        private final Object externalHandler;
        
        private Call(final RPC.RpcKind rpcKind, final Writable param) {
            this.rpcKind = rpcKind;
            this.rpcRequest = param;
            final Integer id = Client.callId.get();
            if (id == null) {
                this.id = Client.nextCallId();
            }
            else {
                Client.callId.set(null);
                this.id = id;
            }
            final Integer rc = Client.retryCount.get();
            if (rc == null) {
                this.retry = 0;
            }
            else {
                this.retry = rc;
            }
            this.externalHandler = Client.EXTERNAL_CALL_HANDLER.get();
        }
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName() + this.id;
        }
        
        protected synchronized void callComplete() {
            this.done = true;
            this.notify();
            if (this.externalHandler != null) {
                synchronized (this.externalHandler) {
                    this.externalHandler.notify();
                }
            }
        }
        
        public synchronized void setException(final IOException error) {
            this.error = error;
            this.callComplete();
        }
        
        public synchronized void setRpcResponse(final Writable rpcResponse) {
            this.rpcResponse = rpcResponse;
            this.callComplete();
        }
        
        public synchronized Writable getRpcResponse() {
            return this.rpcResponse;
        }
    }
    
    private class Connection extends Thread
    {
        private InetSocketAddress server;
        private final ConnectionId remoteId;
        private SaslRpcServer.AuthMethod authMethod;
        private Server.AuthProtocol authProtocol;
        private int serviceClass;
        private SaslRpcClient saslRpcClient;
        private Socket socket;
        private IpcStreams ipcStreams;
        private final int maxResponseLength;
        private final int rpcTimeout;
        private int maxIdleTime;
        private final RetryPolicy connectionRetryPolicy;
        private final int maxRetriesOnSasl;
        private int maxRetriesOnSocketTimeouts;
        private final boolean tcpNoDelay;
        private final boolean tcpLowLatency;
        private final boolean doPing;
        private final int pingInterval;
        private final int soTimeout;
        private byte[] pingRequest;
        private Hashtable<Integer, Call> calls;
        private AtomicLong lastActivity;
        private AtomicBoolean shouldCloseConnection;
        private IOException closeException;
        private final Object sendRpcRequestLock;
        private AtomicReference<Thread> connectingThread;
        
        public Connection(final ConnectionId remoteId, final int serviceClass) throws IOException {
            this.socket = null;
            this.calls = new Hashtable<Integer, Call>();
            this.lastActivity = new AtomicLong();
            this.shouldCloseConnection = new AtomicBoolean();
            this.sendRpcRequestLock = new Object();
            this.connectingThread = new AtomicReference<Thread>();
            this.remoteId = remoteId;
            this.server = remoteId.getAddress();
            if (this.server.isUnresolved()) {
                throw NetUtils.wrapException(this.server.getHostName(), this.server.getPort(), null, 0, new UnknownHostException());
            }
            this.maxResponseLength = remoteId.conf.getInt("ipc.maximum.response.length", 134217728);
            this.rpcTimeout = remoteId.getRpcTimeout();
            this.maxIdleTime = remoteId.getMaxIdleTime();
            this.connectionRetryPolicy = remoteId.connectionRetryPolicy;
            this.maxRetriesOnSasl = remoteId.getMaxRetriesOnSasl();
            this.maxRetriesOnSocketTimeouts = remoteId.getMaxRetriesOnSocketTimeouts();
            this.tcpNoDelay = remoteId.getTcpNoDelay();
            this.tcpLowLatency = remoteId.getTcpLowLatency();
            this.doPing = remoteId.getDoPing();
            if (this.doPing) {
                final ResponseBuffer buf = new ResponseBuffer();
                final RpcHeaderProtos.RpcRequestHeaderProto pingHeader = ProtoUtil.makeRpcRequestHeader(RPC.RpcKind.RPC_PROTOCOL_BUFFER, RpcHeaderProtos.RpcRequestHeaderProto.OperationProto.RPC_FINAL_PACKET, -4, -1, Client.this.clientId);
                pingHeader.writeDelimitedTo(buf);
                this.pingRequest = buf.toByteArray();
            }
            this.pingInterval = remoteId.getPingInterval();
            if (this.rpcTimeout > 0) {
                this.soTimeout = ((this.doPing && this.pingInterval < this.rpcTimeout) ? this.pingInterval : this.rpcTimeout);
            }
            else {
                this.soTimeout = this.pingInterval;
            }
            this.serviceClass = serviceClass;
            if (Client.LOG.isDebugEnabled()) {
                Client.LOG.debug("The ping interval is " + this.pingInterval + " ms.");
            }
            final UserGroupInformation ticket = remoteId.getTicket();
            final boolean trySasl = UserGroupInformation.isSecurityEnabled() || (ticket != null && !ticket.getTokens().isEmpty());
            this.authProtocol = (trySasl ? Server.AuthProtocol.SASL : Server.AuthProtocol.NONE);
            this.setName("IPC Client (" + Client.this.socketFactory.hashCode() + ") connection to " + this.server.toString() + " from " + ((ticket == null) ? "an unknown user" : ticket.getUserName()));
            this.setDaemon(true);
        }
        
        private void touch() {
            this.lastActivity.set(Time.now());
        }
        
        private synchronized boolean addCall(final Call call) {
            if (this.shouldCloseConnection.get()) {
                return false;
            }
            this.calls.put(call.id, call);
            this.notify();
            return true;
        }
        
        private synchronized void disposeSasl() {
            if (this.saslRpcClient != null) {
                try {
                    this.saslRpcClient.dispose();
                    this.saslRpcClient = null;
                }
                catch (IOException ex) {}
            }
        }
        
        private synchronized boolean shouldAuthenticateOverKrb() throws IOException {
            final UserGroupInformation loginUser = UserGroupInformation.getLoginUser();
            final UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
            final UserGroupInformation realUser = currentUser.getRealUser();
            return this.authMethod == SaslRpcServer.AuthMethod.KERBEROS && loginUser != null && loginUser.hasKerberosCredentials() && (loginUser.equals(currentUser) || loginUser.equals(realUser));
        }
        
        private synchronized SaslRpcServer.AuthMethod setupSaslConnection(final IpcStreams streams) throws IOException {
            this.saslRpcClient = new SaslRpcClient(this.remoteId.getTicket(), this.remoteId.getProtocol(), this.remoteId.getAddress(), this.remoteId.conf);
            return this.saslRpcClient.saslConnect(streams);
        }
        
        private synchronized boolean updateAddress() throws IOException {
            final InetSocketAddress currentAddr = NetUtils.createSocketAddrForHost(this.server.getHostName(), this.server.getPort());
            if (!this.server.equals(currentAddr)) {
                Client.LOG.warn("Address change detected. Old: " + this.server.toString() + " New: " + currentAddr.toString());
                this.server = currentAddr;
                return true;
            }
            return false;
        }
        
        private synchronized void setupConnection(final UserGroupInformation ticket) throws IOException {
            short ioFailures = 0;
            short timeoutFailures = 0;
            while (true) {
                try {
                    (this.socket = Client.this.socketFactory.createSocket()).setTcpNoDelay(this.tcpNoDelay);
                    this.socket.setKeepAlive(true);
                    if (this.tcpLowLatency) {
                        this.socket.setTrafficClass(20);
                        this.socket.setPerformancePreferences(1, 2, 0);
                    }
                    final InetSocketAddress bindAddr = null;
                    if (ticket != null && ticket.hasKerberosCredentials()) {
                        final KerberosInfo krbInfo = this.remoteId.getProtocol().getAnnotation(KerberosInfo.class);
                        if (krbInfo != null) {
                            final String principal = ticket.getUserName();
                            final String host = SecurityUtil.getHostFromPrincipal(principal);
                            InetAddress localAddr = NetUtils.getLocalInetAddress(host);
                            if (localAddr != null) {
                                this.socket.setReuseAddress(true);
                                localAddr = NetUtils.bindToLocalAddress(localAddr, Client.this.bindToWildCardAddress);
                                Client.LOG.debug("Binding {} to {}", principal, Client.this.bindToWildCardAddress ? "0.0.0.0" : localAddr);
                                this.socket.bind(new InetSocketAddress(localAddr, 0));
                            }
                        }
                    }
                    NetUtils.connect(this.socket, this.server, bindAddr, Client.this.connectionTimeout);
                    this.socket.setSoTimeout(this.soTimeout);
                }
                catch (ConnectTimeoutException toe) {
                    if (this.updateAddress()) {
                        ioFailures = (timeoutFailures = 0);
                    }
                    final short curRetries = timeoutFailures;
                    ++timeoutFailures;
                    this.handleConnectionTimeout(curRetries, this.maxRetriesOnSocketTimeouts, toe);
                    continue;
                }
                catch (IOException ie) {
                    if (this.updateAddress()) {
                        ioFailures = (timeoutFailures = 0);
                    }
                    final short curRetries2 = ioFailures;
                    ++ioFailures;
                    this.handleConnectionFailure(curRetries2, ie);
                    continue;
                }
                break;
            }
        }
        
        private synchronized void handleSaslConnectionFailure(final int currRetries, final int maxRetries, final Exception ex, final Random rand, final UserGroupInformation ugi) throws IOException, InterruptedException {
            ugi.doAs((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws IOException, InterruptedException {
                    final short MAX_BACKOFF = 5000;
                    Connection.this.closeConnection();
                    Connection.this.disposeSasl();
                    if (Connection.this.shouldAuthenticateOverKrb()) {
                        if (currRetries < maxRetries) {
                            if (Client.LOG.isDebugEnabled()) {
                                Client.LOG.debug("Exception encountered while connecting to the server : " + ex);
                            }
                            if (UserGroupInformation.isLoginKeytabBased()) {
                                UserGroupInformation.getLoginUser().reloginFromKeytab();
                            }
                            else if (UserGroupInformation.isLoginTicketBased()) {
                                UserGroupInformation.getLoginUser().reloginFromTicketCache();
                            }
                            Thread.sleep(rand.nextInt(5000) + 1);
                            return null;
                        }
                        final String msg = "Couldn't setup connection for " + UserGroupInformation.getLoginUser().getUserName() + " to " + Connection.this.remoteId;
                        Client.LOG.warn(msg, ex);
                        throw (IOException)new IOException(msg).initCause(ex);
                    }
                    else {
                        Client.LOG.warn("Exception encountered while connecting to the server : " + ex);
                        if (ex instanceof RemoteException) {
                            throw (RemoteException)ex;
                        }
                        throw new IOException(ex);
                    }
                }
            });
        }
        
        private synchronized void setupIOstreams(final AtomicBoolean fallbackToSimpleAuth) {
            if (this.socket != null || this.shouldCloseConnection.get()) {
                return;
            }
            UserGroupInformation ticket = this.remoteId.getTicket();
            if (ticket != null) {
                final UserGroupInformation realUser = ticket.getRealUser();
                if (realUser != null) {
                    ticket = realUser;
                }
            }
            try {
                this.connectingThread.set(Thread.currentThread());
                if (Client.LOG.isDebugEnabled()) {
                    Client.LOG.debug("Connecting to " + this.server);
                }
                Span span = Tracer.getCurrentSpan();
                if (span != null) {
                    span.addTimelineAnnotation("IPC client connecting to " + this.server);
                }
                short numRetries = 0;
                Random rand = null;
                while (true) {
                    this.setupConnection(ticket);
                    this.writeConnectionHeader(this.ipcStreams = new IpcStreams(this.socket, this.maxResponseLength));
                    if (this.authProtocol != Server.AuthProtocol.SASL) {
                        break;
                    }
                    try {
                        this.authMethod = ticket.doAs((PrivilegedExceptionAction<SaslRpcServer.AuthMethod>)new PrivilegedExceptionAction<SaslRpcServer.AuthMethod>() {
                            @Override
                            public SaslRpcServer.AuthMethod run() throws IOException, InterruptedException {
                                return Connection.this.setupSaslConnection(Connection.this.ipcStreams);
                            }
                        });
                    }
                    catch (IOException ex) {
                        if (this.saslRpcClient == null) {
                            throw ex;
                        }
                        this.authMethod = this.saslRpcClient.getAuthMethod();
                        if (rand == null) {
                            rand = new Random();
                        }
                        final short currRetries = numRetries;
                        ++numRetries;
                        this.handleSaslConnectionFailure(currRetries, this.maxRetriesOnSasl, ex, rand, ticket);
                        continue;
                    }
                    if (this.authMethod != SaslRpcServer.AuthMethod.SIMPLE) {
                        this.ipcStreams.setSaslClient(this.saslRpcClient);
                        this.remoteId.saslQop = (String)this.saslRpcClient.getNegotiatedProperty("javax.security.sasl.qop");
                        Client.LOG.debug("Negotiated QOP is :" + this.remoteId.saslQop);
                        if (fallbackToSimpleAuth != null) {
                            fallbackToSimpleAuth.set(false);
                            break;
                        }
                        break;
                    }
                    else {
                        if (!UserGroupInformation.isSecurityEnabled()) {
                            break;
                        }
                        if (!Client.this.fallbackAllowed) {
                            throw new IOException("Server asks us to fall back to SIMPLE auth, but this client is configured to only allow secure connections.");
                        }
                        if (fallbackToSimpleAuth != null) {
                            fallbackToSimpleAuth.set(true);
                            break;
                        }
                        break;
                    }
                }
                if (this.doPing) {
                    this.ipcStreams.setInputStream(new PingInputStream(this.ipcStreams.in));
                }
                this.writeConnectionContext(this.remoteId, this.authMethod);
                this.touch();
                span = Tracer.getCurrentSpan();
                if (span != null) {
                    span.addTimelineAnnotation("IPC client connected to " + this.server);
                }
                this.start();
            }
            catch (Throwable t) {
                if (t instanceof IOException) {
                    this.markClosed((IOException)t);
                }
                else {
                    this.markClosed(new IOException("Couldn't set up IO streams: " + t, t));
                }
                this.close();
            }
            finally {
                this.connectingThread.set(null);
            }
        }
        
        private void closeConnection() {
            if (this.socket == null) {
                return;
            }
            try {
                this.socket.close();
            }
            catch (IOException e) {
                Client.LOG.warn("Not able to close a socket", e);
            }
            this.socket = null;
        }
        
        private void handleConnectionTimeout(final int curRetries, final int maxRetries, final IOException ioe) throws IOException {
            this.closeConnection();
            if (curRetries >= maxRetries) {
                throw ioe;
            }
            Client.LOG.info("Retrying connect to server: " + this.server + ". Already tried " + curRetries + " time(s); maxRetries=" + maxRetries);
        }
        
        private void handleConnectionFailure(final int curRetries, final IOException ioe) throws IOException {
            this.closeConnection();
            RetryPolicy.RetryAction action;
            try {
                action = this.connectionRetryPolicy.shouldRetry(ioe, curRetries, 0, true);
            }
            catch (Exception e) {
                throw (e instanceof IOException) ? e : new IOException(e);
            }
            if (action.action == RetryPolicy.RetryAction.RetryDecision.FAIL) {
                if (action.reason != null && Client.LOG.isDebugEnabled()) {
                    Client.LOG.debug("Failed to connect to server: " + this.server + ": " + action.reason, ioe);
                }
                throw ioe;
            }
            if (Thread.currentThread().isInterrupted()) {
                Client.LOG.warn("Interrupted while trying for connection");
                throw ioe;
            }
            try {
                Thread.sleep(action.delayMillis);
            }
            catch (InterruptedException e2) {
                throw (IOException)new InterruptedIOException("Interrupted: action=" + action + ", retry policy=" + this.connectionRetryPolicy).initCause(e2);
            }
            Client.LOG.info("Retrying connect to server: " + this.server + ". Already tried " + curRetries + " time(s); retry policy is " + this.connectionRetryPolicy);
        }
        
        private void writeConnectionHeader(final IpcStreams streams) throws IOException {
            final DataOutputStream out = streams.out;
            synchronized (out) {
                out.write(RpcConstants.HEADER.array());
                out.write(9);
                out.write(this.serviceClass);
                out.write(this.authProtocol.callId);
            }
        }
        
        private void writeConnectionContext(final ConnectionId remoteId, final SaslRpcServer.AuthMethod authMethod) throws IOException {
            final IpcConnectionContextProtos.IpcConnectionContextProto message = ProtoUtil.makeIpcConnectionContext(RPC.getProtocolName(remoteId.getProtocol()), remoteId.getTicket(), authMethod);
            final RpcHeaderProtos.RpcRequestHeaderProto connectionContextHeader = ProtoUtil.makeRpcRequestHeader(RPC.RpcKind.RPC_PROTOCOL_BUFFER, RpcHeaderProtos.RpcRequestHeaderProto.OperationProto.RPC_FINAL_PACKET, -3, -1, Client.this.clientId);
            final ResponseBuffer buf = new ResponseBuffer();
            connectionContextHeader.writeDelimitedTo(buf);
            message.writeDelimitedTo(buf);
            synchronized (this.ipcStreams.out) {
                this.ipcStreams.sendRequest(buf.toByteArray());
            }
        }
        
        private synchronized boolean waitForWork() {
            if (this.calls.isEmpty() && !this.shouldCloseConnection.get() && Client.this.running.get()) {
                final long timeout = this.maxIdleTime - (Time.now() - this.lastActivity.get());
                if (timeout > 0L) {
                    try {
                        this.wait(timeout);
                    }
                    catch (InterruptedException ex) {}
                }
            }
            if (!this.calls.isEmpty() && !this.shouldCloseConnection.get() && Client.this.running.get()) {
                return true;
            }
            if (this.shouldCloseConnection.get()) {
                return false;
            }
            if (this.calls.isEmpty()) {
                this.markClosed(null);
                return false;
            }
            this.markClosed((IOException)new IOException().initCause(new InterruptedException()));
            return false;
        }
        
        public InetSocketAddress getRemoteAddress() {
            return this.server;
        }
        
        private synchronized void sendPing() throws IOException {
            final long curTime = Time.now();
            if (curTime - this.lastActivity.get() >= this.pingInterval) {
                this.lastActivity.set(curTime);
                synchronized (this.ipcStreams.out) {
                    this.ipcStreams.sendRequest(this.pingRequest);
                    this.ipcStreams.flush();
                }
            }
        }
        
        @Override
        public void run() {
            if (Client.LOG.isDebugEnabled()) {
                Client.LOG.debug(this.getName() + ": starting, having connections " + Client.this.connections.size());
            }
            try {
                while (this.waitForWork()) {
                    this.receiveRpcResponse();
                }
            }
            catch (Throwable t) {
                Client.LOG.warn("Unexpected error reading responses on connection " + this, t);
                this.markClosed(new IOException("Error reading responses", t));
            }
            this.close();
            if (Client.LOG.isDebugEnabled()) {
                Client.LOG.debug(this.getName() + ": stopped, remaining connections " + Client.this.connections.size());
            }
        }
        
        public void sendRpcRequest(final Call call) throws InterruptedException, IOException {
            if (this.shouldCloseConnection.get()) {
                return;
            }
            final RpcHeaderProtos.RpcRequestHeaderProto header = ProtoUtil.makeRpcRequestHeader(call.rpcKind, RpcHeaderProtos.RpcRequestHeaderProto.OperationProto.RPC_FINAL_PACKET, call.id, call.retry, Client.this.clientId);
            final ResponseBuffer buf = new ResponseBuffer();
            header.writeDelimitedTo(buf);
            RpcWritable.wrap(call.rpcRequest).writeTo(buf);
            synchronized (this.sendRpcRequestLock) {
                final Future<?> senderFuture = Client.this.sendParamsExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            synchronized (Connection.this.ipcStreams.out) {
                                if (Connection.this.shouldCloseConnection.get()) {
                                    return;
                                }
                                if (Client.LOG.isDebugEnabled()) {
                                    Client.LOG.debug(Connection.this.getName() + " sending #" + call.id + " " + call.rpcRequest);
                                }
                                Connection.this.ipcStreams.sendRequest(buf.toByteArray());
                                Connection.this.ipcStreams.flush();
                            }
                        }
                        catch (IOException e) {
                            Connection.this.markClosed(e);
                        }
                        finally {
                            IOUtils.closeStream(buf);
                        }
                    }
                });
                try {
                    senderFuture.get();
                }
                catch (ExecutionException e) {
                    final Throwable cause = e.getCause();
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException)cause;
                    }
                    throw new RuntimeException("unexpected checked exception", cause);
                }
            }
        }
        
        private void receiveRpcResponse() {
            if (this.shouldCloseConnection.get()) {
                return;
            }
            this.touch();
            try {
                final ByteBuffer bb = this.ipcStreams.readResponse();
                final RpcWritable.Buffer packet = RpcWritable.Buffer.wrap(bb);
                final RpcHeaderProtos.RpcResponseHeaderProto header = packet.getValue(RpcHeaderProtos.RpcResponseHeaderProto.getDefaultInstance());
                Client.this.checkResponse(header);
                final int callId = header.getCallId();
                if (Client.LOG.isDebugEnabled()) {
                    Client.LOG.debug(this.getName() + " got value #" + callId);
                }
                final RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto status = header.getStatus();
                if (status == RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.SUCCESS) {
                    final Writable value = packet.newInstance(Client.this.valueClass, Client.this.conf);
                    final Call call = this.calls.remove(callId);
                    call.setRpcResponse(value);
                }
                if (packet.remaining() > 0) {
                    throw new RpcClientException("RPC response length mismatch");
                }
                if (status != RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.SUCCESS) {
                    final String exceptionClassName = header.hasExceptionClassName() ? header.getExceptionClassName() : "ServerDidNotSetExceptionClassName";
                    final String errorMsg = header.hasErrorMsg() ? header.getErrorMsg() : "ServerDidNotSetErrorMsg";
                    final RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto erCode = header.hasErrorDetail() ? header.getErrorDetail() : null;
                    if (erCode == null) {
                        Client.LOG.warn("Detailed error code not set by server on rpc error");
                    }
                    final RemoteException re = new RemoteException(exceptionClassName, errorMsg, erCode);
                    if (status == RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.ERROR) {
                        final Call call2 = this.calls.remove(callId);
                        call2.setException(re);
                    }
                    else if (status == RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.FATAL) {
                        this.markClosed(re);
                    }
                }
            }
            catch (IOException e) {
                this.markClosed(e);
            }
        }
        
        private synchronized void markClosed(final IOException e) {
            if (this.shouldCloseConnection.compareAndSet(false, true)) {
                this.closeException = e;
                this.notifyAll();
            }
        }
        
        private void interruptConnectingThread() {
            final Thread connThread = this.connectingThread.get();
            if (connThread != null) {
                connThread.interrupt();
            }
        }
        
        private synchronized void close() {
            if (!this.shouldCloseConnection.get()) {
                Client.LOG.error("The connection is not in the closed state");
                return;
            }
            Client.this.connections.remove(this.remoteId, this);
            IOUtils.closeStream(this.ipcStreams);
            this.disposeSasl();
            if (this.closeException == null) {
                if (!this.calls.isEmpty()) {
                    Client.LOG.warn("A connection is closed for no cause and calls are not empty");
                    this.closeException = new IOException("Unexpected closed connection");
                    this.cleanupCalls();
                }
            }
            else {
                if (Client.LOG.isDebugEnabled()) {
                    Client.LOG.debug("closing ipc connection to " + this.server + ": " + this.closeException.getMessage(), this.closeException);
                }
                this.cleanupCalls();
            }
            this.closeConnection();
            if (Client.LOG.isDebugEnabled()) {
                Client.LOG.debug(this.getName() + ": closed");
            }
        }
        
        private void cleanupCalls() {
            final Iterator<Map.Entry<Integer, Call>> itor = this.calls.entrySet().iterator();
            while (itor.hasNext()) {
                final Call c = itor.next().getValue();
                itor.remove();
                c.setException(this.closeException);
            }
        }
        
        private class PingInputStream extends FilterInputStream
        {
            protected PingInputStream(final InputStream in) {
                super(in);
            }
            
            private void handleTimeout(final SocketTimeoutException e, final int waiting) throws IOException {
                if (Connection.this.shouldCloseConnection.get() || !Client.this.running.get() || (0 < Connection.this.rpcTimeout && Connection.this.rpcTimeout <= waiting)) {
                    throw e;
                }
                Connection.this.sendPing();
            }
            
            @Override
            public int read() throws IOException {
                int waiting = 0;
                try {
                    return super.read();
                }
                catch (SocketTimeoutException e) {
                    waiting += Connection.this.soTimeout;
                    this.handleTimeout(e, waiting);
                    return super.read();
                }
            }
            
            @Override
            public int read(final byte[] buf, final int off, final int len) throws IOException {
                int waiting = 0;
                try {
                    return super.read(buf, off, len);
                }
                catch (SocketTimeoutException e) {
                    waiting += Connection.this.soTimeout;
                    this.handleTimeout(e, waiting);
                    return super.read(buf, off, len);
                }
            }
        }
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
    @InterfaceStability.Evolving
    public static class ConnectionId
    {
        InetSocketAddress address;
        UserGroupInformation ticket;
        final Class<?> protocol;
        private static final int PRIME = 16777619;
        private final int rpcTimeout;
        private final int maxIdleTime;
        private final RetryPolicy connectionRetryPolicy;
        private final int maxRetriesOnSasl;
        private final int maxRetriesOnSocketTimeouts;
        private final boolean tcpNoDelay;
        private final boolean tcpLowLatency;
        private final boolean doPing;
        private final int pingInterval;
        private String saslQop;
        private final Configuration conf;
        
        ConnectionId(final InetSocketAddress address, final Class<?> protocol, final UserGroupInformation ticket, final int rpcTimeout, final RetryPolicy connectionRetryPolicy, final Configuration conf) {
            this.protocol = protocol;
            this.address = address;
            this.ticket = ticket;
            this.rpcTimeout = rpcTimeout;
            this.connectionRetryPolicy = connectionRetryPolicy;
            this.maxIdleTime = conf.getInt("ipc.client.connection.maxidletime", 10000);
            this.maxRetriesOnSasl = conf.getInt("ipc.client.connect.max.retries.on.sasl", 5);
            this.maxRetriesOnSocketTimeouts = conf.getInt("ipc.client.connect.max.retries.on.timeouts", 45);
            this.tcpNoDelay = conf.getBoolean("ipc.client.tcpnodelay", true);
            this.tcpLowLatency = conf.getBoolean("ipc.client.low-latency", false);
            this.doPing = conf.getBoolean("ipc.client.ping", true);
            this.pingInterval = (this.doPing ? Client.getPingInterval(conf) : 0);
            this.conf = conf;
        }
        
        InetSocketAddress getAddress() {
            return this.address;
        }
        
        Class<?> getProtocol() {
            return this.protocol;
        }
        
        UserGroupInformation getTicket() {
            return this.ticket;
        }
        
        private int getRpcTimeout() {
            return this.rpcTimeout;
        }
        
        int getMaxIdleTime() {
            return this.maxIdleTime;
        }
        
        public int getMaxRetriesOnSasl() {
            return this.maxRetriesOnSasl;
        }
        
        public int getMaxRetriesOnSocketTimeouts() {
            return this.maxRetriesOnSocketTimeouts;
        }
        
        boolean getTcpNoDelay() {
            return this.tcpNoDelay;
        }
        
        boolean getTcpLowLatency() {
            return this.tcpLowLatency;
        }
        
        boolean getDoPing() {
            return this.doPing;
        }
        
        int getPingInterval() {
            return this.pingInterval;
        }
        
        @VisibleForTesting
        String getSaslQop() {
            return this.saslQop;
        }
        
        static ConnectionId getConnectionId(final InetSocketAddress addr, final Class<?> protocol, final UserGroupInformation ticket, final int rpcTimeout, RetryPolicy connectionRetryPolicy, final Configuration conf) throws IOException {
            if (connectionRetryPolicy == null) {
                final int max = conf.getInt("ipc.client.connect.max.retries", 10);
                final int retryInterval = conf.getInt("ipc.client.connect.retry.interval", 1000);
                connectionRetryPolicy = RetryPolicies.retryUpToMaximumCountWithFixedSleep(max, retryInterval, TimeUnit.MILLISECONDS);
            }
            return new ConnectionId(addr, protocol, ticket, rpcTimeout, connectionRetryPolicy, conf);
        }
        
        static boolean isEqual(final Object a, final Object b) {
            return (a == null) ? (b == null) : a.equals(b);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof ConnectionId) {
                final ConnectionId that = (ConnectionId)obj;
                return isEqual(this.address, that.address) && this.doPing == that.doPing && this.maxIdleTime == that.maxIdleTime && isEqual(this.connectionRetryPolicy, that.connectionRetryPolicy) && this.pingInterval == that.pingInterval && isEqual(this.protocol, that.protocol) && this.rpcTimeout == that.rpcTimeout && this.tcpNoDelay == that.tcpNoDelay && isEqual(this.ticket, that.ticket);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = this.connectionRetryPolicy.hashCode();
            result = 16777619 * result + ((this.address == null) ? 0 : this.address.hashCode());
            result = 16777619 * result + (this.doPing ? 1231 : 1237);
            result = 16777619 * result + this.maxIdleTime;
            result = 16777619 * result + this.pingInterval;
            result = 16777619 * result + ((this.protocol == null) ? 0 : this.protocol.hashCode());
            result = 16777619 * result + this.rpcTimeout;
            result = 16777619 * result + (this.tcpNoDelay ? 1231 : 1237);
            result = 16777619 * result + ((this.ticket == null) ? 0 : this.ticket.hashCode());
            return result;
        }
        
        @Override
        public String toString() {
            return this.address.toString();
        }
    }
    
    @InterfaceAudience.Private
    public static class IpcStreams implements Closeable, Flushable
    {
        private DataInputStream in;
        public DataOutputStream out;
        private int maxResponseLength;
        private boolean firstResponse;
        
        IpcStreams(final Socket socket, final int maxResponseLength) throws IOException {
            this.firstResponse = true;
            this.maxResponseLength = maxResponseLength;
            this.setInputStream(new BufferedInputStream(NetUtils.getInputStream(socket)));
            this.setOutputStream(new BufferedOutputStream(NetUtils.getOutputStream(socket)));
        }
        
        void setSaslClient(final SaslRpcClient client) throws IOException {
            this.setInputStream(new BufferedInputStream(client.getInputStream(this.in)));
            this.setOutputStream(client.getOutputStream(this.out));
        }
        
        private void setInputStream(final InputStream is) {
            this.in = (DataInputStream)((is instanceof DataInputStream) ? is : new DataInputStream(is));
        }
        
        private void setOutputStream(final OutputStream os) {
            this.out = (DataOutputStream)((os instanceof DataOutputStream) ? os : new DataOutputStream(os));
        }
        
        public ByteBuffer readResponse() throws IOException {
            final int length = this.in.readInt();
            if (this.firstResponse) {
                this.firstResponse = false;
                if (length == -1) {
                    this.in.readInt();
                    throw new RemoteException(WritableUtils.readString(this.in), WritableUtils.readString(this.in));
                }
            }
            if (length <= 0) {
                throw new RpcException("RPC response has invalid length");
            }
            if (this.maxResponseLength > 0 && length > this.maxResponseLength) {
                throw new RpcException("RPC response exceeds maximum data length");
            }
            final ByteBuffer bb = ByteBuffer.allocate(length);
            this.in.readFully(bb.array());
            return bb;
        }
        
        public void sendRequest(final byte[] buf) throws IOException {
            this.out.write(buf);
        }
        
        @Override
        public void flush() throws IOException {
            this.out.flush();
        }
        
        @Override
        public void close() {
            IOUtils.closeStream(this.out);
            IOUtils.closeStream(this.in);
        }
    }
}
