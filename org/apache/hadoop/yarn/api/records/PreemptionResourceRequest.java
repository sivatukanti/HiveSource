// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class PreemptionResourceRequest
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static PreemptionResourceRequest newInstance(final ResourceRequest req) {
        final PreemptionResourceRequest request = Records.newRecord(PreemptionResourceRequest.class);
        request.setResourceRequest(req);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract ResourceRequest getResourceRequest();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setResourceRequest(final ResourceRequest p0);
}
