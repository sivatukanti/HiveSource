// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tools.protocolPB;

import org.apache.hadoop.ipc.RpcClientUtil;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.ipc.ProtobufHelper;
import org.apache.hadoop.tools.proto.GetUserMappingsProtocolProtos;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import com.google.protobuf.RpcController;
import java.io.Closeable;
import org.apache.hadoop.tools.GetUserMappingsProtocol;
import org.apache.hadoop.ipc.ProtocolMetaInterface;

public class GetUserMappingsProtocolClientSideTranslatorPB implements ProtocolMetaInterface, GetUserMappingsProtocol, Closeable
{
    private static final RpcController NULL_CONTROLLER;
    private final GetUserMappingsProtocolPB rpcProxy;
    
    public GetUserMappingsProtocolClientSideTranslatorPB(final GetUserMappingsProtocolPB rpcProxy) {
        this.rpcProxy = rpcProxy;
    }
    
    @Override
    public void close() throws IOException {
        RPC.stopProxy(this.rpcProxy);
    }
    
    @Override
    public String[] getGroupsForUser(final String user) throws IOException {
        final GetUserMappingsProtocolProtos.GetGroupsForUserRequestProto request = GetUserMappingsProtocolProtos.GetGroupsForUserRequestProto.newBuilder().setUser(user).build();
        GetUserMappingsProtocolProtos.GetGroupsForUserResponseProto resp;
        try {
            resp = this.rpcProxy.getGroupsForUser(GetUserMappingsProtocolClientSideTranslatorPB.NULL_CONTROLLER, request);
        }
        catch (ServiceException se) {
            throw ProtobufHelper.getRemoteException(se);
        }
        return resp.getGroupsList().toArray(new String[resp.getGroupsCount()]);
    }
    
    @Override
    public boolean isMethodSupported(final String methodName) throws IOException {
        return RpcClientUtil.isMethodSupported(this.rpcProxy, GetUserMappingsProtocolPB.class, RPC.RpcKind.RPC_PROTOCOL_BUFFER, RPC.getProtocolVersion(GetUserMappingsProtocolPB.class), methodName);
    }
    
    static {
        NULL_CONTROLLER = null;
    }
}
