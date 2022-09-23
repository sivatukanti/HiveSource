// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.Iterator;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.AllocationConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSLeafQueue;
import java.util.ArrayList;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSQueue;
import java.util.Collection;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ FairSchedulerLeafQueueInfo.class })
public class FairSchedulerQueueInfo
{
    private int maxApps;
    @XmlTransient
    private float fractionMemUsed;
    @XmlTransient
    private float fractionMemFairShare;
    @XmlTransient
    private float fractionMemMinShare;
    @XmlTransient
    private float fractionMemMaxShare;
    private ResourceInfo minResources;
    private ResourceInfo maxResources;
    private ResourceInfo usedResources;
    private ResourceInfo fairResources;
    private ResourceInfo clusterResources;
    private String queueName;
    private String schedulingPolicy;
    private Collection<FairSchedulerQueueInfo> childQueues;
    
    public FairSchedulerQueueInfo() {
    }
    
    public FairSchedulerQueueInfo(final FSQueue queue, final FairScheduler scheduler) {
        final AllocationConfiguration allocConf = scheduler.getAllocationConfiguration();
        this.queueName = queue.getName();
        this.schedulingPolicy = queue.getPolicy().getName();
        this.clusterResources = new ResourceInfo(scheduler.getClusterResource());
        this.usedResources = new ResourceInfo(queue.getResourceUsage());
        this.fractionMemUsed = this.usedResources.getMemory() / (float)this.clusterResources.getMemory();
        this.fairResources = new ResourceInfo(queue.getFairShare());
        this.minResources = new ResourceInfo(queue.getMinShare());
        this.maxResources = new ResourceInfo(queue.getMaxShare());
        this.maxResources = new ResourceInfo(Resources.componentwiseMin(queue.getMaxShare(), scheduler.getClusterResource()));
        this.fractionMemFairShare = this.fairResources.getMemory() / (float)this.clusterResources.getMemory();
        this.fractionMemMinShare = this.minResources.getMemory() / (float)this.clusterResources.getMemory();
        this.fractionMemMaxShare = this.maxResources.getMemory() / (float)this.clusterResources.getMemory();
        this.maxApps = allocConf.getQueueMaxApps(this.queueName);
        final Collection<FSQueue> children = queue.getChildQueues();
        this.childQueues = new ArrayList<FairSchedulerQueueInfo>();
        for (final FSQueue child : children) {
            if (child instanceof FSLeafQueue) {
                this.childQueues.add(new FairSchedulerLeafQueueInfo((FSLeafQueue)child, scheduler));
            }
            else {
                this.childQueues.add(new FairSchedulerQueueInfo(child, scheduler));
            }
        }
    }
    
    public float getFairShareMemoryFraction() {
        return this.fractionMemFairShare;
    }
    
    public ResourceInfo getFairShare() {
        return this.fairResources;
    }
    
    public ResourceInfo getMinResources() {
        return this.minResources;
    }
    
    public ResourceInfo getMaxResources() {
        return this.maxResources;
    }
    
    public int getMaxApplications() {
        return this.maxApps;
    }
    
    public String getQueueName() {
        return this.queueName;
    }
    
    public ResourceInfo getUsedResources() {
        return this.usedResources;
    }
    
    public float getMinShareMemoryFraction() {
        return this.fractionMemMinShare;
    }
    
    public float getUsedMemoryFraction() {
        return this.fractionMemUsed;
    }
    
    public float getMaxResourcesFraction() {
        return this.fractionMemMaxShare;
    }
    
    public String getSchedulingPolicy() {
        return this.schedulingPolicy;
    }
    
    public Collection<FairSchedulerQueueInfo> getChildQueues() {
        return this.childQueues;
    }
}
