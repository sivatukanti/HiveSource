// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Container;
import java.util.List;

public class NodeResponse
{
    private final List<Container> completed;
    private final List<Container> toCleanUp;
    private final List<ApplicationId> finishedApplications;
    
    public NodeResponse(final List<ApplicationId> finishedApplications, final List<Container> completed, final List<Container> toKill) {
        this.finishedApplications = finishedApplications;
        this.completed = completed;
        this.toCleanUp = toKill;
    }
    
    public List<ApplicationId> getFinishedApplications() {
        return this.finishedApplications;
    }
    
    public List<Container> getCompletedContainers() {
        return this.completed;
    }
    
    public List<Container> getContainersToCleanUp() {
        return this.toCleanUp;
    }
}
