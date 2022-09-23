// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt;

import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptStatusupdateEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptRegistrationEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppFailedAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppFinishedAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Allocation;
import java.util.Collections;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent;
import org.apache.hadoop.yarn.state.MultipleArcTransition;
import java.util.Set;
import java.util.EnumSet;
import org.apache.hadoop.yarn.state.SingleArcTransition;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.server.resourcemanager.amlauncher.AMLauncherEvent;
import org.apache.hadoop.yarn.server.resourcemanager.amlauncher.AMLauncherEventType;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeFinishedContainersPulledByAMEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptUnregistrationEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptLaunchFailedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerAllocatedEvent;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.RMServerUtils;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.state.InvalidStateTransitonException;
import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.yarn.security.client.ClientToAMTokenIdentifier;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.util.StringHelper;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.yarn.server.webproxy.ProxyUriUtils;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.state.StateMachineFactory;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import java.util.List;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.concurrent.ConcurrentMap;
import javax.crypto.SecretKey;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hadoop.yarn.server.resourcemanager.ApplicationMasterService;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.state.StateMachine;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.Recoverable;

public class RMAppAttemptImpl implements RMAppAttempt, Recoverable
{
    private static final Log LOG;
    private static final RecordFactory recordFactory;
    public static final Priority AM_CONTAINER_PRIORITY;
    private final StateMachine<RMAppAttemptState, RMAppAttemptEventType, RMAppAttemptEvent> stateMachine;
    private final RMContext rmContext;
    private final EventHandler eventHandler;
    private final YarnScheduler scheduler;
    private final ApplicationMasterService masterService;
    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;
    private final ApplicationAttemptId applicationAttemptId;
    private final ApplicationSubmissionContext submissionContext;
    private Token<AMRMTokenIdentifier> amrmToken;
    private SecretKey clientTokenMasterKey;
    private ConcurrentMap<NodeId, List<ContainerStatus>> justFinishedContainers;
    private ConcurrentMap<NodeId, List<ContainerStatus>> finishedContainersSentToAM;
    private Container masterContainer;
    private float progress;
    private String host;
    private int rpcPort;
    private String originalTrackingUrl;
    private String proxiedTrackingUrl;
    private long startTime;
    private long finishTime;
    private FinalApplicationStatus finalStatus;
    private final StringBuilder diagnostics;
    private int amContainerExitStatus;
    private Configuration conf;
    private final boolean maybeLastAttempt;
    private static final ExpiredTransition EXPIRED_TRANSITION;
    private RMAppAttemptEvent eventCausingFinalSaving;
    private RMAppAttemptState targetedFinalState;
    private RMAppAttemptState recoveredFinalState;
    private RMAppAttemptState stateBeforeFinalSaving;
    private Object transitionTodo;
    private RMAppAttemptMetrics attemptMetrics;
    private ResourceRequest amReq;
    private static final StateMachineFactory<RMAppAttemptImpl, RMAppAttemptState, RMAppAttemptEventType, RMAppAttemptEvent> stateMachineFactory;
    private static final List<ContainerId> EMPTY_CONTAINER_RELEASE_LIST;
    private static final List<ResourceRequest> EMPTY_CONTAINER_REQUEST_LIST;
    
    public RMAppAttemptImpl(final ApplicationAttemptId appAttemptId, final RMContext rmContext, final YarnScheduler scheduler, final ApplicationMasterService masterService, final ApplicationSubmissionContext submissionContext, final Configuration conf, final boolean maybeLastAttempt, final ResourceRequest amReq) {
        this.amrmToken = null;
        this.clientTokenMasterKey = null;
        this.justFinishedContainers = new ConcurrentHashMap<NodeId, List<ContainerStatus>>();
        this.finishedContainersSentToAM = new ConcurrentHashMap<NodeId, List<ContainerStatus>>();
        this.progress = 0.0f;
        this.host = "N/A";
        this.rpcPort = -1;
        this.originalTrackingUrl = "N/A";
        this.proxiedTrackingUrl = "N/A";
        this.startTime = 0L;
        this.finishTime = 0L;
        this.finalStatus = null;
        this.diagnostics = new StringBuilder();
        this.amContainerExitStatus = -1000;
        this.attemptMetrics = null;
        this.amReq = null;
        this.conf = conf;
        this.applicationAttemptId = appAttemptId;
        this.rmContext = rmContext;
        this.eventHandler = rmContext.getDispatcher().getEventHandler();
        this.submissionContext = submissionContext;
        this.scheduler = scheduler;
        this.masterService = masterService;
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
        this.proxiedTrackingUrl = this.generateProxyUriWithScheme(null);
        this.maybeLastAttempt = maybeLastAttempt;
        this.stateMachine = RMAppAttemptImpl.stateMachineFactory.make(this);
        this.attemptMetrics = new RMAppAttemptMetrics(this.applicationAttemptId, rmContext);
        this.amReq = amReq;
    }
    
    @Override
    public ApplicationAttemptId getAppAttemptId() {
        return this.applicationAttemptId;
    }
    
    @Override
    public ApplicationSubmissionContext getSubmissionContext() {
        return this.submissionContext;
    }
    
    @Override
    public FinalApplicationStatus getFinalApplicationStatus() {
        this.readLock.lock();
        try {
            return this.finalStatus;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public RMAppAttemptState getAppAttemptState() {
        this.readLock.lock();
        try {
            return this.stateMachine.getCurrentState();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String getHost() {
        this.readLock.lock();
        try {
            return this.host;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public int getRpcPort() {
        this.readLock.lock();
        try {
            return this.rpcPort;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String getTrackingUrl() {
        this.readLock.lock();
        try {
            return this.getSubmissionContext().getUnmanagedAM() ? this.originalTrackingUrl : this.proxiedTrackingUrl;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String getOriginalTrackingUrl() {
        this.readLock.lock();
        try {
            return this.originalTrackingUrl;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String getWebProxyBase() {
        this.readLock.lock();
        try {
            return ProxyUriUtils.getPath(this.applicationAttemptId.getApplicationId());
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private String generateProxyUriWithScheme(final String trackingUriWithoutScheme) {
        this.readLock.lock();
        try {
            final String scheme = WebAppUtils.getHttpSchemePrefix(this.conf);
            final URI trackingUri = StringUtils.isEmpty(trackingUriWithoutScheme) ? null : ProxyUriUtils.getUriFromAMUrl(scheme, trackingUriWithoutScheme);
            final String proxy = WebAppUtils.getProxyHostAndPort(this.conf);
            final URI proxyUri = ProxyUriUtils.getUriFromAMUrl(scheme, proxy);
            final URI result = ProxyUriUtils.getProxyUri(trackingUri, proxyUri, this.applicationAttemptId.getApplicationId());
            return result.toASCIIString();
        }
        catch (URISyntaxException e) {
            RMAppAttemptImpl.LOG.warn("Could not proxify " + trackingUriWithoutScheme, e);
            return trackingUriWithoutScheme;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private void setTrackingUrlToRMAppPage() {
        this.originalTrackingUrl = StringHelper.pjoin(WebAppUtils.getResolvedRMWebAppURLWithScheme(this.conf), "cluster", "app", this.getAppAttemptId().getApplicationId());
        this.proxiedTrackingUrl = this.originalTrackingUrl;
    }
    
    private void invalidateAMHostAndPort() {
        this.host = "N/A";
        this.rpcPort = -1;
    }
    
    @Override
    public SecretKey getClientTokenMasterKey() {
        return this.clientTokenMasterKey;
    }
    
    @Override
    public Token<AMRMTokenIdentifier> getAMRMToken() {
        this.readLock.lock();
        try {
            return this.amrmToken;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @InterfaceAudience.Private
    public void setAMRMToken(final Token<AMRMTokenIdentifier> lastToken) {
        this.writeLock.lock();
        try {
            this.amrmToken = lastToken;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public Token<ClientToAMTokenIdentifier> createClientToken(final String client) {
        this.readLock.lock();
        try {
            Token<ClientToAMTokenIdentifier> token = null;
            final ClientToAMTokenSecretManagerInRM secretMgr = this.rmContext.getClientToAMTokenSecretManager();
            if (client != null && secretMgr.getMasterKey(this.applicationAttemptId) != null) {
                token = new Token<ClientToAMTokenIdentifier>(new ClientToAMTokenIdentifier(this.applicationAttemptId, client), secretMgr);
            }
            return token;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String getDiagnostics() {
        this.readLock.lock();
        try {
            return this.diagnostics.toString();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public int getAMContainerExitStatus() {
        this.readLock.lock();
        try {
            return this.amContainerExitStatus;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public float getProgress() {
        this.readLock.lock();
        try {
            return this.progress;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @VisibleForTesting
    @Override
    public List<ContainerStatus> getJustFinishedContainers() {
        this.readLock.lock();
        try {
            final List<ContainerStatus> returnList = new ArrayList<ContainerStatus>();
            for (final Collection<ContainerStatus> containerStatusList : this.justFinishedContainers.values()) {
                returnList.addAll(containerStatusList);
            }
            return returnList;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public ConcurrentMap<NodeId, List<ContainerStatus>> getJustFinishedContainersReference() {
        this.readLock.lock();
        try {
            return this.justFinishedContainers;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public ConcurrentMap<NodeId, List<ContainerStatus>> getFinishedContainersSentToAMReference() {
        this.readLock.lock();
        try {
            return this.finishedContainersSentToAM;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public List<ContainerStatus> pullJustFinishedContainers() {
        this.writeLock.lock();
        try {
            final List<ContainerStatus> returnList = new ArrayList<ContainerStatus>();
            this.sendFinishedContainersToNM();
            final boolean keepContainersAcressAttempts = this.submissionContext.getKeepContainersAcrossApplicationAttempts();
            for (final NodeId nodeId : this.justFinishedContainers.keySet()) {
                final List<ContainerStatus> finishedContainers = this.justFinishedContainers.put(nodeId, new ArrayList<ContainerStatus>());
                if (keepContainersAcressAttempts) {
                    returnList.addAll(finishedContainers);
                }
                else {
                    for (final ContainerStatus containerStatus : finishedContainers) {
                        if (containerStatus.getContainerId().getApplicationAttemptId().equals(this.getAppAttemptId())) {
                            returnList.add(containerStatus);
                        }
                    }
                }
                this.finishedContainersSentToAM.putIfAbsent(nodeId, new ArrayList<ContainerStatus>());
                this.finishedContainersSentToAM.get(nodeId).addAll(finishedContainers);
            }
            return returnList;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public Container getMasterContainer() {
        this.readLock.lock();
        try {
            return this.masterContainer;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public void setMasterContainer(final Container container) {
        this.masterContainer = container;
    }
    
    @Override
    public void handle(final RMAppAttemptEvent event) {
        this.writeLock.lock();
        try {
            final ApplicationAttemptId appAttemptID = event.getApplicationAttemptId();
            RMAppAttemptImpl.LOG.debug("Processing event for " + appAttemptID + " of type " + ((AbstractEvent<Object>)event).getType());
            final RMAppAttemptState oldState = this.getAppAttemptState();
            try {
                this.stateMachine.doTransition(event.getType(), event);
            }
            catch (InvalidStateTransitonException e) {
                RMAppAttemptImpl.LOG.error("Can't handle this event at current state", e);
            }
            if (oldState != this.getAppAttemptState()) {
                RMAppAttemptImpl.LOG.info(appAttemptID + " State change from " + oldState + " to " + this.getAppAttemptState());
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public ApplicationResourceUsageReport getApplicationResourceUsageReport() {
        this.readLock.lock();
        try {
            ApplicationResourceUsageReport report = this.scheduler.getAppResourceUsageReport(this.getAppAttemptId());
            if (report == null) {
                report = RMServerUtils.DUMMY_APPLICATION_RESOURCE_USAGE_REPORT;
            }
            final AggregateAppResourceUsage resUsage = this.attemptMetrics.getAggregateAppResourceUsage();
            report.setMemorySeconds(resUsage.getMemorySeconds());
            report.setVcoreSeconds(resUsage.getVcoreSeconds());
            return report;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public void recover(final RMStateStore.RMState state) {
        final RMStateStore.ApplicationState appState = state.getApplicationState().get(this.getAppAttemptId().getApplicationId());
        final RMStateStore.ApplicationAttemptState attemptState = appState.getAttempt(this.getAppAttemptId());
        assert attemptState != null;
        RMAppAttemptImpl.LOG.info("Recovering attempt: " + this.getAppAttemptId() + " with final state: " + attemptState.getState());
        this.diagnostics.append("Attempt recovered after RM restart");
        this.diagnostics.append(attemptState.getDiagnostics());
        this.amContainerExitStatus = attemptState.getAMContainerExitStatus();
        if (this.amContainerExitStatus == -102) {
            this.attemptMetrics.setIsPreempted();
        }
        this.setMasterContainer(attemptState.getMasterContainer());
        this.recoverAppAttemptCredentials(attemptState.getAppAttemptCredentials(), attemptState.getState());
        this.recoveredFinalState = attemptState.getState();
        this.originalTrackingUrl = attemptState.getFinalTrackingUrl();
        this.proxiedTrackingUrl = this.generateProxyUriWithScheme(this.originalTrackingUrl);
        this.finalStatus = attemptState.getFinalApplicationStatus();
        this.startTime = attemptState.getStartTime();
        this.finishTime = attemptState.getFinishTime();
        this.attemptMetrics.updateAggregateAppResourceUsage(attemptState.getMemorySeconds(), attemptState.getVcoreSeconds());
    }
    
    public void transferStateFromPreviousAttempt(final RMAppAttempt attempt) {
        this.justFinishedContainers = attempt.getJustFinishedContainersReference();
        this.finishedContainersSentToAM = attempt.getFinishedContainersSentToAMReference();
    }
    
    private void recoverAppAttemptCredentials(final Credentials appAttemptTokens, final RMAppAttemptState state) {
        if (appAttemptTokens == null || state == RMAppAttemptState.FAILED || state == RMAppAttemptState.FINISHED || state == RMAppAttemptState.KILLED) {
            return;
        }
        if (UserGroupInformation.isSecurityEnabled()) {
            final byte[] clientTokenMasterKeyBytes = appAttemptTokens.getSecretKey(RMStateStore.AM_CLIENT_TOKEN_MASTER_KEY_NAME);
            if (clientTokenMasterKeyBytes != null) {
                this.clientTokenMasterKey = this.rmContext.getClientToAMTokenSecretManager().registerMasterKey(this.applicationAttemptId, clientTokenMasterKeyBytes);
            }
        }
        this.amrmToken = this.rmContext.getAMRMTokenSecretManager().createAndGetAMRMToken(this.applicationAttemptId);
    }
    
    private void retryFetchingAMContainer(final RMAppAttemptImpl appAttempt) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException e) {
                    RMAppAttemptImpl.LOG.warn("Interrupted while waiting to resend the ContainerAllocated Event.");
                }
                appAttempt.eventHandler.handle(new RMAppAttemptContainerAllocatedEvent(appAttempt.applicationAttemptId));
            }
        }.start();
    }
    
    private void rememberTargetTransitions(final RMAppAttemptEvent event, final Object transitionToDo, final RMAppAttemptState targetFinalState) {
        this.transitionTodo = transitionToDo;
        this.targetedFinalState = targetFinalState;
        this.eventCausingFinalSaving = event;
    }
    
    private void rememberTargetTransitionsAndStoreState(final RMAppAttemptEvent event, final Object transitionToDo, final RMAppAttemptState targetFinalState, final RMAppAttemptState stateToBeStored) {
        this.rememberTargetTransitions(event, transitionToDo, targetFinalState);
        this.stateBeforeFinalSaving = this.getState();
        String diags = null;
        String finalTrackingUrl = null;
        FinalApplicationStatus finalStatus = null;
        int exitStatus = -1000;
        switch (event.getType()) {
            case LAUNCH_FAILED: {
                final RMAppAttemptLaunchFailedEvent launchFaileEvent = (RMAppAttemptLaunchFailedEvent)event;
                diags = launchFaileEvent.getMessage();
                break;
            }
            case REGISTERED: {
                diags = getUnexpectedAMRegisteredDiagnostics();
                break;
            }
            case UNREGISTERED: {
                final RMAppAttemptUnregistrationEvent unregisterEvent = (RMAppAttemptUnregistrationEvent)event;
                diags = unregisterEvent.getDiagnostics();
                finalTrackingUrl = sanitizeTrackingUrl(unregisterEvent.getFinalTrackingUrl());
                finalStatus = unregisterEvent.getFinalApplicationStatus();
                break;
            }
            case CONTAINER_FINISHED: {
                final RMAppAttemptContainerFinishedEvent finishEvent = (RMAppAttemptContainerFinishedEvent)event;
                diags = this.getAMContainerCrashedDiagnostics(finishEvent);
                exitStatus = finishEvent.getContainerStatus().getExitStatus();
            }
            case EXPIRE: {
                diags = getAMExpiredDiagnostics(event);
                break;
            }
        }
        final AggregateAppResourceUsage resUsage = this.attemptMetrics.getAggregateAppResourceUsage();
        final RMStateStore rmStore = this.rmContext.getStateStore();
        this.setFinishTime(System.currentTimeMillis());
        final RMStateStore.ApplicationAttemptState attemptState = new RMStateStore.ApplicationAttemptState(this.applicationAttemptId, this.getMasterContainer(), rmStore.getCredentialsFromAppAttempt(this), this.startTime, stateToBeStored, finalTrackingUrl, diags, finalStatus, exitStatus, this.getFinishTime(), resUsage.getMemorySeconds(), resUsage.getVcoreSeconds());
        RMAppAttemptImpl.LOG.info("Updating application attempt " + this.applicationAttemptId + " with final state: " + this.targetedFinalState + ", and exit status: " + exitStatus);
        rmStore.updateApplicationAttemptState(attemptState);
    }
    
    @Override
    public boolean shouldCountTowardsMaxAttemptRetry() {
        try {
            this.readLock.lock();
            final int exitStatus = this.getAMContainerExitStatus();
            return exitStatus != -102 && exitStatus != -100 && exitStatus != -101 && exitStatus != -106;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private void setAMContainerCrashedDiagnosticsAndExitStatus(final RMAppAttemptContainerFinishedEvent finishEvent) {
        final ContainerStatus status = finishEvent.getContainerStatus();
        final String diagnostics = this.getAMContainerCrashedDiagnostics(finishEvent);
        this.diagnostics.append(diagnostics);
        this.amContainerExitStatus = status.getExitStatus();
    }
    
    private String getAMContainerCrashedDiagnostics(final RMAppAttemptContainerFinishedEvent finishEvent) {
        final ContainerStatus status = finishEvent.getContainerStatus();
        final StringBuilder diagnosticsBuilder = new StringBuilder();
        diagnosticsBuilder.append("AM Container for ").append(finishEvent.getApplicationAttemptId()).append(" exited with ").append(" exitCode: ").append(status.getExitStatus()).append("\n");
        if (this.getTrackingUrl() != null) {
            diagnosticsBuilder.append("For more detailed output,").append(" check application tracking page:").append(this.getTrackingUrl()).append("Then, click on links to logs of each attempt.\n");
        }
        diagnosticsBuilder.append("Diagnostics: ").append(status.getDiagnostics()).append("Failing this attempt");
        return diagnosticsBuilder.toString();
    }
    
    private static String getAMExpiredDiagnostics(final RMAppAttemptEvent event) {
        final String diag = "ApplicationMaster for attempt " + event.getApplicationAttemptId() + " timed out";
        return diag;
    }
    
    private static String getUnexpectedAMRegisteredDiagnostics() {
        return "Unmanaged AM must register after AM attempt reaches LAUNCHED state.";
    }
    
    private void updateInfoOnAMUnregister(final RMAppAttemptEvent event) {
        this.progress = 1.0f;
        final RMAppAttemptUnregistrationEvent unregisterEvent = (RMAppAttemptUnregistrationEvent)event;
        this.diagnostics.append(unregisterEvent.getDiagnostics());
        this.originalTrackingUrl = sanitizeTrackingUrl(unregisterEvent.getFinalTrackingUrl());
        this.proxiedTrackingUrl = this.generateProxyUriWithScheme(this.originalTrackingUrl);
        this.finalStatus = unregisterEvent.getFinalApplicationStatus();
    }
    
    private void sendFinishedContainersToNM() {
        for (final NodeId nodeId : this.finishedContainersSentToAM.keySet()) {
            final List<ContainerStatus> currentSentContainers = this.finishedContainersSentToAM.put(nodeId, new ArrayList<ContainerStatus>());
            final List<ContainerId> containerIdList = new ArrayList<ContainerId>(currentSentContainers.size());
            for (final ContainerStatus containerStatus : currentSentContainers) {
                containerIdList.add(containerStatus.getContainerId());
            }
            this.eventHandler.handle(new RMNodeFinishedContainersPulledByAMEvent(nodeId, containerIdList));
        }
    }
    
    private void sendAMContainerToNM(final RMAppAttemptImpl appAttempt, final RMAppAttemptContainerFinishedEvent containerFinishedEvent) {
        final NodeId nodeId = containerFinishedEvent.getNodeId();
        this.finishedContainersSentToAM.putIfAbsent(nodeId, new ArrayList<ContainerStatus>());
        appAttempt.finishedContainersSentToAM.get(nodeId).add(containerFinishedEvent.getContainerStatus());
        if (!appAttempt.getSubmissionContext().getKeepContainersAcrossApplicationAttempts()) {
            appAttempt.sendFinishedContainersToNM();
        }
    }
    
    private static void addJustFinishedContainer(final RMAppAttemptImpl appAttempt, final RMAppAttemptContainerFinishedEvent containerFinishedEvent) {
        appAttempt.justFinishedContainers.putIfAbsent(containerFinishedEvent.getNodeId(), new ArrayList<ContainerStatus>());
        appAttempt.justFinishedContainers.get(containerFinishedEvent.getNodeId()).add(containerFinishedEvent.getContainerStatus());
    }
    
    @Override
    public long getStartTime() {
        this.readLock.lock();
        try {
            return this.startTime;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public RMAppAttemptState getState() {
        this.readLock.lock();
        try {
            return this.stateMachine.getCurrentState();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public YarnApplicationAttemptState createApplicationAttemptState() {
        RMAppAttemptState state = this.getState();
        if (state.equals(RMAppAttemptState.FINAL_SAVING)) {
            state = this.stateBeforeFinalSaving;
        }
        return RMServerUtils.createApplicationAttemptState(state);
    }
    
    private void launchAttempt() {
        this.eventHandler.handle(new AMLauncherEvent(AMLauncherEventType.LAUNCH, this));
    }
    
    private void attemptLaunched() {
        this.rmContext.getAMLivelinessMonitor().register(this.getAppAttemptId());
    }
    
    private void storeAttempt() {
        RMAppAttemptImpl.LOG.info("Storing attempt: AppId: " + this.getAppAttemptId().getApplicationId() + " AttemptId: " + this.getAppAttemptId() + " MasterContainer: " + this.masterContainer);
        this.rmContext.getStateStore().storeNewApplicationAttempt(this);
    }
    
    private void removeCredentials(final RMAppAttemptImpl appAttempt) {
        if (UserGroupInformation.isSecurityEnabled()) {
            appAttempt.rmContext.getClientToAMTokenSecretManager().unRegisterApplication(appAttempt.getAppAttemptId());
        }
        appAttempt.rmContext.getAMRMTokenSecretManager().applicationMasterFinished(appAttempt.getAppAttemptId());
    }
    
    private static String sanitizeTrackingUrl(final String url) {
        return (url == null || url.trim().isEmpty()) ? "N/A" : url;
    }
    
    @Override
    public ApplicationAttemptReport createApplicationAttemptReport() {
        this.readLock.lock();
        ApplicationAttemptReport attemptReport = null;
        try {
            final ContainerId amId = (this.masterContainer == null) ? null : this.masterContainer.getId();
            attemptReport = ApplicationAttemptReport.newInstance(this.getAppAttemptId(), this.getHost(), this.getRpcPort(), this.getTrackingUrl(), this.getOriginalTrackingUrl(), this.getDiagnostics(), YarnApplicationAttemptState.valueOf(this.getState().toString()), amId);
        }
        finally {
            this.readLock.unlock();
        }
        return attemptReport;
    }
    
    public boolean mayBeLastAttempt() {
        return this.maybeLastAttempt;
    }
    
    @Override
    public RMAppAttemptMetrics getRMAppAttemptMetrics() {
        return this.attemptMetrics;
    }
    
    @Override
    public long getFinishTime() {
        try {
            this.readLock.lock();
            return this.finishTime;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private void setFinishTime(final long finishTime) {
        try {
            this.writeLock.lock();
            this.finishTime = finishTime;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    static {
        LOG = LogFactory.getLog(RMAppAttemptImpl.class);
        recordFactory = RecordFactoryProvider.getRecordFactory(null);
        (AM_CONTAINER_PRIORITY = RMAppAttemptImpl.recordFactory.newRecordInstance(Priority.class)).setPriority(0);
        EXPIRED_TRANSITION = new ExpiredTransition();
        stateMachineFactory = new StateMachineFactory<RMAppAttemptImpl, RMAppAttemptState, RMAppAttemptEventType, RMAppAttemptEvent>(RMAppAttemptState.NEW).addTransition(RMAppAttemptState.NEW, RMAppAttemptState.SUBMITTED, RMAppAttemptEventType.START, new AttemptStartedTransition()).addTransition(RMAppAttemptState.NEW, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.KILL, new FinalSavingTransition(new BaseFinalTransition(RMAppAttemptState.KILLED), RMAppAttemptState.KILLED)).addTransition(RMAppAttemptState.NEW, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.REGISTERED, new FinalSavingTransition(new UnexpectedAMRegisteredTransition(), RMAppAttemptState.FAILED)).addTransition(RMAppAttemptState.NEW, EnumSet.of(RMAppAttemptState.FINISHED, RMAppAttemptState.KILLED, RMAppAttemptState.FAILED, RMAppAttemptState.LAUNCHED), RMAppAttemptEventType.RECOVER, new AttemptRecoveredTransition()).addTransition(RMAppAttemptState.SUBMITTED, EnumSet.of(RMAppAttemptState.LAUNCHED_UNMANAGED_SAVING, RMAppAttemptState.SCHEDULED), RMAppAttemptEventType.ATTEMPT_ADDED, new ScheduleTransition()).addTransition(RMAppAttemptState.SUBMITTED, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.KILL, new FinalSavingTransition(new BaseFinalTransition(RMAppAttemptState.KILLED), RMAppAttemptState.KILLED)).addTransition(RMAppAttemptState.SUBMITTED, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.REGISTERED, new FinalSavingTransition(new UnexpectedAMRegisteredTransition(), RMAppAttemptState.FAILED)).addTransition(RMAppAttemptState.SCHEDULED, EnumSet.of(RMAppAttemptState.ALLOCATED_SAVING, RMAppAttemptState.SCHEDULED), RMAppAttemptEventType.CONTAINER_ALLOCATED, new AMContainerAllocatedTransition()).addTransition(RMAppAttemptState.SCHEDULED, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.KILL, new FinalSavingTransition(new BaseFinalTransition(RMAppAttemptState.KILLED), RMAppAttemptState.KILLED)).addTransition(RMAppAttemptState.SCHEDULED, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.CONTAINER_FINISHED, new FinalSavingTransition(new AMContainerCrashedBeforeRunningTransition(), RMAppAttemptState.FAILED)).addTransition(RMAppAttemptState.ALLOCATED_SAVING, RMAppAttemptState.ALLOCATED, RMAppAttemptEventType.ATTEMPT_NEW_SAVED, new AttemptStoredTransition()).addTransition(RMAppAttemptState.ALLOCATED_SAVING, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.KILL, new FinalSavingTransition(new BaseFinalTransition(RMAppAttemptState.KILLED), RMAppAttemptState.KILLED)).addTransition(RMAppAttemptState.ALLOCATED_SAVING, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.CONTAINER_FINISHED, new FinalSavingTransition(new AMContainerCrashedBeforeRunningTransition(), RMAppAttemptState.FAILED)).addTransition(RMAppAttemptState.LAUNCHED_UNMANAGED_SAVING, RMAppAttemptState.LAUNCHED, RMAppAttemptEventType.ATTEMPT_NEW_SAVED, new UnmanagedAMAttemptSavedTransition()).addTransition(RMAppAttemptState.LAUNCHED_UNMANAGED_SAVING, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.REGISTERED, new FinalSavingTransition(new UnexpectedAMRegisteredTransition(), RMAppAttemptState.FAILED)).addTransition(RMAppAttemptState.LAUNCHED_UNMANAGED_SAVING, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.KILL, new FinalSavingTransition(new BaseFinalTransition(RMAppAttemptState.KILLED), RMAppAttemptState.KILLED)).addTransition(RMAppAttemptState.ALLOCATED, RMAppAttemptState.LAUNCHED, RMAppAttemptEventType.LAUNCHED, new AMLaunchedTransition()).addTransition(RMAppAttemptState.ALLOCATED, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.LAUNCH_FAILED, new FinalSavingTransition(new LaunchFailedTransition(), RMAppAttemptState.FAILED)).addTransition(RMAppAttemptState.ALLOCATED, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.KILL, new FinalSavingTransition(new KillAllocatedAMTransition(), RMAppAttemptState.KILLED)).addTransition(RMAppAttemptState.ALLOCATED, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.CONTAINER_FINISHED, new FinalSavingTransition(new AMContainerCrashedBeforeRunningTransition(), RMAppAttemptState.FAILED)).addTransition(RMAppAttemptState.LAUNCHED, RMAppAttemptState.RUNNING, RMAppAttemptEventType.REGISTERED, new AMRegisteredTransition()).addTransition(RMAppAttemptState.LAUNCHED, EnumSet.of(RMAppAttemptState.LAUNCHED, RMAppAttemptState.FINAL_SAVING), RMAppAttemptEventType.CONTAINER_FINISHED, new ContainerFinishedTransition(new AMContainerCrashedBeforeRunningTransition(), RMAppAttemptState.LAUNCHED)).addTransition(RMAppAttemptState.LAUNCHED, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.EXPIRE, new FinalSavingTransition(RMAppAttemptImpl.EXPIRED_TRANSITION, RMAppAttemptState.FAILED)).addTransition(RMAppAttemptState.LAUNCHED, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.KILL, new FinalSavingTransition(new FinalTransition(RMAppAttemptState.KILLED), RMAppAttemptState.KILLED)).addTransition(RMAppAttemptState.RUNNING, EnumSet.of(RMAppAttemptState.FINAL_SAVING, RMAppAttemptState.FINISHED), RMAppAttemptEventType.UNREGISTERED, new AMUnregisteredTransition()).addTransition(RMAppAttemptState.RUNNING, RMAppAttemptState.RUNNING, RMAppAttemptEventType.STATUS_UPDATE, new StatusUpdateTransition()).addTransition(RMAppAttemptState.RUNNING, RMAppAttemptState.RUNNING, RMAppAttemptEventType.CONTAINER_ALLOCATED).addTransition(RMAppAttemptState.RUNNING, EnumSet.of(RMAppAttemptState.RUNNING, RMAppAttemptState.FINAL_SAVING), RMAppAttemptEventType.CONTAINER_FINISHED, new ContainerFinishedTransition(new AMContainerCrashedAtRunningTransition(), RMAppAttemptState.RUNNING)).addTransition(RMAppAttemptState.RUNNING, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.EXPIRE, new FinalSavingTransition(RMAppAttemptImpl.EXPIRED_TRANSITION, RMAppAttemptState.FAILED)).addTransition(RMAppAttemptState.RUNNING, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.KILL, new FinalSavingTransition(new FinalTransition(RMAppAttemptState.KILLED), RMAppAttemptState.KILLED)).addTransition(RMAppAttemptState.FINAL_SAVING, EnumSet.of(RMAppAttemptState.FINISHING, RMAppAttemptState.FAILED, RMAppAttemptState.KILLED, RMAppAttemptState.FINISHED), RMAppAttemptEventType.ATTEMPT_UPDATE_SAVED, new FinalStateSavedTransition()).addTransition(RMAppAttemptState.FINAL_SAVING, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.CONTAINER_FINISHED, new ContainerFinishedAtFinalSavingTransition()).addTransition(RMAppAttemptState.FINAL_SAVING, RMAppAttemptState.FINAL_SAVING, RMAppAttemptEventType.EXPIRE, new AMExpiredAtFinalSavingTransition()).addTransition(RMAppAttemptState.FINAL_SAVING, RMAppAttemptState.FINAL_SAVING, EnumSet.of(RMAppAttemptEventType.UNREGISTERED, RMAppAttemptEventType.STATUS_UPDATE, RMAppAttemptEventType.CONTAINER_ALLOCATED, RMAppAttemptEventType.ATTEMPT_NEW_SAVED, RMAppAttemptEventType.KILL)).addTransition(RMAppAttemptState.FAILED, RMAppAttemptState.FAILED, RMAppAttemptEventType.CONTAINER_FINISHED, new ContainerFinishedAtFinalStateTransition()).addTransition(RMAppAttemptState.FAILED, RMAppAttemptState.FAILED, EnumSet.of(RMAppAttemptEventType.EXPIRE, RMAppAttemptEventType.KILL, RMAppAttemptEventType.UNREGISTERED, RMAppAttemptEventType.STATUS_UPDATE, RMAppAttemptEventType.CONTAINER_ALLOCATED)).addTransition(RMAppAttemptState.FINISHING, EnumSet.of(RMAppAttemptState.FINISHING, RMAppAttemptState.FINISHED), RMAppAttemptEventType.CONTAINER_FINISHED, new AMFinishingContainerFinishedTransition()).addTransition(RMAppAttemptState.FINISHING, RMAppAttemptState.FINISHED, RMAppAttemptEventType.EXPIRE, new FinalTransition(RMAppAttemptState.FINISHED)).addTransition(RMAppAttemptState.FINISHING, RMAppAttemptState.FINISHING, EnumSet.of(RMAppAttemptEventType.UNREGISTERED, RMAppAttemptEventType.STATUS_UPDATE, RMAppAttemptEventType.CONTAINER_ALLOCATED, RMAppAttemptEventType.KILL)).addTransition(RMAppAttemptState.FINISHED, RMAppAttemptState.FINISHED, EnumSet.of(RMAppAttemptEventType.EXPIRE, RMAppAttemptEventType.UNREGISTERED, RMAppAttemptEventType.CONTAINER_ALLOCATED, RMAppAttemptEventType.KILL)).addTransition(RMAppAttemptState.FINISHED, RMAppAttemptState.FINISHED, RMAppAttemptEventType.CONTAINER_FINISHED, new ContainerFinishedAtFinalStateTransition()).addTransition(RMAppAttemptState.KILLED, RMAppAttemptState.KILLED, EnumSet.of(RMAppAttemptEventType.ATTEMPT_ADDED, new RMAppAttemptEventType[] { RMAppAttemptEventType.LAUNCHED, RMAppAttemptEventType.LAUNCH_FAILED, RMAppAttemptEventType.EXPIRE, RMAppAttemptEventType.REGISTERED, RMAppAttemptEventType.CONTAINER_ALLOCATED, RMAppAttemptEventType.UNREGISTERED, RMAppAttemptEventType.KILL, RMAppAttemptEventType.STATUS_UPDATE })).addTransition(RMAppAttemptState.KILLED, RMAppAttemptState.KILLED, RMAppAttemptEventType.CONTAINER_FINISHED, new ContainerFinishedAtFinalStateTransition()).installTopology();
        EMPTY_CONTAINER_RELEASE_LIST = new ArrayList<ContainerId>();
        EMPTY_CONTAINER_REQUEST_LIST = new ArrayList<ResourceRequest>();
    }
    
    private static class BaseTransition implements SingleArcTransition<RMAppAttemptImpl, RMAppAttemptEvent>
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
        }
    }
    
    private static final class AttemptStartedTransition extends BaseTransition
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            boolean transferStateFromPreviousAttempt = false;
            if (event instanceof RMAppStartAttemptEvent) {
                transferStateFromPreviousAttempt = ((RMAppStartAttemptEvent)event).getTransferStateFromPreviousAttempt();
            }
            appAttempt.startTime = System.currentTimeMillis();
            appAttempt.masterService.registerAppAttempt(appAttempt.applicationAttemptId);
            if (UserGroupInformation.isSecurityEnabled()) {
                appAttempt.clientTokenMasterKey = appAttempt.rmContext.getClientToAMTokenSecretManager().createMasterKey(appAttempt.applicationAttemptId);
            }
            appAttempt.eventHandler.handle(new AppAttemptAddedSchedulerEvent(appAttempt.applicationAttemptId, transferStateFromPreviousAttempt));
        }
    }
    
    @VisibleForTesting
    public static final class ScheduleTransition implements MultipleArcTransition<RMAppAttemptImpl, RMAppAttemptEvent, RMAppAttemptState>
    {
        static final /* synthetic */ boolean $assertionsDisabled;
        
        @Override
        public RMAppAttemptState transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final ApplicationSubmissionContext subCtx = appAttempt.submissionContext;
            if (subCtx.getUnmanagedAM()) {
                appAttempt.storeAttempt();
                return RMAppAttemptState.LAUNCHED_UNMANAGED_SAVING;
            }
            appAttempt.amReq.setNumContainers(1);
            appAttempt.amReq.setPriority(RMAppAttemptImpl.AM_CONTAINER_PRIORITY);
            appAttempt.amReq.setResourceName("*");
            appAttempt.amReq.setRelaxLocality(true);
            final Allocation amContainerAllocation = appAttempt.scheduler.allocate(appAttempt.applicationAttemptId, Collections.singletonList(appAttempt.amReq), RMAppAttemptImpl.EMPTY_CONTAINER_RELEASE_LIST, null, null);
            if (amContainerAllocation != null && amContainerAllocation.getContainers() != null && !ScheduleTransition.$assertionsDisabled && amContainerAllocation.getContainers().size() != 0) {
                throw new AssertionError();
            }
            return RMAppAttemptState.SCHEDULED;
        }
    }
    
    private static final class AMContainerAllocatedTransition implements MultipleArcTransition<RMAppAttemptImpl, RMAppAttemptEvent, RMAppAttemptState>
    {
        @Override
        public RMAppAttemptState transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final Allocation amContainerAllocation = appAttempt.scheduler.allocate(appAttempt.applicationAttemptId, RMAppAttemptImpl.EMPTY_CONTAINER_REQUEST_LIST, RMAppAttemptImpl.EMPTY_CONTAINER_RELEASE_LIST, null, null);
            if (amContainerAllocation.getContainers().size() == 0) {
                appAttempt.retryFetchingAMContainer(appAttempt);
                return RMAppAttemptState.SCHEDULED;
            }
            appAttempt.setMasterContainer(amContainerAllocation.getContainers().get(0));
            final RMContainerImpl rmMasterContainer = (RMContainerImpl)appAttempt.scheduler.getRMContainer(appAttempt.getMasterContainer().getId());
            rmMasterContainer.setAMContainer(true);
            appAttempt.rmContext.getNMTokenSecretManager().clearNodeSetForAttempt(appAttempt.applicationAttemptId);
            appAttempt.getSubmissionContext().setResource(appAttempt.getMasterContainer().getResource());
            appAttempt.storeAttempt();
            return RMAppAttemptState.ALLOCATED_SAVING;
        }
    }
    
    private static final class AttemptStoredTransition extends BaseTransition
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            appAttempt.launchAttempt();
        }
    }
    
    private static class AttemptRecoveredTransition implements MultipleArcTransition<RMAppAttemptImpl, RMAppAttemptEvent, RMAppAttemptState>
    {
        @Override
        public RMAppAttemptState transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            if (appAttempt.recoveredFinalState != null) {
                appAttempt.progress = 1.0f;
                final RMApp rmApp = appAttempt.rmContext.getRMApps().get(appAttempt.getAppAttemptId().getApplicationId());
                if (rmApp.getCurrentAppAttempt() == appAttempt && !RMAppImpl.isAppInFinalState(rmApp)) {
                    ((EventHandler<AppAttemptAddedSchedulerEvent>)appAttempt.scheduler).handle(new AppAttemptAddedSchedulerEvent(appAttempt.getAppAttemptId(), false, true));
                    new BaseFinalTransition(appAttempt.recoveredFinalState).transition(appAttempt, event);
                }
                return appAttempt.recoveredFinalState;
            }
            if (appAttempt.rmContext.isWorkPreservingRecoveryEnabled()) {
                appAttempt.masterService.registerAppAttempt(appAttempt.applicationAttemptId);
                ((EventHandler<AppAttemptAddedSchedulerEvent>)appAttempt.scheduler).handle(new AppAttemptAddedSchedulerEvent(appAttempt.getAppAttemptId(), false, true));
            }
            new AMLaunchedTransition().transition(appAttempt, event);
            return RMAppAttemptState.LAUNCHED;
        }
    }
    
    private static class FinalSavingTransition extends BaseTransition
    {
        Object transitionToDo;
        RMAppAttemptState targetedFinalState;
        
        public FinalSavingTransition(final Object transitionToDo, final RMAppAttemptState targetedFinalState) {
            this.transitionToDo = transitionToDo;
            this.targetedFinalState = targetedFinalState;
        }
        
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            appAttempt.rememberTargetTransitionsAndStoreState(event, this.transitionToDo, this.targetedFinalState, this.targetedFinalState);
        }
    }
    
    private static class FinalStateSavedTransition implements MultipleArcTransition<RMAppAttemptImpl, RMAppAttemptEvent, RMAppAttemptState>
    {
        @Override
        public RMAppAttemptState transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final RMAppAttemptEvent causeEvent = appAttempt.eventCausingFinalSaving;
            if (appAttempt.transitionTodo instanceof SingleArcTransition) {
                ((SingleArcTransition)appAttempt.transitionTodo).transition(appAttempt, causeEvent);
            }
            else if (appAttempt.transitionTodo instanceof MultipleArcTransition) {
                ((MultipleArcTransition)appAttempt.transitionTodo).transition(appAttempt, causeEvent);
            }
            return appAttempt.targetedFinalState;
        }
    }
    
    private static class BaseFinalTransition extends BaseTransition
    {
        private final RMAppAttemptState finalAttemptState;
        
        public BaseFinalTransition(final RMAppAttemptState finalAttemptState) {
            this.finalAttemptState = finalAttemptState;
        }
        
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final ApplicationAttemptId appAttemptId = appAttempt.getAppAttemptId();
            appAttempt.masterService.unregisterAttempt(appAttemptId);
            final ApplicationId applicationId = appAttemptId.getApplicationId();
            RMAppEvent appEvent = null;
            boolean keepContainersAcrossAppAttempts = false;
            switch (this.finalAttemptState) {
                case FINISHED: {
                    appEvent = new RMAppFinishedAttemptEvent(applicationId, appAttempt.getDiagnostics());
                    break;
                }
                case KILLED: {
                    appAttempt.setTrackingUrlToRMAppPage();
                    appAttempt.invalidateAMHostAndPort();
                    appEvent = new RMAppFailedAttemptEvent(applicationId, RMAppEventType.ATTEMPT_KILLED, "Application killed by user.", false);
                    break;
                }
                case FAILED: {
                    appAttempt.setTrackingUrlToRMAppPage();
                    appAttempt.invalidateAMHostAndPort();
                    if (appAttempt.submissionContext.getKeepContainersAcrossApplicationAttempts() && !appAttempt.submissionContext.getUnmanagedAM()) {
                        if (!appAttempt.shouldCountTowardsMaxAttemptRetry()) {
                            keepContainersAcrossAppAttempts = true;
                        }
                        else if (!appAttempt.maybeLastAttempt) {
                            keepContainersAcrossAppAttempts = true;
                        }
                    }
                    appEvent = new RMAppFailedAttemptEvent(applicationId, RMAppEventType.ATTEMPT_FAILED, appAttempt.getDiagnostics(), keepContainersAcrossAppAttempts);
                    break;
                }
                default: {
                    RMAppAttemptImpl.LOG.error("Cannot get this state!! Error!!");
                    break;
                }
            }
            appAttempt.eventHandler.handle(appEvent);
            appAttempt.eventHandler.handle(new AppAttemptRemovedSchedulerEvent(appAttemptId, this.finalAttemptState, keepContainersAcrossAppAttempts));
            appAttempt.removeCredentials(appAttempt);
            appAttempt.rmContext.getRMApplicationHistoryWriter().applicationAttemptFinished(appAttempt, this.finalAttemptState);
            appAttempt.rmContext.getSystemMetricsPublisher().appAttemptFinished(appAttempt, this.finalAttemptState, appAttempt.rmContext.getRMApps().get(appAttempt.applicationAttemptId.getApplicationId()), System.currentTimeMillis());
        }
    }
    
    private static class AMLaunchedTransition extends BaseTransition
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            appAttempt.attemptLaunched();
            appAttempt.rmContext.getClientToAMTokenSecretManager().registerApplication(appAttempt.getAppAttemptId(), appAttempt.getClientTokenMasterKey());
        }
    }
    
    private static final class UnmanagedAMAttemptSavedTransition extends AMLaunchedTransition
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            appAttempt.amrmToken = appAttempt.rmContext.getAMRMTokenSecretManager().createAndGetAMRMToken(appAttempt.applicationAttemptId);
            super.transition(appAttempt, event);
        }
    }
    
    private static final class LaunchFailedTransition extends BaseFinalTransition
    {
        public LaunchFailedTransition() {
            super(RMAppAttemptState.FAILED);
        }
        
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final RMAppAttemptLaunchFailedEvent launchFaileEvent = (RMAppAttemptLaunchFailedEvent)event;
            appAttempt.diagnostics.append(launchFaileEvent.getMessage());
            super.transition(appAttempt, event);
        }
    }
    
    private static final class KillAllocatedAMTransition extends BaseFinalTransition
    {
        public KillAllocatedAMTransition() {
            super(RMAppAttemptState.KILLED);
        }
        
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            super.transition(appAttempt, event);
            appAttempt.eventHandler.handle(new AMLauncherEvent(AMLauncherEventType.CLEANUP, appAttempt));
        }
    }
    
    private static final class AMRegisteredTransition extends BaseTransition
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final RMAppAttemptRegistrationEvent registrationEvent = (RMAppAttemptRegistrationEvent)event;
            appAttempt.host = registrationEvent.getHost();
            appAttempt.rpcPort = registrationEvent.getRpcport();
            appAttempt.originalTrackingUrl = sanitizeTrackingUrl(registrationEvent.getTrackingurl());
            appAttempt.proxiedTrackingUrl = appAttempt.generateProxyUriWithScheme(appAttempt.originalTrackingUrl);
            appAttempt.eventHandler.handle(new RMAppEvent(appAttempt.getAppAttemptId().getApplicationId(), RMAppEventType.ATTEMPT_REGISTERED));
            appAttempt.rmContext.getRMApplicationHistoryWriter().applicationAttemptStarted(appAttempt);
            appAttempt.rmContext.getSystemMetricsPublisher().appAttemptRegistered(appAttempt, System.currentTimeMillis());
        }
    }
    
    private static final class AMContainerCrashedBeforeRunningTransition extends BaseFinalTransition
    {
        public AMContainerCrashedBeforeRunningTransition() {
            super(RMAppAttemptState.FAILED);
        }
        
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final RMAppAttemptContainerFinishedEvent finishEvent = (RMAppAttemptContainerFinishedEvent)event;
            appAttempt.rmContext.getAMLivelinessMonitor().unregister(appAttempt.getAppAttemptId());
            appAttempt.setAMContainerCrashedDiagnosticsAndExitStatus(finishEvent);
            super.transition(appAttempt, finishEvent);
        }
    }
    
    private static class FinalTransition extends BaseFinalTransition
    {
        public FinalTransition(final RMAppAttemptState finalAttemptState) {
            super(finalAttemptState);
        }
        
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            appAttempt.progress = 1.0f;
            super.transition(appAttempt, event);
            appAttempt.rmContext.getAMLivelinessMonitor().unregister(appAttempt.getAppAttemptId());
            appAttempt.rmContext.getAMFinishingMonitor().unregister(appAttempt.getAppAttemptId());
            if (!appAttempt.submissionContext.getUnmanagedAM()) {
                appAttempt.eventHandler.handle(new AMLauncherEvent(AMLauncherEventType.CLEANUP, appAttempt));
            }
        }
    }
    
    private static class ExpiredTransition extends FinalTransition
    {
        public ExpiredTransition() {
            super(RMAppAttemptState.FAILED);
        }
        
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            appAttempt.diagnostics.append(getAMExpiredDiagnostics(event));
            super.transition(appAttempt, event);
        }
    }
    
    private static class UnexpectedAMRegisteredTransition extends BaseFinalTransition
    {
        public UnexpectedAMRegisteredTransition() {
            super(RMAppAttemptState.FAILED);
        }
        
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            assert appAttempt.submissionContext.getUnmanagedAM();
            appAttempt.diagnostics.append(getUnexpectedAMRegisteredDiagnostics());
            super.transition(appAttempt, event);
        }
    }
    
    private static final class StatusUpdateTransition extends BaseTransition
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final RMAppAttemptStatusupdateEvent statusUpdateEvent = (RMAppAttemptStatusupdateEvent)event;
            appAttempt.progress = statusUpdateEvent.getProgress();
            appAttempt.rmContext.getAMLivelinessMonitor().receivedPing(statusUpdateEvent.getApplicationAttemptId());
        }
    }
    
    private static final class AMUnregisteredTransition implements MultipleArcTransition<RMAppAttemptImpl, RMAppAttemptEvent, RMAppAttemptState>
    {
        @Override
        public RMAppAttemptState transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            if (appAttempt.getSubmissionContext().getUnmanagedAM()) {
                appAttempt.updateInfoOnAMUnregister(event);
                new FinalTransition(RMAppAttemptState.FINISHED).transition(appAttempt, event);
                return RMAppAttemptState.FINISHED;
            }
            appAttempt.rememberTargetTransitionsAndStoreState(event, new FinalStateSavedAfterAMUnregisterTransition(), RMAppAttemptState.FINISHING, RMAppAttemptState.FINISHED);
            final ApplicationId applicationId = appAttempt.getAppAttemptId().getApplicationId();
            appAttempt.eventHandler.handle(new RMAppEvent(applicationId, RMAppEventType.ATTEMPT_UNREGISTERED));
            return RMAppAttemptState.FINAL_SAVING;
        }
    }
    
    private static class FinalStateSavedAfterAMUnregisterTransition extends BaseTransition
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            appAttempt.rmContext.getAMLivelinessMonitor().unregister(appAttempt.applicationAttemptId);
            appAttempt.rmContext.getAMFinishingMonitor().register(appAttempt.applicationAttemptId);
            appAttempt.updateInfoOnAMUnregister(event);
        }
    }
    
    private static final class ContainerFinishedTransition implements MultipleArcTransition<RMAppAttemptImpl, RMAppAttemptEvent, RMAppAttemptState>
    {
        private BaseTransition transitionToDo;
        private RMAppAttemptState currentState;
        
        public ContainerFinishedTransition(final BaseTransition transitionToDo, final RMAppAttemptState currentState) {
            this.transitionToDo = transitionToDo;
            this.currentState = currentState;
        }
        
        @Override
        public RMAppAttemptState transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final RMAppAttemptContainerFinishedEvent containerFinishedEvent = (RMAppAttemptContainerFinishedEvent)event;
            final ContainerStatus containerStatus = containerFinishedEvent.getContainerStatus();
            if (appAttempt.masterContainer != null && appAttempt.masterContainer.getId().equals(containerStatus.getContainerId())) {
                appAttempt.sendAMContainerToNM(appAttempt, containerFinishedEvent);
                appAttempt.rememberTargetTransitionsAndStoreState(event, this.transitionToDo, RMAppAttemptState.FAILED, RMAppAttemptState.FAILED);
                return RMAppAttemptState.FINAL_SAVING;
            }
            addJustFinishedContainer(appAttempt, containerFinishedEvent);
            return this.currentState;
        }
    }
    
    private static final class ContainerFinishedAtFinalStateTransition extends BaseTransition
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final RMAppAttemptContainerFinishedEvent containerFinishedEvent = (RMAppAttemptContainerFinishedEvent)event;
            addJustFinishedContainer(appAttempt, containerFinishedEvent);
        }
    }
    
    private static class AMContainerCrashedAtRunningTransition extends BaseTransition
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final RMAppAttemptContainerFinishedEvent finishEvent = (RMAppAttemptContainerFinishedEvent)event;
            assert !appAttempt.submissionContext.getUnmanagedAM();
            appAttempt.setAMContainerCrashedDiagnosticsAndExitStatus(finishEvent);
            new FinalTransition(RMAppAttemptState.FAILED).transition(appAttempt, event);
        }
    }
    
    private static final class AMFinishingContainerFinishedTransition implements MultipleArcTransition<RMAppAttemptImpl, RMAppAttemptEvent, RMAppAttemptState>
    {
        @Override
        public RMAppAttemptState transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final RMAppAttemptContainerFinishedEvent containerFinishedEvent = (RMAppAttemptContainerFinishedEvent)event;
            final ContainerStatus containerStatus = containerFinishedEvent.getContainerStatus();
            if (appAttempt.masterContainer.getId().equals(containerStatus.getContainerId())) {
                new FinalTransition(RMAppAttemptState.FINISHED).transition(appAttempt, containerFinishedEvent);
                appAttempt.sendAMContainerToNM(appAttempt, containerFinishedEvent);
                return RMAppAttemptState.FINISHED;
            }
            addJustFinishedContainer(appAttempt, containerFinishedEvent);
            return RMAppAttemptState.FINISHING;
        }
    }
    
    private static class ContainerFinishedAtFinalSavingTransition extends BaseTransition
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            final RMAppAttemptContainerFinishedEvent containerFinishedEvent = (RMAppAttemptContainerFinishedEvent)event;
            final ContainerStatus containerStatus = containerFinishedEvent.getContainerStatus();
            if (!appAttempt.masterContainer.getId().equals(containerStatus.getContainerId())) {
                addJustFinishedContainer(appAttempt, containerFinishedEvent);
                return;
            }
            appAttempt.sendAMContainerToNM(appAttempt, containerFinishedEvent);
            if (appAttempt.targetedFinalState.equals(RMAppAttemptState.FAILED) || appAttempt.targetedFinalState.equals(RMAppAttemptState.KILLED)) {
                return;
            }
            appAttempt.rememberTargetTransitions(event, new AMFinishedAfterFinalSavingTransition(appAttempt.eventCausingFinalSaving), RMAppAttemptState.FINISHED);
        }
    }
    
    private static class AMFinishedAfterFinalSavingTransition extends BaseTransition
    {
        RMAppAttemptEvent amUnregisteredEvent;
        
        public AMFinishedAfterFinalSavingTransition(final RMAppAttemptEvent amUnregisteredEvent) {
            this.amUnregisteredEvent = amUnregisteredEvent;
        }
        
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            appAttempt.updateInfoOnAMUnregister(this.amUnregisteredEvent);
            new FinalTransition(RMAppAttemptState.FINISHED).transition(appAttempt, event);
        }
    }
    
    private static class AMExpiredAtFinalSavingTransition extends BaseTransition
    {
        @Override
        public void transition(final RMAppAttemptImpl appAttempt, final RMAppAttemptEvent event) {
            if (appAttempt.targetedFinalState.equals(RMAppAttemptState.FAILED) || appAttempt.targetedFinalState.equals(RMAppAttemptState.KILLED)) {
                return;
            }
            appAttempt.rememberTargetTransitions(event, new AMFinishedAfterFinalSavingTransition(appAttempt.eventCausingFinalSaving), RMAppAttemptState.FINISHED);
        }
    }
}
