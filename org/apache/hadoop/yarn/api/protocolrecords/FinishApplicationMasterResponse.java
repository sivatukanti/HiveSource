// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class FinishApplicationMasterResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static FinishApplicationMasterResponse newInstance(final boolean isRemovedFromRMStateStore) {
        final FinishApplicationMasterResponse response = Records.newRecord(FinishApplicationMasterResponse.class);
        response.setIsUnregistered(isRemovedFromRMStateStore);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract boolean getIsUnregistered();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setIsUnregistered(final boolean p0);
}
