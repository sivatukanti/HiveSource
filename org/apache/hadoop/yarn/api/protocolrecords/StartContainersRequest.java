// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class StartContainersRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static StartContainersRequest newInstance(final List<StartContainerRequest> requests) {
        final StartContainersRequest request = Records.newRecord(StartContainersRequest.class);
        request.setStartContainerRequests(requests);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<StartContainerRequest> getStartContainerRequests();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setStartContainerRequests(final List<StartContainerRequest> p0);
}
