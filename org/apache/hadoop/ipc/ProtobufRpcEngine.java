// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.io.OutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.RpcController;
import com.google.protobuf.BlockingService;
import org.apache.htrace.core.TraceScope;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.htrace.core.Tracer;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.ipc.protobuf.ProtobufRpcEngineProtos;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.IOException;
import org.apache.hadoop.io.retry.RetryPolicy;
import javax.net.SocketFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import java.net.InetSocketAddress;
import com.google.protobuf.Message;
import org.apache.hadoop.util.concurrent.AsyncGet;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Evolving
public class ProtobufRpcEngine implements RpcEngine
{
    public static final Logger LOG;
    private static final ThreadLocal<AsyncGet<Message, Exception>> ASYNC_RETURN_MESSAGE;
    private static final ClientCache CLIENTS;
    
    @InterfaceStability.Unstable
    public static AsyncGet<Message, Exception> getAsyncReturnMessage() {
        return ProtobufRpcEngine.ASYNC_RETURN_MESSAGE.get();
    }
    
    public <T> ProtocolProxy<T> getProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory, final int rpcTimeout) throws IOException {
        return this.getProxy(protocol, clientVersion, addr, ticket, conf, factory, rpcTimeout, null);
    }
    
    @Override
    public <T> ProtocolProxy<T> getProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory, final int rpcTimeout, final RetryPolicy connectionRetryPolicy) throws IOException {
        return this.getProxy(protocol, clientVersion, addr, ticket, conf, factory, rpcTimeout, connectionRetryPolicy, null);
    }
    
    @Override
    public <T> ProtocolProxy<T> getProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory, final int rpcTimeout, final RetryPolicy connectionRetryPolicy, final AtomicBoolean fallbackToSimpleAuth) throws IOException {
        final Invoker invoker = new Invoker((Class)protocol, addr, ticket, conf, factory, rpcTimeout, connectionRetryPolicy, fallbackToSimpleAuth);
        return new ProtocolProxy<T>(protocol, (T)Proxy.newProxyInstance(protocol.getClassLoader(), new Class[] { protocol }, invoker), false);
    }
    
    @Override
    public ProtocolProxy<ProtocolMetaInfoPB> getProtocolMetaInfoProxy(final Client.ConnectionId connId, final Configuration conf, final SocketFactory factory) throws IOException {
        final Class<ProtocolMetaInfoPB> protocol = ProtocolMetaInfoPB.class;
        return new ProtocolProxy<ProtocolMetaInfoPB>(protocol, (ProtocolMetaInfoPB)Proxy.newProxyInstance(protocol.getClassLoader(), new Class[] { protocol }, new Invoker((Class)protocol, connId, conf, factory)), false);
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @VisibleForTesting
    static Client getClient(final Configuration conf) {
        return ProtobufRpcEngine.CLIENTS.getClient(conf, SocketFactory.getDefault(), RpcWritable.Buffer.class);
    }
    
    @Override
    public RPC.Server getServer(final Class<?> protocol, final Object protocolImpl, final String bindAddress, final int port, final int numHandlers, final int numReaders, final int queueSizePerHandler, final boolean verbose, final Configuration conf, final SecretManager<? extends TokenIdentifier> secretManager, final String portRangeConfig) throws IOException {
        return new Server(protocol, protocolImpl, conf, bindAddress, port, numHandlers, numReaders, queueSizePerHandler, verbose, secretManager, portRangeConfig);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ProtobufRpcEngine.class);
        ASYNC_RETURN_MESSAGE = new ThreadLocal<AsyncGet<Message, Exception>>();
        org.apache.hadoop.ipc.Server.registerProtocolEngine(RPC.RpcKind.RPC_PROTOCOL_BUFFER, RpcProtobufRequest.class, new Server.ProtoBufRpcInvoker());
        CLIENTS = new ClientCache();
    }
    
    private static class Invoker implements RpcInvocationHandler
    {
        private final Map<String, Message> returnTypes;
        private boolean isClosed;
        private final Client.ConnectionId remoteId;
        private final Client client;
        private final long clientProtocolVersion;
        private final String protocolName;
        private AtomicBoolean fallbackToSimpleAuth;
        
        private Invoker(final Class<?> protocol, final InetSocketAddress addr, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory, final int rpcTimeout, final RetryPolicy connectionRetryPolicy, final AtomicBoolean fallbackToSimpleAuth) throws IOException {
            this(protocol, Client.ConnectionId.getConnectionId(addr, protocol, ticket, rpcTimeout, connectionRetryPolicy, conf), conf, factory);
            this.fallbackToSimpleAuth = fallbackToSimpleAuth;
        }
        
        private Invoker(final Class<?> protocol, final Client.ConnectionId connId, final Configuration conf, final SocketFactory factory) {
            this.returnTypes = new ConcurrentHashMap<String, Message>();
            this.isClosed = false;
            this.remoteId = connId;
            this.client = ProtobufRpcEngine.CLIENTS.getClient(conf, factory, RpcWritable.Buffer.class);
            this.protocolName = RPC.getProtocolName(protocol);
            this.clientProtocolVersion = RPC.getProtocolVersion(protocol);
        }
        
        private ProtobufRpcEngineProtos.RequestHeaderProto constructRpcRequestHeader(final Method method) {
            final ProtobufRpcEngineProtos.RequestHeaderProto.Builder builder = ProtobufRpcEngineProtos.RequestHeaderProto.newBuilder();
            builder.setMethodName(method.getName());
            builder.setDeclaringClassProtocolName(this.protocolName);
            builder.setClientProtocolVersion(this.clientProtocolVersion);
            return builder.build();
        }
        
        @Override
        public Message invoke(final Object proxy, final Method method, final Object[] args) throws ServiceException {
            long startTime = 0L;
            if (ProtobufRpcEngine.LOG.isDebugEnabled()) {
                startTime = Time.now();
            }
            if (args.length != 2) {
                throw new ServiceException("Too many or few parameters for request. Method: [" + method.getName() + "], Expected: 2, Actual: " + args.length);
            }
            if (args[1] == null) {
                throw new ServiceException("null param while calling Method: [" + method.getName() + "]");
            }
            final Tracer tracer = Tracer.curThreadTracer();
            TraceScope traceScope = null;
            if (tracer != null) {
                traceScope = tracer.newScope(RpcClientUtil.methodToTraceString(method));
            }
            final ProtobufRpcEngineProtos.RequestHeaderProto rpcRequestHeader = this.constructRpcRequestHeader(method);
            if (ProtobufRpcEngine.LOG.isTraceEnabled()) {
                ProtobufRpcEngine.LOG.trace(Thread.currentThread().getId() + ": Call -> " + this.remoteId + ": " + method.getName() + " {" + TextFormat.shortDebugString((MessageOrBuilder)args[1]) + "}");
            }
            final Message theRequest = (Message)args[1];
            RpcWritable.Buffer val;
            try {
                val = (RpcWritable.Buffer)this.client.call(RPC.RpcKind.RPC_PROTOCOL_BUFFER, new RpcProtobufRequest(rpcRequestHeader, theRequest), this.remoteId, this.fallbackToSimpleAuth);
            }
            catch (Throwable e) {
                if (ProtobufRpcEngine.LOG.isTraceEnabled()) {
                    ProtobufRpcEngine.LOG.trace(Thread.currentThread().getId() + ": Exception <- " + this.remoteId + ": " + method.getName() + " {" + e + "}");
                }
                if (traceScope != null) {
                    traceScope.addTimelineAnnotation("Call got exception: " + e.toString());
                }
                throw new ServiceException(e);
            }
            finally {
                if (traceScope != null) {
                    traceScope.close();
                }
            }
            if (ProtobufRpcEngine.LOG.isDebugEnabled()) {
                final long callTime = Time.now() - startTime;
                ProtobufRpcEngine.LOG.debug("Call: " + method.getName() + " took " + callTime + "ms");
            }
            if (Client.isAsynchronousMode()) {
                final AsyncGet<RpcWritable.Buffer, IOException> arr = Client.getAsyncRpcResponse();
                final AsyncGet<Message, Exception> asyncGet = new AsyncGet<Message, Exception>() {
                    @Override
                    public Message get(final long timeout, final TimeUnit unit) throws Exception {
                        return Invoker.this.getReturnMessage(method, arr.get(timeout, unit));
                    }
                    
                    @Override
                    public boolean isDone() {
                        return arr.isDone();
                    }
                };
                ProtobufRpcEngine.ASYNC_RETURN_MESSAGE.set(asyncGet);
                return null;
            }
            return this.getReturnMessage(method, val);
        }
        
        private Message getReturnMessage(final Method method, final RpcWritable.Buffer buf) throws ServiceException {
            Message prototype = null;
            try {
                prototype = this.getReturnProtoType(method);
            }
            catch (Exception e) {
                throw new ServiceException(e);
            }
            Message returnMessage;
            try {
                returnMessage = buf.getValue(prototype.getDefaultInstanceForType());
                if (ProtobufRpcEngine.LOG.isTraceEnabled()) {
                    ProtobufRpcEngine.LOG.trace(Thread.currentThread().getId() + ": Response <- " + this.remoteId + ": " + method.getName() + " {" + TextFormat.shortDebugString(returnMessage) + "}");
                }
            }
            catch (Throwable e2) {
                throw new ServiceException(e2);
            }
            return returnMessage;
        }
        
        @Override
        public void close() throws IOException {
            if (!this.isClosed) {
                this.isClosed = true;
                ProtobufRpcEngine.CLIENTS.stopClient(this.client);
            }
        }
        
        private Message getReturnProtoType(final Method method) throws Exception {
            if (this.returnTypes.containsKey(method.getName())) {
                return this.returnTypes.get(method.getName());
            }
            final Class<?> returnType = method.getReturnType();
            final Method newInstMethod = returnType.getMethod("getDefaultInstance", (Class<?>[])new Class[0]);
            newInstMethod.setAccessible(true);
            final Message prototype = (Message)newInstMethod.invoke(null, (Object[])null);
            this.returnTypes.put(method.getName(), prototype);
            return prototype;
        }
        
        @Override
        public Client.ConnectionId getConnectionId() {
            return this.remoteId;
        }
    }
    
    public static class Server extends RPC.Server
    {
        static final ThreadLocal<ProtobufRpcEngineCallback> currentCallback;
        static final ThreadLocal<CallInfo> currentCallInfo;
        
        @InterfaceStability.Unstable
        public static ProtobufRpcEngineCallback registerForDeferredResponse() {
            final ProtobufRpcEngineCallback callback = new ProtobufRpcEngineCallbackImpl();
            Server.currentCallback.set(callback);
            return callback;
        }
        
        public Server(final Class<?> protocolClass, final Object protocolImpl, final Configuration conf, final String bindAddress, final int port, final int numHandlers, final int numReaders, final int queueSizePerHandler, final boolean verbose, final SecretManager<? extends TokenIdentifier> secretManager, final String portRangeConfig) throws IOException {
            super(bindAddress, port, null, numHandlers, numReaders, queueSizePerHandler, conf, RPC.Server.serverNameFromClass(protocolImpl.getClass()), secretManager, portRangeConfig);
            this.verbose = verbose;
            this.registerProtocolAndImpl(RPC.RpcKind.RPC_PROTOCOL_BUFFER, protocolClass, protocolImpl);
        }
        
        static {
            currentCallback = new ThreadLocal<ProtobufRpcEngineCallback>();
            currentCallInfo = new ThreadLocal<CallInfo>();
        }
        
        static class CallInfo
        {
            private final RPC.Server server;
            private final String methodName;
            
            public CallInfo(final RPC.Server server, final String methodName) {
                this.server = server;
                this.methodName = methodName;
            }
        }
        
        static class ProtobufRpcEngineCallbackImpl implements ProtobufRpcEngineCallback
        {
            private final RPC.Server server;
            private final Call call;
            private final String methodName;
            private final long setupTime;
            
            public ProtobufRpcEngineCallbackImpl() {
                this.server = Server.currentCallInfo.get().server;
                this.call = org.apache.hadoop.ipc.Server.getCurCall().get();
                this.methodName = Server.currentCallInfo.get().methodName;
                this.setupTime = Time.now();
            }
            
            @Override
            public void setResponse(final Message message) {
                final long processingTime = Time.now() - this.setupTime;
                this.call.setDeferredResponse(RpcWritable.wrap(message));
                this.server.updateDeferredMetrics(this.methodName, processingTime);
            }
            
            @Override
            public void error(final Throwable t) {
                final long processingTime = Time.now() - this.setupTime;
                final String detailedMetricsName = t.getClass().getSimpleName();
                this.server.updateDeferredMetrics(detailedMetricsName, processingTime);
                this.call.setDeferredError(t);
            }
        }
        
        static class ProtoBufRpcInvoker implements RPC.RpcInvoker
        {
            private static ProtoClassProtoImpl getProtocolImpl(final RPC.Server server, final String protoName, final long clientVersion) throws RpcServerException {
                final ProtoNameVer pv = new ProtoNameVer(protoName, clientVersion);
                final ProtoClassProtoImpl impl = server.getProtocolImplMap(RPC.RpcKind.RPC_PROTOCOL_BUFFER).get(pv);
                if (impl != null) {
                    return impl;
                }
                final VerProtocolImpl highest = server.getHighestSupportedProtocol(RPC.RpcKind.RPC_PROTOCOL_BUFFER, protoName);
                if (highest == null) {
                    throw new RpcNoSuchProtocolException("Unknown protocol: " + protoName);
                }
                throw new RPC.VersionMismatch(protoName, clientVersion, highest.version);
            }
            
            @Override
            public Writable call(final RPC.Server server, final String connectionProtocolName, final Writable writableRequest, final long receiveTime) throws Exception {
                final RpcProtobufRequest request = (RpcProtobufRequest)writableRequest;
                final ProtobufRpcEngineProtos.RequestHeaderProto rpcRequest = request.getRequestHeader();
                final String methodName = rpcRequest.getMethodName();
                final String declaringClassProtoName = rpcRequest.getDeclaringClassProtocolName();
                final long clientVersion = rpcRequest.getClientProtocolVersion();
                if (server.verbose) {
                    Server.LOG.info("Call: connectionProtocolName=" + connectionProtocolName + ", method=" + methodName);
                }
                final ProtoClassProtoImpl protocolImpl = getProtocolImpl(server, declaringClassProtoName, clientVersion);
                final BlockingService service = (BlockingService)protocolImpl.protocolImpl;
                final Descriptors.MethodDescriptor methodDescriptor = service.getDescriptorForType().findMethodByName(methodName);
                if (methodDescriptor == null) {
                    final String msg = "Unknown method " + methodName + " called on " + connectionProtocolName + " protocol.";
                    Server.LOG.warn(msg);
                    throw new RpcNoSuchMethodException(msg);
                }
                final Message prototype = service.getRequestPrototype(methodDescriptor);
                final Message param = request.getValue(prototype);
                final long startTime = Time.now();
                final int qTime = (int)(startTime - receiveTime);
                Exception exception = null;
                boolean isDeferred = false;
                Message result;
                try {
                    server.rpcDetailedMetrics.init(protocolImpl.protocolClass);
                    ProtobufRpcEngine.Server.currentCallInfo.set(new CallInfo(server, methodName));
                    result = service.callBlockingMethod(methodDescriptor, null, param);
                    if (ProtobufRpcEngine.Server.currentCallback.get() != null) {
                        Server.getCurCall().get().deferResponse();
                        isDeferred = true;
                        ProtobufRpcEngine.Server.currentCallback.set(null);
                        return null;
                    }
                    return RpcWritable.wrap(result);
                }
                catch (ServiceException e) {
                    exception = (Exception)e.getCause();
                    throw (Exception)e.getCause();
                }
                catch (Exception e2) {
                    exception = e2;
                    throw e2;
                }
                finally {
                    ProtobufRpcEngine.Server.currentCallInfo.set(null);
                    final int processingTime = (int)(Time.now() - startTime);
                    if (Server.LOG.isDebugEnabled()) {
                        String msg2 = "Served: " + methodName + (isDeferred ? ", deferred" : "") + ", queueTime= " + qTime + " procesingTime= " + processingTime;
                        if (exception != null) {
                            msg2 = msg2 + " exception= " + exception.getClass().getSimpleName();
                        }
                        Server.LOG.debug(msg2);
                    }
                    final String detailedMetricsName = (exception == null) ? methodName : exception.getClass().getSimpleName();
                    server.updateMetrics(detailedMetricsName, qTime, processingTime, isDeferred);
                }
                return RpcWritable.wrap(result);
            }
        }
    }
    
    static class RpcProtobufRequest extends Buffer
    {
        private volatile ProtobufRpcEngineProtos.RequestHeaderProto requestHeader;
        private Message payload;
        
        public RpcProtobufRequest() {
        }
        
        RpcProtobufRequest(final ProtobufRpcEngineProtos.RequestHeaderProto header, final Message payload) {
            this.requestHeader = header;
            this.payload = payload;
        }
        
        ProtobufRpcEngineProtos.RequestHeaderProto getRequestHeader() throws IOException {
            if (this.getByteBuffer() != null && this.requestHeader == null) {
                this.requestHeader = this.getValue(ProtobufRpcEngineProtos.RequestHeaderProto.getDefaultInstance());
            }
            return this.requestHeader;
        }
        
        public void writeTo(final ResponseBuffer out) throws IOException {
            this.requestHeader.writeDelimitedTo(out);
            if (this.payload != null) {
                this.payload.writeDelimitedTo(out);
            }
        }
        
        @Override
        public String toString() {
            try {
                final ProtobufRpcEngineProtos.RequestHeaderProto header = this.getRequestHeader();
                return header.getDeclaringClassProtocolName() + "." + header.getMethodName();
            }
            catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
