// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.ahs;

import org.apache.hadoop.yarn.event.AbstractEvent;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import java.util.List;
import org.apache.hadoop.yarn.event.Event;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerFinishData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerStartData;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptFinishData;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptStartData;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationFinishData;
import org.apache.hadoop.yarn.server.resourcemanager.RMServerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationStartData;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.server.applicationhistoryservice.ApplicationHistoryStore;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.applicationhistoryservice.NullApplicationHistoryStore;
import org.apache.hadoop.conf.Configuration;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.server.applicationhistoryservice.ApplicationHistoryWriter;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.CompositeService;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RMApplicationHistoryWriter extends CompositeService
{
    public static final Log LOG;
    private Dispatcher dispatcher;
    @VisibleForTesting
    ApplicationHistoryWriter writer;
    @VisibleForTesting
    boolean historyServiceEnabled;
    
    public RMApplicationHistoryWriter() {
        super(RMApplicationHistoryWriter.class.getName());
    }
    
    @Override
    protected synchronized void serviceInit(final Configuration conf) throws Exception {
        this.historyServiceEnabled = conf.getBoolean("yarn.timeline-service.generic-application-history.enabled", false);
        if (conf.get("yarn.timeline-service.generic-application-history.store-class") == null || conf.get("yarn.timeline-service.generic-application-history.store-class").length() == 0 || conf.get("yarn.timeline-service.generic-application-history.store-class").equals(NullApplicationHistoryStore.class.getName())) {
            this.historyServiceEnabled = false;
        }
        if (this.historyServiceEnabled) {
            this.addIfService(this.writer = this.createApplicationHistoryStore(conf));
            (this.dispatcher = this.createDispatcher(conf)).register(WritingHistoryEventType.class, new ForwardingEventHandler());
            this.addIfService(this.dispatcher);
        }
        super.serviceInit(conf);
    }
    
    protected Dispatcher createDispatcher(final Configuration conf) {
        final MultiThreadedDispatcher dispatcher = new MultiThreadedDispatcher(conf.getInt("yarn.resourcemanager.history-writer.multi-threaded-dispatcher.pool-size", 10));
        dispatcher.setDrainEventsOnStop();
        return dispatcher;
    }
    
    protected ApplicationHistoryStore createApplicationHistoryStore(final Configuration conf) {
        try {
            final Class<? extends ApplicationHistoryStore> storeClass = conf.getClass("yarn.timeline-service.generic-application-history.store-class", NullApplicationHistoryStore.class, ApplicationHistoryStore.class);
            return (ApplicationHistoryStore)storeClass.newInstance();
        }
        catch (Exception e) {
            final String msg = "Could not instantiate ApplicationHistoryWriter: " + conf.get("yarn.timeline-service.generic-application-history.store-class", NullApplicationHistoryStore.class.getName());
            RMApplicationHistoryWriter.LOG.error(msg, e);
            throw new YarnRuntimeException(msg, e);
        }
    }
    
    protected void handleWritingApplicationHistoryEvent(final WritingApplicationHistoryEvent event) {
        switch (event.getType()) {
            case APP_START: {
                final WritingApplicationStartEvent wasEvent = (WritingApplicationStartEvent)event;
                try {
                    this.writer.applicationStarted(wasEvent.getApplicationStartData());
                    RMApplicationHistoryWriter.LOG.info("Stored the start data of application " + wasEvent.getApplicationId());
                }
                catch (IOException e) {
                    RMApplicationHistoryWriter.LOG.error("Error when storing the start data of application " + wasEvent.getApplicationId());
                }
                break;
            }
            case APP_FINISH: {
                final WritingApplicationFinishEvent wafEvent = (WritingApplicationFinishEvent)event;
                try {
                    this.writer.applicationFinished(wafEvent.getApplicationFinishData());
                    RMApplicationHistoryWriter.LOG.info("Stored the finish data of application " + wafEvent.getApplicationId());
                }
                catch (IOException e2) {
                    RMApplicationHistoryWriter.LOG.error("Error when storing the finish data of application " + wafEvent.getApplicationId());
                }
                break;
            }
            case APP_ATTEMPT_START: {
                final WritingApplicationAttemptStartEvent waasEvent = (WritingApplicationAttemptStartEvent)event;
                try {
                    this.writer.applicationAttemptStarted(waasEvent.getApplicationAttemptStartData());
                    RMApplicationHistoryWriter.LOG.info("Stored the start data of application attempt " + waasEvent.getApplicationAttemptId());
                }
                catch (IOException e3) {
                    RMApplicationHistoryWriter.LOG.error("Error when storing the start data of application attempt " + waasEvent.getApplicationAttemptId());
                }
                break;
            }
            case APP_ATTEMPT_FINISH: {
                final WritingApplicationAttemptFinishEvent waafEvent = (WritingApplicationAttemptFinishEvent)event;
                try {
                    this.writer.applicationAttemptFinished(waafEvent.getApplicationAttemptFinishData());
                    RMApplicationHistoryWriter.LOG.info("Stored the finish data of application attempt " + waafEvent.getApplicationAttemptId());
                }
                catch (IOException e4) {
                    RMApplicationHistoryWriter.LOG.error("Error when storing the finish data of application attempt " + waafEvent.getApplicationAttemptId());
                }
                break;
            }
            case CONTAINER_START: {
                final WritingContainerStartEvent wcsEvent = (WritingContainerStartEvent)event;
                try {
                    this.writer.containerStarted(wcsEvent.getContainerStartData());
                    RMApplicationHistoryWriter.LOG.info("Stored the start data of container " + wcsEvent.getContainerId());
                }
                catch (IOException e5) {
                    RMApplicationHistoryWriter.LOG.error("Error when storing the start data of container " + wcsEvent.getContainerId());
                }
                break;
            }
            case CONTAINER_FINISH: {
                final WritingContainerFinishEvent wcfEvent = (WritingContainerFinishEvent)event;
                try {
                    this.writer.containerFinished(wcfEvent.getContainerFinishData());
                    RMApplicationHistoryWriter.LOG.info("Stored the finish data of container " + wcfEvent.getContainerId());
                }
                catch (IOException e6) {
                    RMApplicationHistoryWriter.LOG.error("Error when storing the finish data of container " + wcfEvent.getContainerId());
                }
                break;
            }
            default: {
                RMApplicationHistoryWriter.LOG.error("Unknown WritingApplicationHistoryEvent type: " + ((AbstractEvent<Object>)event).getType());
                break;
            }
        }
    }
    
    public void applicationStarted(final RMApp app) {
        if (this.historyServiceEnabled) {
            this.dispatcher.getEventHandler().handle(new WritingApplicationStartEvent(app.getApplicationId(), ApplicationStartData.newInstance(app.getApplicationId(), app.getName(), app.getApplicationType(), app.getQueue(), app.getUser(), app.getSubmitTime(), app.getStartTime())));
        }
    }
    
    public void applicationFinished(final RMApp app, final RMAppState finalState) {
        if (this.historyServiceEnabled) {
            this.dispatcher.getEventHandler().handle(new WritingApplicationFinishEvent(app.getApplicationId(), ApplicationFinishData.newInstance(app.getApplicationId(), app.getFinishTime(), app.getDiagnostics().toString(), app.getFinalApplicationStatus(), RMServerUtils.createApplicationState(finalState))));
        }
    }
    
    public void applicationAttemptStarted(final RMAppAttempt appAttempt) {
        if (this.historyServiceEnabled) {
            this.dispatcher.getEventHandler().handle(new WritingApplicationAttemptStartEvent(appAttempt.getAppAttemptId(), ApplicationAttemptStartData.newInstance(appAttempt.getAppAttemptId(), appAttempt.getHost(), appAttempt.getRpcPort(), appAttempt.getMasterContainer().getId())));
        }
    }
    
    public void applicationAttemptFinished(final RMAppAttempt appAttempt, final RMAppAttemptState finalState) {
        if (this.historyServiceEnabled) {
            this.dispatcher.getEventHandler().handle(new WritingApplicationAttemptFinishEvent(appAttempt.getAppAttemptId(), ApplicationAttemptFinishData.newInstance(appAttempt.getAppAttemptId(), appAttempt.getDiagnostics().toString(), appAttempt.getTrackingUrl(), appAttempt.getFinalApplicationStatus(), RMServerUtils.createApplicationAttemptState(finalState))));
        }
    }
    
    public void containerStarted(final RMContainer container) {
        if (this.historyServiceEnabled) {
            this.dispatcher.getEventHandler().handle(new WritingContainerStartEvent(container.getContainerId(), ContainerStartData.newInstance(container.getContainerId(), container.getAllocatedResource(), container.getAllocatedNode(), container.getAllocatedPriority(), container.getCreationTime())));
        }
    }
    
    public void containerFinished(final RMContainer container) {
        if (this.historyServiceEnabled) {
            this.dispatcher.getEventHandler().handle(new WritingContainerFinishEvent(container.getContainerId(), ContainerFinishData.newInstance(container.getContainerId(), container.getFinishTime(), container.getDiagnosticsInfo(), container.getContainerExitStatus(), container.getContainerState())));
        }
    }
    
    static {
        LOG = LogFactory.getLog(RMApplicationHistoryWriter.class);
    }
    
    private final class ForwardingEventHandler implements EventHandler<WritingApplicationHistoryEvent>
    {
        @Override
        public void handle(final WritingApplicationHistoryEvent event) {
            RMApplicationHistoryWriter.this.handleWritingApplicationHistoryEvent(event);
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
