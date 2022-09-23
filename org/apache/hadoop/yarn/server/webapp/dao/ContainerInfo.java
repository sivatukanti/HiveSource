// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webapp.dao;

import org.apache.hadoop.yarn.util.Times;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.ContainerState;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "container")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerInfo
{
    protected String containerId;
    protected int allocatedMB;
    protected int allocatedVCores;
    protected String assignedNodeId;
    protected int priority;
    protected long startedTime;
    protected long finishedTime;
    protected long elapsedTime;
    protected String diagnosticsInfo;
    protected String logUrl;
    protected int containerExitStatus;
    protected ContainerState containerState;
    
    public ContainerInfo() {
    }
    
    public ContainerInfo(final ContainerReport container) {
        this.containerId = container.getContainerId().toString();
        if (container.getAllocatedResource() != null) {
            this.allocatedMB = container.getAllocatedResource().getMemory();
            this.allocatedVCores = container.getAllocatedResource().getVirtualCores();
        }
        if (container.getAssignedNode() != null) {
            this.assignedNodeId = container.getAssignedNode().toString();
        }
        this.priority = container.getPriority().getPriority();
        this.startedTime = container.getCreationTime();
        this.finishedTime = container.getFinishTime();
        this.elapsedTime = Times.elapsed(this.startedTime, this.finishedTime);
        this.diagnosticsInfo = container.getDiagnosticsInfo();
        this.logUrl = container.getLogUrl();
        this.containerExitStatus = container.getContainerExitStatus();
        this.containerState = container.getContainerState();
    }
    
    public String getContainerId() {
        return this.containerId;
    }
    
    public int getAllocatedMB() {
        return this.allocatedMB;
    }
    
    public int getAllocatedVCores() {
        return this.allocatedVCores;
    }
    
    public String getAssignedNodeId() {
        return this.assignedNodeId;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public long getStartedTime() {
        return this.startedTime;
    }
    
    public long getFinishedTime() {
        return this.finishedTime;
    }
    
    public long getElapsedTime() {
        return this.elapsedTime;
    }
    
    public String getDiagnosticsInfo() {
        return this.diagnosticsInfo;
    }
    
    public String getLogUrl() {
        return this.logUrl;
    }
    
    public int getContainerExitStatus() {
        return this.containerExitStatus;
    }
    
    public ContainerState getContainerState() {
        return this.containerState;
    }
}
