// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.commons.logging.LogFactory;
import java.text.MessageFormat;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToLabelsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToLabelsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateRequest;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.Plan;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionRequest;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.Future;
import com.google.common.util.concurrent.Futures;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppMoveEvent;
import com.google.common.util.concurrent.SettableFuture;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.api.protocolrecords.MoveApplicationAcrossQueuesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.MoveApplicationAcrossQueuesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenRequest;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenRequest;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueUserAclsInfoRequest;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNodeReport;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoRequest;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest;
import org.apache.commons.lang.math.LongRange;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import java.util.EnumSet;
import java.util.Set;
import org.apache.hadoop.yarn.api.protocolrecords.ApplicationsRequestScope;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterMetricsRequest;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import java.security.AccessControlException;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationRequest;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerAppReport;
import java.util.Collection;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import java.util.Collections;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.exceptions.ContainerNotFoundException;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.exceptions.ApplicationAttemptNotFoundException;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportRequest;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import java.io.IOException;
import org.apache.hadoop.yarn.ipc.RPCUtil;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationRequest;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.InputStream;
import org.apache.hadoop.security.authorize.PolicyProvider;
import org.apache.hadoop.yarn.server.resourcemanager.security.authorize.RMPolicyProvider;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.util.UTCClock;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationInputValidator;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystem;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.server.resourcemanager.security.QueueACLsManager;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMDelegationTokenSecretManager;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.service.AbstractService;

public class ClientRMService extends AbstractService implements ApplicationClientProtocol
{
    private static final ArrayList<ApplicationReport> EMPTY_APPS_REPORT;
    private static final Log LOG;
    private final AtomicInteger applicationCounter;
    private final YarnScheduler scheduler;
    private final RMContext rmContext;
    private final RMAppManager rmAppManager;
    private Server server;
    protected RMDelegationTokenSecretManager rmDTSecretManager;
    private final RecordFactory recordFactory;
    InetSocketAddress clientBindAddress;
    private final ApplicationACLsManager applicationsACLsManager;
    private final QueueACLsManager queueACLsManager;
    private Clock clock;
    private ReservationSystem reservationSystem;
    private ReservationInputValidator rValidator;
    
    public ClientRMService(final RMContext rmContext, final YarnScheduler scheduler, final RMAppManager rmAppManager, final ApplicationACLsManager applicationACLsManager, final QueueACLsManager queueACLsManager, final RMDelegationTokenSecretManager rmDTSecretManager) {
        this(rmContext, scheduler, rmAppManager, applicationACLsManager, queueACLsManager, rmDTSecretManager, new UTCClock());
    }
    
    public ClientRMService(final RMContext rmContext, final YarnScheduler scheduler, final RMAppManager rmAppManager, final ApplicationACLsManager applicationACLsManager, final QueueACLsManager queueACLsManager, final RMDelegationTokenSecretManager rmDTSecretManager, final Clock clock) {
        super(ClientRMService.class.getName());
        this.applicationCounter = new AtomicInteger(0);
        this.recordFactory = RecordFactoryProvider.getRecordFactory(null);
        this.scheduler = scheduler;
        this.rmContext = rmContext;
        this.rmAppManager = rmAppManager;
        this.applicationsACLsManager = applicationACLsManager;
        this.queueACLsManager = queueACLsManager;
        this.rmDTSecretManager = rmDTSecretManager;
        this.reservationSystem = rmContext.getReservationSystem();
        this.clock = clock;
        this.rValidator = new ReservationInputValidator(clock);
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        this.clientBindAddress = this.getBindAddress(conf);
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        final Configuration conf = this.getConfig();
        final YarnRPC rpc = YarnRPC.create(conf);
        this.server = rpc.getServer(ApplicationClientProtocol.class, this, this.clientBindAddress, conf, this.rmDTSecretManager, conf.getInt("yarn.resourcemanager.client.thread-count", 50));
        if (conf.getBoolean("hadoop.security.authorization", false)) {
            final InputStream inputStream = this.rmContext.getConfigurationProvider().getConfigurationInputStream(conf, "hadoop-policy.xml");
            if (inputStream != null) {
                conf.addResource(inputStream);
            }
            this.refreshServiceAcls(conf, RMPolicyProvider.getInstance());
        }
        this.server.start();
        this.clientBindAddress = conf.updateConnectAddr("yarn.resourcemanager.bind-host", "yarn.resourcemanager.address", "0.0.0.0:8032", this.server.getListenerAddress());
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        if (this.server != null) {
            this.server.stop();
        }
        super.serviceStop();
    }
    
    InetSocketAddress getBindAddress(final Configuration conf) {
        return conf.getSocketAddr("yarn.resourcemanager.bind-host", "yarn.resourcemanager.address", "0.0.0.0:8032", 8032);
    }
    
    @InterfaceAudience.Private
    public InetSocketAddress getBindAddress() {
        return this.clientBindAddress;
    }
    
    private boolean checkAccess(final UserGroupInformation callerUGI, final String owner, final ApplicationAccessType operationPerformed, final RMApp application) {
        return this.applicationsACLsManager.checkAccess(callerUGI, operationPerformed, owner, application.getApplicationId()) || this.queueACLsManager.checkAccess(callerUGI, QueueACL.ADMINISTER_QUEUE, application.getQueue());
    }
    
    ApplicationId getNewApplicationId() {
        final ApplicationId applicationId = BuilderUtils.newApplicationId(this.recordFactory, ResourceManager.getClusterTimeStamp(), this.applicationCounter.incrementAndGet());
        ClientRMService.LOG.info("Allocated new applicationId: " + applicationId.getId());
        return applicationId;
    }
    
    @Override
    public GetNewApplicationResponse getNewApplication(final GetNewApplicationRequest request) throws YarnException {
        final GetNewApplicationResponse response = this.recordFactory.newRecordInstance(GetNewApplicationResponse.class);
        response.setApplicationId(this.getNewApplicationId());
        response.setMaximumResourceCapability(this.scheduler.getMaximumResourceCapability());
        return response;
    }
    
    @Override
    public GetApplicationReportResponse getApplicationReport(final GetApplicationReportRequest request) throws YarnException {
        final ApplicationId applicationId = request.getApplicationId();
        UserGroupInformation callerUGI;
        try {
            callerUGI = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ie) {
            ClientRMService.LOG.info("Error getting UGI ", ie);
            throw RPCUtil.getRemoteException(ie);
        }
        final RMApp application = this.rmContext.getRMApps().get(applicationId);
        if (application == null) {
            throw new ApplicationNotFoundException("Application with id '" + applicationId + "' doesn't exist in RM.");
        }
        final boolean allowAccess = this.checkAccess(callerUGI, application.getUser(), ApplicationAccessType.VIEW_APP, application);
        final ApplicationReport report = application.createAndGetApplicationReport(callerUGI.getUserName(), allowAccess);
        final GetApplicationReportResponse response = this.recordFactory.newRecordInstance(GetApplicationReportResponse.class);
        response.setApplicationReport(report);
        return response;
    }
    
    @Override
    public GetApplicationAttemptReportResponse getApplicationAttemptReport(final GetApplicationAttemptReportRequest request) throws YarnException, IOException {
        final ApplicationAttemptId appAttemptId = request.getApplicationAttemptId();
        UserGroupInformation callerUGI;
        try {
            callerUGI = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ie) {
            ClientRMService.LOG.info("Error getting UGI ", ie);
            throw RPCUtil.getRemoteException(ie);
        }
        final RMApp application = this.rmContext.getRMApps().get(appAttemptId.getApplicationId());
        if (application == null) {
            throw new ApplicationNotFoundException("Application with id '" + request.getApplicationAttemptId().getApplicationId() + "' doesn't exist in RM.");
        }
        final boolean allowAccess = this.checkAccess(callerUGI, application.getUser(), ApplicationAccessType.VIEW_APP, application);
        GetApplicationAttemptReportResponse response = null;
        if (!allowAccess) {
            throw new YarnException("User " + callerUGI.getShortUserName() + " does not have privilage to see this attempt " + appAttemptId);
        }
        final RMAppAttempt appAttempt = application.getAppAttempts().get(appAttemptId);
        if (appAttempt == null) {
            throw new ApplicationAttemptNotFoundException("ApplicationAttempt " + appAttemptId + " Not Found in RM");
        }
        final ApplicationAttemptReport attemptReport = appAttempt.createApplicationAttemptReport();
        response = GetApplicationAttemptReportResponse.newInstance(attemptReport);
        return response;
    }
    
    @Override
    public GetApplicationAttemptsResponse getApplicationAttempts(final GetApplicationAttemptsRequest request) throws YarnException, IOException {
        final ApplicationId appId = request.getApplicationId();
        UserGroupInformation callerUGI;
        try {
            callerUGI = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ie) {
            ClientRMService.LOG.info("Error getting UGI ", ie);
            throw RPCUtil.getRemoteException(ie);
        }
        final RMApp application = this.rmContext.getRMApps().get(appId);
        if (application == null) {
            throw new ApplicationNotFoundException("Application with id '" + appId + "' doesn't exist in RM.");
        }
        final boolean allowAccess = this.checkAccess(callerUGI, application.getUser(), ApplicationAccessType.VIEW_APP, application);
        GetApplicationAttemptsResponse response = null;
        if (allowAccess) {
            final Map<ApplicationAttemptId, RMAppAttempt> attempts = application.getAppAttempts();
            final List<ApplicationAttemptReport> listAttempts = new ArrayList<ApplicationAttemptReport>();
            final Iterator<Map.Entry<ApplicationAttemptId, RMAppAttempt>> iter = attempts.entrySet().iterator();
            while (iter.hasNext()) {
                listAttempts.add(iter.next().getValue().createApplicationAttemptReport());
            }
            response = GetApplicationAttemptsResponse.newInstance(listAttempts);
            return response;
        }
        throw new YarnException("User " + callerUGI.getShortUserName() + " does not have privilage to see this aplication " + appId);
    }
    
    @Override
    public GetContainerReportResponse getContainerReport(final GetContainerReportRequest request) throws YarnException, IOException {
        final ContainerId containerId = request.getContainerId();
        final ApplicationAttemptId appAttemptId = containerId.getApplicationAttemptId();
        final ApplicationId appId = appAttemptId.getApplicationId();
        UserGroupInformation callerUGI;
        try {
            callerUGI = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ie) {
            ClientRMService.LOG.info("Error getting UGI ", ie);
            throw RPCUtil.getRemoteException(ie);
        }
        final RMApp application = this.rmContext.getRMApps().get(appId);
        if (application == null) {
            throw new ApplicationNotFoundException("Application with id '" + appId + "' doesn't exist in RM.");
        }
        final boolean allowAccess = this.checkAccess(callerUGI, application.getUser(), ApplicationAccessType.VIEW_APP, application);
        GetContainerReportResponse response = null;
        if (!allowAccess) {
            throw new YarnException("User " + callerUGI.getShortUserName() + " does not have privilage to see this aplication " + appId);
        }
        final RMAppAttempt appAttempt = application.getAppAttempts().get(appAttemptId);
        if (appAttempt == null) {
            throw new ApplicationAttemptNotFoundException("ApplicationAttempt " + appAttemptId + " Not Found in RM");
        }
        final RMContainer rmConatiner = this.rmContext.getScheduler().getRMContainer(containerId);
        if (rmConatiner == null) {
            throw new ContainerNotFoundException("Container with id " + containerId + " not found");
        }
        response = GetContainerReportResponse.newInstance(rmConatiner.createContainerReport());
        return response;
    }
    
    @Override
    public GetContainersResponse getContainers(final GetContainersRequest request) throws YarnException, IOException {
        final ApplicationAttemptId appAttemptId = request.getApplicationAttemptId();
        final ApplicationId appId = appAttemptId.getApplicationId();
        UserGroupInformation callerUGI;
        try {
            callerUGI = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ie) {
            ClientRMService.LOG.info("Error getting UGI ", ie);
            throw RPCUtil.getRemoteException(ie);
        }
        final RMApp application = this.rmContext.getRMApps().get(appId);
        if (application == null) {
            throw new ApplicationNotFoundException("Application with id '" + appId + "' doesn't exist in RM.");
        }
        final boolean allowAccess = this.checkAccess(callerUGI, application.getUser(), ApplicationAccessType.VIEW_APP, application);
        GetContainersResponse response = null;
        if (!allowAccess) {
            throw new YarnException("User " + callerUGI.getShortUserName() + " does not have privilage to see this aplication " + appId);
        }
        final RMAppAttempt appAttempt = application.getAppAttempts().get(appAttemptId);
        if (appAttempt == null) {
            throw new ApplicationAttemptNotFoundException("ApplicationAttempt " + appAttemptId + " Not Found in RM");
        }
        Collection<RMContainer> rmContainers = (Collection<RMContainer>)Collections.emptyList();
        final SchedulerAppReport schedulerAppReport = this.rmContext.getScheduler().getSchedulerAppInfo(appAttemptId);
        if (schedulerAppReport != null) {
            rmContainers = schedulerAppReport.getLiveContainers();
        }
        final List<ContainerReport> listContainers = new ArrayList<ContainerReport>();
        for (final RMContainer rmContainer : rmContainers) {
            listContainers.add(rmContainer.createContainerReport());
        }
        response = GetContainersResponse.newInstance(listContainers);
        return response;
    }
    
    @Override
    public SubmitApplicationResponse submitApplication(final SubmitApplicationRequest request) throws YarnException {
        final ApplicationSubmissionContext submissionContext = request.getApplicationSubmissionContext();
        final ApplicationId applicationId = submissionContext.getApplicationId();
        String user = null;
        try {
            user = UserGroupInformation.getCurrentUser().getShortUserName();
        }
        catch (IOException ie) {
            ClientRMService.LOG.warn("Unable to get the current user.", ie);
            RMAuditLogger.logFailure(user, "Submit Application Request", ie.getMessage(), "ClientRMService", "Exception in submitting application", applicationId);
            throw RPCUtil.getRemoteException(ie);
        }
        if (this.rmContext.getRMApps().get(applicationId) != null) {
            ClientRMService.LOG.info("This is an earlier submitted application: " + applicationId);
            return SubmitApplicationResponse.newInstance();
        }
        if (submissionContext.getQueue() == null) {
            submissionContext.setQueue("default");
        }
        if (submissionContext.getApplicationName() == null) {
            submissionContext.setApplicationName("N/A");
        }
        if (submissionContext.getApplicationType() == null) {
            submissionContext.setApplicationType("YARN");
        }
        else if (submissionContext.getApplicationType().length() > 20) {
            submissionContext.setApplicationType(submissionContext.getApplicationType().substring(0, 20));
        }
        try {
            this.rmAppManager.submitApplication(submissionContext, System.currentTimeMillis(), user);
            ClientRMService.LOG.info("Application with id " + applicationId.getId() + " submitted by user " + user);
            RMAuditLogger.logSuccess(user, "Submit Application Request", "ClientRMService", applicationId);
        }
        catch (YarnException e) {
            ClientRMService.LOG.info("Exception in submitting application with id " + applicationId.getId(), e);
            RMAuditLogger.logFailure(user, "Submit Application Request", e.getMessage(), "ClientRMService", "Exception in submitting application", applicationId);
            throw e;
        }
        final SubmitApplicationResponse response = this.recordFactory.newRecordInstance(SubmitApplicationResponse.class);
        return response;
    }
    
    @Override
    public KillApplicationResponse forceKillApplication(final KillApplicationRequest request) throws YarnException {
        final ApplicationId applicationId = request.getApplicationId();
        UserGroupInformation callerUGI;
        try {
            callerUGI = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ie) {
            ClientRMService.LOG.info("Error getting UGI ", ie);
            RMAuditLogger.logFailure("UNKNOWN", "Kill Application Request", "UNKNOWN", "ClientRMService", "Error getting UGI", applicationId);
            throw RPCUtil.getRemoteException(ie);
        }
        final RMApp application = this.rmContext.getRMApps().get(applicationId);
        if (application == null) {
            RMAuditLogger.logFailure(callerUGI.getUserName(), "Kill Application Request", "UNKNOWN", "ClientRMService", "Trying to kill an absent application", applicationId);
            throw new ApplicationNotFoundException("Trying to kill an absent application " + applicationId);
        }
        if (!this.checkAccess(callerUGI, application.getUser(), ApplicationAccessType.MODIFY_APP, application)) {
            RMAuditLogger.logFailure(callerUGI.getShortUserName(), "Kill Application Request", "User doesn't have permissions to " + ApplicationAccessType.MODIFY_APP.toString(), "ClientRMService", "Unauthorized user", applicationId);
            throw RPCUtil.getRemoteException(new AccessControlException("User " + callerUGI.getShortUserName() + " cannot perform operation " + ApplicationAccessType.MODIFY_APP.name() + " on " + applicationId));
        }
        if (application.isAppFinalStateStored()) {
            RMAuditLogger.logSuccess(callerUGI.getShortUserName(), "Kill Application Request", "ClientRMService", applicationId);
            return KillApplicationResponse.newInstance(true);
        }
        this.rmContext.getDispatcher().getEventHandler().handle(new RMAppEvent(applicationId, RMAppEventType.KILL));
        return KillApplicationResponse.newInstance(application.getApplicationSubmissionContext().getUnmanagedAM());
    }
    
    @Override
    public GetClusterMetricsResponse getClusterMetrics(final GetClusterMetricsRequest request) throws YarnException {
        final GetClusterMetricsResponse response = this.recordFactory.newRecordInstance(GetClusterMetricsResponse.class);
        final YarnClusterMetrics ymetrics = this.recordFactory.newRecordInstance(YarnClusterMetrics.class);
        ymetrics.setNumNodeManagers(this.rmContext.getRMNodes().size());
        response.setClusterMetrics(ymetrics);
        return response;
    }
    
    @Override
    public GetApplicationsResponse getApplications(final GetApplicationsRequest request) throws YarnException {
        return this.getApplications(request, true);
    }
    
    @InterfaceAudience.Private
    public GetApplicationsResponse getApplications(final GetApplicationsRequest request, final boolean caseSensitive) throws YarnException {
        UserGroupInformation callerUGI;
        try {
            callerUGI = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ie) {
            ClientRMService.LOG.info("Error getting UGI ", ie);
            throw RPCUtil.getRemoteException(ie);
        }
        final Set<String> applicationTypes = request.getApplicationTypes();
        final EnumSet<YarnApplicationState> applicationStates = request.getApplicationStates();
        final Set<String> users = request.getUsers();
        final Set<String> queues = request.getQueues();
        final Set<String> tags = request.getApplicationTags();
        final long limit = request.getLimit();
        final LongRange start = request.getStartRange();
        final LongRange finish = request.getFinishRange();
        final ApplicationsRequestScope scope = request.getScope();
        final Map<ApplicationId, RMApp> apps = this.rmContext.getRMApps();
        Iterator<RMApp> appsIter;
        if (queues != null && !queues.isEmpty()) {
            final List<List<ApplicationAttemptId>> queueAppLists = new ArrayList<List<ApplicationAttemptId>>();
            for (final String queue : queues) {
                final List<ApplicationAttemptId> appsInQueue = this.scheduler.getAppsInQueue(queue);
                if (appsInQueue != null && !appsInQueue.isEmpty()) {
                    queueAppLists.add(appsInQueue);
                }
            }
            appsIter = new Iterator<RMApp>() {
                Iterator<List<ApplicationAttemptId>> appListIter = queueAppLists.iterator();
                Iterator<ApplicationAttemptId> schedAppsIter;
                
                @Override
                public boolean hasNext() {
                    return (this.schedAppsIter != null && this.schedAppsIter.hasNext()) || this.appListIter.hasNext();
                }
                
                @Override
                public RMApp next() {
                    if (this.schedAppsIter == null || !this.schedAppsIter.hasNext()) {
                        this.schedAppsIter = this.appListIter.next().iterator();
                    }
                    return apps.get(this.schedAppsIter.next().getApplicationId());
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Remove not supported");
                }
            };
        }
        else {
            appsIter = apps.values().iterator();
        }
        final List<ApplicationReport> reports = new ArrayList<ApplicationReport>();
        while (appsIter.hasNext() && reports.size() < limit) {
            final RMApp application = appsIter.next();
            final boolean allowAccess = this.checkAccess(callerUGI, application.getUser(), ApplicationAccessType.VIEW_APP, application);
            if (scope == ApplicationsRequestScope.OWN && !callerUGI.getUserName().equals(application.getUser())) {
                continue;
            }
            if (scope == ApplicationsRequestScope.VIEWABLE && !allowAccess) {
                continue;
            }
            if (applicationTypes != null && !applicationTypes.isEmpty()) {
                final String appTypeToMatch = caseSensitive ? application.getApplicationType() : application.getApplicationType().toLowerCase();
                if (!applicationTypes.contains(appTypeToMatch)) {
                    continue;
                }
            }
            if (applicationStates != null && !applicationStates.isEmpty() && !applicationStates.contains(application.createApplicationState())) {
                continue;
            }
            if (users != null && !users.isEmpty() && !users.contains(application.getUser())) {
                continue;
            }
            if (start != null && !start.containsLong(application.getStartTime())) {
                continue;
            }
            if (finish != null && !finish.containsLong(application.getFinishTime())) {
                continue;
            }
            if (tags != null && !tags.isEmpty()) {
                final Set<String> appTags = application.getApplicationTags();
                if (appTags == null) {
                    continue;
                }
                if (appTags.isEmpty()) {
                    continue;
                }
                boolean match = false;
                for (final String tag : tags) {
                    if (appTags.contains(tag)) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    continue;
                }
            }
            reports.add(application.createAndGetApplicationReport(callerUGI.getUserName(), allowAccess));
        }
        final GetApplicationsResponse response = this.recordFactory.newRecordInstance(GetApplicationsResponse.class);
        response.setApplicationList(reports);
        return response;
    }
    
    @Override
    public GetClusterNodesResponse getClusterNodes(final GetClusterNodesRequest request) throws YarnException {
        final GetClusterNodesResponse response = this.recordFactory.newRecordInstance(GetClusterNodesResponse.class);
        EnumSet<NodeState> nodeStates = request.getNodeStates();
        if (nodeStates == null || nodeStates.isEmpty()) {
            nodeStates = EnumSet.allOf(NodeState.class);
        }
        final Collection<RMNode> nodes = RMServerUtils.queryRMNodes(this.rmContext, nodeStates);
        final List<NodeReport> nodeReports = new ArrayList<NodeReport>(nodes.size());
        for (final RMNode nodeInfo : nodes) {
            nodeReports.add(this.createNodeReports(nodeInfo));
        }
        response.setNodeReports(nodeReports);
        return response;
    }
    
    @Override
    public GetQueueInfoResponse getQueueInfo(final GetQueueInfoRequest request) throws YarnException {
        final GetQueueInfoResponse response = this.recordFactory.newRecordInstance(GetQueueInfoResponse.class);
        try {
            final QueueInfo queueInfo = this.scheduler.getQueueInfo(request.getQueueName(), request.getIncludeChildQueues(), request.getRecursive());
            List<ApplicationReport> appReports = ClientRMService.EMPTY_APPS_REPORT;
            if (request.getIncludeApplications()) {
                final List<ApplicationAttemptId> apps = this.scheduler.getAppsInQueue(request.getQueueName());
                appReports = new ArrayList<ApplicationReport>(apps.size());
                for (final ApplicationAttemptId app : apps) {
                    final RMApp rmApp = this.rmContext.getRMApps().get(app.getApplicationId());
                    appReports.add(rmApp.createAndGetApplicationReport(null, true));
                }
            }
            queueInfo.setApplications(appReports);
            response.setQueueInfo(queueInfo);
        }
        catch (IOException ioe) {
            ClientRMService.LOG.info("Failed to getQueueInfo for " + request.getQueueName(), ioe);
        }
        return response;
    }
    
    private NodeReport createNodeReports(final RMNode rmNode) {
        final SchedulerNodeReport schedulerNodeReport = this.scheduler.getNodeReport(rmNode.getNodeID());
        Resource used = BuilderUtils.newResource(0, 0);
        int numContainers = 0;
        if (schedulerNodeReport != null) {
            used = schedulerNodeReport.getUsedResource();
            numContainers = schedulerNodeReport.getNumContainers();
        }
        final NodeReport report = BuilderUtils.newNodeReport(rmNode.getNodeID(), rmNode.getState(), rmNode.getHttpAddress(), rmNode.getRackName(), used, rmNode.getTotalCapability(), numContainers, rmNode.getHealthReport(), rmNode.getLastHealthReportTime(), rmNode.getNodeLabels());
        return report;
    }
    
    @Override
    public GetQueueUserAclsInfoResponse getQueueUserAcls(final GetQueueUserAclsInfoRequest request) throws YarnException {
        final GetQueueUserAclsInfoResponse response = this.recordFactory.newRecordInstance(GetQueueUserAclsInfoResponse.class);
        response.setUserAclsInfoList(this.scheduler.getQueueUserAclInfo());
        return response;
    }
    
    @Override
    public GetDelegationTokenResponse getDelegationToken(final GetDelegationTokenRequest request) throws YarnException {
        try {
            if (!this.isAllowedDelegationTokenOp()) {
                throw new IOException("Delegation Token can be issued only with kerberos authentication");
            }
            final GetDelegationTokenResponse response = this.recordFactory.newRecordInstance(GetDelegationTokenResponse.class);
            final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
            final Text owner = new Text(ugi.getUserName());
            Text realUser = null;
            if (ugi.getRealUser() != null) {
                realUser = new Text(ugi.getRealUser().getUserName());
            }
            final RMDelegationTokenIdentifier tokenIdentifier = new RMDelegationTokenIdentifier(owner, new Text(request.getRenewer()), realUser);
            final Token<RMDelegationTokenIdentifier> realRMDTtoken = new Token<RMDelegationTokenIdentifier>(tokenIdentifier, this.rmDTSecretManager);
            response.setRMDelegationToken(BuilderUtils.newDelegationToken(realRMDTtoken.getIdentifier(), realRMDTtoken.getKind().toString(), realRMDTtoken.getPassword(), realRMDTtoken.getService().toString()));
            return response;
        }
        catch (IOException io) {
            throw RPCUtil.getRemoteException(io);
        }
    }
    
    @Override
    public RenewDelegationTokenResponse renewDelegationToken(final RenewDelegationTokenRequest request) throws YarnException {
        try {
            if (!this.isAllowedDelegationTokenOp()) {
                throw new IOException("Delegation Token can be renewed only with kerberos authentication");
            }
            final org.apache.hadoop.yarn.api.records.Token protoToken = request.getDelegationToken();
            final Token<RMDelegationTokenIdentifier> token = new Token<RMDelegationTokenIdentifier>(protoToken.getIdentifier().array(), protoToken.getPassword().array(), new Text(protoToken.getKind()), new Text(protoToken.getService()));
            final String user = this.getRenewerForToken(token);
            final long nextExpTime = this.rmDTSecretManager.renewToken(token, user);
            final RenewDelegationTokenResponse renewResponse = Records.newRecord(RenewDelegationTokenResponse.class);
            renewResponse.setNextExpirationTime(nextExpTime);
            return renewResponse;
        }
        catch (IOException e) {
            throw RPCUtil.getRemoteException(e);
        }
    }
    
    @Override
    public CancelDelegationTokenResponse cancelDelegationToken(final CancelDelegationTokenRequest request) throws YarnException {
        try {
            if (!this.isAllowedDelegationTokenOp()) {
                throw new IOException("Delegation Token can be cancelled only with kerberos authentication");
            }
            final org.apache.hadoop.yarn.api.records.Token protoToken = request.getDelegationToken();
            final Token<RMDelegationTokenIdentifier> token = new Token<RMDelegationTokenIdentifier>(protoToken.getIdentifier().array(), protoToken.getPassword().array(), new Text(protoToken.getKind()), new Text(protoToken.getService()));
            final String user = UserGroupInformation.getCurrentUser().getUserName();
            this.rmDTSecretManager.cancelToken(token, user);
            return Records.newRecord(CancelDelegationTokenResponse.class);
        }
        catch (IOException e) {
            throw RPCUtil.getRemoteException(e);
        }
    }
    
    @Override
    public MoveApplicationAcrossQueuesResponse moveApplicationAcrossQueues(final MoveApplicationAcrossQueuesRequest request) throws YarnException {
        final ApplicationId applicationId = request.getApplicationId();
        UserGroupInformation callerUGI;
        try {
            callerUGI = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ie) {
            ClientRMService.LOG.info("Error getting UGI ", ie);
            RMAuditLogger.logFailure("UNKNOWN", "Move Application Request", "UNKNOWN", "ClientRMService", "Error getting UGI", applicationId);
            throw RPCUtil.getRemoteException(ie);
        }
        final RMApp application = this.rmContext.getRMApps().get(applicationId);
        if (application == null) {
            RMAuditLogger.logFailure(callerUGI.getUserName(), "Move Application Request", "UNKNOWN", "ClientRMService", "Trying to move an absent application", applicationId);
            throw new ApplicationNotFoundException("Trying to move an absent application " + applicationId);
        }
        if (!this.checkAccess(callerUGI, application.getUser(), ApplicationAccessType.MODIFY_APP, application)) {
            RMAuditLogger.logFailure(callerUGI.getShortUserName(), "Move Application Request", "User doesn't have permissions to " + ApplicationAccessType.MODIFY_APP.toString(), "ClientRMService", "Unauthorized user", applicationId);
            throw RPCUtil.getRemoteException(new AccessControlException("User " + callerUGI.getShortUserName() + " cannot perform operation " + ApplicationAccessType.MODIFY_APP.name() + " on " + applicationId));
        }
        if (EnumSet.of(RMAppState.NEW, new RMAppState[] { RMAppState.NEW_SAVING, RMAppState.FAILED, RMAppState.FINAL_SAVING, RMAppState.FINISHING, RMAppState.FINISHED, RMAppState.KILLED, RMAppState.KILLING, RMAppState.FAILED }).contains(application.getState())) {
            final String msg = "App in " + application.getState() + " state cannot be moved.";
            RMAuditLogger.logFailure(callerUGI.getShortUserName(), "Move Application Request", "UNKNOWN", "ClientRMService", msg);
            throw new YarnException(msg);
        }
        final SettableFuture<Object> future = SettableFuture.create();
        this.rmContext.getDispatcher().getEventHandler().handle(new RMAppMoveEvent(applicationId, request.getTargetQueue(), future));
        try {
            Futures.get(future, YarnException.class);
        }
        catch (YarnException ex) {
            RMAuditLogger.logFailure(callerUGI.getShortUserName(), "Move Application Request", "UNKNOWN", "ClientRMService", ex.getMessage());
            throw ex;
        }
        RMAuditLogger.logSuccess(callerUGI.getShortUserName(), "Move Application Request", "ClientRMService", applicationId);
        final MoveApplicationAcrossQueuesResponse response = this.recordFactory.newRecordInstance(MoveApplicationAcrossQueuesResponse.class);
        return response;
    }
    
    private String getRenewerForToken(final Token<RMDelegationTokenIdentifier> token) throws IOException {
        final UserGroupInformation user = UserGroupInformation.getCurrentUser();
        final UserGroupInformation loginUser = UserGroupInformation.getLoginUser();
        return loginUser.getUserName().equals(user.getUserName()) ? token.decodeIdentifier().getRenewer().toString() : user.getShortUserName();
    }
    
    void refreshServiceAcls(final Configuration configuration, final PolicyProvider policyProvider) {
        this.server.refreshServiceAclWithLoadedConfiguration(configuration, policyProvider);
    }
    
    private boolean isAllowedDelegationTokenOp() throws IOException {
        return !UserGroupInformation.isSecurityEnabled() || EnumSet.of(UserGroupInformation.AuthenticationMethod.KERBEROS, UserGroupInformation.AuthenticationMethod.KERBEROS_SSL, UserGroupInformation.AuthenticationMethod.CERTIFICATE).contains(UserGroupInformation.getCurrentUser().getRealAuthenticationMethod());
    }
    
    @VisibleForTesting
    public Server getServer() {
        return this.server;
    }
    
    @Override
    public ReservationSubmissionResponse submitReservation(final ReservationSubmissionRequest request) throws YarnException, IOException {
        this.checkReservationSytem("Submit Reservation Request");
        final ReservationSubmissionResponse response = this.recordFactory.newRecordInstance(ReservationSubmissionResponse.class);
        final ReservationId reservationId = this.reservationSystem.getNewReservationId();
        final Plan plan = this.rValidator.validateReservationSubmissionRequest(this.reservationSystem, request, reservationId);
        final String queueName = request.getQueue();
        final String user = this.checkReservationACLs(queueName, "Submit Reservation Request");
        try {
            final boolean result = plan.getReservationAgent().createReservation(reservationId, user, plan, request.getReservationDefinition());
            if (result) {
                this.reservationSystem.setQueueForReservation(reservationId, queueName);
                this.refreshScheduler(queueName, request.getReservationDefinition(), reservationId.toString());
                response.setReservationId(reservationId);
            }
        }
        catch (PlanningException e) {
            RMAuditLogger.logFailure(user, "Submit Reservation Request", e.getMessage(), "ClientRMService", "Unable to create the reservation: " + reservationId);
            throw RPCUtil.getRemoteException(e);
        }
        RMAuditLogger.logSuccess(user, "Submit Reservation Request", "ClientRMService: " + reservationId);
        return response;
    }
    
    @Override
    public ReservationUpdateResponse updateReservation(final ReservationUpdateRequest request) throws YarnException, IOException {
        this.checkReservationSytem("Update Reservation Request");
        final ReservationUpdateResponse response = this.recordFactory.newRecordInstance(ReservationUpdateResponse.class);
        final Plan plan = this.rValidator.validateReservationUpdateRequest(this.reservationSystem, request);
        final ReservationId reservationId = request.getReservationId();
        final String queueName = this.reservationSystem.getQueueForReservation(reservationId);
        final String user = this.checkReservationACLs(queueName, "Update Reservation Request");
        try {
            final boolean result = plan.getReservationAgent().updateReservation(reservationId, user, plan, request.getReservationDefinition());
            if (!result) {
                final String errMsg = "Unable to update reservation: " + reservationId;
                RMAuditLogger.logFailure(user, "Update Reservation Request", errMsg, "ClientRMService", errMsg);
                throw RPCUtil.getRemoteException(errMsg);
            }
        }
        catch (PlanningException e) {
            RMAuditLogger.logFailure(user, "Update Reservation Request", e.getMessage(), "ClientRMService", "Unable to update the reservation: " + reservationId);
            throw RPCUtil.getRemoteException(e);
        }
        RMAuditLogger.logSuccess(user, "Update Reservation Request", "ClientRMService: " + reservationId);
        return response;
    }
    
    @Override
    public ReservationDeleteResponse deleteReservation(final ReservationDeleteRequest request) throws YarnException, IOException {
        this.checkReservationSytem("Delete Reservation Request");
        final ReservationDeleteResponse response = this.recordFactory.newRecordInstance(ReservationDeleteResponse.class);
        final Plan plan = this.rValidator.validateReservationDeleteRequest(this.reservationSystem, request);
        final ReservationId reservationId = request.getReservationId();
        final String queueName = this.reservationSystem.getQueueForReservation(reservationId);
        final String user = this.checkReservationACLs(queueName, "Delete Reservation Request");
        try {
            final boolean result = plan.getReservationAgent().deleteReservation(reservationId, user, plan);
            if (!result) {
                final String errMsg = "Could not delete reservation: " + reservationId;
                RMAuditLogger.logFailure(user, "Delete Reservation Request", errMsg, "ClientRMService", errMsg);
                throw RPCUtil.getRemoteException(errMsg);
            }
        }
        catch (PlanningException e) {
            RMAuditLogger.logFailure(user, "Delete Reservation Request", e.getMessage(), "ClientRMService", "Unable to delete the reservation: " + reservationId);
            throw RPCUtil.getRemoteException(e);
        }
        RMAuditLogger.logSuccess(user, "Delete Reservation Request", "ClientRMService: " + reservationId);
        return response;
    }
    
    @Override
    public GetNodesToLabelsResponse getNodeToLabels(final GetNodesToLabelsRequest request) throws YarnException, IOException {
        final RMNodeLabelsManager labelsMgr = this.rmContext.getNodeLabelManager();
        final GetNodesToLabelsResponse response = GetNodesToLabelsResponse.newInstance(labelsMgr.getNodeLabels());
        return response;
    }
    
    @Override
    public GetClusterNodeLabelsResponse getClusterNodeLabels(final GetClusterNodeLabelsRequest request) throws YarnException, IOException {
        final RMNodeLabelsManager labelsMgr = this.rmContext.getNodeLabelManager();
        final GetClusterNodeLabelsResponse response = GetClusterNodeLabelsResponse.newInstance(labelsMgr.getClusterNodeLabels());
        return response;
    }
    
    private void checkReservationSytem(final String auditConstant) throws YarnException {
        if (this.reservationSystem == null) {
            throw RPCUtil.getRemoteException("Reservation is not enabled. Please enable & try again");
        }
    }
    
    private void refreshScheduler(final String planName, final ReservationDefinition contract, final String reservationId) {
        if (contract.getArrival() - this.clock.getTime() < this.reservationSystem.getPlanFollowerTimeStep()) {
            ClientRMService.LOG.debug(MessageFormat.format("Reservation {0} is within threshold so attempting to create synchronously.", reservationId));
            this.reservationSystem.synchronizePlan(planName);
            ClientRMService.LOG.info(MessageFormat.format("Created reservation {0} synchronously.", reservationId));
        }
    }
    
    private String checkReservationACLs(final String queueName, final String auditConstant) throws YarnException {
        UserGroupInformation callerUGI;
        try {
            callerUGI = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ie) {
            RMAuditLogger.logFailure("UNKNOWN", auditConstant, queueName, "ClientRMService", "Error getting UGI");
            throw RPCUtil.getRemoteException(ie);
        }
        if (!this.queueACLsManager.checkAccess(callerUGI, QueueACL.SUBMIT_APPLICATIONS, queueName)) {
            RMAuditLogger.logFailure(callerUGI.getShortUserName(), auditConstant, "User doesn't have permissions to " + QueueACL.SUBMIT_APPLICATIONS.toString(), "ClientRMService", "Unauthorized user");
            throw RPCUtil.getRemoteException(new AccessControlException("User " + callerUGI.getShortUserName() + " cannot perform operation " + QueueACL.SUBMIT_APPLICATIONS.name() + " on queue" + queueName));
        }
        return callerUGI.getShortUserName();
    }
    
    static {
        EMPTY_APPS_REPORT = new ArrayList<ApplicationReport>();
        LOG = LogFactory.getLog(ClientRMService.class);
    }
}
