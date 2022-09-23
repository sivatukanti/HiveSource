// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.hadoop.yarn.server.resourcemanager.RMAppManagerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.RMAppManagerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeCleanAppEvent;
import org.apache.hadoop.yarn.state.MultipleArcTransition;
import java.util.EnumSet;
import org.apache.hadoop.yarn.state.SingleArcTransition;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.event.Event;
import java.io.IOException;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.yarn.api.records.ReservationId;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.AggregateAppResourceUsage;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptMetrics;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import java.util.Iterator;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppStartAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptImpl;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.state.InvalidStateTransitonException;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.hadoop.yarn.server.webproxy.ProxyUriUtils;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.security.client.ClientToAMTokenIdentifier;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.server.resourcemanager.RMServerUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import java.util.Collection;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import java.util.Collections;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.util.SystemClock;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import org.apache.hadoop.yarn.state.StateMachine;
import org.apache.hadoop.yarn.state.StateMachineFactory;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import java.util.Set;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hadoop.yarn.server.resourcemanager.ApplicationMasterService;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.Recoverable;

public class RMAppImpl implements RMApp, Recoverable
{
    private static final Log LOG;
    private static final String UNAVAILABLE = "N/A";
    private final ApplicationId applicationId;
    private final RMContext rmContext;
    private final Configuration conf;
    private final String user;
    private final String name;
    private final ApplicationSubmissionContext submissionContext;
    private final Dispatcher dispatcher;
    private final YarnScheduler scheduler;
    private final ApplicationMasterService masterService;
    private final StringBuilder diagnostics;
    private final int maxAppAttempts;
    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;
    private final Map<ApplicationAttemptId, RMAppAttempt> attempts;
    private final long submitTime;
    private final Set<RMNode> updatedNodes;
    private final String applicationType;
    private final Set<String> applicationTags;
    private final long attemptFailuresValidityInterval;
    private Clock systemClock;
    private boolean isNumAttemptsBeyondThreshold;
    private long startTime;
    private long finishTime;
    private long storedFinishTime;
    private volatile RMAppAttempt currentAttempt;
    private String queue;
    private EventHandler handler;
    private static final AppFinishedTransition FINISHED_TRANSITION;
    private Set<NodeId> ranNodes;
    private RMAppState stateBeforeKilling;
    private RMAppState stateBeforeFinalSaving;
    private RMAppEvent eventCausingFinalSaving;
    private RMAppState targetedFinalState;
    private RMAppState recoveredFinalState;
    private ResourceRequest amReq;
    Object transitionTodo;
    private static final StateMachineFactory<RMAppImpl, RMAppState, RMAppEventType, RMAppEvent> stateMachineFactory;
    private final StateMachine<RMAppState, RMAppEventType, RMAppEvent> stateMachine;
    private static final int DUMMY_APPLICATION_ATTEMPT_NUMBER = -1;
    
    public RMAppImpl(final ApplicationId applicationId, final RMContext rmContext, final Configuration config, final String name, final String user, final String queue, final ApplicationSubmissionContext submissionContext, final YarnScheduler scheduler, final ApplicationMasterService masterService, final long submitTime, final String applicationType, final Set<String> applicationTags, final ResourceRequest amReq) {
        this.diagnostics = new StringBuilder();
        this.attempts = new LinkedHashMap<ApplicationAttemptId, RMAppAttempt>();
        this.updatedNodes = new HashSet<RMNode>();
        this.isNumAttemptsBeyondThreshold = false;
        this.finishTime = 0L;
        this.storedFinishTime = 0L;
        this.ranNodes = new ConcurrentSkipListSet<NodeId>();
        this.systemClock = new SystemClock();
        this.applicationId = applicationId;
        this.name = name;
        this.rmContext = rmContext;
        this.dispatcher = rmContext.getDispatcher();
        this.handler = this.dispatcher.getEventHandler();
        this.conf = config;
        this.user = user;
        this.queue = queue;
        this.submissionContext = submissionContext;
        this.scheduler = scheduler;
        this.masterService = masterService;
        this.submitTime = submitTime;
        this.startTime = this.systemClock.getTime();
        this.applicationType = applicationType;
        this.applicationTags = applicationTags;
        this.amReq = amReq;
        final int globalMaxAppAttempts = this.conf.getInt("yarn.resourcemanager.am.max-attempts", 2);
        final int individualMaxAppAttempts = submissionContext.getMaxAppAttempts();
        if (individualMaxAppAttempts <= 0 || individualMaxAppAttempts > globalMaxAppAttempts) {
            this.maxAppAttempts = globalMaxAppAttempts;
            RMAppImpl.LOG.warn("The specific max attempts: " + individualMaxAppAttempts + " for application: " + applicationId.getId() + " is invalid, because it is out of the range [1, " + globalMaxAppAttempts + "]. Use the global max attempts instead.");
        }
        else {
            this.maxAppAttempts = individualMaxAppAttempts;
        }
        this.attemptFailuresValidityInterval = submissionContext.getAttemptFailuresValidityInterval();
        if (this.attemptFailuresValidityInterval > 0L) {
            RMAppImpl.LOG.info("The attemptFailuresValidityInterval for the application: " + this.applicationId + " is " + this.attemptFailuresValidityInterval + ".");
        }
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
        this.stateMachine = RMAppImpl.stateMachineFactory.make(this);
        rmContext.getRMApplicationHistoryWriter().applicationStarted(this);
        rmContext.getSystemMetricsPublisher().appCreated(this, this.startTime);
    }
    
    @Override
    public ApplicationId getApplicationId() {
        return this.applicationId;
    }
    
    @Override
    public ApplicationSubmissionContext getApplicationSubmissionContext() {
        return this.submissionContext;
    }
    
    @Override
    public FinalApplicationStatus getFinalApplicationStatus() {
        this.readLock.lock();
        try {
            if (this.currentAttempt != null && this.currentAttempt.getFinalApplicationStatus() != null) {
                return this.currentAttempt.getFinalApplicationStatus();
            }
            return this.createFinalApplicationStatus(this.stateMachine.getCurrentState());
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public RMAppState getState() {
        this.readLock.lock();
        try {
            return this.stateMachine.getCurrentState();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String getUser() {
        return this.user;
    }
    
    @Override
    public float getProgress() {
        final RMAppAttempt attempt = this.currentAttempt;
        if (attempt != null) {
            return attempt.getProgress();
        }
        return 0.0f;
    }
    
    @Override
    public RMAppAttempt getRMAppAttempt(final ApplicationAttemptId appAttemptId) {
        this.readLock.lock();
        try {
            return this.attempts.get(appAttemptId);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String getQueue() {
        return this.queue;
    }
    
    @Override
    public void setQueue(final String queue) {
        this.queue = queue;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public RMAppAttempt getCurrentAppAttempt() {
        return this.currentAttempt;
    }
    
    @Override
    public Map<ApplicationAttemptId, RMAppAttempt> getAppAttempts() {
        this.readLock.lock();
        try {
            return Collections.unmodifiableMap((Map<? extends ApplicationAttemptId, ? extends RMAppAttempt>)this.attempts);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private FinalApplicationStatus createFinalApplicationStatus(final RMAppState state) {
        switch (state) {
            case NEW:
            case NEW_SAVING:
            case SUBMITTED:
            case ACCEPTED:
            case RUNNING:
            case FINAL_SAVING:
            case KILLING: {
                return FinalApplicationStatus.UNDEFINED;
            }
            case FINISHING:
            case FINISHED:
            case FAILED: {
                return FinalApplicationStatus.FAILED;
            }
            case KILLED: {
                return FinalApplicationStatus.KILLED;
            }
            default: {
                throw new YarnRuntimeException("Unknown state passed!");
            }
        }
    }
    
    @Override
    public int pullRMNodeUpdates(final Collection<RMNode> updatedNodes) {
        this.writeLock.lock();
        try {
            final int updatedNodeCount = this.updatedNodes.size();
            updatedNodes.addAll(this.updatedNodes);
            this.updatedNodes.clear();
            return updatedNodeCount;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public ApplicationReport createAndGetApplicationReport(final String clientUserName, final boolean allowAccess) {
        this.readLock.lock();
        try {
            ApplicationAttemptId currentApplicationAttemptId = null;
            Token clientToAMToken = null;
            String trackingUrl = "N/A";
            String host = "N/A";
            String origTrackingUrl = "N/A";
            int rpcPort = -1;
            ApplicationResourceUsageReport appUsageReport = RMServerUtils.DUMMY_APPLICATION_RESOURCE_USAGE_REPORT;
            final FinalApplicationStatus finishState = this.getFinalApplicationStatus();
            String diags = "N/A";
            float progress = 0.0f;
            Token amrmToken = null;
            if (allowAccess) {
                trackingUrl = this.getDefaultProxyTrackingUrl();
                if (this.currentAttempt != null) {
                    currentApplicationAttemptId = this.currentAttempt.getAppAttemptId();
                    trackingUrl = this.currentAttempt.getTrackingUrl();
                    origTrackingUrl = this.currentAttempt.getOriginalTrackingUrl();
                    if (UserGroupInformation.isSecurityEnabled()) {
                        final org.apache.hadoop.security.token.Token<ClientToAMTokenIdentifier> attemptClientToAMToken = this.currentAttempt.createClientToken(clientUserName);
                        if (attemptClientToAMToken != null) {
                            clientToAMToken = BuilderUtils.newClientToAMToken(attemptClientToAMToken.getIdentifier(), attemptClientToAMToken.getKind().toString(), attemptClientToAMToken.getPassword(), attemptClientToAMToken.getService().toString());
                        }
                    }
                    host = this.currentAttempt.getHost();
                    rpcPort = this.currentAttempt.getRpcPort();
                    appUsageReport = this.currentAttempt.getApplicationResourceUsageReport();
                    progress = this.currentAttempt.getProgress();
                }
                diags = this.diagnostics.toString();
                if (this.currentAttempt != null && this.currentAttempt.getAppAttemptState() == RMAppAttemptState.LAUNCHED && this.getApplicationSubmissionContext().getUnmanagedAM() && clientUserName != null && this.getUser().equals(clientUserName)) {
                    final org.apache.hadoop.security.token.Token<AMRMTokenIdentifier> token = this.currentAttempt.getAMRMToken();
                    if (token != null) {
                        amrmToken = BuilderUtils.newAMRMToken(token.getIdentifier(), token.getKind().toString(), token.getPassword(), token.getService().toString());
                    }
                }
                final RMAppMetrics rmAppMetrics = this.getRMAppMetrics();
                appUsageReport.setMemorySeconds(rmAppMetrics.getMemorySeconds());
                appUsageReport.setVcoreSeconds(rmAppMetrics.getVcoreSeconds());
            }
            if (currentApplicationAttemptId == null) {
                currentApplicationAttemptId = BuilderUtils.newApplicationAttemptId(this.applicationId, -1);
            }
            return BuilderUtils.newApplicationReport(this.applicationId, currentApplicationAttemptId, this.user, this.queue, this.name, host, rpcPort, clientToAMToken, this.createApplicationState(), diags, trackingUrl, this.startTime, this.finishTime, finishState, appUsageReport, origTrackingUrl, progress, this.applicationType, amrmToken, this.applicationTags);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private String getDefaultProxyTrackingUrl() {
        try {
            final String scheme = WebAppUtils.getHttpSchemePrefix(this.conf);
            final String proxy = WebAppUtils.getProxyHostAndPort(this.conf);
            final URI proxyUri = ProxyUriUtils.getUriFromAMUrl(scheme, proxy);
            final URI result = ProxyUriUtils.getProxyUri(null, proxyUri, this.applicationId);
            return result.toASCIIString();
        }
        catch (URISyntaxException e) {
            RMAppImpl.LOG.warn("Could not generate default proxy tracking URL for " + this.applicationId);
            return "N/A";
        }
    }
    
    @Override
    public long getFinishTime() {
        this.readLock.lock();
        try {
            return this.finishTime;
        }
        finally {
            this.readLock.unlock();
        }
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
    public long getSubmitTime() {
        return this.submitTime;
    }
    
    @Override
    public String getTrackingUrl() {
        final RMAppAttempt attempt = this.currentAttempt;
        if (attempt != null) {
            return attempt.getTrackingUrl();
        }
        return null;
    }
    
    @Override
    public String getOriginalTrackingUrl() {
        final RMAppAttempt attempt = this.currentAttempt;
        if (attempt != null) {
            return attempt.getOriginalTrackingUrl();
        }
        return null;
    }
    
    @Override
    public StringBuilder getDiagnostics() {
        this.readLock.lock();
        try {
            return this.diagnostics;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public int getMaxAppAttempts() {
        return this.maxAppAttempts;
    }
    
    @Override
    public void handle(final RMAppEvent event) {
        this.writeLock.lock();
        try {
            final ApplicationId appID = event.getApplicationId();
            RMAppImpl.LOG.debug("Processing event for " + appID + " of type " + ((AbstractEvent<Object>)event).getType());
            final RMAppState oldState = this.getState();
            try {
                this.stateMachine.doTransition(event.getType(), event);
            }
            catch (InvalidStateTransitonException e) {
                RMAppImpl.LOG.error("Can't handle this event at current state", e);
            }
            if (oldState != this.getState()) {
                RMAppImpl.LOG.info(appID + " State change from " + oldState + " to " + this.getState());
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void recover(final RMStateStore.RMState state) {
        final RMStateStore.ApplicationState appState = state.getApplicationState().get(this.getApplicationId());
        this.recoveredFinalState = appState.getState();
        RMAppImpl.LOG.info("Recovering app: " + this.getApplicationId() + " with " + appState.getAttemptCount() + " attempts and final state = " + this.recoveredFinalState);
        this.diagnostics.append(appState.getDiagnostics());
        this.storedFinishTime = appState.getFinishTime();
        this.startTime = appState.getStartTime();
        for (int i = 0; i < appState.getAttemptCount(); ++i) {
            this.createNewAttempt();
            ((RMAppAttemptImpl)this.currentAttempt).recover(state);
        }
    }
    
    private void createNewAttempt() {
        final ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(this.applicationId, this.attempts.size() + 1);
        final RMAppAttempt attempt = new RMAppAttemptImpl(appAttemptId, this.rmContext, this.scheduler, this.masterService, this.submissionContext, this.conf, this.maxAppAttempts == this.getNumFailedAppAttempts() + 1, this.amReq);
        this.attempts.put(appAttemptId, attempt);
        this.currentAttempt = attempt;
    }
    
    private void createAndStartNewAttempt(final boolean transferStateFromPreviousAttempt) {
        this.createNewAttempt();
        this.handler.handle(new RMAppStartAttemptEvent(this.currentAttempt.getAppAttemptId(), transferStateFromPreviousAttempt));
    }
    
    private void processNodeUpdate(final RMAppNodeUpdateEvent.RMAppNodeUpdateType type, final RMNode node) {
        final NodeState nodeState = node.getState();
        this.updatedNodes.add(node);
        RMAppImpl.LOG.debug("Received node update event:" + type + " for node:" + node + " with state:" + nodeState);
    }
    
    private void recoverAppAttempts() {
        for (final RMAppAttempt attempt : this.getAppAttempts().values()) {
            attempt.handle(new RMAppAttemptEvent(attempt.getAppAttemptId(), RMAppAttemptEventType.RECOVER));
        }
    }
    
    private String getAppAttemptFailedDiagnostics(final RMAppEvent event) {
        String msg = null;
        final RMAppFailedAttemptEvent failedEvent = (RMAppFailedAttemptEvent)event;
        if (this.submissionContext.getUnmanagedAM()) {
            msg = "Unmanaged application " + this.getApplicationId() + " failed due to " + failedEvent.getDiagnostics() + ". Failing the application.";
        }
        else if (this.isNumAttemptsBeyondThreshold) {
            msg = "Application " + this.getApplicationId() + " failed " + this.maxAppAttempts + " times due to " + failedEvent.getDiagnostics() + ". Failing the application.";
        }
        return msg;
    }
    
    private void rememberTargetTransitions(final RMAppEvent event, final Object transitionToDo, final RMAppState targetFinalState) {
        this.transitionTodo = transitionToDo;
        this.targetedFinalState = targetFinalState;
        this.eventCausingFinalSaving = event;
    }
    
    private void rememberTargetTransitionsAndStoreState(final RMAppEvent event, final Object transitionToDo, final RMAppState targetFinalState, final RMAppState stateToBeStored) {
        this.rememberTargetTransitions(event, transitionToDo, targetFinalState);
        this.stateBeforeFinalSaving = this.getState();
        this.storedFinishTime = this.systemClock.getTime();
        RMAppImpl.LOG.info("Updating application " + this.applicationId + " with final state: " + this.targetedFinalState);
        String diags = null;
        switch (event.getType()) {
            case APP_REJECTED: {
                final RMAppRejectedEvent rejectedEvent = (RMAppRejectedEvent)event;
                diags = rejectedEvent.getMessage();
                break;
            }
            case ATTEMPT_FINISHED: {
                final RMAppFinishedAttemptEvent finishedEvent = (RMAppFinishedAttemptEvent)event;
                diags = finishedEvent.getDiagnostics();
                break;
            }
            case ATTEMPT_FAILED: {
                final RMAppFailedAttemptEvent failedEvent = (RMAppFailedAttemptEvent)event;
                diags = this.getAppAttemptFailedDiagnostics(failedEvent);
                break;
            }
            case ATTEMPT_KILLED: {
                diags = getAppKilledDiagnostics();
                break;
            }
        }
        final RMStateStore.ApplicationState appState = new RMStateStore.ApplicationState(this.submitTime, this.startTime, this.submissionContext, this.user, stateToBeStored, diags, this.storedFinishTime);
        this.rmContext.getStateStore().updateApplicationState(appState);
    }
    
    private static String getAppKilledDiagnostics() {
        return "Application killed by user.";
    }
    
    private int getNumFailedAppAttempts() {
        int completedAttempts = 0;
        final long endTime = this.systemClock.getTime();
        for (final RMAppAttempt attempt : this.attempts.values()) {
            if (attempt.shouldCountTowardsMaxAttemptRetry() && (this.attemptFailuresValidityInterval <= 0L || attempt.getFinishTime() > endTime - this.attemptFailuresValidityInterval)) {
                ++completedAttempts;
            }
        }
        return completedAttempts;
    }
    
    @Override
    public String getApplicationType() {
        return this.applicationType;
    }
    
    @Override
    public Set<String> getApplicationTags() {
        return this.applicationTags;
    }
    
    @Override
    public boolean isAppFinalStateStored() {
        final RMAppState state = this.getState();
        return state.equals(RMAppState.FINISHING) || state.equals(RMAppState.FINISHED) || state.equals(RMAppState.FAILED) || state.equals(RMAppState.KILLED);
    }
    
    @Override
    public YarnApplicationState createApplicationState() {
        RMAppState rmAppState = this.getState();
        if (rmAppState.equals(RMAppState.FINAL_SAVING)) {
            rmAppState = this.stateBeforeFinalSaving;
        }
        if (rmAppState.equals(RMAppState.KILLING)) {
            rmAppState = this.stateBeforeKilling;
        }
        return RMServerUtils.createApplicationState(rmAppState);
    }
    
    public static boolean isAppInFinalState(final RMApp rmApp) {
        RMAppState appState = ((RMAppImpl)rmApp).getRecoveredFinalState();
        if (appState == null) {
            appState = rmApp.getState();
        }
        return appState == RMAppState.FAILED || appState == RMAppState.FINISHED || appState == RMAppState.KILLED;
    }
    
    private RMAppState getRecoveredFinalState() {
        return this.recoveredFinalState;
    }
    
    @Override
    public Set<NodeId> getRanNodes() {
        return this.ranNodes;
    }
    
    @Override
    public RMAppMetrics getRMAppMetrics() {
        final Resource resourcePreempted = Resource.newInstance(0, 0);
        int numAMContainerPreempted = 0;
        int numNonAMContainerPreempted = 0;
        long memorySeconds = 0L;
        long vcoreSeconds = 0L;
        for (final RMAppAttempt attempt : this.attempts.values()) {
            if (null != attempt) {
                final RMAppAttemptMetrics attemptMetrics = attempt.getRMAppAttemptMetrics();
                Resources.addTo(resourcePreempted, attemptMetrics.getResourcePreempted());
                numAMContainerPreempted += (attemptMetrics.getIsPreempted() ? 1 : 0);
                numNonAMContainerPreempted += attemptMetrics.getNumNonAMContainersPreempted();
                final AggregateAppResourceUsage resUsage = attempt.getRMAppAttemptMetrics().getAggregateAppResourceUsage();
                memorySeconds += resUsage.getMemorySeconds();
                vcoreSeconds += resUsage.getVcoreSeconds();
            }
        }
        return new RMAppMetrics(resourcePreempted, numNonAMContainerPreempted, numAMContainerPreempted, memorySeconds, vcoreSeconds);
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public void setSystemClock(final Clock clock) {
        this.systemClock = clock;
    }
    
    @Override
    public ReservationId getReservationId() {
        return this.submissionContext.getReservationID();
    }
    
    protected Credentials parseCredentials() throws IOException {
        final Credentials credentials = new Credentials();
        final DataInputByteBuffer dibb = new DataInputByteBuffer();
        final ByteBuffer tokens = this.submissionContext.getAMContainerSpec().getTokens();
        if (tokens != null) {
            dibb.reset(tokens);
            credentials.readTokenStorageStream(dibb);
            tokens.rewind();
        }
        return credentials;
    }
    
    static {
        LOG = LogFactory.getLog(RMAppImpl.class);
        FINISHED_TRANSITION = new AppFinishedTransition();
        stateMachineFactory = new StateMachineFactory<RMAppImpl, RMAppState, RMAppEventType, RMAppEvent>(RMAppState.NEW).addTransition(RMAppState.NEW, RMAppState.NEW, RMAppEventType.NODE_UPDATE, new RMAppNodeUpdateTransition()).addTransition(RMAppState.NEW, RMAppState.NEW_SAVING, RMAppEventType.START, new RMAppNewlySavingTransition()).addTransition(RMAppState.NEW, EnumSet.of(RMAppState.SUBMITTED, new RMAppState[] { RMAppState.ACCEPTED, RMAppState.FINISHED, RMAppState.FAILED, RMAppState.KILLED, RMAppState.FINAL_SAVING }), RMAppEventType.RECOVER, new RMAppRecoveredTransition()).addTransition(RMAppState.NEW, RMAppState.KILLED, RMAppEventType.KILL, new AppKilledTransition()).addTransition(RMAppState.NEW, RMAppState.FINAL_SAVING, RMAppEventType.APP_REJECTED, new FinalSavingTransition(new AppRejectedTransition(), RMAppState.FAILED)).addTransition(RMAppState.NEW_SAVING, RMAppState.NEW_SAVING, RMAppEventType.NODE_UPDATE, new RMAppNodeUpdateTransition()).addTransition(RMAppState.NEW_SAVING, RMAppState.SUBMITTED, RMAppEventType.APP_NEW_SAVED, new AddApplicationToSchedulerTransition()).addTransition(RMAppState.NEW_SAVING, RMAppState.FINAL_SAVING, RMAppEventType.KILL, new FinalSavingTransition(new AppKilledTransition(), RMAppState.KILLED)).addTransition(RMAppState.NEW_SAVING, RMAppState.FINAL_SAVING, RMAppEventType.APP_REJECTED, new FinalSavingTransition(new AppRejectedTransition(), RMAppState.FAILED)).addTransition(RMAppState.NEW_SAVING, RMAppState.NEW_SAVING, RMAppEventType.MOVE, new RMAppMoveTransition()).addTransition(RMAppState.SUBMITTED, RMAppState.SUBMITTED, RMAppEventType.NODE_UPDATE, new RMAppNodeUpdateTransition()).addTransition(RMAppState.SUBMITTED, RMAppState.SUBMITTED, RMAppEventType.MOVE, new RMAppMoveTransition()).addTransition(RMAppState.SUBMITTED, RMAppState.FINAL_SAVING, RMAppEventType.APP_REJECTED, new FinalSavingTransition(new AppRejectedTransition(), RMAppState.FAILED)).addTransition(RMAppState.SUBMITTED, RMAppState.ACCEPTED, RMAppEventType.APP_ACCEPTED, new StartAppAttemptTransition()).addTransition(RMAppState.SUBMITTED, RMAppState.FINAL_SAVING, RMAppEventType.KILL, new FinalSavingTransition(new AppKilledTransition(), RMAppState.KILLED)).addTransition(RMAppState.ACCEPTED, RMAppState.ACCEPTED, RMAppEventType.NODE_UPDATE, new RMAppNodeUpdateTransition()).addTransition(RMAppState.ACCEPTED, RMAppState.ACCEPTED, RMAppEventType.MOVE, new RMAppMoveTransition()).addTransition(RMAppState.ACCEPTED, RMAppState.RUNNING, RMAppEventType.ATTEMPT_REGISTERED).addTransition(RMAppState.ACCEPTED, EnumSet.of(RMAppState.ACCEPTED, RMAppState.FINAL_SAVING), RMAppEventType.ATTEMPT_FAILED, new AttemptFailedTransition(RMAppState.ACCEPTED)).addTransition(RMAppState.ACCEPTED, RMAppState.FINAL_SAVING, RMAppEventType.ATTEMPT_FINISHED, new FinalSavingTransition(RMAppImpl.FINISHED_TRANSITION, RMAppState.FINISHED)).addTransition(RMAppState.ACCEPTED, RMAppState.KILLING, RMAppEventType.KILL, new KillAttemptTransition()).addTransition(RMAppState.ACCEPTED, RMAppState.ACCEPTED, RMAppEventType.APP_RUNNING_ON_NODE, new AppRunningOnNodeTransition()).addTransition(RMAppState.RUNNING, RMAppState.RUNNING, RMAppEventType.NODE_UPDATE, new RMAppNodeUpdateTransition()).addTransition(RMAppState.RUNNING, RMAppState.RUNNING, RMAppEventType.MOVE, new RMAppMoveTransition()).addTransition(RMAppState.RUNNING, RMAppState.FINAL_SAVING, RMAppEventType.ATTEMPT_UNREGISTERED, new FinalSavingTransition(new AttemptUnregisteredTransition(), RMAppState.FINISHING, RMAppState.FINISHED)).addTransition(RMAppState.RUNNING, RMAppState.FINISHED, RMAppEventType.ATTEMPT_FINISHED, RMAppImpl.FINISHED_TRANSITION).addTransition(RMAppState.RUNNING, RMAppState.RUNNING, RMAppEventType.APP_RUNNING_ON_NODE, new AppRunningOnNodeTransition()).addTransition(RMAppState.RUNNING, EnumSet.of(RMAppState.ACCEPTED, RMAppState.FINAL_SAVING), RMAppEventType.ATTEMPT_FAILED, new AttemptFailedTransition(RMAppState.ACCEPTED)).addTransition(RMAppState.RUNNING, RMAppState.KILLING, RMAppEventType.KILL, new KillAttemptTransition()).addTransition(RMAppState.FINAL_SAVING, EnumSet.of(RMAppState.FINISHING, RMAppState.FAILED, RMAppState.KILLED, RMAppState.FINISHED), RMAppEventType.APP_UPDATE_SAVED, new FinalStateSavedTransition()).addTransition(RMAppState.FINAL_SAVING, RMAppState.FINAL_SAVING, RMAppEventType.ATTEMPT_FINISHED, new AttemptFinishedAtFinalSavingTransition()).addTransition(RMAppState.FINAL_SAVING, RMAppState.FINAL_SAVING, RMAppEventType.APP_RUNNING_ON_NODE, new AppRunningOnNodeTransition()).addTransition(RMAppState.FINAL_SAVING, RMAppState.FINAL_SAVING, EnumSet.of(RMAppEventType.NODE_UPDATE, RMAppEventType.KILL, RMAppEventType.APP_NEW_SAVED, RMAppEventType.MOVE)).addTransition(RMAppState.FINISHING, RMAppState.FINISHED, RMAppEventType.ATTEMPT_FINISHED, RMAppImpl.FINISHED_TRANSITION).addTransition(RMAppState.FINISHING, RMAppState.FINISHING, RMAppEventType.APP_RUNNING_ON_NODE, new AppRunningOnNodeTransition()).addTransition(RMAppState.FINISHING, RMAppState.FINISHING, EnumSet.of(RMAppEventType.NODE_UPDATE, RMAppEventType.KILL, RMAppEventType.MOVE)).addTransition(RMAppState.KILLING, RMAppState.KILLING, RMAppEventType.APP_RUNNING_ON_NODE, new AppRunningOnNodeTransition()).addTransition(RMAppState.KILLING, RMAppState.FINAL_SAVING, RMAppEventType.ATTEMPT_KILLED, new FinalSavingTransition(new AppKilledTransition(), RMAppState.KILLED)).addTransition(RMAppState.KILLING, RMAppState.FINAL_SAVING, RMAppEventType.ATTEMPT_UNREGISTERED, new FinalSavingTransition(new AttemptUnregisteredTransition(), RMAppState.FINISHING, RMAppState.FINISHED)).addTransition(RMAppState.KILLING, RMAppState.FINISHED, RMAppEventType.ATTEMPT_FINISHED, RMAppImpl.FINISHED_TRANSITION).addTransition(RMAppState.KILLING, EnumSet.of(RMAppState.FINAL_SAVING), RMAppEventType.ATTEMPT_FAILED, new AttemptFailedTransition(RMAppState.KILLING)).addTransition(RMAppState.KILLING, RMAppState.KILLING, EnumSet.of(RMAppEventType.NODE_UPDATE, RMAppEventType.ATTEMPT_REGISTERED, RMAppEventType.APP_UPDATE_SAVED, RMAppEventType.KILL, RMAppEventType.MOVE)).addTransition(RMAppState.FINISHED, RMAppState.FINISHED, RMAppEventType.APP_RUNNING_ON_NODE, new AppRunningOnNodeTransition()).addTransition(RMAppState.FINISHED, RMAppState.FINISHED, EnumSet.of(RMAppEventType.NODE_UPDATE, RMAppEventType.ATTEMPT_UNREGISTERED, RMAppEventType.ATTEMPT_FINISHED, RMAppEventType.KILL, RMAppEventType.MOVE)).addTransition(RMAppState.FAILED, RMAppState.FAILED, RMAppEventType.APP_RUNNING_ON_NODE, new AppRunningOnNodeTransition()).addTransition(RMAppState.FAILED, RMAppState.FAILED, EnumSet.of(RMAppEventType.KILL, RMAppEventType.NODE_UPDATE, RMAppEventType.MOVE)).addTransition(RMAppState.KILLED, RMAppState.KILLED, RMAppEventType.APP_RUNNING_ON_NODE, new AppRunningOnNodeTransition()).addTransition(RMAppState.KILLED, RMAppState.KILLED, EnumSet.of(RMAppEventType.APP_ACCEPTED, new RMAppEventType[] { RMAppEventType.APP_REJECTED, RMAppEventType.KILL, RMAppEventType.ATTEMPT_FINISHED, RMAppEventType.ATTEMPT_FAILED, RMAppEventType.NODE_UPDATE, RMAppEventType.MOVE })).installTopology();
    }
    
    private static class RMAppTransition implements SingleArcTransition<RMAppImpl, RMAppEvent>
    {
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
        }
    }
    
    private static final class RMAppNodeUpdateTransition extends RMAppTransition
    {
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            final RMAppNodeUpdateEvent nodeUpdateEvent = (RMAppNodeUpdateEvent)event;
            app.processNodeUpdate(nodeUpdateEvent.getUpdateType(), nodeUpdateEvent.getNode());
        }
    }
    
    private static final class AppRunningOnNodeTransition extends RMAppTransition
    {
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            final RMAppRunningOnNodeEvent nodeAddedEvent = (RMAppRunningOnNodeEvent)event;
            if (RMAppImpl.isAppInFinalState(app)) {
                app.handler.handle(new RMNodeCleanAppEvent(nodeAddedEvent.getNodeId(), nodeAddedEvent.getApplicationId()));
                return;
            }
            app.ranNodes.add(nodeAddedEvent.getNodeId());
        }
    }
    
    private static final class RMAppMoveTransition extends RMAppTransition
    {
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            final RMAppMoveEvent moveEvent = (RMAppMoveEvent)event;
            try {
                app.queue = app.scheduler.moveApplication(app.applicationId, moveEvent.getTargetQueue());
            }
            catch (YarnException ex) {
                moveEvent.getResult().setException(ex);
                return;
            }
            moveEvent.getResult().set(null);
        }
    }
    
    private static final class RMAppRecoveredTransition implements MultipleArcTransition<RMAppImpl, RMAppEvent, RMAppState>
    {
        @Override
        public RMAppState transition(final RMAppImpl app, final RMAppEvent event) {
            final RMAppRecoverEvent recoverEvent = (RMAppRecoverEvent)event;
            app.recover(recoverEvent.getRMState());
            if (app.recoveredFinalState != null) {
                app.recoverAppAttempts();
                new FinalTransition(app.recoveredFinalState).transition(app, event);
                return app.recoveredFinalState;
            }
            if (UserGroupInformation.isSecurityEnabled()) {
                try {
                    app.rmContext.getDelegationTokenRenewer().addApplicationSync(app.getApplicationId(), app.parseCredentials(), app.submissionContext.getCancelTokensWhenComplete(), app.getUser());
                }
                catch (Exception e) {
                    final String msg = "Failed to renew token for " + app.applicationId + " on recovery : " + e.getMessage();
                    app.diagnostics.append(msg);
                    RMAppImpl.LOG.error(msg, e);
                }
            }
            if (app.attempts.isEmpty()) {
                ((EventHandler<AppAddedSchedulerEvent>)app.scheduler).handle(new AppAddedSchedulerEvent(app.applicationId, app.submissionContext.getQueue(), app.user, app.submissionContext.getReservationID()));
                return RMAppState.SUBMITTED;
            }
            ((EventHandler<AppAddedSchedulerEvent>)app.scheduler).handle(new AppAddedSchedulerEvent(app.applicationId, app.submissionContext.getQueue(), app.user, true, app.submissionContext.getReservationID()));
            app.recoverAppAttempts();
            if (app.currentAttempt != null && (app.currentAttempt.getState() == RMAppAttemptState.KILLED || app.currentAttempt.getState() == RMAppAttemptState.FINISHED || (app.currentAttempt.getState() == RMAppAttemptState.FAILED && app.getNumFailedAppAttempts() == app.maxAppAttempts))) {
                return RMAppState.ACCEPTED;
            }
            return RMAppState.ACCEPTED;
        }
    }
    
    private static final class AddApplicationToSchedulerTransition extends RMAppTransition
    {
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            app.handler.handle(new AppAddedSchedulerEvent(app.applicationId, app.submissionContext.getQueue(), app.user, app.submissionContext.getReservationID()));
        }
    }
    
    private static final class StartAppAttemptTransition extends RMAppTransition
    {
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            app.createAndStartNewAttempt(false);
        }
    }
    
    private static final class FinalStateSavedTransition implements MultipleArcTransition<RMAppImpl, RMAppEvent, RMAppState>
    {
        @Override
        public RMAppState transition(final RMAppImpl app, final RMAppEvent event) {
            if (app.transitionTodo instanceof SingleArcTransition) {
                ((SingleArcTransition)app.transitionTodo).transition(app, app.eventCausingFinalSaving);
            }
            else if (app.transitionTodo instanceof MultipleArcTransition) {
                ((MultipleArcTransition)app.transitionTodo).transition(app, app.eventCausingFinalSaving);
            }
            return app.targetedFinalState;
        }
    }
    
    private static class AttemptFailedFinalStateSavedTransition extends RMAppTransition
    {
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            String msg = null;
            if (event instanceof RMAppFailedAttemptEvent) {
                msg = app.getAppAttemptFailedDiagnostics(event);
            }
            RMAppImpl.LOG.info(msg);
            app.diagnostics.append(msg);
            new FinalTransition(RMAppState.FAILED).transition(app, event);
        }
    }
    
    private static final class RMAppNewlySavingTransition extends RMAppTransition
    {
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            RMAppImpl.LOG.info("Storing application with id " + app.applicationId);
            app.rmContext.getStateStore().storeNewApplication(app);
        }
    }
    
    private static final class FinalSavingTransition extends RMAppTransition
    {
        Object transitionToDo;
        RMAppState targetedFinalState;
        RMAppState stateToBeStored;
        
        public FinalSavingTransition(final Object transitionToDo, final RMAppState targetedFinalState) {
            this(transitionToDo, targetedFinalState, targetedFinalState);
        }
        
        public FinalSavingTransition(final Object transitionToDo, final RMAppState targetedFinalState, final RMAppState stateToBeStored) {
            this.transitionToDo = transitionToDo;
            this.targetedFinalState = targetedFinalState;
            this.stateToBeStored = stateToBeStored;
        }
        
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            app.rememberTargetTransitionsAndStoreState(event, this.transitionToDo, this.targetedFinalState, this.stateToBeStored);
        }
    }
    
    private static class AttemptUnregisteredTransition extends RMAppTransition
    {
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            app.finishTime = app.storedFinishTime;
        }
    }
    
    private static class AppFinishedTransition extends FinalTransition
    {
        public AppFinishedTransition() {
            super(RMAppState.FINISHED);
        }
        
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            final RMAppFinishedAttemptEvent finishedEvent = (RMAppFinishedAttemptEvent)event;
            app.diagnostics.append(finishedEvent.getDiagnostics());
            super.transition(app, event);
        }
    }
    
    private static class AttemptFinishedAtFinalSavingTransition extends RMAppTransition
    {
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            if (app.targetedFinalState.equals(RMAppState.FAILED) || app.targetedFinalState.equals(RMAppState.KILLED)) {
                return;
            }
            app.rememberTargetTransitions(event, new AppFinishedFinalStateSavedTransition(app.eventCausingFinalSaving), RMAppState.FINISHED);
        }
    }
    
    private static class AppFinishedFinalStateSavedTransition extends RMAppTransition
    {
        RMAppEvent attemptUnregistered;
        
        public AppFinishedFinalStateSavedTransition(final RMAppEvent attemptUnregistered) {
            this.attemptUnregistered = attemptUnregistered;
        }
        
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            new AttemptUnregisteredTransition().transition(app, this.attemptUnregistered);
            RMAppImpl.FINISHED_TRANSITION.transition(app, event);
        }
    }
    
    private static class AppKilledTransition extends FinalTransition
    {
        public AppKilledTransition() {
            super(RMAppState.KILLED);
        }
        
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            app.diagnostics.append(getAppKilledDiagnostics());
            super.transition(app, event);
        }
    }
    
    private static class KillAttemptTransition extends RMAppTransition
    {
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            app.stateBeforeKilling = app.getState();
            app.handler.handle(new RMAppAttemptEvent(app.currentAttempt.getAppAttemptId(), RMAppAttemptEventType.KILL));
        }
    }
    
    private static final class AppRejectedTransition extends FinalTransition
    {
        public AppRejectedTransition() {
            super(RMAppState.FAILED);
        }
        
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            final RMAppRejectedEvent rejectedEvent = (RMAppRejectedEvent)event;
            app.diagnostics.append(rejectedEvent.getMessage());
            super.transition(app, event);
        }
    }
    
    private static class FinalTransition extends RMAppTransition
    {
        private final RMAppState finalState;
        
        public FinalTransition(final RMAppState finalState) {
            this.finalState = finalState;
        }
        
        @Override
        public void transition(final RMAppImpl app, final RMAppEvent event) {
            for (final NodeId nodeId : app.getRanNodes()) {
                app.handler.handle(new RMNodeCleanAppEvent(nodeId, app.applicationId));
            }
            app.finishTime = app.storedFinishTime;
            if (app.finishTime == 0L) {
                app.finishTime = app.systemClock.getTime();
            }
            if (app.recoveredFinalState == null) {
                app.handler.handle(new AppRemovedSchedulerEvent(app.applicationId, this.finalState));
            }
            app.handler.handle(new RMAppManagerEvent(app.applicationId, RMAppManagerEventType.APP_COMPLETED));
            app.rmContext.getRMApplicationHistoryWriter().applicationFinished(app, this.finalState);
            app.rmContext.getSystemMetricsPublisher().appFinished(app, this.finalState, app.finishTime);
        }
    }
    
    private static final class AttemptFailedTransition implements MultipleArcTransition<RMAppImpl, RMAppEvent, RMAppState>
    {
        private final RMAppState initialState;
        
        public AttemptFailedTransition(final RMAppState initialState) {
            this.initialState = initialState;
        }
        
        @Override
        public RMAppState transition(final RMAppImpl app, final RMAppEvent event) {
            final int numberOfFailure = app.getNumFailedAppAttempts();
            RMAppImpl.LOG.info("The number of failed attempts" + ((app.attemptFailuresValidityInterval > 0L) ? (" in previous " + app.attemptFailuresValidityInterval + " milliseconds ") : " ") + "is " + numberOfFailure + ". The max attempts is " + app.maxAppAttempts);
            if (app.submissionContext.getUnmanagedAM() || numberOfFailure >= app.maxAppAttempts) {
                if (numberOfFailure >= app.maxAppAttempts) {
                    app.isNumAttemptsBeyondThreshold = true;
                }
                app.rememberTargetTransitionsAndStoreState(event, new AttemptFailedFinalStateSavedTransition(), RMAppState.FAILED, RMAppState.FAILED);
                return RMAppState.FINAL_SAVING;
            }
            if (this.initialState.equals(RMAppState.KILLING)) {
                app.rememberTargetTransitionsAndStoreState(event, new AppKilledTransition(), RMAppState.KILLED, RMAppState.KILLED);
                return RMAppState.FINAL_SAVING;
            }
            final RMAppFailedAttemptEvent failedEvent = (RMAppFailedAttemptEvent)event;
            final boolean transferStateFromPreviousAttempt = failedEvent.getTransferStateFromPreviousAttempt();
            final RMAppAttempt oldAttempt = app.currentAttempt;
            app.createAndStartNewAttempt(transferStateFromPreviousAttempt);
            ((RMAppAttemptImpl)app.currentAttempt).transferStateFromPreviousAttempt(oldAttempt);
            return this.initialState;
        }
    }
}
