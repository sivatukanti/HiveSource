// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class KillApplicationResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static KillApplicationResponse newInstance(final boolean isKillCompleted) {
        final KillApplicationResponse response = Records.newRecord(KillApplicationResponse.class);
        response.setIsKillCompleted(isKillCompleted);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract boolean getIsKillCompleted();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setIsKillCompleted(final boolean p0);
}
