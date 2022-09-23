// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api;

import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersRequest;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface ContainerManagementProtocol
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    StartContainersResponse startContainers(final StartContainersRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    StopContainersResponse stopContainers(final StopContainersRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    GetContainerStatusesResponse getContainerStatuses(final GetContainerStatusesRequest p0) throws YarnException, IOException;
}
