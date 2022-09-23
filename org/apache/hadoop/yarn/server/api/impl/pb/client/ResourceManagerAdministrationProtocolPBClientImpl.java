// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.impl.pb.client;

import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.ReplaceLabelsOnNodeResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.ReplaceLabelsOnNodeRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.ReplaceLabelsOnNodeResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.ReplaceLabelsOnNodeRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RemoveFromClusterNodeLabelsResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RemoveFromClusterNodeLabelsRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoveFromClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoveFromClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.AddToClusterNodeLabelsResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.AddToClusterNodeLabelsRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.AddToClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.AddToClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.UpdateNodeResourceResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.UpdateNodeResourceRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceRequest;
import org.apache.hadoop.ipc.ProtobufHelper;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshServiceAclsResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshServiceAclsRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshServiceAclsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshServiceAclsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshAdminAclsResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshAdminAclsRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshUserToGroupsMappingsResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshUserToGroupsMappingsRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshUserToGroupsMappingsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshUserToGroupsMappingsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshSuperUserGroupsConfigurationResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshSuperUserGroupsConfigurationRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshSuperUserGroupsConfigurationResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshSuperUserGroupsConfigurationRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshNodesResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshNodesRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshNodesResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshNodesRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.ipc.RPCUtil;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshQueuesResponsePBImpl;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshQueuesRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesRequest;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocolPB;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;
import org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocol;

@InterfaceAudience.Private
public class ResourceManagerAdministrationProtocolPBClientImpl implements ResourceManagerAdministrationProtocol, Closeable
{
    private ResourceManagerAdministrationProtocolPB proxy;
    
    public ResourceManagerAdministrationProtocolPBClientImpl(final long clientVersion, final InetSocketAddress addr, final Configuration conf) throws IOException {
        RPC.setProtocolEngine(conf, ResourceManagerAdministrationProtocolPB.class, ProtobufRpcEngine.class);
        this.proxy = RPC.getProxy(ResourceManagerAdministrationProtocolPB.class, clientVersion, addr, conf);
    }
    
    @Override
    public void close() {
        if (this.proxy != null) {
            RPC.stopProxy(this.proxy);
        }
    }
    
    @Override
    public RefreshQueuesResponse refreshQueues(final RefreshQueuesRequest request) throws YarnException, IOException {
        final YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto requestProto = ((RefreshQueuesRequestPBImpl)request).getProto();
        try {
            return new RefreshQueuesResponsePBImpl(this.proxy.refreshQueues(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public RefreshNodesResponse refreshNodes(final RefreshNodesRequest request) throws YarnException, IOException {
        final YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto requestProto = ((RefreshNodesRequestPBImpl)request).getProto();
        try {
            return new RefreshNodesResponsePBImpl(this.proxy.refreshNodes(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public RefreshSuperUserGroupsConfigurationResponse refreshSuperUserGroupsConfiguration(final RefreshSuperUserGroupsConfigurationRequest request) throws YarnException, IOException {
        final YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto requestProto = ((RefreshSuperUserGroupsConfigurationRequestPBImpl)request).getProto();
        try {
            return new RefreshSuperUserGroupsConfigurationResponsePBImpl(this.proxy.refreshSuperUserGroupsConfiguration(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public RefreshUserToGroupsMappingsResponse refreshUserToGroupsMappings(final RefreshUserToGroupsMappingsRequest request) throws YarnException, IOException {
        final YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto requestProto = ((RefreshUserToGroupsMappingsRequestPBImpl)request).getProto();
        try {
            return new RefreshUserToGroupsMappingsResponsePBImpl(this.proxy.refreshUserToGroupsMappings(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public RefreshAdminAclsResponse refreshAdminAcls(final RefreshAdminAclsRequest request) throws YarnException, IOException {
        final YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto requestProto = ((RefreshAdminAclsRequestPBImpl)request).getProto();
        try {
            return new RefreshAdminAclsResponsePBImpl(this.proxy.refreshAdminAcls(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public RefreshServiceAclsResponse refreshServiceAcls(final RefreshServiceAclsRequest request) throws YarnException, IOException {
        final YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto requestProto = ((RefreshServiceAclsRequestPBImpl)request).getProto();
        try {
            return new RefreshServiceAclsResponsePBImpl(this.proxy.refreshServiceAcls(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public String[] getGroupsForUser(final String user) throws IOException {
        final YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto requestProto = YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto.newBuilder().setUser(user).build();
        try {
            final YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto responseProto = this.proxy.getGroupsForUser(null, requestProto);
            return responseProto.getGroupsList().toArray(new String[responseProto.getGroupsCount()]);
        }
        catch (ServiceException e) {
            throw ProtobufHelper.getRemoteException(e);
        }
    }
    
    @Override
    public UpdateNodeResourceResponse updateNodeResource(final UpdateNodeResourceRequest request) throws YarnException, IOException {
        final YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto requestProto = ((UpdateNodeResourceRequestPBImpl)request).getProto();
        try {
            return new UpdateNodeResourceResponsePBImpl(this.proxy.updateNodeResource(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public AddToClusterNodeLabelsResponse addToClusterNodeLabels(final AddToClusterNodeLabelsRequest request) throws YarnException, IOException {
        final YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto requestProto = ((AddToClusterNodeLabelsRequestPBImpl)request).getProto();
        try {
            return new AddToClusterNodeLabelsResponsePBImpl(this.proxy.addToClusterNodeLabels(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public RemoveFromClusterNodeLabelsResponse removeFromClusterNodeLabels(final RemoveFromClusterNodeLabelsRequest request) throws YarnException, IOException {
        final YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto requestProto = ((RemoveFromClusterNodeLabelsRequestPBImpl)request).getProto();
        try {
            return new RemoveFromClusterNodeLabelsResponsePBImpl(this.proxy.removeFromClusterNodeLabels(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public ReplaceLabelsOnNodeResponse replaceLabelsOnNode(final ReplaceLabelsOnNodeRequest request) throws YarnException, IOException {
        final YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto requestProto = ((ReplaceLabelsOnNodeRequestPBImpl)request).getProto();
        try {
            return new ReplaceLabelsOnNodeResponsePBImpl(this.proxy.replaceLabelsOnNodes(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
}
