// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

import org.apache.hadoop.yarn.event.Event;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.state.SingleArcTransition;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.RMFatalEvent;
import org.apache.hadoop.yarn.server.resourcemanager.RMFatalEventType;
import org.apache.hadoop.yarn.state.InvalidStateTransitonException;
import javax.crypto.SecretKey;
import java.util.Iterator;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.AMRMTokenSecretManagerState;
import org.apache.hadoop.security.token.delegation.DelegationKey;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationAttemptStateData;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.AggregateAppResourceUsage;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationSubmissionContextPBImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.records.Version;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.state.StateMachine;
import org.apache.hadoop.yarn.state.StateMachineFactory;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class RMStateStore extends AbstractService
{
    protected static final String RM_APP_ROOT = "RMAppRoot";
    protected static final String RM_DT_SECRET_MANAGER_ROOT = "RMDTSecretManagerRoot";
    protected static final String DELEGATION_KEY_PREFIX = "DelegationKey_";
    protected static final String DELEGATION_TOKEN_PREFIX = "RMDelegationToken_";
    protected static final String DELEGATION_TOKEN_SEQUENCE_NUMBER_PREFIX = "RMDTSequenceNumber_";
    protected static final String AMRMTOKEN_SECRET_MANAGER_ROOT = "AMRMTokenSecretManagerRoot";
    protected static final String VERSION_NODE = "RMVersionNode";
    protected static final String EPOCH_NODE = "EpochNode";
    private ResourceManager resourceManager;
    public static final Log LOG;
    private static final StateMachineFactory<RMStateStore, RMStateStoreState, RMStateStoreEventType, RMStateStoreEvent> stateMachineFactory;
    private final StateMachine<RMStateStoreState, RMStateStoreEventType, RMStateStoreEvent> stateMachine;
    private Dispatcher rmDispatcher;
    AsyncDispatcher dispatcher;
    public static final Text AM_RM_TOKEN_SERVICE;
    public static final Text AM_CLIENT_TOKEN_MASTER_KEY_NAME;
    
    public RMStateStore() {
        super(RMStateStore.class.getName());
        this.stateMachine = RMStateStore.stateMachineFactory.make(this);
    }
    
    public void setRMDispatcher(final Dispatcher dispatcher) {
        this.rmDispatcher = dispatcher;
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        (this.dispatcher = new AsyncDispatcher()).init(conf);
        this.dispatcher.register(RMStateStoreEventType.class, new ForwardingEventHandler());
        this.dispatcher.setDrainEventsOnStop();
        this.initInternal(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        this.dispatcher.start();
        this.startInternal();
    }
    
    protected abstract void initInternal(final Configuration p0) throws Exception;
    
    protected abstract void startInternal() throws Exception;
    
    @Override
    protected void serviceStop() throws Exception {
        this.closeInternal();
        this.dispatcher.stop();
    }
    
    protected abstract void closeInternal() throws Exception;
    
    public void checkVersion() throws Exception {
        Version loadedVersion = this.loadVersion();
        RMStateStore.LOG.info("Loaded RM state version info " + loadedVersion);
        if (loadedVersion != null && loadedVersion.equals(this.getCurrentVersion())) {
            return;
        }
        if (loadedVersion == null) {
            loadedVersion = Version.newInstance(1, 0);
        }
        if (loadedVersion.isCompatibleTo(this.getCurrentVersion())) {
            RMStateStore.LOG.info("Storing RM state version info " + this.getCurrentVersion());
            this.storeVersion();
            return;
        }
        throw new RMStateVersionIncompatibleException("Expecting RM state version " + this.getCurrentVersion() + ", but loading version " + loadedVersion);
    }
    
    protected abstract Version loadVersion() throws Exception;
    
    protected abstract void storeVersion() throws Exception;
    
    protected abstract Version getCurrentVersion();
    
    public abstract long getAndIncrementEpoch() throws Exception;
    
    public abstract RMState loadState() throws Exception;
    
    public synchronized void storeNewApplication(final RMApp app) {
        final ApplicationSubmissionContext context = app.getApplicationSubmissionContext();
        assert context instanceof ApplicationSubmissionContextPBImpl;
        final ApplicationState appState = new ApplicationState(app.getSubmitTime(), app.getStartTime(), context, app.getUser());
        this.dispatcher.getEventHandler().handle(new RMStateStoreAppEvent(appState));
    }
    
    public synchronized void updateApplicationState(final ApplicationState appState) {
        this.dispatcher.getEventHandler().handle(new RMStateUpdateAppEvent(appState));
    }
    
    protected abstract void storeApplicationStateInternal(final ApplicationId p0, final ApplicationStateData p1) throws Exception;
    
    protected abstract void updateApplicationStateInternal(final ApplicationId p0, final ApplicationStateData p1) throws Exception;
    
    public synchronized void storeNewApplicationAttempt(final RMAppAttempt appAttempt) {
        final Credentials credentials = this.getCredentialsFromAppAttempt(appAttempt);
        final AggregateAppResourceUsage resUsage = appAttempt.getRMAppAttemptMetrics().getAggregateAppResourceUsage();
        final ApplicationAttemptState attemptState = new ApplicationAttemptState(appAttempt.getAppAttemptId(), appAttempt.getMasterContainer(), credentials, appAttempt.getStartTime(), resUsage.getMemorySeconds(), resUsage.getVcoreSeconds());
        this.dispatcher.getEventHandler().handle(new RMStateStoreAppAttemptEvent(attemptState));
    }
    
    public synchronized void updateApplicationAttemptState(final ApplicationAttemptState attemptState) {
        this.dispatcher.getEventHandler().handle(new RMStateUpdateAppAttemptEvent(attemptState));
    }
    
    protected abstract void storeApplicationAttemptStateInternal(final ApplicationAttemptId p0, final ApplicationAttemptStateData p1) throws Exception;
    
    protected abstract void updateApplicationAttemptStateInternal(final ApplicationAttemptId p0, final ApplicationAttemptStateData p1) throws Exception;
    
    public synchronized void storeRMDelegationTokenAndSequenceNumber(final RMDelegationTokenIdentifier rmDTIdentifier, final Long renewDate, final int latestSequenceNumber) {
        try {
            this.storeRMDelegationTokenAndSequenceNumberState(rmDTIdentifier, renewDate, latestSequenceNumber);
        }
        catch (Exception e) {
            this.notifyStoreOperationFailed(e);
        }
    }
    
    protected abstract void storeRMDelegationTokenAndSequenceNumberState(final RMDelegationTokenIdentifier p0, final Long p1, final int p2) throws Exception;
    
    public synchronized void removeRMDelegationToken(final RMDelegationTokenIdentifier rmDTIdentifier, final int sequenceNumber) {
        try {
            this.removeRMDelegationTokenState(rmDTIdentifier);
        }
        catch (Exception e) {
            this.notifyStoreOperationFailed(e);
        }
    }
    
    protected abstract void removeRMDelegationTokenState(final RMDelegationTokenIdentifier p0) throws Exception;
    
    public synchronized void updateRMDelegationTokenAndSequenceNumber(final RMDelegationTokenIdentifier rmDTIdentifier, final Long renewDate, final int latestSequenceNumber) {
        try {
            this.updateRMDelegationTokenAndSequenceNumberInternal(rmDTIdentifier, renewDate, latestSequenceNumber);
        }
        catch (Exception e) {
            this.notifyStoreOperationFailed(e);
        }
    }
    
    protected abstract void updateRMDelegationTokenAndSequenceNumberInternal(final RMDelegationTokenIdentifier p0, final Long p1, final int p2) throws Exception;
    
    public synchronized void storeRMDTMasterKey(final DelegationKey delegationKey) {
        try {
            this.storeRMDTMasterKeyState(delegationKey);
        }
        catch (Exception e) {
            this.notifyStoreOperationFailed(e);
        }
    }
    
    protected abstract void storeRMDTMasterKeyState(final DelegationKey p0) throws Exception;
    
    public synchronized void removeRMDTMasterKey(final DelegationKey delegationKey) {
        try {
            this.removeRMDTMasterKeyState(delegationKey);
        }
        catch (Exception e) {
            this.notifyStoreOperationFailed(e);
        }
    }
    
    protected abstract void removeRMDTMasterKeyState(final DelegationKey p0) throws Exception;
    
    public abstract void storeOrUpdateAMRMTokenSecretManagerState(final AMRMTokenSecretManagerState p0, final boolean p1);
    
    public synchronized void removeApplication(final RMApp app) {
        final ApplicationState appState = new ApplicationState(app.getSubmitTime(), app.getStartTime(), app.getApplicationSubmissionContext(), app.getUser());
        for (final RMAppAttempt appAttempt : app.getAppAttempts().values()) {
            final Credentials credentials = this.getCredentialsFromAppAttempt(appAttempt);
            final ApplicationAttemptState attemptState = new ApplicationAttemptState(appAttempt.getAppAttemptId(), appAttempt.getMasterContainer(), credentials, appAttempt.getStartTime(), 0L, 0L);
            appState.attempts.put(attemptState.getAttemptId(), attemptState);
        }
        this.dispatcher.getEventHandler().handle(new RMStateStoreRemoveAppEvent(appState));
    }
    
    protected abstract void removeApplicationStateInternal(final ApplicationState p0) throws Exception;
    
    public Credentials getCredentialsFromAppAttempt(final RMAppAttempt appAttempt) {
        final Credentials credentials = new Credentials();
        final SecretKey clientTokenMasterKey = appAttempt.getClientTokenMasterKey();
        if (clientTokenMasterKey != null) {
            credentials.addSecretKey(RMStateStore.AM_CLIENT_TOKEN_MASTER_KEY_NAME, clientTokenMasterKey.getEncoded());
        }
        return credentials;
    }
    
    protected void handleStoreEvent(final RMStateStoreEvent event) {
        try {
            this.stateMachine.doTransition(event.getType(), event);
        }
        catch (InvalidStateTransitonException e) {
            RMStateStore.LOG.error("Can't handle this event at current state", e);
        }
    }
    
    protected void notifyStoreOperationFailed(final Exception failureCause) {
        if (failureCause instanceof StoreFencedException) {
            final Thread standByTransitionThread = new Thread(new StandByTransitionThread());
            standByTransitionThread.setName("StandByTransitionThread Handler");
            standByTransitionThread.start();
        }
        else {
            this.rmDispatcher.getEventHandler().handle(new RMFatalEvent(RMFatalEventType.STATE_STORE_OP_FAILED, failureCause));
        }
    }
    
    private void notifyApplication(final RMAppEvent event) {
        this.rmDispatcher.getEventHandler().handle(event);
    }
    
    private void notifyApplicationAttempt(final RMAppAttemptEvent event) {
        this.rmDispatcher.getEventHandler().handle(event);
    }
    
    public abstract void deleteStore() throws Exception;
    
    public void setResourceManager(final ResourceManager rm) {
        this.resourceManager = rm;
    }
    
    static {
        LOG = LogFactory.getLog(RMStateStore.class);
        stateMachineFactory = new StateMachineFactory<RMStateStore, RMStateStoreState, RMStateStoreEventType, RMStateStoreEvent>(RMStateStoreState.DEFAULT).addTransition(RMStateStoreState.DEFAULT, RMStateStoreState.DEFAULT, RMStateStoreEventType.STORE_APP, new StoreAppTransition()).addTransition(RMStateStoreState.DEFAULT, RMStateStoreState.DEFAULT, RMStateStoreEventType.UPDATE_APP, new UpdateAppTransition()).addTransition(RMStateStoreState.DEFAULT, RMStateStoreState.DEFAULT, RMStateStoreEventType.REMOVE_APP, new RemoveAppTransition()).addTransition(RMStateStoreState.DEFAULT, RMStateStoreState.DEFAULT, RMStateStoreEventType.STORE_APP_ATTEMPT, new StoreAppAttemptTransition()).addTransition(RMStateStoreState.DEFAULT, RMStateStoreState.DEFAULT, RMStateStoreEventType.UPDATE_APP_ATTEMPT, new UpdateAppAttemptTransition());
        AM_RM_TOKEN_SERVICE = new Text("AM_RM_TOKEN_SERVICE");
        AM_CLIENT_TOKEN_MASTER_KEY_NAME = new Text("YARN_CLIENT_TOKEN_MASTER_KEY");
    }
    
    private enum RMStateStoreState
    {
        DEFAULT;
    }
    
    private static class StoreAppTransition implements SingleArcTransition<RMStateStore, RMStateStoreEvent>
    {
        @Override
        public void transition(final RMStateStore store, final RMStateStoreEvent event) {
            if (!(event instanceof RMStateStoreAppEvent)) {
                RMStateStore.LOG.error("Illegal event type: " + event.getClass());
                return;
            }
            final ApplicationState appState = ((RMStateStoreAppEvent)event).getAppState();
            final ApplicationId appId = appState.getAppId();
            final ApplicationStateData appStateData = ApplicationStateData.newInstance(appState);
            RMStateStore.LOG.info("Storing info for app: " + appId);
            try {
                store.storeApplicationStateInternal(appId, appStateData);
                store.notifyApplication(new RMAppEvent(appId, RMAppEventType.APP_NEW_SAVED));
            }
            catch (Exception e) {
                RMStateStore.LOG.error("Error storing app: " + appId, e);
                store.notifyStoreOperationFailed(e);
            }
        }
    }
    
    private static class UpdateAppTransition implements SingleArcTransition<RMStateStore, RMStateStoreEvent>
    {
        @Override
        public void transition(final RMStateStore store, final RMStateStoreEvent event) {
            if (!(event instanceof RMStateUpdateAppEvent)) {
                RMStateStore.LOG.error("Illegal event type: " + event.getClass());
                return;
            }
            final ApplicationState appState = ((RMStateUpdateAppEvent)event).getAppState();
            final ApplicationId appId = appState.getAppId();
            final ApplicationStateData appStateData = ApplicationStateData.newInstance(appState);
            RMStateStore.LOG.info("Updating info for app: " + appId);
            try {
                store.updateApplicationStateInternal(appId, appStateData);
                store.notifyApplication(new RMAppEvent(appId, RMAppEventType.APP_UPDATE_SAVED));
            }
            catch (Exception e) {
                RMStateStore.LOG.error("Error updating app: " + appId, e);
                store.notifyStoreOperationFailed(e);
            }
        }
    }
    
    private static class RemoveAppTransition implements SingleArcTransition<RMStateStore, RMStateStoreEvent>
    {
        @Override
        public void transition(final RMStateStore store, final RMStateStoreEvent event) {
            if (!(event instanceof RMStateStoreRemoveAppEvent)) {
                RMStateStore.LOG.error("Illegal event type: " + event.getClass());
                return;
            }
            final ApplicationState appState = ((RMStateStoreRemoveAppEvent)event).getAppState();
            final ApplicationId appId = appState.getAppId();
            RMStateStore.LOG.info("Removing info for app: " + appId);
            try {
                store.removeApplicationStateInternal(appState);
            }
            catch (Exception e) {
                RMStateStore.LOG.error("Error removing app: " + appId, e);
                store.notifyStoreOperationFailed(e);
            }
        }
    }
    
    private static class StoreAppAttemptTransition implements SingleArcTransition<RMStateStore, RMStateStoreEvent>
    {
        @Override
        public void transition(final RMStateStore store, final RMStateStoreEvent event) {
            if (!(event instanceof RMStateStoreAppAttemptEvent)) {
                RMStateStore.LOG.error("Illegal event type: " + event.getClass());
                return;
            }
            final ApplicationAttemptState attemptState = ((RMStateStoreAppAttemptEvent)event).getAppAttemptState();
            try {
                final ApplicationAttemptStateData attemptStateData = ApplicationAttemptStateData.newInstance(attemptState);
                if (RMStateStore.LOG.isDebugEnabled()) {
                    RMStateStore.LOG.debug("Storing info for attempt: " + attemptState.getAttemptId());
                }
                store.storeApplicationAttemptStateInternal(attemptState.getAttemptId(), attemptStateData);
                store.notifyApplicationAttempt(new RMAppAttemptEvent(attemptState.getAttemptId(), RMAppAttemptEventType.ATTEMPT_NEW_SAVED));
            }
            catch (Exception e) {
                RMStateStore.LOG.error("Error storing appAttempt: " + attemptState.getAttemptId(), e);
                store.notifyStoreOperationFailed(e);
            }
        }
    }
    
    private static class UpdateAppAttemptTransition implements SingleArcTransition<RMStateStore, RMStateStoreEvent>
    {
        @Override
        public void transition(final RMStateStore store, final RMStateStoreEvent event) {
            if (!(event instanceof RMStateUpdateAppAttemptEvent)) {
                RMStateStore.LOG.error("Illegal event type: " + event.getClass());
                return;
            }
            final ApplicationAttemptState attemptState = ((RMStateUpdateAppAttemptEvent)event).getAppAttemptState();
            try {
                final ApplicationAttemptStateData attemptStateData = ApplicationAttemptStateData.newInstance(attemptState);
                if (RMStateStore.LOG.isDebugEnabled()) {
                    RMStateStore.LOG.debug("Updating info for attempt: " + attemptState.getAttemptId());
                }
                store.updateApplicationAttemptStateInternal(attemptState.getAttemptId(), attemptStateData);
                store.notifyApplicationAttempt(new RMAppAttemptEvent(attemptState.getAttemptId(), RMAppAttemptEventType.ATTEMPT_UPDATE_SAVED));
            }
            catch (Exception e) {
                RMStateStore.LOG.error("Error updating appAttempt: " + attemptState.getAttemptId(), e);
                store.notifyStoreOperationFailed(e);
            }
        }
    }
    
    public static class ApplicationAttemptState
    {
        final ApplicationAttemptId attemptId;
        final Container masterContainer;
        final Credentials appAttemptCredentials;
        long startTime;
        long finishTime;
        RMAppAttemptState state;
        String finalTrackingUrl;
        String diagnostics;
        int exitStatus;
        FinalApplicationStatus amUnregisteredFinalStatus;
        long memorySeconds;
        long vcoreSeconds;
        
        public ApplicationAttemptState(final ApplicationAttemptId attemptId, final Container masterContainer, final Credentials appAttemptCredentials, final long startTime, final long memorySeconds, final long vcoreSeconds) {
            this(attemptId, masterContainer, appAttemptCredentials, startTime, null, null, "", null, -1000, 0L, memorySeconds, vcoreSeconds);
        }
        
        public ApplicationAttemptState(final ApplicationAttemptId attemptId, final Container masterContainer, final Credentials appAttemptCredentials, final long startTime, final RMAppAttemptState state, final String finalTrackingUrl, final String diagnostics, final FinalApplicationStatus amUnregisteredFinalStatus, final int exitStatus, final long finishTime, final long memorySeconds, final long vcoreSeconds) {
            this.startTime = 0L;
            this.finishTime = 0L;
            this.finalTrackingUrl = "N/A";
            this.exitStatus = -1000;
            this.attemptId = attemptId;
            this.masterContainer = masterContainer;
            this.appAttemptCredentials = appAttemptCredentials;
            this.startTime = startTime;
            this.state = state;
            this.finalTrackingUrl = finalTrackingUrl;
            this.diagnostics = ((diagnostics == null) ? "" : diagnostics);
            this.amUnregisteredFinalStatus = amUnregisteredFinalStatus;
            this.exitStatus = exitStatus;
            this.finishTime = finishTime;
            this.memorySeconds = memorySeconds;
            this.vcoreSeconds = vcoreSeconds;
        }
        
        public Container getMasterContainer() {
            return this.masterContainer;
        }
        
        public ApplicationAttemptId getAttemptId() {
            return this.attemptId;
        }
        
        public Credentials getAppAttemptCredentials() {
            return this.appAttemptCredentials;
        }
        
        public RMAppAttemptState getState() {
            return this.state;
        }
        
        public String getFinalTrackingUrl() {
            return this.finalTrackingUrl;
        }
        
        public String getDiagnostics() {
            return this.diagnostics;
        }
        
        public long getStartTime() {
            return this.startTime;
        }
        
        public FinalApplicationStatus getFinalApplicationStatus() {
            return this.amUnregisteredFinalStatus;
        }
        
        public int getAMContainerExitStatus() {
            return this.exitStatus;
        }
        
        public long getMemorySeconds() {
            return this.memorySeconds;
        }
        
        public long getVcoreSeconds() {
            return this.vcoreSeconds;
        }
        
        public long getFinishTime() {
            return this.finishTime;
        }
    }
    
    public static class ApplicationState
    {
        final ApplicationSubmissionContext context;
        final long submitTime;
        final long startTime;
        final String user;
        Map<ApplicationAttemptId, ApplicationAttemptState> attempts;
        RMAppState state;
        String diagnostics;
        long finishTime;
        
        public ApplicationState(final long submitTime, final long startTime, final ApplicationSubmissionContext context, final String user) {
            this(submitTime, startTime, context, user, null, "", 0L);
        }
        
        public ApplicationState(final long submitTime, final long startTime, final ApplicationSubmissionContext context, final String user, final RMAppState state, final String diagnostics, final long finishTime) {
            this.attempts = new HashMap<ApplicationAttemptId, ApplicationAttemptState>();
            this.submitTime = submitTime;
            this.startTime = startTime;
            this.context = context;
            this.user = user;
            this.state = state;
            this.diagnostics = ((diagnostics == null) ? "" : diagnostics);
            this.finishTime = finishTime;
        }
        
        public ApplicationId getAppId() {
            return this.context.getApplicationId();
        }
        
        public long getSubmitTime() {
            return this.submitTime;
        }
        
        public long getStartTime() {
            return this.startTime;
        }
        
        public int getAttemptCount() {
            return this.attempts.size();
        }
        
        public ApplicationSubmissionContext getApplicationSubmissionContext() {
            return this.context;
        }
        
        public ApplicationAttemptState getAttempt(final ApplicationAttemptId attemptId) {
            return this.attempts.get(attemptId);
        }
        
        public String getUser() {
            return this.user;
        }
        
        public RMAppState getState() {
            return this.state;
        }
        
        public String getDiagnostics() {
            return this.diagnostics;
        }
        
        public long getFinishTime() {
            return this.finishTime;
        }
    }
    
    public static class RMDTSecretManagerState
    {
        Map<RMDelegationTokenIdentifier, Long> delegationTokenState;
        Set<DelegationKey> masterKeyState;
        int dtSequenceNumber;
        
        public RMDTSecretManagerState() {
            this.delegationTokenState = new HashMap<RMDelegationTokenIdentifier, Long>();
            this.masterKeyState = new HashSet<DelegationKey>();
            this.dtSequenceNumber = 0;
        }
        
        public Map<RMDelegationTokenIdentifier, Long> getTokenState() {
            return this.delegationTokenState;
        }
        
        public Set<DelegationKey> getMasterKeyState() {
            return this.masterKeyState;
        }
        
        public int getDTSequenceNumber() {
            return this.dtSequenceNumber;
        }
    }
    
    public static class RMState
    {
        Map<ApplicationId, ApplicationState> appState;
        RMDTSecretManagerState rmSecretManagerState;
        AMRMTokenSecretManagerState amrmTokenSecretManagerState;
        
        public RMState() {
            this.appState = new TreeMap<ApplicationId, ApplicationState>();
            this.rmSecretManagerState = new RMDTSecretManagerState();
            this.amrmTokenSecretManagerState = null;
        }
        
        public Map<ApplicationId, ApplicationState> getApplicationState() {
            return this.appState;
        }
        
        public RMDTSecretManagerState getRMDTSecretManagerState() {
            return this.rmSecretManagerState;
        }
        
        public AMRMTokenSecretManagerState getAMRMTokenSecretManagerState() {
            return this.amrmTokenSecretManagerState;
        }
    }
    
    private final class ForwardingEventHandler implements EventHandler<RMStateStoreEvent>
    {
        @Override
        public void handle(final RMStateStoreEvent event) {
            RMStateStore.this.handleStoreEvent(event);
        }
    }
    
    private class StandByTransitionThread implements Runnable
    {
        @Override
        public void run() {
            RMStateStore.LOG.info("RMStateStore has been fenced");
            RMStateStore.this.resourceManager.handleTransitionToStandBy();
        }
    }
}
