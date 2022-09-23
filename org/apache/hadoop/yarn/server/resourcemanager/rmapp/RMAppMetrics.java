// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

import org.apache.hadoop.yarn.api.records.Resource;

public class RMAppMetrics
{
    final Resource resourcePreempted;
    final int numNonAMContainersPreempted;
    final int numAMContainersPreempted;
    final long memorySeconds;
    final long vcoreSeconds;
    
    public RMAppMetrics(final Resource resourcePreempted, final int numNonAMContainersPreempted, final int numAMContainersPreempted, final long memorySeconds, final long vcoreSeconds) {
        this.resourcePreempted = resourcePreempted;
        this.numNonAMContainersPreempted = numNonAMContainersPreempted;
        this.numAMContainersPreempted = numAMContainersPreempted;
        this.memorySeconds = memorySeconds;
        this.vcoreSeconds = vcoreSeconds;
    }
    
    public Resource getResourcePreempted() {
        return this.resourcePreempted;
    }
    
    public int getNumNonAMContainersPreempted() {
        return this.numNonAMContainersPreempted;
    }
    
    public int getNumAMContainersPreempted() {
        return this.numAMContainersPreempted;
    }
    
    public long getMemorySeconds() {
        return this.memorySeconds;
    }
    
    public long getVcoreSeconds() {
        return this.vcoreSeconds;
    }
}
