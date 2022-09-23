// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;

public class CapacityHeadroomProvider
{
    LeafQueue.User user;
    LeafQueue queue;
    FiCaSchedulerApp application;
    Resource required;
    LeafQueue.QueueHeadroomInfo queueHeadroomInfo;
    
    public CapacityHeadroomProvider(final LeafQueue.User user, final LeafQueue queue, final FiCaSchedulerApp application, final Resource required, final LeafQueue.QueueHeadroomInfo queueHeadroomInfo) {
        this.user = user;
        this.queue = queue;
        this.application = application;
        this.required = required;
        this.queueHeadroomInfo = queueHeadroomInfo;
    }
    
    public Resource getHeadroom() {
        final Resource queueMaxCap;
        final Resource clusterResource;
        synchronized (this.queueHeadroomInfo) {
            queueMaxCap = this.queueHeadroomInfo.getQueueMaxCap();
            clusterResource = this.queueHeadroomInfo.getClusterResource();
        }
        final Resource headroom = this.queue.getHeadroom(this.user, queueMaxCap, clusterResource, this.application, this.required);
        if (headroom.getMemory() < 0) {
            headroom.setMemory(0);
        }
        return headroom;
    }
}
