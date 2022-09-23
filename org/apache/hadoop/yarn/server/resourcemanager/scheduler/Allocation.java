// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.yarn.api.records.NMToken;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.Container;
import java.util.List;

public class Allocation
{
    final List<Container> containers;
    final Resource resourceLimit;
    final Set<ContainerId> strictContainers;
    final Set<ContainerId> fungibleContainers;
    final List<ResourceRequest> fungibleResources;
    final List<NMToken> nmTokens;
    
    public Allocation(final List<Container> containers, final Resource resourceLimit, final Set<ContainerId> strictContainers, final Set<ContainerId> fungibleContainers, final List<ResourceRequest> fungibleResources) {
        this(containers, resourceLimit, strictContainers, fungibleContainers, fungibleResources, null);
    }
    
    public Allocation(final List<Container> containers, final Resource resourceLimit, final Set<ContainerId> strictContainers, final Set<ContainerId> fungibleContainers, final List<ResourceRequest> fungibleResources, final List<NMToken> nmTokens) {
        this.containers = containers;
        this.resourceLimit = resourceLimit;
        this.strictContainers = strictContainers;
        this.fungibleContainers = fungibleContainers;
        this.fungibleResources = fungibleResources;
        this.nmTokens = nmTokens;
    }
    
    public List<Container> getContainers() {
        return this.containers;
    }
    
    public Resource getResourceLimit() {
        return this.resourceLimit;
    }
    
    public Set<ContainerId> getStrictContainerPreemptions() {
        return this.strictContainers;
    }
    
    public Set<ContainerId> getContainerPreemptions() {
        return this.fungibleContainers;
    }
    
    public List<ResourceRequest> getResourcePreemptions() {
        return this.fungibleResources;
    }
    
    public List<NMToken> getNMTokens() {
        return this.nmTokens;
    }
}
