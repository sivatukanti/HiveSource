// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.net.NetUtils;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.List;
import java.io.IOException;
import java.lang.reflect.Method;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.ipc.protobuf.ProtocolInfoProtos;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.protobuf.RpcController;

public class RpcClientUtil
{
    private static RpcController NULL_CONTROLLER;
    private static final int PRIME = 16777619;
    private static ConcurrentHashMap<ProtoSigCacheKey, Map<Long, ProtocolSignature>> signatureMap;
    
    private static void putVersionSignatureMap(final InetSocketAddress addr, final String protocol, final String rpcKind, final Map<Long, ProtocolSignature> map) {
        RpcClientUtil.signatureMap.put(new ProtoSigCacheKey(addr, protocol, rpcKind), map);
    }
    
    private static Map<Long, ProtocolSignature> getVersionSignatureMap(final InetSocketAddress addr, final String protocol, final String rpcKind) {
        return RpcClientUtil.signatureMap.get(new ProtoSigCacheKey(addr, protocol, rpcKind));
    }
    
    public static boolean isMethodSupported(final Object rpcProxy, final Class<?> protocol, final RPC.RpcKind rpcKind, final long version, final String methodName) throws IOException {
        final InetSocketAddress serverAddress = RPC.getServerAddress(rpcProxy);
        Map<Long, ProtocolSignature> versionMap = getVersionSignatureMap(serverAddress, protocol.getName(), rpcKind.toString());
        if (versionMap == null) {
            final Configuration conf = new Configuration();
            RPC.setProtocolEngine(conf, ProtocolMetaInfoPB.class, ProtobufRpcEngine.class);
            final ProtocolMetaInfoPB protocolInfoProxy = getProtocolMetaInfoProxy(rpcProxy, conf);
            final ProtocolInfoProtos.GetProtocolSignatureRequestProto.Builder builder = ProtocolInfoProtos.GetProtocolSignatureRequestProto.newBuilder();
            builder.setProtocol(protocol.getName());
            builder.setRpcKind(rpcKind.toString());
            ProtocolInfoProtos.GetProtocolSignatureResponseProto resp;
            try {
                resp = protocolInfoProxy.getProtocolSignature(RpcClientUtil.NULL_CONTROLLER, builder.build());
            }
            catch (ServiceException se) {
                throw ProtobufHelper.getRemoteException(se);
            }
            versionMap = convertProtocolSignatureProtos(resp.getProtocolSignatureList());
            putVersionSignatureMap(serverAddress, protocol.getName(), rpcKind.toString(), versionMap);
        }
        final Method[] allMethods = protocol.getMethods();
        Method desiredMethod = null;
        for (final Method m : allMethods) {
            if (m.getName().equals(methodName)) {
                desiredMethod = m;
                break;
            }
        }
        if (desiredMethod == null) {
            return false;
        }
        final int methodHash = ProtocolSignature.getFingerprint(desiredMethod);
        return methodExists(methodHash, version, versionMap);
    }
    
    private static Map<Long, ProtocolSignature> convertProtocolSignatureProtos(final List<ProtocolInfoProtos.ProtocolSignatureProto> protoList) {
        final Map<Long, ProtocolSignature> map = new TreeMap<Long, ProtocolSignature>();
        for (final ProtocolInfoProtos.ProtocolSignatureProto p : protoList) {
            final int[] methods = new int[p.getMethodsList().size()];
            int index = 0;
            for (final int m : p.getMethodsList()) {
                methods[index++] = m;
            }
            map.put(p.getVersion(), new ProtocolSignature(p.getVersion(), methods));
        }
        return map;
    }
    
    private static boolean methodExists(final int methodHash, final long version, final Map<Long, ProtocolSignature> versionMap) {
        final ProtocolSignature sig = versionMap.get(version);
        if (sig != null) {
            for (final int m : sig.getMethods()) {
                if (m == methodHash) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static ProtocolMetaInfoPB getProtocolMetaInfoProxy(final Object proxy, final Configuration conf) throws IOException {
        final RpcInvocationHandler inv = (RpcInvocationHandler)Proxy.getInvocationHandler(proxy);
        return RPC.getProtocolEngine(ProtocolMetaInfoPB.class, conf).getProtocolMetaInfoProxy(inv.getConnectionId(), conf, NetUtils.getDefaultSocketFactory(conf)).getProxy();
    }
    
    public static String methodToTraceString(final Method method) {
        Class<?> clazz = method.getDeclaringClass();
        while (true) {
            final Class<?> next = clazz.getEnclosingClass();
            if (next == null || next.getEnclosingClass() == null) {
                break;
            }
            clazz = next;
        }
        return clazz.getSimpleName() + "#" + method.getName();
    }
    
    public static String toTraceName(final String fullName) {
        final int lastPeriod = fullName.lastIndexOf(46);
        if (lastPeriod < 0) {
            return fullName;
        }
        final int secondLastPeriod = fullName.lastIndexOf(46, lastPeriod - 1);
        if (secondLastPeriod < 0) {
            return fullName;
        }
        return fullName.substring(secondLastPeriod + 1, lastPeriod) + "#" + fullName.substring(lastPeriod + 1);
    }
    
    static {
        RpcClientUtil.NULL_CONTROLLER = null;
        RpcClientUtil.signatureMap = new ConcurrentHashMap<ProtoSigCacheKey, Map<Long, ProtocolSignature>>();
    }
    
    private static class ProtoSigCacheKey
    {
        private InetSocketAddress serverAddress;
        private String protocol;
        private String rpcKind;
        
        ProtoSigCacheKey(final InetSocketAddress addr, final String p, final String rk) {
            this.serverAddress = addr;
            this.protocol = p;
            this.rpcKind = rk;
        }
        
        @Override
        public int hashCode() {
            int result = 1;
            result = 16777619 * result + ((this.serverAddress == null) ? 0 : this.serverAddress.hashCode());
            result = 16777619 * result + ((this.protocol == null) ? 0 : this.protocol.hashCode());
            result = 16777619 * result + ((this.rpcKind == null) ? 0 : this.rpcKind.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(final Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof ProtoSigCacheKey) {
                final ProtoSigCacheKey otherKey = (ProtoSigCacheKey)other;
                return this.serverAddress.equals(otherKey.serverAddress) && this.protocol.equals(otherKey.protocol) && this.rpcKind.equals(otherKey.rpcKind);
            }
            return false;
        }
    }
}
