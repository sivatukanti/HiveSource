// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.event.Event;
import java.util.Iterator;
import java.util.Map;
import java.io.IOException;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.yarn.exceptions.InvalidResourceRequestException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerUtils;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptImpl;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppRecoverEvent;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.ipc.RPCUtil;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppRejectedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.security.UserGroupInformation;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.LinkedList;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.Recoverable;
import org.apache.hadoop.yarn.event.EventHandler;

public class RMAppManager implements EventHandler<RMAppManagerEvent>, Recoverable
{
    private static final Log LOG;
    private int maxCompletedAppsInMemory;
    private int maxCompletedAppsInStateStore;
    protected int completedAppsInStateStore;
    private LinkedList<ApplicationId> completedApps;
    private final RMContext rmContext;
    private final ApplicationMasterService masterService;
    private final YarnScheduler scheduler;
    private final ApplicationACLsManager applicationACLsManager;
    private Configuration conf;
    
    public RMAppManager(final RMContext context, final YarnScheduler scheduler, final ApplicationMasterService masterService, final ApplicationACLsManager applicationACLsManager, final Configuration conf) {
        this.completedAppsInStateStore = 0;
        this.completedApps = new LinkedList<ApplicationId>();
        this.rmContext = context;
        this.scheduler = scheduler;
        this.masterService = masterService;
        this.applicationACLsManager = applicationACLsManager;
        this.conf = conf;
        this.maxCompletedAppsInMemory = conf.getInt("yarn.resourcemanager.max-completed-applications", 10000);
        this.maxCompletedAppsInStateStore = conf.getInt("yarn.resourcemanager.state-store.max-completed-applications", 10000);
        if (this.maxCompletedAppsInStateStore > this.maxCompletedAppsInMemory) {
            this.maxCompletedAppsInStateStore = this.maxCompletedAppsInMemory;
        }
    }
    
    @VisibleForTesting
    public void logApplicationSummary(final ApplicationId appId) {
        ApplicationSummary.logAppSummary(this.rmContext.getRMApps().get(appId));
    }
    
    protected synchronized int getCompletedAppsListSize() {
        return this.completedApps.size();
    }
    
    protected synchronized void finishApplication(final ApplicationId applicationId) {
        if (applicationId == null) {
            RMAppManager.LOG.error("RMAppManager received completed appId of null, skipping");
        }
        else {
            if (UserGroupInformation.isSecurityEnabled()) {
                this.rmContext.getDelegationTokenRenewer().applicationFinished(applicationId);
            }
            this.completedApps.add(applicationId);
            ++this.completedAppsInStateStore;
            this.writeAuditLog(applicationId);
        }
    }
    
    protected void writeAuditLog(final ApplicationId appId) {
        final RMApp app = this.rmContext.getRMApps().get(appId);
        String operation = "UNKONWN";
        boolean success = false;
        switch (app.getState()) {
            case FAILED: {
                operation = "Application Finished - Failed";
                break;
            }
            case FINISHED: {
                operation = "Application Finished - Succeeded";
                success = true;
                break;
            }
            case KILLED: {
                operation = "Application Finished - Killed";
                success = true;
                break;
            }
        }
        if (success) {
            RMAuditLogger.logSuccess(app.getUser(), operation, "RMAppManager", app.getApplicationId());
        }
        else {
            final StringBuilder diag = app.getDiagnostics();
            final String msg = (diag == null) ? null : diag.toString();
            RMAuditLogger.logFailure(app.getUser(), operation, msg, "RMAppManager", "App failed with state: " + app.getState(), appId);
        }
    }
    
    protected synchronized void checkAppNumCompletedLimit() {
        while (this.completedAppsInStateStore > this.maxCompletedAppsInStateStore) {
            final ApplicationId removeId = this.completedApps.get(this.completedApps.size() - this.completedAppsInStateStore);
            final RMApp removeApp = this.rmContext.getRMApps().get(removeId);
            RMAppManager.LOG.info("Max number of completed apps kept in state store met: maxCompletedAppsInStateStore = " + this.maxCompletedAppsInStateStore + ", removing app " + removeApp.getApplicationId() + " from state store.");
            this.rmContext.getStateStore().removeApplication(removeApp);
            --this.completedAppsInStateStore;
        }
        while (this.completedApps.size() > this.maxCompletedAppsInMemory) {
            final ApplicationId removeId = this.completedApps.remove();
            RMAppManager.LOG.info("Application should be expired, max number of completed apps kept in memory met: maxCompletedAppsInMemory = " + this.maxCompletedAppsInMemory + ", removing app " + removeId + " from memory: ");
            this.rmContext.getRMApps().remove(removeId);
            this.applicationACLsManager.removeApplication(removeId);
        }
    }
    
    protected void submitApplication(final ApplicationSubmissionContext submissionContext, final long submitTime, final String user) throws YarnException {
        final ApplicationId applicationId = submissionContext.getApplicationId();
        final RMAppImpl application = this.createAndPopulateNewRMApp(submissionContext, submitTime, user);
        final ApplicationId appId = submissionContext.getApplicationId();
        if (UserGroupInformation.isSecurityEnabled()) {
            try {
                this.rmContext.getDelegationTokenRenewer().addApplicationAsync(appId, this.parseCredentials(submissionContext), submissionContext.getCancelTokensWhenComplete(), application.getUser());
                return;
            }
            catch (Exception e) {
                RMAppManager.LOG.warn("Unable to parse credentials.", e);
                assert application.getState() == RMAppState.NEW;
                this.rmContext.getDispatcher().getEventHandler().handle(new RMAppRejectedEvent(applicationId, e.getMessage()));
                throw RPCUtil.getRemoteException(e);
            }
        }
        this.rmContext.getDispatcher().getEventHandler().handle(new RMAppEvent(applicationId, RMAppEventType.START));
    }
    
    protected void recoverApplication(final RMStateStore.ApplicationState appState, final RMStateStore.RMState rmState) throws Exception {
        final ApplicationSubmissionContext appContext = appState.getApplicationSubmissionContext();
        final ApplicationId appId = appState.getAppId();
        final RMAppImpl application = this.createAndPopulateNewRMApp(appContext, appState.getSubmitTime(), appState.getUser());
        application.handle((RMAppEvent)new RMAppRecoverEvent(appId, rmState));
    }
    
    private RMAppImpl createAndPopulateNewRMApp(final ApplicationSubmissionContext submissionContext, final long submitTime, final String user) throws YarnException {
        final ApplicationId applicationId = submissionContext.getApplicationId();
        final ResourceRequest amReq = this.validateAndCreateResourceRequest(submissionContext);
        final RMAppImpl application = new RMAppImpl(applicationId, this.rmContext, this.conf, submissionContext.getApplicationName(), user, submissionContext.getQueue(), submissionContext, this.scheduler, this.masterService, submitTime, submissionContext.getApplicationType(), submissionContext.getApplicationTags(), amReq);
        if (this.rmContext.getRMApps().putIfAbsent(applicationId, application) != null) {
            final String message = "Application with id " + applicationId + " is already present! Cannot add a duplicate!";
            RMAppManager.LOG.warn(message);
            throw RPCUtil.getRemoteException(message);
        }
        this.applicationACLsManager.addApplication(applicationId, submissionContext.getAMContainerSpec().getApplicationACLs());
        final String appViewACLs = submissionContext.getAMContainerSpec().getApplicationACLs().get(ApplicationAccessType.VIEW_APP);
        this.rmContext.getSystemMetricsPublisher().appACLsUpdated(application, appViewACLs, System.currentTimeMillis());
        return application;
    }
    
    private ResourceRequest validateAndCreateResourceRequest(final ApplicationSubmissionContext submissionContext) throws InvalidResourceRequestException {
        if (!submissionContext.getUnmanagedAM()) {
            ResourceRequest amReq;
            if (submissionContext.getAMContainerResourceRequest() != null) {
                amReq = submissionContext.getAMContainerResourceRequest();
            }
            else {
                amReq = BuilderUtils.newResourceRequest(RMAppAttemptImpl.AM_CONTAINER_PRIORITY, "*", submissionContext.getResource(), 1);
            }
            if (null == amReq.getNodeLabelExpression()) {
                amReq.setNodeLabelExpression(submissionContext.getNodeLabelExpression());
            }
            try {
                SchedulerUtils.validateResourceRequest(amReq, this.scheduler.getMaximumResourceCapability(), submissionContext.getQueue(), this.scheduler);
            }
            catch (InvalidResourceRequestException e) {
                RMAppManager.LOG.warn("RM app submission failed in validating AM resource request for application " + submissionContext.getApplicationId(), e);
                throw e;
            }
            return amReq;
        }
        return null;
    }
    
    protected Credentials parseCredentials(final ApplicationSubmissionContext application) throws IOException {
        final Credentials credentials = new Credentials();
        final DataInputByteBuffer dibb = new DataInputByteBuffer();
        final ByteBuffer tokens = application.getAMContainerSpec().getTokens();
        if (tokens != null) {
            dibb.reset(tokens);
            credentials.readTokenStorageStream(dibb);
            tokens.rewind();
        }
        return credentials;
    }
    
    @Override
    public void recover(final RMStateStore.RMState state) throws Exception {
        final RMStateStore store = this.rmContext.getStateStore();
        assert store != null;
        final Map<ApplicationId, RMStateStore.ApplicationState> appStates = state.getApplicationState();
        RMAppManager.LOG.info("Recovering " + appStates.size() + " applications");
        for (final RMStateStore.ApplicationState appState : appStates.values()) {
            this.recoverApplication(appState, state);
        }
    }
    
    @Override
    public void handle(final RMAppManagerEvent event) {
        final ApplicationId applicationId = event.getApplicationId();
        RMAppManager.LOG.debug("RMAppManager processing event for " + applicationId + " of type " + ((AbstractEvent<Object>)event).getType());
        switch (event.getType()) {
            case APP_COMPLETED: {
                this.finishApplication(applicationId);
                this.logApplicationSummary(applicationId);
                this.checkAppNumCompletedLimit();
                break;
            }
            default: {
                RMAppManager.LOG.error("Invalid eventtype " + ((AbstractEvent<Object>)event).getType() + ". Ignoring!");
                break;
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(RMAppManager.class);
    }
    
    static class ApplicationSummary
    {
        static final Log LOG;
        static final char EQUALS = '=';
        static final char[] charsToEscape;
        
        public static SummaryBuilder createAppSummary(final RMApp app) {
            String trackingUrl = "N/A";
            String host = "N/A";
            final RMAppAttempt attempt = app.getCurrentAppAttempt();
            if (attempt != null) {
                trackingUrl = attempt.getTrackingUrl();
                host = attempt.getHost();
            }
            final SummaryBuilder summary = new SummaryBuilder().add("appId", app.getApplicationId()).add("name", app.getName()).add("user", app.getUser()).add("queue", app.getQueue()).add("state", app.getState()).add("trackingUrl", trackingUrl).add("appMasterHost", host).add("startTime", app.getStartTime()).add("finishTime", app.getFinishTime()).add("finalStatus", app.getFinalApplicationStatus());
            return summary;
        }
        
        public static void logAppSummary(final RMApp app) {
            if (app != null) {
                ApplicationSummary.LOG.info(createAppSummary(app));
            }
        }
        
        static {
            LOG = LogFactory.getLog(ApplicationSummary.class);
            charsToEscape = new char[] { ',', '=', '\\' };
        }
        
        static class SummaryBuilder
        {
            final StringBuilder buffer;
            
            SummaryBuilder() {
                this.buffer = new StringBuilder();
            }
            
            SummaryBuilder add(final String key, final long value) {
                return this._add(key, Long.toString(value));
            }
            
             <T> SummaryBuilder add(final String key, final T value) {
                final String escapedString = StringUtils.escapeString(String.valueOf(value), '\\', ApplicationSummary.charsToEscape).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
                return this._add(key, escapedString);
            }
            
            SummaryBuilder add(final SummaryBuilder summary) {
                if (this.buffer.length() > 0) {
                    this.buffer.append(',');
                }
                this.buffer.append((CharSequence)summary.buffer);
                return this;
            }
            
            SummaryBuilder _add(final String key, final String value) {
                if (this.buffer.length() > 0) {
                    this.buffer.append(',');
                }
                this.buffer.append(key).append('=').append(value);
                return this;
            }
            
            @Override
            public String toString() {
                return this.buffer.toString();
            }
        }
    }
}
