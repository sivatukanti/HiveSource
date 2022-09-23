// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
public abstract class ContainerResourceIncreaseRequest
{
    @InterfaceAudience.Public
    public static ContainerResourceIncreaseRequest newInstance(final ContainerId existingContainerId, final Resource targetCapability) {
        final ContainerResourceIncreaseRequest context = Records.newRecord(ContainerResourceIncreaseRequest.class);
        context.setContainerId(existingContainerId);
        context.setCapability(targetCapability);
        return context;
    }
    
    @InterfaceAudience.Public
    public abstract ContainerId getContainerId();
    
    @InterfaceAudience.Public
    public abstract void setContainerId(final ContainerId p0);
    
    @InterfaceAudience.Public
    public abstract Resource getCapability();
    
    @InterfaceAudience.Public
    public abstract void setCapability(final Resource p0);
    
    @Override
    public int hashCode() {
        return this.getCapability().hashCode() + this.getContainerId().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof ContainerResourceIncreaseRequest) {
            final ContainerResourceIncreaseRequest ctx = (ContainerResourceIncreaseRequest)other;
            return (this.getContainerId() != null || ctx.getContainerId() == null) && this.getContainerId().equals(ctx.getContainerId()) && (this.getCapability() != null || ctx.getCapability() == null) && this.getCapability().equals(ctx.getCapability());
        }
        return false;
    }
}
