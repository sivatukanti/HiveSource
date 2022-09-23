// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.impl.pb.client;

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
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationsResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationsRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.ipc.RPCUtil;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationReportResponsePBImpl;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetApplicationReportRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.api.ApplicationHistoryProtocolPB;
import java.io.Closeable;
import org.apache.hadoop.yarn.api.ApplicationHistoryProtocol;

public class ApplicationHistoryProtocolPBClientImpl implements ApplicationHistoryProtocol, Closeable
{
    private ApplicationHistoryProtocolPB proxy;
    
    public ApplicationHistoryProtocolPBClientImpl(final long clientVersion, final InetSocketAddress addr, final Configuration conf) throws IOException {
        RPC.setProtocolEngine(conf, ApplicationHistoryProtocolPB.class, ProtobufRpcEngine.class);
        this.proxy = RPC.getProxy(ApplicationHistoryProtocolPB.class, clientVersion, addr, conf);
    }
    
    @Override
    public void close() throws IOException {
        if (this.proxy != null) {
            RPC.stopProxy(this.proxy);
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
}
