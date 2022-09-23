// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetDelegationTokenRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetDelegationTokenRequest newInstance(final String renewer) {
        final GetDelegationTokenRequest request = Records.newRecord(GetDelegationTokenRequest.class);
        request.setRenewer(renewer);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getRenewer();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setRenewer(final String p0);
}
