// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha.protocolPB;

import org.apache.hadoop.ha.HAServiceStatus;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.ipc.ProtobufHelper;
import org.apache.hadoop.security.UserGroupInformation;
import javax.net.SocketFactory;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.ha.proto.HAServiceProtocolProtos;
import com.google.protobuf.RpcController;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.ipc.ProtocolTranslator;
import java.io.Closeable;
import org.apache.hadoop.ha.HAServiceProtocol;

@InterfaceAudience.Private
@InterfaceStability.Stable
public class HAServiceProtocolClientSideTranslatorPB implements HAServiceProtocol, Closeable, ProtocolTranslator
{
    private static final RpcController NULL_CONTROLLER;
    private static final HAServiceProtocolProtos.MonitorHealthRequestProto MONITOR_HEALTH_REQ;
    private static final HAServiceProtocolProtos.GetServiceStatusRequestProto GET_SERVICE_STATUS_REQ;
    private final HAServiceProtocolPB rpcProxy;
    
    public HAServiceProtocolClientSideTranslatorPB(final InetSocketAddress addr, final Configuration conf) throws IOException {
        RPC.setProtocolEngine(conf, HAServiceProtocolPB.class, ProtobufRpcEngine.class);
        this.rpcProxy = RPC.getProxy(HAServiceProtocolPB.class, RPC.getProtocolVersion(HAServiceProtocolPB.class), addr, conf);
    }
    
    public HAServiceProtocolClientSideTranslatorPB(final InetSocketAddress addr, final Configuration conf, final SocketFactory socketFactory, final int timeout) throws IOException {
        RPC.setProtocolEngine(conf, HAServiceProtocolPB.class, ProtobufRpcEngine.class);
        this.rpcProxy = RPC.getProxy(HAServiceProtocolPB.class, RPC.getProtocolVersion(HAServiceProtocolPB.class), addr, UserGroupInformation.getCurrentUser(), conf, socketFactory, timeout);
    }
    
    @Override
    public void monitorHealth() throws IOException {
        try {
            this.rpcProxy.monitorHealth(HAServiceProtocolClientSideTranslatorPB.NULL_CONTROLLER, HAServiceProtocolClientSideTranslatorPB.MONITOR_HEALTH_REQ);
        }
        catch (ServiceException e) {
            throw ProtobufHelper.getRemoteException(e);
        }
    }
    
    @Override
    public void transitionToActive(final StateChangeRequestInfo reqInfo) throws IOException {
        try {
            final HAServiceProtocolProtos.TransitionToActiveRequestProto req = HAServiceProtocolProtos.TransitionToActiveRequestProto.newBuilder().setReqInfo(this.convert(reqInfo)).build();
            this.rpcProxy.transitionToActive(HAServiceProtocolClientSideTranslatorPB.NULL_CONTROLLER, req);
        }
        catch (ServiceException e) {
            throw ProtobufHelper.getRemoteException(e);
        }
    }
    
    @Override
    public void transitionToStandby(final StateChangeRequestInfo reqInfo) throws IOException {
        try {
            final HAServiceProtocolProtos.TransitionToStandbyRequestProto req = HAServiceProtocolProtos.TransitionToStandbyRequestProto.newBuilder().setReqInfo(this.convert(reqInfo)).build();
            this.rpcProxy.transitionToStandby(HAServiceProtocolClientSideTranslatorPB.NULL_CONTROLLER, req);
        }
        catch (ServiceException e) {
            throw ProtobufHelper.getRemoteException(e);
        }
    }
    
    @Override
    public HAServiceStatus getServiceStatus() throws IOException {
        HAServiceProtocolProtos.GetServiceStatusResponseProto status;
        try {
            status = this.rpcProxy.getServiceStatus(HAServiceProtocolClientSideTranslatorPB.NULL_CONTROLLER, HAServiceProtocolClientSideTranslatorPB.GET_SERVICE_STATUS_REQ);
        }
        catch (ServiceException e) {
            throw ProtobufHelper.getRemoteException(e);
        }
        final HAServiceStatus ret = new HAServiceStatus(this.convert(status.getState()));
        if (status.getReadyToBecomeActive()) {
            ret.setReadyToBecomeActive();
        }
        else {
            ret.setNotReadyToBecomeActive(status.getNotReadyReason());
        }
        return ret;
    }
    
    private HAServiceState convert(final HAServiceProtocolProtos.HAServiceStateProto state) {
        switch (state) {
            case ACTIVE: {
                return HAServiceState.ACTIVE;
            }
            case STANDBY: {
                return HAServiceState.STANDBY;
            }
            default: {
                return HAServiceState.INITIALIZING;
            }
        }
    }
    
    private HAServiceProtocolProtos.HAStateChangeRequestInfoProto convert(final StateChangeRequestInfo reqInfo) {
        HAServiceProtocolProtos.HARequestSource src = null;
        switch (reqInfo.getSource()) {
            case REQUEST_BY_USER: {
                src = HAServiceProtocolProtos.HARequestSource.REQUEST_BY_USER;
                break;
            }
            case REQUEST_BY_USER_FORCED: {
                src = HAServiceProtocolProtos.HARequestSource.REQUEST_BY_USER_FORCED;
                break;
            }
            case REQUEST_BY_ZKFC: {
                src = HAServiceProtocolProtos.HARequestSource.REQUEST_BY_ZKFC;
                break;
            }
            default: {
                throw new IllegalArgumentException("Bad source: " + reqInfo.getSource());
            }
        }
        return HAServiceProtocolProtos.HAStateChangeRequestInfoProto.newBuilder().setReqSource(src).build();
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
        MONITOR_HEALTH_REQ = HAServiceProtocolProtos.MonitorHealthRequestProto.newBuilder().build();
        GET_SERVICE_STATUS_REQ = HAServiceProtocolProtos.GetServiceStatusRequestProto.newBuilder().build();
    }
}
