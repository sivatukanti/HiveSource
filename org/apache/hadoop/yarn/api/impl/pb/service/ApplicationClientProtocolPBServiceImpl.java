// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.impl.pb.service;

import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterNodeLabelsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterNodeLabelsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToLabelsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetNodesToLabelsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToLabelsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetNodesToLabelsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationDeleteResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationDeleteRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationUpdateResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationUpdateRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationSubmissionResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationSubmissionRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainersResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainersRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainerReportResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainerReportRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationAttemptsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationAttemptsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationAttemptReportResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationAttemptReportRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.MoveApplicationAcrossQueuesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.MoveApplicationAcrossQueuesResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.MoveApplicationAcrossQueuesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.MoveApplicationAcrossQueuesRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.CancelDelegationTokenResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.CancelDelegationTokenRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.RenewDelegationTokenResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.RenewDelegationTokenRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetDelegationTokenResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetDelegationTokenRequestPBImpl;
import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetQueueUserAclsInfoResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetQueueUserAclsInfoRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetQueueInfoResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetQueueInfoRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterNodesResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterNodesRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.SubmitApplicationResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.SubmitApplicationRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetNewApplicationResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetNewApplicationRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterMetricsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterMetricsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationReportResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationReportRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationResponse;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.KillApplicationResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.KillApplicationRequestPBImpl;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.ApplicationClientProtocolPB;

@InterfaceAudience.Private
public class ApplicationClientProtocolPBServiceImpl implements ApplicationClientProtocolPB
{
    private org.apache.hadoop.yarn.api.ApplicationClientProtocol real;
    
    public ApplicationClientProtocolPBServiceImpl(final org.apache.hadoop.yarn.api.ApplicationClientProtocol impl) {
        this.real = impl;
    }
    
    @Override
    public YarnServiceProtos.KillApplicationResponseProto forceKillApplication(final RpcController arg0, final YarnServiceProtos.KillApplicationRequestProto proto) throws ServiceException {
        final KillApplicationRequestPBImpl request = new KillApplicationRequestPBImpl(proto);
        try {
            final KillApplicationResponse response = this.real.forceKillApplication(request);
            return ((KillApplicationResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetApplicationReportResponseProto getApplicationReport(final RpcController arg0, final YarnServiceProtos.GetApplicationReportRequestProto proto) throws ServiceException {
        final GetApplicationReportRequestPBImpl request = new GetApplicationReportRequestPBImpl(proto);
        try {
            final GetApplicationReportResponse response = this.real.getApplicationReport(request);
            return ((GetApplicationReportResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetClusterMetricsResponseProto getClusterMetrics(final RpcController arg0, final YarnServiceProtos.GetClusterMetricsRequestProto proto) throws ServiceException {
        final GetClusterMetricsRequestPBImpl request = new GetClusterMetricsRequestPBImpl(proto);
        try {
            final GetClusterMetricsResponse response = this.real.getClusterMetrics(request);
            return ((GetClusterMetricsResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetNewApplicationResponseProto getNewApplication(final RpcController arg0, final YarnServiceProtos.GetNewApplicationRequestProto proto) throws ServiceException {
        final GetNewApplicationRequestPBImpl request = new GetNewApplicationRequestPBImpl(proto);
        try {
            final GetNewApplicationResponse response = this.real.getNewApplication(request);
            return ((GetNewApplicationResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.SubmitApplicationResponseProto submitApplication(final RpcController arg0, final YarnServiceProtos.SubmitApplicationRequestProto proto) throws ServiceException {
        final SubmitApplicationRequestPBImpl request = new SubmitApplicationRequestPBImpl(proto);
        try {
            final SubmitApplicationResponse response = this.real.submitApplication(request);
            return ((SubmitApplicationResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetApplicationsResponseProto getApplications(final RpcController controller, final YarnServiceProtos.GetApplicationsRequestProto proto) throws ServiceException {
        final GetApplicationsRequestPBImpl request = new GetApplicationsRequestPBImpl(proto);
        try {
            final GetApplicationsResponse response = this.real.getApplications(request);
            return ((GetApplicationsResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetClusterNodesResponseProto getClusterNodes(final RpcController controller, final YarnServiceProtos.GetClusterNodesRequestProto proto) throws ServiceException {
        final GetClusterNodesRequestPBImpl request = new GetClusterNodesRequestPBImpl(proto);
        try {
            final GetClusterNodesResponse response = this.real.getClusterNodes(request);
            return ((GetClusterNodesResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetQueueInfoResponseProto getQueueInfo(final RpcController controller, final YarnServiceProtos.GetQueueInfoRequestProto proto) throws ServiceException {
        final GetQueueInfoRequestPBImpl request = new GetQueueInfoRequestPBImpl(proto);
        try {
            final GetQueueInfoResponse response = this.real.getQueueInfo(request);
            return ((GetQueueInfoResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetQueueUserAclsInfoResponseProto getQueueUserAcls(final RpcController controller, final YarnServiceProtos.GetQueueUserAclsInfoRequestProto proto) throws ServiceException {
        final GetQueueUserAclsInfoRequestPBImpl request = new GetQueueUserAclsInfoRequestPBImpl(proto);
        try {
            final GetQueueUserAclsInfoResponse response = this.real.getQueueUserAcls(request);
            return ((GetQueueUserAclsInfoResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public SecurityProtos.GetDelegationTokenResponseProto getDelegationToken(final RpcController controller, final SecurityProtos.GetDelegationTokenRequestProto proto) throws ServiceException {
        final GetDelegationTokenRequestPBImpl request = new GetDelegationTokenRequestPBImpl(proto);
        try {
            final GetDelegationTokenResponse response = this.real.getDelegationToken(request);
            return ((GetDelegationTokenResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public SecurityProtos.RenewDelegationTokenResponseProto renewDelegationToken(final RpcController controller, final SecurityProtos.RenewDelegationTokenRequestProto proto) throws ServiceException {
        final RenewDelegationTokenRequestPBImpl request = new RenewDelegationTokenRequestPBImpl(proto);
        try {
            final RenewDelegationTokenResponse response = this.real.renewDelegationToken(request);
            return ((RenewDelegationTokenResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public SecurityProtos.CancelDelegationTokenResponseProto cancelDelegationToken(final RpcController controller, final SecurityProtos.CancelDelegationTokenRequestProto proto) throws ServiceException {
        final CancelDelegationTokenRequestPBImpl request = new CancelDelegationTokenRequestPBImpl(proto);
        try {
            final CancelDelegationTokenResponse response = this.real.cancelDelegationToken(request);
            return ((CancelDelegationTokenResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto moveApplicationAcrossQueues(final RpcController controller, final YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto proto) throws ServiceException {
        final MoveApplicationAcrossQueuesRequestPBImpl request = new MoveApplicationAcrossQueuesRequestPBImpl(proto);
        try {
            final MoveApplicationAcrossQueuesResponse response = this.real.moveApplicationAcrossQueues(request);
            return ((MoveApplicationAcrossQueuesResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetApplicationAttemptReportResponseProto getApplicationAttemptReport(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptReportRequestProto proto) throws ServiceException {
        final GetApplicationAttemptReportRequestPBImpl request = new GetApplicationAttemptReportRequestPBImpl(proto);
        try {
            final GetApplicationAttemptReportResponse response = this.real.getApplicationAttemptReport(request);
            return ((GetApplicationAttemptReportResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetApplicationAttemptsResponseProto getApplicationAttempts(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptsRequestProto proto) throws ServiceException {
        final GetApplicationAttemptsRequestPBImpl request = new GetApplicationAttemptsRequestPBImpl(proto);
        try {
            final GetApplicationAttemptsResponse response = this.real.getApplicationAttempts(request);
            return ((GetApplicationAttemptsResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetContainerReportResponseProto getContainerReport(final RpcController controller, final YarnServiceProtos.GetContainerReportRequestProto proto) throws ServiceException {
        final GetContainerReportRequestPBImpl request = new GetContainerReportRequestPBImpl(proto);
        try {
            final GetContainerReportResponse response = this.real.getContainerReport(request);
            return ((GetContainerReportResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetContainersResponseProto getContainers(final RpcController controller, final YarnServiceProtos.GetContainersRequestProto proto) throws ServiceException {
        final GetContainersRequestPBImpl request = new GetContainersRequestPBImpl(proto);
        try {
            final GetContainersResponse response = this.real.getContainers(request);
            return ((GetContainersResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.ReservationSubmissionResponseProto submitReservation(final RpcController controller, final YarnServiceProtos.ReservationSubmissionRequestProto requestProto) throws ServiceException {
        final ReservationSubmissionRequestPBImpl request = new ReservationSubmissionRequestPBImpl(requestProto);
        try {
            final ReservationSubmissionResponse response = this.real.submitReservation(request);
            return ((ReservationSubmissionResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.ReservationUpdateResponseProto updateReservation(final RpcController controller, final YarnServiceProtos.ReservationUpdateRequestProto requestProto) throws ServiceException {
        final ReservationUpdateRequestPBImpl request = new ReservationUpdateRequestPBImpl(requestProto);
        try {
            final ReservationUpdateResponse response = this.real.updateReservation(request);
            return ((ReservationUpdateResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.ReservationDeleteResponseProto deleteReservation(final RpcController controller, final YarnServiceProtos.ReservationDeleteRequestProto requestProto) throws ServiceException {
        final ReservationDeleteRequestPBImpl request = new ReservationDeleteRequestPBImpl(requestProto);
        try {
            final ReservationDeleteResponse response = this.real.deleteReservation(request);
            return ((ReservationDeleteResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetNodesToLabelsResponseProto getNodeToLabels(final RpcController controller, final YarnServiceProtos.GetNodesToLabelsRequestProto proto) throws ServiceException {
        final GetNodesToLabelsRequestPBImpl request = new GetNodesToLabelsRequestPBImpl(proto);
        try {
            final GetNodesToLabelsResponse response = this.real.getNodeToLabels(request);
            return ((GetNodesToLabelsResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetClusterNodeLabelsResponseProto getClusterNodeLabels(final RpcController controller, final YarnServiceProtos.GetClusterNodeLabelsRequestProto proto) throws ServiceException {
        final GetClusterNodeLabelsRequestPBImpl request = new GetClusterNodeLabelsRequestPBImpl(proto);
        try {
            final GetClusterNodeLabelsResponse response = this.real.getClusterNodeLabels(request);
            return ((GetClusterNodeLabelsResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
}
