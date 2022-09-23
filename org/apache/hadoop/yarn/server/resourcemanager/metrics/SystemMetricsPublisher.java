// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.metrics;

import org.apache.hadoop.yarn.event.AbstractEvent;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import java.util.List;
import org.apache.hadoop.yarn.event.Event;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.util.timeline.TimelineUtils;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvent;
import java.util.HashMap;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.RMServerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.client.api.TimelineClient;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.CompositeService;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class SystemMetricsPublisher extends CompositeService
{
    private static final Log LOG;
    private Dispatcher dispatcher;
    private TimelineClient client;
    private boolean publishSystemMetrics;
    
    public SystemMetricsPublisher() {
        super(SystemMetricsPublisher.class.getName());
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        this.publishSystemMetrics = (conf.getBoolean("yarn.timeline-service.enabled", false) && conf.getBoolean("yarn.resourcemanager.system-metrics-publisher.enabled", false));
        if (this.publishSystemMetrics) {
            this.addIfService(this.client = TimelineClient.createTimelineClient());
            (this.dispatcher = this.createDispatcher(conf)).register(SystemMetricsEventType.class, new ForwardingEventHandler());
            this.addIfService(this.dispatcher);
            SystemMetricsPublisher.LOG.info("YARN system metrics publishing service is enabled");
        }
        else {
            SystemMetricsPublisher.LOG.info("YARN system metrics publishing service is not enabled");
        }
        super.serviceInit(conf);
    }
    
    public void appCreated(final RMApp app, final long createdTime) {
        if (this.publishSystemMetrics) {
            this.dispatcher.getEventHandler().handle(new ApplicationCreatedEvent(app.getApplicationId(), app.getName(), app.getApplicationType(), app.getUser(), app.getQueue(), app.getSubmitTime(), createdTime));
        }
    }
    
    public void appFinished(final RMApp app, final RMAppState state, final long finishedTime) {
        if (this.publishSystemMetrics) {
            this.dispatcher.getEventHandler().handle(new ApplicationFinishedEvent(app.getApplicationId(), app.getDiagnostics().toString(), app.getFinalApplicationStatus(), RMServerUtils.createApplicationState(state), (app.getCurrentAppAttempt() == null) ? null : app.getCurrentAppAttempt().getAppAttemptId(), finishedTime));
        }
    }
    
    public void appACLsUpdated(final RMApp app, final String appViewACLs, final long updatedTime) {
        if (this.publishSystemMetrics) {
            this.dispatcher.getEventHandler().handle(new ApplicationACLsUpdatedEvent(app.getApplicationId(), (appViewACLs == null) ? "" : appViewACLs, updatedTime));
        }
    }
    
    public void appAttemptRegistered(final RMAppAttempt appAttempt, final long registeredTime) {
        if (this.publishSystemMetrics) {
            this.dispatcher.getEventHandler().handle(new AppAttemptRegisteredEvent(appAttempt.getAppAttemptId(), appAttempt.getHost(), appAttempt.getRpcPort(), appAttempt.getTrackingUrl(), appAttempt.getOriginalTrackingUrl(), appAttempt.getMasterContainer().getId(), registeredTime));
        }
    }
    
    public void appAttemptFinished(final RMAppAttempt appAttempt, final RMAppAttemptState appAttemtpState, final RMApp app, final long finishedTime) {
        if (this.publishSystemMetrics) {
            this.dispatcher.getEventHandler().handle(new AppAttemptFinishedEvent(appAttempt.getAppAttemptId(), appAttempt.getTrackingUrl(), appAttempt.getOriginalTrackingUrl(), appAttempt.getDiagnostics(), app.getFinalApplicationStatus(), RMServerUtils.createApplicationAttemptState(appAttemtpState), finishedTime));
        }
    }
    
    public void containerCreated(final RMContainer container, final long createdTime) {
        if (this.publishSystemMetrics) {
            this.dispatcher.getEventHandler().handle(new ContainerCreatedEvent(container.getContainerId(), container.getAllocatedResource(), container.getAllocatedNode(), container.getAllocatedPriority(), createdTime));
        }
    }
    
    public void containerFinished(final RMContainer container, final long finishedTime) {
        if (this.publishSystemMetrics) {
            this.dispatcher.getEventHandler().handle(new ContainerFinishedEvent(container.getContainerId(), container.getDiagnosticsInfo(), container.getContainerExitStatus(), container.getContainerState(), finishedTime));
        }
    }
    
    protected Dispatcher createDispatcher(final Configuration conf) {
        final MultiThreadedDispatcher dispatcher = new MultiThreadedDispatcher(conf.getInt("yarn.resourcemanager.system-metrics-publisher.dispatcher.pool-size", 10));
        dispatcher.setDrainEventsOnStop();
        return dispatcher;
    }
    
    protected void handleSystemMetricsEvent(final SystemMetricsEvent event) {
        switch (event.getType()) {
            case APP_CREATED: {
                this.publishApplicationCreatedEvent((ApplicationCreatedEvent)event);
                break;
            }
            case APP_FINISHED: {
                this.publishApplicationFinishedEvent((ApplicationFinishedEvent)event);
                break;
            }
            case APP_ACLS_UPDATED: {
                this.publishApplicationACLsUpdatedEvent((ApplicationACLsUpdatedEvent)event);
                break;
            }
            case APP_ATTEMPT_REGISTERED: {
                this.publishAppAttemptRegisteredEvent((AppAttemptRegisteredEvent)event);
                break;
            }
            case APP_ATTEMPT_FINISHED: {
                this.publishAppAttemptFinishedEvent((AppAttemptFinishedEvent)event);
                break;
            }
            case CONTAINER_CREATED: {
                this.publishContainerCreatedEvent((ContainerCreatedEvent)event);
                break;
            }
            case CONTAINER_FINISHED: {
                this.publishContainerFinishedEvent((ContainerFinishedEvent)event);
                break;
            }
            default: {
                SystemMetricsPublisher.LOG.error("Unknown SystemMetricsEvent type: " + ((AbstractEvent<Object>)event).getType());
                break;
            }
        }
    }
    
    private void publishApplicationCreatedEvent(final ApplicationCreatedEvent event) {
        final TimelineEntity entity = createApplicationEntity(event.getApplicationId());
        final Map<String, Object> entityInfo = new HashMap<String, Object>();
        entityInfo.put("YARN_APPLICATION_NAME", event.getApplicationName());
        entityInfo.put("YARN_APPLICATION_TYPE", event.getApplicationType());
        entityInfo.put("YARN_APPLICATION_USER", event.getUser());
        entityInfo.put("YARN_APPLICATION_QUEUE", event.getQueue());
        entityInfo.put("YARN_APPLICATION_SUBMITTED_TIME", event.getSubmittedTime());
        entity.setOtherInfo(entityInfo);
        final TimelineEvent tEvent = new TimelineEvent();
        tEvent.setEventType("YARN_APPLICATION_CREATED");
        tEvent.setTimestamp(event.getTimestamp());
        entity.addEvent(tEvent);
        this.putEntity(entity);
    }
    
    private void publishApplicationFinishedEvent(final ApplicationFinishedEvent event) {
        final TimelineEntity entity = createApplicationEntity(event.getApplicationId());
        final TimelineEvent tEvent = new TimelineEvent();
        tEvent.setEventType("YARN_APPLICATION_FINISHED");
        tEvent.setTimestamp(event.getTimestamp());
        final Map<String, Object> eventInfo = new HashMap<String, Object>();
        eventInfo.put("YARN_APPLICATION_DIAGNOSTICS_INFO", event.getDiagnosticsInfo());
        eventInfo.put("YARN_APPLICATION_FINAL_STATUS", event.getFinalApplicationStatus().toString());
        eventInfo.put("YARN_APPLICATION_STATE", event.getYarnApplicationState().toString());
        if (event.getLatestApplicationAttemptId() != null) {
            eventInfo.put("YARN_APPLICATION_LATEST_APP_ATTEMPT", event.getLatestApplicationAttemptId().toString());
        }
        tEvent.setEventInfo(eventInfo);
        entity.addEvent(tEvent);
        this.putEntity(entity);
    }
    
    private void publishApplicationACLsUpdatedEvent(final ApplicationACLsUpdatedEvent event) {
        final TimelineEntity entity = createApplicationEntity(event.getApplicationId());
        final TimelineEvent tEvent = new TimelineEvent();
        final Map<String, Object> entityInfo = new HashMap<String, Object>();
        entityInfo.put("YARN_APPLICATION_VIEW_ACLS", event.getViewAppACLs());
        entity.setOtherInfo(entityInfo);
        tEvent.setEventType("YARN_APPLICATION_ACLS_UPDATED");
        tEvent.setTimestamp(event.getTimestamp());
        entity.addEvent(tEvent);
        this.putEntity(entity);
    }
    
    private static TimelineEntity createApplicationEntity(final ApplicationId applicationId) {
        final TimelineEntity entity = new TimelineEntity();
        entity.setEntityType("YARN_APPLICATION");
        entity.setEntityId(applicationId.toString());
        return entity;
    }
    
    private void publishAppAttemptRegisteredEvent(final AppAttemptRegisteredEvent event) {
        final TimelineEntity entity = createAppAttemptEntity(event.getApplicationAttemptId());
        final TimelineEvent tEvent = new TimelineEvent();
        tEvent.setEventType("YARN_APPLICATION_ATTEMPT_REGISTERED");
        tEvent.setTimestamp(event.getTimestamp());
        final Map<String, Object> eventInfo = new HashMap<String, Object>();
        eventInfo.put("YARN_APPLICATION_ATTEMPT_TRACKING_URL", event.getTrackingUrl());
        eventInfo.put("YARN_APPLICATION_ATTEMPT_ORIGINAL_TRACKING_URL", event.getOriginalTrackingURL());
        eventInfo.put("YARN_APPLICATION_ATTEMPT_HOST", event.getHost());
        eventInfo.put("YARN_APPLICATION_ATTEMPT_RPC_PORT", event.getRpcPort());
        eventInfo.put("YARN_APPLICATION_ATTEMPT_MASTER_CONTAINER", event.getMasterContainerId().toString());
        tEvent.setEventInfo(eventInfo);
        entity.addEvent(tEvent);
        this.putEntity(entity);
    }
    
    private void publishAppAttemptFinishedEvent(final AppAttemptFinishedEvent event) {
        final TimelineEntity entity = createAppAttemptEntity(event.getApplicationAttemptId());
        final TimelineEvent tEvent = new TimelineEvent();
        tEvent.setEventType("YARN_APPLICATION_ATTEMPT_FINISHED");
        tEvent.setTimestamp(event.getTimestamp());
        final Map<String, Object> eventInfo = new HashMap<String, Object>();
        eventInfo.put("YARN_APPLICATION_ATTEMPT_TRACKING_URL", event.getTrackingUrl());
        eventInfo.put("YARN_APPLICATION_ATTEMPT_ORIGINAL_TRACKING_URL", event.getOriginalTrackingURL());
        eventInfo.put("YARN_APPLICATION_ATTEMPT_DIAGNOSTICS_INFO", event.getDiagnosticsInfo());
        eventInfo.put("YARN_APPLICATION_ATTEMPT_FINAL_STATUS", event.getFinalApplicationStatus().toString());
        eventInfo.put("YARN_APPLICATION_ATTEMPT_STATE", event.getYarnApplicationAttemptState().toString());
        tEvent.setEventInfo(eventInfo);
        entity.addEvent(tEvent);
        this.putEntity(entity);
    }
    
    private static TimelineEntity createAppAttemptEntity(final ApplicationAttemptId appAttemptId) {
        final TimelineEntity entity = new TimelineEntity();
        entity.setEntityType("YARN_APPLICATION_ATTEMPT");
        entity.setEntityId(appAttemptId.toString());
        entity.addPrimaryFilter("YARN_APPLICATION_ATTEMPT_PARENT", appAttemptId.getApplicationId().toString());
        return entity;
    }
    
    private void publishContainerCreatedEvent(final ContainerCreatedEvent event) {
        final TimelineEntity entity = createContainerEntity(event.getContainerId());
        final Map<String, Object> entityInfo = new HashMap<String, Object>();
        entityInfo.put("YARN_CONTAINER_ALLOCATED_MEMORY", event.getAllocatedResource().getMemory());
        entityInfo.put("YARN_CONTAINER_ALLOCATED_VCORE", event.getAllocatedResource().getVirtualCores());
        entityInfo.put("YARN_CONTAINER_ALLOCATED_HOST", event.getAllocatedNode().getHost());
        entityInfo.put("YARN_CONTAINER_ALLOCATED_PORT", event.getAllocatedNode().getPort());
        entityInfo.put("YARN_CONTAINER_ALLOCATED_PRIORITY", event.getAllocatedPriority().getPriority());
        entity.setOtherInfo(entityInfo);
        final TimelineEvent tEvent = new TimelineEvent();
        tEvent.setEventType("YARN_CONTAINER_CREATED");
        tEvent.setTimestamp(event.getTimestamp());
        entity.addEvent(tEvent);
        this.putEntity(entity);
    }
    
    private void publishContainerFinishedEvent(final ContainerFinishedEvent event) {
        final TimelineEntity entity = createContainerEntity(event.getContainerId());
        final TimelineEvent tEvent = new TimelineEvent();
        tEvent.setEventType("YARN_CONTAINER_FINISHED");
        tEvent.setTimestamp(event.getTimestamp());
        final Map<String, Object> eventInfo = new HashMap<String, Object>();
        eventInfo.put("YARN_CONTAINER_DIAGNOSTICS_INFO", event.getDiagnosticsInfo());
        eventInfo.put("YARN_CONTAINER_EXIT_STATUS", event.getContainerExitStatus());
        eventInfo.put("YARN_CONTAINER_STATE", event.getContainerState().toString());
        tEvent.setEventInfo(eventInfo);
        entity.addEvent(tEvent);
        this.putEntity(entity);
    }
    
    private static TimelineEntity createContainerEntity(final ContainerId containerId) {
        final TimelineEntity entity = new TimelineEntity();
        entity.setEntityType("YARN_CONTAINER");
        entity.setEntityId(containerId.toString());
        entity.addPrimaryFilter("YARN_CONTAINER_PARENT", containerId.getApplicationAttemptId().toString());
        return entity;
    }
    
    private void putEntity(final TimelineEntity entity) {
        try {
            if (SystemMetricsPublisher.LOG.isDebugEnabled()) {
                SystemMetricsPublisher.LOG.debug("Publishing the entity " + entity.getEntityId() + ", JSON-style content: " + TimelineUtils.dumpTimelineRecordtoJSON(entity));
            }
            this.client.putEntities(entity);
        }
        catch (Exception e) {
            SystemMetricsPublisher.LOG.error("Error when publishing entity [" + entity.getEntityType() + "," + entity.getEntityId() + "]", e);
        }
    }
    
    static {
        LOG = LogFactory.getLog(SystemMetricsPublisher.class);
    }
    
    private final class ForwardingEventHandler implements EventHandler<SystemMetricsEvent>
    {
        @Override
        public void handle(final SystemMetricsEvent event) {
            SystemMetricsPublisher.this.handleSystemMetricsEvent(event);
        }
    }
    
    protected static class MultiThreadedDispatcher extends CompositeService implements Dispatcher
    {
        private List<AsyncDispatcher> dispatchers;
        
        public MultiThreadedDispatcher(final int num) {
            super(MultiThreadedDispatcher.class.getName());
            this.dispatchers = new ArrayList<AsyncDispatcher>();
            for (int i = 0; i < num; ++i) {
                final AsyncDispatcher dispatcher = this.createDispatcher();
                this.dispatchers.add(dispatcher);
                this.addIfService(dispatcher);
            }
        }
        
        @Override
        public EventHandler getEventHandler() {
            return new CompositEventHandler();
        }
        
        @Override
        public void register(final Class<? extends Enum> eventType, final EventHandler handler) {
            for (final AsyncDispatcher dispatcher : this.dispatchers) {
                dispatcher.register(eventType, handler);
            }
        }
        
        public void setDrainEventsOnStop() {
            for (final AsyncDispatcher dispatcher : this.dispatchers) {
                dispatcher.setDrainEventsOnStop();
            }
        }
        
        protected AsyncDispatcher createDispatcher() {
            return new AsyncDispatcher();
        }
        
        private class CompositEventHandler implements EventHandler<Event>
        {
            @Override
            public void handle(final Event event) {
                final int index = (event.hashCode() & Integer.MAX_VALUE) % MultiThreadedDispatcher.this.dispatchers.size();
                MultiThreadedDispatcher.this.dispatchers.get(index).getEventHandler().handle(event);
            }
        }
    }
}
