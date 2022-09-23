// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class GetContainerReportResponse
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static GetContainerReportResponse newInstance(final ContainerReport containerReport) {
        final GetContainerReportResponse response = Records.newRecord(GetContainerReportResponse.class);
        response.setContainerReport(containerReport);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ContainerReport getContainerReport();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setContainerReport(final ContainerReport p0);
}
