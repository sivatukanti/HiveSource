// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.impl.pb.service;

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
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationReportResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationReportRequestPBImpl;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.api.ApplicationHistoryProtocol;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.ApplicationHistoryProtocolPB;

@InterfaceAudience.Private
public class ApplicationHistoryProtocolPBServiceImpl implements ApplicationHistoryProtocolPB
{
    private org.apache.hadoop.yarn.api.ApplicationHistoryProtocol real;
    
    public ApplicationHistoryProtocolPBServiceImpl(final org.apache.hadoop.yarn.api.ApplicationHistoryProtocol impl) {
        this.real = impl;
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
}
