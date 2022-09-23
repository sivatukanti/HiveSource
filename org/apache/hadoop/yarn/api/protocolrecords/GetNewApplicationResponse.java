// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetNewApplicationResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static GetNewApplicationResponse newInstance(final ApplicationId applicationId, final Resource minCapability, final Resource maxCapability) {
        final GetNewApplicationResponse response = Records.newRecord(GetNewApplicationResponse.class);
        response.setApplicationId(applicationId);
        response.setMaximumResourceCapability(maxCapability);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ApplicationId getApplicationId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationId(final ApplicationId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Resource getMaximumResourceCapability();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setMaximumResourceCapability(final Resource p0);
}
