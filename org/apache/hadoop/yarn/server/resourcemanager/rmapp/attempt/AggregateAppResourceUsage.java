// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class AggregateAppResourceUsage
{
    long memorySeconds;
    long vcoreSeconds;
    
    public AggregateAppResourceUsage(final long memorySeconds, final long vcoreSeconds) {
        this.memorySeconds = memorySeconds;
        this.vcoreSeconds = vcoreSeconds;
    }
    
    public long getMemorySeconds() {
        return this.memorySeconds;
    }
    
    public void setMemorySeconds(final long memorySeconds) {
        this.memorySeconds = memorySeconds;
    }
    
    public long getVcoreSeconds() {
        return this.vcoreSeconds;
    }
    
    public void setVcoreSeconds(final long vcoreSeconds) {
        this.vcoreSeconds = vcoreSeconds;
    }
}
