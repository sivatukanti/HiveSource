// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class ResourceBlacklistRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static ResourceBlacklistRequest newInstance(final List<String> additions, final List<String> removals) {
        final ResourceBlacklistRequest blacklistRequest = Records.newRecord(ResourceBlacklistRequest.class);
        blacklistRequest.setBlacklistAdditions(additions);
        blacklistRequest.setBlacklistRemovals(removals);
        return blacklistRequest;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<String> getBlacklistAdditions();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setBlacklistAdditions(final List<String> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<String> getBlacklistRemovals();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setBlacklistRemovals(final List<String> p0);
}
