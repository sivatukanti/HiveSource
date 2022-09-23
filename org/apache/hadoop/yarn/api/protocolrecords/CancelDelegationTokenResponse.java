// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class CancelDelegationTokenResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static CancelDelegationTokenResponse newInstance() {
        final CancelDelegationTokenResponse response = Records.newRecord(CancelDelegationTokenResponse.class);
        return response;
    }
}
