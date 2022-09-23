// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ContainerContext
{
    private final String user;
    private final ContainerId containerId;
    private final Resource resource;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public ContainerContext(final String user, final ContainerId containerId, final Resource resource) {
        this.user = user;
        this.containerId = containerId;
        this.resource = resource;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public ContainerId getContainerId() {
        return this.containerId;
    }
    
    public Resource getResource() {
        return this.resource;
    }
}
