// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.PlanQueue;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CSQueue;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.QueueState;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ CapacitySchedulerLeafQueueInfo.class })
public class CapacitySchedulerQueueInfo
{
    @XmlTransient
    static final float EPSILON = 1.0E-8f;
    @XmlTransient
    protected String queuePath;
    protected float capacity;
    protected float usedCapacity;
    protected float maxCapacity;
    protected float absoluteCapacity;
    protected float absoluteMaxCapacity;
    protected float absoluteUsedCapacity;
    protected int numApplications;
    protected String queueName;
    protected QueueState state;
    protected CapacitySchedulerQueueInfoList queues;
    protected ResourceInfo resourcesUsed;
    private boolean hideReservationQueues;
    protected ArrayList<String> nodeLabels;
    
    CapacitySchedulerQueueInfo() {
        this.hideReservationQueues = false;
        this.nodeLabels = new ArrayList<String>();
    }
    
    CapacitySchedulerQueueInfo(final CSQueue q) {
        this.hideReservationQueues = false;
        this.nodeLabels = new ArrayList<String>();
        this.queuePath = q.getQueuePath();
        this.capacity = q.getCapacity() * 100.0f;
        this.usedCapacity = q.getUsedCapacity() * 100.0f;
        this.maxCapacity = q.getMaximumCapacity();
        if (this.maxCapacity < 1.0E-8f || this.maxCapacity > 1.0f) {
            this.maxCapacity = 1.0f;
        }
        this.maxCapacity *= 100.0f;
        this.absoluteCapacity = cap(q.getAbsoluteCapacity(), 0.0f, 1.0f) * 100.0f;
        this.absoluteMaxCapacity = cap(q.getAbsoluteMaximumCapacity(), 0.0f, 1.0f) * 100.0f;
        this.absoluteUsedCapacity = cap(q.getAbsoluteUsedCapacity(), 0.0f, 1.0f) * 100.0f;
        this.numApplications = q.getNumApplications();
        this.queueName = q.getQueueName();
        this.state = q.getState();
        this.resourcesUsed = new ResourceInfo(q.getUsedResources());
        if (q instanceof PlanQueue && !((PlanQueue)q).showReservationsAsQueues()) {
            this.hideReservationQueues = true;
        }
        final Set<String> labelSet = q.getAccessibleNodeLabels();
        if (labelSet != null) {
            this.nodeLabels.addAll(labelSet);
            Collections.sort(this.nodeLabels);
        }
    }
    
    public float getCapacity() {
        return this.capacity;
    }
    
    public float getUsedCapacity() {
        return this.usedCapacity;
    }
    
    public float getMaxCapacity() {
        return this.maxCapacity;
    }
    
    public float getAbsoluteCapacity() {
        return this.absoluteCapacity;
    }
    
    public float getAbsoluteMaxCapacity() {
        return this.absoluteMaxCapacity;
    }
    
    public float getAbsoluteUsedCapacity() {
        return this.absoluteUsedCapacity;
    }
    
    public int getNumApplications() {
        return this.numApplications;
    }
    
    public String getQueueName() {
        return this.queueName;
    }
    
    public String getQueueState() {
        return this.state.toString();
    }
    
    public String getQueuePath() {
        return this.queuePath;
    }
    
    public CapacitySchedulerQueueInfoList getQueues() {
        if (this.hideReservationQueues) {
            return new CapacitySchedulerQueueInfoList();
        }
        return this.queues;
    }
    
    public ResourceInfo getResourcesUsed() {
        return this.resourcesUsed;
    }
    
    static float cap(final float val, final float low, final float hi) {
        return Math.min(Math.max(val, low), hi);
    }
    
    public ArrayList<String> getNodeLabels() {
        return this.nodeLabels;
    }
}
