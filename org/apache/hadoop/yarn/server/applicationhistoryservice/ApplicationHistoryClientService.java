// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice;

import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenRequest;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;
import org.apache.hadoop.yarn.exceptions.ContainerNotFoundException;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportRequest;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import java.util.List;
import java.util.Collection;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsRequest;
import org.apache.hadoop.yarn.exceptions.ApplicationAttemptNotFoundException;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportRequest;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenRequest;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.authorize.PolicyProvider;
import org.apache.hadoop.yarn.server.timeline.security.authorize.TimelinePolicyProvider;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import java.net.InetSocketAddress;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.yarn.api.ApplicationHistoryProtocol;
import org.apache.commons.logging.Log;
import org.apache.hadoop.service.AbstractService;

public class ApplicationHistoryClientService extends AbstractService
{
    private static final Log LOG;
    private ApplicationHistoryManager history;
    private ApplicationHistoryProtocol protocolHandler;
    private Server server;
    private InetSocketAddress bindAddress;
    
    public ApplicationHistoryClientService(final ApplicationHistoryManager history) {
        super("ApplicationHistoryClientService");
        this.history = history;
        this.protocolHandler = new ApplicationHSClientProtocolHandler();
    }
    
    @Override
    protected void serviceStart() throws Exception {
        final Configuration conf = this.getConfig();
        final YarnRPC rpc = YarnRPC.create(conf);
        final InetSocketAddress address = conf.getSocketAddr("yarn.timeline-service.bind-host", "yarn.timeline-service.address", "0.0.0.0:10200", 10200);
        this.server = rpc.getServer(ApplicationHistoryProtocol.class, this.protocolHandler, address, conf, null, conf.getInt("yarn.timeline-service.handler-thread-count", 10));
        if (conf.getBoolean("hadoop.security.authorization", false)) {
            this.refreshServiceAcls(conf, new TimelinePolicyProvider());
        }
        this.server.start();
        this.bindAddress = conf.updateConnectAddr("yarn.timeline-service.bind-host", "yarn.timeline-service.address", "0.0.0.0:10200", this.server.getListenerAddress());
        ApplicationHistoryClientService.LOG.info("Instantiated ApplicationHistoryClientService at " + this.bindAddress);
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        if (this.server != null) {
            this.server.stop();
        }
        super.serviceStop();
    }
    
    @InterfaceAudience.Private
    public ApplicationHistoryProtocol getClientHandler() {
        return this.protocolHandler;
    }
    
    @InterfaceAudience.Private
    public InetSocketAddress getBindAddress() {
        return this.bindAddress;
    }
    
    private void refreshServiceAcls(final Configuration configuration, final PolicyProvider policyProvider) {
        this.server.refreshServiceAcl(configuration, policyProvider);
    }
    
    static {
        LOG = LogFactory.getLog(ApplicationHistoryClientService.class);
    }
    
    private class ApplicationHSClientProtocolHandler implements ApplicationHistoryProtocol
    {
        @Override
        public CancelDelegationTokenResponse cancelDelegationToken(final CancelDelegationTokenRequest request) throws YarnException, IOException {
            return null;
        }
        
        @Override
        public GetApplicationAttemptReportResponse getApplicationAttemptReport(final GetApplicationAttemptReportRequest request) throws YarnException, IOException {
            try {
                final GetApplicationAttemptReportResponse response = GetApplicationAttemptReportResponse.newInstance(ApplicationHistoryClientService.this.history.getApplicationAttempt(request.getApplicationAttemptId()));
                return response;
            }
            catch (IOException e) {
                throw new ApplicationAttemptNotFoundException(e.getMessage());
            }
        }
        
        @Override
        public GetApplicationAttemptsResponse getApplicationAttempts(final GetApplicationAttemptsRequest request) throws YarnException, IOException {
            final GetApplicationAttemptsResponse response = GetApplicationAttemptsResponse.newInstance(new ArrayList<ApplicationAttemptReport>(ApplicationHistoryClientService.this.history.getApplicationAttempts(request.getApplicationId()).values()));
            return response;
        }
        
        @Override
        public GetApplicationReportResponse getApplicationReport(final GetApplicationReportRequest request) throws YarnException, IOException {
            try {
                final ApplicationId applicationId = request.getApplicationId();
                final GetApplicationReportResponse response = GetApplicationReportResponse.newInstance(ApplicationHistoryClientService.this.history.getApplication(applicationId));
                return response;
            }
            catch (IOException e) {
                throw new ApplicationNotFoundException(e.getMessage());
            }
        }
        
        @Override
        public GetApplicationsResponse getApplications(final GetApplicationsRequest request) throws YarnException, IOException {
            final GetApplicationsResponse response = GetApplicationsResponse.newInstance(new ArrayList<ApplicationReport>(ApplicationHistoryClientService.this.history.getAllApplications().values()));
            return response;
        }
        
        @Override
        public GetContainerReportResponse getContainerReport(final GetContainerReportRequest request) throws YarnException, IOException {
            try {
                final GetContainerReportResponse response = GetContainerReportResponse.newInstance(ApplicationHistoryClientService.this.history.getContainer(request.getContainerId()));
                return response;
            }
            catch (IOException e) {
                throw new ContainerNotFoundException(e.getMessage());
            }
        }
        
        @Override
        public GetContainersResponse getContainers(final GetContainersRequest request) throws YarnException, IOException {
            final GetContainersResponse response = GetContainersResponse.newInstance(new ArrayList<ContainerReport>(ApplicationHistoryClientService.this.history.getContainers(request.getApplicationAttemptId()).values()));
            return response;
        }
        
        @Override
        public GetDelegationTokenResponse getDelegationToken(final GetDelegationTokenRequest request) throws YarnException, IOException {
            return null;
        }
        
        @Override
        public RenewDelegationTokenResponse renewDelegationToken(final RenewDelegationTokenRequest request) throws YarnException, IOException {
            return null;
        }
    }
}
