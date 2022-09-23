// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "userMetrics")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserMetricsInfo
{
    protected int appsSubmitted;
    protected int appsCompleted;
    protected int appsPending;
    protected int appsRunning;
    protected int appsFailed;
    protected int appsKilled;
    protected int runningContainers;
    protected int pendingContainers;
    protected int reservedContainers;
    protected long reservedMB;
    protected long pendingMB;
    protected long allocatedMB;
    protected long reservedVirtualCores;
    protected long pendingVirtualCores;
    protected long allocatedVirtualCores;
    @XmlTransient
    protected boolean userMetricsAvailable;
    
    public UserMetricsInfo() {
    }
    
    public UserMetricsInfo(final ResourceManager rm, final RMContext rmContext, final String user) {
        final ResourceScheduler rs = rm.getResourceScheduler();
        final QueueMetrics metrics = rs.getRootQueueMetrics();
        final QueueMetrics userMetrics = metrics.getUserMetrics(user);
        this.userMetricsAvailable = false;
        if (userMetrics != null) {
            this.userMetricsAvailable = true;
            this.appsSubmitted = userMetrics.getAppsSubmitted();
            this.appsCompleted = metrics.getAppsCompleted();
            this.appsPending = metrics.getAppsPending();
            this.appsRunning = metrics.getAppsRunning();
            this.appsFailed = metrics.getAppsFailed();
            this.appsKilled = metrics.getAppsKilled();
            this.runningContainers = userMetrics.getAllocatedContainers();
            this.pendingContainers = userMetrics.getPendingContainers();
            this.reservedContainers = userMetrics.getReservedContainers();
            this.reservedMB = userMetrics.getReservedMB();
            this.pendingMB = userMetrics.getPendingMB();
            this.allocatedMB = userMetrics.getAllocatedMB();
            this.reservedVirtualCores = userMetrics.getReservedVirtualCores();
            this.pendingVirtualCores = userMetrics.getPendingVirtualCores();
            this.allocatedVirtualCores = userMetrics.getAllocatedVirtualCores();
        }
    }
    
    public boolean metricsAvailable() {
        return this.userMetricsAvailable;
    }
    
    public int getAppsSubmitted() {
        return this.appsSubmitted;
    }
    
    public int getAppsCompleted() {
        return this.appsCompleted;
    }
    
    public int getAppsPending() {
        return this.appsPending;
    }
    
    public int getAppsRunning() {
        return this.appsRunning;
    }
    
    public int getAppsFailed() {
        return this.appsFailed;
    }
    
    public int getAppsKilled() {
        return this.appsKilled;
    }
    
    public long getReservedMB() {
        return this.reservedMB;
    }
    
    public long getAllocatedMB() {
        return this.allocatedMB;
    }
    
    public long getPendingMB() {
        return this.pendingMB;
    }
    
    public long getReservedVirtualCores() {
        return this.reservedVirtualCores;
    }
    
    public long getAllocatedVirtualCores() {
        return this.allocatedVirtualCores;
    }
    
    public long getPendingVirtualCores() {
        return this.pendingVirtualCores;
    }
    
    public int getReservedContainers() {
        return this.reservedContainers;
    }
    
    public int getRunningContainers() {
        return this.runningContainers;
    }
    
    public int getPendingContainers() {
        return this.pendingContainers;
    }
}
