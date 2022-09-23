// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api;

import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToLabelsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToLabelsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.MoveApplicationAcrossQueuesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.MoveApplicationAcrossQueuesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationRequest;
import org.apache.hadoop.io.retry.Idempotent;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationRequest;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface ApplicationClientProtocol
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    GetNewApplicationResponse getNewApplication(final GetNewApplicationRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    SubmitApplicationResponse submitApplication(final SubmitApplicationRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    KillApplicationResponse forceKillApplication(final KillApplicationRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    GetApplicationReportResponse getApplicationReport(final GetApplicationReportRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    GetClusterMetricsResponse getClusterMetrics(final GetClusterMetricsRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    GetApplicationsResponse getApplications(final GetApplicationsRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    GetClusterNodesResponse getClusterNodes(final GetClusterNodesRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    GetQueueInfoResponse getQueueInfo(final GetQueueInfoRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    GetQueueUserAclsInfoResponse getQueueUserAcls(final GetQueueUserAclsInfoRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    @Idempotent
    GetDelegationTokenResponse getDelegationToken(final GetDelegationTokenRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @Idempotent
    RenewDelegationTokenResponse renewDelegationToken(final RenewDelegationTokenRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @Idempotent
    CancelDelegationTokenResponse cancelDelegationToken(final CancelDelegationTokenRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    @Idempotent
    MoveApplicationAcrossQueuesResponse moveApplicationAcrossQueues(final MoveApplicationAcrossQueuesRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    @Idempotent
    GetApplicationAttemptReportResponse getApplicationAttemptReport(final GetApplicationAttemptReportRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    @Idempotent
    GetApplicationAttemptsResponse getApplicationAttempts(final GetApplicationAttemptsRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    @Idempotent
    GetContainerReportResponse getContainerReport(final GetContainerReportRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    @Idempotent
    GetContainersResponse getContainers(final GetContainersRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    ReservationSubmissionResponse submitReservation(final ReservationSubmissionRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    ReservationUpdateResponse updateReservation(final ReservationUpdateRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    ReservationDeleteResponse deleteReservation(final ReservationDeleteRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    GetNodesToLabelsResponse getNodeToLabels(final GetNodesToLabelsRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    GetClusterNodeLabelsResponse getClusterNodeLabels(final GetClusterNodeLabelsRequest p0) throws YarnException, IOException;
}
