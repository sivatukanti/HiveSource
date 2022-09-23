// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.webapp.View;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.Controller;

public class RmController extends Controller
{
    @Inject
    RmController(final RequestContext ctx) {
        super(ctx);
    }
    
    @Override
    public void index() {
        this.setTitle("Applications");
    }
    
    public void about() {
        this.setTitle("About the Cluster");
        this.render(AboutPage.class);
    }
    
    public void app() {
        this.render(AppPage.class);
    }
    
    public void nodes() {
        this.render(NodesPage.class);
    }
    
    public void scheduler() {
        this.set("app.state", StringHelper.cjoin(YarnApplicationState.NEW.toString(), YarnApplicationState.NEW_SAVING.toString(), YarnApplicationState.SUBMITTED.toString(), YarnApplicationState.ACCEPTED.toString(), YarnApplicationState.RUNNING.toString()));
        final ResourceManager rm = this.getInstance(ResourceManager.class);
        final ResourceScheduler rs = rm.getResourceScheduler();
        if (rs == null || rs instanceof CapacityScheduler) {
            this.setTitle("Capacity Scheduler");
            this.render(CapacitySchedulerPage.class);
            return;
        }
        if (rs instanceof FairScheduler) {
            this.setTitle("Fair Scheduler");
            this.render(FairSchedulerPage.class);
            return;
        }
        this.setTitle("Default Scheduler");
        this.render(DefaultSchedulerPage.class);
    }
    
    public void queue() {
        this.setTitle(StringHelper.join("Queue ", this.get("queue.name", "unknown")));
    }
    
    public void submit() {
        this.setTitle("Application Submission Not Allowed");
    }
}
