// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetDelegationTokenResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static GetDelegationTokenResponse newInstance(final Token rmDTToken) {
        final GetDelegationTokenResponse response = Records.newRecord(GetDelegationTokenResponse.class);
        response.setRMDelegationToken(rmDTToken);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Token getRMDelegationToken();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setRMDelegationToken(final Token p0);
}
