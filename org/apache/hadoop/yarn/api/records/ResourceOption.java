// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class ResourceOption
{
    public static ResourceOption newInstance(final Resource resource, final int overCommitTimeout) {
        final ResourceOption resourceOption = Records.newRecord(ResourceOption.class);
        resourceOption.setResource(resource);
        resourceOption.setOverCommitTimeout(overCommitTimeout);
        resourceOption.build();
        return resourceOption;
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Evolving
    public abstract Resource getResource();
    
    @InterfaceAudience.Private
    @InterfaceStability.Evolving
    protected abstract void setResource(final Resource p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Evolving
    public abstract int getOverCommitTimeout();
    
    @InterfaceAudience.Private
    @InterfaceStability.Evolving
    protected abstract void setOverCommitTimeout(final int p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Evolving
    protected abstract void build();
    
    @Override
    public String toString() {
        return "Resource:" + this.getResource().toString() + ", overCommitTimeout:" + this.getOverCommitTimeout();
    }
}
