// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmnode;

import org.apache.hadoop.yarn.api.records.ContainerStatus;
import java.util.List;

public class UpdatedContainerInfo
{
    private List<ContainerStatus> newlyLaunchedContainers;
    private List<ContainerStatus> completedContainers;
    
    public UpdatedContainerInfo() {
    }
    
    public UpdatedContainerInfo(final List<ContainerStatus> newlyLaunchedContainers, final List<ContainerStatus> completedContainers) {
        this.newlyLaunchedContainers = newlyLaunchedContainers;
        this.completedContainers = completedContainers;
    }
    
    public List<ContainerStatus> getNewlyLaunchedContainers() {
        return this.newlyLaunchedContainers;
    }
    
    public List<ContainerStatus> getCompletedContainers() {
        return this.completedContainers;
    }
}
