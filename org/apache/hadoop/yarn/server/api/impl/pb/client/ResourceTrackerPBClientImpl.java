// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.impl.pb.client;

import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.NodeHeartbeatResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.NodeHeartbeatRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.ipc.RPCUtil;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RegisterNodeManagerResponsePBImpl;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RegisterNodeManagerRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.server.api.ResourceTrackerPB;
import java.io.Closeable;
import org.apache.hadoop.yarn.server.api.ResourceTracker;

public class ResourceTrackerPBClientImpl implements ResourceTracker, Closeable
{
    private ResourceTrackerPB proxy;
    
    public ResourceTrackerPBClientImpl(final long clientVersion, final InetSocketAddress addr, final Configuration conf) throws IOException {
        RPC.setProtocolEngine(conf, ResourceTrackerPB.class, ProtobufRpcEngine.class);
        this.proxy = RPC.getProxy(ResourceTrackerPB.class, clientVersion, addr, conf);
    }
    
    @Override
    public void close() {
        if (this.proxy != null) {
            RPC.stopProxy(this.proxy);
        }
    }
    
    @Override
    public RegisterNodeManagerResponse registerNodeManager(final RegisterNodeManagerRequest request) throws YarnException, IOException {
        final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto requestProto = ((RegisterNodeManagerRequestPBImpl)request).getProto();
        try {
            return new RegisterNodeManagerResponsePBImpl(this.proxy.registerNodeManager(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public NodeHeartbeatResponse nodeHeartbeat(final NodeHeartbeatRequest request) throws YarnException, IOException {
        final YarnServerCommonServiceProtos.NodeHeartbeatRequestProto requestProto = ((NodeHeartbeatRequestPBImpl)request).getProto();
        try {
            return new NodeHeartbeatResponsePBImpl(this.proxy.nodeHeartbeat(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
}
