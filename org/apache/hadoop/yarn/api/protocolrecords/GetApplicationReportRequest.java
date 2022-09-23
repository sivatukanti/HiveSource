// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetApplicationReportRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetApplicationReportRequest newInstance(final ApplicationId applicationId) {
        final GetApplicationReportRequest request = Records.newRecord(GetApplicationReportRequest.class);
        request.setApplicationId(applicationId);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ApplicationId getApplicationId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setApplicationId(final ApplicationId p0);
}
