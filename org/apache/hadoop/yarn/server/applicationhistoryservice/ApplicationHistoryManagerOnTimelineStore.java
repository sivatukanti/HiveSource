// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice;

import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import java.util.List;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvent;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.exceptions.ContainerNotFoundException;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.exceptions.ApplicationAttemptNotFoundException;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import java.util.HashMap;
import java.util.Collection;
import org.apache.hadoop.yarn.server.timeline.NameValuePair;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.EnumSet;
import org.apache.hadoop.yarn.server.timeline.TimelineReader;
import java.util.Map;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.hadoop.yarn.server.timeline.TimelineDataManager;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.service.AbstractService;

public class ApplicationHistoryManagerOnTimelineStore extends AbstractService implements ApplicationHistoryManager
{
    @VisibleForTesting
    static final String UNAVAILABLE = "N/A";
    private TimelineDataManager timelineDataManager;
    private ApplicationACLsManager aclsManager;
    private String serverHttpAddress;
    
    public ApplicationHistoryManagerOnTimelineStore(final TimelineDataManager timelineDataManager, final ApplicationACLsManager aclsManager) {
        super(ApplicationHistoryManagerOnTimelineStore.class.getName());
        this.timelineDataManager = timelineDataManager;
        this.aclsManager = aclsManager;
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        this.serverHttpAddress = WebAppUtils.getHttpSchemePrefix(conf) + WebAppUtils.getAHSWebAppURLWithoutScheme(conf);
        super.serviceInit(conf);
    }
    
    @Override
    public ApplicationReport getApplication(final ApplicationId appId) throws YarnException, IOException {
        return this.getApplication(appId, ApplicationReportField.ALL).appReport;
    }
    
    @Override
    public Map<ApplicationId, ApplicationReport> getAllApplications() throws YarnException, IOException {
        final TimelineEntities entities = this.timelineDataManager.getEntities("YARN_APPLICATION", null, null, null, null, null, null, Long.MAX_VALUE, EnumSet.allOf(TimelineReader.Field.class), UserGroupInformation.getLoginUser());
        final Map<ApplicationId, ApplicationReport> apps = new HashMap<ApplicationId, ApplicationReport>();
        if (entities != null && entities.getEntities() != null) {
            for (final TimelineEntity entity : entities.getEntities()) {
                final ApplicationReportExt app = this.generateApplicationReport(entity, ApplicationReportField.ALL);
                apps.put(app.appReport.getApplicationId(), app.appReport);
            }
        }
        return apps;
    }
    
    @Override
    public Map<ApplicationAttemptId, ApplicationAttemptReport> getApplicationAttempts(final ApplicationId appId) throws YarnException, IOException {
        final ApplicationReportExt app = this.getApplication(appId, ApplicationReportField.USER_AND_ACLS);
        this.checkAccess(app);
        final TimelineEntities entities = this.timelineDataManager.getEntities("YARN_APPLICATION_ATTEMPT", new NameValuePair("YARN_APPLICATION_ATTEMPT_PARENT", appId.toString()), null, null, null, null, null, Long.MAX_VALUE, EnumSet.allOf(TimelineReader.Field.class), UserGroupInformation.getLoginUser());
        final Map<ApplicationAttemptId, ApplicationAttemptReport> appAttempts = new HashMap<ApplicationAttemptId, ApplicationAttemptReport>();
        for (final TimelineEntity entity : entities.getEntities()) {
            final ApplicationAttemptReport appAttempt = convertToApplicationAttemptReport(entity);
            appAttempts.put(appAttempt.getApplicationAttemptId(), appAttempt);
        }
        return appAttempts;
    }
    
    @Override
    public ApplicationAttemptReport getApplicationAttempt(final ApplicationAttemptId appAttemptId) throws YarnException, IOException {
        final ApplicationReportExt app = this.getApplication(appAttemptId.getApplicationId(), ApplicationReportField.USER_AND_ACLS);
        this.checkAccess(app);
        final TimelineEntity entity = this.timelineDataManager.getEntity("YARN_APPLICATION_ATTEMPT", appAttemptId.toString(), EnumSet.allOf(TimelineReader.Field.class), UserGroupInformation.getLoginUser());
        if (entity == null) {
            throw new ApplicationAttemptNotFoundException("The entity for application attempt " + appAttemptId + " doesn't exist in the timeline store");
        }
        return convertToApplicationAttemptReport(entity);
    }
    
    @Override
    public ContainerReport getContainer(final ContainerId containerId) throws YarnException, IOException {
        final ApplicationReportExt app = this.getApplication(containerId.getApplicationAttemptId().getApplicationId(), ApplicationReportField.USER_AND_ACLS);
        this.checkAccess(app);
        final TimelineEntity entity = this.timelineDataManager.getEntity("YARN_CONTAINER", containerId.toString(), EnumSet.allOf(TimelineReader.Field.class), UserGroupInformation.getLoginUser());
        if (entity == null) {
            throw new ContainerNotFoundException("The entity for container " + containerId + " doesn't exist in the timeline store");
        }
        return convertToContainerReport(entity, this.serverHttpAddress, app.appReport.getUser());
    }
    
    @Override
    public ContainerReport getAMContainer(final ApplicationAttemptId appAttemptId) throws YarnException, IOException {
        final ApplicationAttemptReport appAttempt = this.getApplicationAttempt(appAttemptId);
        return this.getContainer(appAttempt.getAMContainerId());
    }
    
    @Override
    public Map<ContainerId, ContainerReport> getContainers(final ApplicationAttemptId appAttemptId) throws YarnException, IOException {
        final ApplicationReportExt app = this.getApplication(appAttemptId.getApplicationId(), ApplicationReportField.USER_AND_ACLS);
        this.checkAccess(app);
        final TimelineEntities entities = this.timelineDataManager.getEntities("YARN_CONTAINER", new NameValuePair("YARN_CONTAINER_PARENT", appAttemptId.toString()), null, null, null, null, null, Long.MAX_VALUE, EnumSet.allOf(TimelineReader.Field.class), UserGroupInformation.getLoginUser());
        final Map<ContainerId, ContainerReport> containers = new HashMap<ContainerId, ContainerReport>();
        if (entities != null && entities.getEntities() != null) {
            for (final TimelineEntity entity : entities.getEntities()) {
                final ContainerReport container = convertToContainerReport(entity, this.serverHttpAddress, app.appReport.getUser());
                containers.put(container.getContainerId(), container);
            }
        }
        return containers;
    }
    
    private static ApplicationReportExt convertToApplicationReport(final TimelineEntity entity, final ApplicationReportField field) {
        String user = null;
        String queue = null;
        String name = null;
        String type = null;
        long createdTime = 0L;
        long finishedTime = 0L;
        ApplicationAttemptId latestApplicationAttemptId = null;
        String diagnosticsInfo = null;
        FinalApplicationStatus finalStatus = FinalApplicationStatus.UNDEFINED;
        YarnApplicationState state = null;
        final Map<ApplicationAccessType, String> appViewACLs = new HashMap<ApplicationAccessType, String>();
        final Map<String, Object> entityInfo = entity.getOtherInfo();
        if (entityInfo != null) {
            if (entityInfo.containsKey("YARN_APPLICATION_USER")) {
                user = entityInfo.get("YARN_APPLICATION_USER").toString();
            }
            if (entityInfo.containsKey("YARN_APPLICATION_VIEW_ACLS")) {
                final String appViewACLsStr = entityInfo.get("YARN_APPLICATION_VIEW_ACLS").toString();
                if (appViewACLsStr.length() > 0) {
                    appViewACLs.put(ApplicationAccessType.VIEW_APP, appViewACLsStr);
                }
            }
            if (field == ApplicationReportField.USER_AND_ACLS) {
                return new ApplicationReportExt(ApplicationReport.newInstance(ConverterUtils.toApplicationId(entity.getEntityId()), latestApplicationAttemptId, user, queue, name, null, -1, null, state, diagnosticsInfo, null, createdTime, finishedTime, finalStatus, null, null, 1.0f, type, null), appViewACLs);
            }
            if (entityInfo.containsKey("YARN_APPLICATION_QUEUE")) {
                queue = entityInfo.get("YARN_APPLICATION_QUEUE").toString();
            }
            if (entityInfo.containsKey("YARN_APPLICATION_NAME")) {
                name = entityInfo.get("YARN_APPLICATION_NAME").toString();
            }
            if (entityInfo.containsKey("YARN_APPLICATION_TYPE")) {
                type = entityInfo.get("YARN_APPLICATION_TYPE").toString();
            }
        }
        final List<TimelineEvent> events = entity.getEvents();
        if (events != null) {
            for (final TimelineEvent event : events) {
                if (event.getEventType().equals("YARN_APPLICATION_CREATED")) {
                    createdTime = event.getTimestamp();
                }
                else {
                    if (!event.getEventType().equals("YARN_APPLICATION_FINISHED")) {
                        continue;
                    }
                    finishedTime = event.getTimestamp();
                    final Map<String, Object> eventInfo = event.getEventInfo();
                    if (eventInfo == null) {
                        continue;
                    }
                    if (eventInfo.containsKey("YARN_APPLICATION_LATEST_APP_ATTEMPT")) {
                        latestApplicationAttemptId = ConverterUtils.toApplicationAttemptId(eventInfo.get("YARN_APPLICATION_LATEST_APP_ATTEMPT").toString());
                    }
                    if (eventInfo.containsKey("YARN_APPLICATION_DIAGNOSTICS_INFO")) {
                        diagnosticsInfo = eventInfo.get("YARN_APPLICATION_DIAGNOSTICS_INFO").toString();
                    }
                    if (eventInfo.containsKey("YARN_APPLICATION_FINAL_STATUS")) {
                        finalStatus = FinalApplicationStatus.valueOf(eventInfo.get("YARN_APPLICATION_FINAL_STATUS").toString());
                    }
                    if (!eventInfo.containsKey("YARN_APPLICATION_STATE")) {
                        continue;
                    }
                    state = YarnApplicationState.valueOf(eventInfo.get("YARN_APPLICATION_STATE").toString());
                }
            }
        }
        return new ApplicationReportExt(ApplicationReport.newInstance(ConverterUtils.toApplicationId(entity.getEntityId()), latestApplicationAttemptId, user, queue, name, null, -1, null, state, diagnosticsInfo, null, createdTime, finishedTime, finalStatus, null, null, 1.0f, type, null), appViewACLs);
    }
    
    private static ApplicationAttemptReport convertToApplicationAttemptReport(final TimelineEntity entity) {
        String host = null;
        int rpcPort = -1;
        ContainerId amContainerId = null;
        String trackingUrl = null;
        String originalTrackingUrl = null;
        String diagnosticsInfo = null;
        YarnApplicationAttemptState state = null;
        final List<TimelineEvent> events = entity.getEvents();
        if (events != null) {
            for (final TimelineEvent event : events) {
                if (event.getEventType().equals("YARN_APPLICATION_ATTEMPT_REGISTERED")) {
                    final Map<String, Object> eventInfo = event.getEventInfo();
                    if (eventInfo == null) {
                        continue;
                    }
                    if (eventInfo.containsKey("YARN_APPLICATION_ATTEMPT_HOST")) {
                        host = eventInfo.get("YARN_APPLICATION_ATTEMPT_HOST").toString();
                    }
                    if (eventInfo.containsKey("YARN_APPLICATION_ATTEMPT_RPC_PORT")) {
                        rpcPort = eventInfo.get("YARN_APPLICATION_ATTEMPT_RPC_PORT");
                    }
                    if (!eventInfo.containsKey("YARN_APPLICATION_ATTEMPT_MASTER_CONTAINER")) {
                        continue;
                    }
                    amContainerId = ConverterUtils.toContainerId(eventInfo.get("YARN_APPLICATION_ATTEMPT_MASTER_CONTAINER").toString());
                }
                else {
                    if (!event.getEventType().equals("YARN_APPLICATION_ATTEMPT_FINISHED")) {
                        continue;
                    }
                    final Map<String, Object> eventInfo = event.getEventInfo();
                    if (eventInfo == null) {
                        continue;
                    }
                    if (eventInfo.containsKey("YARN_APPLICATION_ATTEMPT_TRACKING_URL")) {
                        trackingUrl = eventInfo.get("YARN_APPLICATION_ATTEMPT_TRACKING_URL").toString();
                    }
                    if (eventInfo.containsKey("YARN_APPLICATION_ATTEMPT_ORIGINAL_TRACKING_URL")) {
                        originalTrackingUrl = eventInfo.get("YARN_APPLICATION_ATTEMPT_ORIGINAL_TRACKING_URL").toString();
                    }
                    if (eventInfo.containsKey("YARN_APPLICATION_ATTEMPT_DIAGNOSTICS_INFO")) {
                        diagnosticsInfo = eventInfo.get("YARN_APPLICATION_ATTEMPT_DIAGNOSTICS_INFO").toString();
                    }
                    if (!eventInfo.containsKey("YARN_APPLICATION_ATTEMPT_STATE")) {
                        continue;
                    }
                    state = YarnApplicationAttemptState.valueOf(eventInfo.get("YARN_APPLICATION_ATTEMPT_STATE").toString());
                }
            }
        }
        return ApplicationAttemptReport.newInstance(ConverterUtils.toApplicationAttemptId(entity.getEntityId()), host, rpcPort, trackingUrl, originalTrackingUrl, diagnosticsInfo, state, amContainerId);
    }
    
    private static ContainerReport convertToContainerReport(final TimelineEntity entity, final String serverHttpAddress, final String user) {
        int allocatedMem = 0;
        int allocatedVcore = 0;
        String allocatedHost = null;
        int allocatedPort = -1;
        int allocatedPriority = 0;
        long createdTime = 0L;
        long finishedTime = 0L;
        String diagnosticsInfo = null;
        int exitStatus = -1000;
        ContainerState state = null;
        final Map<String, Object> entityInfo = entity.getOtherInfo();
        if (entityInfo != null) {
            if (entityInfo.containsKey("YARN_CONTAINER_ALLOCATED_MEMORY")) {
                allocatedMem = entityInfo.get("YARN_CONTAINER_ALLOCATED_MEMORY");
            }
            if (entityInfo.containsKey("YARN_CONTAINER_ALLOCATED_VCORE")) {
                allocatedVcore = entityInfo.get("YARN_CONTAINER_ALLOCATED_VCORE");
            }
            if (entityInfo.containsKey("YARN_CONTAINER_ALLOCATED_HOST")) {
                allocatedHost = entityInfo.get("YARN_CONTAINER_ALLOCATED_HOST").toString();
            }
            if (entityInfo.containsKey("YARN_CONTAINER_ALLOCATED_PORT")) {
                allocatedPort = entityInfo.get("YARN_CONTAINER_ALLOCATED_PORT");
            }
            if (entityInfo.containsKey("YARN_CONTAINER_ALLOCATED_PRIORITY")) {
                allocatedPriority = entityInfo.get("YARN_CONTAINER_ALLOCATED_PRIORITY");
            }
        }
        final List<TimelineEvent> events = entity.getEvents();
        if (events != null) {
            for (final TimelineEvent event : events) {
                if (event.getEventType().equals("YARN_CONTAINER_CREATED")) {
                    createdTime = event.getTimestamp();
                }
                else {
                    if (!event.getEventType().equals("YARN_CONTAINER_FINISHED")) {
                        continue;
                    }
                    finishedTime = event.getTimestamp();
                    final Map<String, Object> eventInfo = event.getEventInfo();
                    if (eventInfo == null) {
                        continue;
                    }
                    if (eventInfo.containsKey("YARN_CONTAINER_DIAGNOSTICS_INFO")) {
                        diagnosticsInfo = eventInfo.get("YARN_CONTAINER_DIAGNOSTICS_INFO").toString();
                    }
                    if (eventInfo.containsKey("YARN_CONTAINER_EXIT_STATUS")) {
                        exitStatus = eventInfo.get("YARN_CONTAINER_EXIT_STATUS");
                    }
                    if (!eventInfo.containsKey("YARN_CONTAINER_STATE")) {
                        continue;
                    }
                    state = ContainerState.valueOf(eventInfo.get("YARN_CONTAINER_STATE").toString());
                }
            }
        }
        final NodeId allocatedNode = NodeId.newInstance(allocatedHost, allocatedPort);
        final ContainerId containerId = ConverterUtils.toContainerId(entity.getEntityId());
        final String logUrl = WebAppUtils.getAggregatedLogURL(serverHttpAddress, allocatedNode.toString(), containerId.toString(), containerId.toString(), user);
        return ContainerReport.newInstance(ConverterUtils.toContainerId(entity.getEntityId()), Resource.newInstance(allocatedMem, allocatedVcore), NodeId.newInstance(allocatedHost, allocatedPort), Priority.newInstance(allocatedPriority), createdTime, finishedTime, diagnosticsInfo, logUrl, exitStatus, state);
    }
    
    private ApplicationReportExt generateApplicationReport(final TimelineEntity entity, final ApplicationReportField field) throws YarnException, IOException {
        final ApplicationReportExt app = convertToApplicationReport(entity, field);
        if (field == ApplicationReportField.USER_AND_ACLS) {
            return app;
        }
        try {
            this.checkAccess(app);
            if (app.appReport.getCurrentApplicationAttemptId() != null) {
                final ApplicationAttemptReport appAttempt = this.getApplicationAttempt(app.appReport.getCurrentApplicationAttemptId());
                if (appAttempt != null) {
                    app.appReport.setHost(appAttempt.getHost());
                    app.appReport.setRpcPort(appAttempt.getRpcPort());
                    app.appReport.setTrackingUrl(appAttempt.getTrackingUrl());
                    app.appReport.setOriginalTrackingUrl(appAttempt.getOriginalTrackingUrl());
                }
            }
        }
        catch (AuthorizationException e) {
            app.appReport.setDiagnostics(null);
            app.appReport.setCurrentApplicationAttemptId(null);
        }
        if (app.appReport.getCurrentApplicationAttemptId() == null) {
            app.appReport.setCurrentApplicationAttemptId(ApplicationAttemptId.newInstance(app.appReport.getApplicationId(), -1));
        }
        if (app.appReport.getHost() == null) {
            app.appReport.setHost("N/A");
        }
        if (app.appReport.getRpcPort() < 0) {
            app.appReport.setRpcPort(-1);
        }
        if (app.appReport.getTrackingUrl() == null) {
            app.appReport.setTrackingUrl("N/A");
        }
        if (app.appReport.getOriginalTrackingUrl() == null) {
            app.appReport.setOriginalTrackingUrl("N/A");
        }
        if (app.appReport.getDiagnostics() == null) {
            app.appReport.setDiagnostics("");
        }
        return app;
    }
    
    private ApplicationReportExt getApplication(final ApplicationId appId, final ApplicationReportField field) throws YarnException, IOException {
        final TimelineEntity entity = this.timelineDataManager.getEntity("YARN_APPLICATION", appId.toString(), EnumSet.allOf(TimelineReader.Field.class), UserGroupInformation.getLoginUser());
        if (entity == null) {
            throw new ApplicationNotFoundException("The entity for application " + appId + " doesn't exist in the timeline store");
        }
        return this.generateApplicationReport(entity, field);
    }
    
    private void checkAccess(final ApplicationReportExt app) throws YarnException, IOException {
        if (app.appViewACLs != null) {
            this.aclsManager.addApplication(app.appReport.getApplicationId(), app.appViewACLs);
            try {
                if (!this.aclsManager.checkAccess(UserGroupInformation.getCurrentUser(), ApplicationAccessType.VIEW_APP, app.appReport.getUser(), app.appReport.getApplicationId())) {
                    throw new AuthorizationException("User " + UserGroupInformation.getCurrentUser().getShortUserName() + " does not have privilage to see this application " + app.appReport.getApplicationId());
                }
            }
            finally {
                this.aclsManager.removeApplication(app.appReport.getApplicationId());
            }
        }
    }
    
    private enum ApplicationReportField
    {
        ALL, 
        USER_AND_ACLS;
    }
    
    private static class ApplicationReportExt
    {
        private ApplicationReport appReport;
        private Map<ApplicationAccessType, String> appViewACLs;
        
        public ApplicationReportExt(final ApplicationReport appReport, final Map<ApplicationAccessType, String> appViewACLs) {
            this.appReport = appReport;
            this.appViewACLs = appViewACLs;
        }
    }
}
