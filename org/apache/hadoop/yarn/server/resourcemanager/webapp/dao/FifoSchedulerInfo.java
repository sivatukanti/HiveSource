// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNodeReport;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.hadoop.yarn.api.records.QueueState;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fifoScheduler")
@XmlType(name = "fifoScheduler")
@XmlAccessorType(XmlAccessType.FIELD)
public class FifoSchedulerInfo extends SchedulerInfo
{
    protected float capacity;
    protected float usedCapacity;
    protected QueueState qstate;
    protected int minQueueMemoryCapacity;
    protected int maxQueueMemoryCapacity;
    protected int numNodes;
    protected int usedNodeCapacity;
    protected int availNodeCapacity;
    protected int totalNodeCapacity;
    protected int numContainers;
    @XmlTransient
    protected String qstateFormatted;
    @XmlTransient
    protected String qName;
    
    public FifoSchedulerInfo() {
    }
    
    public FifoSchedulerInfo(final ResourceManager rm) {
        final RMContext rmContext = rm.getRMContext();
        final FifoScheduler fs = (FifoScheduler)rm.getResourceScheduler();
        this.qName = fs.getQueueInfo("", false, false).getQueueName();
        final QueueInfo qInfo = fs.getQueueInfo(this.qName, true, true);
        this.usedCapacity = qInfo.getCurrentCapacity();
        this.capacity = qInfo.getCapacity();
        this.minQueueMemoryCapacity = fs.getMinimumResourceCapability().getMemory();
        this.maxQueueMemoryCapacity = fs.getMaximumResourceCapability().getMemory();
        this.qstate = qInfo.getQueueState();
        this.numNodes = rmContext.getRMNodes().size();
        this.usedNodeCapacity = 0;
        this.availNodeCapacity = 0;
        this.totalNodeCapacity = 0;
        this.numContainers = 0;
        for (final RMNode ni : rmContext.getRMNodes().values()) {
            final SchedulerNodeReport report = fs.getNodeReport(ni.getNodeID());
            this.usedNodeCapacity += report.getUsedResource().getMemory();
            this.availNodeCapacity += report.getAvailableResource().getMemory();
            this.totalNodeCapacity += ni.getTotalCapability().getMemory();
            this.numContainers += fs.getNodeReport(ni.getNodeID()).getNumContainers();
        }
    }
    
    public int getNumNodes() {
        return this.numNodes;
    }
    
    public int getUsedNodeCapacity() {
        return this.usedNodeCapacity;
    }
    
    public int getAvailNodeCapacity() {
        return this.availNodeCapacity;
    }
    
    public int getTotalNodeCapacity() {
        return this.totalNodeCapacity;
    }
    
    public int getNumContainers() {
        return this.numContainers;
    }
    
    public String getState() {
        return this.qstate.toString();
    }
    
    public String getQueueName() {
        return this.qName;
    }
    
    public int getMinQueueMemoryCapacity() {
        return this.minQueueMemoryCapacity;
    }
    
    public int getMaxQueueMemoryCapacity() {
        return this.maxQueueMemoryCapacity;
    }
    
    public float getCapacity() {
        return this.capacity;
    }
    
    public float getUsedCapacity() {
        return this.usedCapacity;
    }
}
