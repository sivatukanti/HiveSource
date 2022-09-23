// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import com.google.protobuf.BlockingService;
import org.apache.hadoop.ipc.protobuf.ProtocolInfoProtos;
import org.apache.hadoop.io.Writable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import java.lang.reflect.InvocationHandler;
import java.io.Closeable;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.lang.reflect.Proxy;
import org.apache.hadoop.security.SaslRpcServer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.net.SocketFactory;
import java.io.InterruptedIOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.io.retry.RetryPolicy;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "Common", "HDFS", "MapReduce", "Yarn" })
@InterfaceStability.Evolving
public class RPC
{
    static final int RPC_SERVICE_CLASS_DEFAULT = 0;
    static final Logger LOG;
    private static final Map<Class<?>, RpcEngine> PROTOCOL_ENGINES;
    private static final String ENGINE_PROP = "rpc.engine";
    
    static Class<?>[] getSuperInterfaces(final Class<?>[] childInterfaces) {
        final List<Class<?>> allInterfaces = new ArrayList<Class<?>>();
        for (final Class<?> childInterface : childInterfaces) {
            if (VersionedProtocol.class.isAssignableFrom(childInterface)) {
                allInterfaces.add(childInterface);
                allInterfaces.addAll(Arrays.asList(getSuperInterfaces(childInterface.getInterfaces())));
            }
            else {
                RPC.LOG.warn("Interface " + childInterface + " ignored because it does not extend VersionedProtocol");
            }
        }
        return allInterfaces.toArray(new Class[allInterfaces.size()]);
    }
    
    static Class<?>[] getProtocolInterfaces(final Class<?> protocol) {
        final Class<?>[] interfaces = protocol.getInterfaces();
        return getSuperInterfaces(interfaces);
    }
    
    public static String getProtocolName(final Class<?> protocol) {
        if (protocol == null) {
            return null;
        }
        final ProtocolInfo anno = protocol.getAnnotation(ProtocolInfo.class);
        return (anno == null) ? protocol.getName() : anno.protocolName();
    }
    
    public static long getProtocolVersion(final Class<?> protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("Null protocol");
        }
        final ProtocolInfo anno = protocol.getAnnotation(ProtocolInfo.class);
        if (anno != null) {
            final long version = anno.protocolVersion();
            if (version != -1L) {
                return version;
            }
        }
        try {
            final Field versionField = protocol.getField("versionID");
            versionField.setAccessible(true);
            return versionField.getLong(protocol);
        }
        catch (NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
        catch (IllegalAccessException ex2) {
            throw new RuntimeException(ex2);
        }
    }
    
    private RPC() {
    }
    
    public static void setProtocolEngine(final Configuration conf, final Class<?> protocol, final Class<?> engine) {
        conf.setClass("rpc.engine." + protocol.getName(), engine, RpcEngine.class);
    }
    
    static synchronized RpcEngine getProtocolEngine(final Class<?> protocol, final Configuration conf) {
        RpcEngine engine = RPC.PROTOCOL_ENGINES.get(protocol);
        if (engine == null) {
            final Class<?> impl = conf.getClass("rpc.engine." + protocol.getName(), WritableRpcEngine.class);
            engine = ReflectionUtils.newInstance(impl, conf);
            RPC.PROTOCOL_ENGINES.put(protocol, engine);
        }
        return engine;
    }
    
    public static <T> T waitForProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final Configuration conf) throws IOException {
        return waitForProtocolProxy(protocol, clientVersion, addr, conf).getProxy();
    }
    
    public static <T> ProtocolProxy<T> waitForProtocolProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final Configuration conf) throws IOException {
        return waitForProtocolProxy(protocol, clientVersion, addr, conf, Long.MAX_VALUE);
    }
    
    public static <T> T waitForProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final Configuration conf, final long connTimeout) throws IOException {
        return waitForProtocolProxy(protocol, clientVersion, addr, conf, connTimeout).getProxy();
    }
    
    public static <T> ProtocolProxy<T> waitForProtocolProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final Configuration conf, final long connTimeout) throws IOException {
        return waitForProtocolProxy(protocol, clientVersion, addr, conf, getRpcTimeout(conf), null, connTimeout);
    }
    
    public static <T> T waitForProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final Configuration conf, final int rpcTimeout, final long timeout) throws IOException {
        return waitForProtocolProxy(protocol, clientVersion, addr, conf, rpcTimeout, null, timeout).getProxy();
    }
    
    public static <T> ProtocolProxy<T> waitForProtocolProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final Configuration conf, final int rpcTimeout, final RetryPolicy connectionRetryPolicy, final long timeout) throws IOException {
        final long startTime = Time.now();
        while (true) {
            IOException ioe;
            try {
                return getProtocolProxy(protocol, clientVersion, addr, UserGroupInformation.getCurrentUser(), conf, NetUtils.getDefaultSocketFactory(conf), rpcTimeout, connectionRetryPolicy);
            }
            catch (ConnectException se) {
                RPC.LOG.info("Server at " + addr + " not available yet, Zzzzz...");
                ioe = se;
            }
            catch (SocketTimeoutException te) {
                RPC.LOG.info("Problem connecting to server: " + addr);
                ioe = te;
            }
            catch (NoRouteToHostException nrthe) {
                RPC.LOG.info("No route to host for server: " + addr);
                ioe = nrthe;
            }
            if (Time.now() - timeout >= startTime) {
                throw ioe;
            }
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedIOException("Interrupted waiting for the proxy");
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw (IOException)new InterruptedIOException("Interrupted waiting for the proxy").initCause(ioe);
            }
        }
    }
    
    public static <T> T getProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final Configuration conf, final SocketFactory factory) throws IOException {
        return getProtocolProxy(protocol, clientVersion, addr, conf, factory).getProxy();
    }
    
    public static <T> ProtocolProxy<T> getProtocolProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final Configuration conf, final SocketFactory factory) throws IOException {
        final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        return getProtocolProxy(protocol, clientVersion, addr, ugi, conf, factory);
    }
    
    public static <T> T getProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory) throws IOException {
        return getProtocolProxy(protocol, clientVersion, addr, ticket, conf, factory).getProxy();
    }
    
    public static <T> ProtocolProxy<T> getProtocolProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory) throws IOException {
        return getProtocolProxy(protocol, clientVersion, addr, ticket, conf, factory, getRpcTimeout(conf), null);
    }
    
    public static <T> T getProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory, final int rpcTimeout) throws IOException {
        return getProtocolProxy(protocol, clientVersion, addr, ticket, conf, factory, rpcTimeout, null).getProxy();
    }
    
    public static <T> ProtocolProxy<T> getProtocolProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory, final int rpcTimeout, final RetryPolicy connectionRetryPolicy) throws IOException {
        return getProtocolProxy(protocol, clientVersion, addr, ticket, conf, factory, rpcTimeout, connectionRetryPolicy, null);
    }
    
    public static <T> ProtocolProxy<T> getProtocolProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final UserGroupInformation ticket, final Configuration conf, final SocketFactory factory, final int rpcTimeout, final RetryPolicy connectionRetryPolicy, final AtomicBoolean fallbackToSimpleAuth) throws IOException {
        if (UserGroupInformation.isSecurityEnabled()) {
            SaslRpcServer.init(conf);
        }
        return getProtocolEngine(protocol, conf).getProxy(protocol, clientVersion, addr, ticket, conf, factory, rpcTimeout, connectionRetryPolicy, fallbackToSimpleAuth);
    }
    
    public static <T> T getProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final Configuration conf) throws IOException {
        return getProtocolProxy(protocol, clientVersion, addr, conf).getProxy();
    }
    
    public static InetSocketAddress getServerAddress(final Object proxy) {
        return getConnectionIdForProxy(proxy).getAddress();
    }
    
    public static Client.ConnectionId getConnectionIdForProxy(Object proxy) {
        if (proxy instanceof ProtocolTranslator) {
            proxy = ((ProtocolTranslator)proxy).getUnderlyingProxyObject();
        }
        final RpcInvocationHandler inv = (RpcInvocationHandler)Proxy.getInvocationHandler(proxy);
        return inv.getConnectionId();
    }
    
    public static <T> ProtocolProxy<T> getProtocolProxy(final Class<T> protocol, final long clientVersion, final InetSocketAddress addr, final Configuration conf) throws IOException {
        return getProtocolProxy(protocol, clientVersion, addr, conf, NetUtils.getDefaultSocketFactory(conf));
    }
    
    public static void stopProxy(final Object proxy) {
        if (proxy == null) {
            throw new HadoopIllegalArgumentException("Cannot close proxy since it is null");
        }
        try {
            if (proxy instanceof Closeable) {
                ((Closeable)proxy).close();
                return;
            }
            final InvocationHandler handler = Proxy.getInvocationHandler(proxy);
            if (handler instanceof Closeable) {
                ((Closeable)handler).close();
                return;
            }
        }
        catch (IOException e) {
            RPC.LOG.error("Closing proxy or invocation handler caused exception", e);
        }
        catch (IllegalArgumentException e2) {
            RPC.LOG.error("RPC.stopProxy called on non proxy: class=" + proxy.getClass().getName(), e2);
        }
        throw new HadoopIllegalArgumentException("Cannot close proxy - is not Closeable or does not provide closeable invocation handler " + proxy.getClass());
    }
    
    public static int getRpcTimeout(final Configuration conf) {
        return conf.getInt("ipc.client.rpc-timeout.ms", 0);
    }
    
    static {
        LOG = LoggerFactory.getLogger(RPC.class);
        PROTOCOL_ENGINES = new HashMap<Class<?>, RpcEngine>();
    }
    
    public enum RpcKind
    {
        RPC_BUILTIN((short)1), 
        RPC_WRITABLE((short)2), 
        RPC_PROTOCOL_BUFFER((short)3);
        
        static final short MAX_INDEX;
        private final short value;
        
        private RpcKind(final short val) {
            this.value = val;
        }
        
        static {
            MAX_INDEX = RpcKind.RPC_PROTOCOL_BUFFER.value;
        }
    }
    
    public static class VersionMismatch extends RpcServerException
    {
        private static final long serialVersionUID = 0L;
        private String interfaceName;
        private long clientVersion;
        private long serverVersion;
        
        public VersionMismatch(final String interfaceName, final long clientVersion, final long serverVersion) {
            super("Protocol " + interfaceName + " version mismatch. (client = " + clientVersion + ", server = " + serverVersion + ")");
            this.interfaceName = interfaceName;
            this.clientVersion = clientVersion;
            this.serverVersion = serverVersion;
        }
        
        public String getInterfaceName() {
            return this.interfaceName;
        }
        
        public long getClientVersion() {
            return this.clientVersion;
        }
        
        public long getServerVersion() {
            return this.serverVersion;
        }
        
        @Override
        public RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto getRpcStatusProto() {
            return RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.ERROR;
        }
        
        @Override
        public RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto getRpcErrorCodeProto() {
            return RpcHeaderProtos.RpcResponseHeaderProto.RpcErrorCodeProto.ERROR_RPC_VERSION_MISMATCH;
        }
    }
    
    public static class Builder
    {
        private Class<?> protocol;
        private Object instance;
        private String bindAddress;
        private int port;
        private int numHandlers;
        private int numReaders;
        private int queueSizePerHandler;
        private boolean verbose;
        private final Configuration conf;
        private SecretManager<? extends TokenIdentifier> secretManager;
        private String portRangeConfig;
        
        public Builder(final Configuration conf) {
            this.protocol = null;
            this.instance = null;
            this.bindAddress = "0.0.0.0";
            this.port = 0;
            this.numHandlers = 1;
            this.numReaders = -1;
            this.queueSizePerHandler = -1;
            this.verbose = false;
            this.secretManager = null;
            this.portRangeConfig = null;
            this.conf = conf;
        }
        
        public Builder setProtocol(final Class<?> protocol) {
            this.protocol = protocol;
            return this;
        }
        
        public Builder setInstance(final Object instance) {
            this.instance = instance;
            return this;
        }
        
        public Builder setBindAddress(final String bindAddress) {
            this.bindAddress = bindAddress;
            return this;
        }
        
        public Builder setPort(final int port) {
            this.port = port;
            return this;
        }
        
        public Builder setNumHandlers(final int numHandlers) {
            this.numHandlers = numHandlers;
            return this;
        }
        
        public Builder setnumReaders(final int numReaders) {
            this.numReaders = numReaders;
            return this;
        }
        
        public Builder setQueueSizePerHandler(final int queueSizePerHandler) {
            this.queueSizePerHandler = queueSizePerHandler;
            return this;
        }
        
        public Builder setVerbose(final boolean verbose) {
            this.verbose = verbose;
            return this;
        }
        
        public Builder setSecretManager(final SecretManager<? extends TokenIdentifier> secretManager) {
            this.secretManager = secretManager;
            return this;
        }
        
        public Builder setPortRangeConfig(final String portRangeConfig) {
            this.portRangeConfig = portRangeConfig;
            return this;
        }
        
        public Server build() throws IOException, HadoopIllegalArgumentException {
            if (this.conf == null) {
                throw new HadoopIllegalArgumentException("conf is not set");
            }
            if (this.protocol == null) {
                throw new HadoopIllegalArgumentException("protocol is not set");
            }
            if (this.instance == null) {
                throw new HadoopIllegalArgumentException("instance is not set");
            }
            return RPC.getProtocolEngine(this.protocol, this.conf).getServer(this.protocol, this.instance, this.bindAddress, this.port, this.numHandlers, this.numReaders, this.queueSizePerHandler, this.verbose, this.conf, this.secretManager, this.portRangeConfig);
        }
    }
    
    public abstract static class Server extends org.apache.hadoop.ipc.Server
    {
        boolean verbose;
        private static final Pattern COMPLEX_SERVER_NAME_PATTERN;
        ArrayList<Map<ProtoNameVer, ProtoClassProtoImpl>> protocolImplMapArray;
        
        static String serverNameFromClass(final Class<?> clazz) {
            String name = clazz.getName();
            final String[] names = clazz.getName().split("\\.", -1);
            if (names != null && names.length > 0) {
                name = names[names.length - 1];
            }
            final Matcher matcher = Server.COMPLEX_SERVER_NAME_PATTERN.matcher(name);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return name;
        }
        
        Map<ProtoNameVer, ProtoClassProtoImpl> getProtocolImplMap(final RpcKind rpcKind) {
            if (this.protocolImplMapArray.size() == 0) {
                for (int i = 0; i <= RpcKind.MAX_INDEX; ++i) {
                    this.protocolImplMapArray.add(new HashMap<ProtoNameVer, ProtoClassProtoImpl>(10));
                }
            }
            return this.protocolImplMapArray.get(rpcKind.ordinal());
        }
        
        void registerProtocolAndImpl(final RpcKind rpcKind, final Class<?> protocolClass, final Object protocolImpl) {
            final String protocolName = RPC.getProtocolName(protocolClass);
            long version;
            try {
                version = RPC.getProtocolVersion(protocolClass);
            }
            catch (Exception ex) {
                Server.LOG.warn("Protocol " + protocolClass + " NOT registered as cannot get protocol version ");
                return;
            }
            this.getProtocolImplMap(rpcKind).put(new ProtoNameVer(protocolName, version), new ProtoClassProtoImpl(protocolClass, protocolImpl));
            if (Server.LOG.isDebugEnabled()) {
                Server.LOG.debug("RpcKind = " + rpcKind + " Protocol Name = " + protocolName + " version=" + version + " ProtocolImpl=" + protocolImpl.getClass().getName() + " protocolClass=" + protocolClass.getName());
            }
        }
        
        VerProtocolImpl[] getSupportedProtocolVersions(final RpcKind rpcKind, final String protocolName) {
            final VerProtocolImpl[] resultk = new VerProtocolImpl[this.getProtocolImplMap(rpcKind).size()];
            int i = 0;
            for (final Map.Entry<ProtoNameVer, ProtoClassProtoImpl> pv : this.getProtocolImplMap(rpcKind).entrySet()) {
                if (pv.getKey().protocol.equals(protocolName)) {
                    resultk[i++] = new VerProtocolImpl(pv.getKey().version, pv.getValue());
                }
            }
            if (i == 0) {
                return null;
            }
            final VerProtocolImpl[] result = new VerProtocolImpl[i];
            System.arraycopy(resultk, 0, result, 0, i);
            return result;
        }
        
        VerProtocolImpl getHighestSupportedProtocol(final RpcKind rpcKind, final String protocolName) {
            Long highestVersion = 0L;
            ProtoClassProtoImpl highest = null;
            if (Server.LOG.isDebugEnabled()) {
                Server.LOG.debug("Size of protoMap for " + rpcKind + " =" + this.getProtocolImplMap(rpcKind).size());
            }
            for (final Map.Entry<ProtoNameVer, ProtoClassProtoImpl> pv : this.getProtocolImplMap(rpcKind).entrySet()) {
                if (pv.getKey().protocol.equals(protocolName) && (highest == null || pv.getKey().version > highestVersion)) {
                    highest = pv.getValue();
                    highestVersion = pv.getKey().version;
                }
            }
            if (highest == null) {
                return null;
            }
            return new VerProtocolImpl(highestVersion, highest);
        }
        
        protected Server(final String bindAddress, final int port, final Class<? extends Writable> paramClass, final int handlerCount, final int numReaders, final int queueSizePerHandler, final Configuration conf, final String serverName, final SecretManager<? extends TokenIdentifier> secretManager, final String portRangeConfig) throws IOException {
            super(bindAddress, port, paramClass, handlerCount, numReaders, queueSizePerHandler, conf, serverName, secretManager, portRangeConfig);
            this.protocolImplMapArray = new ArrayList<Map<ProtoNameVer, ProtoClassProtoImpl>>(RpcKind.MAX_INDEX);
            this.initProtocolMetaInfo(conf);
        }
        
        private void initProtocolMetaInfo(final Configuration conf) {
            RPC.setProtocolEngine(conf, ProtocolMetaInfoPB.class, ProtobufRpcEngine.class);
            final ProtocolMetaInfoServerSideTranslatorPB xlator = new ProtocolMetaInfoServerSideTranslatorPB(this);
            final BlockingService protocolInfoBlockingService = ProtocolInfoProtos.ProtocolInfoService.newReflectiveBlockingService(xlator);
            this.addProtocol(RpcKind.RPC_PROTOCOL_BUFFER, ProtocolMetaInfoPB.class, protocolInfoBlockingService);
        }
        
        public Server addProtocol(final RpcKind rpcKind, final Class<?> protocolClass, final Object protocolImpl) {
            this.registerProtocolAndImpl(rpcKind, protocolClass, protocolImpl);
            return this;
        }
        
        @Override
        public Writable call(final RpcKind rpcKind, final String protocol, final Writable rpcRequest, final long receiveTime) throws Exception {
            return org.apache.hadoop.ipc.Server.getRpcInvoker(rpcKind).call(this, protocol, rpcRequest, receiveTime);
        }
        
        static {
            COMPLEX_SERVER_NAME_PATTERN = Pattern.compile("(?:[^\\$]*\\$)*([A-Za-z][^\\$]+)(?:\\$\\d+)?");
        }
        
        static class ProtoNameVer
        {
            final String protocol;
            final long version;
            
            ProtoNameVer(final String protocol, final long ver) {
                this.protocol = protocol;
                this.version = ver;
            }
            
            @Override
            public boolean equals(final Object o) {
                if (o == null) {
                    return false;
                }
                if (this == o) {
                    return true;
                }
                if (!(o instanceof ProtoNameVer)) {
                    return false;
                }
                final ProtoNameVer pv = (ProtoNameVer)o;
                return pv.protocol.equals(this.protocol) && pv.version == this.version;
            }
            
            @Override
            public int hashCode() {
                return this.protocol.hashCode() * 37 + (int)this.version;
            }
        }
        
        static class ProtoClassProtoImpl
        {
            final Class<?> protocolClass;
            final Object protocolImpl;
            
            ProtoClassProtoImpl(final Class<?> protocolClass, final Object protocolImpl) {
                this.protocolClass = protocolClass;
                this.protocolImpl = protocolImpl;
            }
        }
        
        static class VerProtocolImpl
        {
            final long version;
            final ProtoClassProtoImpl protocolTarget;
            
            VerProtocolImpl(final long ver, final ProtoClassProtoImpl protocolTarget) {
                this.version = ver;
                this.protocolTarget = protocolTarget;
            }
        }
    }
    
    interface RpcInvoker
    {
        Writable call(final Server p0, final String p1, final Writable p2, final long p3) throws Exception;
    }
}
