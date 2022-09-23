// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha.protocolPB;

import org.apache.hadoop.security.AccessControlException;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.ipc.ProtobufHelper;
import org.apache.hadoop.ha.proto.ZKFCProtocolProtos;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import javax.net.SocketFactory;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import com.google.protobuf.RpcController;
import org.apache.hadoop.ipc.ProtocolTranslator;
import java.io.Closeable;
import org.apache.hadoop.ha.ZKFCProtocol;

public class ZKFCProtocolClientSideTranslatorPB implements ZKFCProtocol, Closeable, ProtocolTranslator
{
    private static final RpcController NULL_CONTROLLER;
    private final ZKFCProtocolPB rpcProxy;
    
    public ZKFCProtocolClientSideTranslatorPB(final InetSocketAddress addr, final Configuration conf, final SocketFactory socketFactory, final int timeout) throws IOException {
        RPC.setProtocolEngine(conf, ZKFCProtocolPB.class, ProtobufRpcEngine.class);
        this.rpcProxy = RPC.getProxy(ZKFCProtocolPB.class, RPC.getProtocolVersion(ZKFCProtocolPB.class), addr, UserGroupInformation.getCurrentUser(), conf, socketFactory, timeout);
    }
    
    @Override
    public void cedeActive(final int millisToCede) throws IOException, AccessControlException {
        try {
            final ZKFCProtocolProtos.CedeActiveRequestProto req = ZKFCProtocolProtos.CedeActiveRequestProto.newBuilder().setMillisToCede(millisToCede).build();
            this.rpcProxy.cedeActive(ZKFCProtocolClientSideTranslatorPB.NULL_CONTROLLER, req);
        }
        catch (ServiceException e) {
            throw ProtobufHelper.getRemoteException(e);
        }
    }
    
    @Override
    public void gracefulFailover() throws IOException, AccessControlException {
        try {
            this.rpcProxy.gracefulFailover(ZKFCProtocolClientSideTranslatorPB.NULL_CONTROLLER, ZKFCProtocolProtos.GracefulFailoverRequestProto.getDefaultInstance());
        }
        catch (ServiceException e) {
            throw ProtobufHelper.getRemoteException(e);
        }
    }
    
    @Override
    public void close() {
        RPC.stopProxy(this.rpcProxy);
    }
    
    @Override
    public Object getUnderlyingProxyObject() {
        return this.rpcProxy;
    }
    
    static {
        NULL_CONTROLLER = null;
    }
}
