// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.lang.reflect.InvocationTargetException;
import org.apache.htrace.core.TraceScope;
import org.apache.htrace.core.Tracer;
import org.apache.hadoop.util.Time;
import java.io.DataOutput;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.UTF8;
import java.io.DataInput;
import java.lang.reflect.Method;
import org.apache.hadoop.conf.Configurable;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.io.retry.RetryPolicy;
import javax.net.SocketFactory;
import org.apache.hadoop.security.UserGroupInformation;
import java.net.InetSocketAddress;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Evolving
@Deprecated
public class WritableRpcEngine implements RpcEngine
{
    private static final Logger LOG;
    public static final long writableRpcVersion = 2L;
    private static boolean isInitialized;
    private static ClientCache CLIENTS;
    
    public static synchronized void ensureInitialized() {
        if (!WritableRpcEngine.isInitialized) {
            initialize();
        }
    }
    
    private static synchronized void initialize() {
        org.apache.hadoop.ipc.Server.registerProtocolEngine(RPC.RpcKind.RPC_WRITABLE, Invocation.class, new Server.WritableRpcInvoker());
        WritableRpcEngine.isInitialized = true;
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    static Client getClient(final Configuration conf) {
        return WritableRpcEngine.CLIENTS.getClient(conf);
    }
    
    @Override
    public <T> ProtocolProxy<T> getProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory, final int rpcTimeout, final RetryPolicy connectionRetryPolicy) throws IOException {
        return this.getProxy(protocol, clientVersion, addr, ticket, conf, factory, rpcTimeout, connectionRetryPolicy, null);
    }
    
    @Override
    public <T> ProtocolProxy<T> getProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory, final int rpcTimeout, final RetryPolicy connectionRetryPolicy, final AtomicBoolean fallbackToSimpleAuth) throws IOException {
        if (connectionRetryPolicy != null) {
            throw new UnsupportedOperationException("Not supported: connectionRetryPolicy=" + connectionRetryPolicy);
        }
        final T proxy = (T)Proxy.newProxyInstance(protocol.getClassLoader(), new Class[] { protocol }, new Invoker(protocol, addr, ticket, conf, factory, rpcTimeout, fallbackToSimpleAuth));
        return new ProtocolProxy<T>(protocol, proxy, true);
    }
    
    @Override
    public RPC.Server getServer(final Class<?> protocolClass, final Object protocolImpl, final String bindAddress, final int port, final int numHandlers, final int numReaders, final int queueSizePerHandler, final boolean verbose, final Configuration conf, final SecretManager<? extends TokenIdentifier> secretManager, final String portRangeConfig) throws IOException {
        return new Server(protocolClass, protocolImpl, conf, bindAddress, port, numHandlers, numReaders, queueSizePerHandler, verbose, secretManager, portRangeConfig);
    }
    
    @Override
    public ProtocolProxy<ProtocolMetaInfoPB> getProtocolMetaInfoProxy(final Client.ConnectionId connId, final Configuration conf, final SocketFactory factory) throws IOException {
        throw new UnsupportedOperationException("This proxy is not supported");
    }
    
    static {
        LOG = LoggerFactory.getLogger(RPC.class);
        WritableRpcEngine.isInitialized = false;
        ensureInitialized();
        WritableRpcEngine.CLIENTS = new ClientCache();
    }
    
    private static class Invocation implements Writable, Configurable
    {
        private String methodName;
        private Class<?>[] parameterClasses;
        private Object[] parameters;
        private Configuration conf;
        private long clientVersion;
        private int clientMethodsHash;
        private String declaringClassProtocolName;
        private long rpcVersion;
        
        public Invocation() {
        }
        
        public Invocation(final Method method, final Object[] parameters) {
            this.methodName = method.getName();
            this.parameterClasses = method.getParameterTypes();
            this.parameters = parameters;
            this.rpcVersion = 2L;
            if (method.getDeclaringClass().equals(VersionedProtocol.class)) {
                this.clientVersion = 0L;
                this.clientMethodsHash = 0;
            }
            else {
                this.clientVersion = RPC.getProtocolVersion(method.getDeclaringClass());
                this.clientMethodsHash = ProtocolSignature.getFingerprint(method.getDeclaringClass().getMethods());
            }
            this.declaringClassProtocolName = RPC.getProtocolName(method.getDeclaringClass());
        }
        
        public String getMethodName() {
            return this.methodName;
        }
        
        public Class<?>[] getParameterClasses() {
            return this.parameterClasses;
        }
        
        public Object[] getParameters() {
            return this.parameters;
        }
        
        private long getProtocolVersion() {
            return this.clientVersion;
        }
        
        private int getClientMethodsHash() {
            return this.clientMethodsHash;
        }
        
        public long getRpcVersion() {
            return this.rpcVersion;
        }
        
        @Override
        public void readFields(final DataInput in) throws IOException {
            this.rpcVersion = in.readLong();
            this.declaringClassProtocolName = UTF8.readString(in);
            this.methodName = UTF8.readString(in);
            this.clientVersion = in.readLong();
            this.clientMethodsHash = in.readInt();
            this.parameters = new Object[in.readInt()];
            this.parameterClasses = (Class<?>[])new Class[this.parameters.length];
            final ObjectWritable objectWritable = new ObjectWritable();
            for (int i = 0; i < this.parameters.length; ++i) {
                this.parameters[i] = ObjectWritable.readObject(in, objectWritable, this.conf);
                this.parameterClasses[i] = (Class<?>)objectWritable.getDeclaredClass();
            }
        }
        
        @Override
        public void write(final DataOutput out) throws IOException {
            out.writeLong(this.rpcVersion);
            UTF8.writeString(out, this.declaringClassProtocolName);
            UTF8.writeString(out, this.methodName);
            out.writeLong(this.clientVersion);
            out.writeInt(this.clientMethodsHash);
            out.writeInt(this.parameterClasses.length);
            for (int i = 0; i < this.parameterClasses.length; ++i) {
                ObjectWritable.writeObject(out, this.parameters[i], this.parameterClasses[i], this.conf, true);
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder();
            buffer.append(this.methodName);
            buffer.append("(");
            for (int i = 0; i < this.parameters.length; ++i) {
                if (i != 0) {
                    buffer.append(", ");
                }
                buffer.append(this.parameters[i]);
            }
            buffer.append(")");
            buffer.append(", rpc version=" + this.rpcVersion);
            buffer.append(", client version=" + this.clientVersion);
            buffer.append(", methodsFingerPrint=" + this.clientMethodsHash);
            return buffer.toString();
        }
        
        @Override
        public void setConf(final Configuration conf) {
            this.conf = conf;
        }
        
        @Override
        public Configuration getConf() {
            return this.conf;
        }
    }
    
    private static class Invoker implements RpcInvocationHandler
    {
        private Client.ConnectionId remoteId;
        private Client client;
        private boolean isClosed;
        private final AtomicBoolean fallbackToSimpleAuth;
        
        public Invoker(final Class<?> protocol, final InetSocketAddress address, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory, final int rpcTimeout, final AtomicBoolean fallbackToSimpleAuth) throws IOException {
            this.isClosed = false;
            this.remoteId = Client.ConnectionId.getConnectionId(address, protocol, ticket, rpcTimeout, null, conf);
            this.client = WritableRpcEngine.CLIENTS.getClient(conf, factory);
            this.fallbackToSimpleAuth = fallbackToSimpleAuth;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            long startTime = 0L;
            if (WritableRpcEngine.LOG.isDebugEnabled()) {
                startTime = Time.monotonicNow();
            }
            final Tracer tracer = Tracer.curThreadTracer();
            TraceScope traceScope = null;
            if (tracer != null) {
                traceScope = tracer.newScope(RpcClientUtil.methodToTraceString(method));
            }
            ObjectWritable value;
            try {
                value = (ObjectWritable)this.client.call(RPC.RpcKind.RPC_WRITABLE, new Invocation(method, args), this.remoteId, this.fallbackToSimpleAuth);
            }
            finally {
                if (traceScope != null) {
                    traceScope.close();
                }
            }
            if (WritableRpcEngine.LOG.isDebugEnabled()) {
                final long callTime = Time.monotonicNow() - startTime;
                WritableRpcEngine.LOG.debug("Call: " + method.getName() + " " + callTime);
            }
            return value.get();
        }
        
        @Override
        public synchronized void close() {
            if (!this.isClosed) {
                this.isClosed = true;
                WritableRpcEngine.CLIENTS.stopClient(this.client);
            }
        }
        
        @Override
        public Client.ConnectionId getConnectionId() {
            return this.remoteId;
        }
    }
    
    @Deprecated
    public static class Server extends RPC.Server
    {
        @Deprecated
        public Server(final Object instance, final Configuration conf, final String bindAddress, final int port) throws IOException {
            this(null, instance, conf, bindAddress, port);
        }
        
        public Server(final Class<?> protocolClass, final Object protocolImpl, final Configuration conf, final String bindAddress, final int port) throws IOException {
            this(protocolClass, protocolImpl, conf, bindAddress, port, 1, -1, -1, false, null, null);
        }
        
        @Deprecated
        public Server(final Object protocolImpl, final Configuration conf, final String bindAddress, final int port, final int numHandlers, final int numReaders, final int queueSizePerHandler, final boolean verbose, final SecretManager<? extends TokenIdentifier> secretManager) throws IOException {
            this(null, protocolImpl, conf, bindAddress, port, numHandlers, numReaders, queueSizePerHandler, verbose, secretManager, null);
        }
        
        public Server(final Class<?> protocolClass, final Object protocolImpl, final Configuration conf, final String bindAddress, final int port, final int numHandlers, final int numReaders, final int queueSizePerHandler, final boolean verbose, final SecretManager<? extends TokenIdentifier> secretManager, final String portRangeConfig) throws IOException {
            super(bindAddress, port, null, numHandlers, numReaders, queueSizePerHandler, conf, RPC.Server.serverNameFromClass(protocolImpl.getClass()), secretManager, portRangeConfig);
            this.verbose = verbose;
            Class<?>[] protocols;
            if (protocolClass == null) {
                protocols = RPC.getProtocolInterfaces(protocolImpl.getClass());
            }
            else {
                if (!protocolClass.isAssignableFrom(protocolImpl.getClass())) {
                    throw new IOException("protocolClass " + protocolClass + " is not implemented by protocolImpl which is of class " + protocolImpl.getClass());
                }
                this.registerProtocolAndImpl(RPC.RpcKind.RPC_WRITABLE, protocolClass, protocolImpl);
                protocols = RPC.getProtocolInterfaces(protocolClass);
            }
            for (final Class<?> p : protocols) {
                if (!p.equals(VersionedProtocol.class)) {
                    this.registerProtocolAndImpl(RPC.RpcKind.RPC_WRITABLE, p, protocolImpl);
                }
            }
        }
        
        private static void log(String value) {
            if (value != null && value.length() > 55) {
                value = value.substring(0, 55) + "...";
            }
            Server.LOG.info(value);
        }
        
        @Deprecated
        static class WritableRpcInvoker implements RPC.RpcInvoker
        {
            @Override
            public Writable call(final RPC.Server server, final String protocolName, final Writable rpcRequest, final long receivedTime) throws IOException, RPC.VersionMismatch {
                final Invocation call = (Invocation)rpcRequest;
                if (server.verbose) {
                    log("Call: " + call);
                }
                if (call.getRpcVersion() != 2L) {
                    throw new RpcServerException("WritableRpc version mismatch, client side version=" + call.getRpcVersion() + ", server side version=" + 2L);
                }
                final long clientVersion = call.getProtocolVersion();
                ProtoClassProtoImpl protocolImpl;
                if (call.declaringClassProtocolName.equals(VersionedProtocol.class.getName())) {
                    final VerProtocolImpl highest = server.getHighestSupportedProtocol(RPC.RpcKind.RPC_WRITABLE, protocolName);
                    if (highest == null) {
                        throw new RpcServerException("Unknown protocol: " + protocolName);
                    }
                    protocolImpl = highest.protocolTarget;
                }
                else {
                    final String protoName = call.declaringClassProtocolName;
                    final ProtoNameVer pv = new ProtoNameVer(call.declaringClassProtocolName, clientVersion);
                    protocolImpl = server.getProtocolImplMap(RPC.RpcKind.RPC_WRITABLE).get(pv);
                    if (protocolImpl == null) {
                        final VerProtocolImpl highest2 = server.getHighestSupportedProtocol(RPC.RpcKind.RPC_WRITABLE, protoName);
                        if (highest2 == null) {
                            throw new RpcServerException("Unknown protocol: " + protoName);
                        }
                        throw new RPC.VersionMismatch(protoName, clientVersion, highest2.version);
                    }
                }
                final long startTime = Time.now();
                final int qTime = (int)(startTime - receivedTime);
                Exception exception = null;
                try {
                    final Method method = protocolImpl.protocolClass.getMethod(call.getMethodName(), call.getParameterClasses());
                    method.setAccessible(true);
                    server.rpcDetailedMetrics.init(protocolImpl.protocolClass);
                    final Object value = method.invoke(protocolImpl.protocolImpl, call.getParameters());
                    if (server.verbose) {
                        log("Return: " + value);
                    }
                    return new ObjectWritable(method.getReturnType(), value);
                }
                catch (InvocationTargetException e) {
                    final Throwable target = e.getTargetException();
                    if (target instanceof IOException) {
                        exception = (IOException)target;
                        throw (IOException)target;
                    }
                    final IOException ioe = new IOException(target.toString());
                    ioe.setStackTrace(target.getStackTrace());
                    exception = ioe;
                    throw ioe;
                }
                catch (Throwable e2) {
                    if (!(e2 instanceof IOException)) {
                        Server.LOG.error("Unexpected throwable object ", e2);
                    }
                    final IOException ioe2 = new IOException(e2.toString());
                    ioe2.setStackTrace(e2.getStackTrace());
                    exception = ioe2;
                    throw ioe2;
                }
                finally {
                    final int processingTime = (int)(Time.now() - startTime);
                    if (Server.LOG.isDebugEnabled()) {
                        String msg = "Served: " + call.getMethodName() + " queueTime= " + qTime + " procesingTime= " + processingTime;
                        if (exception != null) {
                            msg = msg + " exception= " + exception.getClass().getSimpleName();
                        }
                        Server.LOG.debug(msg);
                    }
                    final String detailedMetricsName = (exception == null) ? call.getMethodName() : exception.getClass().getSimpleName();
                    server.updateMetrics(detailedMetricsName, qTime, processingTime, false);
                }
            }
        }
    }
}
