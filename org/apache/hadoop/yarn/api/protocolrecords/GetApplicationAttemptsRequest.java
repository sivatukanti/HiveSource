// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class GetApplicationAttemptsRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static GetApplicationAttemptsRequest newInstance(final ApplicationId applicationId) {
        final GetApplicationAttemptsRequest request = Records.newRecord(GetApplicationAttemptsRequest.class);
        request.setApplicationId(applicationId);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ApplicationId getApplicationId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setApplicationId(final ApplicationId p0);
}
