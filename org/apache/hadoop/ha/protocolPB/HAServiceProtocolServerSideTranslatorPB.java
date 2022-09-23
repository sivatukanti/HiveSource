// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha.protocolPB;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.ipc.VersionedProtocol;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ha.HAServiceStatus;
import java.io.IOException;
import com.google.protobuf.ServiceException;
import com.google.protobuf.RpcController;
import org.slf4j.Logger;
import org.apache.hadoop.ha.proto.HAServiceProtocolProtos;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Stable
public class HAServiceProtocolServerSideTranslatorPB implements HAServiceProtocolPB
{
    private final HAServiceProtocol server;
    private static final HAServiceProtocolProtos.MonitorHealthResponseProto MONITOR_HEALTH_RESP;
    private static final HAServiceProtocolProtos.TransitionToActiveResponseProto TRANSITION_TO_ACTIVE_RESP;
    private static final HAServiceProtocolProtos.TransitionToStandbyResponseProto TRANSITION_TO_STANDBY_RESP;
    private static final Logger LOG;
    
    public HAServiceProtocolServerSideTranslatorPB(final HAServiceProtocol server) {
        this.server = server;
    }
    
    @Override
    public HAServiceProtocolProtos.MonitorHealthResponseProto monitorHealth(final RpcController controller, final HAServiceProtocolProtos.MonitorHealthRequestProto request) throws ServiceException {
        try {
            this.server.monitorHealth();
            return HAServiceProtocolServerSideTranslatorPB.MONITOR_HEALTH_RESP;
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }
    
    private HAServiceProtocol.StateChangeRequestInfo convert(final HAServiceProtocolProtos.HAStateChangeRequestInfoProto proto) {
        HAServiceProtocol.RequestSource src = null;
        switch (proto.getReqSource()) {
            case REQUEST_BY_USER: {
                src = HAServiceProtocol.RequestSource.REQUEST_BY_USER;
                break;
            }
            case REQUEST_BY_USER_FORCED: {
                src = HAServiceProtocol.RequestSource.REQUEST_BY_USER_FORCED;
                break;
            }
            case REQUEST_BY_ZKFC: {
                src = HAServiceProtocol.RequestSource.REQUEST_BY_ZKFC;
                break;
            }
            default: {
                HAServiceProtocolServerSideTranslatorPB.LOG.warn("Unknown request source: " + proto.getReqSource());
                src = null;
                break;
            }
        }
        return new HAServiceProtocol.StateChangeRequestInfo(src);
    }
    
    @Override
    public HAServiceProtocolProtos.TransitionToActiveResponseProto transitionToActive(final RpcController controller, final HAServiceProtocolProtos.TransitionToActiveRequestProto request) throws ServiceException {
        try {
            this.server.transitionToActive(this.convert(request.getReqInfo()));
            return HAServiceProtocolServerSideTranslatorPB.TRANSITION_TO_ACTIVE_RESP;
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }
    
    @Override
    public HAServiceProtocolProtos.TransitionToStandbyResponseProto transitionToStandby(final RpcController controller, final HAServiceProtocolProtos.TransitionToStandbyRequestProto request) throws ServiceException {
        try {
            this.server.transitionToStandby(this.convert(request.getReqInfo()));
            return HAServiceProtocolServerSideTranslatorPB.TRANSITION_TO_STANDBY_RESP;
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }
    
    @Override
    public HAServiceProtocolProtos.GetServiceStatusResponseProto getServiceStatus(final RpcController controller, final HAServiceProtocolProtos.GetServiceStatusRequestProto request) throws ServiceException {
        HAServiceStatus s;
        try {
            s = this.server.getServiceStatus();
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
        HAServiceProtocolProtos.HAServiceStateProto retState = null;
        switch (s.getState()) {
            case ACTIVE: {
                retState = HAServiceProtocolProtos.HAServiceStateProto.ACTIVE;
                break;
            }
            case STANDBY: {
                retState = HAServiceProtocolProtos.HAServiceStateProto.STANDBY;
                break;
            }
            default: {
                retState = HAServiceProtocolProtos.HAServiceStateProto.INITIALIZING;
                break;
            }
        }
        final HAServiceProtocolProtos.GetServiceStatusResponseProto.Builder ret = HAServiceProtocolProtos.GetServiceStatusResponseProto.newBuilder().setState(retState).setReadyToBecomeActive(s.isReadyToBecomeActive());
        if (!s.isReadyToBecomeActive()) {
            ret.setNotReadyReason(s.getNotReadyReason());
        }
        return ret.build();
    }
    
    @Override
    public long getProtocolVersion(final String protocol, final long clientVersion) throws IOException {
        return RPC.getProtocolVersion(HAServiceProtocolPB.class);
    }
    
    @Override
    public ProtocolSignature getProtocolSignature(final String protocol, final long clientVersion, final int clientMethodsHash) throws IOException {
        if (!protocol.equals(RPC.getProtocolName(HAServiceProtocolPB.class))) {
            throw new IOException("Serverside implements " + RPC.getProtocolName(HAServiceProtocolPB.class) + ". The following requested protocol is unknown: " + protocol);
        }
        return ProtocolSignature.getProtocolSignature(clientMethodsHash, RPC.getProtocolVersion(HAServiceProtocolPB.class), HAServiceProtocolPB.class);
    }
    
    static {
        MONITOR_HEALTH_RESP = HAServiceProtocolProtos.MonitorHealthResponseProto.newBuilder().build();
        TRANSITION_TO_ACTIVE_RESP = HAServiceProtocolProtos.TransitionToActiveResponseProto.newBuilder().build();
        TRANSITION_TO_STANDBY_RESP = HAServiceProtocolProtos.TransitionToStandbyResponseProto.newBuilder().build();
        LOG = LoggerFactory.getLogger(HAServiceProtocolServerSideTranslatorPB.class);
    }
}
