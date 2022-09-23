// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSAppAttempt;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSQueue;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fairScheduler")
@XmlType(name = "fairScheduler")
@XmlAccessorType(XmlAccessType.FIELD)
public class FairSchedulerInfo extends SchedulerInfo
{
    public static final int INVALID_FAIR_SHARE = -1;
    private FairSchedulerQueueInfo rootQueue;
    @XmlTransient
    private FairScheduler scheduler;
    
    public FairSchedulerInfo() {
    }
    
    public FairSchedulerInfo(final FairScheduler fs) {
        this.scheduler = fs;
        this.rootQueue = new FairSchedulerQueueInfo(this.scheduler.getQueueManager().getRootQueue(), this.scheduler);
    }
    
    public int getAppFairShare(final ApplicationAttemptId appAttemptId) {
        final FSAppAttempt fsAppAttempt = this.scheduler.getSchedulerApp(appAttemptId);
        return (fsAppAttempt == null) ? -1 : fsAppAttempt.getFairShare().getMemory();
    }
    
    public FairSchedulerQueueInfo getRootQueueInfo() {
        return this.rootQueue;
    }
}
