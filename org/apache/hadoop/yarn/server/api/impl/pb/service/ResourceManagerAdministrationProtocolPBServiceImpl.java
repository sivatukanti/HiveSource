// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.impl.pb.service;

import org.apache.hadoop.yarn.server.api.protocolrecords.ReplaceLabelsOnNodeResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.ReplaceLabelsOnNodeResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.ReplaceLabelsOnNodeRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.ReplaceLabelsOnNodeRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoveFromClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RemoveFromClusterNodeLabelsResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoveFromClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RemoveFromClusterNodeLabelsRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.AddToClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.AddToClusterNodeLabelsResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.AddToClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.AddToClusterNodeLabelsRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.UpdateNodeResourceResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.UpdateNodeResourceRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshServiceAclsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshServiceAclsResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshServiceAclsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshServiceAclsRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshUserToGroupsMappingsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshUserToGroupsMappingsResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshUserToGroupsMappingsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshUserToGroupsMappingsRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshSuperUserGroupsConfigurationResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshSuperUserGroupsConfigurationResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshSuperUserGroupsConfigurationRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshSuperUserGroupsConfigurationRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshNodesResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshNodesResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshNodesRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshNodesRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshAdminAclsResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshAdminAclsRequestPBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesResponse;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshQueuesResponsePBImpl;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RefreshQueuesRequestPBImpl;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocol;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocolPB;

@InterfaceAudience.Private
public class ResourceManagerAdministrationProtocolPBServiceImpl implements ResourceManagerAdministrationProtocolPB
{
    private org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocol real;
    
    public ResourceManagerAdministrationProtocolPBServiceImpl(final org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocol impl) {
        this.real = impl;
    }
    
    @Override
    public YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto refreshQueues(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto proto) throws ServiceException {
        final RefreshQueuesRequestPBImpl request = new RefreshQueuesRequestPBImpl(proto);
        try {
            final RefreshQueuesResponse response = this.real.refreshQueues(request);
            return ((RefreshQueuesResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto refreshAdminAcls(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto proto) throws ServiceException {
        final RefreshAdminAclsRequestPBImpl request = new RefreshAdminAclsRequestPBImpl(proto);
        try {
            final RefreshAdminAclsResponse response = this.real.refreshAdminAcls(request);
            return ((RefreshAdminAclsResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto refreshNodes(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto proto) throws ServiceException {
        final RefreshNodesRequestPBImpl request = new RefreshNodesRequestPBImpl(proto);
        try {
            final RefreshNodesResponse response = this.real.refreshNodes(request);
            return ((RefreshNodesResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto refreshSuperUserGroupsConfiguration(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto proto) throws ServiceException {
        final RefreshSuperUserGroupsConfigurationRequestPBImpl request = new RefreshSuperUserGroupsConfigurationRequestPBImpl(proto);
        try {
            final RefreshSuperUserGroupsConfigurationResponse response = this.real.refreshSuperUserGroupsConfiguration(request);
            return ((RefreshSuperUserGroupsConfigurationResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto refreshUserToGroupsMappings(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto proto) throws ServiceException {
        final RefreshUserToGroupsMappingsRequestPBImpl request = new RefreshUserToGroupsMappingsRequestPBImpl(proto);
        try {
            final RefreshUserToGroupsMappingsResponse response = this.real.refreshUserToGroupsMappings(request);
            return ((RefreshUserToGroupsMappingsResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto refreshServiceAcls(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto proto) throws ServiceException {
        final RefreshServiceAclsRequestPBImpl request = new RefreshServiceAclsRequestPBImpl(proto);
        try {
            final RefreshServiceAclsResponse response = this.real.refreshServiceAcls(request);
            return ((RefreshServiceAclsResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto getGroupsForUser(final RpcController controller, final YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto request) throws ServiceException {
        final String user = request.getUser();
        try {
            final String[] groups = this.real.getGroupsForUser(user);
            final YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto.Builder responseBuilder = YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto.newBuilder();
            for (final String group : groups) {
                responseBuilder.addGroups(group);
            }
            return responseBuilder.build();
        }
        catch (IOException e) {
            throw new ServiceException(e);
        }
    }
    
    @Override
    public YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto updateNodeResource(final RpcController controller, final YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto proto) throws ServiceException {
        final UpdateNodeResourceRequestPBImpl request = new UpdateNodeResourceRequestPBImpl(proto);
        try {
            final UpdateNodeResourceResponse response = this.real.updateNodeResource(request);
            return ((UpdateNodeResourceResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto addToClusterNodeLabels(final RpcController controller, final YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto proto) throws ServiceException {
        final AddToClusterNodeLabelsRequestPBImpl request = new AddToClusterNodeLabelsRequestPBImpl(proto);
        try {
            final AddToClusterNodeLabelsResponse response = this.real.addToClusterNodeLabels(request);
            return ((AddToClusterNodeLabelsResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto removeFromClusterNodeLabels(final RpcController controller, final YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto proto) throws ServiceException {
        final RemoveFromClusterNodeLabelsRequestPBImpl request = new RemoveFromClusterNodeLabelsRequestPBImpl(proto);
        try {
            final RemoveFromClusterNodeLabelsResponse response = this.real.removeFromClusterNodeLabels(request);
            return ((RemoveFromClusterNodeLabelsResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto replaceLabelsOnNodes(final RpcController controller, final YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto proto) throws ServiceException {
        final ReplaceLabelsOnNodeRequestPBImpl request = new ReplaceLabelsOnNodeRequestPBImpl(proto);
        try {
            final ReplaceLabelsOnNodeResponse response = this.real.replaceLabelsOnNode(request);
            return ((ReplaceLabelsOnNodeResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
}
