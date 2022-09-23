// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.util.Records;

public abstract class ContainerResourceIncrease
{
    @InterfaceAudience.Public
    public static ContainerResourceIncrease newInstance(final ContainerId existingContainerId, final Resource targetCapability, final Token token) {
        final ContainerResourceIncrease context = Records.newRecord(ContainerResourceIncrease.class);
        context.setContainerId(existingContainerId);
        context.setCapability(targetCapability);
        context.setContainerToken(token);
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
    
    @InterfaceAudience.Public
    public abstract Token getContainerToken();
    
    @InterfaceAudience.Public
    public abstract void setContainerToken(final Token p0);
    
    @Override
    public int hashCode() {
        return this.getCapability().hashCode() + this.getContainerId().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof ContainerResourceIncrease) {
            final ContainerResourceIncrease ctx = (ContainerResourceIncrease)other;
            return (this.getContainerId() != null || ctx.getContainerId() == null) && this.getContainerId().equals(ctx.getContainerId()) && (this.getCapability() != null || ctx.getCapability() == null) && this.getCapability().equals(ctx.getCapability());
        }
        return false;
    }
}
