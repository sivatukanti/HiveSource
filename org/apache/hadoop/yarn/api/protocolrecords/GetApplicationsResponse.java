// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetApplicationsResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static GetApplicationsResponse newInstance(final List<ApplicationReport> applications) {
        final GetApplicationsResponse response = Records.newRecord(GetApplicationsResponse.class);
        response.setApplicationList(applications);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<ApplicationReport> getApplicationList();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationList(final List<ApplicationReport> p0);
}
