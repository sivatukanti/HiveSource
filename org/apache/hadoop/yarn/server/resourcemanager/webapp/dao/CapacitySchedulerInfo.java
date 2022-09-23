// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.Iterator;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.LeafQueue;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CSQueue;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "capacityScheduler")
@XmlType(name = "capacityScheduler")
@XmlAccessorType(XmlAccessType.FIELD)
public class CapacitySchedulerInfo extends SchedulerInfo
{
    protected float capacity;
    protected float usedCapacity;
    protected float maxCapacity;
    protected String queueName;
    protected CapacitySchedulerQueueInfoList queues;
    @XmlTransient
    static final float EPSILON = 1.0E-8f;
    
    public CapacitySchedulerInfo() {
    }
    
    public CapacitySchedulerInfo(final CSQueue parent) {
        this.queueName = parent.getQueueName();
        this.usedCapacity = parent.getUsedCapacity() * 100.0f;
        this.capacity = parent.getCapacity() * 100.0f;
        float max = parent.getMaximumCapacity();
        if (max < 1.0E-8f || max > 1.0f) {
            max = 1.0f;
        }
        this.maxCapacity = max * 100.0f;
        this.queues = this.getQueues(parent);
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
    
    public String getQueueName() {
        return this.queueName;
    }
    
    public CapacitySchedulerQueueInfoList getQueues() {
        return this.queues;
    }
    
    protected CapacitySchedulerQueueInfoList getQueues(final CSQueue parent) {
        final CSQueue parentQueue = parent;
        final CapacitySchedulerQueueInfoList queuesInfo = new CapacitySchedulerQueueInfoList();
        for (final CSQueue queue : parentQueue.getChildQueues()) {
            CapacitySchedulerQueueInfo info;
            if (queue instanceof LeafQueue) {
                info = new CapacitySchedulerLeafQueueInfo((LeafQueue)queue);
            }
            else {
                info = new CapacitySchedulerQueueInfo(queue);
                info.queues = this.getQueues(queue);
            }
            queuesInfo.addToQueueInfoList(info);
        }
        return queuesInfo;
    }
}
