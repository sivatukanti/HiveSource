// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class Container implements Comparable<Container>
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static Container newInstance(final ContainerId containerId, final NodeId nodeId, final String nodeHttpAddress, final Resource resource, final Priority priority, final Token containerToken) {
        final Container container = Records.newRecord(Container.class);
        container.setId(containerId);
        container.setNodeId(nodeId);
        container.setNodeHttpAddress(nodeHttpAddress);
        container.setResource(resource);
        container.setPriority(priority);
        container.setContainerToken(containerToken);
        return container;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ContainerId getId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setId(final ContainerId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract NodeId getNodeId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNodeId(final NodeId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getNodeHttpAddress();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNodeHttpAddress(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Resource getResource();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setResource(final Resource p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Priority getPriority();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setPriority(final Priority p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Token getContainerToken();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setContainerToken(final Token p0);
}
