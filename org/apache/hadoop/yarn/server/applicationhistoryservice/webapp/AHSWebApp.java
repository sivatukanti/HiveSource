// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.webapp;

import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.Controller;
import org.apache.hadoop.yarn.server.api.ApplicationContext;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.server.timeline.webapp.TimelineWebServices;
import org.apache.hadoop.yarn.webapp.YarnJacksonJaxbJsonProvider;
import org.apache.hadoop.yarn.server.timeline.TimelineDataManager;
import org.apache.hadoop.yarn.server.applicationhistoryservice.ApplicationHistoryManager;
import org.apache.hadoop.yarn.webapp.YarnWebParams;
import org.apache.hadoop.yarn.webapp.WebApp;

public class AHSWebApp extends WebApp implements YarnWebParams
{
    private ApplicationHistoryManager applicationHistoryManager;
    private TimelineDataManager timelineDataManager;
    
    public AHSWebApp(final TimelineDataManager timelineDataManager, final ApplicationHistoryManager applicationHistoryManager) {
        this.timelineDataManager = timelineDataManager;
        this.applicationHistoryManager = applicationHistoryManager;
    }
    
    public ApplicationHistoryManager getApplicationHistoryManager() {
        return this.applicationHistoryManager;
    }
    
    public TimelineDataManager getTimelineDataManager() {
        return this.timelineDataManager;
    }
    
    @Override
    public void setup() {
        this.bind(YarnJacksonJaxbJsonProvider.class);
        this.bind(AHSWebServices.class);
        this.bind(TimelineWebServices.class);
        this.bind(GenericExceptionHandler.class);
        this.bind(ApplicationContext.class).toInstance(this.applicationHistoryManager);
        this.bind(TimelineDataManager.class).toInstance(this.timelineDataManager);
        this.route("/", AHSController.class);
        this.route(StringHelper.pajoin("/apps", "app.state"), AHSController.class);
        this.route(StringHelper.pajoin("/app", "app.id"), AHSController.class, "app");
        this.route(StringHelper.pajoin("/appattempt", "appattempt.id"), AHSController.class, "appattempt");
        this.route(StringHelper.pajoin("/container", "container.id"), AHSController.class, "container");
        this.route(StringHelper.pajoin("/logs", "nm.id", "container.id", "entity.string", "app.owner", "log.type"), AHSController.class, "logs");
    }
}
