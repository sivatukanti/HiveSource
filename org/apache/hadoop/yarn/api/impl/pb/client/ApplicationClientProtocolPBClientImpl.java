// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.impl.pb.client;

import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterNodeLabelsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterNodeLabelsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetNodesToLabelsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetNodesToLabelsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToLabelsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToLabelsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationDeleteResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationDeleteRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationUpdateResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationUpdateRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationSubmissionResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.ReservationSubmissionRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainersResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainersRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainerReportResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainerReportRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationAttemptsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationAttemptsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationAttemptReportResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationAttemptReportRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.MoveApplicationAcrossQueuesResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.MoveApplicationAcrossQueuesRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.MoveApplicationAcrossQueuesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.MoveApplicationAcrossQueuesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.CancelDelegationTokenResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.CancelDelegationTokenRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.RenewDelegationTokenResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.RenewDelegationTokenRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenRequest;
import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetDelegationTokenResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetDelegationTokenRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetQueueUserAclsInfoResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetQueueUserAclsInfoRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetQueueInfoResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetQueueInfoRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterNodesResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterNodesRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.SubmitApplicationResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.SubmitApplicationRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetNewApplicationResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetNewApplicationRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterMetricsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetClusterMetricsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationReportResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationReportRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.ipc.RPCUtil;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.KillApplicationResponsePBImpl;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.KillApplicationRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.api.ApplicationClientProtocolPB;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;

@InterfaceAudience.Private
public class ApplicationClientProtocolPBClientImpl implements ApplicationClientProtocol, Closeable
{
    private ApplicationClientProtocolPB proxy;
    
    public ApplicationClientProtocolPBClientImpl(final long clientVersion, final InetSocketAddress addr, final Configuration conf) throws IOException {
        RPC.setProtocolEngine(conf, ApplicationClientProtocolPB.class, ProtobufRpcEngine.class);
        this.proxy = RPC.getProxy(ApplicationClientProtocolPB.class, clientVersion, addr, conf);
    }
    
    @Override
    public void close() {
        if (this.proxy != null) {
            RPC.stopProxy(this.proxy);
        }
    }
    
    @Override
    public KillApplicationResponse forceKillApplication(final KillApplicationRequest request) throws YarnException, IOException {
        final YarnServiceProtos.KillApplicationRequestProto requestProto = ((KillApplicationRequestPBImpl)request).getProto();
        try {
            return new KillApplicationResponsePBImpl(this.proxy.forceKillApplication(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetApplicationReportResponse getApplicationReport(final GetApplicationReportRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetApplicationReportRequestProto requestProto = ((GetApplicationReportRequestPBImpl)request).getProto();
        try {
            return new GetApplicationReportResponsePBImpl(this.proxy.getApplicationReport(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetClusterMetricsResponse getClusterMetrics(final GetClusterMetricsRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetClusterMetricsRequestProto requestProto = ((GetClusterMetricsRequestPBImpl)request).getProto();
        try {
            return new GetClusterMetricsResponsePBImpl(this.proxy.getClusterMetrics(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetNewApplicationResponse getNewApplication(final GetNewApplicationRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetNewApplicationRequestProto requestProto = ((GetNewApplicationRequestPBImpl)request).getProto();
        try {
            return new GetNewApplicationResponsePBImpl(this.proxy.getNewApplication(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public SubmitApplicationResponse submitApplication(final SubmitApplicationRequest request) throws YarnException, IOException {
        final YarnServiceProtos.SubmitApplicationRequestProto requestProto = ((SubmitApplicationRequestPBImpl)request).getProto();
        try {
            return new SubmitApplicationResponsePBImpl(this.proxy.submitApplication(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetApplicationsResponse getApplications(final GetApplicationsRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetApplicationsRequestProto requestProto = ((GetApplicationsRequestPBImpl)request).getProto();
        try {
            return new GetApplicationsResponsePBImpl(this.proxy.getApplications(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetClusterNodesResponse getClusterNodes(final GetClusterNodesRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetClusterNodesRequestProto requestProto = ((GetClusterNodesRequestPBImpl)request).getProto();
        try {
            return new GetClusterNodesResponsePBImpl(this.proxy.getClusterNodes(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetQueueInfoResponse getQueueInfo(final GetQueueInfoRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetQueueInfoRequestProto requestProto = ((GetQueueInfoRequestPBImpl)request).getProto();
        try {
            return new GetQueueInfoResponsePBImpl(this.proxy.getQueueInfo(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetQueueUserAclsInfoResponse getQueueUserAcls(final GetQueueUserAclsInfoRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetQueueUserAclsInfoRequestProto requestProto = ((GetQueueUserAclsInfoRequestPBImpl)request).getProto();
        try {
            return new GetQueueUserAclsInfoResponsePBImpl(this.proxy.getQueueUserAcls(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetDelegationTokenResponse getDelegationToken(final GetDelegationTokenRequest request) throws YarnException, IOException {
        final SecurityProtos.GetDelegationTokenRequestProto requestProto = ((GetDelegationTokenRequestPBImpl)request).getProto();
        try {
            return new GetDelegationTokenResponsePBImpl(this.proxy.getDelegationToken(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public RenewDelegationTokenResponse renewDelegationToken(final RenewDelegationTokenRequest request) throws YarnException, IOException {
        final SecurityProtos.RenewDelegationTokenRequestProto requestProto = ((RenewDelegationTokenRequestPBImpl)request).getProto();
        try {
            return new RenewDelegationTokenResponsePBImpl(this.proxy.renewDelegationToken(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public CancelDelegationTokenResponse cancelDelegationToken(final CancelDelegationTokenRequest request) throws YarnException, IOException {
        final SecurityProtos.CancelDelegationTokenRequestProto requestProto = ((CancelDelegationTokenRequestPBImpl)request).getProto();
        try {
            return new CancelDelegationTokenResponsePBImpl(this.proxy.cancelDelegationToken(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public MoveApplicationAcrossQueuesResponse moveApplicationAcrossQueues(final MoveApplicationAcrossQueuesRequest request) throws YarnException, IOException {
        final YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto requestProto = ((MoveApplicationAcrossQueuesRequestPBImpl)request).getProto();
        try {
            return new MoveApplicationAcrossQueuesResponsePBImpl(this.proxy.moveApplicationAcrossQueues(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetApplicationAttemptReportResponse getApplicationAttemptReport(final GetApplicationAttemptReportRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetApplicationAttemptReportRequestProto requestProto = ((GetApplicationAttemptReportRequestPBImpl)request).getProto();
        try {
            return new GetApplicationAttemptReportResponsePBImpl(this.proxy.getApplicationAttemptReport(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetApplicationAttemptsResponse getApplicationAttempts(final GetApplicationAttemptsRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetApplicationAttemptsRequestProto requestProto = ((GetApplicationAttemptsRequestPBImpl)request).getProto();
        try {
            return new GetApplicationAttemptsResponsePBImpl(this.proxy.getApplicationAttempts(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetContainerReportResponse getContainerReport(final GetContainerReportRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetContainerReportRequestProto requestProto = ((GetContainerReportRequestPBImpl)request).getProto();
        try {
            return new GetContainerReportResponsePBImpl(this.proxy.getContainerReport(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetContainersResponse getContainers(final GetContainersRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetContainersRequestProto requestProto = ((GetContainersRequestPBImpl)request).getProto();
        try {
            return new GetContainersResponsePBImpl(this.proxy.getContainers(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public ReservationSubmissionResponse submitReservation(final ReservationSubmissionRequest request) throws YarnException, IOException {
        final YarnServiceProtos.ReservationSubmissionRequestProto requestProto = ((ReservationSubmissionRequestPBImpl)request).getProto();
        try {
            return new ReservationSubmissionResponsePBImpl(this.proxy.submitReservation(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public ReservationUpdateResponse updateReservation(final ReservationUpdateRequest request) throws YarnException, IOException {
        final YarnServiceProtos.ReservationUpdateRequestProto requestProto = ((ReservationUpdateRequestPBImpl)request).getProto();
        try {
            return new ReservationUpdateResponsePBImpl(this.proxy.updateReservation(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public ReservationDeleteResponse deleteReservation(final ReservationDeleteRequest request) throws YarnException, IOException {
        final YarnServiceProtos.ReservationDeleteRequestProto requestProto = ((ReservationDeleteRequestPBImpl)request).getProto();
        try {
            return new ReservationDeleteResponsePBImpl(this.proxy.deleteReservation(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetNodesToLabelsResponse getNodeToLabels(final GetNodesToLabelsRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetNodesToLabelsRequestProto requestProto = ((GetNodesToLabelsRequestPBImpl)request).getProto();
        try {
            return new GetNodesToLabelsResponsePBImpl(this.proxy.getNodeToLabels(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public GetClusterNodeLabelsResponse getClusterNodeLabels(final GetClusterNodeLabelsRequest request) throws YarnException, IOException {
        final YarnServiceProtos.GetClusterNodeLabelsRequestProto requestProto = ((GetClusterNodeLabelsRequestPBImpl)request).getProto();
        try {
            return new GetClusterNodeLabelsResponsePBImpl(this.proxy.getClusterNodeLabels(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
}
