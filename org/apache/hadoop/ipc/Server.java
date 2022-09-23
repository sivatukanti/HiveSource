// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.security.authorize.ProxyUsers;
import org.apache.htrace.core.SpanId;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.io.ByteArrayInputStream;
import javax.security.sasl.SaslException;
import org.apache.hadoop.security.AccessControlException;
import javax.security.sasl.SaslServer;
import org.apache.hadoop.ipc.protobuf.IpcConnectionContextProtos;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.util.LinkedList;
import org.apache.hadoop.util.ExitUtil;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SocketChannel;
import org.apache.commons.logging.Log;
import org.apache.hadoop.io.IOUtils;
import java.io.Closeable;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.lang.reflect.UndeclaredThrowableException;
import org.apache.hadoop.util.Time;
import org.apache.htrace.core.TraceScope;
import java.util.concurrent.atomic.AtomicInteger;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.security.authorize.AuthorizationException;
import java.io.DataOutput;
import org.apache.hadoop.io.WritableUtils;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;
import org.apache.hadoop.util.StringUtils;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import org.apache.hadoop.security.SecurityUtil;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.hadoop.security.authorize.PolicyProvider;
import java.util.Arrays;
import java.util.Iterator;
import java.net.SocketException;
import org.apache.hadoop.net.NetUtils;
import java.net.BindException;
import java.net.SocketAddress;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import org.apache.hadoop.security.UserGroupInformation;
import java.net.InetAddress;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.util.ProtoUtil;
import org.apache.hadoop.security.authorize.ServiceAuthorizationManager;
import org.apache.hadoop.security.SaslPropertiesResolver;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.metrics.RpcDetailedMetrics;
import org.apache.hadoop.ipc.metrics.RpcMetrics;
import org.apache.hadoop.io.Writable;
import org.slf4j.Logger;
import java.util.Map;
import java.nio.ByteBuffer;
import org.apache.htrace.core.Tracer;
import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos;
import org.apache.hadoop.security.SaslRpcServer;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class Server
{
    private final boolean authorize;
    private List<SaslRpcServer.AuthMethod> enabledAuthMethods;
    private RpcHeaderProtos.RpcSaslProto negotiateResponse;
    private ExceptionsHandler exceptionsHandler;
    private Tracer tracer;
    private final String serverName;
    private static final ByteBuffer HTTP_GET_BYTES;
    static final String RECEIVED_HTTP_REQ_RESPONSE = "HTTP/1.1 404 Not Found\r\nContent-type: text/plain\r\n\r\nIt looks like you are making an HTTP request to a Hadoop IPC port. This is not the correct port for the web interface on this daemon.\r\n";
    static int INITIAL_RESP_BUF_SIZE;
    static Map<RPC.RpcKind, RpcKindMapValue> rpcKindMap;
    public static final Logger LOG;
    public static final Logger AUDITLOG;
    private static final String AUTH_FAILED_FOR = "Auth failed for ";
    private static final String AUTH_SUCCESSFUL_FOR = "Auth successful for ";
    private static final ThreadLocal<Server> SERVER;
    private static final Map<String, Class<?>> PROTOCOL_CACHE;
    private static final ThreadLocal<Call> CurCall;
    private String bindAddress;
    private int port;
    private int handlerCount;
    private int readThreads;
    private int readerPendingConnectionQueue;
    private Class<? extends Writable> rpcRequestClass;
    protected final RpcMetrics rpcMetrics;
    protected final RpcDetailedMetrics rpcDetailedMetrics;
    private Configuration conf;
    private String portRangeConfig;
    private SecretManager<TokenIdentifier> secretManager;
    private SaslPropertiesResolver saslPropsResolver;
    private ServiceAuthorizationManager serviceAuthorizationManager;
    private int maxQueueSize;
    private final int maxRespSize;
    private final ThreadLocal<ResponseBuffer> responseBuffer;
    private int socketSendBufferSize;
    private final int maxDataLength;
    private final boolean tcpNoDelay;
    private volatile boolean running;
    private CallQueueManager<Call> callQueue;
    private ConnectionManager connectionManager;
    private Listener listener;
    private Responder responder;
    private Handler[] handlers;
    private boolean logSlowRPC;
    private static int NIO_BUFFER_LIMIT;
    
    public void addTerseExceptions(final Class<?>... exceptionClass) {
        this.exceptionsHandler.addTerseLoggingExceptions(exceptionClass);
    }
    
    public void addSuppressedLoggingExceptions(final Class<?>... exceptionClass) {
        this.exceptionsHandler.addSuppressedLoggingExceptions(exceptionClass);
    }
    
    public static void registerProtocolEngine(final RPC.RpcKind rpcKind, final Class<? extends Writable> rpcRequestWrapperClass, final RPC.RpcInvoker rpcInvoker) {
        final RpcKindMapValue old = Server.rpcKindMap.put(rpcKind, new RpcKindMapValue(rpcRequestWrapperClass, rpcInvoker));
        if (old != null) {
            Server.rpcKindMap.put(rpcKind, old);
            throw new IllegalArgumentException("ReRegistration of rpcKind: " + rpcKind);
        }
        if (Server.LOG.isDebugEnabled()) {
            Server.LOG.debug("rpcKind=" + rpcKind + ", rpcRequestWrapperClass=" + rpcRequestWrapperClass + ", rpcInvoker=" + rpcInvoker);
        }
    }
    
    public Class<? extends Writable> getRpcRequestWrapper(final RpcHeaderProtos.RpcKindProto rpcKind) {
        if (this.rpcRequestClass != null) {
            return this.rpcRequestClass;
        }
        final RpcKindMapValue val = Server.rpcKindMap.get(ProtoUtil.convert(rpcKind));
        return (val == null) ? null : val.rpcRequestWrapperClass;
    }
    
    public static RPC.RpcInvoker getRpcInvoker(final RPC.RpcKind rpcKind) {
        final RpcKindMapValue val = Server.rpcKindMap.get(rpcKind);
        return (val == null) ? null : val.rpcInvoker;
    }
    
    static Class<?> getProtocolClass(final String protocolName, final Configuration conf) throws ClassNotFoundException {
        Class<?> protocol = Server.PROTOCOL_CACHE.get(protocolName);
        if (protocol == null) {
            protocol = conf.getClassByName(protocolName);
            Server.PROTOCOL_CACHE.put(protocolName, protocol);
        }
        return protocol;
    }
    
    public static Server get() {
        return Server.SERVER.get();
    }
    
    @VisibleForTesting
    public static ThreadLocal<Call> getCurCall() {
        return Server.CurCall;
    }
    
    public static int getCallId() {
        final Call call = Server.CurCall.get();
        return (call != null) ? call.callId : -2;
    }
    
    public static int getCallRetryCount() {
        final Call call = Server.CurCall.get();
        return (call != null) ? call.retryCount : -1;
    }
    
    public static InetAddress getRemoteIp() {
        final Call call = Server.CurCall.get();
        return (call != null) ? call.getHostInetAddress() : null;
    }
    
    public static byte[] getClientId() {
        final Call call = Server.CurCall.get();
        return (call != null) ? call.clientId : RpcConstants.DUMMY_CLIENT_ID;
    }
    
    public static String getRemoteAddress() {
        final InetAddress addr = getRemoteIp();
        return (addr == null) ? null : addr.getHostAddress();
    }
    
    public static UserGroupInformation getRemoteUser() {
        final Call call = Server.CurCall.get();
        return (call != null) ? call.getRemoteUser() : null;
    }
    
    public static String getProtocol() {
        final Call call = Server.CurCall.get();
        return (call != null) ? call.getProtocol() : null;
    }
    
    public static boolean isRpcInvocation() {
        return Server.CurCall.get() != null;
    }
    
    public static int getPriorityLevel() {
        final Call call = Server.CurCall.get();
        return (call != null) ? call.getPriorityLevel() : 0;
    }
    
    protected boolean isLogSlowRPC() {
        return this.logSlowRPC;
    }
    
    @VisibleForTesting
    protected void setLogSlowRPC(final boolean logSlowRPCFlag) {
        this.logSlowRPC = logSlowRPCFlag;
    }
    
    void logSlowRpcCalls(final String methodName, final int processingTime) {
        final int deviation = 3;
        final int minSampleSize = 1024;
        final double threeSigma = this.rpcMetrics.getProcessingMean() + this.rpcMetrics.getProcessingStdDev() * 3.0;
        if (this.rpcMetrics.getProcessingSampleCount() > 1024L && processingTime > threeSigma) {
            if (Server.LOG.isWarnEnabled()) {
                final String client = Server.CurCall.get().toString();
                Server.LOG.warn("Slow RPC : " + methodName + " took " + processingTime + " milliseconds to process from client " + client);
            }
            this.rpcMetrics.incrSlowRpc();
        }
    }
    
    void updateMetrics(final String name, final int queueTime, final int processingTime, final boolean deferredCall) {
        this.rpcMetrics.addRpcQueueTime(queueTime);
        if (!deferredCall) {
            this.rpcMetrics.addRpcProcessingTime(processingTime);
            this.rpcDetailedMetrics.addProcessingTime(name, processingTime);
            this.callQueue.addResponseTime(name, getPriorityLevel(), queueTime, processingTime);
            if (this.isLogSlowRPC()) {
                this.logSlowRpcCalls(name, processingTime);
            }
        }
    }
    
    void updateDeferredMetrics(final String name, final long processingTime) {
        this.rpcMetrics.addDeferredRpcProcessingTime(processingTime);
        this.rpcDetailedMetrics.addDeferredProcessingTime(name, processingTime);
    }
    
    public static void bind(final ServerSocket socket, final InetSocketAddress address, final int backlog) throws IOException {
        bind(socket, address, backlog, null, null);
    }
    
    public static void bind(final ServerSocket socket, final InetSocketAddress address, final int backlog, final Configuration conf, final String rangeConf) throws IOException {
        try {
            Configuration.IntegerRanges range = null;
            if (rangeConf != null) {
                range = conf.getRange(rangeConf, "");
            }
            if (range == null || range.isEmpty() || address.getPort() != 0) {
                socket.bind(address, backlog);
            }
            else {
                for (final Integer port : range) {
                    if (socket.isBound()) {
                        break;
                    }
                    try {
                        final InetSocketAddress temp = new InetSocketAddress(address.getAddress(), port);
                        socket.bind(temp, backlog);
                    }
                    catch (BindException ex) {}
                }
                if (!socket.isBound()) {
                    throw new BindException("Could not find a free port in " + range);
                }
            }
        }
        catch (SocketException e) {
            throw NetUtils.wrapException(null, 0, address.getHostName(), address.getPort(), e);
        }
    }
    
    @VisibleForTesting
    public RpcMetrics getRpcMetrics() {
        return this.rpcMetrics;
    }
    
    @VisibleForTesting
    public RpcDetailedMetrics getRpcDetailedMetrics() {
        return this.rpcDetailedMetrics;
    }
    
    @VisibleForTesting
    Iterable<? extends Thread> getHandlers() {
        return Arrays.asList(this.handlers);
    }
    
    @VisibleForTesting
    Connection[] getConnections() {
        return this.connectionManager.toArray();
    }
    
    public void refreshServiceAcl(final Configuration conf, final PolicyProvider provider) {
        this.serviceAuthorizationManager.refresh(conf, provider);
    }
    
    @InterfaceAudience.Private
    public void refreshServiceAclWithLoadedConfiguration(final Configuration conf, final PolicyProvider provider) {
        this.serviceAuthorizationManager.refreshWithLoadedConfiguration(conf, provider);
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
    public ServiceAuthorizationManager getServiceAuthorizationManager() {
        return this.serviceAuthorizationManager;
    }
    
    private String getQueueClassPrefix() {
        return "ipc." + this.port;
    }
    
    static Class<? extends BlockingQueue<Call>> getQueueClass(final String prefix, final Configuration conf) {
        final String name = prefix + "." + "callqueue.impl";
        final Class<?> queueClass = conf.getClass(name, LinkedBlockingQueue.class);
        return CallQueueManager.convertQueueClass(queueClass, Call.class);
    }
    
    static Class<? extends RpcScheduler> getSchedulerClass(final String prefix, final Configuration conf) {
        final String schedulerKeyname = prefix + "." + "scheduler.impl";
        Class<?> schedulerClass = conf.getClass(schedulerKeyname, null);
        if (schedulerClass == null) {
            final String queueKeyName = prefix + "." + "callqueue.impl";
            final Class<?> queueClass = conf.getClass(queueKeyName, null);
            if (queueClass != null && queueClass.getCanonicalName().equals(FairCallQueue.class.getCanonicalName())) {
                conf.setClass(schedulerKeyname, DecayRpcScheduler.class, RpcScheduler.class);
            }
        }
        schedulerClass = conf.getClass(schedulerKeyname, DefaultRpcScheduler.class);
        return CallQueueManager.convertSchedulerClass(schedulerClass);
    }
    
    public synchronized void refreshCallQueue(final Configuration conf) {
        final String prefix = this.getQueueClassPrefix();
        this.maxQueueSize = this.handlerCount * conf.getInt("ipc.server.handler.queue.size", 100);
        this.callQueue.swapQueue(getSchedulerClass(prefix, conf), getQueueClass(prefix, conf), this.maxQueueSize, prefix, conf);
    }
    
    static boolean getClientBackoffEnable(final String prefix, final Configuration conf) {
        final String name = prefix + "." + "backoff.enable";
        return conf.getBoolean(name, false);
    }
    
    public void queueCall(final Call call) throws IOException, InterruptedException {
        try {
            this.internalQueueCall(call);
        }
        catch (RpcServerException rse) {
            throw (IOException)rse.getCause();
        }
    }
    
    private void internalQueueCall(final Call call) throws IOException, InterruptedException {
        try {
            this.callQueue.put(call);
        }
        catch (CallQueueManager.CallQueueOverflowException cqe) {
            this.rpcMetrics.incrClientBackoff();
            throw cqe.getCause();
        }
    }
    
    @VisibleForTesting
    void logException(final Logger logger, final Throwable e, final Call call) {
        if (this.exceptionsHandler.isSuppressedLog(e.getClass())) {
            return;
        }
        final String logMsg = Thread.currentThread().getName() + ", call " + call;
        if (this.exceptionsHandler.isTerseLog(e.getClass())) {
            logger.info(logMsg + ": " + e);
        }
        else if (e instanceof RuntimeException || e instanceof Error) {
            logger.warn(logMsg, e);
        }
        else {
            logger.info(logMsg, e);
        }
    }
    
    protected Server(final String bindAddress, final int port, final Class<? extends Writable> paramClass, final int handlerCount, final Configuration conf) throws IOException {
        this(bindAddress, port, paramClass, handlerCount, -1, -1, conf, Integer.toString(port), null, null);
    }
    
    protected Server(final String bindAddress, final int port, final Class<? extends Writable> rpcRequestClass, final int handlerCount, final int numReaders, final int queueSizePerHandler, final Configuration conf, final String serverName, final SecretManager<? extends TokenIdentifier> secretManager) throws IOException {
        this(bindAddress, port, rpcRequestClass, handlerCount, numReaders, queueSizePerHandler, conf, serverName, secretManager, null);
    }
    
    protected Server(final String bindAddress, final int port, final Class<? extends Writable> rpcRequestClass, final int handlerCount, final int numReaders, final int queueSizePerHandler, final Configuration conf, final String serverName, final SecretManager<? extends TokenIdentifier> secretManager, final String portRangeConfig) throws IOException {
        this.exceptionsHandler = new ExceptionsHandler();
        this.portRangeConfig = null;
        this.serviceAuthorizationManager = new ServiceAuthorizationManager();
        this.responseBuffer = new ThreadLocal<ResponseBuffer>() {
            @Override
            protected ResponseBuffer initialValue() {
                return new ResponseBuffer(Server.INITIAL_RESP_BUF_SIZE);
            }
        };
        this.running = true;
        this.listener = null;
        this.responder = null;
        this.handlers = null;
        this.logSlowRPC = false;
        this.bindAddress = bindAddress;
        this.conf = conf;
        this.portRangeConfig = portRangeConfig;
        this.port = port;
        this.rpcRequestClass = rpcRequestClass;
        this.handlerCount = handlerCount;
        this.socketSendBufferSize = 0;
        this.serverName = serverName;
        this.maxDataLength = conf.getInt("ipc.maximum.data.length", 67108864);
        if (queueSizePerHandler != -1) {
            this.maxQueueSize = handlerCount * queueSizePerHandler;
        }
        else {
            this.maxQueueSize = handlerCount * conf.getInt("ipc.server.handler.queue.size", 100);
        }
        this.maxRespSize = conf.getInt("ipc.server.max.response.size", 1048576);
        if (numReaders != -1) {
            this.readThreads = numReaders;
        }
        else {
            this.readThreads = conf.getInt("ipc.server.read.threadpool.size", 1);
        }
        this.readerPendingConnectionQueue = conf.getInt("ipc.server.read.connection-queue.size", 100);
        final String prefix = this.getQueueClassPrefix();
        this.callQueue = new CallQueueManager<Call>(getQueueClass(prefix, conf), getSchedulerClass(prefix, conf), getClientBackoffEnable(prefix, conf), this.maxQueueSize, prefix, conf);
        this.secretManager = (SecretManager<TokenIdentifier>)secretManager;
        this.authorize = conf.getBoolean("hadoop.security.authorization", false);
        this.enabledAuthMethods = this.getAuthMethods(secretManager, conf);
        this.negotiateResponse = this.buildNegotiateResponse(this.enabledAuthMethods);
        this.listener = new Listener();
        this.port = this.listener.getAddress().getPort();
        this.connectionManager = new ConnectionManager();
        this.rpcMetrics = RpcMetrics.create(this, conf);
        this.rpcDetailedMetrics = RpcDetailedMetrics.create(this.port);
        this.tcpNoDelay = conf.getBoolean("ipc.server.tcpnodelay", true);
        this.setLogSlowRPC(conf.getBoolean("ipc.server.log.slow.rpc", false));
        this.responder = new Responder();
        if (secretManager != null || UserGroupInformation.isSecurityEnabled()) {
            SaslRpcServer.init(conf);
            this.saslPropsResolver = SaslPropertiesResolver.getInstance(conf);
        }
        this.exceptionsHandler.addTerseLoggingExceptions(StandbyException.class);
    }
    
    private RpcHeaderProtos.RpcSaslProto buildNegotiateResponse(final List<SaslRpcServer.AuthMethod> authMethods) throws IOException {
        final RpcHeaderProtos.RpcSaslProto.Builder negotiateBuilder = RpcHeaderProtos.RpcSaslProto.newBuilder();
        if (authMethods.contains(SaslRpcServer.AuthMethod.SIMPLE) && authMethods.size() == 1) {
            negotiateBuilder.setState(RpcHeaderProtos.RpcSaslProto.SaslState.SUCCESS);
        }
        else {
            negotiateBuilder.setState(RpcHeaderProtos.RpcSaslProto.SaslState.NEGOTIATE);
            for (final SaslRpcServer.AuthMethod authMethod : authMethods) {
                final SaslRpcServer saslRpcServer = new SaslRpcServer(authMethod);
                final RpcHeaderProtos.RpcSaslProto.SaslAuth.Builder builder = negotiateBuilder.addAuthsBuilder().setMethod(authMethod.toString()).setMechanism(saslRpcServer.mechanism);
                if (saslRpcServer.protocol != null) {
                    builder.setProtocol(saslRpcServer.protocol);
                }
                if (saslRpcServer.serverId != null) {
                    builder.setServerId(saslRpcServer.serverId);
                }
            }
        }
        return negotiateBuilder.build();
    }
    
    private List<SaslRpcServer.AuthMethod> getAuthMethods(final SecretManager<?> secretManager, final Configuration conf) {
        final UserGroupInformation.AuthenticationMethod confAuthenticationMethod = SecurityUtil.getAuthenticationMethod(conf);
        final List<SaslRpcServer.AuthMethod> authMethods = new ArrayList<SaslRpcServer.AuthMethod>();
        if (confAuthenticationMethod == UserGroupInformation.AuthenticationMethod.TOKEN) {
            if (secretManager == null) {
                throw new IllegalArgumentException(UserGroupInformation.AuthenticationMethod.TOKEN + " authentication requires a secret manager");
            }
        }
        else if (secretManager != null) {
            Server.LOG.debug(UserGroupInformation.AuthenticationMethod.TOKEN + " authentication enabled for secret manager");
            authMethods.add(UserGroupInformation.AuthenticationMethod.TOKEN.getAuthMethod());
        }
        authMethods.add(confAuthenticationMethod.getAuthMethod());
        Server.LOG.debug("Server accepts auth methods:" + authMethods);
        return authMethods;
    }
    
    private void closeConnection(final Connection connection) {
        this.connectionManager.close(connection);
    }
    
    private void setupResponse(final RpcCall call, final RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto status, final RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto erCode, final Writable rv, final String errorClass, final String error) throws IOException {
        if (status == RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.FATAL) {
            call.connection.setShouldClose();
        }
        final RpcHeaderProtos.RpcResponseHeaderProto.Builder headerBuilder = RpcHeaderProtos.RpcResponseHeaderProto.newBuilder();
        headerBuilder.setClientId(ByteString.copyFrom(call.clientId));
        headerBuilder.setCallId(call.callId);
        headerBuilder.setRetryCount(call.retryCount);
        headerBuilder.setStatus(status);
        headerBuilder.setServerIpcVersionNum(9);
        if (status == RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.SUCCESS) {
            final RpcHeaderProtos.RpcResponseHeaderProto header = headerBuilder.build();
            try {
                this.setupResponse(call, header, rv);
            }
            catch (Throwable t) {
                Server.LOG.warn("Error serializing call response for call " + call, t);
                this.setupResponse(call, RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.ERROR, RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.ERROR_SERIALIZING_RESPONSE, null, t.getClass().getName(), StringUtils.stringifyException(t));
            }
        }
        else {
            headerBuilder.setExceptionClassName(errorClass);
            headerBuilder.setErrorMsg(error);
            headerBuilder.setErrorDetail(erCode);
            this.setupResponse(call, headerBuilder.build(), null);
        }
    }
    
    private void setupResponse(final RpcCall call, final RpcHeaderProtos.RpcResponseHeaderProto header, final Writable rv) throws IOException {
        byte[] response;
        if (rv == null || rv instanceof RpcWritable.ProtobufWrapper) {
            response = this.setupResponseForProtobuf(header, rv);
        }
        else {
            response = this.setupResponseForWritable(header, rv);
        }
        if (response.length > this.maxRespSize) {
            Server.LOG.warn("Large response size " + response.length + " for call " + call.toString());
        }
        call.setResponse(ByteBuffer.wrap(response));
    }
    
    private byte[] setupResponseForWritable(final RpcHeaderProtos.RpcResponseHeaderProto header, final Writable rv) throws IOException {
        final ResponseBuffer buf = this.responseBuffer.get().reset();
        try {
            RpcWritable.wrap(header).writeTo(buf);
            if (rv != null) {
                RpcWritable.wrap(rv).writeTo(buf);
            }
            return buf.toByteArray();
        }
        finally {
            if (buf.capacity() > this.maxRespSize) {
                buf.setCapacity(Server.INITIAL_RESP_BUF_SIZE);
            }
        }
    }
    
    private byte[] setupResponseForProtobuf(final RpcHeaderProtos.RpcResponseHeaderProto header, final Writable rv) throws IOException {
        final Message payload = (rv != null) ? ((RpcWritable.ProtobufWrapper)rv).getMessage() : null;
        int length = getDelimitedLength(header);
        if (payload != null) {
            length += getDelimitedLength(payload);
        }
        final byte[] buf = new byte[length + 4];
        final CodedOutputStream cos = CodedOutputStream.newInstance(buf);
        cos.writeRawByte((byte)(length >>> 24 & 0xFF));
        cos.writeRawByte((byte)(length >>> 16 & 0xFF));
        cos.writeRawByte((byte)(length >>> 8 & 0xFF));
        cos.writeRawByte((byte)(length >>> 0 & 0xFF));
        cos.writeRawVarint32(header.getSerializedSize());
        header.writeTo(cos);
        if (payload != null) {
            cos.writeRawVarint32(payload.getSerializedSize());
            payload.writeTo(cos);
        }
        return buf;
    }
    
    private static int getDelimitedLength(final Message message) {
        final int length = message.getSerializedSize();
        return length + CodedOutputStream.computeRawVarint32Size(length);
    }
    
    private void setupResponseOldVersionFatal(final ByteArrayOutputStream response, final RpcCall call, final Writable rv, final String errorClass, final String error) throws IOException {
        final int OLD_VERSION_FATAL_STATUS = -1;
        response.reset();
        final DataOutputStream out = new DataOutputStream(response);
        out.writeInt(call.callId);
        out.writeInt(-1);
        WritableUtils.writeString(out, errorClass);
        WritableUtils.writeString(out, error);
        call.setResponse(ByteBuffer.wrap(response.toByteArray()));
    }
    
    private void wrapWithSasl(final RpcCall call) throws IOException {
        if (call.connection.saslServer != null) {
            byte[] token = call.rpcResponse.array();
            synchronized (call.connection.saslServer) {
                token = call.connection.saslServer.wrap(token, 0, token.length);
            }
            if (Server.LOG.isDebugEnabled()) {
                Server.LOG.debug("Adding saslServer wrapped token of size " + token.length + " as call response.");
            }
            final RpcHeaderProtos.RpcResponseHeaderProto saslHeader = RpcHeaderProtos.RpcResponseHeaderProto.newBuilder().setCallId(AuthProtocol.SASL.callId).setStatus(RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.SUCCESS).build();
            final RpcHeaderProtos.RpcSaslProto saslMessage = RpcHeaderProtos.RpcSaslProto.newBuilder().setState(RpcHeaderProtos.RpcSaslProto.SaslState.WRAP).setToken(ByteString.copyFrom(token)).build();
            this.setupResponse(call, saslHeader, RpcWritable.wrap(saslMessage));
        }
    }
    
    Configuration getConf() {
        return this.conf;
    }
    
    public void setSocketSendBufSize(final int size) {
        this.socketSendBufferSize = size;
    }
    
    public void setTracer(final Tracer t) {
        this.tracer = t;
    }
    
    public synchronized void start() {
        this.responder.start();
        this.listener.start();
        this.handlers = new Handler[this.handlerCount];
        for (int i = 0; i < this.handlerCount; ++i) {
            (this.handlers[i] = new Handler(i)).start();
        }
    }
    
    public synchronized void stop() {
        Server.LOG.info("Stopping server on " + this.port);
        this.running = false;
        if (this.handlers != null) {
            for (int i = 0; i < this.handlerCount; ++i) {
                if (this.handlers[i] != null) {
                    this.handlers[i].interrupt();
                }
            }
        }
        this.listener.interrupt();
        this.listener.doStop();
        this.responder.interrupt();
        this.notifyAll();
        this.rpcMetrics.shutdown();
        this.rpcDetailedMetrics.shutdown();
    }
    
    public synchronized void join() throws InterruptedException {
        while (this.running) {
            this.wait();
        }
    }
    
    public synchronized InetSocketAddress getListenerAddress() {
        return this.listener.getAddress();
    }
    
    @Deprecated
    public Writable call(final Writable param, final long receiveTime) throws Exception {
        return this.call(RPC.RpcKind.RPC_BUILTIN, null, param, receiveTime);
    }
    
    public abstract Writable call(final RPC.RpcKind p0, final String p1, final Writable p2, final long p3) throws Exception;
    
    private void authorize(final UserGroupInformation user, final String protocolName, final InetAddress addr) throws AuthorizationException {
        if (this.authorize) {
            if (protocolName == null) {
                throw new AuthorizationException("Null protocol not authorized");
            }
            Class<?> protocol = null;
            try {
                protocol = getProtocolClass(protocolName, this.getConf());
            }
            catch (ClassNotFoundException cfne) {
                throw new AuthorizationException("Unknown protocol: " + protocolName);
            }
            this.serviceAuthorizationManager.authorize(user, protocol, this.getConf(), addr);
        }
    }
    
    public int getPort() {
        return this.port;
    }
    
    public int getNumOpenConnections() {
        return this.connectionManager.size();
    }
    
    public String getNumOpenConnectionsPerUser() {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this.connectionManager.getUserToConnectionsMap());
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    public long getNumDroppedConnections() {
        return this.connectionManager.getDroppedConnections();
    }
    
    public int getCallQueueLen() {
        return this.callQueue.size();
    }
    
    public boolean isClientBackoffEnabled() {
        return this.callQueue.isClientBackoffEnabled();
    }
    
    public void setClientBackoffEnabled(final boolean value) {
        this.callQueue.setClientBackoffEnabled(value);
    }
    
    public int getMaxQueueSize() {
        return this.maxQueueSize;
    }
    
    public int getNumReaders() {
        return this.readThreads;
    }
    
    private int channelWrite(final WritableByteChannel channel, final ByteBuffer buffer) throws IOException {
        final int count = (buffer.remaining() <= Server.NIO_BUFFER_LIMIT) ? channel.write(buffer) : channelIO(null, channel, buffer);
        if (count > 0) {
            this.rpcMetrics.incrSentBytes(count);
        }
        return count;
    }
    
    private int channelRead(final ReadableByteChannel channel, final ByteBuffer buffer) throws IOException {
        final int count = (buffer.remaining() <= Server.NIO_BUFFER_LIMIT) ? channel.read(buffer) : channelIO(channel, null, buffer);
        if (count > 0) {
            this.rpcMetrics.incrReceivedBytes(count);
        }
        return count;
    }
    
    private static int channelIO(final ReadableByteChannel readCh, final WritableByteChannel writeCh, final ByteBuffer buf) throws IOException {
        final int originalLimit = buf.limit();
        final int initialRemaining = buf.remaining();
        int ret = 0;
        while (buf.remaining() > 0) {
            try {
                final int ioSize = Math.min(buf.remaining(), Server.NIO_BUFFER_LIMIT);
                buf.limit(buf.position() + ioSize);
                ret = ((readCh == null) ? writeCh.write(buf) : readCh.read(buf));
                if (ret < ioSize) {
                    break;
                }
                continue;
            }
            finally {
                buf.limit(originalLimit);
            }
        }
        final int nBytes = initialRemaining - buf.remaining();
        return (nBytes > 0) ? nBytes : ret;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    static {
        HTTP_GET_BYTES = ByteBuffer.wrap("GET ".getBytes(StandardCharsets.UTF_8));
        Server.INITIAL_RESP_BUF_SIZE = 10240;
        Server.rpcKindMap = new HashMap<RPC.RpcKind, RpcKindMapValue>(4);
        LOG = LoggerFactory.getLogger(Server.class);
        AUDITLOG = LoggerFactory.getLogger("SecurityLogger." + Server.class.getName());
        SERVER = new ThreadLocal<Server>();
        PROTOCOL_CACHE = new ConcurrentHashMap<String, Class<?>>();
        CurCall = new ThreadLocal<Call>();
        Server.NIO_BUFFER_LIMIT = 8192;
    }
    
    static class ExceptionsHandler
    {
        private volatile Set<String> terseExceptions;
        private volatile Set<String> suppressedExceptions;
        
        ExceptionsHandler() {
            this.terseExceptions = new HashSet<String>();
            this.suppressedExceptions = new HashSet<String>();
        }
        
        void addTerseLoggingExceptions(final Class<?>... exceptionClass) {
            this.terseExceptions = addExceptions(this.terseExceptions, exceptionClass);
        }
        
        void addSuppressedLoggingExceptions(final Class<?>... exceptionClass) {
            this.suppressedExceptions = addExceptions(this.suppressedExceptions, exceptionClass);
        }
        
        boolean isTerseLog(final Class<?> t) {
            return this.terseExceptions.contains(t.toString());
        }
        
        boolean isSuppressedLog(final Class<?> t) {
            return this.suppressedExceptions.contains(t.toString());
        }
        
        private static Set<String> addExceptions(final Set<String> exceptionsSet, final Class<?>[] exceptionClass) {
            final HashSet<String> newSet = new HashSet<String>(exceptionsSet);
            for (final Class<?> name : exceptionClass) {
                newSet.add(name.toString());
            }
            return Collections.unmodifiableSet((Set<? extends String>)newSet);
        }
    }
    
    static class RpcKindMapValue
    {
        final Class<? extends Writable> rpcRequestWrapperClass;
        final RPC.RpcInvoker rpcInvoker;
        
        RpcKindMapValue(final Class<? extends Writable> rpcRequestWrapperClass, final RPC.RpcInvoker rpcInvoker) {
            this.rpcInvoker = rpcInvoker;
            this.rpcRequestWrapperClass = rpcRequestWrapperClass;
        }
    }
    
    public static class Call implements Schedulable, PrivilegedExceptionAction<Void>
    {
        final int callId;
        final int retryCount;
        long timestamp;
        private AtomicInteger responseWaitCount;
        final RPC.RpcKind rpcKind;
        final byte[] clientId;
        private final TraceScope traceScope;
        private final CallerContext callerContext;
        private boolean deferredResponse;
        private int priorityLevel;
        
        Call() {
            this(-2, -1, RPC.RpcKind.RPC_BUILTIN, RpcConstants.DUMMY_CLIENT_ID);
        }
        
        Call(final Call call) {
            this(call.callId, call.retryCount, call.rpcKind, call.clientId, call.traceScope, call.callerContext);
        }
        
        Call(final int id, final int retryCount, final RPC.RpcKind kind, final byte[] clientId) {
            this(id, retryCount, kind, clientId, null, null);
        }
        
        @VisibleForTesting
        public Call(final int id, final int retryCount, final Void ignore1, final Void ignore2, final RPC.RpcKind kind, final byte[] clientId) {
            this(id, retryCount, kind, clientId, null, null);
        }
        
        Call(final int id, final int retryCount, final RPC.RpcKind kind, final byte[] clientId, final TraceScope traceScope, final CallerContext callerContext) {
            this.responseWaitCount = new AtomicInteger(1);
            this.deferredResponse = false;
            this.callId = id;
            this.retryCount = retryCount;
            this.timestamp = Time.now();
            this.rpcKind = kind;
            this.clientId = clientId;
            this.traceScope = traceScope;
            this.callerContext = callerContext;
        }
        
        @Override
        public String toString() {
            return "Call#" + this.callId + " Retry#" + this.retryCount;
        }
        
        @Override
        public Void run() throws Exception {
            return null;
        }
        
        public UserGroupInformation getRemoteUser() {
            return null;
        }
        
        public InetAddress getHostInetAddress() {
            return null;
        }
        
        public String getHostAddress() {
            final InetAddress addr = this.getHostInetAddress();
            return (addr != null) ? addr.getHostAddress() : null;
        }
        
        public String getProtocol() {
            return null;
        }
        
        @InterfaceStability.Unstable
        @InterfaceAudience.LimitedPrivate({ "HDFS" })
        public final void postponeResponse() {
            final int count = this.responseWaitCount.incrementAndGet();
            assert count > 0 : "response has already been sent";
        }
        
        @InterfaceStability.Unstable
        @InterfaceAudience.LimitedPrivate({ "HDFS" })
        public final void sendResponse() throws IOException {
            final int count = this.responseWaitCount.decrementAndGet();
            assert count >= 0 : "response has already been sent";
            if (count == 0) {
                this.doResponse(null);
            }
        }
        
        @InterfaceStability.Unstable
        @InterfaceAudience.LimitedPrivate({ "HDFS" })
        public final void abortResponse(final Throwable t) throws IOException {
            if (this.responseWaitCount.getAndSet(-1) > 0) {
                this.doResponse(t);
            }
        }
        
        void doResponse(final Throwable t) throws IOException {
        }
        
        @Override
        public UserGroupInformation getUserGroupInformation() {
            return this.getRemoteUser();
        }
        
        @Override
        public int getPriorityLevel() {
            return this.priorityLevel;
        }
        
        public void setPriorityLevel(final int priorityLevel) {
            this.priorityLevel = priorityLevel;
        }
        
        @InterfaceStability.Unstable
        public void deferResponse() {
            this.deferredResponse = true;
        }
        
        @InterfaceStability.Unstable
        public boolean isResponseDeferred() {
            return this.deferredResponse;
        }
        
        public void setDeferredResponse(final Writable response) {
        }
        
        public void setDeferredError(final Throwable t) {
        }
    }
    
    private class RpcCall extends Call
    {
        final Connection connection;
        final Writable rpcRequest;
        ByteBuffer rpcResponse;
        
        RpcCall(final RpcCall call) {
            super(call);
            this.connection = call.connection;
            this.rpcRequest = call.rpcRequest;
        }
        
        RpcCall(final Server server, final Connection connection, final int id) {
            this(server, connection, id, -1);
        }
        
        RpcCall(final Server server, final Connection connection, final int id, final int retryCount) {
            this(server, connection, id, retryCount, null, RPC.RpcKind.RPC_BUILTIN, RpcConstants.DUMMY_CLIENT_ID, null, null);
        }
        
        RpcCall(final Connection connection, final int id, final int retryCount, final Writable param, final RPC.RpcKind kind, final byte[] clientId, final TraceScope traceScope, final CallerContext context) {
            super(id, retryCount, kind, clientId, traceScope, context);
            this.connection = connection;
            this.rpcRequest = param;
        }
        
        @Override
        public String getProtocol() {
            return "rpc";
        }
        
        @Override
        public UserGroupInformation getRemoteUser() {
            return this.connection.user;
        }
        
        @Override
        public InetAddress getHostInetAddress() {
            return this.connection.getHostInetAddress();
        }
        
        @Override
        public Void run() throws Exception {
            if (!this.connection.channel.isOpen()) {
                Server.LOG.info(Thread.currentThread().getName() + ": skipped " + this);
                return null;
            }
            Writable value = null;
            final ResponseParams responseParams = new ResponseParams();
            try {
                value = Server.this.call(this.rpcKind, this.connection.protocolName, this.rpcRequest, this.timestamp);
            }
            catch (Throwable e) {
                this.populateResponseParamsOnError(e, responseParams);
            }
            if (!this.isResponseDeferred()) {
                Server.this.setupResponse(this, responseParams.returnStatus, responseParams.detailedErr, value, responseParams.errorClass, responseParams.error);
                this.sendResponse();
            }
            else if (Server.LOG.isDebugEnabled()) {
                Server.LOG.debug("Deferring response for callId: " + this.callId);
            }
            return null;
        }
        
        private void populateResponseParamsOnError(Throwable t, final ResponseParams responseParams) {
            if (t instanceof UndeclaredThrowableException) {
                t = t.getCause();
            }
            Server.this.logException(Server.LOG, t, this);
            if (t instanceof RpcServerException) {
                final RpcServerException rse = (RpcServerException)t;
                responseParams.returnStatus = rse.getRpcStatusProto();
                responseParams.detailedErr = rse.getRpcErrorCodeProto();
            }
            else {
                responseParams.returnStatus = RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.ERROR;
                responseParams.detailedErr = RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.ERROR_APPLICATION;
            }
            responseParams.errorClass = t.getClass().getName();
            responseParams.error = StringUtils.stringifyException(t);
            final String exceptionHdr = responseParams.errorClass + ": ";
            if (responseParams.error.startsWith(exceptionHdr)) {
                responseParams.error = responseParams.error.substring(exceptionHdr.length());
            }
        }
        
        void setResponse(final ByteBuffer response) throws IOException {
            this.rpcResponse = response;
        }
        
        @Override
        void doResponse(final Throwable t) throws IOException {
            RpcCall call = this;
            if (t != null) {
                call = new RpcCall(this);
                Server.this.setupResponse(call, RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.FATAL, RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.ERROR_RPC_SERVER, null, t.getClass().getName(), StringUtils.stringifyException(t));
            }
            this.connection.sendResponse(call);
        }
        
        private void sendDeferedResponse() {
            try {
                this.connection.sendResponse(this);
            }
            catch (Exception e) {
                Server.LOG.error("Failed to send deferred response. ThreadName=" + Thread.currentThread().getName() + ", CallId=" + this.callId + ", hostname=" + this.getHostAddress());
            }
        }
        
        @Override
        public void setDeferredResponse(final Writable response) {
            if (this.connection.getServer().running) {
                try {
                    Server.this.setupResponse(this, RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.SUCCESS, null, response, null, null);
                }
                catch (IOException e) {
                    Server.LOG.error("Failed to setup deferred successful response. ThreadName=" + Thread.currentThread().getName() + ", Call=" + this);
                    return;
                }
                this.sendDeferedResponse();
            }
        }
        
        @Override
        public void setDeferredError(Throwable t) {
            if (this.connection.getServer().running) {
                if (t == null) {
                    t = new IOException("User code indicated an error without an exception");
                }
                try {
                    final ResponseParams responseParams = new ResponseParams();
                    this.populateResponseParamsOnError(t, responseParams);
                    Server.this.setupResponse(this, responseParams.returnStatus, responseParams.detailedErr, null, responseParams.errorClass, responseParams.error);
                }
                catch (IOException e) {
                    Server.LOG.error("Failed to setup deferred error response. ThreadName=" + Thread.currentThread().getName() + ", Call=" + this);
                }
                this.sendDeferedResponse();
            }
        }
        
        @Override
        public String toString() {
            return super.toString() + " " + this.rpcRequest + " from " + this.connection;
        }
        
        private class ResponseParams
        {
            String errorClass;
            String error;
            RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto detailedErr;
            RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto returnStatus;
            
            private ResponseParams() {
                this.errorClass = null;
                this.error = null;
                this.detailedErr = null;
                this.returnStatus = RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.SUCCESS;
            }
        }
    }
    
    private class Listener extends Thread
    {
        private ServerSocketChannel acceptChannel;
        private Selector selector;
        private Reader[] readers;
        private int currentReader;
        private InetSocketAddress address;
        private int backlogLength;
        final /* synthetic */ Server this$0;
        
        public Listener() throws IOException {
            this.acceptChannel = null;
            this.selector = null;
            this.readers = null;
            this.currentReader = 0;
            this.backlogLength = Server.this.conf.getInt("ipc.server.listen.queue.size", 128);
            this.address = new InetSocketAddress(Server.this.bindAddress, Server.this.port);
            (this.acceptChannel = ServerSocketChannel.open()).configureBlocking(false);
            Server.bind(this.acceptChannel.socket(), this.address, this.backlogLength, Server.this.conf, Server.this.portRangeConfig);
            Server.this.port = this.acceptChannel.socket().getLocalPort();
            this.selector = Selector.open();
            this.readers = new Reader[Server.this.readThreads];
            for (int i = 0; i < Server.this.readThreads; ++i) {
                final Reader reader = new Reader("Socket Reader #" + (i + 1) + " for port " + Server.this.port);
                (this.readers[i] = reader).start();
            }
            this.acceptChannel.register(this.selector, 16);
            this.setName("IPC Server listener on " + Server.this.port);
            this.setDaemon(true);
        }
        
        @Override
        public void run() {
            Server.LOG.info(Thread.currentThread().getName() + ": starting");
            Server.SERVER.set(Server.this);
            Server.this.connectionManager.startIdleScan();
            while (Server.this.running) {
                SelectionKey key = null;
                try {
                    this.getSelector().select();
                    final Iterator<SelectionKey> iter = this.getSelector().selectedKeys().iterator();
                    while (iter.hasNext()) {
                        key = iter.next();
                        iter.remove();
                        try {
                            if (key.isValid() && key.isAcceptable()) {
                                this.doAccept(key);
                            }
                        }
                        catch (IOException ex) {}
                        key = null;
                    }
                }
                catch (OutOfMemoryError e) {
                    Server.LOG.warn("Out of Memory in server select", e);
                    this.closeCurrentConnection(key, e);
                    Server.this.connectionManager.closeIdle(true);
                    try {
                        Thread.sleep(60000L);
                    }
                    catch (Exception ex2) {}
                }
                catch (Exception e2) {
                    this.closeCurrentConnection(key, e2);
                }
            }
            Server.LOG.info("Stopping " + Thread.currentThread().getName());
            synchronized (this) {
                try {
                    this.acceptChannel.close();
                    this.selector.close();
                }
                catch (IOException ex3) {}
                this.selector = null;
                this.acceptChannel = null;
                Server.this.connectionManager.stopIdleScan();
                Server.this.connectionManager.closeAll();
            }
        }
        
        private void closeCurrentConnection(final SelectionKey key, final Throwable e) {
            if (key != null) {
                Connection c = (Connection)key.attachment();
                if (c != null) {
                    Server.this.closeConnection(c);
                    c = null;
                }
            }
        }
        
        InetSocketAddress getAddress() {
            return (InetSocketAddress)this.acceptChannel.socket().getLocalSocketAddress();
        }
        
        void doAccept(final SelectionKey key) throws InterruptedException, IOException, OutOfMemoryError {
            final ServerSocketChannel server = (ServerSocketChannel)key.channel();
            SocketChannel channel;
            while ((channel = server.accept()) != null) {
                channel.configureBlocking(false);
                channel.socket().setTcpNoDelay(Server.this.tcpNoDelay);
                channel.socket().setKeepAlive(true);
                final Reader reader = this.getReader();
                final Connection c = Server.this.connectionManager.register(channel);
                if (c == null) {
                    if (channel.isOpen()) {
                        IOUtils.cleanup(null, channel);
                    }
                    Server.this.connectionManager.droppedConnections.getAndIncrement();
                }
                else {
                    key.attach(c);
                    reader.addConnection(c);
                }
            }
        }
        
        void doRead(final SelectionKey key) throws InterruptedException {
            Connection c = (Connection)key.attachment();
            if (c == null) {
                return;
            }
            c.setLastContact(Time.now());
            int count;
            try {
                count = c.readAndProcess();
            }
            catch (InterruptedException ieo) {
                Server.LOG.info(Thread.currentThread().getName() + ": readAndProcess caught InterruptedException", ieo);
                throw ieo;
            }
            catch (Exception e) {
                Server.LOG.info(Thread.currentThread().getName() + ": readAndProcess from client " + c + " threw exception [" + e + "]", e);
                count = -1;
            }
            if (count < 0 || c.shouldClose()) {
                Server.this.closeConnection(c);
                c = null;
            }
            else {
                c.setLastContact(Time.now());
            }
        }
        
        synchronized void doStop() {
            if (this.selector != null) {
                this.selector.wakeup();
                Thread.yield();
            }
            if (this.acceptChannel != null) {
                try {
                    this.acceptChannel.socket().close();
                }
                catch (IOException e) {
                    Server.LOG.info(Thread.currentThread().getName() + ":Exception in closing listener socket. " + e);
                }
            }
            for (final Reader r : this.readers) {
                r.shutdown();
            }
        }
        
        synchronized Selector getSelector() {
            return this.selector;
        }
        
        Reader getReader() {
            this.currentReader = (this.currentReader + 1) % this.readers.length;
            return this.readers[this.currentReader];
        }
        
        private class Reader extends Thread
        {
            private final BlockingQueue<Connection> pendingConnections;
            private final Selector readSelector;
            
            Reader(final String name) throws IOException {
                super(name);
                this.pendingConnections = new LinkedBlockingQueue<Connection>(Listener.this.this$0.readerPendingConnectionQueue);
                this.readSelector = Selector.open();
            }
            
            @Override
            public void run() {
                Server.LOG.info("Starting " + Thread.currentThread().getName());
                try {
                    this.doRunLoop();
                }
                finally {
                    try {
                        this.readSelector.close();
                    }
                    catch (IOException ioe) {
                        Server.LOG.error("Error closing read selector in " + Thread.currentThread().getName(), ioe);
                    }
                }
            }
            
            private synchronized void doRunLoop() {
                while (Server.this.running) {
                    SelectionKey key = null;
                    try {
                        int i;
                        for (int size = i = this.pendingConnections.size(); i > 0; --i) {
                            final Connection conn = this.pendingConnections.take();
                            conn.channel.register(this.readSelector, 1, conn);
                        }
                        this.readSelector.select();
                        final Iterator<SelectionKey> iter = this.readSelector.selectedKeys().iterator();
                        while (iter.hasNext()) {
                            key = iter.next();
                            iter.remove();
                            try {
                                if (key.isReadable()) {
                                    Listener.this.doRead(key);
                                }
                            }
                            catch (CancelledKeyException cke) {
                                Server.LOG.info(Thread.currentThread().getName() + ": connection aborted from " + key.attachment());
                            }
                            key = null;
                        }
                    }
                    catch (InterruptedException e) {
                        if (!Server.this.running) {
                            continue;
                        }
                        Server.LOG.info(Thread.currentThread().getName() + " unexpectedly interrupted", e);
                    }
                    catch (IOException ex) {
                        Server.LOG.error("Error in Reader", ex);
                    }
                    catch (Throwable re) {
                        Server.LOG.error("Bug in read selector!", re);
                        ExitUtil.terminate(1, "Bug in read selector!");
                    }
                }
            }
            
            public void addConnection(final Connection conn) throws InterruptedException {
                this.pendingConnections.put(conn);
                this.readSelector.wakeup();
            }
            
            void shutdown() {
                assert !Server.this.running;
                this.readSelector.wakeup();
                try {
                    super.interrupt();
                    super.join();
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    private class Responder extends Thread
    {
        private final Selector writeSelector;
        private int pending;
        static final int PURGE_INTERVAL = 900000;
        
        Responder() throws IOException {
            this.setName("IPC Server Responder");
            this.setDaemon(true);
            this.writeSelector = Selector.open();
            this.pending = 0;
        }
        
        @Override
        public void run() {
            Server.LOG.info(Thread.currentThread().getName() + ": starting");
            Server.SERVER.set(Server.this);
            try {
                this.doRunLoop();
            }
            finally {
                Server.LOG.info("Stopping " + Thread.currentThread().getName());
                try {
                    this.writeSelector.close();
                }
                catch (IOException ioe) {
                    Server.LOG.error("Couldn't close write selector in " + Thread.currentThread().getName(), ioe);
                }
            }
        }
        
        private void doRunLoop() {
            long lastPurgeTime = 0L;
            while (Server.this.running) {
                try {
                    this.waitPending();
                    this.writeSelector.select(900000L);
                    Iterator<SelectionKey> iter = this.writeSelector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        final SelectionKey key = iter.next();
                        iter.remove();
                        try {
                            if (!key.isWritable()) {
                                continue;
                            }
                            this.doAsyncWrite(key);
                        }
                        catch (CancelledKeyException cke) {
                            final RpcCall call = (RpcCall)key.attachment();
                            if (call == null) {
                                continue;
                            }
                            Server.LOG.info(Thread.currentThread().getName() + ": connection aborted from " + call.connection);
                        }
                        catch (IOException e) {
                            Server.LOG.info(Thread.currentThread().getName() + ": doAsyncWrite threw exception " + e);
                        }
                    }
                    final long now = Time.now();
                    if (now < lastPurgeTime + 900000L) {
                        continue;
                    }
                    lastPurgeTime = now;
                    if (Server.LOG.isDebugEnabled()) {
                        Server.LOG.debug("Checking for old call responses.");
                    }
                    final ArrayList<RpcCall> calls;
                    synchronized (this.writeSelector.keys()) {
                        calls = new ArrayList<RpcCall>(this.writeSelector.keys().size());
                        iter = this.writeSelector.keys().iterator();
                        while (iter.hasNext()) {
                            final SelectionKey key2 = iter.next();
                            final RpcCall call2 = (RpcCall)key2.attachment();
                            if (call2 != null && key2.channel() == call2.connection.channel) {
                                calls.add(call2);
                            }
                        }
                    }
                    for (final RpcCall call3 : calls) {
                        this.doPurge(call3, now);
                    }
                }
                catch (OutOfMemoryError e2) {
                    Server.LOG.warn("Out of Memory in server select", e2);
                    try {
                        Thread.sleep(60000L);
                    }
                    catch (Exception ex) {}
                }
                catch (Exception e3) {
                    Server.LOG.warn("Exception in Responder", e3);
                }
            }
        }
        
        private void doAsyncWrite(final SelectionKey key) throws IOException {
            final RpcCall call = (RpcCall)key.attachment();
            if (call == null) {
                return;
            }
            if (key.channel() != call.connection.channel) {
                throw new IOException("doAsyncWrite: bad channel");
            }
            synchronized (call.connection.responseQueue) {
                if (this.processResponse(call.connection.responseQueue, false)) {
                    try {
                        key.interestOps(0);
                    }
                    catch (CancelledKeyException e) {
                        Server.LOG.warn("Exception while changing ops : " + e);
                    }
                }
            }
        }
        
        private void doPurge(RpcCall call, final long now) {
            final LinkedList<RpcCall> responseQueue = call.connection.responseQueue;
            synchronized (responseQueue) {
                final Iterator<RpcCall> iter = responseQueue.listIterator(0);
                while (iter.hasNext()) {
                    call = iter.next();
                    if (now > call.timestamp + 900000L) {
                        Server.this.closeConnection(call.connection);
                        break;
                    }
                }
            }
        }
        
        private boolean processResponse(final LinkedList<RpcCall> responseQueue, final boolean inHandler) throws IOException {
            boolean error = true;
            boolean done = false;
            int numElements = 0;
            RpcCall call = null;
            try {
                synchronized (responseQueue) {
                    numElements = responseQueue.size();
                    if (numElements == 0) {
                        error = false;
                        return true;
                    }
                    call = responseQueue.removeFirst();
                    final SocketChannel channel = call.connection.channel;
                    if (Server.LOG.isDebugEnabled()) {
                        Server.LOG.debug(Thread.currentThread().getName() + ": responding to " + call);
                    }
                    final int numBytes = Server.this.channelWrite(channel, call.rpcResponse);
                    if (numBytes < 0) {
                        return true;
                    }
                    if (!call.rpcResponse.hasRemaining()) {
                        call.rpcResponse = null;
                        call.connection.decRpcCount();
                        done = (numElements == 1);
                        if (Server.LOG.isDebugEnabled()) {
                            Server.LOG.debug(Thread.currentThread().getName() + ": responding to " + call + " Wrote " + numBytes + " bytes.");
                        }
                    }
                    else {
                        call.connection.responseQueue.addFirst(call);
                        if (inHandler) {
                            call.timestamp = Time.now();
                            this.incPending();
                            try {
                                this.writeSelector.wakeup();
                                channel.register(this.writeSelector, 4, call);
                            }
                            catch (ClosedChannelException e) {
                                done = true;
                            }
                            finally {
                                this.decPending();
                            }
                        }
                        if (Server.LOG.isDebugEnabled()) {
                            Server.LOG.debug(Thread.currentThread().getName() + ": responding to " + call + " Wrote partial " + numBytes + " bytes.");
                        }
                    }
                    error = false;
                }
            }
            finally {
                if (error && call != null) {
                    Server.LOG.warn(Thread.currentThread().getName() + ", call " + call + ": output error");
                    done = true;
                    Server.this.closeConnection(call.connection);
                }
            }
            return done;
        }
        
        void doRespond(final RpcCall call) throws IOException {
            synchronized (call.connection.responseQueue) {
                if (call.connection.useWrap) {
                    Server.this.wrapWithSasl(call);
                }
                call.connection.responseQueue.addLast(call);
                if (call.connection.responseQueue.size() == 1) {
                    this.processResponse(call.connection.responseQueue, true);
                }
            }
        }
        
        private synchronized void incPending() {
            ++this.pending;
        }
        
        private synchronized void decPending() {
            --this.pending;
            this.notify();
        }
        
        private synchronized void waitPending() throws InterruptedException {
            while (this.pending > 0) {
                this.wait();
            }
        }
    }
    
    @InterfaceAudience.Private
    public enum AuthProtocol
    {
        NONE(0), 
        SASL(-33);
        
        public final int callId;
        
        private AuthProtocol(final int callId) {
            this.callId = callId;
        }
        
        static AuthProtocol valueOf(final int callId) {
            for (final AuthProtocol authType : values()) {
                if (authType.callId == callId) {
                    return authType;
                }
            }
            return null;
        }
    }
    
    private static class FatalRpcServerException extends RpcServerException
    {
        private final RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto errCode;
        
        public FatalRpcServerException(final RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto errCode, final IOException ioe) {
            super(ioe.toString(), ioe);
            this.errCode = errCode;
        }
        
        public FatalRpcServerException(final RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto errCode, final String message) {
            this(errCode, new RpcServerException(message));
        }
        
        @Override
        public RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto getRpcStatusProto() {
            return RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.FATAL;
        }
        
        @Override
        public RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto getRpcErrorCodeProto() {
            return this.errCode;
        }
        
        @Override
        public String toString() {
            return this.getCause().toString();
        }
    }
    
    public class Connection
    {
        private boolean connectionHeaderRead;
        private boolean connectionContextRead;
        private SocketChannel channel;
        private ByteBuffer data;
        private ByteBuffer dataLengthBuffer;
        private LinkedList<RpcCall> responseQueue;
        private AtomicInteger rpcCount;
        private long lastContact;
        private int dataLength;
        private Socket socket;
        private String hostAddress;
        private int remotePort;
        private InetAddress addr;
        IpcConnectionContextProtos.IpcConnectionContextProto connectionContext;
        String protocolName;
        SaslServer saslServer;
        private SaslRpcServer.AuthMethod authMethod;
        private AuthProtocol authProtocol;
        private boolean saslContextEstablished;
        private ByteBuffer connectionHeaderBuf;
        private ByteBuffer unwrappedData;
        private ByteBuffer unwrappedDataLengthBuffer;
        private int serviceClass;
        private boolean shouldClose;
        UserGroupInformation user;
        public UserGroupInformation attemptingUser;
        private final RpcCall authFailedCall;
        private boolean sentNegotiate;
        private boolean useWrap;
        
        public Connection(final SocketChannel channel, final long lastContact) {
            this.connectionHeaderRead = false;
            this.connectionContextRead = false;
            this.rpcCount = new AtomicInteger();
            this.connectionHeaderBuf = null;
            this.shouldClose = false;
            this.user = null;
            this.attemptingUser = null;
            this.authFailedCall = new RpcCall(this, -1);
            this.sentNegotiate = false;
            this.useWrap = false;
            this.channel = channel;
            this.lastContact = lastContact;
            this.data = null;
            this.dataLengthBuffer = ByteBuffer.allocate(4);
            this.unwrappedData = null;
            this.unwrappedDataLengthBuffer = ByteBuffer.allocate(4);
            this.socket = channel.socket();
            this.addr = this.socket.getInetAddress();
            if (this.addr == null) {
                this.hostAddress = "*Unknown*";
            }
            else {
                this.hostAddress = this.addr.getHostAddress();
            }
            this.remotePort = this.socket.getPort();
            this.responseQueue = new LinkedList<RpcCall>();
            if (Server.this.socketSendBufferSize != 0) {
                try {
                    this.socket.setSendBufferSize(Server.this.socketSendBufferSize);
                }
                catch (IOException e) {
                    Server.LOG.warn("Connection: unable to set socket send buffer size to " + Server.this.socketSendBufferSize);
                }
            }
        }
        
        @Override
        public String toString() {
            return this.getHostAddress() + ":" + this.remotePort;
        }
        
        boolean setShouldClose() {
            return this.shouldClose = true;
        }
        
        boolean shouldClose() {
            return this.shouldClose;
        }
        
        public String getHostAddress() {
            return this.hostAddress;
        }
        
        public InetAddress getHostInetAddress() {
            return this.addr;
        }
        
        public void setLastContact(final long lastContact) {
            this.lastContact = lastContact;
        }
        
        public long getLastContact() {
            return this.lastContact;
        }
        
        public Server getServer() {
            return Server.this;
        }
        
        private boolean isIdle() {
            return this.rpcCount.get() == 0;
        }
        
        private void decRpcCount() {
            this.rpcCount.decrementAndGet();
        }
        
        private void incRpcCount() {
            this.rpcCount.incrementAndGet();
        }
        
        private UserGroupInformation getAuthorizedUgi(final String authorizedId) throws SecretManager.InvalidToken, AccessControlException {
            if (this.authMethod != SaslRpcServer.AuthMethod.TOKEN) {
                return UserGroupInformation.createRemoteUser(authorizedId, this.authMethod);
            }
            final TokenIdentifier tokenId = SaslRpcServer.getIdentifier(authorizedId, Server.this.secretManager);
            final UserGroupInformation ugi = tokenId.getUser();
            if (ugi == null) {
                throw new AccessControlException("Can't retrieve username from tokenIdentifier.");
            }
            ugi.addTokenIdentifier(tokenId);
            return ugi;
        }
        
        private void saslReadAndProcess(final RpcWritable.Buffer buffer) throws RpcServerException, IOException, InterruptedException {
            final RpcHeaderProtos.RpcSaslProto saslMessage = this.getMessage(RpcHeaderProtos.RpcSaslProto.getDefaultInstance(), buffer);
            switch (saslMessage.getState()) {
                case WRAP: {
                    if (!this.saslContextEstablished || !this.useWrap) {
                        throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER, new SaslException("Server is not wrapping data"));
                    }
                    this.unwrapPacketAndProcessRpcs(saslMessage.getToken().toByteArray());
                    break;
                }
                default: {
                    this.saslProcess(saslMessage);
                    break;
                }
            }
        }
        
        private Throwable getTrueCause(final IOException e) {
            for (Throwable cause = e; cause != null; cause = cause.getCause()) {
                if (cause instanceof RetriableException) {
                    return cause;
                }
                if (cause instanceof StandbyException) {
                    return cause;
                }
                if (cause instanceof SecretManager.InvalidToken) {
                    if (cause.getCause() != null) {
                        cause = cause.getCause();
                    }
                    return cause;
                }
            }
            return e;
        }
        
        private void saslProcess(final RpcHeaderProtos.RpcSaslProto saslMessage) throws RpcServerException, IOException, InterruptedException {
            if (this.saslContextEstablished) {
                throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER, new SaslException("Negotiation is already complete"));
            }
            RpcHeaderProtos.RpcSaslProto saslResponse = null;
            try {
                try {
                    saslResponse = this.processSaslMessage(saslMessage);
                }
                catch (IOException e) {
                    Server.this.rpcMetrics.incrAuthenticationFailures();
                    if (Server.LOG.isDebugEnabled()) {
                        Server.LOG.debug(StringUtils.stringifyException(e));
                    }
                    final IOException tce = (IOException)this.getTrueCause(e);
                    Server.AUDITLOG.warn("Auth failed for " + this.toString() + ":" + this.attemptingUser + " (" + e.getLocalizedMessage() + ") with true cause: (" + tce.getLocalizedMessage() + ")");
                    throw tce;
                }
                if (this.saslServer != null && this.saslServer.isComplete()) {
                    if (Server.LOG.isDebugEnabled()) {
                        Server.LOG.debug("SASL server context established. Negotiated QoP is " + this.saslServer.getNegotiatedProperty("javax.security.sasl.qop"));
                    }
                    this.user = this.getAuthorizedUgi(this.saslServer.getAuthorizationID());
                    if (Server.LOG.isDebugEnabled()) {
                        Server.LOG.debug("SASL server successfully authenticated client: " + this.user);
                    }
                    Server.this.rpcMetrics.incrAuthenticationSuccesses();
                    Server.AUDITLOG.info("Auth successful for " + this.user);
                    this.saslContextEstablished = true;
                }
            }
            catch (RpcServerException rse) {
                throw rse;
            }
            catch (IOException ioe) {
                throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_UNAUTHORIZED, ioe);
            }
            if (saslResponse != null) {
                this.doSaslReply(saslResponse);
            }
            if (this.saslContextEstablished) {
                final String qop = (String)this.saslServer.getNegotiatedProperty("javax.security.sasl.qop");
                if (!(this.useWrap = (qop != null && !"auth".equalsIgnoreCase(qop)))) {
                    this.disposeSasl();
                }
            }
        }
        
        private RpcHeaderProtos.RpcSaslProto processSaslMessage(final RpcHeaderProtos.RpcSaslProto saslMessage) throws SaslException, IOException, AccessControlException, InterruptedException {
            final RpcHeaderProtos.RpcSaslProto.SaslState state = saslMessage.getState();
            RpcHeaderProtos.RpcSaslProto saslResponse = null;
            switch (state) {
                case NEGOTIATE: {
                    if (this.sentNegotiate) {
                        throw new AccessControlException("Client already attempted negotiation");
                    }
                    saslResponse = this.buildSaslNegotiateResponse();
                    if (saslResponse.getState() == RpcHeaderProtos.RpcSaslProto.SaslState.SUCCESS) {
                        this.switchToSimple();
                        break;
                    }
                    break;
                }
                case INITIATE: {
                    if (saslMessage.getAuthsCount() != 1) {
                        throw new SaslException("Client mechanism is malformed");
                    }
                    final RpcHeaderProtos.RpcSaslProto.SaslAuth clientSaslAuth = saslMessage.getAuths(0);
                    if (!Server.this.negotiateResponse.getAuthsList().contains(clientSaslAuth)) {
                        if (this.sentNegotiate) {
                            throw new AccessControlException(clientSaslAuth.getMethod() + " authentication is not enabled.  Available:" + Server.this.enabledAuthMethods);
                        }
                        saslResponse = this.buildSaslNegotiateResponse();
                        break;
                    }
                    else {
                        this.authMethod = SaslRpcServer.AuthMethod.valueOf(clientSaslAuth.getMethod());
                        if (this.authMethod == SaslRpcServer.AuthMethod.SIMPLE) {
                            this.switchToSimple();
                            saslResponse = null;
                            break;
                        }
                        if (this.saslServer == null || this.authMethod != SaslRpcServer.AuthMethod.TOKEN) {
                            this.saslServer = this.createSaslServer(this.authMethod);
                        }
                        saslResponse = this.processSaslToken(saslMessage);
                        break;
                    }
                    break;
                }
                case RESPONSE: {
                    saslResponse = this.processSaslToken(saslMessage);
                    break;
                }
                default: {
                    throw new SaslException("Client sent unsupported state " + state);
                }
            }
            return saslResponse;
        }
        
        private RpcHeaderProtos.RpcSaslProto processSaslToken(final RpcHeaderProtos.RpcSaslProto saslMessage) throws SaslException {
            if (!saslMessage.hasToken()) {
                throw new SaslException("Client did not send a token");
            }
            byte[] saslToken = saslMessage.getToken().toByteArray();
            if (Server.LOG.isDebugEnabled()) {
                Server.LOG.debug("Have read input token of size " + saslToken.length + " for processing by saslServer.evaluateResponse()");
            }
            saslToken = this.saslServer.evaluateResponse(saslToken);
            return this.buildSaslResponse(this.saslServer.isComplete() ? RpcHeaderProtos.RpcSaslProto.SaslState.SUCCESS : RpcHeaderProtos.RpcSaslProto.SaslState.CHALLENGE, saslToken);
        }
        
        private void switchToSimple() {
            this.authProtocol = AuthProtocol.NONE;
            this.disposeSasl();
        }
        
        private RpcHeaderProtos.RpcSaslProto buildSaslResponse(final RpcHeaderProtos.RpcSaslProto.SaslState state, final byte[] replyToken) {
            if (Server.LOG.isDebugEnabled()) {
                Server.LOG.debug("Will send " + state + " token of size " + ((replyToken != null) ? Integer.valueOf(replyToken.length) : null) + " from saslServer.");
            }
            final RpcHeaderProtos.RpcSaslProto.Builder response = RpcHeaderProtos.RpcSaslProto.newBuilder();
            response.setState(state);
            if (replyToken != null) {
                response.setToken(ByteString.copyFrom(replyToken));
            }
            return response.build();
        }
        
        private void doSaslReply(final Message message) throws IOException {
            final RpcCall saslCall = new RpcCall(this, AuthProtocol.SASL.callId);
            Server.this.setupResponse(saslCall, RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.SUCCESS, null, RpcWritable.wrap(message), null, null);
            this.sendResponse(saslCall);
        }
        
        private void doSaslReply(final Exception ioe) throws IOException {
            Server.this.setupResponse(this.authFailedCall, RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.FATAL, RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_UNAUTHORIZED, null, ioe.getClass().getName(), ioe.getLocalizedMessage());
            this.sendResponse(this.authFailedCall);
        }
        
        private void disposeSasl() {
            if (this.saslServer != null) {
                try {
                    this.saslServer.dispose();
                }
                catch (SaslException ex) {}
                finally {
                    this.saslServer = null;
                }
            }
        }
        
        private void checkDataLength(final int dataLength) throws IOException {
            if (dataLength < 0) {
                final String error = "Unexpected data length " + dataLength + "!! from " + this.getHostAddress();
                Server.LOG.warn(error);
                throw new IOException(error);
            }
            if (dataLength > Server.this.maxDataLength) {
                final String error = "Requested data length " + dataLength + " is longer than maximum configured RPC length " + Server.this.maxDataLength + ".  RPC came from " + this.getHostAddress();
                Server.LOG.warn(error);
                throw new IOException(error);
            }
        }
        
        public int readAndProcess() throws IOException, InterruptedException {
            while (!this.shouldClose()) {
                int count = -1;
                if (this.dataLengthBuffer.remaining() > 0) {
                    count = Server.this.channelRead(this.channel, this.dataLengthBuffer);
                    if (count < 0 || this.dataLengthBuffer.remaining() > 0) {
                        return count;
                    }
                }
                if (this.connectionHeaderRead) {
                    if (this.data == null) {
                        this.dataLengthBuffer.flip();
                        this.checkDataLength(this.dataLength = this.dataLengthBuffer.getInt());
                        this.data = ByteBuffer.allocate(this.dataLength);
                    }
                    count = Server.this.channelRead(this.channel, this.data);
                    if (this.data.remaining() == 0) {
                        this.dataLengthBuffer.clear();
                        this.data.flip();
                        final ByteBuffer requestData = this.data;
                        this.data = null;
                        final boolean isHeaderRead = this.connectionContextRead;
                        this.processOneRpc(requestData);
                        if (!isHeaderRead) {
                            continue;
                        }
                    }
                    return count;
                }
                if (this.connectionHeaderBuf == null) {
                    this.connectionHeaderBuf = ByteBuffer.allocate(3);
                }
                count = Server.this.channelRead(this.channel, this.connectionHeaderBuf);
                if (count < 0 || this.connectionHeaderBuf.remaining() > 0) {
                    return count;
                }
                final int version = this.connectionHeaderBuf.get(0);
                this.setServiceClass(this.connectionHeaderBuf.get(1));
                this.dataLengthBuffer.flip();
                if (Server.HTTP_GET_BYTES.equals(this.dataLengthBuffer)) {
                    this.setupHttpRequestOnIpcPortResponse();
                    return -1;
                }
                if (!RpcConstants.HEADER.equals(this.dataLengthBuffer) || version != 9) {
                    Server.LOG.warn("Incorrect header or version mismatch from " + this.hostAddress + ":" + this.remotePort + " got version " + version + " expected version " + 9);
                    this.setupBadVersionResponse(version);
                    return -1;
                }
                this.authProtocol = this.initializeAuthContext(this.connectionHeaderBuf.get(2));
                this.dataLengthBuffer.clear();
                this.connectionHeaderBuf = null;
                this.connectionHeaderRead = true;
            }
            return -1;
        }
        
        private AuthProtocol initializeAuthContext(final int authType) throws IOException {
            final AuthProtocol authProtocol = AuthProtocol.valueOf(authType);
            if (authProtocol == null) {
                final IOException ioe = new IpcException("Unknown auth protocol:" + authType);
                this.doSaslReply(ioe);
                throw ioe;
            }
            final boolean isSimpleEnabled = Server.this.enabledAuthMethods.contains(SaslRpcServer.AuthMethod.SIMPLE);
            switch (authProtocol) {
                case NONE: {
                    if (!isSimpleEnabled) {
                        final IOException ioe2 = new AccessControlException("SIMPLE authentication is not enabled.  Available:" + Server.this.enabledAuthMethods);
                        this.doSaslReply(ioe2);
                        throw ioe2;
                    }
                    break;
                }
            }
            return authProtocol;
        }
        
        private RpcHeaderProtos.RpcSaslProto buildSaslNegotiateResponse() throws InterruptedException, SaslException, IOException {
            RpcHeaderProtos.RpcSaslProto negotiateMessage = Server.this.negotiateResponse;
            if (Server.this.enabledAuthMethods.contains(SaslRpcServer.AuthMethod.TOKEN)) {
                this.saslServer = this.createSaslServer(SaslRpcServer.AuthMethod.TOKEN);
                final byte[] challenge = this.saslServer.evaluateResponse(new byte[0]);
                final RpcHeaderProtos.RpcSaslProto.Builder negotiateBuilder = RpcHeaderProtos.RpcSaslProto.newBuilder(Server.this.negotiateResponse);
                negotiateBuilder.getAuthsBuilder(0).setChallenge(ByteString.copyFrom(challenge));
                negotiateMessage = negotiateBuilder.build();
            }
            this.sentNegotiate = true;
            return negotiateMessage;
        }
        
        private SaslServer createSaslServer(final SaslRpcServer.AuthMethod authMethod) throws IOException, InterruptedException {
            final Map<String, ?> saslProps = Server.this.saslPropsResolver.getServerProperties(this.addr);
            return new SaslRpcServer(authMethod).create(this, saslProps, Server.this.secretManager);
        }
        
        private void setupBadVersionResponse(final int clientVersion) throws IOException {
            final String errMsg = "Server IPC version 9 cannot communicate with client version " + clientVersion;
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            if (clientVersion >= 9) {
                final RpcCall fakeCall = new RpcCall(this, -1);
                Server.this.setupResponse(fakeCall, RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.FATAL, RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_VERSION_MISMATCH, null, RPC.VersionMismatch.class.getName(), errMsg);
                this.sendResponse(fakeCall);
            }
            else if (clientVersion >= 3) {
                final RpcCall fakeCall = new RpcCall(this, -1);
                Server.this.setupResponseOldVersionFatal(buffer, fakeCall, null, RPC.VersionMismatch.class.getName(), errMsg);
                this.sendResponse(fakeCall);
            }
            else if (clientVersion == 2) {
                final RpcCall fakeCall = new RpcCall(this, 0);
                final DataOutputStream out = new DataOutputStream(buffer);
                out.writeInt(0);
                out.writeBoolean(true);
                WritableUtils.writeString(out, RPC.VersionMismatch.class.getName());
                WritableUtils.writeString(out, errMsg);
                fakeCall.setResponse(ByteBuffer.wrap(buffer.toByteArray()));
                this.sendResponse(fakeCall);
            }
        }
        
        private void setupHttpRequestOnIpcPortResponse() throws IOException {
            final RpcCall fakeCall = new RpcCall(this, 0);
            fakeCall.setResponse(ByteBuffer.wrap("HTTP/1.1 404 Not Found\r\nContent-type: text/plain\r\n\r\nIt looks like you are making an HTTP request to a Hadoop IPC port. This is not the correct port for the web interface on this daemon.\r\n".getBytes(StandardCharsets.UTF_8)));
            this.sendResponse(fakeCall);
        }
        
        private void processConnectionContext(final RpcWritable.Buffer buffer) throws RpcServerException {
            if (this.connectionContextRead) {
                throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER, "Connection context already processed");
            }
            this.connectionContext = this.getMessage(IpcConnectionContextProtos.IpcConnectionContextProto.getDefaultInstance(), buffer);
            this.protocolName = (this.connectionContext.hasProtocol() ? this.connectionContext.getProtocol() : null);
            final UserGroupInformation protocolUser = ProtoUtil.getUgi(this.connectionContext);
            if (this.authProtocol == AuthProtocol.NONE) {
                this.user = protocolUser;
            }
            else {
                this.user.setAuthenticationMethod(this.authMethod);
                if (protocolUser != null && !protocolUser.getUserName().equals(this.user.getUserName())) {
                    if (this.authMethod == SaslRpcServer.AuthMethod.TOKEN) {
                        throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_UNAUTHORIZED, new AccessControlException("Authenticated user (" + this.user + ") doesn't match what the client claims to be (" + protocolUser + ")"));
                    }
                    final UserGroupInformation realUser = this.user;
                    this.user = UserGroupInformation.createProxyUser(protocolUser.getUserName(), realUser);
                }
            }
            this.authorizeConnection();
            this.connectionContextRead = true;
            if (this.user != null) {
                Server.this.connectionManager.incrUserConnections(this.user.getShortUserName());
            }
        }
        
        private void unwrapPacketAndProcessRpcs(byte[] inBuf) throws IOException, InterruptedException {
            if (Server.LOG.isDebugEnabled()) {
                Server.LOG.debug("Have read input token of size " + inBuf.length + " for processing by saslServer.unwrap()");
            }
            inBuf = this.saslServer.unwrap(inBuf, 0, inBuf.length);
            final ReadableByteChannel ch = Channels.newChannel(new ByteArrayInputStream(inBuf));
            while (!this.shouldClose()) {
                int count = -1;
                if (this.unwrappedDataLengthBuffer.remaining() > 0) {
                    count = Server.this.channelRead(ch, this.unwrappedDataLengthBuffer);
                    if (count <= 0 || this.unwrappedDataLengthBuffer.remaining() > 0) {
                        return;
                    }
                }
                if (this.unwrappedData == null) {
                    this.unwrappedDataLengthBuffer.flip();
                    final int unwrappedDataLength = this.unwrappedDataLengthBuffer.getInt();
                    this.unwrappedData = ByteBuffer.allocate(unwrappedDataLength);
                }
                count = Server.this.channelRead(ch, this.unwrappedData);
                if (count <= 0 || this.unwrappedData.remaining() > 0) {
                    return;
                }
                if (this.unwrappedData.remaining() != 0) {
                    continue;
                }
                this.unwrappedDataLengthBuffer.clear();
                this.unwrappedData.flip();
                final ByteBuffer requestData = this.unwrappedData;
                this.unwrappedData = null;
                this.processOneRpc(requestData);
            }
        }
        
        private void processOneRpc(final ByteBuffer bb) throws IOException, InterruptedException {
            int callId = -1;
            int retry = -1;
            try {
                final RpcWritable.Buffer buffer = RpcWritable.Buffer.wrap(bb);
                final RpcHeaderProtos.RpcRequestHeaderProto header = this.getMessage(RpcHeaderProtos.RpcRequestHeaderProto.getDefaultInstance(), buffer);
                callId = header.getCallId();
                retry = header.getRetryCount();
                if (Server.LOG.isDebugEnabled()) {
                    Server.LOG.debug(" got #" + callId);
                }
                this.checkRpcHeaders(header);
                if (callId < 0) {
                    this.processRpcOutOfBandRequest(header, buffer);
                }
                else {
                    if (!this.connectionContextRead) {
                        throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER, "Connection context not established");
                    }
                    this.processRpcRequest(header, buffer);
                }
            }
            catch (RpcServerException rse) {
                if (Server.LOG.isDebugEnabled()) {
                    Server.LOG.debug(Thread.currentThread().getName() + ": processOneRpc from client " + this + " threw exception [" + rse + "]");
                }
                final Throwable t = (rse.getCause() != null) ? rse.getCause() : rse;
                final RpcCall call = new RpcCall(this, callId, retry);
                Server.this.setupResponse(call, rse.getRpcStatusProto(), rse.getRpcErrorCodeProto(), null, t.getClass().getName(), t.getMessage());
                this.sendResponse(call);
            }
        }
        
        private void checkRpcHeaders(final RpcHeaderProtos.RpcRequestHeaderProto header) throws RpcServerException {
            if (!header.hasRpcOp()) {
                final String err = " IPC Server: No rpc op in rpcRequestHeader";
                throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER, err);
            }
            if (header.getRpcOp() != RpcHeaderProtos.RpcRequestHeaderProto.OperationProto.RPC_FINAL_PACKET) {
                final String err = "IPC Server does not implement rpc header operation" + header.getRpcOp();
                throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER, err);
            }
            if (!header.hasRpcKind()) {
                final String err = " IPC Server: No rpc kind in rpcRequestHeader";
                throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER, err);
            }
        }
        
        private void processRpcRequest(final RpcHeaderProtos.RpcRequestHeaderProto header, final RpcWritable.Buffer buffer) throws RpcServerException, InterruptedException {
            final Class<? extends Writable> rpcRequestClass = Server.this.getRpcRequestWrapper(header.getRpcKind());
            if (rpcRequestClass == null) {
                Server.LOG.warn("Unknown rpc kind " + header.getRpcKind() + " from client " + this.getHostAddress());
                final String err = "Unknown rpc kind in rpc header" + header.getRpcKind();
                throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER, err);
            }
            Writable rpcRequest;
            try {
                rpcRequest = buffer.newInstance(rpcRequestClass, Server.this.conf);
            }
            catch (RpcServerException rse) {
                throw rse;
            }
            catch (Throwable t) {
                Server.LOG.warn("Unable to read call parameters for client " + this.getHostAddress() + "on connection protocol " + this.protocolName + " for rpcKind " + header.getRpcKind(), t);
                final String err2 = "IPC server unable to read call parameters: " + t.getMessage();
                throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_DESERIALIZING_REQUEST, err2);
            }
            TraceScope traceScope = null;
            if (header.hasTraceInfo() && Server.this.tracer != null) {
                final SpanId parentSpanId = new SpanId(header.getTraceInfo().getTraceId(), header.getTraceInfo().getParentId());
                traceScope = Server.this.tracer.newScope(RpcClientUtil.toTraceName(rpcRequest.toString()), parentSpanId);
                traceScope.detach();
            }
            CallerContext callerContext = null;
            if (header.hasCallerContext()) {
                callerContext = new CallerContext.Builder(header.getCallerContext().getContext()).setSignature(header.getCallerContext().getSignature().toByteArray()).build();
            }
            final RpcCall call = new RpcCall(this, header.getCallId(), header.getRetryCount(), rpcRequest, ProtoUtil.convert(header.getRpcKind()), header.getClientId().toByteArray(), traceScope, callerContext);
            call.setPriorityLevel(Server.this.callQueue.getPriorityLevel(call));
            try {
                Server.this.internalQueueCall(call);
            }
            catch (RpcServerException rse2) {
                throw rse2;
            }
            catch (IOException ioe) {
                throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.ERROR_RPC_SERVER, ioe);
            }
            this.incRpcCount();
        }
        
        private void processRpcOutOfBandRequest(final RpcHeaderProtos.RpcRequestHeaderProto header, final RpcWritable.Buffer buffer) throws RpcServerException, IOException, InterruptedException {
            final int callId = header.getCallId();
            if (callId == -3) {
                if (this.authProtocol == AuthProtocol.SASL && !this.saslContextEstablished) {
                    throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER, "Connection header sent during SASL negotiation");
                }
                this.processConnectionContext(buffer);
            }
            else if (callId == AuthProtocol.SASL.callId) {
                if (this.authProtocol != AuthProtocol.SASL) {
                    throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER, "SASL protocol not requested by client");
                }
                this.saslReadAndProcess(buffer);
            }
            else {
                if (callId != -4) {
                    throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER, "Unknown out of band call #" + callId);
                }
                Server.LOG.debug("Received ping message");
            }
        }
        
        private void authorizeConnection() throws RpcServerException {
            try {
                if (this.user != null && this.user.getRealUser() != null && this.authMethod != SaslRpcServer.AuthMethod.TOKEN) {
                    ProxyUsers.authorize(this.user, this.getHostAddress());
                }
                Server.this.authorize(this.user, this.protocolName, this.getHostInetAddress());
                if (Server.LOG.isDebugEnabled()) {
                    Server.LOG.debug("Successfully authorized " + this.connectionContext);
                }
                Server.this.rpcMetrics.incrAuthorizationSuccesses();
            }
            catch (AuthorizationException ae) {
                Server.LOG.info("Connection from " + this + " for protocol " + this.connectionContext.getProtocol() + " is unauthorized for user " + this.user);
                Server.this.rpcMetrics.incrAuthorizationFailures();
                throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_UNAUTHORIZED, ae);
            }
        }
        
         <T extends Message> T getMessage(final Message message, final RpcWritable.Buffer buffer) throws RpcServerException {
            try {
                return buffer.getValue(message);
            }
            catch (Exception ioe) {
                final Class<?> protoClass = message.getClass();
                throw new FatalRpcServerException(RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.FATAL_DESERIALIZING_REQUEST, "Error decoding " + protoClass.getSimpleName() + ": " + ioe);
            }
        }
        
        private void sendResponse(final RpcCall call) throws IOException {
            Server.this.responder.doRespond(call);
        }
        
        public int getServiceClass() {
            return this.serviceClass;
        }
        
        public void setServiceClass(final int serviceClass) {
            this.serviceClass = serviceClass;
        }
        
        private synchronized void close() {
            this.disposeSasl();
            this.data = null;
            this.dataLengthBuffer = null;
            if (!this.channel.isOpen()) {
                return;
            }
            try {
                this.socket.shutdownOutput();
            }
            catch (Exception e) {
                Server.LOG.debug("Ignoring socket shutdown exception", e);
            }
            if (this.channel.isOpen()) {
                IOUtils.cleanup(null, this.channel);
            }
            IOUtils.cleanup(null, this.socket);
        }
    }
    
    private class Handler extends Thread
    {
        public Handler(final int instanceNumber) {
            this.setDaemon(true);
            this.setName("IPC Server handler " + instanceNumber + " on " + Server.this.port);
        }
        
        @Override
        public void run() {
            Server.LOG.debug(Thread.currentThread().getName() + ": starting");
            Server.SERVER.set(Server.this);
            while (Server.this.running) {
                TraceScope traceScope = null;
                try {
                    final Call call = Server.this.callQueue.take();
                    if (Server.LOG.isDebugEnabled()) {
                        Server.LOG.debug(Thread.currentThread().getName() + ": " + call + " for RpcKind " + call.rpcKind);
                    }
                    Server.CurCall.set(call);
                    if (call.traceScope != null) {
                        call.traceScope.reattach();
                        traceScope = call.traceScope;
                        traceScope.getSpan().addTimelineAnnotation("called");
                    }
                    CallerContext.setCurrent(call.callerContext);
                    final UserGroupInformation remoteUser = call.getRemoteUser();
                    if (remoteUser != null) {
                        remoteUser.doAs((PrivilegedExceptionAction<Object>)call);
                    }
                    else {
                        call.run();
                    }
                }
                catch (InterruptedException e) {
                    if (Server.this.running) {
                        Server.LOG.info(Thread.currentThread().getName() + " unexpectedly interrupted", e);
                        if (traceScope != null) {
                            traceScope.getSpan().addTimelineAnnotation("unexpectedly interrupted: " + StringUtils.stringifyException(e));
                        }
                    }
                }
                catch (Exception e2) {
                    Server.LOG.info(Thread.currentThread().getName() + " caught an exception", e2);
                    if (traceScope != null) {
                        traceScope.getSpan().addTimelineAnnotation("Exception: " + StringUtils.stringifyException(e2));
                    }
                }
                finally {
                    Server.CurCall.set(null);
                    IOUtils.cleanupWithLogger(Server.LOG, traceScope);
                }
            }
            Server.LOG.debug(Thread.currentThread().getName() + ": exiting");
        }
    }
    
    private class ConnectionManager
    {
        private final AtomicInteger count;
        private final AtomicLong droppedConnections;
        private final Set<Connection> connections;
        private final Map<String, Integer> userToConnectionsMap;
        private final Object userToConnectionsMapLock;
        private final Timer idleScanTimer;
        private final int idleScanThreshold;
        private final int idleScanInterval;
        private final int maxIdleTime;
        private final int maxIdleToClose;
        private final int maxConnections;
        
        ConnectionManager() {
            this.count = new AtomicInteger();
            this.droppedConnections = new AtomicLong();
            this.userToConnectionsMapLock = new Object();
            this.idleScanTimer = new Timer("IPC Server idle connection scanner for port " + Server.this.getPort(), true);
            this.idleScanThreshold = Server.this.conf.getInt("ipc.client.idlethreshold", 4000);
            this.idleScanInterval = Server.this.conf.getInt("ipc.client.connection.idle-scan-interval.ms", 10000);
            this.maxIdleTime = 2 * Server.this.conf.getInt("ipc.client.connection.maxidletime", 10000);
            this.maxIdleToClose = Server.this.conf.getInt("ipc.client.kill.max", 10);
            this.maxConnections = Server.this.conf.getInt("ipc.server.max.connections", 0);
            this.connections = Collections.newSetFromMap(new ConcurrentHashMap<Connection, Boolean>(Server.this.maxQueueSize, 0.75f, Server.this.readThreads + 2));
            this.userToConnectionsMap = new ConcurrentHashMap<String, Integer>();
        }
        
        private boolean add(final Connection connection) {
            final boolean added = this.connections.add(connection);
            if (added) {
                this.count.getAndIncrement();
            }
            return added;
        }
        
        private boolean remove(final Connection connection) {
            final boolean removed = this.connections.remove(connection);
            if (removed) {
                this.count.getAndDecrement();
            }
            return removed;
        }
        
        void incrUserConnections(final String user) {
            synchronized (this.userToConnectionsMapLock) {
                Integer count = this.userToConnectionsMap.get(user);
                if (count == null) {
                    count = 1;
                }
                else {
                    ++count;
                }
                this.userToConnectionsMap.put(user, count);
            }
        }
        
        void decrUserConnections(final String user) {
            synchronized (this.userToConnectionsMapLock) {
                Integer count = this.userToConnectionsMap.get(user);
                if (count == null) {
                    return;
                }
                --count;
                if (count == 0) {
                    this.userToConnectionsMap.remove(user);
                }
                else {
                    this.userToConnectionsMap.put(user, count);
                }
            }
        }
        
        Map<String, Integer> getUserToConnectionsMap() {
            return this.userToConnectionsMap;
        }
        
        long getDroppedConnections() {
            return this.droppedConnections.get();
        }
        
        int size() {
            return this.count.get();
        }
        
        boolean isFull() {
            return this.maxConnections > 0 && this.size() >= this.maxConnections;
        }
        
        Connection[] toArray() {
            return this.connections.toArray(new Connection[0]);
        }
        
        Connection register(final SocketChannel channel) {
            if (this.isFull()) {
                return null;
            }
            final Connection connection = new Connection(channel, Time.now());
            this.add(connection);
            if (Server.LOG.isDebugEnabled()) {
                Server.LOG.debug("Server connection from " + connection + "; # active connections: " + this.size() + "; # queued calls: " + Server.this.callQueue.size());
            }
            return connection;
        }
        
        boolean close(final Connection connection) {
            final boolean exists = this.remove(connection);
            if (exists) {
                if (Server.LOG.isDebugEnabled()) {
                    Server.LOG.debug(Thread.currentThread().getName() + ": disconnecting client " + connection + ". Number of active connections: " + this.size());
                }
                connection.close();
                if (connection.user != null && connection.connectionContextRead) {
                    this.decrUserConnections(connection.user.getShortUserName());
                }
            }
            return exists;
        }
        
        synchronized void closeIdle(final boolean scanAll) {
            final long minLastContact = Time.now() - this.maxIdleTime;
            int closed = 0;
            for (final Connection connection : this.connections) {
                if (!scanAll && this.size() < this.idleScanThreshold) {
                    break;
                }
                if (connection.isIdle() && connection.getLastContact() < minLastContact && this.close(connection) && !scanAll && ++closed == this.maxIdleToClose) {
                    break;
                }
            }
        }
        
        void closeAll() {
            for (final Connection connection : this.toArray()) {
                this.close(connection);
            }
        }
        
        void startIdleScan() {
            this.scheduleIdleScanTask();
        }
        
        void stopIdleScan() {
            this.idleScanTimer.cancel();
        }
        
        private void scheduleIdleScanTask() {
            if (!Server.this.running) {
                return;
            }
            final TimerTask idleScanTask = new TimerTask() {
                @Override
                public void run() {
                    if (!Server.this.running) {
                        return;
                    }
                    if (Server.LOG.isDebugEnabled()) {
                        Server.LOG.debug(Thread.currentThread().getName() + ": task running");
                    }
                    try {
                        ConnectionManager.this.closeIdle(false);
                    }
                    finally {
                        ConnectionManager.this.scheduleIdleScanTask();
                    }
                }
            };
            this.idleScanTimer.schedule(idleScanTask, this.idleScanInterval);
        }
    }
}
