// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class StopContainersRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static StopContainersRequest newInstance(final List<ContainerId> containerIds) {
        final StopContainersRequest request = Records.newRecord(StopContainersRequest.class);
        request.setContainerIds(containerIds);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ContainerId> getContainerIds();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setContainerIds(final List<ContainerId> p0);
}
