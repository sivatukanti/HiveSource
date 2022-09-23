// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.factories.impl.pb;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import java.io.IOException;
import com.google.protobuf.BlockingService;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.factories.RpcServerFactory;

@InterfaceAudience.Private
public class RpcServerFactoryPBImpl implements RpcServerFactory
{
    private static final Log LOG;
    private static final String PROTO_GEN_PACKAGE_NAME = "org.apache.hadoop.yarn.proto";
    private static final String PROTO_GEN_CLASS_SUFFIX = "Service";
    private static final String PB_IMPL_PACKAGE_SUFFIX = "impl.pb.service";
    private static final String PB_IMPL_CLASS_SUFFIX = "PBServiceImpl";
    private static final RpcServerFactoryPBImpl self;
    private Configuration localConf;
    private ConcurrentMap<Class<?>, Constructor<?>> serviceCache;
    private ConcurrentMap<Class<?>, Method> protoCache;
    
    public static RpcServerFactoryPBImpl get() {
        return RpcServerFactoryPBImpl.self;
    }
    
    private RpcServerFactoryPBImpl() {
        this.localConf = new Configuration();
        this.serviceCache = new ConcurrentHashMap<Class<?>, Constructor<?>>();
        this.protoCache = new ConcurrentHashMap<Class<?>, Method>();
    }
    
    public Server getServer(final Class<?> protocol, final Object instance, final InetSocketAddress addr, final Configuration conf, final SecretManager<? extends TokenIdentifier> secretManager, final int numHandlers) {
        return this.getServer(protocol, instance, addr, conf, secretManager, numHandlers, null);
    }
    
    @Override
    public Server getServer(final Class<?> protocol, final Object instance, final InetSocketAddress addr, final Configuration conf, final SecretManager<? extends TokenIdentifier> secretManager, final int numHandlers, final String portRangeConfig) {
        Constructor<?> constructor = this.serviceCache.get(protocol);
        if (constructor == null) {
            Class<?> pbServiceImplClazz = null;
            try {
                pbServiceImplClazz = this.localConf.getClassByName(this.getPbServiceImplClassName(protocol));
            }
            catch (ClassNotFoundException e) {
                throw new YarnRuntimeException("Failed to load class: [" + this.getPbServiceImplClassName(protocol) + "]", e);
            }
            try {
                constructor = pbServiceImplClazz.getConstructor(protocol);
                constructor.setAccessible(true);
                this.serviceCache.putIfAbsent(protocol, constructor);
            }
            catch (NoSuchMethodException e2) {
                throw new YarnRuntimeException("Could not find constructor with params: " + Long.TYPE + ", " + InetSocketAddress.class + ", " + Configuration.class, e2);
            }
        }
        Object service = null;
        try {
            service = constructor.newInstance(instance);
        }
        catch (InvocationTargetException e3) {
            throw new YarnRuntimeException(e3);
        }
        catch (IllegalAccessException e4) {
            throw new YarnRuntimeException(e4);
        }
        catch (InstantiationException e5) {
            throw new YarnRuntimeException(e5);
        }
        final Class<?> pbProtocol = service.getClass().getInterfaces()[0];
        Method method = this.protoCache.get(protocol);
        if (method == null) {
            Class<?> protoClazz = null;
            try {
                protoClazz = this.localConf.getClassByName(this.getProtoClassName(protocol));
            }
            catch (ClassNotFoundException e6) {
                throw new YarnRuntimeException("Failed to load class: [" + this.getProtoClassName(protocol) + "]", e6);
            }
            try {
                method = protoClazz.getMethod("newReflectiveBlockingService", pbProtocol.getInterfaces()[0]);
                method.setAccessible(true);
                this.protoCache.putIfAbsent(protocol, method);
            }
            catch (NoSuchMethodException e7) {
                throw new YarnRuntimeException(e7);
            }
        }
        try {
            return this.createServer(pbProtocol, addr, conf, secretManager, numHandlers, (BlockingService)method.invoke(null, service), portRangeConfig);
        }
        catch (InvocationTargetException e8) {
            throw new YarnRuntimeException(e8);
        }
        catch (IllegalAccessException e9) {
            throw new YarnRuntimeException(e9);
        }
        catch (IOException e10) {
            throw new YarnRuntimeException(e10);
        }
    }
    
    private String getProtoClassName(final Class<?> clazz) {
        final String srcClassName = this.getClassName(clazz);
        return "org.apache.hadoop.yarn.proto." + srcClassName + "$" + srcClassName + "Service";
    }
    
    private String getPbServiceImplClassName(final Class<?> clazz) {
        final String srcPackagePart = this.getPackageName(clazz);
        final String srcClassName = this.getClassName(clazz);
        final String destPackagePart = srcPackagePart + "." + "impl.pb.service";
        final String destClassPart = srcClassName + "PBServiceImpl";
        return destPackagePart + "." + destClassPart;
    }
    
    private String getClassName(final Class<?> clazz) {
        final String fqName = clazz.getName();
        return fqName.substring(fqName.lastIndexOf(".") + 1, fqName.length());
    }
    
    private String getPackageName(final Class<?> clazz) {
        return clazz.getPackage().getName();
    }
    
    private Server createServer(final Class<?> pbProtocol, final InetSocketAddress addr, final Configuration conf, final SecretManager<? extends TokenIdentifier> secretManager, final int numHandlers, final BlockingService blockingService, final String portRangeConfig) throws IOException {
        RPC.setProtocolEngine(conf, pbProtocol, ProtobufRpcEngine.class);
        final RPC.Server server = new RPC.Builder(conf).setProtocol(pbProtocol).setInstance(blockingService).setBindAddress(addr.getHostName()).setPort(addr.getPort()).setNumHandlers(numHandlers).setVerbose(false).setSecretManager(secretManager).setPortRangeConfig(portRangeConfig).build();
        RpcServerFactoryPBImpl.LOG.info("Adding protocol " + pbProtocol.getCanonicalName() + " to the server");
        server.addProtocol(RPC.RpcKind.RPC_PROTOCOL_BUFFER, pbProtocol, blockingService);
        return server;
    }
    
    static {
        LOG = LogFactory.getLog(RpcServerFactoryPBImpl.class);
        self = new RpcServerFactoryPBImpl();
    }
}
