// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class RenewDelegationTokenRequest
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static RenewDelegationTokenRequest newInstance(final Token dToken) {
        final RenewDelegationTokenRequest request = Records.newRecord(RenewDelegationTokenRequest.class);
        request.setDelegationToken(dToken);
        return request;
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract Token getDelegationToken();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setDelegationToken(final Token p0);
}
