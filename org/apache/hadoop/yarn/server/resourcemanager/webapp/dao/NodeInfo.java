// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.Set;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNodeReport;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.NodeState;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeInfo
{
    protected String rack;
    protected NodeState state;
    protected String id;
    protected String nodeHostName;
    protected String nodeHTTPAddress;
    protected long lastHealthUpdate;
    protected String version;
    protected String healthReport;
    protected int numContainers;
    protected long usedMemoryMB;
    protected long availMemoryMB;
    protected long usedVirtualCores;
    protected long availableVirtualCores;
    protected ArrayList<String> nodeLabels;
    
    public NodeInfo() {
        this.nodeLabels = new ArrayList<String>();
    }
    
    public NodeInfo(final RMNode ni, final ResourceScheduler sched) {
        this.nodeLabels = new ArrayList<String>();
        final NodeId id = ni.getNodeID();
        final SchedulerNodeReport report = sched.getNodeReport(id);
        this.numContainers = 0;
        this.usedMemoryMB = 0L;
        this.availMemoryMB = 0L;
        if (report != null) {
            this.numContainers = report.getNumContainers();
            this.usedMemoryMB = report.getUsedResource().getMemory();
            this.availMemoryMB = report.getAvailableResource().getMemory();
            this.usedVirtualCores = report.getUsedResource().getVirtualCores();
            this.availableVirtualCores = report.getAvailableResource().getVirtualCores();
        }
        this.id = id.toString();
        this.rack = ni.getRackName();
        this.nodeHostName = ni.getHostName();
        this.state = ni.getState();
        this.nodeHTTPAddress = ni.getHttpAddress();
        this.lastHealthUpdate = ni.getLastHealthReportTime();
        this.healthReport = String.valueOf(ni.getHealthReport());
        this.version = ni.getNodeManagerVersion();
        final Set<String> labelSet = ni.getNodeLabels();
        if (labelSet != null) {
            this.nodeLabels.addAll(labelSet);
            Collections.sort(this.nodeLabels);
        }
    }
    
    public String getRack() {
        return this.rack;
    }
    
    public String getState() {
        return String.valueOf(this.state);
    }
    
    public String getNodeId() {
        return this.id;
    }
    
    public String getNodeHTTPAddress() {
        return this.nodeHTTPAddress;
    }
    
    public void setNodeHTTPAddress(final String nodeHTTPAddress) {
        this.nodeHTTPAddress = nodeHTTPAddress;
    }
    
    public long getLastHealthUpdate() {
        return this.lastHealthUpdate;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public String getHealthReport() {
        return this.healthReport;
    }
    
    public int getNumContainers() {
        return this.numContainers;
    }
    
    public long getUsedMemory() {
        return this.usedMemoryMB;
    }
    
    public long getAvailableMemory() {
        return this.availMemoryMB;
    }
    
    public long getUsedVirtualCores() {
        return this.usedVirtualCores;
    }
    
    public long getAvailableVirtualCores() {
        return this.availableVirtualCores;
    }
    
    public ArrayList<String> getNodeLabels() {
        return this.nodeLabels;
    }
}
