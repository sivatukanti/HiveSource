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
public class ContainerInitializationContext extends ContainerContext
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public ContainerInitializationContext(final String user, final ContainerId containerId, final Resource resource) {
        super(user, containerId, resource);
    }
}
