// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.protocolPB;

import org.apache.hadoop.ipc.RpcClientUtil;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.ipc.ProtobufHelper;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.proto.RefreshCallQueueProtocolProtos;
import com.google.protobuf.RpcController;
import java.io.Closeable;
import org.apache.hadoop.ipc.RefreshCallQueueProtocol;
import org.apache.hadoop.ipc.ProtocolMetaInterface;

public class RefreshCallQueueProtocolClientSideTranslatorPB implements ProtocolMetaInterface, RefreshCallQueueProtocol, Closeable
{
    private static final RpcController NULL_CONTROLLER;
    private final RefreshCallQueueProtocolPB rpcProxy;
    private static final RefreshCallQueueProtocolProtos.RefreshCallQueueRequestProto VOID_REFRESH_CALL_QUEUE_REQUEST;
    
    public RefreshCallQueueProtocolClientSideTranslatorPB(final RefreshCallQueueProtocolPB rpcProxy) {
        this.rpcProxy = rpcProxy;
    }
    
    @Override
    public void close() throws IOException {
        RPC.stopProxy(this.rpcProxy);
    }
    
    @Override
    public void refreshCallQueue() throws IOException {
        try {
            this.rpcProxy.refreshCallQueue(RefreshCallQueueProtocolClientSideTranslatorPB.NULL_CONTROLLER, RefreshCallQueueProtocolClientSideTranslatorPB.VOID_REFRESH_CALL_QUEUE_REQUEST);
        }
        catch (ServiceException se) {
            throw ProtobufHelper.getRemoteException(se);
        }
    }
    
    @Override
    public boolean isMethodSupported(final String methodName) throws IOException {
        return RpcClientUtil.isMethodSupported(this.rpcProxy, RefreshCallQueueProtocolPB.class, RPC.RpcKind.RPC_PROTOCOL_BUFFER, RPC.getProtocolVersion(RefreshCallQueueProtocolPB.class), methodName);
    }
    
    static {
        NULL_CONTROLLER = null;
        VOID_REFRESH_CALL_QUEUE_REQUEST = RefreshCallQueueProtocolProtos.RefreshCallQueueRequestProto.newBuilder().build();
    }
}
