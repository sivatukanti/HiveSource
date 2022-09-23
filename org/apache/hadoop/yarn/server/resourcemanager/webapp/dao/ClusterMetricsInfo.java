// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.ClusterMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clusterMetrics")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusterMetricsInfo
{
    protected int appsSubmitted;
    protected int appsCompleted;
    protected int appsPending;
    protected int appsRunning;
    protected int appsFailed;
    protected int appsKilled;
    protected long reservedMB;
    protected long availableMB;
    protected long allocatedMB;
    protected long reservedVirtualCores;
    protected long availableVirtualCores;
    protected long allocatedVirtualCores;
    protected int containersAllocated;
    protected int containersReserved;
    protected int containersPending;
    protected long totalMB;
    protected long totalVirtualCores;
    protected int totalNodes;
    protected int lostNodes;
    protected int unhealthyNodes;
    protected int decommissionedNodes;
    protected int rebootedNodes;
    protected int activeNodes;
    
    public ClusterMetricsInfo() {
    }
    
    public ClusterMetricsInfo(final ResourceManager rm, final RMContext rmContext) {
        final ResourceScheduler rs = rm.getResourceScheduler();
        final QueueMetrics metrics = rs.getRootQueueMetrics();
        final ClusterMetrics clusterMetrics = ClusterMetrics.getMetrics();
        this.appsSubmitted = metrics.getAppsSubmitted();
        this.appsCompleted = metrics.getAppsCompleted();
        this.appsPending = metrics.getAppsPending();
        this.appsRunning = metrics.getAppsRunning();
        this.appsFailed = metrics.getAppsFailed();
        this.appsKilled = metrics.getAppsKilled();
        this.reservedMB = metrics.getReservedMB();
        this.availableMB = metrics.getAvailableMB();
        this.allocatedMB = metrics.getAllocatedMB();
        this.reservedVirtualCores = metrics.getReservedVirtualCores();
        this.availableVirtualCores = metrics.getAvailableVirtualCores();
        this.allocatedVirtualCores = metrics.getAllocatedVirtualCores();
        this.containersAllocated = metrics.getAllocatedContainers();
        this.containersPending = metrics.getPendingContainers();
        this.containersReserved = metrics.getReservedContainers();
        this.totalMB = this.availableMB + this.allocatedMB;
        this.totalVirtualCores = this.availableVirtualCores + this.allocatedVirtualCores;
        this.activeNodes = clusterMetrics.getNumActiveNMs();
        this.lostNodes = clusterMetrics.getNumLostNMs();
        this.unhealthyNodes = clusterMetrics.getUnhealthyNMs();
        this.decommissionedNodes = clusterMetrics.getNumDecommisionedNMs();
        this.rebootedNodes = clusterMetrics.getNumRebootedNMs();
        this.totalNodes = this.activeNodes + this.lostNodes + this.decommissionedNodes + this.rebootedNodes + this.unhealthyNodes;
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
    
    public long getAvailableMB() {
        return this.availableMB;
    }
    
    public long getAllocatedMB() {
        return this.allocatedMB;
    }
    
    public long getReservedVirtualCores() {
        return this.reservedVirtualCores;
    }
    
    public long getAvailableVirtualCores() {
        return this.availableVirtualCores;
    }
    
    public long getAllocatedVirtualCores() {
        return this.allocatedVirtualCores;
    }
    
    public int getContainersAllocated() {
        return this.containersAllocated;
    }
    
    public int getReservedContainers() {
        return this.containersReserved;
    }
    
    public int getPendingContainers() {
        return this.containersPending;
    }
    
    public long getTotalMB() {
        return this.totalMB;
    }
    
    public long getTotalVirtualCores() {
        return this.totalVirtualCores;
    }
    
    public int getTotalNodes() {
        return this.totalNodes;
    }
    
    public int getActiveNodes() {
        return this.activeNodes;
    }
    
    public int getLostNodes() {
        return this.lostNodes;
    }
    
    public int getRebootedNodes() {
        return this.rebootedNodes;
    }
    
    public int getUnhealthyNodes() {
        return this.unhealthyNodes;
    }
    
    public int getDecommissionedNodes() {
        return this.decommissionedNodes;
    }
}
