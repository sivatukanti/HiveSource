// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CapacitySchedulerQueueInfoList
{
    protected ArrayList<CapacitySchedulerQueueInfo> queue;
    
    public CapacitySchedulerQueueInfoList() {
        this.queue = new ArrayList<CapacitySchedulerQueueInfo>();
    }
    
    public ArrayList<CapacitySchedulerQueueInfo> getQueueInfoList() {
        return this.queue;
    }
    
    public boolean addToQueueInfoList(final CapacitySchedulerQueueInfo e) {
        return this.queue.add(e);
    }
    
    public CapacitySchedulerQueueInfo getQueueInfo(final int i) {
        return this.queue.get(i);
    }
}
