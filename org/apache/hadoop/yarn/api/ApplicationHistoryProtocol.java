// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api;

import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public interface ApplicationHistoryProtocol
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    GetApplicationReportResponse getApplicationReport(final GetApplicationReportRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    GetApplicationsResponse getApplications(final GetApplicationsRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    GetApplicationAttemptReportResponse getApplicationAttemptReport(final GetApplicationAttemptReportRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    GetApplicationAttemptsResponse getApplicationAttempts(final GetApplicationAttemptsRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    GetContainerReportResponse getContainerReport(final GetContainerReportRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    GetContainersResponse getContainers(final GetContainersRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    GetDelegationTokenResponse getDelegationToken(final GetDelegationTokenRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    RenewDelegationTokenResponse renewDelegationToken(final RenewDelegationTokenRequest p0) throws YarnException, IOException;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    CancelDelegationTokenResponse cancelDelegationToken(final CancelDelegationTokenRequest p0) throws YarnException, IOException;
}
