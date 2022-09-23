// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.impl.pb.service;

import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.NodeHeartbeatResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.NodeHeartbeatRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RegisterNodeManagerResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RegisterNodeManagerRequestPBImpl;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.server.api.ResourceTracker;
import org.apache.hadoop.yarn.server.api.ResourceTrackerPB;

public class ResourceTrackerPBServiceImpl implements ResourceTrackerPB
{
    private org.apache.hadoop.yarn.server.api.ResourceTracker real;
    
    public ResourceTrackerPBServiceImpl(final org.apache.hadoop.yarn.server.api.ResourceTracker impl) {
        this.real = impl;
    }
    
    @Override
    public YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto registerNodeManager(final RpcController controller, final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto proto) throws ServiceException {
        final RegisterNodeManagerRequestPBImpl request = new RegisterNodeManagerRequestPBImpl(proto);
        try {
            final RegisterNodeManagerResponse response = this.real.registerNodeManager(request);
            return ((RegisterNodeManagerResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServerCommonServiceProtos.NodeHeartbeatResponseProto nodeHeartbeat(final RpcController controller, final YarnServerCommonServiceProtos.NodeHeartbeatRequestProto proto) throws ServiceException {
        final NodeHeartbeatRequestPBImpl request = new NodeHeartbeatRequestPBImpl(proto);
        try {
            final NodeHeartbeatResponse response = this.real.nodeHeartbeat(request);
            return ((NodeHeartbeatResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
}
