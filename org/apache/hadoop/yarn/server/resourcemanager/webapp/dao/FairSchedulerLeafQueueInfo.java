// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSQueue;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSLeafQueue;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FairSchedulerLeafQueueInfo extends FairSchedulerQueueInfo
{
    private int numPendingApps;
    private int numActiveApps;
    
    public FairSchedulerLeafQueueInfo() {
    }
    
    public FairSchedulerLeafQueueInfo(final FSLeafQueue queue, final FairScheduler scheduler) {
        super(queue, scheduler);
        final Collection<FSAppAttempt> apps = queue.getRunnableAppSchedulables();
        for (final FSAppAttempt app : apps) {
            if (app.isPending()) {
                ++this.numPendingApps;
            }
            else {
                ++this.numActiveApps;
            }
        }
        this.numPendingApps += queue.getNonRunnableAppSchedulables().size();
    }
    
    public int getNumActiveApplications() {
        return this.numActiveApps;
    }
    
    public int getNumPendingApplications() {
        return this.numPendingApps;
    }
}
