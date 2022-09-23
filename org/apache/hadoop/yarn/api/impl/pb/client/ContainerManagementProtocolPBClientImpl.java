// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.impl.pb.client;

import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainerStatusesResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainerStatusesRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.StopContainersResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.StopContainersRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.ipc.RPCUtil;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.StartContainersResponsePBImpl;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.StartContainersRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import java.io.IOException;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.api.ContainerManagementProtocolPB;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;

@InterfaceAudience.Private
public class ContainerManagementProtocolPBClientImpl implements ContainerManagementProtocol, Closeable
{
    static final String NM_COMMAND_TIMEOUT = "yarn.rpc.nm-command-timeout";
    static final int DEFAULT_COMMAND_TIMEOUT = 60000;
    private ContainerManagementProtocolPB proxy;
    
    public ContainerManagementProtocolPBClientImpl(final long clientVersion, final InetSocketAddress addr, final Configuration conf) throws IOException {
        RPC.setProtocolEngine(conf, ContainerManagementProtocolPB.class, ProtobufRpcEngine.class);
        final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        final int expireIntvl = conf.getInt("yarn.rpc.nm-command-timeout", 60000);
        this.proxy = RPC.getProxy(ContainerManagementProtocolPB.class, clientVersion, addr, ugi, conf, NetUtils.getDefaultSocketFactory(conf), expireIntvl);
    }
    
    @Override
    public void close() {
        if (this.proxy != null) {
            RPC.stopProxy(this.proxy);
        }
    }
    
    @Override
    public StartContainersResponse startContainers(final StartContainersRequest requests) throws YarnException, IOException {
        final YarnServiceProtos.StartContainersRequestProto requestProto = ((StartContainersRequestPBImpl)requests).getProto();
        try {
            return new StartContainersResponsePBImpl(this.proxy.startContainers(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public StopContainersResponse stopContainers(final StopContainersRequest requests) throws YarnException, IOException {
        final YarnServiceProtos.StopContainersRequestProto requestProto = ((StopContainersRequestPBImpl)requests).getProto();
        try {
            return new StopContainersResponsePBImpl(this.proxy.stopContainers(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetContainerStatusesResponse getContainerStatuses(final GetContainerStatusesRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetContainerStatusesRequestProto requestProto = ((GetContainerStatusesRequestPBImpl)request).getProto();
        try {
            return new GetContainerStatusesResponsePBImpl(this.proxy.getContainerStatuses(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
}
