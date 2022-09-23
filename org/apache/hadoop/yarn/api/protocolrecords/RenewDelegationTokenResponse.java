// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class RenewDelegationTokenResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static RenewDelegationTokenResponse newInstance(final long expTime) {
        final RenewDelegationTokenResponse response = Records.newRecord(RenewDelegationTokenResponse.class);
        response.setNextExpirationTime(expTime);
        return response;
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract long getNextExpirationTime();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNextExpirationTime(final long p0);
}
