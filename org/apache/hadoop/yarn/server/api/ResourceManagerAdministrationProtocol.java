// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api;

import org.apache.hadoop.yarn.server.api.protocolrecords.ReplaceLabelsOnNodeResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.ReplaceLabelsOnNodeRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoveFromClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoveFromClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.AddToClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.AddToClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshServiceAclsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshServiceAclsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshUserToGroupsMappingsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshUserToGroupsMappingsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshSuperUserGroupsConfigurationResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshSuperUserGroupsConfigurationRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshNodesResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshNodesRequest;
import org.apache.hadoop.io.retry.Idempotent;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.ipc.StandbyException;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesRequest;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.tools.GetUserMappingsProtocol;

@InterfaceAudience.Private
@InterfaceStability.Stable
public interface ResourceManagerAdministrationProtocol extends GetUserMappingsProtocol
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    RefreshQueuesResponse refreshQueues(final RefreshQueuesRequest p0) throws StandbyException, YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    RefreshNodesResponse refreshNodes(final RefreshNodesRequest p0) throws StandbyException, YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    RefreshSuperUserGroupsConfigurationResponse refreshSuperUserGroupsConfiguration(final RefreshSuperUserGroupsConfigurationRequest p0) throws StandbyException, YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    RefreshUserToGroupsMappingsResponse refreshUserToGroupsMappings(final RefreshUserToGroupsMappingsRequest p0) throws StandbyException, YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    RefreshAdminAclsResponse refreshAdminAcls(final RefreshAdminAclsRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    RefreshServiceAclsResponse refreshServiceAcls(final RefreshServiceAclsRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    @Idempotent
    UpdateNodeResourceResponse updateNodeResource(final UpdateNodeResourceRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    @Idempotent
    AddToClusterNodeLabelsResponse addToClusterNodeLabels(final AddToClusterNodeLabelsRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    @Idempotent
    RemoveFromClusterNodeLabelsResponse removeFromClusterNodeLabels(final RemoveFromClusterNodeLabelsRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    @Idempotent
    ReplaceLabelsOnNodeResponse replaceLabelsOnNode(final ReplaceLabelsOnNodeRequest p0) throws YarnException, IOException;
}
