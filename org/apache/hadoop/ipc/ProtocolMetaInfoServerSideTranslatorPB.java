// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import com.google.protobuf.ServiceException;
import org.apache.hadoop.ipc.protobuf.ProtocolInfoProtos;
import com.google.protobuf.RpcController;

public class ProtocolMetaInfoServerSideTranslatorPB implements ProtocolMetaInfoPB
{
    RPC.Server server;
    
    public ProtocolMetaInfoServerSideTranslatorPB(final RPC.Server server) {
        this.server = server;
    }
    
    @Override
    public ProtocolInfoProtos.GetProtocolVersionsResponseProto getProtocolVersions(final RpcController controller, final ProtocolInfoProtos.GetProtocolVersionsRequestProto request) throws ServiceException {
        final String protocol = request.getProtocol();
        final ProtocolInfoProtos.GetProtocolVersionsResponseProto.Builder builder = ProtocolInfoProtos.GetProtocolVersionsResponseProto.newBuilder();
        for (final RPC.RpcKind r : RPC.RpcKind.values()) {
            long[] versions;
            try {
                versions = this.getProtocolVersionForRpcKind(r, protocol);
            }
            catch (ClassNotFoundException e) {
                throw new ServiceException(e);
            }
            final ProtocolInfoProtos.ProtocolVersionProto.Builder b = ProtocolInfoProtos.ProtocolVersionProto.newBuilder();
            if (versions != null) {
                b.setRpcKind(r.toString());
                for (final long v : versions) {
                    b.addVersions(v);
                }
            }
            builder.addProtocolVersions(b.build());
        }
        return builder.build();
    }
    
    @Override
    public ProtocolInfoProtos.GetProtocolSignatureResponseProto getProtocolSignature(final RpcController controller, final ProtocolInfoProtos.GetProtocolSignatureRequestProto request) throws ServiceException {
        final ProtocolInfoProtos.GetProtocolSignatureResponseProto.Builder builder = ProtocolInfoProtos.GetProtocolSignatureResponseProto.newBuilder();
        final String protocol = request.getProtocol();
        final String rpcKind = request.getRpcKind();
        long[] versions;
        try {
            versions = this.getProtocolVersionForRpcKind(RPC.RpcKind.valueOf(rpcKind), protocol);
        }
        catch (ClassNotFoundException e1) {
            throw new ServiceException(e1);
        }
        if (versions == null) {
            return builder.build();
        }
        for (final long v : versions) {
            final ProtocolInfoProtos.ProtocolSignatureProto.Builder sigBuilder = ProtocolInfoProtos.ProtocolSignatureProto.newBuilder();
            sigBuilder.setVersion(v);
            try {
                final ProtocolSignature signature = ProtocolSignature.getProtocolSignature(protocol, v);
                for (final int m : signature.getMethods()) {
                    sigBuilder.addMethods(m);
                }
            }
            catch (ClassNotFoundException e2) {
                throw new ServiceException(e2);
            }
            builder.addProtocolSignature(sigBuilder.build());
        }
        return builder.build();
    }
    
    private long[] getProtocolVersionForRpcKind(final RPC.RpcKind rpcKind, final String protocol) throws ClassNotFoundException {
        final Class<?> protocolClass = Class.forName(protocol);
        final String protocolName = RPC.getProtocolName(protocolClass);
        final RPC.Server.VerProtocolImpl[] vers = this.server.getSupportedProtocolVersions(rpcKind, protocolName);
        if (vers == null) {
            return null;
        }
        final long[] versions = new long[vers.length];
        for (int i = 0; i < versions.length; ++i) {
            versions[i] = vers[i].version;
        }
        return versions;
    }
}
