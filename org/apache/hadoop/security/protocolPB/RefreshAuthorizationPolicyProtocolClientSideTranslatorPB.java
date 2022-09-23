// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.protocolPB;

import org.apache.hadoop.ipc.RpcClientUtil;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.ipc.ProtobufHelper;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.security.proto.RefreshAuthorizationPolicyProtocolProtos;
import com.google.protobuf.RpcController;
import java.io.Closeable;
import org.apache.hadoop.security.authorize.RefreshAuthorizationPolicyProtocol;
import org.apache.hadoop.ipc.ProtocolMetaInterface;

public class RefreshAuthorizationPolicyProtocolClientSideTranslatorPB implements ProtocolMetaInterface, RefreshAuthorizationPolicyProtocol, Closeable
{
    private static final RpcController NULL_CONTROLLER;
    private final RefreshAuthorizationPolicyProtocolPB rpcProxy;
    private static final RefreshAuthorizationPolicyProtocolProtos.RefreshServiceAclRequestProto VOID_REFRESH_SERVICE_ACL_REQUEST;
    
    public RefreshAuthorizationPolicyProtocolClientSideTranslatorPB(final RefreshAuthorizationPolicyProtocolPB rpcProxy) {
        this.rpcProxy = rpcProxy;
    }
    
    @Override
    public void close() throws IOException {
        RPC.stopProxy(this.rpcProxy);
    }
    
    @Override
    public void refreshServiceAcl() throws IOException {
        try {
            this.rpcProxy.refreshServiceAcl(RefreshAuthorizationPolicyProtocolClientSideTranslatorPB.NULL_CONTROLLER, RefreshAuthorizationPolicyProtocolClientSideTranslatorPB.VOID_REFRESH_SERVICE_ACL_REQUEST);
        }
        catch (ServiceException se) {
            throw ProtobufHelper.getRemoteException(se);
        }
    }
    
    @Override
    public boolean isMethodSupported(final String methodName) throws IOException {
        return RpcClientUtil.isMethodSupported(this.rpcProxy, RefreshAuthorizationPolicyProtocolPB.class, RPC.RpcKind.RPC_PROTOCOL_BUFFER, RPC.getProtocolVersion(RefreshAuthorizationPolicyProtocolPB.class), methodName);
    }
    
    static {
        NULL_CONTROLLER = null;
        VOID_REFRESH_SERVICE_ACL_REQUEST = RefreshAuthorizationPolicyProtocolProtos.RefreshServiceAclRequestProto.newBuilder().build();
    }
}
