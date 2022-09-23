// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class GetContainersResponse
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static GetContainersResponse newInstance(final List<ContainerReport> containers) {
        final GetContainersResponse response = Records.newRecord(GetContainersResponse.class);
        response.setContainerList(containers);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract List<ContainerReport> getContainerList();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setContainerList(final List<ContainerReport> p0);
}
