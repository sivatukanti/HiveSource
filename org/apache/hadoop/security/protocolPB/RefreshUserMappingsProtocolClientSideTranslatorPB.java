// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.protocolPB;

import org.apache.hadoop.ipc.RpcClientUtil;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.ipc.ProtobufHelper;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.security.proto.RefreshUserMappingsProtocolProtos;
import com.google.protobuf.RpcController;
import java.io.Closeable;
import org.apache.hadoop.security.RefreshUserMappingsProtocol;
import org.apache.hadoop.ipc.ProtocolMetaInterface;

public class RefreshUserMappingsProtocolClientSideTranslatorPB implements ProtocolMetaInterface, RefreshUserMappingsProtocol, Closeable
{
    private static final RpcController NULL_CONTROLLER;
    private final RefreshUserMappingsProtocolPB rpcProxy;
    private static final RefreshUserMappingsProtocolProtos.RefreshUserToGroupsMappingsRequestProto VOID_REFRESH_USER_TO_GROUPS_MAPPING_REQUEST;
    private static final RefreshUserMappingsProtocolProtos.RefreshSuperUserGroupsConfigurationRequestProto VOID_REFRESH_SUPERUSER_GROUPS_CONFIGURATION_REQUEST;
    
    public RefreshUserMappingsProtocolClientSideTranslatorPB(final RefreshUserMappingsProtocolPB rpcProxy) {
        this.rpcProxy = rpcProxy;
    }
    
    @Override
    public void close() throws IOException {
        RPC.stopProxy(this.rpcProxy);
    }
    
    @Override
    public void refreshUserToGroupsMappings() throws IOException {
        try {
            this.rpcProxy.refreshUserToGroupsMappings(RefreshUserMappingsProtocolClientSideTranslatorPB.NULL_CONTROLLER, RefreshUserMappingsProtocolClientSideTranslatorPB.VOID_REFRESH_USER_TO_GROUPS_MAPPING_REQUEST);
        }
        catch (ServiceException se) {
            throw ProtobufHelper.getRemoteException(se);
        }
    }
    
    @Override
    public void refreshSuperUserGroupsConfiguration() throws IOException {
        try {
            this.rpcProxy.refreshSuperUserGroupsConfiguration(RefreshUserMappingsProtocolClientSideTranslatorPB.NULL_CONTROLLER, RefreshUserMappingsProtocolClientSideTranslatorPB.VOID_REFRESH_SUPERUSER_GROUPS_CONFIGURATION_REQUEST);
        }
        catch (ServiceException se) {
            throw ProtobufHelper.getRemoteException(se);
        }
    }
    
    @Override
    public boolean isMethodSupported(final String methodName) throws IOException {
        return RpcClientUtil.isMethodSupported(this.rpcProxy, RefreshUserMappingsProtocolPB.class, RPC.RpcKind.RPC_PROTOCOL_BUFFER, RPC.getProtocolVersion(RefreshUserMappingsProtocolPB.class), methodName);
    }
    
    static {
        NULL_CONTROLLER = null;
        VOID_REFRESH_USER_TO_GROUPS_MAPPING_REQUEST = RefreshUserMappingsProtocolProtos.RefreshUserToGroupsMappingsRequestProto.newBuilder().build();
        VOID_REFRESH_SUPERUSER_GROUPS_CONFIGURATION_REQUEST = RefreshUserMappingsProtocolProtos.RefreshSuperUserGroupsConfigurationRequestProto.newBuilder().build();
    }
}
