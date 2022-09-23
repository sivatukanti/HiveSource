// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.commons.logging.LogFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.api.records.PreemptionResourceRequest;
import org.apache.hadoop.yarn.api.records.PreemptionContract;
import org.apache.hadoop.yarn.api.records.PreemptionContainer;
import java.util.HashSet;
import org.apache.hadoop.yarn.api.records.StrictPreemptionContract;
import org.apache.hadoop.yarn.api.records.PreemptionMessage;
import org.apache.hadoop.yarn.server.security.MasterKeyData;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNodeReport;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Allocation;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ResourceBlacklistRequest;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptImpl;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.api.records.NodeReport;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.exceptions.InvalidContainerReleaseException;
import org.apache.hadoop.yarn.exceptions.InvalidResourceBlacklistRequestException;
import org.apache.hadoop.yarn.exceptions.InvalidResourceRequestException;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import java.util.Collections;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptStatusupdateEvent;
import org.apache.hadoop.yarn.exceptions.ApplicationAttemptNotFoundException;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptUnregistrationEvent;
import org.apache.hadoop.yarn.exceptions.ApplicationMasterNotRegisteredException;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import java.util.List;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.net.UnknownHostException;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.NMToken;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.AbstractYarnScheduler;
import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptRegistrationEvent;
import org.apache.hadoop.yarn.exceptions.InvalidApplicationMasterRequestException;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.ipc.RPCUtil;
import org.apache.hadoop.util.StringUtils;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.security.UserGroupInformation;
import java.io.InputStream;
import org.apache.hadoop.security.authorize.PolicyProvider;
import org.apache.hadoop.yarn.server.resourcemanager.security.authorize.RMPolicyProvider;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.security.SaslRpcServer;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.ipc.Server;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.AMLivelinessMonitor;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Private
public class ApplicationMasterService extends AbstractService implements ApplicationMasterProtocol
{
    private static final Log LOG;
    private final AMLivelinessMonitor amLivelinessMonitor;
    private YarnScheduler rScheduler;
    private InetSocketAddress bindAddress;
    private Server server;
    private final RecordFactory recordFactory;
    private final ConcurrentMap<ApplicationAttemptId, AllocateResponseLock> responseMap;
    private final RMContext rmContext;
    
    public ApplicationMasterService(final RMContext rmContext, final YarnScheduler scheduler) {
        super(ApplicationMasterService.class.getName());
        this.recordFactory = RecordFactoryProvider.getRecordFactory(null);
        this.responseMap = new ConcurrentHashMap<ApplicationAttemptId, AllocateResponseLock>();
        this.amLivelinessMonitor = rmContext.getAMLivelinessMonitor();
        this.rScheduler = scheduler;
        this.rmContext = rmContext;
    }
    
    @Override
    protected void serviceStart() throws Exception {
        final Configuration conf = this.getConfig();
        final YarnRPC rpc = YarnRPC.create(conf);
        final InetSocketAddress masterServiceAddress = conf.getSocketAddr("yarn.resourcemanager.bind-host", "yarn.resourcemanager.scheduler.address", "0.0.0.0:8030", 8030);
        Configuration serverConf = conf;
        serverConf = new Configuration(conf);
        serverConf.set("hadoop.security.authentication", SaslRpcServer.AuthMethod.TOKEN.toString());
        this.server = rpc.getServer(ApplicationMasterProtocol.class, this, masterServiceAddress, serverConf, this.rmContext.getAMRMTokenSecretManager(), serverConf.getInt("yarn.resourcemanager.scheduler.client.thread-count", 50));
        if (conf.getBoolean("hadoop.security.authorization", false)) {
            final InputStream inputStream = this.rmContext.getConfigurationProvider().getConfigurationInputStream(conf, "hadoop-policy.xml");
            if (inputStream != null) {
                conf.addResource(inputStream);
            }
            this.refreshServiceAcls(conf, RMPolicyProvider.getInstance());
        }
        this.server.start();
        this.bindAddress = conf.updateConnectAddr("yarn.resourcemanager.bind-host", "yarn.resourcemanager.scheduler.address", "0.0.0.0:8030", this.server.getListenerAddress());
        super.serviceStart();
    }
    
    @InterfaceAudience.Private
    public InetSocketAddress getBindAddress() {
        return this.bindAddress;
    }
    
    private AMRMTokenIdentifier selectAMRMTokenIdentifier(final UserGroupInformation remoteUgi) throws IOException {
        AMRMTokenIdentifier result = null;
        final Set<TokenIdentifier> tokenIds = remoteUgi.getTokenIdentifiers();
        for (final TokenIdentifier tokenId : tokenIds) {
            if (tokenId instanceof AMRMTokenIdentifier) {
                result = (AMRMTokenIdentifier)tokenId;
                break;
            }
        }
        return result;
    }
    
    private AMRMTokenIdentifier authorizeRequest() throws YarnException {
        UserGroupInformation remoteUgi;
        try {
            remoteUgi = UserGroupInformation.getCurrentUser();
        }
        catch (IOException e) {
            final String msg = "Cannot obtain the user-name for authorizing ApplicationMaster. Got exception: " + StringUtils.stringifyException(e);
            ApplicationMasterService.LOG.warn(msg);
            throw RPCUtil.getRemoteException(msg);
        }
        boolean tokenFound = false;
        String message = "";
        AMRMTokenIdentifier appTokenIdentifier = null;
        try {
            appTokenIdentifier = this.selectAMRMTokenIdentifier(remoteUgi);
            if (appTokenIdentifier == null) {
                tokenFound = false;
                message = "No AMRMToken found for user " + remoteUgi.getUserName();
            }
            else {
                tokenFound = true;
            }
        }
        catch (IOException e2) {
            tokenFound = false;
            message = "Got exception while looking for AMRMToken for user " + remoteUgi.getUserName();
        }
        if (!tokenFound) {
            ApplicationMasterService.LOG.warn(message);
            throw RPCUtil.getRemoteException(message);
        }
        return appTokenIdentifier;
    }
    
    @Override
    public RegisterApplicationMasterResponse registerApplicationMaster(final RegisterApplicationMasterRequest request) throws YarnException, IOException {
        final AMRMTokenIdentifier amrmTokenIdentifier = this.authorizeRequest();
        final ApplicationAttemptId applicationAttemptId = amrmTokenIdentifier.getApplicationAttemptId();
        final ApplicationId appID = applicationAttemptId.getApplicationId();
        final AllocateResponseLock lock = this.responseMap.get(applicationAttemptId);
        if (lock == null) {
            RMAuditLogger.logFailure(this.rmContext.getRMApps().get(appID).getUser(), "Register App Master", "Application doesn't exist in cache " + applicationAttemptId, "ApplicationMasterService", "Error in registering application master", appID, applicationAttemptId);
            this.throwApplicationDoesNotExistInCacheException(applicationAttemptId);
        }
        synchronized (lock) {
            final AllocateResponse lastResponse = lock.getAllocateResponse();
            if (this.hasApplicationMasterRegistered(applicationAttemptId)) {
                final String message = "Application Master is already registered : " + appID;
                ApplicationMasterService.LOG.warn(message);
                RMAuditLogger.logFailure(this.rmContext.getRMApps().get(appID).getUser(), "Register App Master", "", "ApplicationMasterService", message, appID, applicationAttemptId);
                throw new InvalidApplicationMasterRequestException(message);
            }
            this.amLivelinessMonitor.receivedPing(applicationAttemptId);
            final RMApp app = this.rmContext.getRMApps().get(appID);
            lastResponse.setResponseId(0);
            lock.setAllocateResponse(lastResponse);
            ApplicationMasterService.LOG.info("AM registration " + applicationAttemptId);
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppAttemptRegistrationEvent(applicationAttemptId, request.getHost(), request.getRpcPort(), request.getTrackingUrl()));
            RMAuditLogger.logSuccess(app.getUser(), "Register App Master", "ApplicationMasterService", appID, applicationAttemptId);
            final RegisterApplicationMasterResponse response = this.recordFactory.newRecordInstance(RegisterApplicationMasterResponse.class);
            response.setMaximumResourceCapability(this.rScheduler.getMaximumResourceCapability());
            response.setApplicationACLs(app.getRMAppAttempt(applicationAttemptId).getSubmissionContext().getAMContainerSpec().getApplicationACLs());
            response.setQueue(app.getQueue());
            if (UserGroupInformation.isSecurityEnabled()) {
                ApplicationMasterService.LOG.info("Setting client token master key");
                response.setClientToAMTokenMasterKey(ByteBuffer.wrap(this.rmContext.getClientToAMTokenSecretManager().getMasterKey(applicationAttemptId).getEncoded()));
            }
            final List<Container> transferredContainers = (List<Container>)((AbstractYarnScheduler)this.rScheduler).getTransferredContainers(applicationAttemptId);
            if (!transferredContainers.isEmpty()) {
                response.setContainersFromPreviousAttempts(transferredContainers);
                final List<NMToken> nmTokens = new ArrayList<NMToken>();
                for (final Container container : transferredContainers) {
                    try {
                        final NMToken token = this.rmContext.getNMTokenSecretManager().createAndGetNMToken(app.getUser(), applicationAttemptId, container);
                        if (null == token) {
                            continue;
                        }
                        nmTokens.add(token);
                    }
                    catch (IllegalArgumentException e) {
                        if (e.getCause() instanceof UnknownHostException) {
                            throw (UnknownHostException)e.getCause();
                        }
                        continue;
                    }
                }
                response.setNMTokensFromPreviousAttempts(nmTokens);
                ApplicationMasterService.LOG.info("Application " + appID + " retrieved " + transferredContainers.size() + " containers from previous" + " attempts and " + nmTokens.size() + " NM tokens.");
            }
            response.setSchedulerResourceTypes(this.rScheduler.getSchedulingResourceTypes());
            return response;
        }
    }
    
    @Override
    public FinishApplicationMasterResponse finishApplicationMaster(final FinishApplicationMasterRequest request) throws YarnException, IOException {
        final ApplicationAttemptId applicationAttemptId = this.authorizeRequest().getApplicationAttemptId();
        final ApplicationId appId = applicationAttemptId.getApplicationId();
        final RMApp rmApp = this.rmContext.getRMApps().get(applicationAttemptId.getApplicationId());
        if (rmApp.isAppFinalStateStored()) {
            ApplicationMasterService.LOG.info(rmApp.getApplicationId() + " unregistered successfully. ");
            return FinishApplicationMasterResponse.newInstance(true);
        }
        final AllocateResponseLock lock = this.responseMap.get(applicationAttemptId);
        if (lock == null) {
            this.throwApplicationDoesNotExistInCacheException(applicationAttemptId);
        }
        synchronized (lock) {
            if (!this.hasApplicationMasterRegistered(applicationAttemptId)) {
                final String message = "Application Master is trying to unregister before registering for: " + appId;
                ApplicationMasterService.LOG.error(message);
                RMAuditLogger.logFailure(this.rmContext.getRMApps().get(appId).getUser(), "Unregister App Master", "", "ApplicationMasterService", message, appId, applicationAttemptId);
                throw new ApplicationMasterNotRegisteredException(message);
            }
            this.amLivelinessMonitor.receivedPing(applicationAttemptId);
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppAttemptUnregistrationEvent(applicationAttemptId, request.getTrackingUrl(), request.getFinalApplicationStatus(), request.getDiagnostics()));
            return FinishApplicationMasterResponse.newInstance(rmApp.getApplicationSubmissionContext().getUnmanagedAM());
        }
    }
    
    private void throwApplicationDoesNotExistInCacheException(final ApplicationAttemptId appAttemptId) throws InvalidApplicationMasterRequestException {
        final String message = "Application doesn't exist in cache " + appAttemptId;
        ApplicationMasterService.LOG.error(message);
        throw new InvalidApplicationMasterRequestException(message);
    }
    
    public boolean hasApplicationMasterRegistered(final ApplicationAttemptId appAttemptId) {
        boolean hasApplicationMasterRegistered = false;
        final AllocateResponseLock lastResponse = this.responseMap.get(appAttemptId);
        if (lastResponse != null) {
            synchronized (lastResponse) {
                if (lastResponse.getAllocateResponse() != null && lastResponse.getAllocateResponse().getResponseId() >= 0) {
                    hasApplicationMasterRegistered = true;
                }
            }
        }
        return hasApplicationMasterRegistered;
    }
    
    @Override
    public AllocateResponse allocate(final AllocateRequest request) throws YarnException, IOException {
        final AMRMTokenIdentifier amrmTokenIdentifier = this.authorizeRequest();
        final ApplicationAttemptId appAttemptId = amrmTokenIdentifier.getApplicationAttemptId();
        final ApplicationId applicationId = appAttemptId.getApplicationId();
        this.amLivelinessMonitor.receivedPing(appAttemptId);
        final AllocateResponseLock lock = this.responseMap.get(appAttemptId);
        if (lock == null) {
            final String message = "Application attempt " + appAttemptId + " doesn't exist in ApplicationMasterService cache.";
            ApplicationMasterService.LOG.error(message);
            throw new ApplicationAttemptNotFoundException(message);
        }
        synchronized (lock) {
            final AllocateResponse lastResponse = lock.getAllocateResponse();
            if (!this.hasApplicationMasterRegistered(appAttemptId)) {
                final String message2 = "AM is not registered for known application attempt: " + appAttemptId + " or RM had restarted after AM registered . AM should re-register.";
                ApplicationMasterService.LOG.info(message2);
                RMAuditLogger.logFailure(this.rmContext.getRMApps().get(appAttemptId.getApplicationId()).getUser(), "App Master Heartbeats", "", "ApplicationMasterService", message2, applicationId, appAttemptId);
                throw new ApplicationMasterNotRegisteredException(message2);
            }
            if (request.getResponseId() + 1 == lastResponse.getResponseId()) {
                return lastResponse;
            }
            if (request.getResponseId() + 1 < lastResponse.getResponseId()) {
                final String message2 = "Invalid responseId in AllocateRequest from application attempt: " + appAttemptId + ", expect responseId to be " + (lastResponse.getResponseId() + 1);
                throw new InvalidApplicationMasterRequestException(message2);
            }
            final float filteredProgress = request.getProgress();
            if (Float.isNaN(filteredProgress) || filteredProgress == Float.NEGATIVE_INFINITY || filteredProgress < 0.0f) {
                request.setProgress(0.0f);
            }
            else if (filteredProgress > 1.0f || filteredProgress == Float.POSITIVE_INFINITY) {
                request.setProgress(1.0f);
            }
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppAttemptStatusupdateEvent(appAttemptId, request.getProgress()));
            final List<ResourceRequest> ask = request.getAskList();
            final List<ContainerId> release = request.getReleaseList();
            final ResourceBlacklistRequest blacklistRequest = request.getResourceBlacklistRequest();
            final List<String> blacklistAdditions = (blacklistRequest != null) ? blacklistRequest.getBlacklistAdditions() : Collections.EMPTY_LIST;
            final List<String> blacklistRemovals = (blacklistRequest != null) ? blacklistRequest.getBlacklistRemovals() : Collections.EMPTY_LIST;
            final RMApp app = this.rmContext.getRMApps().get(applicationId);
            final ApplicationSubmissionContext asc = app.getApplicationSubmissionContext();
            for (final ResourceRequest req : ask) {
                if (null == req.getNodeLabelExpression()) {
                    req.setNodeLabelExpression(asc.getNodeLabelExpression());
                }
            }
            try {
                RMServerUtils.validateResourceRequests(ask, this.rScheduler.getMaximumResourceCapability(), app.getQueue(), this.rScheduler);
            }
            catch (InvalidResourceRequestException e) {
                ApplicationMasterService.LOG.warn("Invalid resource ask by application " + appAttemptId, e);
                throw e;
            }
            try {
                RMServerUtils.validateBlacklistRequest(blacklistRequest);
            }
            catch (InvalidResourceBlacklistRequestException e2) {
                ApplicationMasterService.LOG.warn("Invalid blacklist request by application " + appAttemptId, e2);
                throw e2;
            }
            if (!app.getApplicationSubmissionContext().getKeepContainersAcrossApplicationAttempts()) {
                try {
                    RMServerUtils.validateContainerReleaseRequest(release, appAttemptId);
                }
                catch (InvalidContainerReleaseException e3) {
                    ApplicationMasterService.LOG.warn("Invalid container release by application " + appAttemptId, e3);
                    throw e3;
                }
            }
            final Allocation allocation = this.rScheduler.allocate(appAttemptId, ask, release, blacklistAdditions, blacklistRemovals);
            if (!blacklistAdditions.isEmpty() || !blacklistRemovals.isEmpty()) {
                ApplicationMasterService.LOG.info("blacklist are updated in Scheduler.blacklistAdditions: " + blacklistAdditions + ", " + "blacklistRemovals: " + blacklistRemovals);
            }
            final RMAppAttempt appAttempt = app.getRMAppAttempt(appAttemptId);
            final AllocateResponse allocateResponse = this.recordFactory.newRecordInstance(AllocateResponse.class);
            if (!allocation.getContainers().isEmpty()) {
                allocateResponse.setNMTokens(allocation.getNMTokens());
            }
            final List<RMNode> updatedNodes = new ArrayList<RMNode>();
            if (app.pullRMNodeUpdates(updatedNodes) > 0) {
                final List<NodeReport> updatedNodeReports = new ArrayList<NodeReport>();
                for (final RMNode rmNode : updatedNodes) {
                    final SchedulerNodeReport schedulerNodeReport = this.rScheduler.getNodeReport(rmNode.getNodeID());
                    Resource used = BuilderUtils.newResource(0, 0);
                    int numContainers = 0;
                    if (schedulerNodeReport != null) {
                        used = schedulerNodeReport.getUsedResource();
                        numContainers = schedulerNodeReport.getNumContainers();
                    }
                    final NodeId nodeId = rmNode.getNodeID();
                    final NodeReport report = BuilderUtils.newNodeReport(nodeId, rmNode.getState(), rmNode.getHttpAddress(), rmNode.getRackName(), used, rmNode.getTotalCapability(), numContainers, rmNode.getHealthReport(), rmNode.getLastHealthReportTime(), rmNode.getNodeLabels());
                    updatedNodeReports.add(report);
                }
                allocateResponse.setUpdatedNodes(updatedNodeReports);
            }
            allocateResponse.setAllocatedContainers(allocation.getContainers());
            allocateResponse.setCompletedContainersStatuses(appAttempt.pullJustFinishedContainers());
            allocateResponse.setResponseId(lastResponse.getResponseId() + 1);
            allocateResponse.setAvailableResources(allocation.getResourceLimit());
            allocateResponse.setNumClusterNodes(this.rScheduler.getNumClusterNodes());
            allocateResponse.setPreemptionMessage(this.generatePreemptionMessage(allocation));
            final MasterKeyData nextMasterKey = this.rmContext.getAMRMTokenSecretManager().getNextMasterKeyData();
            if (nextMasterKey != null && nextMasterKey.getMasterKey().getKeyId() != amrmTokenIdentifier.getKeyId()) {
                final org.apache.hadoop.security.token.Token<AMRMTokenIdentifier> amrmToken = this.rmContext.getAMRMTokenSecretManager().createAndGetAMRMToken(appAttemptId);
                ((RMAppAttemptImpl)appAttempt).setAMRMToken(amrmToken);
                allocateResponse.setAMRMToken(Token.newInstance(amrmToken.getIdentifier(), amrmToken.getKind().toString(), amrmToken.getPassword(), amrmToken.getService().toString()));
                ApplicationMasterService.LOG.info("The AMRMToken has been rolled-over. Send new AMRMToken back to application: " + applicationId);
            }
            lock.setAllocateResponse(allocateResponse);
            return allocateResponse;
        }
    }
    
    private PreemptionMessage generatePreemptionMessage(final Allocation allocation) {
        PreemptionMessage pMsg = null;
        if (allocation.getStrictContainerPreemptions() != null) {
            pMsg = this.recordFactory.newRecordInstance(PreemptionMessage.class);
            final StrictPreemptionContract pStrict = this.recordFactory.newRecordInstance(StrictPreemptionContract.class);
            final Set<PreemptionContainer> pCont = new HashSet<PreemptionContainer>();
            for (final ContainerId cId : allocation.getStrictContainerPreemptions()) {
                final PreemptionContainer pc = this.recordFactory.newRecordInstance(PreemptionContainer.class);
                pc.setId(cId);
                pCont.add(pc);
            }
            pStrict.setContainers(pCont);
            pMsg.setStrictContract(pStrict);
        }
        if (allocation.getResourcePreemptions() != null && allocation.getResourcePreemptions().size() > 0 && allocation.getContainerPreemptions() != null && allocation.getContainerPreemptions().size() > 0) {
            if (pMsg == null) {
                pMsg = this.recordFactory.newRecordInstance(PreemptionMessage.class);
            }
            final PreemptionContract contract = this.recordFactory.newRecordInstance(PreemptionContract.class);
            final Set<PreemptionContainer> pCont = new HashSet<PreemptionContainer>();
            for (final ContainerId cId : allocation.getContainerPreemptions()) {
                final PreemptionContainer pc = this.recordFactory.newRecordInstance(PreemptionContainer.class);
                pc.setId(cId);
                pCont.add(pc);
            }
            final List<PreemptionResourceRequest> pRes = new ArrayList<PreemptionResourceRequest>();
            for (final ResourceRequest crr : allocation.getResourcePreemptions()) {
                final PreemptionResourceRequest prr = this.recordFactory.newRecordInstance(PreemptionResourceRequest.class);
                prr.setResourceRequest(crr);
                pRes.add(prr);
            }
            contract.setContainers(pCont);
            contract.setResourceRequest(pRes);
            pMsg.setContract(contract);
        }
        return pMsg;
    }
    
    public void registerAppAttempt(final ApplicationAttemptId attemptId) {
        final AllocateResponse response = this.recordFactory.newRecordInstance(AllocateResponse.class);
        response.setResponseId(-1);
        ApplicationMasterService.LOG.info("Registering app attempt : " + attemptId);
        this.responseMap.put(attemptId, new AllocateResponseLock(response));
        this.rmContext.getNMTokenSecretManager().registerApplicationAttempt(attemptId);
    }
    
    public void unregisterAttempt(final ApplicationAttemptId attemptId) {
        ApplicationMasterService.LOG.info("Unregistering app attempt : " + attemptId);
        this.responseMap.remove(attemptId);
        this.rmContext.getNMTokenSecretManager().unregisterApplicationAttempt(attemptId);
    }
    
    public void refreshServiceAcls(final Configuration configuration, final PolicyProvider policyProvider) {
        this.server.refreshServiceAclWithLoadedConfiguration(configuration, policyProvider);
    }
    
    @Override
    protected void serviceStop() throws Exception {
        if (this.server != null) {
            this.server.stop();
        }
        super.serviceStop();
    }
    
    @VisibleForTesting
    public Server getServer() {
        return this.server;
    }
    
    static {
        LOG = LogFactory.getLog(ApplicationMasterService.class);
    }
    
    public static class AllocateResponseLock
    {
        private AllocateResponse response;
        
        public AllocateResponseLock(final AllocateResponse response) {
            this.response = response;
        }
        
        public synchronized AllocateResponse getAllocateResponse() {
            return this.response;
        }
        
        public synchronized void setAllocateResponse(final AllocateResponse response) {
            this.response = response;
        }
    }
}
